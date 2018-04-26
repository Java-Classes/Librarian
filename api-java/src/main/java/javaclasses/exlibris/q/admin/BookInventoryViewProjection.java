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
import javaclasses.exlibris.InventoryId;
import javaclasses.exlibris.InventoryItemId;
import javaclasses.exlibris.UserId;
import javaclasses.exlibris.c.BookBorrowed;
import javaclasses.exlibris.c.BookEnrichment;
import javaclasses.exlibris.c.BookLost;
import javaclasses.exlibris.c.BookReturned;
import javaclasses.exlibris.c.InventoryAppended;
import javaclasses.exlibris.c.InventoryDecreased;
import javaclasses.exlibris.c.LoanBecameOverdue;
import javaclasses.exlibris.c.LoanPeriodExtended;
import javaclasses.exlibris.q.BookInventoryView;
import javaclasses.exlibris.q.BookInventoryViewVBuilder;
import javaclasses.exlibris.q.InventoryItemState;
import javaclasses.exlibris.q.LoanDetails;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.stream.IntStream;

import static javaclasses.exlibris.EnrichmentHelper.getEnrichment;
import static javaclasses.exlibris.Timestamps.toLocalDate;

/**
 * The projection state of a one inventory.
 *
 * @author Yurii Haidamaka
 */
public class BookInventoryViewProjection extends Projection<InventoryId, BookInventoryView, BookInventoryViewVBuilder> {

    /**
     * @see Projection#Projection(Object)
     */
    public BookInventoryViewProjection(InventoryId id) {
        super(id);
    }

    @Subscribe
    public void on(InventoryAppended event, EventContext context) {
        final BookEnrichment enrichment = getEnrichment(BookEnrichment.class, context);
        final BookDetails bookDetails = enrichment.getBook()
                                                  .getBookDetails();
        final AuthorName authorName = bookDetails.getAuthor();
        final BookTitle title = bookDetails.getTitle();
        final InventoryItemId itemId = event.getInventoryItemId();
        final InventoryItemState state = InventoryItemState.newBuilder()
                                                           .setItemId(itemId)
                                                           .setInLibrary(true)
                                                           .build();
        getBuilder().setTitle(title)
                    .setAuthor(authorName)
                    .addItemState(state);
    }

    @Subscribe
    public void on(InventoryDecreased event) {
        getBuilder().clearTitle()
                    .clearAuthor()
                    .clearItemState();
    }

    @Subscribe
    public void on(BookBorrowed event) {
        final InventoryItemId itemId = event.getInventoryItemId();
        final UserId userId = event.getWhoBorrowed();
        // TODO: 4/26/2018 yurii.haidamaka SET USERNAME FROM GOOGLE BY EMAIL
        final PersonName userName = PersonName.newBuilder()
                                              .setGivenName("Ivan")
                                              .setFamilyName("Petrov")
                                              .build();
        final EmailAddress email = event.getWhoBorrowed()
                                        .getEmail();
        final Timestamp whenTakenTimestamp = event.getWhenBorrowed();
        final LocalDate whenTaken = toLocalDate(whenTakenTimestamp);
        final Timestamp whenDueTimestamp = event.getWhenDue();
        final LocalDate whenDue = toLocalDate(whenDueTimestamp);
        final LoanDetails loanDetails = LoanDetails.newBuilder()
                                                   .setUserId(userId)
                                                   .setUserName(userName)
                                                   .setEmail(email)
                                                   .setWhenTaken(whenTaken)
                                                   .setWhenDue(whenDue)
                                                   .setOverdue(false)
                                                   .build();
        final InventoryItemState inventoryItemState = InventoryItemState.newBuilder()
                                                                        .setItemId(itemId)
                                                                        .setLoanDetails(loanDetails)
                                                                        .build();
        final List<InventoryItemState> items = new ArrayList<>(getBuilder().getItemState());
        final int index = getIndexByInventoryId(items, event.getInventoryItemId());
        getBuilder().setItemState(index, inventoryItemState);
    }

    @Subscribe
    public void on(BookLost event) {
        final InventoryItemId itemId = event.getInventoryItemId();

        final List<InventoryItemState> items = new ArrayList<>(getBuilder().getItemState());
        final int index = getIndexByInventoryId(items, event.getInventoryItemId());
        final InventoryItemState invItemState = items.get(index);
        final InventoryItemState newInvItemState = InventoryItemState.newBuilder(invItemState)
                                                                     .setItemId(itemId)
                                                                     .setLost(true)
                                                                     .build();
        getBuilder().setItemState(index, newInvItemState);
    }

    @Subscribe
    public void on(LoanBecameOverdue event) {
        final InventoryItemId itemId = event.getInventoryItemId();

        final List<InventoryItemState> items = new ArrayList<>(getBuilder().getItemState());
        final int index = getIndexByInventoryId(items, event.getInventoryItemId());
        final InventoryItemState invItemState = items.get(index);
        final LoanDetails loanDetails = LoanDetails.newBuilder(invItemState.getLoanDetails())
                                                   .setOverdue(true)
                                                   .build();
        final InventoryItemState newInvItemState = InventoryItemState.newBuilder(invItemState)
                                                                     .setItemId(itemId)
                                                                     .setLoanDetails(loanDetails)
                                                                     .build();
        getBuilder().setItemState(index, newInvItemState);
    }

    @Subscribe
    public void on(BookReturned event) {
        final InventoryItemId itemId = event.getInventoryItemId();

        final List<InventoryItemState> items = new ArrayList<>(getBuilder().getItemState());
        final int index = getIndexByInventoryId(items, event.getInventoryItemId());
        final InventoryItemState invItemState = items.get(index);
        final InventoryItemState newInvItemState = InventoryItemState.newBuilder(invItemState)
                                                                     .setItemId(itemId)
                                                                     .setInLibrary(true)
                                                                     .build();
        getBuilder().setItemState(index, newInvItemState);
    }

    @Subscribe
    public void on(LoanPeriodExtended event) {
        final InventoryItemId itemId = event.getInventoryItemId();
        final Timestamp whenDueTimestamp = event.getNewDueDate();
        final LocalDate whenDue = toLocalDate(whenDueTimestamp);

        final List<InventoryItemState> items = new ArrayList<>(getBuilder().getItemState());
        final int index = getIndexByInventoryId(items, event.getInventoryItemId());
        final InventoryItemState invItemState = items.get(index);

        final LoanDetails loanDetails = LoanDetails.newBuilder(invItemState.getLoanDetails())
                                                   .setWhenDue(whenDue)
                                                   .build();
        final InventoryItemState newInvItemState = InventoryItemState.newBuilder(invItemState)
                                                                     .setItemId(itemId)
                                                                     .setLoanDetails(loanDetails)
                                                                     .build();
        getBuilder().setItemState(index, newInvItemState);
    }

    private int getIndexByInventoryId(List<InventoryItemState> items, InventoryItemId id) {
        final OptionalInt index = IntStream.range(0, items.size())
                                           .filter(i -> items.get(i)
                                                             .getItemId()
                                                             .equals(id))
                                           .findFirst();
        return index.isPresent() ? index.getAsInt() : -1;
    }

}
