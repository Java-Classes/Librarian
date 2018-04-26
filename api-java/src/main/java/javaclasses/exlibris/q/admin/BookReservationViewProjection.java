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
import javaclasses.exlibris.BookId;
import javaclasses.exlibris.BookReservationViewId;
import javaclasses.exlibris.c.ReservationAdded;
import javaclasses.exlibris.c.ReservationCanceled;
import javaclasses.exlibris.c.ReservationPickUpPeriodExpired;
import javaclasses.exlibris.q.BookReservationView;
import javaclasses.exlibris.q.BookReservationViewVBuilder;

/**
 * The projection state of a one book reservation.
 *
 * @author Yurii Haidamaka
 */
public class BookReservationViewProjection extends Projection<BookReservationViewId, BookReservationView, BookReservationViewVBuilder> {

    /**
     * @see Projection#Projection(Object)
     */
    public BookReservationViewProjection(BookReservationViewId id) {
        super(id);
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
        final Timestamp whenReserved = event.getWhenCreated();
        getBuilder().setBookId(bookId)
                    .setUserName(userName)
                    .setEmail(email)
                    .setWhenReserved(whenReserved);
    }

    @Subscribe
    public void on(ReservationCanceled event) {
        getBuilder().clearBookId()
                    .clearUserName()
                    .clearEmail()
                    .clearWhenReserved();
    }

    @Subscribe
    public void on(ReservationPickUpPeriodExpired event) {
        getBuilder().clearBookId()
                    .clearUserName()
                    .clearEmail()
                    .clearWhenReserved();
    }
}
