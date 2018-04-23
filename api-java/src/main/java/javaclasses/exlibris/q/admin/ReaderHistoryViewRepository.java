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
import javaclasses.exlibris.UserId;
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
import javaclasses.exlibris.q.ReaderHistoryView;

import java.util.Collections;

/**
 * Repository for the {@link ReaderHistoryViewProjection}.
 *
 * @author Yegor Udovchenko
 */
public class ReaderHistoryViewRepository extends ProjectionRepository<UserId, ReaderHistoryViewProjection, ReaderHistoryView> {
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
            if (message instanceof BookBorrowed) {
                BookBorrowed event = (BookBorrowed) message;
                return Collections.singleton(event.getWhoBorrowed());
            }
            if (message instanceof BookReturned) {
                BookReturned event = (BookReturned) message;
                return Collections.singleton(event.getWhoReturned());
            }
            if (message instanceof ReservationAdded) {
                ReservationAdded event = (ReservationAdded) message;
                return Collections.singleton(event.getForWhomReserved());
            }
            if (message instanceof ReservationCanceled) {
                ReservationCanceled event = (ReservationCanceled) message;
                return Collections.singleton(event.getWhoCanceled());
            }
            if (message instanceof BookReadyToPickup) {
                BookReadyToPickup event = (BookReadyToPickup) message;
                return Collections.singleton(event.getForWhom());
            }
            if (message instanceof ReservationPickUpPeriodExpired) {
                ReservationPickUpPeriodExpired event = (ReservationPickUpPeriodExpired) message;
                return Collections.singleton(event.getUserId());
            }
            if (message instanceof ReservationBecameLoan) {
                ReservationBecameLoan event = (ReservationBecameLoan) message;
                return Collections.singleton(event.getUserId());
            }
            if (message instanceof LoanBecameOverdue) {
                LoanBecameOverdue event = (LoanBecameOverdue) message;
                return Collections.singleton(event.getUserId());
            }
            if (message instanceof LoanBecameShouldReturnSoon) {
                LoanBecameShouldReturnSoon event = (LoanBecameShouldReturnSoon) message;
                return Collections.singleton(event.getUserId());
            }
            if (message instanceof LoanPeriodExtended) {
                LoanPeriodExtended event = (LoanPeriodExtended) message;
                return Collections.singleton(event.getUserId());
            }
            if (message instanceof BookLost) {
                BookLost event = (BookLost) message;
                return Collections.singleton(event.getWhoLost());
            }
            return null;
        }));
    }
}
