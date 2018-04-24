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

import io.spine.server.projection.ProjectionRepository;
import javaclasses.exlibris.UserId;
import javaclasses.exlibris.c.BookReadyToPickup;
import javaclasses.exlibris.c.ReservationAdded;
import javaclasses.exlibris.c.ReservationBecameLoan;
import javaclasses.exlibris.c.ReservationCanceled;
import javaclasses.exlibris.q.ReservedBooksListView;

import java.util.Collections;

/**
 * Repository for the {@link ReservedBooksListViewProjection}.
 *
 * @author Yurii Haidamaka
 */
public class ReservedBooksListViewRepository extends ProjectionRepository<UserId, ReservedBooksListViewProjection, ReservedBooksListView> {
    @Override
    public void onRegistered() {
        super.onRegistered();
        setUpEventRoute();
    }

    /**
     * Adds the {@link io.spine.server.route.EventRoute EventRoute}s to the repository.
     */
    protected void setUpEventRoute() {
        getEventRouting().replaceDefault(((message, context) -> {
            if (message instanceof ReservationAdded) {
                final ReservationAdded event = (ReservationAdded) message;
                return Collections.singleton(event.getForWhomReserved());
            }
            if (message instanceof ReservationBecameLoan) {
                final ReservationBecameLoan event = (ReservationBecameLoan) message;
                return Collections.singleton(event.getUserId());
            }
            if (message instanceof ReservationCanceled) {
                final ReservationCanceled event = (ReservationCanceled) message;
                return Collections.singleton(event.getWhoCanceled());
            }
            if (message instanceof BookReadyToPickup) {
                final BookReadyToPickup event = (BookReadyToPickup) message;
                return Collections.singleton(event.getForWhom());
            }
            return null;
        }));
    }
}
