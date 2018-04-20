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

import javaclasses.exlibris.ListViewId;
import javaclasses.exlibris.c.BookAdded;
import javaclasses.exlibris.c.BookBorrowed;
import javaclasses.exlibris.c.BookRemoved;
import javaclasses.exlibris.c.BookReturned;
import javaclasses.exlibris.c.InventoryAppended;
import javaclasses.exlibris.c.InventoryDecreased;
import javaclasses.exlibris.q.BookItem;
import javaclasses.exlibris.q.BookItemStatus;
import javaclasses.exlibris.q.ProjectionTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.spine.server.projection.ProjectionEventDispatcher.dispatch;
import static javaclasses.exlibris.testdata.BookEventFactory.AUTHOR;
import static javaclasses.exlibris.testdata.BookEventFactory.BOOK_ID;
import static javaclasses.exlibris.testdata.BookEventFactory.CATEGORY;
import static javaclasses.exlibris.testdata.BookEventFactory.COVER_URL;
import static javaclasses.exlibris.testdata.BookEventFactory.ISBN;
import static javaclasses.exlibris.testdata.BookEventFactory.SYNOPSIS;
import static javaclasses.exlibris.testdata.BookEventFactory.TITLE;
import static javaclasses.exlibris.testdata.BookEventFactory.bookAddedInstance;
import static javaclasses.exlibris.testdata.BookEventFactory.bookRemovedInstance;
import static javaclasses.exlibris.testdata.InventoryEventFactory.bookBorrowedInstance;
import static javaclasses.exlibris.testdata.InventoryEventFactory.bookReturnedInstance;
import static javaclasses.exlibris.testdata.InventoryEventFactory.inventoryAppendedInstance;
import static javaclasses.exlibris.testdata.InventoryEventFactory.inventoryDecreasedInstance;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AllBooksListViewProjectionTest extends ProjectionTest {

    private AllBooksListViewProjection projection;

    @BeforeEach
    void setUp() {
        final ListViewId bookListId = createBookListId();
        projection = new AllBooksListViewProjection(bookListId);
    }

    @Nested
    @DisplayName("BookAdded event should be interpreted by AllBooksListViewProjection and")
    class BookAddedEvent {

        @Test
        @DisplayName("add BookItem to the list of all books")
        void addBookItem() {
            final BookAdded bookAdded = bookAddedInstance();
            dispatch(projection, createEvent(bookAdded));

            final List<BookItem> books = projection.getState()
                                                   .getBookItemList();
            assertEquals(1, books.size());
            final BookItem bookItem = books.get(0);
            assertEquals(BOOK_ID, bookItem.getBookId());
            assertEquals(TITLE, bookItem.getTitle());
            assertEquals(AUTHOR, bookItem.getAuthors());
            assertEquals(ISBN, bookItem.getIsbn());
            assertEquals(COVER_URL, bookItem.getCoverUrl());
            assertEquals(CATEGORY, bookItem.getCategories(0));
            assertEquals(SYNOPSIS, bookItem.getSynopsis());
            assertEquals(0, bookItem.getAvailableCount());
            Assertions.assertEquals(BookItemStatus.EXPECTED, bookItem.getStatus());
        }
    }

    @Nested
    @DisplayName("BookRemoved event should be interpreted by AllBooksListViewProjection and")
    class BookRemovedEvent {

        @Test
        @DisplayName("remove BookItem from the list of all books")
        void removeBookItem() {
            final BookAdded bookAdded = bookAddedInstance();
            final BookRemoved bookRemoved = bookRemovedInstance();
            dispatch(projection, createEvent(bookAdded));
            dispatch(projection, createEvent(bookRemoved));

            final List<BookItem> books = projection.getState()
                                                   .getBookItemList();
            assertEquals(0, books.size());
        }
    }

    @Nested
    @DisplayName("InventoryAppended event should be interpreted by AllBooksListViewProjection and")
    class InventoryAppendedEvent {

        @Test
        @DisplayName("increase the number of available books and change book status")
        void increaseNumberOfAvailableBooks() {
            final BookAdded bookAdded = bookAddedInstance();

            final InventoryAppended inventoryAppended = inventoryAppendedInstance();

            dispatch(projection, createEvent(bookAdded));
            dispatch(projection, createEvent(inventoryAppended));

            final List<BookItem> books = projection.getState()
                                                   .getBookItemList();
            assertEquals(1, books.size());
            final BookItem bookItem = books.get(0);
            assertEquals(1, bookItem.getAvailableCount());
            assertEquals(BookItemStatus.AVAILABLE, bookItem.getStatus());
        }
    }

    @Nested
    @DisplayName("InventoryDecreased event should be interpreted by AllBooksListViewProjection and")
    class InventoryDecreasedEvent {

        @Test
        @DisplayName("decrease the number of available books and change book status")
        void decreaseNumberOfAvailableBooks() {
            final BookAdded bookAdded = bookAddedInstance();
            final InventoryAppended inventoryAppended = inventoryAppendedInstance();
            final InventoryDecreased inventoryDecreased = inventoryDecreasedInstance();

            dispatch(projection, createEvent(bookAdded));
            dispatch(projection, createEvent(inventoryAppended));
            dispatch(projection, createEvent(inventoryDecreased));

            final List<BookItem> books = projection.getState()
                                                   .getBookItemList();
            assertEquals(1, books.size());
            final BookItem bookItem = books.get(0);
            assertEquals(0, bookItem.getAvailableCount());
            assertEquals(BookItemStatus.EXPECTED, bookItem.getStatus());
        }
    }

    @Nested
    @DisplayName("BookBorrowed event should be interpreted by AllBooksListViewProjection and")
    class BookBorrowedEvent {

        @Test
        @DisplayName("decrease the number of available books and change book status")
        void decreaseNumberOfAvailableBooks() {
            final BookAdded bookAdded = bookAddedInstance();
            final InventoryAppended inventoryAppended = inventoryAppendedInstance();
            final BookBorrowed bookBorrowed = bookBorrowedInstance();

            dispatch(projection, createEvent(bookAdded));
            dispatch(projection, createEvent(inventoryAppended));
            dispatch(projection, createEvent(bookBorrowed));

            final List<BookItem> books = projection.getState()
                                                   .getBookItemList();
            assertEquals(1, books.size());
            final BookItem bookItem = books.get(0);
            assertEquals(0, bookItem.getAvailableCount());
            assertEquals(BookItemStatus.EXPECTED, bookItem.getStatus());
        }
    }

    @Nested
    @DisplayName("BookReturned event should be interpreted by AllBooksListViewProjection and")
    class BookReturnedEvent {

        @Test
        @DisplayName("increase the number of available books and change book status")
        void increaseNumberOfAvailableBooks() {
            final BookAdded bookAdded = bookAddedInstance();
            final InventoryAppended inventoryAppended = inventoryAppendedInstance();
            final BookBorrowed bookBorrowed = bookBorrowedInstance();
            final BookReturned bookReturned = bookReturnedInstance();

            dispatch(projection, createEvent(bookAdded));
            dispatch(projection, createEvent(inventoryAppended));
            dispatch(projection, createEvent(bookBorrowed));
            dispatch(projection, createEvent(bookReturned));

            final List<BookItem> books = projection.getState()
                                                   .getBookItemList();
            assertEquals(1, books.size());
            final BookItem bookItem = books.get(0);
            assertEquals(1, bookItem.getAvailableCount());
            assertEquals(BookItemStatus.AVAILABLE, bookItem.getStatus());
        }
    }

}
