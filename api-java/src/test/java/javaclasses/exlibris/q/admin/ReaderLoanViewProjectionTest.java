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

import javaclasses.exlibris.ReaderLoanViewId;
import javaclasses.exlibris.c.BookBorrowed;
import javaclasses.exlibris.c.BookReturned;
import javaclasses.exlibris.c.LoanPeriodExtended;
import javaclasses.exlibris.q.ProjectionTest;
import javaclasses.exlibris.q.ReaderLoanView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static io.spine.server.projection.ProjectionEventDispatcher.dispatch;
import static javaclasses.exlibris.testdata.BookEventFactory.AUTHOR;
import static javaclasses.exlibris.testdata.BookEventFactory.TITLE;
import static javaclasses.exlibris.testdata.BookEventFactory.USER_ID;
import static javaclasses.exlibris.testdata.InventoryEventFactory.DEFAULT_DATE1;
import static javaclasses.exlibris.testdata.InventoryEventFactory.DEFAULT_DATE2;
import static javaclasses.exlibris.testdata.InventoryEventFactory.DEFAULT_DUE_DATE;
import static javaclasses.exlibris.testdata.InventoryEventFactory.INVENTORY_ITEM_ID;
import static javaclasses.exlibris.testdata.InventoryEventFactory.LOAN_ID;
import static javaclasses.exlibris.testdata.InventoryEventFactory.bookBorrowedInstance;
import static javaclasses.exlibris.testdata.InventoryEventFactory.bookReturnedInstance;
import static javaclasses.exlibris.testdata.InventoryEventFactory.loanPeriodExtendedInstance;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ReaderLoanViewProjectionTest extends ProjectionTest {

    private ReaderLoanViewProjection projection;

    @BeforeEach
    void setUp() {
        final ReaderLoanViewId id = ReaderLoanViewId.newBuilder()
                                                    .setUserId(USER_ID)
                                                    .setLoanId(LOAN_ID)
                                                    .build();
        projection = new ReaderLoanViewProjection(id);
    }

    @Nested
    @DisplayName("BookBorrowed event should be interpreted by" +
            " ReaderLoanViewProjection and")
    class BookBorrowedEvent {

        @Test
        @DisplayName("add information about start of loan period")
        void addInformation() {
            final BookBorrowed bookBorrowed = bookBorrowedInstance();
            dispatch(projection, createEvent(bookBorrowed));

            final ReaderLoanView state = projection.getState();
            assertEquals(TITLE, state.getTitle());
            assertEquals(AUTHOR, state.getAuthors());
            assertEquals(INVENTORY_ITEM_ID, state.getItemId());
            assertEquals(DEFAULT_DATE1, state.getWhenTaken());
        }
    }

    @Nested
    @DisplayName("BookReturned event should be interpreted by" +
            " ReaderLoanViewProjection and")
    class BookReturnedEvent {

        @Test
        @DisplayName("add due Date")
        void addDueDate() {
            final BookBorrowed bookBorrowed = bookBorrowedInstance();
            dispatch(projection, createEvent(bookBorrowed));

            final BookReturned bookReturned = bookReturnedInstance();
            dispatch(projection, createEvent(bookReturned));

            final ReaderLoanView state = projection.getState();
            assertEquals(TITLE, state.getTitle());
            assertEquals(AUTHOR, state.getAuthors());
            assertEquals(INVENTORY_ITEM_ID, state.getItemId());
            assertEquals(DEFAULT_DATE1, state.getWhenTaken());
            assertEquals(DEFAULT_DUE_DATE, state.getWhenDue());
        }
    }

    @Nested
    @DisplayName("LoanPeriodExtended event should be interpreted by" +
            " ReaderLoanViewProjection and")
    class LoanPeriodExtendedEvent {

        @Test
        @DisplayName("change due Date")
        void changeDueDate() {
            final BookBorrowed bookBorrowed = bookBorrowedInstance();
            dispatch(projection, createEvent(bookBorrowed));

            final LoanPeriodExtended loanPeriodExtended = loanPeriodExtendedInstance();
            dispatch(projection, createEvent(loanPeriodExtended));

            final ReaderLoanView state = projection.getState();
            assertEquals(TITLE, state.getTitle());
            assertEquals(AUTHOR, state.getAuthors());
            assertEquals(INVENTORY_ITEM_ID, state.getItemId());
            assertEquals(DEFAULT_DATE1, state.getWhenTaken());
            assertEquals(DEFAULT_DATE2, state.getWhenDue());
        }
    }
}
