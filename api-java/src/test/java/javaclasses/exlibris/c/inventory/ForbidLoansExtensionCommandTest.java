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
import javaclasses.exlibris.Loan;
import javaclasses.exlibris.UserId;
import javaclasses.exlibris.c.AppendInventory;
import javaclasses.exlibris.c.BorrowBook;
import javaclasses.exlibris.c.ForbidLoansExtension;
import javaclasses.exlibris.c.LoansExtensionForbidden;
import javaclasses.exlibris.testdata.InventoryCommandFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static io.spine.server.aggregate.AggregateMessageDispatcher.dispatchCommand;
import static javaclasses.exlibris.testdata.InventoryCommandFactory.forbidLoansExtensionInstance;
import static javaclasses.exlibris.testdata.InventoryCommandFactory.inventoryId;
import static javaclasses.exlibris.testdata.InventoryCommandFactory.userId;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Yegor Udovchenko
 */
@DisplayName("ForbidLoansExtension command should be interpreted by InventoryAggregate and")
public class ForbidLoansExtensionCommandTest extends InventoryCommandTest<ForbidLoansExtension> {
    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
        appendInventory();
        borrowBook();
    }

    @Test
    @DisplayName("produce LoansExtensionForbidden event")
    void produceEvent() {
        List<UserId> borrowers = Collections.singletonList(userId);
        final ForbidLoansExtension forbidLoansExtension = forbidLoansExtensionInstance(inventoryId,
                                                                                       borrowers);
        final List<? extends Message> messageList =
                dispatchCommand(aggregate, envelopeOf(forbidLoansExtension));

        assertEquals(1, messageList.size());
        assertEquals(LoansExtensionForbidden.class, messageList.get(0)
                                                               .getClass());
        final LoansExtensionForbidden loansExtensionForbidden =
                (LoansExtensionForbidden) messageList.get(0);
        final UserId borrower = loansExtensionForbidden.getBorrowers(0);
        assertEquals(userId, borrower);
    }

    @Test
    @DisplayName("change loan status to forbidden for extension")
    void changeLoanStatus() {
        final Inventory stateBefore = aggregate.getState();
        final Loan loan = stateBefore.getLoans(0);
        assertTrue(loan.getIsAllowedExtension());
        List<UserId> borrowers = Collections.singletonList(userId);
        final ForbidLoansExtension forbidLoansExtension = forbidLoansExtensionInstance(inventoryId,
                                                                                       borrowers);
        dispatchCommand(aggregate, envelopeOf(forbidLoansExtension));

        final Inventory stateAfter = aggregate.getState();
        final Loan updatedLoan = stateAfter.getLoans(0);
        assertFalse(updatedLoan.getIsAllowedExtension());
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
