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

import javaclasses.exlibris.Reservation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("ReservationQueueProcman should")
public class ReservationQueueProcmanTest extends ProcessManagerTest {
    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
    }

    @Test
    @DisplayName("react on book returned event and make book available for borrowing.")
    void reactOnBookReturnedMakesAvailable() {
        commandBus.post(appendInventoryItem1, observer);
        commandBus.post(borrowBookUser1Item1, observer);
        commandBus.post(returnBookUser1Item1, observer);
        commandBus.post(borrowBookUser1Item1, observer);

        assertFalse(inventoryRejectionsSubscriber.wasCalled());
    }

    @Test
    @DisplayName("react on book returned event and make book ready to pick up for next in reservation queue.")
    void reactOnBookReturnedMakesReadyToPickUp() {
        commandBus.post(appendInventoryItem1, observer);
        commandBus.post(borrowBookUser1Item1, observer);
        commandBus.post(reserveBookUser2, observer);
        commandBus.post(returnBookUser1Item1, observer);
        commandBus.post(borrowBookUser1Item1, observer);

        final Reservation reservation = getAggregateState().getReservations(0);
        assertTrue(reservation.getIsSatisfied());
        assertTrue(inventoryRejectionsSubscriber.wasCalled());
    }

    @Test
    @DisplayName("react on reservation canceled event and make book available for borrowing.")
    void reactOnCancelReservationMakesBookAvailable() {
        commandBus.post(appendInventoryItem1, observer);
        commandBus.post(borrowBookUser1Item1, observer);
        commandBus.post(reserveBookUser2, observer);
        commandBus.post(returnBookUser1Item1, observer);
        commandBus.post(cancelReservationUser2, observer);
        commandBus.post(borrowBookUser1Item1, observer);

        assertFalse(inventoryRejectionsSubscriber.wasCalled());
    }

    @Test
    @DisplayName("react on reservation canceled event and make book ready to pick up for next in reservation queue.")
    void reactOnCancelReservationMakesReadyToPickUp() {
        commandBus.post(reserveBookUser2, observer);
        commandBus.post(reserveBookUser3, observer);
        commandBus.post(appendInventoryItem1, observer);
        commandBus.post(cancelReservationUser2, observer);
        commandBus.post(borrowBookUser3Item1, observer);

        assertFalse(inventoryRejectionsSubscriber.wasCalled());
    }

    @Test
    @DisplayName("react on reservation pickup period expired event and make book available for borrowing.")
    void reactOnReservationExpiredMakesBookAvailable() {
        commandBus.post(reserveBookUser3, observer);
        commandBus.post(appendInventoryItem1, observer);
        commandBus.post(expireReservationUser3, observer);
        commandBus.post(reserveBookUser2, observer);

        assertTrue(inventoryRejectionsSubscriber.wasCalled());
    }

    @Test
    @DisplayName("react on reservation pickup period expired event and make book ready to pick up for next in reservation queue.")
    void reactOnReservationExpiredMakesReadyToPickUp() {
        commandBus.post(reserveBookUser3, observer);
        commandBus.post(appendInventoryItem1, observer);
        commandBus.post(reserveBookUser2, observer);
        commandBus.post(expireReservationUser3, observer);
        commandBus.post(borrowBookUser1Item1, observer);

        assertTrue(inventoryRejectionsSubscriber.wasCalled());
        inventoryRejectionsSubscriber.clear();

        commandBus.post(borrowBookUser2Item1, observer);
        assertFalse(inventoryRejectionsSubscriber.wasCalled());
    }
}
