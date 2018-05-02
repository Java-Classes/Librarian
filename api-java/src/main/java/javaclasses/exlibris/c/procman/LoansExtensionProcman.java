/*
 * Copyright 2018, TeamDev Ltd. All rights reserved.
 *
 * Redistribution and use in source and/or binary forms, with or without
 * modification, must retain the above copyright notice and the following
 * disclaimer.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package javaclasses.exlibris.c.procman;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import io.spine.core.CommandContext;
import io.spine.core.EventContext;
import io.spine.core.React;
import io.spine.server.procman.CommandRouted;
import io.spine.server.procman.CommandRouter;
import io.spine.server.procman.ProcessManager;
import javaclasses.exlibris.InventoryId;
import javaclasses.exlibris.Loan;
import javaclasses.exlibris.LoansExtension;
import javaclasses.exlibris.LoansExtensionId;
import javaclasses.exlibris.LoansExtensionVBuilder;
import javaclasses.exlibris.Reservation;
import javaclasses.exlibris.UserId;
import javaclasses.exlibris.c.AllowLoansExtension;
import javaclasses.exlibris.c.BookReadyToPickup;
import javaclasses.exlibris.c.ForbidLoansExtension;
import javaclasses.exlibris.c.ReservationAdded;
import javaclasses.exlibris.c.ReservationCanceled;
import javaclasses.exlibris.c.inventory.InventoryAggregate;
import javaclasses.exlibris.c.inventory.InventoryRepository;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class LoansExtensionProcman extends ProcessManager<LoansExtensionId, LoansExtension, LoansExtensionVBuilder> {

    /**
     * Creates a new instance.
     *
     * @param id an ID for the new instance
     * @throws IllegalArgumentException if the ID type is unsupported
     */
    protected LoansExtensionProcman(LoansExtensionId id) {
        super(id);
    }

    protected static final LoansExtensionId ID =
            LoansExtensionId.newBuilder()
                            .setValue("ReservationQueueSingleton")
                            .build();

    @React
    CommandRouted on(ReservationAdded event, EventContext ctx) {
        final InventoryId inventoryId = event.getInventoryId();
        final CommandContext commandContext = ctx.getCommandContext();
        return updateLoansExtensionStateRouter(inventoryId, commandContext);
    }

    @React
    CommandRouted on(BookReadyToPickup event, EventContext ctx) {
        final InventoryId inventoryId = event.getInventoryId();
        final CommandContext commandContext = ctx.getCommandContext();
        return updateLoansExtensionStateRouter(inventoryId, commandContext);
    }

    @React
    CommandRouted on(ReservationCanceled event, EventContext ctx) {
        final boolean wasSatisfied = event.getWasSatisfied();
        if (!wasSatisfied) {
            final InventoryId inventoryId = event.getInventoryId();
            final CommandContext commandContext = ctx.getCommandContext();
            return updateLoansExtensionStateRouter(inventoryId, commandContext);
        }
        return null;
    }

    private CommandRouted updateLoansExtensionStateRouter(InventoryId inventoryId,
                                                          CommandContext commandContext) {
        final Optional<InventoryAggregate> inventoryOptional = getInventory(inventoryId);
        if (!inventoryOptional.isPresent()) {
            final String errorMessage = "The aggregate state for %s identifier not found in the InventoryRepository.";
            throw new IllegalArgumentException(String.format(errorMessage, inventoryId.getBookId()
                                                                                      .getIsbn62()));
        }
        final InventoryAggregate inventory = inventoryOptional.get();
        final List<Reservation> reservations = inventory.getState()
                                                        .getReservationsList();
        final List<Loan> loans = inventory.getState()
                                          .getLoansList();
        final int unsatisfiedReservationsCount = getUnsatisfiedReservationsCount(reservations);
        final int forbiddenToExtendLoansCount = getForbiddenToExtendLoansCount(loans);
        final int queueDifference = unsatisfiedReservationsCount - forbiddenToExtendLoansCount;

        if (queueDifference > 0) {
            final Optional<CommandRouter> routerOptional =
                    forbidLoansExtensionRouter(inventoryId, commandContext, loans, queueDifference);
            return routerOptional.isPresent() ? routerOptional.get()
                                                              .routeAll()
                                              : null;
        }
        final Optional<CommandRouter> routerOptional =
                allowLoansExtensionRouter(inventoryId, commandContext, loans,
                                          Math.abs(queueDifference));
        return routerOptional.isPresent() ? routerOptional.get()
                                                          .routeAll()
                                          : null;
    }

    private Optional<CommandRouter> forbidLoansExtensionRouter(InventoryId inventoryId,
                                                               CommandContext commandContext,
                                                               List<Loan> loans,
                                                               int numberToForbid) {
        final List<Loan> allowedForExtensionLoans = loans.stream()
                                                         .filter(Loan::getIsAllowedExtension)
                                                         .collect(Collectors.toList());

        final List<UserId> userIds = allowedForExtensionLoans.stream()
                                                             .map(Loan::getWhoBorrowed)
                                                             .collect(Collectors.toList());
        final Iterator<UserId> iterator = userIds.iterator();

        final List<UserId> resultUserIds = new ArrayList<>();
        while (iterator.hasNext() && resultUserIds.size() < numberToForbid) {
            resultUserIds.add(iterator.next());
        }

        if (!resultUserIds.isEmpty()) {
            final ForbidLoansExtension forbidLoansExtensionCmd =
                    ForbidLoansExtension.newBuilder()
                                        .setInventoryId(inventoryId)
                                        .addAllBorrowers(resultUserIds)
                                        .build();
            final CommandRouter commandRouter =
                    newRouterFor(forbidLoansExtensionCmd, commandContext)
                            .add(forbidLoansExtensionCmd);
            return Optional.of(commandRouter);
        }
        return Optional.absent();
    }

    private Optional<CommandRouter> allowLoansExtensionRouter(InventoryId inventoryId,
                                                              CommandContext commandContext,
                                                              List<Loan> loans,
                                                              int numberToAllow) {
        final List<Loan> forbiddenForExtensionLoans = loans.stream()
                                                           .filter(item -> !item.getIsAllowedExtension())
                                                           .collect(Collectors.toList());

        final List<UserId> userIds = forbiddenForExtensionLoans.stream()
                                                               .map(Loan::getWhoBorrowed)
                                                               .collect(Collectors.toList());
        final List<UserId> reverseUserIds = Lists.reverse(userIds);

        final Iterator<UserId> iterator = reverseUserIds.iterator();
        final List<UserId> resultUserIds = new ArrayList<>();
        while (iterator.hasNext() && resultUserIds.size() < numberToAllow) {
            resultUserIds.add(iterator.next());
        }

        if (!resultUserIds.isEmpty()) {
            final AllowLoansExtension allowLoansExtension =
                    AllowLoansExtension.newBuilder()
                                       .setInventoryId(inventoryId)
                                       .addAllBorrowers(resultUserIds)
                                       .build();
            final CommandRouter commandRouter =
                    newRouterFor(allowLoansExtension, commandContext)
                            .add(allowLoansExtension);
            return Optional.of(commandRouter);
        }
        return Optional.absent();
    }

    private int getUnsatisfiedReservationsCount(List<Reservation> reservations) {
        final int unsatisfiedReservationsCount = (int) reservations.stream()
                                                                   .filter(item -> !item.getIsSatisfied())
                                                                   .count();
        return unsatisfiedReservationsCount;
    }

    private int getForbiddenToExtendLoansCount(List<Loan> loans) {
        final int forbiddenToExtendLoansCount = (int) loans.stream()
                                                           .filter(item -> !item.getIsAllowedExtension())
                                                           .count();
        return forbiddenToExtendLoansCount;
    }

    private Optional<InventoryAggregate> getInventory(InventoryId inventoryId) {
        final InventoryRepository inventoryRepository = InventoryRepository.getRepository();
        final Optional<InventoryAggregate> inventoryOptional =
                inventoryRepository.find(inventoryId);
        return inventoryOptional;
    }
}
