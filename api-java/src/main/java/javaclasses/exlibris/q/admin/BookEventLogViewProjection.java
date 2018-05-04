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

import com.google.protobuf.Timestamp;
import io.spine.core.Subscribe;
import io.spine.net.EmailAddress;
import io.spine.people.PersonName;
import io.spine.server.projection.Projection;
import javaclasses.exlibris.BookEventLogViewId;
import javaclasses.exlibris.BookId;
import javaclasses.exlibris.InventoryItemId;
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
import javaclasses.exlibris.q.BookEventLogViewVBuilder;

/**
 * The projection state of a one action with book.
 *
 * @author Yurii Haidamaka
 */
public class BookEventLogViewProjection extends Projection<BookEventLogViewId, BookEventLogView, BookEventLogViewVBuilder> {

    /**
     * @see Projection#Projection(Object)
     */
    public BookEventLogViewProjection(BookEventLogViewId id) {
        super(id);
    }

    @Subscribe
    public void on(BookBorrowed event) {
        final BookId bookId = event.getInventoryId()
                                   .getBookId();
        final InventoryItemId itemId = event.getInventoryItemId();
        // TODO: 4/26/2018 yurii.haidamaka SET USERNAME FROM GOOGLE BY EMAIL
        final PersonName userName = PersonName.newBuilder()
                                              .setGivenName("Ivan")
                                              .setFamilyName("Petrov")
                                              .build();
        final EmailAddress email = event.getWhoBorrowed()
                                        .getEmail();
        final String eventType = event.getClass()
                                      .getSimpleName();
        final Timestamp whenEmitted = event.getWhenBorrowed();

        getBuilder().setBookId(bookId)
                    .setItemId(itemId)
                    .setUserName(userName)
                    .setEmail(email)
                    .setEventType(eventType)
                    .setWhenEmitted(whenEmitted);
    }

    @Subscribe
    public void on(BookReturned event) {
        final BookId bookId = event.getInventoryId()
                                   .getBookId();
        final InventoryItemId itemId = event.getInventoryItemId();
        // TODO: 4/26/2018 yurii.haidamaka SET USERNAME FROM GOOGLE BY EMAIL
        final PersonName userName = PersonName.newBuilder()
                                              .setGivenName("Ivan")
                                              .setFamilyName("Petrov")
                                              .build();
        final EmailAddress email = event.getWhoReturned()
                                        .getEmail();
        final String eventType = event.getClass()
                                      .getSimpleName();
        final Timestamp whenEmitted = event.getWhenReturned();

        getBuilder().setBookId(bookId)
                    .setItemId(itemId)
                    .setUserName(userName)
                    .setEmail(email)
                    .setEventType(eventType)
                    .setWhenEmitted(whenEmitted);
    }

    @Subscribe
    public void on(ReservationAdded event) {
        final BookId bookId = event.getInventoryId()
                                   .getBookId();
        // TODO: 4/26/2018 yurii.haidamaka SET USERNAME FROM GOOGLE BY EMAIL
        final PersonName userName = PersonName.newBuilder()
                                              .setGivenName("Ivan")
                                              .setFamilyName("Petrov")
                                              .build();
        final EmailAddress email = event.getForWhomReserved()
                                        .getEmail();
        final String eventType = event.getClass()
                                      .getSimpleName();
        final Timestamp whenEmitted = event.getWhenCreated();
        getBuilder().setBookId(bookId)
                    .setUserName(userName)
                    .setEmail(email)
                    .setEventType(eventType)
                    .setWhenEmitted(whenEmitted);
    }

    @Subscribe
    public void on(ReservationCanceled event) {
        final BookId bookId = event.getInventoryId()
                                   .getBookId();
        // TODO: 4/26/2018 yurii.haidamaka SET USERNAME FROM GOOGLE BY EMAIL
        final PersonName userName = PersonName.newBuilder()
                                              .setGivenName("Ivan")
                                              .setFamilyName("Petrov")
                                              .build();
        final EmailAddress email = event.getWhoCanceled()
                                        .getEmail();
        final String eventType = event.getClass()
                                      .getSimpleName();
        final Timestamp whenEmitted = event.getWhenCanceled();

        getBuilder().setBookId(bookId)
                    .setUserName(userName)
                    .setEmail(email)
                    .setEventType(eventType)
                    .setWhenEmitted(whenEmitted);
    }

    @Subscribe
    public void on(ReservationPickUpPeriodExpired event) {
        final BookId bookId = event.getInventoryId()
                                   .getBookId();
        // TODO: 4/26/2018 yurii.haidamaka SET USERNAME FROM GOOGLE BY EMAIL
        final PersonName userName = PersonName.newBuilder()
                                              .setGivenName("Ivan")
                                              .setFamilyName("Petrov")
                                              .build();
        final EmailAddress email = event.getUserId()
                                        .getEmail();
        final String eventType = event.getClass()
                                      .getSimpleName();
        final Timestamp whenEmitted = event.getWhenExpired();

        getBuilder().setBookId(bookId)
                    .setUserName(userName)
                    .setEmail(email)
                    .setEventType(eventType)
                    .setWhenEmitted(whenEmitted);
    }

