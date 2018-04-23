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
import javaclasses.exlibris.c.LoanBecameShouldReturnSoon;
import javaclasses.exlibris.c.LoanPeriodExtended;
import javaclasses.exlibris.c.ReservationAdded;
import javaclasses.exlibris.c.ReservationBecameLoan;
import javaclasses.exlibris.c.ReservationCanceled;
import javaclasses.exlibris.c.ReservationPickUpPeriodExpired;
import javaclasses.exlibris.q.ReaderEventLogItem;
import javaclasses.exlibris.q.ReaderHistoryView;
import javaclasses.exlibris.q.ReaderHistoryViewVBuilder;

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
                                                             .setEventType(event.getClass()
                                                                                .getSimpleName())
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
                                                             .setEventType(event.getClass()
                                                                                .getSimpleName())
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
                                                             .setEventType(event.getClass()
                                                                                .getSimpleName())
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
                .setEventType(event.getClass()
                                   .getSimpleName())
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
                .setEventType(event.getClass()
                                   .getSimpleName())
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
                .setEventType(event.getClass()
                                   .getSimpleName())
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
                .setEventType(event.getClass()
                                   .getSimpleName())
                .build();
        getBuilder().addEvent(logItem);
    }

    @Subscribe
    public void on(LoanBecameOverdue event) {
        final InventoryItemId inventoryItemId = event.getInventoryItemId();
        final Timestamp whenBecameOverdue = event.getWhenBecameOverdue();

        final ReaderEventLogItem logItem = ReaderEventLogItem
                .newBuilder()
                .setItemId(inventoryItemId)
                .setWhenEmitted(whenBecameOverdue)
                .setEventType(event.getClass()
                                   .getSimpleName())
                .build();
        getBuilder().addEvent(logItem);
    }

    @Subscribe
    public void on(LoanBecameShouldReturnSoon event) {
        final InventoryItemId inventoryItemId = event.getInventoryItemId();
        final Timestamp whenBecameShouldReturnSoon = event.getWhenBecameShouldReturnSoon();

        final ReaderEventLogItem logItem = ReaderEventLogItem
                .newBuilder()
                .setItemId(inventoryItemId)
                .setWhenEmitted(whenBecameShouldReturnSoon)
                .setEventType(event.getClass()
                                   .getSimpleName())
                .build();
        getBuilder().addEvent(logItem);
    }

    @Subscribe
    public void on(LoanPeriodExtended event) {
        final InventoryItemId inventoryItemId = event.getInventoryItemId();
        final Timestamp whenExtended = event.getWhenExtended();

        final ReaderEventLogItem logItem = ReaderEventLogItem
                .newBuilder()
                .setItemId(inventoryItemId)
                .setWhenEmitted(whenExtended)
                .setEventType(event.getClass()
                                   .getSimpleName())
                .build();
        getBuilder().addEvent(logItem);
    }

    @Subscribe
    public void on(BookLost event) {
        final InventoryItemId inventoryItemId = event.getInventoryItemId();
        final Timestamp whenReported = event.getWhenReported();

        final ReaderEventLogItem logItem = ReaderEventLogItem
                .newBuilder()
                .setItemId(inventoryItemId)
                .setWhenEmitted(whenReported)
                .setEventType(event.getClass()
                                   .getSimpleName())
                .build();
        getBuilder().addEvent(logItem);
    }
}
