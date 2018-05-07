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

package javaclasses.exlibris.q.admin;

import io.spine.server.entity.LifecycleFlags;
import javaclasses.exlibris.BookReservationViewId;
import javaclasses.exlibris.c.ReservationAdded;
import javaclasses.exlibris.c.ReservationBecameLoan;
import javaclasses.exlibris.c.ReservationCanceled;
import javaclasses.exlibris.c.ReservationPickUpPeriodExpired;
import javaclasses.exlibris.q.BookReservationView;
import javaclasses.exlibris.q.ProjectionTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static io.spine.server.projection.ProjectionEventDispatcher.dispatch;
import static javaclasses.exlibris.testdata.InventoryEventFactory.reservationAddedInstance;
import static javaclasses.exlibris.testdata.InventoryEventFactory.reservationBecameLoanInstance;
import static javaclasses.exlibris.testdata.InventoryEventFactory.reservationCanceledInstance;
import static javaclasses.exlibris.testdata.InventoryEventFactory.reservationPickUpPeriodExpiredInstance;
import static javaclasses.exlibris.testdata.TestValues.BOOK_ID;
import static javaclasses.exlibris.testdata.TestValues.DEFAULT_TIMESTAMP1;
import static javaclasses.exlibris.testdata.TestValues.USER_EMAIL_1;
import static javaclasses.exlibris.testdata.TestValues.USER_ID;
import static javaclasses.exlibris.testdata.TestValues.USER_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BookReservationViewProjectionTest extends ProjectionTest {

    private BookReservationViewProjection projection;

    @BeforeEach
    void setUp() {
        final BookReservationViewId id = BookReservationViewId.newBuilder()
                                                              .setUserId(USER_ID)
                                                              .setBookId(BOOK_ID)
                                                              .build();
        projection = new BookReservationViewProjection(id);
    }

    @Nested
    @DisplayName("ReservationAdded event should be interpreted by" +
            " BookReservationViewProjection and")
    class BookAddedEvent {

        @Test
        @DisplayName("add information about start of reservation")
        void addInformation() {
            final ReservationAdded reservationAdded = reservationAddedInstance();
            dispatch(projection, createEvent(reservationAdded));

            final BookReservationView state = projection.getState();
            assertEquals(BOOK_ID, state.getBookId());
            assertEquals(USER_NAME, state.getUserName());
            assertEquals(USER_EMAIL_1, state.getEmail());
            assertEquals(DEFAULT_TIMESTAMP1, state.getWhenReserved());
        }
    }

    @Nested
    @DisplayName("ReservationCanceled event should be interpreted by" +
            " BookReservationViewProjection and")
    class ReservationCanceledEvent {

        @Test
        @DisplayName("clear this projection")
        void clearProjection() {
            final ReservationAdded reservationAdded = reservationAddedInstance();
            dispatch(projection, createEvent(reservationAdded));

            final ReservationCanceled reservationCanceled = reservationCanceledInstance();
            dispatch(projection, createEvent(reservationCanceled));

            final LifecycleFlags lifecycleFlags = projection.getLifecycleFlags();
            assertTrue(lifecycleFlags.getDeleted());
        }
    }

    @Nested
    @DisplayName("ReservationPickUpPeriodExpired event should be interpreted by" +
            " BookReservationViewProjection and")
    class ReservationPickUpPeriodExpiredEvent {

        @Test
        @DisplayName("clear this projection")
        void clearProjection() {
            final ReservationAdded reservationAdded = reservationAddedInstance();
            dispatch(projection, createEvent(reservationAdded));

            final ReservationPickUpPeriodExpired reservationPickUpPeriodExpired =
                    reservationPickUpPeriodExpiredInstance();
            dispatch(projection, createEvent(reservationPickUpPeriodExpired));

            final LifecycleFlags lifecycleFlags = projection.getLifecycleFlags();
            assertTrue(lifecycleFlags.getDeleted());
        }
    }

    @Nested
    @DisplayName("ReservationBecameLoan event should be interpreted by" +
            " BookReservationViewProjection and")
    class ReservationBecameLoanEvent {

        @Test
        @DisplayName("clear this projection")
        void clearProjection() {
            final ReservationAdded reservationAdded = reservationAddedInstance();
            dispatch(projection, createEvent(reservationAdded));

            final ReservationBecameLoan reservationBecameLoan =
                    reservationBecameLoanInstance();
            dispatch(projection, createEvent(reservationBecameLoan));

            final LifecycleFlags lifecycleFlags = projection.getLifecycleFlags();
            assertTrue(lifecycleFlags.getDeleted());
        }
    }
}
