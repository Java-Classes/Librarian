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
import javaclasses.exlibris.Reservation;
import javaclasses.exlibris.c.AppendInventory;
import javaclasses.exlibris.c.BorrowBook;
import javaclasses.exlibris.c.ReservationAdded;
import javaclasses.exlibris.c.ReserveBook;
import javaclasses.exlibris.c.rejection.BookAlreadyBorrowed;
import javaclasses.exlibris.c.rejection.BookAlreadyReserved;
import javaclasses.exlibris.c.rejection.CannotReserveAvailableBook;
import javaclasses.exlibris.testdata.InventoryCommandFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.spine.server.aggregate.AggregateMessageDispatcher.dispatchCommand;
import static javaclasses.exlibris.testdata.InventoryCommandFactory.appendInventoryInstance;
import static javaclasses.exlibris.testdata.InventoryCommandFactory.borrowBookInstance;
import static javaclasses.exlibris.testdata.TestValues.BOOK_ID;
import static javaclasses.exlibris.testdata.TestValues.INVENTORY_ID;
import static javaclasses.exlibris.testdata.TestValues.INVENTORY_ITEM_ID_1;
import static javaclasses.exlibris.testdata.TestValues.USER_ID;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Alexander Karpets
 */
@DisplayName("ReserveBook command should be interpreted by InventoryAggregate and")
public class ReserveBookCommandTest extends InventoryCommandTest<ReserveBook> {

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
    }

    @Test
    @DisplayName("produce ReservationAdded event")
    void produceEvent() {
        final ReserveBook reserveBook = InventoryCommandFactory.reserveBookInstance();

        final List<? extends Message> messageList = dispatchCommand(aggregate,
                                                                    envelopeOf(reserveBook));

        assertEquals(1, messageList.size());
        assertEquals(ReservationAdded.class, messageList.get(0)
                                                        .getClass());
        final ReservationAdded reservationAdded = (ReservationAdded) messageList.get(0);
        assertEquals(INVENTORY_ID, reservationAdded.getInventoryId());
        assertEquals(USER_ID, reservationAdded.getForWhomReserved());
    }

    @Test
    @DisplayName("add reservation to the list")
    void reserveBook() {
        final ReserveBook reserveBook = InventoryCommandFactory.reserveBookInstance();
        dispatchCommand(aggregate, envelopeOf(reserveBook));

        Inventory inventory = aggregate.getState();
        assertEquals(1, inventory.getReservationsCount());
        final Reservation reservation = inventory.getReservations(0);
        assertEquals(BOOK_ID, reservation.getBookId());
        assertEquals(USER_ID, reservation.getWhoReserved());
    }

    @Test
    @DisplayName("throw BookAlreadyReserved rejection upon " +
            "an attempt reserve the book that is already reserved")
    void reserveBookTwice() {
        final ReserveBook reserveBook = InventoryCommandFactory.reserveBookInstance();
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
        final BorrowBook borrowBook = borrowBookInstance(INVENTORY_ID, INVENTORY_ITEM_ID_1,
                                                         USER_ID);
        dispatchCommand(aggregate, envelopeOf(borrowBook));

        final ReserveBook reserveBook = InventoryCommandFactory.reserveBookInstance();
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

        final ReserveBook reserveBook = InventoryCommandFactory.reserveBookInstance();
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
