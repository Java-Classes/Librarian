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

import io.spine.server.projection.ProjectionRepository;
import io.spine.server.route.EventRouting;
import javaclasses.exlibris.BookReservationViewId;
import javaclasses.exlibris.c.ReservationAdded;
import javaclasses.exlibris.c.ReservationCanceled;
import javaclasses.exlibris.c.ReservationPickUpPeriodExpired;
import javaclasses.exlibris.q.BookReservationView;

import static java.util.Collections.singleton;

/**
 * Repository for the {@link BookReservationViewProjection}.
 *
 * @author Yurii Haidamaka
 */
public class BookReservationViewRepository extends ProjectionRepository<BookReservationViewId, BookReservationViewProjection, BookReservationView> {
    @Override
    public void onRegistered() {
        super.onRegistered();
        setUpEventRoute();
    }

    /**
     * Adds the {@link io.spine.server.route.EventRoute EventRoute}s to the repository.
     */
    protected void setUpEventRoute() {
        final EventRouting<BookReservationViewId> routing = getEventRouting();
        routing.route(ReservationCanceled.class,
                      (message, context) -> {
                          final BookReservationViewId id =
                                  BookReservationViewId.newBuilder()
                                                       .setBookId(
                                                               message.getInventoryId()
                                                                      .getBookId())
                                                       .setUserId(
                                                               message.getWhoCanceled())
                                                       .build();
                          return singleton(id);
                      });
        routing.route(ReservationAdded.class,
                      (message, context) -> {
                          final BookReservationViewId id =
                                  BookReservationViewId.newBuilder()
                                                       .setBookId(
                                                               message.getInventoryId()
                                                                      .getBookId())
                                                       .setUserId(
                                                               message.getForWhomReserved())
                                                       .build();
                          return singleton(id);
                      });
        routing.route(ReservationPickUpPeriodExpired.class,
                      (message, context) -> {
                          final BookReservationViewId id =
                                  BookReservationViewId.newBuilder()
                                                       .setBookId(
                                                               message.getInventoryId()
                                                                      .getBookId())
                                                       .setUserId(
                                                               message.getUserId())
                                                       .build();
                          return singleton(id);
                      });
    }
}
