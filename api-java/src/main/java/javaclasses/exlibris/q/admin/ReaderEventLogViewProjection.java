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
import io.spine.core.EventContext;
import io.spine.core.Subscribe;
import io.spine.server.projection.Projection;
import javaclasses.exlibris.AuthorName;
import javaclasses.exlibris.BookDetails;
import javaclasses.exlibris.BookId;
import javaclasses.exlibris.BookTitle;
import javaclasses.exlibris.InventoryItemId;
import javaclasses.exlibris.ReaderEventLogViewId;
import javaclasses.exlibris.UserId;
import javaclasses.exlibris.c.BookBorrowed;
import javaclasses.exlibris.c.BookEnrichment;
import javaclasses.exlibris.c.BookLost;
import javaclasses.exlibris.c.BookReturned;
import javaclasses.exlibris.c.LoanBecameOverdue;
import javaclasses.exlibris.c.LoanBecameShouldReturnSoon;
import javaclasses.exlibris.c.LoanPeriodExtended;
import javaclasses.exlibris.c.ReservationAdded;
import javaclasses.exlibris.c.ReservationBecameLoan;
import javaclasses.exlibris.c.ReservationCanceled;
import javaclasses.exlibris.c.ReservationPickUpPeriodExpired;
import javaclasses.exlibris.q.ReaderEventLogView;
import javaclasses.exlibris.q.ReaderEventLogViewVBuilder;

import static javaclasses.exlibris.EnrichmentHelper.getEnrichment;

/**
 * The projection state of a one user's action.
 *
 * <p>Includes all information about one user action.
 *
 * @author Yurii Haidamaka
 */
public class ReaderEventLogViewProjection extends Projection<ReaderEventLogViewId, ReaderEventLogView, ReaderEventLogViewVBuilder> {

    /**
     * @see Projection#Projection(Object)
     */
    public ReaderEventLogViewProjection(ReaderEventLogViewId id) {
        super(id);
    }

    @Subscribe
    public void on(BookBorrowed event, EventContext context) {
        final InventoryItemId inventoryItemId = event.getInventoryItemId();
        final Timestamp whenEmitted = event.getWhenBorrowed();
        final UserId userId = event.getWhoBorrowed();
        setAuthorAndTitle(context);

        getBuilder().setUserId(userId)
                    .setItemId(inventoryItemId)
                    .setEventType(event.getClass()
                                       .getSimpleName())
                    .setWhenEmitted(whenEmitted);
    }

    @Subscribe
    public void on(BookReturned event, EventContext context) {
        final InventoryItemId inventoryItemId = event.getInventoryItemId();
        final Timestamp whenEmitted = event.getWhenReturned();
        final UserId userId = event.getWhoReturned();
        setAuthorAndTitle(context);
        getBuilder().setUserId(userId)
                    .setItemId(inventoryItemId)
                    .setEventType(event.getClass()
                                       .getSimpleName())
                    .setWhenEmitted(whenEmitted);
    }

    @Subscribe
    public void on(ReservationAdded event, EventContext context) {
        final BookId bookId = event.getInventoryId()
                                   .getBookId();
        final Timestamp whenEmitted = event.getWhenCreated();
        final UserId userId = event.getForWhomReserved();
        setAuthorAndTitle(context);

        getBuilder().setUserId(userId)
                    .setBookId(bookId)
                    .setEventType(event.getClass()
                                       .getSimpleName())
                    .setWhenEmitted(whenEmitted);
    }

    @Subscribe
    public void on(ReservationCanceled event, EventContext context) {
        final BookId bookId = event.getInventoryId()
                                   .getBookId();
        final Timestamp whenEmitted = event.getWhenCanceled();
        final UserId userId = event.getWhoCanceled();
        setAuthorAndTitle(context);

        getBuilder().setUserId(userId)
                    .setBookId(bookId)
                    .setEventType(event.getClass()
                                       .getSimpleName())
                    .setWhenEmitted(whenEmitted);
    }

    @Subscribe
    public void on(ReservationPickUpPeriodExpired event, EventContext context) {
        final BookId bookId = event.getInventoryId()
                                   .getBookId();
        final Timestamp whenEmitted = event.getWhenExpired();
        final UserId userId = event.getUserId();
        setAuthorAndTitle(context);

        getBuilder().setUserId(userId)
                    .setBookId(bookId)
                    .setEventType(event.getClass()
                                       .getSimpleName())
                    .setWhenEmitted(whenEmitted);
    }

    @Subscribe
    public void on(ReservationBecameLoan event, EventContext context) {
        final BookId bookId = event.getInventoryId()
                                   .getBookId();
        final Timestamp whenEmitted = event.getWhenBecameLoan();
        final UserId userId = event.getUserId();
        setAuthorAndTitle(context);

        getBuilder().setUserId(userId)
                    .setBookId(bookId)
                    .setEventType(event.getClass()
                                       .getSimpleName())
                    .setWhenEmitted(whenEmitted);
    }

    @Subscribe
    public void on(LoanBecameOverdue event, EventContext context) {
        final InventoryItemId itemId = event.getInventoryItemId();
        final Timestamp whenEmitted = event.getWhenBecameOverdue();
        final UserId userId = event.getUserId();
        setAuthorAndTitle(context);

        getBuilder().setUserId(userId)
                    .setItemId(itemId)
                    .setEventType(event.getClass()
                                       .getSimpleName())
                    .setWhenEmitted(whenEmitted);
    }

    @Subscribe
    public void on(LoanBecameShouldReturnSoon event, EventContext context) {
        final InventoryItemId itemId = event.getInventoryItemId();
        final Timestamp whenEmitted = event.getWhenBecameShouldReturnSoon();
        final UserId userId = event.getUserId();
        setAuthorAndTitle(context);

        getBuilder().setUserId(userId)
                    .setItemId(itemId)
                    .setEventType(event.getClass()
                                       .getSimpleName())
                    .setWhenEmitted(whenEmitted);
    }

    @Subscribe
    public void on(LoanPeriodExtended event, EventContext context) {
        final InventoryItemId itemId = event.getInventoryItemId();
        final Timestamp whenEmitted = event.getWhenExtended();
        final UserId userId = event.getUserId();
        setAuthorAndTitle(context);

        getBuilder().setUserId(userId)
                    .setItemId(itemId)
                    .setEventType(event.getClass()
                                       .getSimpleName())
                    .setWhenEmitted(whenEmitted);
    }

    @Subscribe
    public void on(BookLost event, EventContext context) {
        final InventoryItemId itemId = event.getInventoryItemId();
        final Timestamp whenEmitted = event.getWhenReported();
        final UserId userId = event.getWhoLost();
        setAuthorAndTitle(context);

        getBuilder().setUserId(userId)
                    .setItemId(itemId)
                    .setEventType(event.getClass()
                                       .getSimpleName())
                    .setWhenEmitted(whenEmitted);
    }

    private void setAuthorAndTitle(EventContext context) {
        final BookEnrichment enrichment = getEnrichment(BookEnrichment.class, context);
        final BookDetails bookDetails = enrichment.getBook()
                                                  .getBookDetails();
        final AuthorName authorName = bookDetails.getAuthor();
        final BookTitle title = bookDetails.getTitle();
        getBuilder().setTitle(title)
                    .setAuthors(authorName);

    }
}
