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
import com.google.common.collect.Iterables;
import com.google.protobuf.Empty;
import io.spine.core.CommandContext;
import io.spine.core.EventContext;
import io.spine.core.React;
import io.spine.server.procman.CommandRouted;
import io.spine.server.procman.CommandRouter;
import io.spine.server.procman.ProcessManager;
import io.spine.validate.EmptyVBuilder;
import javaclasses.exlibris.InventoryId;
import javaclasses.exlibris.Reservation;
import javaclasses.exlibris.ReservationQueueId;
import javaclasses.exlibris.UserId;
import javaclasses.exlibris.c.BookReturned;
import javaclasses.exlibris.c.InventoryAppended;
import javaclasses.exlibris.c.MarkBookAsAvailable;
import javaclasses.exlibris.c.ReservationCanceled;
import javaclasses.exlibris.c.ReservationPickUpPeriodExpired;
import javaclasses.exlibris.c.SatisfyReservation;
import javaclasses.exlibris.c.inventory.InventoryAggregate;
import javaclasses.exlibris.c.inventory.InventoryRepository;

import java.util.List;

public class ReservationQueue extends ProcessManager<ReservationQueueId, Empty, EmptyVBuilder> {

    /**
     * Creates a new instance.
     *
     * @param id an ID for the new instance
     * @throws IllegalArgumentException if the ID type is unsupported
     */
    protected ReservationQueue(ReservationQueueId id) {
        super(id);
    }

    public static final ReservationQueueId ID =
            ReservationQueueId.newBuilder()
                              .setValue("ReservationQueueSingleton")
                              .build();

    @React
    CommandRouted on(BookReturned event, EventContext ctx) {
        final InventoryId inventoryId = event.getInventoryId();
        final CommandContext commandContext = ctx.getCommandContext();
        final CommandRouter commandRouter = markBookAsAvailableOrSatisfyReservation(inventoryId,
                                                                                    commandContext);
        return commandRouter.routeAll();
    }

    @React
    CommandRouted on(InventoryAppended event, EventContext ctx) {
        final InventoryId inventoryId = event.getInventoryId();
        final CommandContext commandContext = ctx.getCommandContext();
        final CommandRouter commandRouter = markBookAsAvailableOrSatisfyReservation(inventoryId,
                                                                                    commandContext);
        return commandRouter.routeAll();
    }

    @React
    CommandRouted on(ReservationPickUpPeriodExpired event, EventContext ctx) {
        final InventoryId inventoryId = event.getInventoryId();
        final CommandContext commandContext = ctx.getCommandContext();
        final CommandRouter commandRouter = markBookAsAvailableOrSatisfyReservation(inventoryId,
                                                                                    commandContext);
        return commandRouter.routeAll();
    }

    @React
    CommandRouted on(ReservationCanceled event, EventContext ctx) {
        final boolean reservationWasSatisfied = event.getWasSatisfied();
        if (reservationWasSatisfied) {
            final InventoryId inventoryId = event.getInventoryId();
            final CommandContext commandContext = ctx.getCommandContext();
            final CommandRouter commandRouter = markBookAsAvailableOrSatisfyReservation(inventoryId,
                                                                                        commandContext);
            return commandRouter.routeAll();
        }

        return CommandRouted.getDefaultInstance();
    }

    private CommandRouter markBookAsAvailableOrSatisfyReservation(InventoryId inventoryId,
                                                                  CommandContext commandContext) {
        final Optional<UserId> userIdOptional = findUserToSatisfyReservation(inventoryId);
        if (userIdOptional.isPresent()) {
            final UserId userId = userIdOptional.get();
            final CommandRouter satisfyReservation = createSatisfyReservation(inventoryId,
                                                                              commandContext,
                                                                              userId);
            return satisfyReservation;
        }
        final CommandRouter markBookAsAvailable = createMarkBookAsAvailable(inventoryId,
                                                                            commandContext);
        return markBookAsAvailable;
    }

    private CommandRouter createMarkBookAsAvailable(InventoryId inventoryId,
                                                    CommandContext commandContext) {
        final MarkBookAsAvailable markBookAsAvailable =
                MarkBookAsAvailable.newBuilder()
                                   .setInventoryId(inventoryId)
                                   .build();
        return newRouterFor(markBookAsAvailable, commandContext).add(markBookAsAvailable);
    }

    private CommandRouter createSatisfyReservation(InventoryId inventoryId,
                                                   CommandContext commandContext,
                                                   UserId userId) {
        final SatisfyReservation satisfyReservation = SatisfyReservation.newBuilder()
                                                                        .setInventoryId(inventoryId)
                                                                        .setUserId(userId)
                                                                        .build();
        return newRouterFor(satisfyReservation, commandContext).add(satisfyReservation);
    }

    private Optional<InventoryAggregate> getInventory(InventoryId inventoryId) {
        final InventoryRepository inventoryRepository = InventoryRepository.getRepository();

        final Optional<InventoryAggregate> inventory = inventoryRepository.find(inventoryId);

        return inventory;
    }

    private Optional<UserId> findUserToSatisfyReservation(InventoryId inventoryId) {
        final Optional<InventoryAggregate> inventoryOptional = getInventory(inventoryId);
        final InventoryAggregate inventory = inventoryOptional.get();

        final List<Reservation> reservations = inventory.getState()
                                                        .getReservationsList();
        final int index = Iterables.indexOf(reservations, item -> !item.getIsSatisfied());

        if (index != -1) {
            final Reservation reservationToSatisfy = reservations.get(index);
            final UserId whoReserved = reservationToSatisfy.getWhoReserved();
            return Optional.of(whoReserved);
        }
        return Optional.absent();
    }
}
