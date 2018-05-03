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
import io.spine.server.route.EventRouting;
import javaclasses.exlibris.UserId;
import javaclasses.exlibris.c.BookBorrowed;
import javaclasses.exlibris.c.BookLost;
import javaclasses.exlibris.c.BookReturned;
import javaclasses.exlibris.c.LoanBecameOverdue;
import javaclasses.exlibris.c.LoanPeriodExtended;
import javaclasses.exlibris.c.LoansExtensionAllowed;
import javaclasses.exlibris.c.LoansExtensionForbidden;
import javaclasses.exlibris.q.BorrowedBooksListView;

import java.util.Collections;
import java.util.HashSet;

/**
 * Repository for the {@link BorrowedBooksListViewProjection}.
 *
 * @author Yurii Haidamaka
 */
public class BorrowedBooksListViewRepository extends ProjectionRepository<UserId, BorrowedBooksListViewProjection, BorrowedBooksListView> {
    @Override
    public void onRegistered() {
        super.onRegistered();
        setUpEventRoute();
    }

    /**
     * Adds the {@link io.spine.server.route.EventRoute EventRoute}s to the repository.
     */
    protected void setUpEventRoute() {
        final EventRouting<UserId> routing = getEventRouting();
        routing.route(BookBorrowed.class,
                      (message, context) -> Collections.singleton(message.getWhoBorrowed()));
        routing.route(LoanBecameOverdue.class,
                      (message, context) -> Collections.singleton(message.getUserId()));
        routing.route(LoanPeriodExtended.class,
                      (message, context) -> Collections.singleton(message.getUserId()));
        routing.route(BookReturned.class,
                      (message, context) -> Collections.singleton(message.getWhoReturned()));
        routing.route(BookLost.class,
                      (message, context) -> Collections.singleton(message.getWhoLost()));
        routing.route(LoansExtensionAllowed.class,
                      (message, context) -> new HashSet<>(message.getBorrowersList()));
        routing.route(LoansExtensionForbidden.class,
                      (message, context) -> new HashSet<>(message.getBorrowersList()));
    }
}
