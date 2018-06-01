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
import javaclasses.exlibris.c.BookBorrowed;
import javaclasses.exlibris.c.BookLost;
import javaclasses.exlibris.c.BookReturned;
import javaclasses.exlibris.c.InventoryAppended;
import javaclasses.exlibris.c.InventoryDecreased;
import javaclasses.exlibris.c.InventoryRemoved;
import javaclasses.exlibris.c.LoanBecameOverdue;
import javaclasses.exlibris.c.LoanPeriodExtended;
import javaclasses.exlibris.q.BookInventoryView;
import javaclasses.exlibris.q.ProjectionTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static io.spine.server.projection.ProjectionEventDispatcher.dispatch;
import static javaclasses.exlibris.testdata.InventoryEventFactory.bookBorrowedInstance;
import static javaclasses.exlibris.testdata.InventoryEventFactory.bookLostInstance;
import static javaclasses.exlibris.testdata.InventoryEventFactory.bookReturnedInstance;
import static javaclasses.exlibris.testdata.InventoryEventFactory.inventoryAppendedInstance;
import static javaclasses.exlibris.testdata.InventoryEventFactory.inventoryDecreasedInstance;
import static javaclasses.exlibris.testdata.InventoryEventFactory.inventoryRemovedInstance;
import static javaclasses.exlibris.testdata.InventoryEventFactory.loanBecameOverdueInstance;
import static javaclasses.exlibris.testdata.InventoryEventFactory.loanPeriodExtendedInstance;
import static javaclasses.exlibris.testdata.TestValues.AUTHOR;
import static javaclasses.exlibris.testdata.TestValues.BOOK_TITLE;
import static javaclasses.exlibris.testdata.TestValues.BORROWED_ITEM_STATE;
import static javaclasses.exlibris.testdata.TestValues.DEFAULT_DATE2;
import static javaclasses.exlibris.testdata.TestValues.INVENTORY_ID;
import static javaclasses.exlibris.testdata.TestValues.IN_LIBRARY_ITEM_STATE;
import static javaclasses.exlibris.testdata.TestValues.LOST_ITEM_STATE;
import static javaclasses.exlibris.testdata.TestValues.OVERDUE_ITEM_STATE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
            assertEquals(BOOK_TITLE, state.getTitle());
            assertEquals(AUTHOR, state.getAuthorList()
                                      .get(0));
            assertEquals(IN_LIBRARY_ITEM_STATE, state.getItemState(0));
        }
    }

    @Nested
    @DisplayName("InventoryRemoved event should be interpreted by BookInventoryViewProjection and")
    class InventoryRemovedEvent {

        @Test
        @DisplayName("clear this projection")
        void clearProjection() {
            final InventoryAppended inventoryAppended = inventoryAppendedInstance();
            dispatch(projection, createEvent(inventoryAppended));

            final InventoryRemoved inventoryRemoved =
                    inventoryRemovedInstance();
            dispatch(projection, createEvent(inventoryRemoved));

            final LifecycleFlags lifecycleFlags = projection.getLifecycleFlags();
            assertTrue(lifecycleFlags.getDeleted());
        }
    }

    @Nested
    @DisplayName("InventoryDecreased event should be interpreted by BookInventoryViewProjection and")
    class InventoryDecreasedEvent {

        @Test
        @DisplayName("remove this Inventory item state")
        void removeItemState() {
            final InventoryAppended inventoryAppended = inventoryAppendedInstance();
            dispatch(projection, createEvent(inventoryAppended));

            final InventoryDecreased inventoryDecreased =
                    inventoryDecreasedInstance();
            dispatch(projection, createEvent(inventoryDecreased));

            final BookInventoryView state = projection.getState();
            assertEquals(0, state.getItemStateList()
                                 .size());
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

