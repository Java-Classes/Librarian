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
import javaclasses.exlibris.c.BookReadyToPickup;
import javaclasses.exlibris.c.ReservationAdded;
import javaclasses.exlibris.c.ReservationBecameLoan;
import javaclasses.exlibris.c.ReservationCanceled;
import javaclasses.exlibris.q.ProjectionTest;
import javaclasses.exlibris.q.ReservedBookItem;
import javaclasses.exlibris.q.ReservedBookItemStatus;
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
import static javaclasses.exlibris.testdata.InventoryEventFactory.bookReadyToPickUpInstance;
import static javaclasses.exlibris.testdata.InventoryEventFactory.reservationAddedInstance;
import static javaclasses.exlibris.testdata.InventoryEventFactory.reservationBecameLoanInstance;
import static javaclasses.exlibris.testdata.InventoryEventFactory.reservationCanceledInstance;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ReservedBooksListViewProjectionTest extends ProjectionTest {

    private ReservedBooksListViewProjection projection;

    @BeforeEach
    void setUp() {
        final UserId userId = USER_ID;
        projection = new ReservedBooksListViewProjection(userId);
    }

    @Nested
    @DisplayName("ReservationAdded event should be interpreted by ReservedBooksListViewProjection and")
    class ReservationAddedEvent {

        @Test
        @DisplayName("add book with full information to the list of books")
        void addBook() {
            final ReservationAdded reservationAdded = reservationAddedInstance();
            dispatch(projection, createEvent(reservationAdded));

            final List<ReservedBookItem> books = projection.getState()
                                                           .getBookItemList();
            assertEquals(1, books.size());
            final ReservedBookItem bookItem = books.get(0);
            assertEquals(BOOK_ID, bookItem.getBookId());
            assertEquals(ISBN, bookItem.getIsbn());
            assertEquals(TITLE, bookItem.getTitle());
            assertEquals(AUTHOR, bookItem.getAuthors());
            assertEquals(COVER_URL, bookItem.getCoverUrl());
            assertEquals(CATEGORY, bookItem.getCategorieList()
                                           .get(0));
            assertEquals(SYNOPSIS, bookItem.getSynopsis());
            assertEquals(DEFAULT_DATE1, bookItem.getWhenReadyToPickUp());
            assertEquals(ReservedBookItemStatus.RESERVED, bookItem.getStatus());
        }
    }

    @Nested
    @DisplayName("ReservationBecameLoan event should be interpreted by ReservedBooksListViewProjection and")
    class ReservationBecameLoanEvent {

        @Test
        @DisplayName("delete book from the list of reserved books")
        void deleteBook() {
            final ReservationAdded reservationAdded = reservationAddedInstance();
            dispatch(projection, createEvent(reservationAdded));
            final ReservationBecameLoan reservationBecameLoan = reservationBecameLoanInstance();
            dispatch(projection, createEvent(reservationBecameLoan));
            final List<ReservedBookItem> books = projection.getState()
                                                           .getBookItemList();
            assertEquals(0, books.size());
        }
    }

    @Nested
    @DisplayName("ReservationCanceled event should be interpreted by ReservedBooksListViewProjection and")
    class ReservationCanceledEvent {

        @Test
        @DisplayName("delete book from the list of reserved books")
        void deleteBook() {
            final ReservationAdded reservationAdded = reservationAddedInstance();
            dispatch(projection, createEvent(reservationAdded));
            final ReservationCanceled reservationCanceled = reservationCanceledInstance();
            dispatch(projection, createEvent(reservationCanceled));
            final List<ReservedBookItem> books = projection.getState()
                                                           .getBookItemList();
            assertEquals(0, books.size());
        }
    }

    @Nested
    @DisplayName("BookReadyToPickUp event should be interpreted by ReservedBooksListViewProjection and")
    class BookReadyToPickUpEvent {

        @Test
        @DisplayName("change book status to READY_TO_PICK_UP")
        void changeBookStatus() {
            final ReservationAdded reservationAdded = reservationAddedInstance();
            dispatch(projection, createEvent(reservationAdded));
            final BookReadyToPickup bookReadyToPickup = bookReadyToPickUpInstance();
            dispatch(projection, createEvent(bookReadyToPickup));
            final List<ReservedBookItem> books = projection.getState()
                                                           .getBookItemList();
            assertEquals(1, books.size());
            final ReservedBookItem bookItem = books.get(0);
            assertEquals(ReservedBookItemStatus.READY_TO_PICK_UP, bookItem.getStatus());
        }
    }
}
