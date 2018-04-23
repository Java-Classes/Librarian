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
import io.spine.server.projection.Projection;
import javaclasses.exlibris.BookId;
import javaclasses.exlibris.InventoryId;
import javaclasses.exlibris.InventoryItemId;
import javaclasses.exlibris.UserId;
import javaclasses.exlibris.c.BookBorrowed;
import javaclasses.exlibris.c.BookLost;
import javaclasses.exlibris.c.BookReadyToPickup;
import javaclasses.exlibris.c.BookReturned;
import javaclasses.exlibris.c.LoanBecameOverdue;
import javaclasses.exlibris.c.LoanPeriodExtended;
import javaclasses.exlibris.c.ReservationAdded;
import javaclasses.exlibris.c.ReservationBecameLoan;
import javaclasses.exlibris.c.ReservationCanceled;
import javaclasses.exlibris.c.ReservationPickUpPeriodExpired;
import javaclasses.exlibris.q.ReaderEventLogItem;
import javaclasses.exlibris.q.ReaderHistoryView;
import javaclasses.exlibris.q.ReaderHistoryViewVBuilder;

import static javaclasses.exlibris.q.ReaderEventLogItemType.READER_EVENT_BOOK_LOST;
import static javaclasses.exlibris.q.ReaderEventLogItemType.READER_EVENT_BORROWED;
import static javaclasses.exlibris.q.ReaderEventLogItemType.READER_EVENT_RESERVATION_BECAME_LOAN;
import static javaclasses.exlibris.q.ReaderEventLogItemType.READER_EVENT_RESERVATION_CANCELED;
import static javaclasses.exlibris.q.ReaderEventLogItemType.READER_EVENT_RESERVATION_PICK_UP_PERIOD_EXPIRED;
import static javaclasses.exlibris.q.ReaderEventLogItemType.READER_EVENT_RESERVATION_READY_TO_PICK_UP;
import static javaclasses.exlibris.q.ReaderEventLogItemType.READER_EVENT_RESERVED;
import static javaclasses.exlibris.q.ReaderEventLogItemType.READER_EVENT_RETURNED;

/**
 * The projection state of a reader's history view.
 *
 * <p>Includes the reader's event log for the full history view
 * and the reader's loans list for the compact history view.
 *
 * @author Yegor Udovchenko
 */
public class ReaderHistoryViewProjection extends Projection<UserId, ReaderHistoryView, ReaderHistoryViewVBuilder> {

    /**
     * @see Projection#Projection(Object)
     */
    public ReaderHistoryViewProjection(UserId id) {
        super(id);
    }

    @Subscribe
    public void on(BookBorrowed event) {
        final InventoryItemId inventoryItemId = event.getInventoryItemId();
        final Timestamp whenBorrowed = event.getWhenBorrowed();

        final ReaderEventLogItem logItem = ReaderEventLogItem.newBuilder()
                                                             .setItemId(inventoryItemId)
                                                             .setWhenEmitted(whenBorrowed)
                                                             .setEventType(READER_EVENT_BORROWED)
                                                             .build();
        getBuilder().addEvent(logItem);
    }

    @Subscribe
    public void on(BookReturned event) {
        final InventoryItemId inventoryItemId = event.getInventoryItemId();
        final Timestamp whenReturned = event.getWhenReturned();

        final ReaderEventLogItem logItem = ReaderEventLogItem.newBuilder()
                                                             .setItemId(inventoryItemId)
                                                             .setWhenEmitted(whenReturned)
                                                             .setEventType(READER_EVENT_RETURNED)
                                                             .build();
        getBuilder().addEvent(logItem);
    }

    @Subscribe
    public void on(ReservationAdded event) {
        final InventoryId inventoryId = event.getInventoryId();
        final BookId bookId = inventoryId.getBookId();
        final Timestamp whenCreated = event.getWhenCreated();

        final ReaderEventLogItem logItem = ReaderEventLogItem.newBuilder()
                                                             .setBookId(bookId)
                                                             .setWhenEmitted(whenCreated)
                                                             .setEventType(READER_EVENT_RESERVED)
                                                             .build();
        getBuilder().addEvent(logItem);
    }

    @Subscribe
    public void on(ReservationCanceled event) {
        final InventoryId inventoryId = event.getInventoryId();
        final BookId bookId = inventoryId.getBookId();
        final Timestamp whenCanceled = event.getWhenCanceled();

        final ReaderEventLogItem logItem = ReaderEventLogItem
                .newBuilder()
                .setBookId(bookId)
                .setWhenEmitted(whenCanceled)
                .setEventType(READER_EVENT_RESERVATION_CANCELED)
                .build();
        getBuilder().addEvent(logItem);
    }

    @Subscribe
    public void on(BookReadyToPickup event) {
        final InventoryItemId inventoryItemId = event.getInventoryItemId();
        final Timestamp whenBecameReadyToPickup = event.getWhenBecameReadyToPickup();

        final ReaderEventLogItem logItem = ReaderEventLogItem
                .newBuilder()
                .setItemId(inventoryItemId)
                .setWhenEmitted(whenBecameReadyToPickup)
                .setEventType(READER_EVENT_RESERVATION_READY_TO_PICK_UP)
                .build();
        getBuilder().addEvent(logItem);
    }

    @Subscribe
    public void on(ReservationPickUpPeriodExpired event) {
        final InventoryId inventoryId = event.getInventoryId();
        final BookId bookId = inventoryId.getBookId();
        final Timestamp whenExpired = event.getWhenExpired();

        final ReaderEventLogItem logItem = ReaderEventLogItem
                .newBuilder()
                .setBookId(bookId)
                .setWhenEmitted(whenExpired)
                .setEventType(READER_EVENT_RESERVATION_PICK_UP_PERIOD_EXPIRED)
                .build();
        getBuilder().addEvent(logItem);
    }

    @Subscribe
    public void on(ReservationBecameLoan event) {
        final InventoryId inventoryId = event.getInventoryId();
        final BookId bookId = inventoryId.getBookId();
        final Timestamp whenBecameLoan = event.getWhenBecameLoan();

        final ReaderEventLogItem logItem = ReaderEventLogItem
                .newBuilder()
                .setBookId(bookId)
                .setWhenEmitted(whenBecameLoan)
                .setEventType(READER_EVENT_RESERVATION_BECAME_LOAN)
                .build();
        getBuilder().addEvent(logItem);
    }

    @Subscribe
    public void on(LoanBecameOverdue event) {
        // TODO 4/20/2018[yegor.udovchenko]: refactor LoanBecameOverdue event. Add InventoryItemId and UserId
    }

    // TODO 4/23/2018[yegor.udovchenko]: Add LoanBecameShouldReturnSoon event and proper command.
//    @Subscribe
//    public void on(LoanBecameShouldReturnSoon event) {
//    }

    @Subscribe
    public void on(LoanPeriodExtended event) {
        // TODO 4/23/2018[yegor.udovchenko]: Add InventoryItemId to the event.
    }

    @Subscribe
    public void on(BookLost event) {
        final InventoryItemId inventoryItemId = event.getInventoryItemId();
        final Timestamp whenReported = event.getWhenReported();

        final ReaderEventLogItem logItem = ReaderEventLogItem
                .newBuilder()
                .setItemId(inventoryItemId)
                .setWhenEmitted(whenReported)
                .setEventType(READER_EVENT_BOOK_LOST)
                .build();
        getBuilder().addEvent(logItem);
    }
}
