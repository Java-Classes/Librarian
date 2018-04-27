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

package javaclasses.exlibris.c.inventory;

import com.google.protobuf.Message;
import javaclasses.exlibris.Inventory;
import javaclasses.exlibris.Reservation;
import javaclasses.exlibris.UserId;
import javaclasses.exlibris.c.BookReadyToPickup;
import javaclasses.exlibris.c.ReserveBook;
import javaclasses.exlibris.c.SatisfyReservation;
import javaclasses.exlibris.testdata.InventoryCommandFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.spine.server.aggregate.AggregateMessageDispatcher.dispatchCommand;
import static javaclasses.exlibris.testdata.InventoryCommandFactory.satisfyReservationInstance;
import static javaclasses.exlibris.testdata.InventoryCommandFactory.userId;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Yegor Udovchenko
 */
@DisplayName("SatisfyReservation command should be interpreted by InventoryAggregate and")
public class SatisfyReservationCommandTest extends InventoryCommandTest<SatisfyReservation> {
    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
        dispatchReserveBook();
    }

    @Test
    @DisplayName("produce BookReadyToPickup event")
    void produceEvent() {
        final SatisfyReservation satisfyReservation = satisfyReservationInstance();
        final List<? extends Message> messageList =
                dispatchCommand(aggregate, envelopeOf(satisfyReservation));

        assertEquals(1, messageList.size());
        assertEquals(BookReadyToPickup.class, messageList.get(0)
                                                         .getClass());
        // Pick up period. Seconds in two days.
        final long OPEN_FOR_BORROW = 60 * 60 * 24 * 2;
        final BookReadyToPickup bookReadyToPickup = (BookReadyToPickup) messageList.get(0);
        final UserId forWhom = bookReadyToPickup.getForWhom();
        final long whenBecame = bookReadyToPickup.getWhenBecameReadyToPickup()
                                                 .getSeconds();
        final long pickUpDeadline = bookReadyToPickup.getPickUpDeadline()
                                                     .getSeconds();
        assertEquals(whenBecame + OPEN_FOR_BORROW, pickUpDeadline);
        assertEquals(userId, forWhom);
    }

    @Test
    @DisplayName("set reservation as satisfied")
    void setReservationSatisfied() {
        final Inventory stateBefore = aggregate.getState();
        final Reservation reservation = stateBefore.getReservations(0);
        assertFalse(reservation.getIsSatisfied());

        final SatisfyReservation satisfyReservation = satisfyReservationInstance();
        dispatchCommand(aggregate, envelopeOf(satisfyReservation));

        final Inventory stateAfter = aggregate.getState();
        final Reservation reservationAfter = stateAfter.getReservations(0);
        assertTrue(reservationAfter.getIsSatisfied());
    }

    private void dispatchReserveBook() {
        final ReserveBook reserveBook = InventoryCommandFactory.reserveBookInstance();
        dispatchCommand(aggregate, envelopeOf(reserveBook));
    }
}
