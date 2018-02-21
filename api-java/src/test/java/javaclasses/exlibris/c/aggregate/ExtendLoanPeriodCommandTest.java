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

import com.google.common.base.Throwables;
import javaclasses.exlibris.Inventory;
import javaclasses.exlibris.c.AppendInventory;
import javaclasses.exlibris.c.BorrowBook;
import javaclasses.exlibris.c.ExtendLoanPeriod;
import javaclasses.exlibris.c.ReserveBook;
import javaclasses.exlibris.c.rejection.CannotExtendLoanPeriod;
import javaclasses.exlibris.testdata.InventoryCommandFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.spine.server.aggregate.AggregateMessageDispatcher.dispatchCommand;
import static javaclasses.exlibris.testdata.InventoryCommandFactory.appendInventoryInstance;
import static javaclasses.exlibris.testdata.InventoryCommandFactory.borrowBookInstance;
import static javaclasses.exlibris.testdata.InventoryCommandFactory.extendLoanPeriodInstance;
import static javaclasses.exlibris.testdata.InventoryCommandFactory.inventoryItemId;
import static javaclasses.exlibris.testdata.InventoryCommandFactory.userId;
import static javaclasses.exlibris.testdata.InventoryCommandFactory.userId2;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
    @DisplayName("extend a loan period")
    void extendLoanPeriod() {

        dispatchAppendInventory();

        final BorrowBook borrowBook = borrowBookInstance(inventoryId, inventoryItemId, userId);

        dispatchCommand(aggregate, envelopeOf(borrowBook));
        final Inventory state = aggregate.getState();

        long secondsInTwoWeeks = 60 * 60 * 24 * 14;

        assertEquals(state.getLoans(0)
                          .getWhenDue()
                          .getSeconds(), state.getLoans(0)
                                              .getWhenTaken()
                                              .getSeconds() + secondsInTwoWeeks);
        long oldDueDate = state.getLoans(0)
                               .getWhenDue()
                               .getSeconds();
        final ExtendLoanPeriod extendLoanPeriod = extendLoanPeriodInstance(inventoryId,
                                                                           state.getLoans(0)
                                                                                .getLoanId(),
                                                                           userId);

        dispatchCommand(aggregate, envelopeOf(extendLoanPeriod));

        final Inventory state2 = aggregate.getState();

        assertEquals(oldDueDate + secondsInTwoWeeks, state2.getLoans(0)
                                                           .getWhenDue()
                                                           .getSeconds());

    }

    @Test
    @DisplayName("throw CannotExtendLoanPeriod rejection upon " +
            "an attempt to extend loan period if the book has already been reserved or borrowed")
    void notExtendLoanPeriod() {

        dispatchAppendInventory();

        final BorrowBook borrowBook = borrowBookInstance(inventoryId, inventoryItemId, userId);

        dispatchCommand(aggregate, envelopeOf(borrowBook));
        final Inventory state = aggregate.getState();

        long secondsInTwoWeeks = 60 * 60 * 24 * 14;

        assertEquals(state.getLoans(0)
                          .getWhenDue()
                          .getSeconds(), state.getLoans(0)
                                              .getWhenTaken()
                                              .getSeconds() + secondsInTwoWeeks);

        final ReserveBook reserveBook = InventoryCommandFactory.reserveBookInstance(userId2, inventoryId);
        dispatchCommand(aggregate, envelopeOf(reserveBook));

        final ExtendLoanPeriod extendLoanPeriod = extendLoanPeriodInstance(inventoryId,
                                                                           state.getLoans(0)
                                                                                .getLoanId(),
                                                                           userId);

        final Throwable t = assertThrows(Throwable.class,
                                         () -> dispatchCommand(aggregate,
                                                               envelopeOf(extendLoanPeriod)));

        final Throwable cause = Throwables.getRootCause(t);

        assertThat(cause, instanceOf(CannotExtendLoanPeriod.class));

    }

    private void dispatchAppendInventory() {
        final AppendInventory appendInventory = appendInventoryInstance();
        dispatchCommand(aggregate, envelopeOf(appendInventory));
    }

}
