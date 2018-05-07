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
import io.spine.time.LocalDate;
import javaclasses.exlibris.AuthorName;
import javaclasses.exlibris.BookDetails;
import javaclasses.exlibris.BookTitle;
import javaclasses.exlibris.InventoryItemId;
import javaclasses.exlibris.ReaderLoanViewId;
import javaclasses.exlibris.UserId;
import javaclasses.exlibris.c.BookBorrowed;
import javaclasses.exlibris.c.BookEnrichment;
import javaclasses.exlibris.c.BookReturned;
import javaclasses.exlibris.c.LoanPeriodExtended;
import javaclasses.exlibris.q.ReaderLoanView;
import javaclasses.exlibris.q.ReaderLoanViewVBuilder;

import static javaclasses.exlibris.EnrichmentHelper.getEnrichment;
import static javaclasses.exlibris.Timestamps.toLocalDate;

/**
 * The projection state of a one reader's loan.
 *
 * <p>Includes all information about one loan.
 *
 * @author Yurii Haidamaka
 */
public class ReaderLoanViewProjection extends Projection<ReaderLoanViewId, ReaderLoanView, ReaderLoanViewVBuilder> {

    /**
     * @see Projection#Projection(Object)
     */
    public ReaderLoanViewProjection(ReaderLoanViewId id) {
        super(id);
    }

    @Subscribe
    public void on(BookBorrowed event, EventContext context) {
        final UserId userId = event.getWhoBorrowed();
        final InventoryItemId inventoryItemId = event.getInventoryItemId();
        final Timestamp whenTakenTimestamp = event.getWhenBorrowed();
        final LocalDate whenTaken = toLocalDate(whenTakenTimestamp);
        final Timestamp whenDueTimestamp = event.getWhenDue();
        final LocalDate whenDue = toLocalDate(whenDueTimestamp);
        final BookEnrichment enrichment = getEnrichment(BookEnrichment.class, context);
        final BookDetails bookDetails = enrichment.getBook()
                                                  .getBookDetails();
        final AuthorName authorName = bookDetails.getAuthor();
        final BookTitle title = bookDetails.getTitle();
        final ReaderLoanViewId readerLoanViewId = ReaderLoanViewId.newBuilder()
                                                                  .setUserId(userId)
                                                                  .setLoanId(event.getLoanId())
                                                                  .build();
        getBuilder().setId(readerLoanViewId)
                    .setUserId(userId)
                    .setTitle(title)
                    .setAuthors(authorName)
                    .setItemId(inventoryItemId)
                    .setWhenTaken(whenTaken)
                    .setWhenDue(whenDue);
    }

    @Subscribe
    public void on(BookReturned event) {
        final Timestamp whenReturned = event.getWhenReturned();
        getBuilder().setWhenReturned(whenReturned);
    }

    @Subscribe
    public void on(LoanPeriodExtended event) {
        final Timestamp whenDueTimestamp = event.getNewDueDate();
        final LocalDate whenDue = toLocalDate(whenDueTimestamp);
        getBuilder().setWhenDue(whenDue);
    }
}
