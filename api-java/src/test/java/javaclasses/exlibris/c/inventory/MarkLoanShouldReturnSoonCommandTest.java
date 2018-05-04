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
import javaclasses.exlibris.LoanId;
import javaclasses.exlibris.c.AppendInventory;
import javaclasses.exlibris.c.BookBorrowed;
import javaclasses.exlibris.c.BorrowBook;
import javaclasses.exlibris.c.LoanBecameShouldReturnSoon;
import javaclasses.exlibris.c.MarkLoanShouldReturnSoon;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.spine.server.aggregate.AggregateMessageDispatcher.dispatchCommand;
import static javaclasses.exlibris.LoanStatus.LOAN_SOULD_RETURN_SOON;
import static javaclasses.exlibris.testdata.InventoryCommandFactory.appendInventoryInstance;
import static javaclasses.exlibris.testdata.InventoryCommandFactory.borrowBookInstance;
import static javaclasses.exlibris.testdata.InventoryCommandFactory.markLoanShouldReturnSoon;
import static javaclasses.exlibris.testdata.TestValues.INVENTORY_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Yegor Udovchenko
 */
@DisplayName("MarkLoanShouldReturnSoon command should be interpreted by InventoryAggregate and")
public class MarkLoanShouldReturnSoonCommandTest extends InventoryCommandTest<MarkLoanShouldReturnSoon> {

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
    }

    @Test
    @DisplayName("produce LoanBecameShouldReturnSoon event")
    void produceEvent() {
        final LoanId loanId = prepareLoan();

        final MarkLoanShouldReturnSoon markLoanShouldReturnSoon = markLoanShouldReturnSoon(loanId,
                                                                                           INVENTORY_ID);
        final List<? extends Message> messageList =
                dispatchCommand(aggregate, envelopeOf(markLoanShouldReturnSoon));

        assertEquals(1, messageList.size());
        assertEquals(LoanBecameShouldReturnSoon.class, messageList.get(0)
                                                                  .getClass());
        final LoanBecameShouldReturnSoon becameShouldReturnSoon =
                (LoanBecameShouldReturnSoon) messageList.get(0);
        assertEquals(loanId, becameShouldReturnSoon.getLoanId());
    }

    @Test
    @DisplayName("marks loan as should return soon")
    void markLoanPeriodAsOverdue() {
        final LoanId loanId = prepareLoan();

        final MarkLoanShouldReturnSoon markLoanShouldReturnSoon = markLoanShouldReturnSoon(loanId,
                                                                                           INVENTORY_ID);
        dispatchCommand(aggregate, envelopeOf(markLoanShouldReturnSoon));
        final Inventory state = aggregate.getState();

        assertTrue(state.getInventoryItems(0)
                        .getBorrowed());
        assertEquals(state.getLoans(0)
                          .getStatus(), LOAN_SOULD_RETURN_SOON);
    }

    @SuppressWarnings("Duplicates")
    private LoanId prepareLoan() {
        final AppendInventory appendInventory = appendInventoryInstance();
        dispatchCommand(aggregate, envelopeOf(appendInventory));
        final BorrowBook borrowBook = borrowBookInstance();
        final List<? extends Message> messages = dispatchCommand(aggregate, envelopeOf(borrowBook));
        final BookBorrowed bookBorrowed = (BookBorrowed) messages.get(0);
        return bookBorrowed.getLoanId();
    }
}
