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

package io.spine.javaclasses.exlibris.c.aggregate.definition;

import com.google.common.base.Throwables;
import com.google.protobuf.Message;
import io.spine.javaclasses.exlibris.testdata.InventoryCommandFactory;
import javaclasses.exlibris.Inventory;
import javaclasses.exlibris.c.AppendInventory;
import javaclasses.exlibris.c.CancelReservation;
import javaclasses.exlibris.c.ReservationCanceled;
import javaclasses.exlibris.c.ReserveBook;
import javaclasses.exlibris.c.rejection.CannotCancelMissingReservation;
import javaclasses.exlibris.c.rejection.CannotWriteMissingBookOff;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.spine.server.aggregate.AggregateMessageDispatcher.dispatchCommand;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Alexander Karpets
 * @author Paul Ageyev
 */
public class CancelReservationCommandTest extends InventoryCommandTest<AppendInventory> {

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
    }

    private void reserveBook() {
        final ReserveBook reserveBook = InventoryCommandFactory.reserveBookInstance();
        dispatchCommand(aggregate, envelopeOf(reserveBook));
    }

    @Test
    @DisplayName("cancel reservation successfully produces event")
    void produceEvent() {
        reserveBook();
        final CancelReservation cancelReservation = InventoryCommandFactory.cancelReservationInstance();

        final List<? extends Message> messageList = dispatchCommand(aggregate,
                                                                    envelopeOf(cancelReservation));
        assertNotNull(aggregate.getId());
        assertEquals(1, messageList.size());
        assertEquals(ReservationCanceled.class, messageList.get(0)
                                                           .getClass());

        final ReservationCanceled reservationCanceled = (ReservationCanceled) messageList.get(0);

        assertEquals(InventoryCommandFactory.inventoryId, reservationCanceled.getInventoryId());

        assertEquals(InventoryCommandFactory.userId.getEmail()
                                                   .getValue(), reservationCanceled.getWhoCanceled()
                                                                                   .getEmail()
                                                                                   .getValue());
    }

    @Test
    @DisplayName("a reservation canceled")
    void cancelReservation() {
        reserveBook();
        final Inventory inventoryReserved = aggregate.getState();
        assertEquals(1, inventoryReserved.getReservationsList()
                                         .size());
        final CancelReservation cancelReservation = InventoryCommandFactory.cancelReservationInstance();
        dispatchCommand(aggregate, envelopeOf(cancelReservation));

        final Inventory inventory = aggregate.getState();
        assertEquals(0, inventory.getReservationsList()
                                 .size());
    }

    @Test
    @DisplayName("reservation not canceled")
    void notCancelReservation() {

        final CancelReservation cancelReservation = InventoryCommandFactory.cancelReservationInstance();

        final Throwable t = assertThrows(Throwable.class,
                                         () -> dispatchCommand(aggregate,
                                                               envelopeOf(cancelReservation)));

        final Throwable cause = Throwables.getRootCause(t);

        assertThat(cause, instanceOf(CannotCancelMissingReservation.class));

    }
}
