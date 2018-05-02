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
import javaclasses.exlibris.Inventory;
import javaclasses.exlibris.LoanId;
import javaclasses.exlibris.UserId;
import javaclasses.exlibris.c.AppendInventory;
import javaclasses.exlibris.c.BorrowBook;
import javaclasses.exlibris.c.ExtendLoanPeriod;
import javaclasses.exlibris.c.ForbidLoansExtension;
import javaclasses.exlibris.c.LoanPeriodExtended;
import javaclasses.exlibris.c.MarkLoanOverdue;
import javaclasses.exlibris.c.MarkLoanShouldReturnSoon;
import javaclasses.exlibris.c.rejection.CannotExtendLoanPeriod;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static io.spine.server.aggregate.AggregateMessageDispatcher.dispatchCommand;
import static javaclasses.exlibris.testdata.InventoryCommandFactory.appendInventoryInstance;
import static javaclasses.exlibris.testdata.InventoryCommandFactory.borrowBookInstance;
import static javaclasses.exlibris.testdata.InventoryCommandFactory.extendLoanPeriodInstance;
import static javaclasses.exlibris.testdata.InventoryCommandFactory.forbidLoansExtensionInstance;
import static javaclasses.exlibris.testdata.InventoryCommandFactory.markLoanOverdue;
import static javaclasses.exlibris.testdata.InventoryCommandFactory.markLoanShouldReturnSoon;
import static javaclasses.exlibris.testdata.TestValues.INVENTORY_ID;
import static javaclasses.exlibris.testdata.TestValues.USER_ID;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Paul Ageyev
 * @author Yegor Udovchenko
 */
