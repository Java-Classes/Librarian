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

package javaclasses.exlibris.c.inventory;

import com.google.common.base.Throwables;
import com.google.protobuf.Message;
import javaclasses.exlibris.BookId;
import javaclasses.exlibris.Inventory;
import javaclasses.exlibris.Loan;
import javaclasses.exlibris.Reservation;
import javaclasses.exlibris.c.AppendInventory;
import javaclasses.exlibris.c.BorrowBook;
import javaclasses.exlibris.c.ForbidLoansExtension;
import javaclasses.exlibris.c.ReservationAdded;
import javaclasses.exlibris.c.ReserveBook;
import javaclasses.exlibris.c.rejection.BookAlreadyBorrowed;
import javaclasses.exlibris.c.rejection.BookAlreadyReserved;
import javaclasses.exlibris.c.rejection.CannotReserveAvailableBook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static io.spine.server.aggregate.AggregateMessageDispatcher.dispatchCommand;
import static javaclasses.exlibris.testdata.InventoryCommandFactory.appendInventoryInstance;
import static javaclasses.exlibris.testdata.InventoryCommandFactory.bookId;
import static javaclasses.exlibris.testdata.InventoryCommandFactory.borrowBookInstance;
import static javaclasses.exlibris.testdata.InventoryCommandFactory.forbidLoansExtensionInstance;
import static javaclasses.exlibris.testdata.InventoryCommandFactory.inventoryId;
import static javaclasses.exlibris.testdata.InventoryCommandFactory.inventoryItemId;
import static javaclasses.exlibris.testdata.InventoryCommandFactory.reserveBookInstance;
import static javaclasses.exlibris.testdata.InventoryCommandFactory.userId;
import static javaclasses.exlibris.testdata.InventoryCommandFactory.userId2;
import static javaclasses.exlibris.testdata.InventoryCommandFactory.userId3;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Alexander Karpets
 */
@DisplayName("ReserveBook command should be interpreted by InventoryAggregate and")
public class ReserveBookCommandTest extends InventoryCommandTest<ReserveBook> {

    private final int LOAN_PERIOD = 60 * 60 * 24 * 14;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
    }

    @Test
    @DisplayName("produce ReservationAdded event")
    void produceEvent() {
        final ReserveBook reserveBook = reserveBookInstance();

        final List<? extends Message> messageList = dispatchCommand(aggregate,
                                                                    envelopeOf(reserveBook));

        assertEquals(1, messageList.size());
        assertEquals(ReservationAdded.class, messageList.get(0)
                                                        .getClass());
        final ReservationAdded reservationAdded = (ReservationAdded) messageList.get(0);
        assertEquals(inventoryId, reservationAdded.getInventoryId());
        assertEquals(userId, reservationAdded.getForWhomReserved());
    }

    @Test
    @DisplayName("add reservation to the list")
    void reserveBook() {
        final ReserveBook reserveBook = reserveBookInstance();
        dispatchCommand(aggregate, envelopeOf(reserveBook));

        Inventory inventory = aggregate.getState();
        assertEquals(1, inventory.getReservationsCount());
        final Reservation reservation = inventory.getReservations(0);
        assertEquals(bookId, reservation.getBookId());
        assertEquals(userId, reservation.getWhoReserved());
    }

    @Test
    @DisplayName("calculate when expected time.")
    void reserveCoupleTimes() {
        final AppendInventory appendInventory = appendInventoryInstance();
        dispatchCommand(aggregate, envelopeOf(appendInventory));

        final BorrowBook borrowBook = borrowBookInstance();
        dispatchCommand(aggregate, envelopeOf(borrowBook));

        final ReserveBook reserveBook2 = reserveBookInstance(userId2, inventoryId);
        dispatchCommand(aggregate, envelopeOf(reserveBook2));

        final ForbidLoansExtension forbidLoansExtension =
                forbidLoansExtensionInstance(inventoryId, Collections.singletonList(userId));
        dispatchCommand(aggregate, envelopeOf(forbidLoansExtension));

        final ReserveBook reserveBook3 = reserveBookInstance(userId3, inventoryId);
        final List<? extends Message> messageList = dispatchCommand(aggregate,
                                                                    envelopeOf(reserveBook3));

        final ReservationAdded reservationAddedEvent = (ReservationAdded) messageList.get(0);

        Inventory inventory = aggregate.getState();
        assertEquals(2, inventory.getReservationsCount());

        final Loan loan = inventory.getLoans(0);
        final long expectedTime = loan.getWhenDue()
                                      .getSeconds() + LOAN_PERIOD;
        assertEquals(expectedTime, reservationAddedEvent.getWhenExpected()
                                                        .getSeconds());
    }

    @Test
    @DisplayName("throw BookAlreadyReserved rejection upon " +
            "an attempt reserve the book that is already reserved")
    void reserveBookTwice() {
        final ReserveBook reserveBook = reserveBookInstance();
        dispatchCommand(aggregate, envelopeOf(reserveBook));

        final Throwable t = assertThrows(Throwable.class,
                                         () -> dispatchCommand(aggregate,
                                                               envelopeOf(reserveBook)));
        final Throwable cause = Throwables.getRootCause(t);

        assertThat(cause, instanceOf(BookAlreadyReserved.class));

        final BookAlreadyReserved rejection = (BookAlreadyReserved) cause;
        final BookId actualBookId = rejection.getMessageThrown()
                                             .getInventoryId()
                                             .getBookId();
        assertEquals(reserveBook.getInventoryId()
                                .getBookId(), actualBookId);
    }

    @Test
    @DisplayName("throw BookAlreadyBorrowed rejection upon " +
            "an attempt reserve the book that is already borrowed")
    void notReserveBorrowedBook() {
        final AppendInventory appendInventory = appendInventoryInstance();
        dispatchCommand(aggregate, envelopeOf(appendInventory));
        final BorrowBook borrowBook = borrowBookInstance(inventoryId,
                                                         inventoryItemId,
                                                         userId);
        dispatchCommand(aggregate, envelopeOf(borrowBook));

        final ReserveBook reserveBook = reserveBookInstance();
        final Throwable t = assertThrows(Throwable.class,
                                         () -> dispatchCommand(aggregate,
                                                               envelopeOf(reserveBook)));
        final Throwable cause = Throwables.getRootCause(t);

        assertThat(cause, instanceOf(BookAlreadyBorrowed.class));

        final BookAlreadyBorrowed rejection = (BookAlreadyBorrowed) cause;
        final BookId actualBookId = rejection.getMessageThrown()
                                             .getInventoryId()
                                             .getBookId();
        assertEquals(reserveBook.getInventoryId()
                                .getBookId(), actualBookId);
    }

    @Test
    @DisplayName("throw CannotReserveAvailableBook rejection upon " +
            "an attempt reserve the book that is available for borrowing")
    void notReserveAvailableBook() {
        final AppendInventory appendInventory = appendInventoryInstance();
        dispatchCommand(aggregate, envelopeOf(appendInventory));

        final ReserveBook reserveBook = reserveBookInstance();
        final Throwable t = assertThrows(Throwable.class,
                                         () -> dispatchCommand(aggregate,
                                                               envelopeOf(reserveBook)));
        final Throwable cause = Throwables.getRootCause(t);

        assertThat(cause, instanceOf(CannotReserveAvailableBook.class));

        final CannotReserveAvailableBook rejection = (CannotReserveAvailableBook) cause;
        final BookId actualBookId = rejection.getMessageThrown()
                                             .getInventoryId()
                                             .getBookId();
        assertEquals(reserveBook.getInventoryId()
                                .getBookId(), actualBookId);
    }
}
