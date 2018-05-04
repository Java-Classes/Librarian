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

package javaclasses.exlibris.c.procman;

import io.spine.core.Command;
import javaclasses.exlibris.Loan;
import javaclasses.exlibris.LoanId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.spine.protobuf.TypeConverter.toMessage;
import static javaclasses.exlibris.testdata.InventoryCommandFactory.markLoanOverdue;
import static javaclasses.exlibris.testdata.TestValues.INVENTORY_ID;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("LoansExtensionProcman should")
public class LoansExtensionProcmanTest extends ProcessManagerTest {
    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
    }

    @Test
    @DisplayName("react on ReservationAdded event and make first loan not allowed for extension.")
    void reactOnReservationAddedMakesFirstLoanNotAllowedForExtension() {
        commandBus.post(appendInventoryItem1, observer);
        commandBus.post(borrowBookUser1Item1, observer);
        commandBus.post(reserveBookUser2, observer);

        final LoanId loanId = getAggregateState().getLoans(0)
                                                 .getLoanId();
        final Command markLoanOverdueUser1Item1 =
                requestFactory.createCommand(toMessage(markLoanOverdue(loanId, INVENTORY_ID)));
        commandBus.post(markLoanOverdueUser1Item1, observer);
        assertFalse(inventoryRejectionsSubscriber.wasCalled());

        commandBus.post(extendLoanPeriodUser1Item1, observer);
        assertTrue(inventoryRejectionsSubscriber.wasCalled());
    }

    @Test
    @DisplayName("react on BookReadyToPickup event and make loans allowed for extension if necessary.")
    void reactOnBookReturnedMakesAvailable() {
        commandBus.post(appendInventoryItem1, observer);
        commandBus.post(appendInventoryItem2, observer);
        commandBus.post(borrowBookUser1Item1, observer);
        commandBus.post(borrowBookUser2Item2, observer);
        commandBus.post(reserveBookUser3, observer);

        final Loan loanUser1Item1 = getAggregateState().getLoans(0);
        final Loan loanUser2Item2 = getAggregateState().getLoans(1);
        assertFalse(loanUser1Item1.getIsAllowedExtension());
        assertTrue(loanUser2Item2.getIsAllowedExtension());

        commandBus.post(returnBookUser2Item2, observer);
        final Loan loanUser1Item1After = getAggregateState().getLoans(0);
        assertTrue(loanUser1Item1After.getIsAllowedExtension());
        assertFalse(inventoryRejectionsSubscriber.wasCalled());
    }

    @Test
    @DisplayName("react on ReservationCanceled event and make loans allowed for extension if necessary.")
    void reactOnUnsatisfiedReservationCanceledEventAndAllowLoansExtension() {
        commandBus.post(appendInventoryItem1, observer);
        commandBus.post(borrowBookUser1Item1, observer);
        commandBus.post(reserveBookUser2, observer);

        final Loan loanUser1Item1 = getAggregateState().getLoans(0);
        assertFalse(loanUser1Item1.getIsAllowedExtension());

        commandBus.post(cancelReservationUser2, observer);
        final Loan loanUser1Item1After = getAggregateState().getLoans(0);
        assertTrue(loanUser1Item1After.getIsAllowedExtension());
        assertFalse(inventoryRejectionsSubscriber.wasCalled());
    }
}
