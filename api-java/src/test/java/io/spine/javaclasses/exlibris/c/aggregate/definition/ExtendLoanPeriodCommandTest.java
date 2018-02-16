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

import javaclasses.exlibris.Inventory;
import javaclasses.exlibris.c.AppendInventory;
import javaclasses.exlibris.c.BorrowBook;
import javaclasses.exlibris.c.ExtendLoanPeriod;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.spine.javaclasses.exlibris.testdata.InventoryCommandFactory.appendInventoryInstance;
import static io.spine.javaclasses.exlibris.testdata.InventoryCommandFactory.borrowBookInstance;
import static io.spine.javaclasses.exlibris.testdata.InventoryCommandFactory.extendLoanPeriodInstance;
import static io.spine.javaclasses.exlibris.testdata.InventoryCommandFactory.inventoryItemId;
import static io.spine.javaclasses.exlibris.testdata.InventoryCommandFactory.userId;
import static io.spine.server.aggregate.AggregateMessageDispatcher.dispatchCommand;
import static io.spine.time.Time.getCurrentTime;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Paul Ageyev
 */
@DisplayName("ExtendLoanPeriod command should be interpreted by InventoryAggregate and")
public class ExtendLoanPeriodCommandTest extends InventoryCommandTest<ExtendLoanPeriod> {

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
    }

    @Test
    @DisplayName("extend loan period")
    void extendLoanPeriod() {

        dispatchAppendInventory();

        final BorrowBook borrowBook = borrowBookInstance(inventoryId, inventoryItemId, userId);

        dispatchCommand(aggregate, envelopeOf(borrowBook));
        Inventory state = aggregate.getState();

        assertTrue(state.getInventoryItems(0)
                        .getBorrowed());

        ExtendLoanPeriod extendLoanPeriod = extendLoanPeriodInstance(inventoryId, state.getLoans(0).getLoanId(), userId, getCurrentTime());

        dispatchCommand(aggregate, envelopeOf(extendLoanPeriod));

        state = aggregate.getState();

        assertEquals(state.getLoans(state.getLoansCount() - 1)
                          .getInventoryItemId(), inventoryItemId);

        assertEquals(state.getLoans(state.getLoansCount() - 1)
                          .getWhoBorrowed(), userId);

        assertEquals(state.getLoans(state.getLoansCount() - 1)
                          .getWhenDue(), getCurrentTime());

    }

    private void dispatchAppendInventory() {
        final AppendInventory appendInventory = appendInventoryInstance();
        dispatchCommand(aggregate, envelopeOf(appendInventory));
    }

}
