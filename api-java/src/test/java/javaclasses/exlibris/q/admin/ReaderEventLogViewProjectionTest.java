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

import com.google.protobuf.Message;
import javaclasses.exlibris.ReaderEventLogViewId;
import javaclasses.exlibris.c.BookBorrowed;
import javaclasses.exlibris.c.BookLost;
import javaclasses.exlibris.c.BookReturned;
import javaclasses.exlibris.c.LoanBecameOverdue;
import javaclasses.exlibris.c.LoanBecameShouldReturnSoon;
import javaclasses.exlibris.c.LoanPeriodExtended;
import javaclasses.exlibris.c.ReservationAdded;
import javaclasses.exlibris.c.ReservationBecameLoan;
import javaclasses.exlibris.c.ReservationCanceled;
import javaclasses.exlibris.c.ReservationPickUpPeriodExpired;
import javaclasses.exlibris.q.ProjectionTest;
import javaclasses.exlibris.q.ReaderEventLogView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static io.spine.Identifier.newUuid;
import static io.spine.server.projection.ProjectionEventDispatcher.dispatch;
import static javaclasses.exlibris.testdata.InventoryEventFactory.bookBorrowedInstance;
import static javaclasses.exlibris.testdata.InventoryEventFactory.bookLostInstance;
import static javaclasses.exlibris.testdata.InventoryEventFactory.bookReturnedInstance;
import static javaclasses.exlibris.testdata.InventoryEventFactory.loanBecameOverdueInstance;
import static javaclasses.exlibris.testdata.InventoryEventFactory.loanBecameShouldReturnSoonInstance;
import static javaclasses.exlibris.testdata.InventoryEventFactory.loanPeriodExtendedInstance;
import static javaclasses.exlibris.testdata.InventoryEventFactory.reservationAddedInstance;
import static javaclasses.exlibris.testdata.InventoryEventFactory.reservationBecameLoanInstance;
import static javaclasses.exlibris.testdata.InventoryEventFactory.reservationCanceledInstance;
import static javaclasses.exlibris.testdata.InventoryEventFactory.reservationPickUpPeriodExpiredInstance;
import static javaclasses.exlibris.testdata.TestValues.BOOK_ID;
import static javaclasses.exlibris.testdata.TestValues.DEFAULT_TIMESTAMP1;
import static javaclasses.exlibris.testdata.TestValues.INVENTORY_ITEM_ID_1;
import static javaclasses.exlibris.testdata.TestValues.USER_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ReaderEventLogViewProjectionTest extends ProjectionTest {

    private ReaderEventLogViewProjection projection;

    @BeforeEach
    void setUp() {
        ReaderEventLogViewId id = ReaderEventLogViewId.newBuilder()
                                                      .setValue(newUuid())
                                                      .build();
        projection = new ReaderEventLogViewProjection(id);
    }

    @Nested
    @DisplayName("BookBorrowed event should be interpreted by ReaderEventLogViewProjection and")
    class BookBorrowedEvent {

        @Test
        @DisplayName("add information about start loan period")
        void addInformation() {
            final BookBorrowed bookBorrowed = bookBorrowedInstance();
            dispatch(projection, createEvent(bookBorrowed));

            assertEqualsWithItemId(bookBorrowed);
        }
    }

    @Nested
    @DisplayName("BookReturned event should be interpreted by ReaderEventLogViewProjection and")
    class BookReturnedEvent {

        @Test
        @DisplayName("add information about `BookReturned` event")
        void addInformation() {
            final BookReturned bookReturned = bookReturnedInstance();
            dispatch(projection, createEvent(bookReturned));
            assertEqualsWithItemId(bookReturned);
        }
    }

    @Nested
    @DisplayName("ReservationAdded event should be interpreted by ReaderEventLogViewProjection and")
    class ReservationAddedEvent {

        @Test
        @DisplayName("add information about `ReservationAdded` event")
        void addInformation() {
            final ReservationAdded reservationAdded = reservationAddedInstance();
            dispatch(projection, createEvent(reservationAdded));
            assertEqualsWithBookId(reservationAdded);
        }
    }

    @Nested
    @DisplayName("ReservationCanceled event should be interpreted by ReaderEventLogViewProjection and")
    class ReservationCanceledEvent {

        @Test
        @DisplayName("add information about `ReservationCanceled` event")
        void addInformation() {
            final ReservationCanceled reservationCanceled = reservationCanceledInstance();
            dispatch(projection, createEvent(reservationCanceled));
            assertEqualsWithBookId(reservationCanceled);
        }
    }

    @Nested
    @DisplayName("ReservationPickUpPeriodExpired event should be interpreted by ReaderEventLogViewProjection and")
    class ReservationPickUpPeriodExpiredEvent {

        @Test
        @DisplayName("add information about `ReservationPickUpPeriodExpired` event")
        void addInformation() {
            final ReservationPickUpPeriodExpired reservationPickUpPeriodExpired =
                    reservationPickUpPeriodExpiredInstance();
            dispatch(projection, createEvent(reservationPickUpPeriodExpired));
            assertEqualsWithBookId(reservationPickUpPeriodExpired);
        }
    }

    @Nested
    @DisplayName("ReservationBecameLoan event should be interpreted by ReaderEventLogViewProjection and")
    class ReservationBecameLoanEvent {

        @Test
        @DisplayName("add information about `ReservationBecameLoan` event")
        void addInformation() {
            final ReservationBecameLoan reservationBecameLoan =
                    reservationBecameLoanInstance();
            dispatch(projection, createEvent(reservationBecameLoan));
            assertEqualsWithBookId(reservationBecameLoan);
        }
    }

    @Nested
    @DisplayName("LoanBecameOverdue event should be interpreted by ReaderEventLogViewProjection and")
    class LoanBecameOverdueEvent {

        @Test
        @DisplayName("add information about `LoanBecameOverdue` event")
        void addInformation() {
            final LoanBecameOverdue loanBecameOverdue =
                    loanBecameOverdueInstance();
            dispatch(projection, createEvent(loanBecameOverdue));
            assertEqualsWithItemId(loanBecameOverdue);
        }
    }

    @Nested
    @DisplayName("LoanBecameShouldReturnSoon event should be interpreted by ReaderEventLogViewProjection and")
    class LoanBecameShouldReturnSoonEvent {

        @Test
        @DisplayName("add information about `LoanBecameShouldReturnSoon` event")
        void addInformation() {
            final LoanBecameShouldReturnSoon loanBecameShouldReturnSoon =
                    loanBecameShouldReturnSoonInstance();
            dispatch(projection, createEvent(loanBecameShouldReturnSoon));
            assertEqualsWithItemId(loanBecameShouldReturnSoon);
        }
    }

    @Nested
    @DisplayName("LoanPeriodExtended event should be interpreted by ReaderEventLogViewProjection and")
    class LoanPeriodExtendedEvent {

        @Test
        @DisplayName("add information about `LoanPeriodExtended` event")
        void addInformation() {
            final LoanPeriodExtended loanPeriodExtended = loanPeriodExtendedInstance();
            dispatch(projection, createEvent(loanPeriodExtended));
            assertEqualsWithItemId(loanPeriodExtended);
        }
    }

    @Nested
    @DisplayName("BookLost event should be interpreted by ReaderEventLogViewProjection and")
    class BookLostEvent {

        @Test
        @DisplayName("add information about `BookLost` event")
        void addInformation() {
            final BookLost bookLost = bookLostInstance();
            dispatch(projection, createEvent(bookLost));
            assertEqualsWithItemId(bookLost);
        }
    }

    private void assertEqualsWithItemId(Message event) {
        final ReaderEventLogView state = projection.getState();
        assertEquals(USER_ID, state.getUserId());
        assertEquals(INVENTORY_ITEM_ID_1, state.getItemId());
        assertEquals(event.getClass()
                          .getSimpleName(), state.getEventType());
        assertEquals(DEFAULT_TIMESTAMP1, state.getWhenEmitted());
    }

    private void assertEqualsWithBookId(Message event) {
        final ReaderEventLogView state = projection.getState();
        assertEquals(USER_ID, state.getUserId());
        assertEquals(BOOK_ID, state.getBookId());
        assertEquals(event.getClass()
                          .getSimpleName(), state.getEventType());
        assertEquals(DEFAULT_TIMESTAMP1, state.getWhenEmitted());
    }
}
