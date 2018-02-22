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
import javaclasses.exlibris.c.AppendInventory;
import javaclasses.exlibris.c.BookBorrowed;
import javaclasses.exlibris.c.BorrowBook;
import javaclasses.exlibris.c.ReserveBook;
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
import static javaclasses.exlibris.testdata.InventoryCommandFactory.inventoryItemId;
import static javaclasses.exlibris.testdata.InventoryCommandFactory.inventoryItemId2;
import static javaclasses.exlibris.testdata.InventoryCommandFactory.rfid;
import static javaclasses.exlibris.testdata.InventoryCommandFactory.userId;
import static javaclasses.exlibris.testdata.InventoryCommandFactory.userId2;
import static javaclasses.exlibris.testdata.InventoryCommandFactory.userId3;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Dmytry Dyachenko
 * @author Alexander Karpets
 * @author Paul Ageyev
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
    void produceEvent() {

        dispatchAppendInventory();

        final BorrowBook borrowBook = borrowBookInstance(inventoryId, inventoryItemId, userId);

        final List<? extends Message> messageList = dispatchCommand(aggregate,
                                                                    envelopeOf(borrowBook));
        assertEquals(2, messageList.size());
        assertEquals(BookBorrowed.class, messageList.get(0)
                                                    .getClass());
        final BookBorrowed bookBorrowed = (BookBorrowed) messageList.get(0);

        assertEquals(inventoryId, bookBorrowed.getInventoryId());
    }

    @Test
    @DisplayName("borrow the book")
    void borrowBook() {

        dispatchAppendInventory();

        final BorrowBook borrowBook = borrowBookInstance(inventoryId, inventoryItemId, userId);

        dispatchCommand(aggregate, envelopeOf(borrowBook));
        final Inventory state = aggregate.getState();

        assertTrue(state.getInventoryItems(0)
                        .getBorrowed());

    }

    @Test
    @DisplayName("create the loan for the borrowed book and the specific user")
    void createLoan() {

        dispatchAppendInventory();

        final BorrowBook borrowBook = borrowBookInstance(inventoryId, inventoryItemId, userId);

        dispatchCommand(aggregate, envelopeOf(borrowBook));
        final Inventory state = aggregate.getState();

        assertEquals(1, state.getLoansCount());

        assertEquals(state.getLoans(state.getLoansCount() - 1)
                          .getInventoryItemId(), inventoryItemId);

        assertEquals(state.getLoans(state.getLoansCount() - 1)
                          .getWhoBorrowed(), userId);

    }

    @Test
    @DisplayName("reservation became loan")
    void reservationBecameLoan() {

        dispatchAppendInventory();
        dispatchReserveBook();

        final BorrowBook borrowBook = borrowBookInstance(InventoryCommandFactory.inventoryId,
                                                         InventoryCommandFactory.inventoryItemId,
                                                         InventoryCommandFactory.userId);

        dispatchCommand(aggregate, envelopeOf(borrowBook));
        final Inventory state = aggregate.getState();

        assertEquals(1, state.getLoansCount());

        assertEquals(state.getLoans(state.getLoansCount() - 1)
                          .getInventoryItemId(), inventoryItemId);

        assertEquals(state.getLoans(state.getLoansCount() - 1)
                          .getWhoBorrowed(), userId);
        assertEquals(0, state.getReservationsList()
                             .size());

    }

    @Test
    @DisplayName("throw CannotBorrowBook rejection upon " +
            "an attempt to borrow a book that has already borrowed")
    void bookAlreadyBorrowed() {

        dispatchAppendInventory();

        final BorrowBook borrowBook = borrowBookInstance(InventoryCommandFactory.inventoryId,
                                                         inventoryItemId, userId);

        dispatchCommand(aggregate, envelopeOf(borrowBook));

        final Throwable t = assertThrows(Throwable.class,
                                         () -> dispatchCommand(aggregate,
                                                               envelopeOf(borrowBook)));
        final Throwable cause = Throwables.getRootCause(t);

        assertThat(cause, instanceOf(BookAlreadyBorrowed.class));
    }

    @Test
    @DisplayName("throw CannotBorrowBook rejection upon " +
            "an attempt to borrow a book that isn't available")
    void notAvailableBook() {

        dispatchAppendInventory();

        final BorrowBook borrowBook = borrowBookInstance(InventoryCommandFactory.inventoryId,
                                                         inventoryItemId, userId);

        dispatchCommand(aggregate, envelopeOf(borrowBook));

        final AppendInventory appendInventoryCommand = appendInventoryInstance(inventoryId,
                                                                               inventoryItemId2,
                                                                               userId,
                                                                               rfid);
        dispatchCommand(aggregate,
                        envelopeOf(appendInventoryCommand));

        final ReserveBook reserveBook = InventoryCommandFactory.reserveBookInstance(userId2,
                                                                                    InventoryCommandFactory.inventoryId);

        dispatchCommand(aggregate, envelopeOf(reserveBook));

        final BorrowBook borrowBook2 = borrowBookInstance(InventoryCommandFactory.inventoryId,
                                                          inventoryItemId, userId3);

        final Throwable t = assertThrows(Throwable.class,
                                         () -> dispatchCommand(aggregate,
                                                               envelopeOf(borrowBook2)));
        final Throwable cause = Throwables.getRootCause(t);

        assertThat(cause, instanceOf(NonAvailableBook.class));

    }

    private void dispatchReserveBook() {
        final ReserveBook reserveBook = InventoryCommandFactory.reserveBookInstance();
        dispatchCommand(aggregate, envelopeOf(reserveBook));
    }

    private void dispatchAppendInventory() {
        final AppendInventory appendInventory = appendInventoryInstance();
        dispatchCommand(aggregate, envelopeOf(appendInventory));
    }

}
