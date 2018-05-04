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
import io.spine.net.EmailAddress;
import io.spine.people.PersonName;
import io.spine.server.projection.Projection;
import io.spine.time.LocalDate;
import javaclasses.exlibris.AuthorName;
import javaclasses.exlibris.BookDetails;
import javaclasses.exlibris.BookTitle;
import javaclasses.exlibris.InventoryItemId;
import javaclasses.exlibris.c.BookEnrichment;
import javaclasses.exlibris.c.BookLost;
import javaclasses.exlibris.c.InventoryDecreased;
import javaclasses.exlibris.q.LostBookView;
import javaclasses.exlibris.q.LostBookViewVBuilder;

import static javaclasses.exlibris.EnrichmentHelper.getEnrichment;
import static javaclasses.exlibris.Timestamps.toLocalDate;

/**
 * The projection state of a one lost books.
 *
 * @author Yurii Haidamaka
 */
public class LostBookViewProjection extends Projection<InventoryItemId, LostBookView, LostBookViewVBuilder> {

    /**
     * @see Projection#Projection(Object)
     */
    public LostBookViewProjection(InventoryItemId id) {
        super(id);
    }

    @Subscribe
    public void on(BookLost event, EventContext context) {
        final InventoryItemId itemId = event.getInventoryItemId();
        final Timestamp whenReportedTimestamp = event.getWhenReported();
        final LocalDate whenReported = toLocalDate(whenReportedTimestamp);
        final EmailAddress userEmail = event.getWhoLost()
                                            .getEmail();
        // TODO: 4/26/2018 yurii.haidamaka SET USERNAME FROM GOOGLE BY EMAIL
        final PersonName userName = PersonName.newBuilder()
                                              .setGivenName("Ivan")
                                              .setFamilyName("Petrov")
                                              .build();
        final BookEnrichment enrichment = getEnrichment(BookEnrichment.class, context);
        final BookDetails bookDetails = enrichment.getBook()
                                                  .getBookDetails();
        final AuthorName authorName = bookDetails.getAuthor();
        final BookTitle title = bookDetails.getTitle();

        getBuilder().setItemId(itemId)
                    .setAuthors(authorName)
                    .setTitle(title)
                    .setUserName(userName)
                    .setEmail(userEmail)
                    .setWhenReported(whenReported);
    }

    @Subscribe
    public void on(InventoryDecreased event) {
        getBuilder().clearItemId()
                    .clearAuthors()
                    .clearTitle()
                    .clearUserName()
                    .clearEmail()
                    .clearWhenReported();
    }

}
