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
import javaclasses.exlibris.InventoryItem;
import javaclasses.exlibris.InventoryItemId;
import javaclasses.exlibris.Isbn62;
import javaclasses.exlibris.c.AppendInventory;
import javaclasses.exlibris.c.BookReturned;
import javaclasses.exlibris.c.BorrowBook;
import javaclasses.exlibris.c.ReturnBook;
import javaclasses.exlibris.c.rejection.CannotReturnMissingBook;
import javaclasses.exlibris.c.rejection.CannotReturnNonBorrowedBook;
import javaclasses.exlibris.testdata.InventoryCommandFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.spine.server.aggregate.AggregateMessageDispatcher.dispatchCommand;
import static javaclasses.exlibris.testdata.TestValues.INVENTORY_ID;
import static javaclasses.exlibris.testdata.TestValues.USER_ID;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Alexander Karpets
 */
@DisplayName("ReturnBook command should be interpreted by InventoryAggregate and")
public class ReturnBookCommandTest extends InventoryCommandTest<AppendInventory> {

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
    }

    @Test
    @DisplayName("produce BookReturned event")
    void produceEvent() {
        appendInventory();
        borrowBook();

        final ReturnBook returnBook = InventoryCommandFactory.returnBookInstance();
        final List<? extends Message> messageList = dispatchCommand(aggregate,
                                                                    envelopeOf(returnBook));
        assertNotNull(aggregate.getId());
        assertEquals(1, messageList.size());
        assertEquals(BookReturned.class, messageList.get(0)
                                                    .getClass());

        final BookReturned bookReturned = (BookReturned) messageList.get(0);
        assertEquals(INVENTORY_ID, bookReturned.getInventoryId());
    }

    @Test
    @DisplayName("set return book item as in library and remove the loan")
    void returnBookWithoutReservation() {
        appendInventory();
        borrowBook();
        final Inventory inventory = aggregate.getState();
        assertEquals(1, inventory.getLoansCount());

        final ReturnBook returnBook = InventoryCommandFactory.returnBookInstance();
        dispatchCommand(aggregate, envelopeOf(returnBook));

        final Inventory updatedInventory = aggregate.getState();
        assertEquals(0, updatedInventory.getLoansCount());
        final InventoryItem returnedItem = updatedInventory.getInventoryItems(0);
        assertTrue(returnedItem.getInLibrary());
        assertFalse(returnedItem.getLost());
        assertFalse(returnedItem.getBorrowed());
    }

    @Test
    @DisplayName("throw CannotReturnNonBorrowedBook rejection upon " +
            "an attempt to return non borrowed book")
    void throwsCannotReturnNonBorrowedBook() {
        appendInventory();

        final ReturnBook returnBook = InventoryCommandFactory.returnBookInstance();

        final Throwable t = assertThrows(Throwable.class,
                                         () -> dispatchCommand(aggregate,
                                                               envelopeOf(returnBook)));
        final Throwable cause = Throwables.getRootCause(t);
        assertThat(cause, instanceOf(CannotReturnNonBorrowedBook.class));
    }

    @Test
    @DisplayName("throw CannotReturnMissingBook rejection upon " +
            "an attempt to return missing book")
    void throwsCannotReturnMissingBook() {
        appendInventory();
        borrowBook();
        final InventoryItemId fakeItemId =
                InventoryItemId.newBuilder()
                               .setBookId(BookId.newBuilder()
                                                .setIsbn62(Isbn62.newBuilder()
                                                                 .setValue("123456780")
                                                                 .build())
                                                .build())
                               .build();

        final ReturnBook returnBook = InventoryCommandFactory.returnBookInstance(INVENTORY_ID,
                                                                                 fakeItemId,
                                                                                 USER_ID);
        final Throwable t = assertThrows(Throwable.class,
                                         () -> dispatchCommand(aggregate,
                                                               envelopeOf(returnBook)));
        final Throwable cause = Throwables.getRootCause(t);
        assertThat(cause, instanceOf(CannotReturnMissingBook.class));
    }

    private void appendInventory() {
        final AppendInventory appendInventory = InventoryCommandFactory.appendInventoryInstance();
        dispatchCommand(aggregate, envelopeOf(appendInventory));
    }

    private void borrowBook() {
        final BorrowBook borrowBook = InventoryCommandFactory.borrowBookInstance();
        dispatchCommand(aggregate, envelopeOf(borrowBook));
    }
}
