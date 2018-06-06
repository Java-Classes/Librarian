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
import javaclasses.exlibris.LoanId;
import javaclasses.exlibris.UserId;
import javaclasses.exlibris.c.BookBorrowed;
import javaclasses.exlibris.c.BookEnrichment;
import javaclasses.exlibris.c.BookLost;
import javaclasses.exlibris.c.BookReturned;
import javaclasses.exlibris.c.LoanBecameOverdue;
import javaclasses.exlibris.c.LoanBecameShouldReturnSoon;
import javaclasses.exlibris.c.LoanPeriodExtended;
import javaclasses.exlibris.c.LoansExtensionAllowed;
import javaclasses.exlibris.c.LoansExtensionForbidden;
import javaclasses.exlibris.q.BorrowedBookItem;
import javaclasses.exlibris.q.BorrowedBookItemStatus;
import javaclasses.exlibris.q.BorrowedBooksListView;
import javaclasses.exlibris.q.BorrowedBooksListViewVBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.stream.IntStream;

import static javaclasses.exlibris.EnrichmentHelper.getEnrichment;
import static javaclasses.exlibris.Timestamps.toLocalDate;

/**
 * A projection state of all books that are borrowed by user.
 *
 * <p>Contains the list of borrowed books.
 *
 * @author Yurii Haidamaka
 */
public class BorrowedBooksListViewProjection extends Projection<UserId, BorrowedBooksListView, BorrowedBooksListViewVBuilder> {

    /**
     * @see Projection#Projection(Object)
     */
    public BorrowedBooksListViewProjection(UserId id) {
        super(id);
    }

    @Subscribe
    public void on(BookBorrowed event, EventContext context) {
        final LoanId loanId = event.getLoanId();
        final BookId bookId = event.getInventoryId()
                                   .getBookId();
        final BorrowedBookItemStatus status = BorrowedBookItemStatus.BORROWED;
        final BookEnrichment enrichment = getEnrichment(BookEnrichment.class, context);
        final BookDetails bookDetails = enrichment.getBook()
                                                  .getBookDetails();
        final Timestamp whenBorrowedTimeStamp = event.getWhenBorrowed();
        final LocalDate whenBorrowed = toLocalDate(whenBorrowedTimeStamp);
        final LocalDate dueDate = toLocalDate(event.getWhenDue());
        final BorrowedBookItem bookItem = BorrowedBookItem.newBuilder()
                                                          .setLoanId(loanId)
                                                          .setBookId(bookId)
                                                          .setIsbn(bookDetails.getIsbn())
                                                          .setTitle(bookDetails.getTitle())
                                                          .addAllAuthor(bookDetails.getAuthorList())
                                                          .setCoverUrl(
                                                                  bookDetails.getBookCoverUrl())
                                                          .addAllCategories(
                                                                  bookDetails.getCategoriesList())
                                                          .setSynopsis(bookDetails.getSynopsis())
                                                          .setDueDate(dueDate)
                                                          .setWhenBorrowed(whenBorrowed)
                                                          .setStatus(status)
                                                          .setIsAllowedLoanExtension(false)
                                                          .build();
        getBuilder().setUserId(event.getWhoBorrowed())
                    .addBookItem(bookItem)
                    .setNumberOfBorrowedBooks(getBuilder().getBookItem()
                                                          .size());
    }

    @Subscribe
    public void on(LoanBecameOverdue event) {
        final List<BorrowedBookItem> items = new ArrayList<>(getBuilder().getBookItem());
        final int index = getIndexByBookId(items, event.getInventoryId()
                                                       .getBookId());
        final BorrowedBookItem bookItem = items.get(index);
        final BorrowedBookItem newBookItem = BorrowedBookItem.newBuilder(bookItem)
                                                             .setStatus(
                                                                     BorrowedBookItemStatus.OVERDUE)
                                                             .build();
        getBuilder().setBookItem(index, newBookItem)
                    .setNumberOfOverdueBooks(getNumberOfOverdueBooks(getBuilder().getBookItem()));
    }

