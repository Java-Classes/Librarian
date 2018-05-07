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
import io.spine.Identifier;
import javaclasses.exlibris.BookEventLogViewId;
import javaclasses.exlibris.c.BookBorrowed;
import javaclasses.exlibris.c.BookLost;
import javaclasses.exlibris.c.BookReadyToPickup;
import javaclasses.exlibris.c.BookReturned;
import javaclasses.exlibris.c.LoanBecameOverdue;
import javaclasses.exlibris.c.LoanBecameShouldReturnSoon;
import javaclasses.exlibris.c.LoanPeriodExtended;
import javaclasses.exlibris.c.ReservationAdded;
import javaclasses.exlibris.c.ReservationBecameLoan;
import javaclasses.exlibris.c.ReservationCanceled;
import javaclasses.exlibris.c.ReservationPickUpPeriodExpired;
import javaclasses.exlibris.q.BookEventLogView;
import javaclasses.exlibris.q.ProjectionTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static io.spine.server.projection.ProjectionEventDispatcher.dispatch;
import static javaclasses.exlibris.testdata.InventoryEventFactory.bookBorrowedInstance;
import static javaclasses.exlibris.testdata.InventoryEventFactory.bookLostInstance;
import static javaclasses.exlibris.testdata.InventoryEventFactory.bookReadyToPickUpInstance;
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
import static javaclasses.exlibris.testdata.TestValues.USER_EMAIL_1;
import static javaclasses.exlibris.testdata.TestValues.USER_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;

class BookEventLogViewProjectionTest extends ProjectionTest {
    private BookEventLogViewProjection projection;

    @BeforeEach
    void setUp() {
        final BookEventLogViewId id = BookEventLogViewId.newBuilder()
                                                        .setValue(Identifier.newUuid())
                                                        .build();
        projection = new BookEventLogViewProjection(id);
    }

    @Nested
    @DisplayName("BookBorrowed event should be interpreted by BookEventLogViewProjection and")
    class BookBorrowedEvent {

        @Test
        @DisplayName("add information about start loan period")
        void addInformation() {
            final BookBorrowed bookBorrowed = bookBorrowedInstance();
            dispatch(projection, createEvent(bookBorrowed));
            assertEqualsAllFields(bookBorrowed);
        }

    }

    @Nested
    @DisplayName("BookReturned event should be interpreted by BookEventLogViewProjection and")
    class BookReturnedEvent {

        @Test
        @DisplayName("add information about book returned event")
        void addInformation() {
            final BookReturned bookReturned = bookReturnedInstance();
            dispatch(projection, createEvent(bookReturned));
            assertEqualsAllFields(bookReturned);
        }
    }

    @Nested
    @DisplayName("ReservationAdded event should be interpreted by BookEventLogViewProjection and")
    class ReservationAddedEvent {

        @Test
        @DisplayName("add information about adding reservation")
        void addInformation() {
            final ReservationAdded reservationAdded = reservationAddedInstance();
            dispatch(projection, createEvent(reservationAdded));
            assertEqualsAllFieldsWithoutItemId(reservationAdded);
        }
    }

    @Nested
    @DisplayName("ReservationCanceled event should be interpreted by BookEventLogViewProjection and")
    class ReservationCanceledEvent {

        @Test
        @DisplayName("add information about canceling reservation")
        void addInformation() {
            final ReservationCanceled reservationCanceled = reservationCanceledInstance();
            dispatch(projection, createEvent(reservationCanceled));
            assertEqualsAllFieldsWithoutItemId(reservationCanceled);
        }
    }

    @Nested
    @DisplayName("ReservationPickUpPeriodExpired event should be interpreted by BookEventLogViewProjection and")
    class ReservationPickUpPeriodExpiredEvent {

