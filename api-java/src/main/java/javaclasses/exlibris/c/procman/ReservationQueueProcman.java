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
import io.spine.core.CommandContext;
import io.spine.core.EventContext;
import io.spine.core.React;
import io.spine.server.procman.CommandRouted;
import io.spine.server.procman.CommandRouter;
import io.spine.server.procman.ProcessManager;
import javaclasses.exlibris.InventoryId;
import javaclasses.exlibris.Reservation;
import javaclasses.exlibris.ReservationQueue;
import javaclasses.exlibris.ReservationQueueId;
import javaclasses.exlibris.ReservationQueueVBuilder;
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

public class ReservationQueueProcman extends ProcessManager<ReservationQueueId, ReservationQueue, ReservationQueueVBuilder> {

    /**
     * As long as the {@code ReservationQueueProcman} is an {@code InventoryAggregate}
     * service and does not hold model state it is a singleton. All subscribed events
     * are routed to the single instance.
     */
    protected static final ReservationQueueId ID =
            ReservationQueueId.newBuilder()
                              .setValue("ReservationQueueSingleton")
                              .build();

    /**
     * Creates a new instance.
     *
     * @param id an ID for the new instance
     * @throws IllegalArgumentException if the ID type is unsupported
     * @see ReservationQueue for more details.
     */
    protected ReservationQueueProcman(ReservationQueueId id) {
        super(id);
    }

    @React
    CommandRouted on(BookReturned event, EventContext eventContext) {
        final InventoryId inventoryId = event.getInventoryId();
        final CommandContext commandContext = eventContext.getCommandContext();
        final CommandRouter commandRouter =
                markBookAsAvailableOrSatisfyReservationRouter(inventoryId, commandContext);
        return commandRouter.routeAll();
    }

    @React
    CommandRouted on(InventoryAppended event, EventContext eventContext) {
        final InventoryId inventoryId = event.getInventoryId();
        final CommandContext commandContext = eventContext.getCommandContext();
        final CommandRouter commandRouter =
                markBookAsAvailableOrSatisfyReservationRouter(inventoryId, commandContext);
        return commandRouter.routeAll();
    }

    @React
    CommandRouted on(ReservationPickUpPeriodExpired event, EventContext eventContext) {
        final InventoryId inventoryId = event.getInventoryId();
        final CommandContext commandContext = eventContext.getCommandContext();
        final CommandRouter commandRouter =
                markBookAsAvailableOrSatisfyReservationRouter(inventoryId, commandContext);
        return commandRouter.routeAll();
    }

    /**
     * Reacts on {@code ReservationCanceled} event.
     *
     * <p>Performs action only when the canceled reservation was satisfied (in that case available
     * books count has changed).
     *
     * @param event        the {@code ReservationCanceled} event to react on.
     * @param eventContext the event context
     * @return routed command.
     */
    @React
    CommandRouted on(ReservationCanceled event, EventContext eventContext) {
        final boolean reservationWasSatisfied = event.getWasSatisfied();
        if (reservationWasSatisfied) {
            final InventoryId inventoryId = event.getInventoryId();
            final CommandContext commandContext = eventContext.getCommandContext();
            final CommandRouter commandRouter =
                    markBookAsAvailableOrSatisfyReservationRouter(inventoryId, commandContext);
            return commandRouter.routeAll();
        }
        return null;
    }

    /**
     * Checks the reservation queue for unsatisfied reservations.
     *
     * <p>If there are unsatisfied reservations, gets the first one and creates a
     * {@link SatisfyReservation} command for it. If there are no reservations to satisfy
     * creates a {@link MarkBookAsAvailable} command.
     *
     * @param inventoryId    the inventory identifier to check its reservations.
     * @param commandContext the command context to route commands.
     * @return the command router that routes specified command in one call
     */
    private CommandRouter markBookAsAvailableOrSatisfyReservationRouter(InventoryId inventoryId,
                                                                        CommandContext commandContext) {
        final Optional<UserId> userIdOptional = findUserToSatisfyReservation(inventoryId);
        if (userIdOptional.isPresent()) {
            final UserId userId = userIdOptional.get();
            final CommandRouter satisfyReservation = createSatisfyReservationRouter(inventoryId,
                                                                                    commandContext,
                                                                                    userId);
            return satisfyReservation;
        }
        final CommandRouter markBookAsAvailable = createMarkBookAsAvailableRouter(inventoryId,
                                                                                  commandContext);
        return markBookAsAvailable;
    }

    private CommandRouter createMarkBookAsAvailableRouter(InventoryId inventoryId,
                                                          CommandContext commandContext) {
        final MarkBookAsAvailable markBookAsAvailable =
                MarkBookAsAvailable.newBuilder()
                                   .setInventoryId(inventoryId)
                                   .build();
        return newRouterFor(markBookAsAvailable, commandContext).add(markBookAsAvailable);
    }

    private CommandRouter createSatisfyReservationRouter(InventoryId inventoryId,
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

        final Optional<InventoryAggregate> inventoryOptional = inventoryRepository.find(
                inventoryId);

        return inventoryOptional;
    }

    /**
     * Finds first unsatisfied reservation in queue.
     *
     * @param inventoryId the inventory identifier to check its reservations.
     * @return Optional user identifier to satisfy reservation, Optional.absent if there are no
     * reservations to satis
     */
    private Optional<UserId> findUserToSatisfyReservation(InventoryId inventoryId) {
        final Optional<InventoryAggregate> inventoryOptional = getInventory(inventoryId);
        if (!inventoryOptional.isPresent()) {
            final String errorMessage = "The aggregate state for %s identifier not found in the InventoryRepository.";
            throw new IllegalArgumentException(String.format(errorMessage, inventoryId.getBookId()
                                                                                      .getIsbn62()));
        }
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