    @Subscribe
    public void on(LoanBecameShouldReturnSoon event) {
        final List<BorrowedBookItem> items = new ArrayList<>(getBuilder().getBookItem());

        final int index = getIndexByBookId(items, event.getInventoryId()
                                                       .getBookId());
        final BorrowedBookItem bookItem = items.get(index);
        final BorrowedBookItem newBookItem =
                BorrowedBookItem.newBuilder(bookItem)
                                .setStatus(
                                        BorrowedBookItemStatus.SHOULD_RETURN_SOON)
                                .setIsAllowedLoanExtension(
                                        event.getIsAllowedExtension())
                                .build();
        getBuilder().setBookItem(index, newBookItem);
    }

    @Subscribe
    public void on(LoanPeriodExtended event) {
        final Timestamp newDueDateTimestamp = event.getNewDueDate();
        final LocalDate newDueDate = toLocalDate(newDueDateTimestamp);
        final List<BorrowedBookItem> items = new ArrayList<>(getBuilder().getBookItem());
        final int index = getIndexByBookId(items, event.getInventoryId()
                                                       .getBookId());
        final BorrowedBookItem bookItem = items.get(index);
        final BorrowedBookItem newBookItem = BorrowedBookItem.newBuilder(bookItem)
                                                             .setStatus(
                                                                     BorrowedBookItemStatus.BORROWED)
                                                             .setIsAllowedLoanExtension(false)
                                                             .setDueDate(newDueDate)
                                                             .build();
        getBuilder().setBookItem(index, newBookItem)
                    .setNumberOfOverdueBooks(getNumberOfOverdueBooks(getBuilder().getBookItem()));
    }

    @Subscribe
    public void on(BookReturned event) {
        final List<BorrowedBookItem> items = new ArrayList<>(getBuilder().getBookItem());
        final int index = getIndexByBookId(items, event.getInventoryId()
                                                       .getBookId());
        getBuilder().removeBookItem(index)
                    .setNumberOfBorrowedBooks(getBuilder().getBookItem()
                                                          .size());
    }

    @Subscribe
    public void on(BookLost event) {
        final List<BorrowedBookItem> items = new ArrayList<>(getBuilder().getBookItem());
        final int index = getIndexByBookId(items, event.getInventoryId()
                                                       .getBookId());
        getBuilder().removeBookItem(index)
                    .setNumberOfBorrowedBooks(getBuilder().getBookItem()
                                                          .size());
    }

    @Subscribe
    public void on(LoansExtensionAllowed event) {
        final List<BorrowedBookItem> items = new ArrayList<>(getBuilder().getBookItem());
        final int index = getIndexByBookId(items, event.getInventoryId()
                                                       .getBookId());
        final BorrowedBookItem bookItem = items.get(index);
        if (!bookItem.getStatus()
                     .equals(BorrowedBookItemStatus.BORROWED)) {
            final BorrowedBookItem newBookItem = BorrowedBookItem.newBuilder(bookItem)
                                                                 .setIsAllowedLoanExtension(true)
                                                                 .build();
            getBuilder().setBookItem(index, newBookItem);
        }
    }

    @Subscribe
    public void on(LoansExtensionForbidden event) {
        final List<BorrowedBookItem> items = new ArrayList<>(getBuilder().getBookItem());
        final int index = getIndexByBookId(items, event.getInventoryId()
                                                       .getBookId());
        final BorrowedBookItem bookItem = items.get(index);
        final BorrowedBookItem newBookItem = BorrowedBookItem.newBuilder(bookItem)
                                                             .setIsAllowedLoanExtension(false)
                                                             .build();
        getBuilder().setBookItem(index, newBookItem);
    }

    private int getIndexByBookId(List<BorrowedBookItem> items, BookId id) {
        final OptionalInt index = IntStream.range(0, items.size())
                                           .filter(i -> items.get(i)
                                                             .getBookId()
                                                             .equals(id))
                                           .findFirst();
        return index.isPresent() ? index.getAsInt() : -1;
    }

    private int getNumberOfOverdueBooks(List<BorrowedBookItem> items) {
        final int count = (int) items.stream()
                                     .filter(item -> item.getStatus()
                                                         .equals(BorrowedBookItemStatus.OVERDUE))
                                     .count();
        return count;
    }
}
