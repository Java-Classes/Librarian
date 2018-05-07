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

import com.google.protobuf.Timestamp;
import io.spine.core.EventContext;
import io.spine.core.Subscribe;
import io.spine.server.projection.Projection;
import io.spine.time.LocalDate;
import javaclasses.exlibris.BookDetails;
import javaclasses.exlibris.BookId;
import javaclasses.exlibris.UserId;
import javaclasses.exlibris.c.BookEnrichment;
import javaclasses.exlibris.c.BookReadyToPickup;
import javaclasses.exlibris.c.ReservationAdded;
import javaclasses.exlibris.c.ReservationBecameLoan;
import javaclasses.exlibris.c.ReservationCanceled;
import javaclasses.exlibris.c.ReservationPickUpPeriodExpired;
import javaclasses.exlibris.q.ReservedBookItem;
import javaclasses.exlibris.q.ReservedBookItemStatus;
import javaclasses.exlibris.q.ReservedBooksListView;
import javaclasses.exlibris.q.ReservedBooksListViewVBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.stream.IntStream;

import static javaclasses.exlibris.EnrichmentHelper.getEnrichment;
import static javaclasses.exlibris.Timestamps.toLocalDate;

/**
 * A projection state of all books that are reserved by user.
 *
 * <p>Contains the list of reserved books.
 *
 * @author Yurii Haidamaka
 */
public class ReservedBooksListViewProjection extends Projection<UserId, ReservedBooksListView, ReservedBooksListViewVBuilder> {

    /**
     * @see Projection#Projection(Object)
     */
    public ReservedBooksListViewProjection(UserId id) {
        super(id);
    }

    @Subscribe
    public void on(ReservationAdded event, EventContext context) {
        final BookId bookId = event.getInventoryId()
                                   .getBookId();
        final ReservedBookItemStatus status = ReservedBookItemStatus.RESERVED;
        final BookEnrichment enrichment = getEnrichment(BookEnrichment.class, context);
        final BookDetails bookDetails = enrichment.getBook()
                                                  .getBookDetails();
        final Timestamp whenExpectedTimestamp = event.getWhenExpected();
        final LocalDate whenExpected = toLocalDate(whenExpectedTimestamp);

        final ReservedBookItem bookItem = ReservedBookItem.newBuilder()
                                                          .setBookId(bookId)
                                                          .setIsbn(bookDetails.getIsbn())
                                                          .setTitle(bookDetails.getTitle())
                                                          .setAuthors(bookDetails.getAuthor())
                                                          .setCoverUrl(
                                                                  bookDetails.getBookCoverUrl())
                                                          .addAllCategorie(
                                                                  bookDetails.getCategoriesList())
                                                          .setSynopsis(bookDetails.getSynopsis())
                                                          .setWhenReadyToPickUp(whenExpected)
                                                          .setStatus(status)
                                                          .build();
        getBuilder().setUserId(event.getForWhomReserved())
                    .addBookItem(bookItem)
                    .setNumberOfReservedBooks((getBuilder().getBookItem()
                                                           .size()));
    }

    @Subscribe
    public void on(ReservationBecameLoan event) {
        final List<ReservedBookItem> items = new ArrayList<>(getBuilder().getBookItem());
        final int index = getIndexByBookId(items, event.getInventoryId()
                                                       .getBookId());
        if (index != -1) {
            getBuilder().removeBookItem(index)
                        .setNumberOfReservedBooks((getBuilder().getBookItem()
                                                               .size()));
        }
    }

    @Subscribe
    public void on(ReservationCanceled event) {
        final List<ReservedBookItem> items = new ArrayList<>(getBuilder().getBookItem());
        final int index = getIndexByBookId(items, event.getInventoryId()
                                                       .getBookId());
        if (index != -1) {
            getBuilder().removeBookItem(index)
                        .setNumberOfReservedBooks((getBuilder().getBookItem()
                                                               .size()));
        }
    }

    @Subscribe
    public void on(ReservationPickUpPeriodExpired event) {
        final List<ReservedBookItem> items = new ArrayList<>(getBuilder().getBookItem());
        final int index = getIndexByBookId(items, event.getInventoryId()
                                                       .getBookId());
        if (index != -1) {
            getBuilder().removeBookItem(index)
                        .setNumberOfReservedBooks((getBuilder().getBookItem()
                                                               .size()));
        }
    }

    @Subscribe
    public void on(BookReadyToPickup event) {
        final List<ReservedBookItem> items = new ArrayList<>(getBuilder().getBookItem());
        final int index = getIndexByBookId(items, event.getInventoryId()
                                                       .getBookId());
        if (index != -1) {
            final ReservedBookItem bookItem = items.get(index);
            final ReservedBookItem newBookItem = ReservedBookItem.newBuilder(bookItem)
                                                                 .setStatus(
                                                                         ReservedBookItemStatus.READY_TO_PICK_UP)
                                                                 .build();
            getBuilder().setBookItem(index, newBookItem)
                        .setNumberOfReservedBooks((getBuilder().getBookItem()
                                                               .size()));
        }
    }

    private int getIndexByBookId(List<ReservedBookItem> items, BookId id) {
        final OptionalInt index = IntStream.range(0, items.size())
                                           .filter(i -> items.get(i)
                                                             .getBookId()
                                                             .equals(id))
                                           .findFirst();
        return index.isPresent() ? index.getAsInt() : -1;
    }
}
