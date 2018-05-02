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

import com.google.protobuf.Message;
import javaclasses.exlibris.Inventory;
import javaclasses.exlibris.InventoryItem;
import javaclasses.exlibris.UserId;
import javaclasses.exlibris.c.AppendInventory;
import javaclasses.exlibris.c.BookLost;
import javaclasses.exlibris.c.BorrowBook;
import javaclasses.exlibris.c.ReportLostBook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.spine.server.aggregate.AggregateMessageDispatcher.dispatchCommand;
import static javaclasses.exlibris.testdata.InventoryCommandFactory.appendInventoryInstance;
import static javaclasses.exlibris.testdata.InventoryCommandFactory.borrowBookInstance;
import static javaclasses.exlibris.testdata.InventoryCommandFactory.inventoryId;
import static javaclasses.exlibris.testdata.InventoryCommandFactory.reportLostBookInstance;
import static javaclasses.exlibris.testdata.InventoryCommandFactory.userEmailAddress1;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Paul Ageyev
 */
@DisplayName("ReportLostBook command should be interpreted by InventoryAggregate and")
public class ReportLostBookCommandTest extends InventoryCommandTest<ReportLostBook> {

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
    }

    @Test
    @DisplayName("produce BookLost event")
    void produceEvent() {
        borrowBook();

        final ReportLostBook reportLostBook = reportLostBookInstance();

        final List<? extends Message> messageList = dispatchCommand(aggregate,
                                                                    envelopeOf(reportLostBook));
        assertNotNull(aggregate.getId());
        assertEquals(1, messageList.size());
        assertEquals(BookLost.class, messageList.get(0)
                                                .getClass());

        final BookLost bookLost = (BookLost) messageList.get(0);

        assertEquals(inventoryId, bookLost.getInventoryId());
        assertEquals(userEmailAddress1, bookLost.getWhoLost()
                                                .getEmail());
    }

    @Test
    @DisplayName("set item status as lost and remove absent loan.")
    void reportLostBook() {
        borrowBook();

        final ReportLostBook reportLostBook = reportLostBookInstance();
        dispatchCommand(aggregate, envelopeOf(reportLostBook));

        final Inventory state = aggregate.getState();
        final InventoryItem lostItem = state.getInventoryItems(0);

        assertTrue(lostItem.getLost());
        assertFalse(lostItem.getBorrowed());
        assertEquals(UserId.getDefaultInstance(), lostItem.getUserId());
    }

    private void borrowBook() {
        final AppendInventory appendInventory = appendInventoryInstance();
        dispatchCommand(aggregate, envelopeOf(appendInventory));
        final BorrowBook borrowBook = borrowBookInstance();
        dispatchCommand(aggregate, envelopeOf(borrowBook));
    }
}
