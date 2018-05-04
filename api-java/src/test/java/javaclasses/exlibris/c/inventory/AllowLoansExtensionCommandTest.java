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
import javaclasses.exlibris.c.AllowLoansExtension;
import javaclasses.exlibris.c.AppendInventory;
import javaclasses.exlibris.c.BorrowBook;
import javaclasses.exlibris.c.ForbidLoansExtension;
import javaclasses.exlibris.c.LoansExtensionAllowed;
import javaclasses.exlibris.testdata.InventoryCommandFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static io.spine.server.aggregate.AggregateMessageDispatcher.dispatchCommand;
import static javaclasses.exlibris.testdata.InventoryCommandFactory.allowLoansExtensionInstance;
import static javaclasses.exlibris.testdata.InventoryCommandFactory.forbidLoansExtensionInstance;
import static javaclasses.exlibris.testdata.InventoryCommandFactory.inventoryId;
import static javaclasses.exlibris.testdata.InventoryCommandFactory.userId;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Yegor Udovchenko
 */
@DisplayName("AllowLoansExtension command should be interpreted by InventoryAggregate and")
public class AllowLoansExtensionCommandTest extends InventoryCommandTest<AllowLoansExtension> {
    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
        appendInventory();
        borrowBook();
        forbidLoansExtension();
    }

    @Test
    @DisplayName("produce LoansExtensionAllowed event")
    void produceEvent() {
        final AllowLoansExtension allowLoansExtension =
                allowLoansExtensionInstance(inventoryId, Collections.singletonList(userId));
        final List<? extends Message> messageList =
                dispatchCommand(aggregate, envelopeOf(allowLoansExtension));

        assertEquals(1, messageList.size());
        assertEquals(LoansExtensionAllowed.class, messageList.get(0)
                                                             .getClass());
        final LoansExtensionAllowed loansExtensionAllowed =
                (LoansExtensionAllowed) messageList.get(0);
        final UserId borrower = loansExtensionAllowed.getBorrowers(0);
        assertEquals(userId, borrower);
    }

    @Test
    @DisplayName("change loan status to allowed for extension")
    void changeLoanStatus() {
        final Inventory stateBefore = aggregate.getState();
        final Loan loan = stateBefore.getLoans(0);
        assertFalse(loan.getIsAllowedExtension());
        final AllowLoansExtension allowLoansExtension =
                allowLoansExtensionInstance(inventoryId, Collections.singletonList(userId));
        dispatchCommand(aggregate, envelopeOf(allowLoansExtension));

        final Inventory stateAfter = aggregate.getState();
        final Loan updatedLoan = stateAfter.getLoans(0);
        assertTrue(updatedLoan.getIsAllowedExtension());
    }

    private void appendInventory() {
        final AppendInventory appendInventory = InventoryCommandFactory.appendInventoryInstance();
        dispatchCommand(aggregate, envelopeOf(appendInventory));
    }

    private void borrowBook() {
        final BorrowBook borrowBook = InventoryCommandFactory.borrowBookInstance();
        dispatchCommand(aggregate, envelopeOf(borrowBook));
    }

    private void forbidLoansExtension() {
        List<UserId> borrowers = Collections.singletonList(userId);
        final ForbidLoansExtension forbidLoansExtension = forbidLoansExtensionInstance(inventoryId,
                                                                                       borrowers);
        dispatchCommand(aggregate, envelopeOf(forbidLoansExtension));
    }
}