@DisplayName("ExtendLoanPeriod command should be interpreted by InventoryAggregate and")
public class ExtendLoanPeriodCommandTest extends InventoryCommandTest<ExtendLoanPeriod> {

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
    }

    @Test
    @DisplayName("produce LoanPeriodExtended event")
    void produceEvent() {
        borrowBook();
        final Inventory state = aggregate.getState();
        final LoanId loanId = state.getLoans(0)
                                   .getLoanId();
        makeLoanOverdue(loanId);

        final ExtendLoanPeriod extendLoanPeriod = extendLoanPeriodInstance(INVENTORY_ID,
                                                                           loanId,
                                                                           USER_ID);
        final List<? extends Message> messageList = dispatchCommand(aggregate,
                                                                    envelopeOf(extendLoanPeriod));
        assertEquals(1, messageList.size());
        assertEquals(LoanPeriodExtended.class, messageList.get(0)
                                                          .getClass());

        final LoanPeriodExtended loanExtended = (LoanPeriodExtended) messageList.get(0);

        assertEquals(INVENTORY_ID, loanExtended.getInventoryId());
        assertEquals(USER_ID, loanExtended.getUserId());
    }

    @Test
    @DisplayName("extend a loan period of overdue loan")
    void extendLoanPeriodOfOverdueLoan() {
        borrowBook();
        final Inventory state = aggregate.getState();
        final LoanId loanId = state.getLoans(0)
                                   .getLoanId();
        makeLoanOverdue(loanId);
        long secondsInTwoWeeks = 60 * 60 * 24 * 14;
        assertEquals(state.getLoans(0)
                          .getWhenDue()
                          .getSeconds(), state.getLoans(0)
                                              .getWhenTaken()
                                              .getSeconds() + secondsInTwoWeeks);
        long oldDueDate = state.getLoans(0)
                               .getWhenDue()
                               .getSeconds();
        final ExtendLoanPeriod extendLoanPeriod = extendLoanPeriodInstance(INVENTORY_ID,
                                                                           state.getLoans(0)
                                                                                .getLoanId(),
                                                                           USER_ID);

        dispatchCommand(aggregate, envelopeOf(extendLoanPeriod));
        final Inventory stateAfterExtension = aggregate.getState();
        assertEquals(oldDueDate + secondsInTwoWeeks, stateAfterExtension.getLoans(0)
                                                                        .getWhenDue()
                                                                        .getSeconds());
    }

    @Test
    @DisplayName("extend a loan period of should return soon loan")
    void extendLoanPeriodOfShouldReturnSoonLoan() {
        borrowBook();
        final Inventory state = aggregate.getState();
        final LoanId loanId = state.getLoans(0)
                                   .getLoanId();
        makeLoanShouldReturnSoon(loanId);
        long secondsInTwoWeeks = 60 * 60 * 24 * 14;
        assertEquals(state.getLoans(0)
                          .getWhenDue()
                          .getSeconds(), state.getLoans(0)
                                              .getWhenTaken()
                                              .getSeconds() + secondsInTwoWeeks);
        long oldDueDate = state.getLoans(0)
                               .getWhenDue()
                               .getSeconds();
        final ExtendLoanPeriod extendLoanPeriod = extendLoanPeriodInstance(INVENTORY_ID,
                                                                           state.getLoans(0)
                                                                                .getLoanId(),
                                                                           USER_ID);

        dispatchCommand(aggregate, envelopeOf(extendLoanPeriod));
        final Inventory stateAfterExtension = aggregate.getState();
        assertEquals(oldDueDate + secondsInTwoWeeks, stateAfterExtension.getLoans(0)
                                                                        .getWhenDue()
                                                                        .getSeconds());
    }

    @Test
    @DisplayName("throw CannotExtendLoanPeriod rejection upon " +
            "an attempt to extend loan period of non existing loan")
    void extendMissingLoan() {
        final LoanId fakeLoanId = LoanId.newBuilder()
                                        .setValue(0)
                                        .build();
        final ExtendLoanPeriod extendLoanPeriod = extendLoanPeriodInstance(INVENTORY_ID,
                                                                           fakeLoanId,
                                                                           USER_ID);
        final Throwable t = assertThrows(Throwable.class,
                                         () -> dispatchCommand(aggregate,
                                                               envelopeOf(extendLoanPeriod)));
        final Throwable cause = Throwables.getRootCause(t);
        assertThat(cause, instanceOf(CannotExtendLoanPeriod.class));
    }

    @Test
    @DisplayName("throw CannotExtendLoanPeriod rejection upon " +
            "an attempt to extend loan period of a recent loan")
    void extendRecentLoan() {
        borrowBook();
        final Inventory state = aggregate.getState();
        final LoanId loanId = state.getLoans(0)
                                   .getLoanId();
        final ExtendLoanPeriod extendLoanPeriod = extendLoanPeriodInstance(INVENTORY_ID,
                                                                           loanId,
                                                                           USER_ID);
        final Throwable t = assertThrows(Throwable.class,
                                         () -> dispatchCommand(aggregate,
                                                               envelopeOf(extendLoanPeriod)));
        final Throwable cause = Throwables.getRootCause(t);
        assertThat(cause, instanceOf(CannotExtendLoanPeriod.class));
    }

    @Test
    @DisplayName("throw CannotExtendLoanPeriod rejection upon " +
            "an attempt to extend loan which is forbidden for extension")
    void extendLoanNotAllowedForExtension() {
        borrowBook();
        final Inventory state = aggregate.getState();
        final UserId whoBorrowed = state.getLoans(0)
                                        .getWhoBorrowed();
        final LoanId loanId = state.getLoans(0)
                                   .getLoanId();
        List<UserId> borrowers = Collections.singletonList(whoBorrowed);
        final ForbidLoansExtension forbidLoansExtension = forbidLoansExtensionInstance(INVENTORY_ID,
                                                                                       borrowers);
        dispatchCommand(aggregate, envelopeOf(forbidLoansExtension));

        final ExtendLoanPeriod extendLoanPeriod = extendLoanPeriodInstance(INVENTORY_ID,
                                                                           loanId,
                                                                           USER_ID);
        final Throwable t = assertThrows(Throwable.class,
                                         () -> dispatchCommand(aggregate,
                                                               envelopeOf(extendLoanPeriod)));
        final Throwable cause = Throwables.getRootCause(t);
        assertThat(cause, instanceOf(CannotExtendLoanPeriod.class));
    }

    private void borrowBook() {
        final AppendInventory appendInventory = appendInventoryInstance();
        dispatchCommand(aggregate, envelopeOf(appendInventory));
        final BorrowBook borrowBook = borrowBookInstance();
        dispatchCommand(aggregate, envelopeOf(borrowBook));
    }

    private void makeLoanOverdue(LoanId loanId) {
        final MarkLoanOverdue markLoanOverdue = markLoanOverdue(loanId, INVENTORY_ID);
        dispatchCommand(aggregate, envelopeOf(markLoanOverdue));
    }

    private void makeLoanShouldReturnSoon(LoanId loanId) {
        final MarkLoanShouldReturnSoon markLoanOverdue = markLoanShouldReturnSoon(loanId,
                                                                                  INVENTORY_ID);
        dispatchCommand(aggregate, envelopeOf(markLoanOverdue));
    }
}
