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

import io.spine.server.entity.LifecycleFlags;
import javaclasses.exlibris.c.BookAdded;
import javaclasses.exlibris.c.BookBecameAvailable;
import javaclasses.exlibris.c.BookBorrowed;
import javaclasses.exlibris.c.BookRemoved;
import javaclasses.exlibris.c.InventoryDecreased;
import javaclasses.exlibris.q.BookStatus;
import javaclasses.exlibris.q.BookView;
import javaclasses.exlibris.q.ProjectionTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static io.spine.server.projection.ProjectionEventDispatcher.dispatch;
import static javaclasses.exlibris.testdata.BookEventFactory.bookAddedInstance;
import static javaclasses.exlibris.testdata.BookEventFactory.bookRemovedInstance;
import static javaclasses.exlibris.testdata.InventoryEventFactory.bookBecameAvailableInstance;
import static javaclasses.exlibris.testdata.InventoryEventFactory.bookBorrowedInstance;
import static javaclasses.exlibris.testdata.InventoryEventFactory.inventoryDecreasedInstance;
import static javaclasses.exlibris.testdata.TestValues.AUTHOR;
import static javaclasses.exlibris.testdata.TestValues.BOOK_CATEGORY;
import static javaclasses.exlibris.testdata.TestValues.BOOK_ID;
import static javaclasses.exlibris.testdata.TestValues.BOOK_SYNOPSIS;
import static javaclasses.exlibris.testdata.TestValues.BOOK_TITLE;
import static javaclasses.exlibris.testdata.TestValues.COVER_URL;
import static javaclasses.exlibris.testdata.TestValues.ISBN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BookViewProjectionTest extends ProjectionTest {

    private BookViewProjection projection;

    @BeforeEach
    void setUp() {
        projection = new BookViewProjection(BOOK_ID);
    }

    @Nested
    @DisplayName("BookAdded event should be interpreted by BookViewProjection and")
    class BookAddedEvent {

        @Test
        @DisplayName("add BookItem to the list of all books")
        void addBookItem() {
            final BookAdded bookAdded = bookAddedInstance();
            dispatch(projection, createEvent(bookAdded));

            final BookView book = projection.getState();
            assertEquals(BOOK_ID, book.getBookId());
            assertEquals(BOOK_TITLE, book.getTitle());
            assertEquals(AUTHOR, book.getAuthorList()
                                     .get(0));
            assertEquals(ISBN, book.getIsbn());
            assertEquals(COVER_URL, book.getCoverUrl());
            assertEquals(BOOK_CATEGORY, book.getCategories(0));
            assertEquals(BOOK_SYNOPSIS, book.getSynopsis());
            assertEquals(0, book.getAvailableCount());
            assertEquals(BookStatus.EXPECTED, book.getStatus());
        }
    }

    @Nested
    @DisplayName("BookRemoved event should be interpreted by BookViewProjection and")
    class BookRemovedEvent {

        @Test
        @DisplayName("clear all information about book")
        void removeBookItem() {
            final BookAdded bookAdded = bookAddedInstance();
            final BookRemoved bookRemoved = bookRemovedInstance();
            dispatch(projection, createEvent(bookAdded));
            dispatch(projection, createEvent(bookRemoved));

            final LifecycleFlags lifecycleFlags = projection.getLifecycleFlags();
            assertTrue(lifecycleFlags.getDeleted());
        }
    }

    @Nested
    @DisplayName("BookBecameAvailable event should be interpreted by BookViewProjection and")
    class BookBecameAvailableEvent {

        @Test
        @DisplayName("change the number of available books and change book status")
        void changeNumberOfAvailableBooks() {
            final BookAdded bookAdded = bookAddedInstance();
            final BookBecameAvailable bookBecameAvailable = bookBecameAvailableInstance();

            dispatch(projection, createEvent(bookAdded));
            dispatch(projection, createEvent(bookBecameAvailable));

            final BookView bookView = projection.getState();
            assertEquals(1, bookView.getAvailableCount());
            assertEquals(BookStatus.AVAILABLE, bookView.getStatus());
        }
    }

    @Nested
    @DisplayName("InventoryDecreased event should be interpreted by BookViewProjection and")
    class InventoryDecreasedEvent {

        @Test
        @DisplayName("change the number of available books and change book status")
        void decreaseNumberOfAvailableBooks() {
            final BookAdded bookAdded = bookAddedInstance();
            final BookBecameAvailable bookBecameAvailable = bookBecameAvailableInstance();
            final InventoryDecreased inventoryDecreased = inventoryDecreasedInstance();

            dispatch(projection, createEvent(bookAdded));
            dispatch(projection, createEvent(bookBecameAvailable));
            dispatch(projection, createEvent(inventoryDecreased));

            final BookView bookView = projection.getState();
            assertEquals(0, bookView.getAvailableCount());
            assertEquals(BookStatus.EXPECTED, bookView.getStatus());
        }
    }

    @Nested
    @DisplayName("BookBorrowed event should be interpreted by BookViewProjection and")
    class BookBorrowedEvent {

        @Test
        @DisplayName("change the number of available books and change book status")
        void decreaseNumberOfAvailableBooks() {
            final BookAdded bookAdded = bookAddedInstance();
            final BookBecameAvailable bookBecameAvailable = bookBecameAvailableInstance();
            final BookBorrowed bookBorrowed = bookBorrowedInstance();

            dispatch(projection, createEvent(bookAdded));
            dispatch(projection, createEvent(bookBecameAvailable));
            dispatch(projection, createEvent(bookBorrowed));

            final BookView bookView = projection.getState();
            assertEquals(0, bookView.getAvailableCount());
            assertEquals(BookStatus.EXPECTED, bookView.getStatus());
        }
    }
}
