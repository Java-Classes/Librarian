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
import javaclasses.exlibris.Inventory;
import javaclasses.exlibris.InventoryItem;
import javaclasses.exlibris.InventoryItemId;
import javaclasses.exlibris.Loan;
import javaclasses.exlibris.UserId;
import javaclasses.exlibris.c.AppendInventory;
import javaclasses.exlibris.c.BookBorrowed;
import javaclasses.exlibris.c.BorrowBook;
import javaclasses.exlibris.c.ReservationBecameLoan;
import javaclasses.exlibris.c.ReserveBook;
import javaclasses.exlibris.c.SatisfyReservation;
import javaclasses.exlibris.c.rejection.BookAlreadyBorrowed;
import javaclasses.exlibris.c.rejection.NonAvailableBook;
import javaclasses.exlibris.testdata.InventoryCommandFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.spine.server.aggregate.AggregateMessageDispatcher.dispatchCommand;
import static javaclasses.exlibris.testdata.InventoryCommandFactory.appendInventoryInstance;
import static javaclasses.exlibris.testdata.InventoryCommandFactory.borrowBookInstance;
import static javaclasses.exlibris.testdata.InventoryCommandFactory.inventoryId;
import static javaclasses.exlibris.testdata.InventoryCommandFactory.inventoryItemId;
import static javaclasses.exlibris.testdata.InventoryCommandFactory.inventoryItemId2;
import static javaclasses.exlibris.testdata.InventoryCommandFactory.userId;
import static javaclasses.exlibris.testdata.InventoryCommandFactory.userId2;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Dmytry Dyachenko
 * @author Alexander Karpets
 * @author Paul Ageyev
 * @author Yegor Udovchenko
 */
