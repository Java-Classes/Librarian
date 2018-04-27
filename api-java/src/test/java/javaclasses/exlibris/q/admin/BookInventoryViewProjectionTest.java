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

import javaclasses.exlibris.c.BookBorrowed;
import javaclasses.exlibris.c.BookLost;
import javaclasses.exlibris.c.BookReturned;
import javaclasses.exlibris.c.InventoryAppended;
import javaclasses.exlibris.c.InventoryDecreased;
import javaclasses.exlibris.c.LoanBecameOverdue;
import javaclasses.exlibris.c.LoanPeriodExtended;
import javaclasses.exlibris.q.BookInventoryView;
import javaclasses.exlibris.q.ProjectionTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static io.spine.server.projection.ProjectionEventDispatcher.dispatch;
import static javaclasses.exlibris.testdata.BookEventFactory.AUTHOR;
import static javaclasses.exlibris.testdata.BookEventFactory.TITLE;
import static javaclasses.exlibris.testdata.InventoryEventFactory.BORROWED_ITEM_STATE;
import static javaclasses.exlibris.testdata.InventoryEventFactory.DEFAULT_DATE2;
import static javaclasses.exlibris.testdata.InventoryEventFactory.INVENTORY_ID;
import static javaclasses.exlibris.testdata.InventoryEventFactory.IN_LIBRARY_ITEM_STATE;
import static javaclasses.exlibris.testdata.InventoryEventFactory.LOST_ITEM_STATE;
import static javaclasses.exlibris.testdata.InventoryEventFactory.OVERDUE_ITEM_STATE;
import static javaclasses.exlibris.testdata.InventoryEventFactory.bookBorrowedInstance;
import static javaclasses.exlibris.testdata.InventoryEventFactory.bookLostInstance;
import static javaclasses.exlibris.testdata.InventoryEventFactory.bookReturnedInstance;
import static javaclasses.exlibris.testdata.InventoryEventFactory.inventoryAppendedInstance;
import static javaclasses.exlibris.testdata.InventoryEventFactory.inventoryDecreasedInstance;
import static javaclasses.exlibris.testdata.InventoryEventFactory.loanBecameOverdueInstance;
import static javaclasses.exlibris.testdata.InventoryEventFactory.loanPeriodExtendedInstance;
import static org.junit.jupiter.api.Assertions.assertEquals;

class BookInventoryViewProjectionTest extends ProjectionTest {

    private BookInventoryViewProjection projection;

    @BeforeEach
    void setUp() {
        projection = new BookInventoryViewProjection(INVENTORY_ID);
    }

    @Nested
    @DisplayName("InventoryAppended event should be interpreted by BookInventoryViewProjection and")
    class InventoryAppendedEvent {

        @Test
        @DisplayName("add information about inventory item")
        void addInformation() {
            final InventoryAppended inventoryAppended = inventoryAppendedInstance();
            dispatch(projection, createEvent(inventoryAppended));

            final BookInventoryView state = projection.getState();
            assertEquals(TITLE, state.getTitle());
            assertEquals(AUTHOR, state.getAuthor());
            assertEquals(IN_LIBRARY_ITEM_STATE, state.getItemState(0));
        }
    }

    @Nested
    @DisplayName("InventoryDecreased event should be interpreted by BookInventoryViewProjection and")
    class InventoryDecreasedEvent {

        @Test
        @DisplayName("clear this projection")
        void clearProjection() {
            final InventoryAppended inventoryAppended = inventoryAppendedInstance();
            dispatch(projection, createEvent(inventoryAppended));

            final InventoryDecreased inventoryDecreased =
                    inventoryDecreasedInstance();
            dispatch(projection, createEvent(inventoryDecreased));

            final BookInventoryView state = projection.getState();
            assertEquals("", state.toString());
        }
    }

    @Nested
    @DisplayName("BookBorrowed event should be interpreted by BookInventoryViewProjection and")
    class BookBorrowedEvent {

        @Test
        @DisplayName("all loan details")
        void addLoanDetails() {
            final InventoryAppended inventoryAppended = inventoryAppendedInstance();
            dispatch(projection, createEvent(inventoryAppended));

            final BookBorrowed bookBorrowed = bookBorrowedInstance();
            dispatch(projection, createEvent(bookBorrowed));

            final BookInventoryView state = projection.getState();
            assertEquals(BORROWED_ITEM_STATE, state.getItemState(0));
        }
    }

    @Nested
    @DisplayName("BookLost event should be interpreted by BookInventoryViewProjection and")
    class BookLostEvent {

        @Test
        @DisplayName("change inventory item state")
        void changeState() {
            final InventoryAppended inventoryAppended = inventoryAppendedInstance();
            dispatch(projection, createEvent(inventoryAppended));

            final BookLost bookLost = bookLostInstance();
            dispatch(projection, createEvent(bookLost));

            final BookInventoryView state = projection.getState();
            assertEquals(LOST_ITEM_STATE, state.getItemState(0));
        }
    }

    @Nested
    @DisplayName("LoanBecameOverdue event should be interpreted by BookInventoryViewProjection and")
    class LoanBecameOverdueEvent {

        @Test
        @DisplayName("change inventory item state")
        void changeState() {
            final InventoryAppended inventoryAppended = inventoryAppendedInstance();
            dispatch(projection, createEvent(inventoryAppended));

            final BookBorrowed bookBorrowed = bookBorrowedInstance();
            dispatch(projection, createEvent(bookBorrowed));

            final LoanBecameOverdue loanBecameOverdue = loanBecameOverdueInstance();
            dispatch(projection, createEvent(loanBecameOverdue));

            final BookInventoryView state = projection.getState();
            assertEquals(OVERDUE_ITEM_STATE, state.getItemState(0));
        }
    }

    @Nested
    @DisplayName("BookReturned event should be interpreted by BookInventoryViewProjection and")
    class BookReturnedEvent {

        @Test
        @DisplayName("change inventory item state to in library")
        void changeState() {
            final InventoryAppended inventoryAppended = inventoryAppendedInstance();
            dispatch(projection, createEvent(inventoryAppended));

            final BookBorrowed bookBorrowed = bookBorrowedInstance();
            dispatch(projection, createEvent(bookBorrowed));

            final BookReturned bookReturned = bookReturnedInstance();
            dispatch(projection, createEvent(bookReturned));

            final BookInventoryView state = projection.getState();
            assertEquals(IN_LIBRARY_ITEM_STATE, state.getItemState(0));
        }
    }

    @Nested
    @DisplayName("LoanPeriodExtended event should be interpreted by BookInventoryViewProjection and")
    class LoanPeriodExtendedEvent {

        @Test
        @DisplayName("change due date")
        void changeDueDate() {
            final InventoryAppended inventoryAppended = inventoryAppendedInstance();
            dispatch(projection, createEvent(inventoryAppended));

            final BookBorrowed bookBorrowed = bookBorrowedInstance();
            dispatch(projection, createEvent(bookBorrowed));

            final LoanPeriodExtended loanPeriodExtended = loanPeriodExtendedInstance();
            dispatch(projection, createEvent(loanPeriodExtended));

            final BookInventoryView state = projection.getState();
            assertEquals(DEFAULT_DATE2, state.getItemState(0)
                                             .getLoanDetails()
                                             .getWhenDue());
        }
    }
}

