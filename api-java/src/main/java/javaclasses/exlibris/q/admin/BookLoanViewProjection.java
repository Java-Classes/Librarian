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
import io.spine.core.Subscribe;
import io.spine.net.EmailAddress;
import io.spine.people.PersonName;
import io.spine.server.projection.Projection;
import io.spine.time.LocalDate;
import javaclasses.exlibris.BookId;
import javaclasses.exlibris.BookLoanViewId;
import javaclasses.exlibris.InventoryItemId;
import javaclasses.exlibris.c.BookBorrowed;
import javaclasses.exlibris.c.LoanPeriodExtended;
import javaclasses.exlibris.q.BookLoanView;
import javaclasses.exlibris.q.BookLoanViewVBuilder;

import static javaclasses.exlibris.Timestamps.toLocalDate;

/**
 * The projection state of a one loan in relation to the book at the time of the borrowing.
 *
 * @author Yurii Haidamaka
 */
public class BookLoanViewProjection extends Projection<BookLoanViewId, BookLoanView, BookLoanViewVBuilder> {

    /**
     * @see Projection#Projection(Object)
     */
    public BookLoanViewProjection(BookLoanViewId id) {
        super(id);
    }

    @Subscribe
    public void on(BookBorrowed event) {
        final EmailAddress email = event.getWhoBorrowed()
                                        .getEmail();
        final BookId bookId = event.getInventoryId()
                                   .getBookId();
        final InventoryItemId inventoryItemId = event.getInventoryItemId();
        final Timestamp whenTakenTimestamp = event.getWhenBorrowed();
        final LocalDate whenTaken = toLocalDate(whenTakenTimestamp);
        final Timestamp whenDueTimestamp = event.getWhenDue();
        final LocalDate whenDue = toLocalDate(whenDueTimestamp);
        // TODO: 4/26/2018 yurii.haidamaka SET USERNAME FROM GOOGLE BY EMAIL
        final PersonName userName = PersonName.newBuilder()
                                              .setGivenName("Ivan")
                                              .setFamilyName("Petrov")
                                              .build();

        getBuilder().setBookId(bookId)
                    .setItemId(inventoryItemId)
                    .setUserName(userName)
                    .setEmail(email)
                    .setWhenTaken(whenTaken)
                    .setWhenDue(whenDue);
    }

    @Subscribe
    public void on(LoanPeriodExtended event) {
        final EmailAddress email = event.getUserId()
                                        .getEmail();
        final BookId bookId = event.getInventoryId()
                                   .getBookId();
        final InventoryItemId inventoryItemId = event.getInventoryItemId();
        final Timestamp whenTakenTimestamp = event.getWhenExtended();
        final LocalDate whenTaken = toLocalDate(whenTakenTimestamp);
        final Timestamp whenDueTimestamp = event.getNewDueDate();
        final LocalDate whenDue = toLocalDate(whenDueTimestamp);
        // TODO: 4/26/2018 yurii.haidamaka SET USERNAME FROM GOOGLE BY EMAIL
        final PersonName userName = PersonName.newBuilder()
                                              .setGivenName("Ivan")
                                              .setFamilyName("Petrov")
                                              .build();

        getBuilder().setBookId(bookId)
                    .setItemId(inventoryItemId)
                    .setUserName(userName)
                    .setEmail(email)
                    .setWhenTaken(whenTaken)
                    .setWhenDue(whenDue);
    }
}
