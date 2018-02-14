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

package io.spine.javaclasses.exlibris.c.aggregate.definition;

import com.google.protobuf.Message;
import io.spine.javaclasses.exlibris.testdata.InventoryCommandFactory;
import javaclasses.exlibris.Inventory;
import javaclasses.exlibris.c.AppendInventory;
import javaclasses.exlibris.c.BookReturned;
import javaclasses.exlibris.c.BorrowBook;
import javaclasses.exlibris.c.ReturnBook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.spine.server.aggregate.AggregateMessageDispatcher.dispatchCommand;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Alexander Karpets
 */
public class ReturnBookCommandTest extends InventoryCommandTest<AppendInventory> {

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
    }

    private void appendInventory() {
        final AppendInventory appendInventory = InventoryCommandFactory.appendInventoryInstance();
        dispatchCommand(aggregate, envelopeOf(appendInventory));
    }

    private void borrowBook() {
        final BorrowBook borrowBook = InventoryCommandFactory.borrowBookInstance();
        dispatchCommand(aggregate, envelopeOf(borrowBook));
    }

    @Test
    void produceEvent() {
        appendInventory();
        borrowBook();
        final ReturnBook returnBook = InventoryCommandFactory.returnBookInstance();

        final List<? extends Message> messageList = dispatchCommand(aggregate,
                                                                    envelopeOf(returnBook));
        assertNotNull(aggregate.getId());
        assertEquals(2, messageList.size());
        assertEquals(BookReturned.class, messageList.get(0)
                                                    .getClass());

        final BookReturned bookReturned = (BookReturned) messageList.get(0);

        assertEquals(InventoryCommandFactory.inventoryId, bookReturned.getInventoryId());

    }

    @Test
    void returnBook() {
        appendInventory();
        final Inventory inventoryAppended = aggregate.getState();
        assertEquals(0, inventoryAppended.getLoansList()
                                         .size());

        borrowBook();
        final Inventory inventoryBorrowed = aggregate.getState();
        assertEquals(1, inventoryBorrowed.getLoansList()
                                         .size());

        final ReturnBook returnBook = InventoryCommandFactory.returnBookInstance();
        dispatchCommand(aggregate, envelopeOf(returnBook));

        final Inventory inventoryReturned = aggregate.getState();
        assertEquals(0, inventoryReturned.getLoansList()
                                         .size());
        assertTrue(inventoryReturned.getInventoryItemsList().get(0).getInLibrary());
        assertEquals(1, inventoryReturned.getInventoryItemsList().get(0).getInventoryItemId().getItemNumber());
    }
}
