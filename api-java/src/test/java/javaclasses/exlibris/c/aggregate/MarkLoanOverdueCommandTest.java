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

package javaclasses.exlibris.c.aggregate;

import com.google.protobuf.Message;
import javaclasses.exlibris.Inventory;
import javaclasses.exlibris.LoanId;
import javaclasses.exlibris.c.AppendInventory;
import javaclasses.exlibris.c.BookBorrowed;
import javaclasses.exlibris.c.BorrowBook;
import javaclasses.exlibris.c.LoanBecameOverdue;
import javaclasses.exlibris.c.MarkLoanOverdue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.spine.server.aggregate.AggregateMessageDispatcher.dispatchCommand;
import static javaclasses.exlibris.testdata.InventoryCommandFactory.appendInventoryInstance;
import static javaclasses.exlibris.testdata.InventoryCommandFactory.borrowBookInstance;
import static javaclasses.exlibris.testdata.InventoryCommandFactory.markLoanOverdue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Dmytry Dyachenko
 */
@DisplayName("MarkLoanOverdue command should be interpreted by InventoryAggregate and")
public class MarkLoanOverdueCommandTest extends InventoryCommandTest<MarkLoanOverdue> {

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
    }

    @Test
    @DisplayName("produce LoanBecameOverdue event")
    void produceEvent() {
        dispatchAppendInventory();
        final LoanId eventLoanId = dispatchBorrowBookAndReturnLoanId();

        final MarkLoanOverdue markLoanOverdue = markLoanOverdue(eventLoanId, inventoryId);

        final List<? extends Message> messageList = dispatchCommand(aggregate,
                                                                    envelopeOf(markLoanOverdue));
        assertEquals(1, messageList.size());
        assertEquals(LoanBecameOverdue.class, messageList.get(0)
                                                         .getClass());
        final LoanBecameOverdue loanBecameOverdue = (LoanBecameOverdue) messageList.get(0);

        assertEquals(eventLoanId, loanBecameOverdue.getLoanId());

    }

    @Test
    @DisplayName("marks loan as overdue")
    void markLoanPeriodAsOverdue() {
        dispatchAppendInventory();
        final LoanId eventLoanId = dispatchBorrowBookAndReturnLoanId();

        final MarkLoanOverdue markLoanOverdue = markLoanOverdue(eventLoanId, inventoryId);

        dispatchCommand(aggregate, envelopeOf(markLoanOverdue));
        final Inventory state = aggregate.getState();

        assertTrue(state.getInventoryItems(0)
                        .getBorrowed());

        assertTrue(state.getLoans(0)
                        .getOverdue());

    }

    private void dispatchAppendInventory() {
        final AppendInventory appendInventory = appendInventoryInstance();
        dispatchCommand(aggregate, envelopeOf(appendInventory));
    }

    private LoanId dispatchBorrowBookAndReturnLoanId() {
        final BorrowBook borrowBook = borrowBookInstance();

        final List<? extends Message> messages = dispatchCommand(aggregate, envelopeOf(borrowBook));
        final BookBorrowed bookBorrowed = (BookBorrowed) messages.get(0);
        return bookBorrowed.getLoanId();
    }

}
