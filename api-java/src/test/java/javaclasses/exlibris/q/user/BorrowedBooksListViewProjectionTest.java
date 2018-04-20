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

package javaclasses.exlibris.q.user;

import javaclasses.exlibris.UserId;
import javaclasses.exlibris.c.BookBorrowed;
import javaclasses.exlibris.c.BookReturned;
import javaclasses.exlibris.c.LoanBecameOverdue;
import javaclasses.exlibris.c.LoanPeriodExtended;
import javaclasses.exlibris.q.BorrowedBookItem;
import javaclasses.exlibris.q.BorrowedBookItemStatus;
import javaclasses.exlibris.q.ProjectionTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.spine.server.projection.ProjectionEventDispatcher.dispatch;
import static javaclasses.exlibris.testdata.BookEventFactory.AUTHOR;
import static javaclasses.exlibris.testdata.BookEventFactory.CATEGORY;
import static javaclasses.exlibris.testdata.BookEventFactory.COVER_URL;
import static javaclasses.exlibris.testdata.BookEventFactory.ISBN;
import static javaclasses.exlibris.testdata.BookEventFactory.SYNOPSIS;
import static javaclasses.exlibris.testdata.BookEventFactory.TITLE;
import static javaclasses.exlibris.testdata.BookEventFactory.USER_ID;
import static javaclasses.exlibris.testdata.InventoryEventFactory.BOOK_ID;
import static javaclasses.exlibris.testdata.InventoryEventFactory.DEFAULT_DATE1;
import static javaclasses.exlibris.testdata.InventoryEventFactory.DEFAULT_DATE2;
import static javaclasses.exlibris.testdata.InventoryEventFactory.DEFAULT_DUE_DATE;
import static javaclasses.exlibris.testdata.InventoryEventFactory.LOAN_ID;
import static javaclasses.exlibris.testdata.InventoryEventFactory.bookBorrowedInstance;
import static javaclasses.exlibris.testdata.InventoryEventFactory.bookReturnedInstance;
import static javaclasses.exlibris.testdata.InventoryEventFactory.loanBecameOverdueInstance;
import static javaclasses.exlibris.testdata.InventoryEventFactory.loanPeriodExtendedInstance;
import static org.junit.jupiter.api.Assertions.assertEquals;

class BorrowedBooksListViewProjectionTest extends ProjectionTest {

    private BorrowedBooksListViewProjection projection;

    @BeforeEach
    void setUp() {
        final UserId userId = USER_ID;
        projection = new BorrowedBooksListViewProjection(userId);
    }

    @Nested
    @DisplayName("BookBorrowed event should be interpreted by BorrowedBooksListViewProjection and")
    class BookBorrowedEvent {

        @Test
        @DisplayName("add book with full information to the list of books")
        void addBook() {
            final BookBorrowed bookBorrowed = bookBorrowedInstance();
            dispatch(projection, createEvent(bookBorrowed));

            final List<BorrowedBookItem> books = projection.getState()
                                                           .getBookItemList();
            assertEquals(1, books.size());
            final BorrowedBookItem bookItem = books.get(0);
            assertEquals(LOAN_ID, bookItem.getLoanId());
            assertEquals(BOOK_ID, bookItem.getBookId());
            assertEquals(ISBN, bookItem.getIsbn());
            assertEquals(TITLE, bookItem.getTitle());
            assertEquals(AUTHOR, bookItem.getAuthors());
            assertEquals(COVER_URL, bookItem.getCoverUrl());
            assertEquals(CATEGORY, bookItem.getCategoriesList()
                                           .get(0));
            assertEquals(SYNOPSIS, bookItem.getSynopsis());
            assertEquals(false, bookItem.getIsAllowedLoanExtension());
            assertEquals(BorrowedBookItemStatus.BORROWED, bookItem.getStatus());
            assertEquals(DEFAULT_DATE1, bookItem.getWhenBorrowed());
            assertEquals(DEFAULT_DUE_DATE, bookItem.getDueDate());
            assertEquals(BorrowedBookItemStatus.BORROWED, bookItem.getStatus());

        }

    }

    @Nested
    @DisplayName("LoanBecameOverdue event should be interpreted by BorrowedBooksListViewProjection and")
    class LoanBecameOverdueEvent {

        @Test
        @DisplayName("change status to OVERDUE and set new due date")
        void changeStatus() {
            final BookBorrowed bookBorrowed = bookBorrowedInstance();
            dispatch(projection, createEvent(bookBorrowed));
            final LoanBecameOverdue loanBecameOverdue = loanBecameOverdueInstance();
            dispatch(projection, createEvent(loanBecameOverdue));
            final List<BorrowedBookItem> books = projection.getState()
                                                           .getBookItemList();
            assertEquals(1, books.size());
            final BorrowedBookItem bookItem = books.get(0);
            assertEquals(BorrowedBookItemStatus.OVERDUE, bookItem.getStatus());
        }

    }

    @Nested
    @DisplayName("LoanPeriodExtended event should be interpreted by BorrowedBooksListViewProjection and")
    class LoanPeriodExtendedEvent {

        @Test
        @DisplayName("change status to BORROWED, set new dueDate and set false to IsAllowedLoanExtension")
        void changeStatus(){
            final BookBorrowed bookBorrowed = bookBorrowedInstance();
            dispatch(projection, createEvent(bookBorrowed));
            final LoanPeriodExtended loanPeriodExtended = loanPeriodExtendedInstance();
            dispatch(projection, createEvent(loanPeriodExtended));
            final List<BorrowedBookItem> books = projection.getState()
                                                           .getBookItemList();
            assertEquals(1, books.size());
            final BorrowedBookItem bookItem = books.get(0);
            assertEquals(BorrowedBookItemStatus.BORROWED, bookItem.getStatus());
            assertEquals(DEFAULT_DATE2, bookItem.getDueDate());
        }
    }

    @Nested
    @DisplayName("BookReturned event should be interpreted by BorrowedBooksListViewProjection and")
    class BookReturnedEvent {

        @Test
        @DisplayName("delete book from the list of borrowed books")
        void deleteBook(){
            final BookBorrowed bookBorrowed = bookBorrowedInstance();
            dispatch(projection, createEvent(bookBorrowed));
            final BookReturned bookReturned = bookReturnedInstance();
            dispatch(projection, createEvent(bookReturned));
            final List<BorrowedBookItem> books = projection.getState()
                                                           .getBookItemList();
            assertEquals(0, books.size());
        }
    }
}
