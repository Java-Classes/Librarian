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
import io.spine.time.MonthOfYear;
import javaclasses.exlibris.BookDetails;
import javaclasses.exlibris.BookId;
import javaclasses.exlibris.LoanId;
import javaclasses.exlibris.UserId;
import javaclasses.exlibris.c.BookBorrowed;
import javaclasses.exlibris.c.BookEnrichment;
import javaclasses.exlibris.q.BookItem;
import javaclasses.exlibris.q.BorrowedBookItemStatus;
import javaclasses.exlibris.q.BorrowedBooksListView;
import javaclasses.exlibris.q.BorrowedBooksListViewVBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.IntStream;

import static io.spine.time.Timestamps2.toDate;
import static javaclasses.exlibris.EnrichmentHelper.getEnrichment;

/**
 * A projection state of all books that are borrowed by user.
 *
 * <p>Contains the list of all books.
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
        final boolean isAllowedLoanExtension = false;
        final BookEnrichment enrichment = getEnrichment(BookEnrichment.class, context);
        final BookDetails bookDetails = enrichment.getBook()
                                                  .getBookDetails();
        final Timestamp whenBorrowed = event.getWhenBorrowed();
        final GregorianCalendar calendar = new GregorianCalendar(TimeZone.getDefault());
        calendar.setGregorianChange(toDate(whenBorrowed));
        final LocalDate date = LocalDate.newBuilder()
                                  .setDay(calendar.get(Calendar.DAY_OF_MONTH))
                                  .setMonth(
                                          MonthOfYear.valueOf(calendar.get(Calendar.MONTH)))
                                  .setYear(calendar.get(Calendar.YEAR))
                                  .build();
        log().info("{}", date);
        log().info("{}", calendar.get(Calendar.YEAR));
        log().info("{}", calendar.get(Calendar.MONTH));
        log().info("{}", calendar.get(Calendar.DAY_OF_MONTH));

    }

    private int getIndexByBookId(List<BookItem> items, BookId id) {
        final int index = IntStream.range(0, items.size())
                                   .filter(i -> items.get(i)
                                                     .getBookId()
                                                     .equals(id))
                                   .findFirst()
                                   .getAsInt();
        return index;
    }

    private static Logger log() {
        return LogSingleton.INSTANCE.value;
    }

    private enum LogSingleton {
        INSTANCE;
        @SuppressWarnings("NonSerializableFieldInSerializableClass")
        private final Logger value = LoggerFactory.getLogger(BorrowedBooksListViewProjection.class);
    }
}