    @Subscribe
    public void on(ReservationBecameLoan event) {
        final BookId bookId = event.getInventoryId()
                                   .getBookId();
        // TODO: 4/26/2018 yurii.haidamaka SET USERNAME FROM GOOGLE BY EMAIL
        final PersonName userName = PersonName.newBuilder()
                                              .setGivenName("Ivan")
                                              .setFamilyName("Petrov")
                                              .build();
        final EmailAddress email = event.getUserId()
                                        .getEmail();
        final String eventType = event.getClass()
                                      .getSimpleName();
        final Timestamp whenEmitted = event.getWhenBecameLoan();

        getBuilder().setBookId(bookId)
                    .setUserName(userName)
                    .setEmail(email)
                    .setEventType(eventType)
                    .setWhenEmitted(whenEmitted);
    }

    @Subscribe
    public void on(LoanBecameOverdue event) {
        final BookId bookId = event.getInventoryId()
                                   .getBookId();
        final InventoryItemId itemId = event.getInventoryItemId();
        // TODO: 4/26/2018 yurii.haidamaka SET USERNAME FROM GOOGLE BY EMAIL
        final PersonName userName = PersonName.newBuilder()
                                              .setGivenName("Ivan")
                                              .setFamilyName("Petrov")
                                              .build();
        final EmailAddress email = event.getUserId()
                                        .getEmail();
        final String eventType = event.getClass()
                                      .getSimpleName();
        final Timestamp whenEmitted = event.getWhenBecameOverdue();
        getBuilder().setBookId(bookId)
                    .setItemId(itemId)
                    .setUserName(userName)
                    .setEmail(email)
                    .setEventType(eventType)
                    .setWhenEmitted(whenEmitted);
    }

    @Subscribe
    public void on(LoanBecameShouldReturnSoon event) {
        final BookId bookId = event.getInventoryId()
                                   .getBookId();
        final InventoryItemId itemId = event.getInventoryItemId();
        // TODO: 4/26/2018 yurii.haidamaka SET USERNAME FROM GOOGLE BY EMAIL
        final PersonName userName = PersonName.newBuilder()
                                              .setGivenName("Ivan")
                                              .setFamilyName("Petrov")
                                              .build();
        final EmailAddress email = event.getUserId()
                                        .getEmail();
        final String eventType = event.getClass()
                                      .getSimpleName();
        final Timestamp whenEmitted = event.getWhenBecameShouldReturnSoon();

        getBuilder().setBookId(bookId)
                    .setItemId(itemId)
                    .setUserName(userName)
                    .setEmail(email)
                    .setEventType(eventType)
                    .setWhenEmitted(whenEmitted);
    }

    @Subscribe
    public void on(LoanPeriodExtended event) {
        final BookId bookId = event.getInventoryId()
                                   .getBookId();
        final InventoryItemId itemId = event.getInventoryItemId();
        // TODO: 4/26/2018 yurii.haidamaka SET USERNAME FROM GOOGLE BY EMAIL
        final PersonName userName = PersonName.newBuilder()
                                              .setGivenName("Ivan")
                                              .setFamilyName("Petrov")
                                              .build();
        final EmailAddress email = event.getUserId()
                                        .getEmail();
        final String eventType = event.getClass()
                                      .getSimpleName();
        final Timestamp whenEmitted = event.getWhenExtended();
        getBuilder().setBookId(bookId)
                    .setItemId(itemId)
                    .setUserName(userName)
                    .setEmail(email)
                    .setEventType(eventType)
                    .setWhenEmitted(whenEmitted);
    }

    @Subscribe
    public void on(BookLost event) {
        final BookId bookId = event.getInventoryId()
                                   .getBookId();
        final InventoryItemId itemId = event.getInventoryItemId();
        // TODO: 4/26/2018 yurii.haidamaka SET USERNAME FROM GOOGLE BY EMAIL
        final PersonName userName = PersonName.newBuilder()
                                              .setGivenName("Ivan")
                                              .setFamilyName("Petrov")
                                              .build();
        final EmailAddress email = event.getWhoLost()
                                        .getEmail();
        final String eventType = event.getClass()
                                      .getSimpleName();
        final Timestamp whenEmitted = event.getWhenReported();

        getBuilder().setBookId(bookId)
                    .setItemId(itemId)
                    .setUserName(userName)
                    .setEmail(email)
                    .setEventType(eventType)
                    .setWhenEmitted(whenEmitted);
    }

    @Subscribe
    public void on(BookReadyToPickup event) {
        final BookId bookId = event.getInventoryId()
                                   .getBookId();
        // TODO: 4/26/2018 yurii.haidamaka SET USERNAME FROM GOOGLE BY EMAIL
        final PersonName userName = PersonName.newBuilder()
                                              .setGivenName("Ivan")
                                              .setFamilyName("Petrov")
                                              .build();
        final EmailAddress email = event.getForWhom()
                                        .getEmail();
        final String eventType = event.getClass()
                                      .getSimpleName();
        final Timestamp whenEmitted = event.getWhenBecameReadyToPickup();

        getBuilder().setBookId(bookId)
                    .setUserName(userName)
                    .setEmail(email)
                    .setEventType(eventType)
                    .setWhenEmitted(whenEmitted);
    }

}
