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
import javaclasses.exlibris.ReaderLoanViewId;
import javaclasses.exlibris.c.BookBorrowed;
import javaclasses.exlibris.c.BookReturned;
import javaclasses.exlibris.c.LoanPeriodExtended;
import javaclasses.exlibris.q.ReaderLoanView;

import static java.util.Collections.singleton;

/**
 * Repository for the {@link ReaderLoanViewProjection}.
 *
 * @author Yurii Haidamaka
 */
public class ReaderLoanViewRepository extends ProjectionRepository<ReaderLoanViewId, ReaderLoanViewProjection, ReaderLoanView> {
    @Override
    public void onRegistered() {
        super.onRegistered();
        setUpEventRoute();
    }

    /**
     * Adds the {@link io.spine.server.route.EventRoute EventRoute}s to the repository.
     */
    protected void setUpEventRoute() {
        final EventRouting<ReaderLoanViewId> routing = getEventRouting();
        routing.route(BookBorrowed.class,
                      (message, context) -> {
                          final ReaderLoanViewId id = ReaderLoanViewId.newBuilder()
                                                                      .setLoanId(
                                                                              message.getLoanId())
                                                                      .setUserId(
                                                                              message.getWhoBorrowed())
                                                                      .build();
                          return singleton(id);
                      });
        routing.route(BookReturned.class,
                      (message, context) -> {
                          final ReaderLoanViewId id = ReaderLoanViewId.newBuilder()
                                                                      .setLoanId(
                                                                              message.getLoanId())
                                                                      .setUserId(
                                                                              message.getWhoReturned())
                                                                      .build();
                          return singleton(id);
                      });
        routing.route(LoanPeriodExtended.class,
                      (message, context) -> {
                          final ReaderLoanViewId id = ReaderLoanViewId.newBuilder()
                                                                      .setLoanId(
                                                                              message.getLoanId())
                                                                      .setUserId(
                                                                              message.getUserId())
                                                                      .build();
                          return singleton(id);
                      });
    }
}