        @Test
        @DisplayName("add information about expiring loan pick up period")
        void addInformation() {
            final ReservationPickUpPeriodExpired reservationPickUpPeriodExpired =
                    reservationPickUpPeriodExpiredInstance();
            dispatch(projection, createEvent(reservationPickUpPeriodExpired));
            assertEqualsAllFieldsWithoutItemId(reservationPickUpPeriodExpired);
        }
    }

    @Nested
    @DisplayName("ReservationBecameLoan event should be interpreted by BookEventLogViewProjection and")
    class ReservationBecameLoanEvent {

        @Test
        @DisplayName("add information about start loan period after reservation")
        void addInformation() {
            final ReservationBecameLoan reservationBecameLoan =
                    reservationBecameLoanInstance();
            dispatch(projection, createEvent(reservationBecameLoan));
            assertEqualsAllFieldsWithoutItemId(reservationBecameLoan);
        }
    }

    @Nested
    @DisplayName("LoanBecameOverdue event should be interpreted by BookEventLogViewProjection and")
    class LoanBecameOverdueEvent {

        @Test
        @DisplayName("add information about overdue loan period")
        void addInformation() {
            final LoanBecameOverdue loanBecameOverdue =
                    loanBecameOverdueInstance();
            dispatch(projection, createEvent(loanBecameOverdue));
            assertEqualsAllFields(loanBecameOverdue);
        }
    }

    @Nested
    @DisplayName("LoanBecameShouldReturnSoon event should be interpreted by BookEventLogViewProjection and")
    class LoanBecameShouldReturnSoonEvent {

        @Test
        @DisplayName("add information about `should return soon` loan period")
        void addInformation() {
            final LoanBecameShouldReturnSoon loanBecameShouldReturnSoon =
                    loanBecameShouldReturnSoonInstance();
            dispatch(projection, createEvent(loanBecameShouldReturnSoon));
            assertEqualsAllFields(loanBecameShouldReturnSoon);
        }
    }

    @Nested
    @DisplayName("LoanPeriodExtended event should be interpreted by BookEventLogViewProjection and")
    class LoanPeriodExtendedEvent {

        @Test
        @DisplayName("add information about loan period extension")
        void addInformation() {
            final LoanPeriodExtended loanPeriodExtended =
                    loanPeriodExtendedInstance();
            dispatch(projection, createEvent(loanPeriodExtended));
            assertEqualsAllFields(loanPeriodExtended);
        }
    }

    @Nested
    @DisplayName("BookLost event should be interpreted by BookEventLogViewProjection and")
    class BookLostEvent {

        @Test
        @DisplayName("add information about book lost event")
        void addInformation() {
            final BookLost bookLost =
                    bookLostInstance();
            dispatch(projection, createEvent(bookLost));
            assertEqualsAllFields(bookLost);
        }
    }

    @Nested
    @DisplayName("BookReadyToPickUp event should be interpreted by BookEventLogViewProjection and")
    class BookReadyToPickUpEvent {

        @Test
        @DisplayName("add information about BookReadyToPickUp event")
        void addInformation() {
            final BookReadyToPickup bookReadyToPickup =
                    bookReadyToPickUpInstance();
            dispatch(projection, createEvent(bookReadyToPickup));
            assertEqualsAllFieldsWithoutItemId(bookReadyToPickup);
        }
    }

    private void assertEqualsAllFields(Message bookBorrowed) {
        final BookEventLogView state = projection.getState();
        assertEquals(INVENTORY_ITEM_ID_1, state.getItemId());
        assertEqualsAllFieldsWithoutItemId(bookBorrowed);
    }

    private void assertEqualsAllFieldsWithoutItemId(Message bookBorrowed) {
        final BookEventLogView state = projection.getState();
        assertEquals(BOOK_ID, state.getBookId());
        assertEquals(USER_NAME, state.getUserName());
        assertEquals(USER_EMAIL_1, state.getEmail());
        assertEquals(bookBorrowed.getClass()
                                 .getSimpleName(), state.getEventType());
        assertEquals(DEFAULT_TIMESTAMP1, state.getWhenEmitted());
    }

}