@DisplayName("BorrowBook command should be interpreted by InventoryAggregate and")
public class BorrowBookCommandTest extends InventoryCommandTest<BorrowBook> {

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
    }

    @Test
    @DisplayName("produce BookBorrowed event")
    void produceBookBorrowedEvent() {
        dispatchAppendInventory();

        final BorrowBook borrowBook = borrowBookInstance(inventoryId, inventoryItemId, userId);
        final List<? extends Message> messageList = dispatchCommand(aggregate,
                                                                    envelopeOf(borrowBook));
        assertEquals(2, messageList.size());
        assertEquals(BookBorrowed.class, messageList.get(0)
                                                    .getClass());
        final BookBorrowed bookBorrowed = (BookBorrowed) messageList.get(0);
        final InventoryItemId borrowedItemId = bookBorrowed.getInventoryItemId();
        final UserId whoBorrowed = bookBorrowed.getWhoBorrowed();
        assertEquals(inventoryId, bookBorrowed.getInventoryId());
        assertEquals(inventoryItemId, borrowedItemId);
        assertEquals(userId, whoBorrowed);
    }

    @Test
    @DisplayName("change the inventory item state to borrowed.")
    void borrowBook() {
        dispatchAppendInventory();

        final BorrowBook borrowBook = borrowBookInstance(inventoryId, inventoryItemId, userId);
        dispatchCommand(aggregate, envelopeOf(borrowBook));
        final Inventory state = aggregate.getState();
        final InventoryItem borrowedItem = state.getInventoryItems(0);
        assertTrue(borrowedItem.getBorrowed());
        assertEquals(userId, borrowedItem.getUserId());
    }

    @Test
    @DisplayName("create the loan for the borrowed book and the specific user")
    void createLoan() {
        dispatchAppendInventory();

        final BorrowBook borrowBook = borrowBookInstance(inventoryId, inventoryItemId, userId);
        dispatchCommand(aggregate, envelopeOf(borrowBook));

        final Inventory state = aggregate.getState();
        assertEquals(1, state.getLoansCount());
        final Loan loan = state.getLoans(0);
        assertEquals(loan.getInventoryItemId(), inventoryItemId);
        assertEquals(loan.getWhoBorrowed(), userId);
    }

    @Test
    @DisplayName("produce ReservationBecameLoan event when borrowing is a consequence of reservation.")
    void produceReservationBecameLoanEvent() {
        dispatchReserveBook();
        dispatchAppendInventory();
        dispatchSatisfyReservation();

        final BorrowBook borrowBook = borrowBookInstance(inventoryId, inventoryItemId, userId);
        final List<? extends Message> messageList = dispatchCommand(aggregate,
                                                                    envelopeOf(borrowBook));
        assertEquals(2, messageList.size());
        assertEquals(ReservationBecameLoan.class, messageList.get(1)
                                                             .getClass());

        final ReservationBecameLoan becameLoan = (ReservationBecameLoan) messageList.get(1);
        assertEquals(userId, becameLoan.getUserId());
    }

    @Test
    @DisplayName("remove reservation when borrowing is a consequence of reservation.")
    void removeReservationWhenBecameLoan() {
        dispatchReserveBook();
        dispatchAppendInventory();
        dispatchSatisfyReservation();

        final BorrowBook borrowBook = borrowBookInstance(inventoryId, inventoryItemId, userId);
        dispatchCommand(aggregate, envelopeOf(borrowBook));

        final Inventory state = aggregate.getState();

        assertEquals(1, state.getLoansCount());
        assertEquals(0, state.getReservationsCount());
    }

    @Test
    @DisplayName("throw BookAlreadyBorrowed rejection upon " +
            "an attempt to borrow a book more than 1 book")
    void throwBookAlreadyBorrowed() {
        dispatchAppendInventoryTwice();

        final BorrowBook borrowBook = borrowBookInstance(inventoryId,
                                                         inventoryItemId, userId);

        dispatchCommand(aggregate, envelopeOf(borrowBook));

        final BorrowBook borrowOneMoreBook = borrowBookInstance(inventoryId,
                                                                inventoryItemId2, userId);

        final Throwable t = assertThrows(Throwable.class,
                                         () -> dispatchCommand(aggregate,
                                                               envelopeOf(borrowOneMoreBook)));
        final Throwable cause = Throwables.getRootCause(t);
        assertThat(cause, instanceOf(BookAlreadyBorrowed.class));
    }

    @Test
    @DisplayName("throw NonAvailableBook rejection upon " +
            "an attempt to borrow a book item that is already borrowed")
    void borrowingSomeonesBook() {
        dispatchAppendInventory();

        final BorrowBook borrowBookForSomeone = borrowBookInstance(inventoryId,
                                                                   inventoryItemId,
                                                                   userId);
        dispatchCommand(aggregate, envelopeOf(borrowBookForSomeone));

        final BorrowBook borrowSomeOnesBook = borrowBookInstance(inventoryId,
                                                                 inventoryItemId,
                                                                 userId2);

        final Throwable t = assertThrows(Throwable.class,
                                         () -> dispatchCommand(aggregate,
                                                               envelopeOf(borrowSomeOnesBook)));

        final Throwable cause = Throwables.getRootCause(t);
        assertThat(cause, instanceOf(NonAvailableBook.class));
    }

    @Test
    @DisplayName("throw NonAvailableBook rejection upon " +
            "an attempt to borrow a book with unsatisfied reservation")
    void borrowWithUnsatisfiedReservation() {
        dispatchReserveBook();
        dispatchAppendInventory();

        final BorrowBook borrowBook = borrowBookInstance(inventoryId,
                                                         inventoryItemId,
                                                         userId);
        final Throwable t = assertThrows(Throwable.class,
                                         () -> dispatchCommand(aggregate,
                                                               envelopeOf(borrowBook)));

        final Throwable cause = Throwables.getRootCause(t);
        assertThat(cause, instanceOf(NonAvailableBook.class));
    }

    @Test
    @DisplayName("throw NonAvailableBook rejection upon " +
            "an attempt to borrow a book that satisfies someone's reservation")
    void borrowNotPublicAvailableBookReservation() {
        dispatchReserveBook();
        dispatchAppendInventory();
        dispatchSatisfyReservation();

        final BorrowBook borrowBook = borrowBookInstance(inventoryId,
                                                         inventoryItemId,
                                                         userId2);
        final Throwable t = assertThrows(Throwable.class,
                                         () -> dispatchCommand(aggregate,
                                                               envelopeOf(borrowBook)));

        final Throwable cause = Throwables.getRootCause(t);
        assertThat(cause, instanceOf(NonAvailableBook.class));
    }

    private void dispatchReserveBook() {
        final ReserveBook reserveBook = InventoryCommandFactory.reserveBookInstance();
        dispatchCommand(aggregate, envelopeOf(reserveBook));
    }

    private void dispatchSatisfyReservation() {
        final SatisfyReservation satisfyReservation = InventoryCommandFactory.satisfyReservationInstance();
        dispatchCommand(aggregate, envelopeOf(satisfyReservation));
    }

    private void dispatchAppendInventory() {
        final AppendInventory appendInventory = appendInventoryInstance();
        dispatchCommand(aggregate, envelopeOf(appendInventory));
    }

    private void dispatchAppendInventoryTwice() {
        final AppendInventory appendInventory = appendInventoryInstance();
        dispatchCommand(aggregate, envelopeOf(appendInventory));
        final AppendInventory appendAnotherInventory = appendInventoryInstance(inventoryId,
                                                                               inventoryItemId2,
                                                                               userId);
        dispatchCommand(aggregate, envelopeOf(appendAnotherInventory));
    }
}
