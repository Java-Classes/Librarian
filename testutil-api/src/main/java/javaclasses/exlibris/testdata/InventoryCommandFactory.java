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

package javaclasses.exlibris.testdata;

import javaclasses.exlibris.*;
import javaclasses.exlibris.c.AllowLoansExtension;
import javaclasses.exlibris.c.AppendInventory;
import javaclasses.exlibris.c.BorrowBook;
import javaclasses.exlibris.c.BorrowOrReturnBook;
import javaclasses.exlibris.c.CancelReservation;
import javaclasses.exlibris.c.ExtendLoanPeriod;
import javaclasses.exlibris.c.ForbidLoansExtension;
import javaclasses.exlibris.c.LoanPeriodExtended;
import javaclasses.exlibris.c.MarkBookAsAvailable;
import javaclasses.exlibris.c.MarkLoanOverdue;
import javaclasses.exlibris.c.MarkLoanShouldReturnSoon;
import javaclasses.exlibris.c.MarkReservationExpired;
import javaclasses.exlibris.c.ReportLostBook;
import javaclasses.exlibris.c.ReserveBook;
import javaclasses.exlibris.c.ReturnBook;
import javaclasses.exlibris.c.SatisfyReservation;
import javaclasses.exlibris.c.WriteBookOff;

import java.util.List;

import static javaclasses.exlibris.testdata.TestValues.INVENTORY_ID;
import static javaclasses.exlibris.testdata.TestValues.INVENTORY_ITEM_ID_1;
import static javaclasses.exlibris.testdata.TestValues.LOAN;
import static javaclasses.exlibris.testdata.TestValues.REASON;
import static javaclasses.exlibris.testdata.TestValues.USER_ID;

/**
 * A factory of the task commands for the test needs.
 *
 * @author Alexander Karpets
 * @author Paul Ageyev
 */
public class InventoryCommandFactory {

    private InventoryCommandFactory() {
    }

    public static AppendInventory appendInventoryInstance() {

        final AppendInventory result = appendInventoryInstance(INVENTORY_ID, INVENTORY_ITEM_ID_1,
                                                               USER_ID);
        return result;
    }

    public static AppendInventory appendInventoryInstance(InventoryId inventoryId,
                                                          InventoryItemId inventoryItemId,
                                                          UserId userId) {

        AppendInventory result = AppendInventory.newBuilder()
                                                .setInventoryId(inventoryId)
                                                .setInventoryItemId(inventoryItemId)
                                                .setLibrarianId(userId)
                                                .build();
        return result;
    }

    public static BorrowBook borrowBookInstance() {

        return borrowBookInstance(INVENTORY_ID, INVENTORY_ITEM_ID_1, USER_ID);
    }

    public static BorrowBook borrowBookInstance(InventoryId inventoryId,
                                                InventoryItemId inventoryItemId,
                                                UserId userId) {
        final BorrowBook result = BorrowBook.newBuilder()
                                            .setInventoryId(inventoryId)
                                            .setInventoryItemId(inventoryItemId)
                                            .setUserId(userId)
                                            .build();
        return result;
    }

    public static MarkLoanOverdue markLoanOverdue(LoanId loanId, InventoryId inventoryId) {
        final MarkLoanOverdue result = MarkLoanOverdue.newBuilder()
                                                      .setLoanId(loanId)
                                                      .setInventoryId(inventoryId)
                                                      .build();
        return result;
    }

    public static MarkLoanShouldReturnSoon markLoanShouldReturnSoon(LoanId loanId,
                                                                    InventoryId inventoryId) {
        final MarkLoanShouldReturnSoon result = MarkLoanShouldReturnSoon.newBuilder()
                                                                        .setLoanId(loanId)
                                                                        .setInventoryId(inventoryId)
                                                                        .build();
        return result;
    }

    public static WriteBookOff writeBookOffInstance() {

        final WriteBookOff result = writeBookOffInstance(INVENTORY_ID, INVENTORY_ITEM_ID_1, USER_ID,
                                                         REASON);
        return result;
    }

    public static WriteBookOff writeBookOffInstance(InventoryId inventoryId,
                                                    InventoryItemId inventoryItemId,
                                                    UserId librarianId, WriteOffReason reason) {
        WriteBookOff result = WriteBookOff.newBuilder()
                                          .setInventoryId(inventoryId)
                                          .setInventoryItemId(inventoryItemId)
                                          .setLibrarianId(librarianId)
                                          .setWriteBookOffReason(reason)
                                          .build();
        return result;
    }

    public static ReserveBook reserveBookInstance() {
        final ReserveBook result = reserveBookInstance(USER_ID, INVENTORY_ID);
        return result;
    }

    public static ReserveBook reserveBookInstance(UserId userId, InventoryId inventoryId) {
        ReserveBook result = ReserveBook.newBuilder()
                                        .setInventoryId(inventoryId)
                                        .setUserId(userId)
                                        .build();
        return result;
    }

    public static SatisfyReservation satisfyReservationInstance() {
        final SatisfyReservation result = satisfyReservationInstance(USER_ID, INVENTORY_ID);
        return result;
    }

    public static SatisfyReservation satisfyReservationInstance(UserId userId,
                                                                InventoryId inventoryId) {
        SatisfyReservation result = SatisfyReservation.newBuilder()
                                                      .setInventoryId(inventoryId)
                                                      .setUserId(userId)
                                                      .build();
        return result;
    }

    public static MarkBookAsAvailable markBookAsAvailableInstance() {
        MarkBookAsAvailable result = MarkBookAsAvailable.newBuilder()
                                                        .setInventoryId(INVENTORY_ID)
                                                        .build();
        return result;
    }

    public static CancelReservation cancelReservationInstance() {

        final CancelReservation result = cancelReservationInstance(INVENTORY_ID, USER_ID);
        return result;
    }

    public static CancelReservation cancelReservationInstance(InventoryId inventoryId,
                                                              UserId userId) {
        CancelReservation result = CancelReservation.newBuilder()
                                                    .setInventoryId(inventoryId)
                                                    .setUserId(userId)
                                                    .build();
        return result;
    }

    public static ReturnBook returnBookInstance() {

        final ReturnBook result = returnBookInstance(INVENTORY_ID, INVENTORY_ITEM_ID_1, USER_ID);
        return result;
    }

    public static ReturnBook returnBookInstance(InventoryId inventoryId,
                                                InventoryItemId inventoryItemId, UserId userId) {
        ReturnBook result = ReturnBook.newBuilder()
                                      .setInventoryId(inventoryId)
                                      .setInventoryItemId(inventoryItemId)
                                      .setUserId(userId)
                                      .build();
        return result;
    }

    public static ReportLostBook reportLostBookInstance() {
        return reportLostBook(INVENTORY_ID, INVENTORY_ITEM_ID_1, USER_ID);
    }

    public static ReportLostBook reportLostBook(InventoryId inventoryId,
                                                InventoryItemId inventoryItemId, UserId userId) {

        ReportLostBook result = ReportLostBook.newBuilder()
                                              .setInventoryId(inventoryId)
                                              .setInventoryItemId(inventoryItemId)
                                              .setWhoLost(userId)
                                              .build();

        return result;
    }

    public static MarkReservationExpired markReservationExpiredInstance() {

        final MarkReservationExpired result = markReservationExpiredInstance(INVENTORY_ID,
                                                                             USER_ID);
        return result;
    }

    public static MarkReservationExpired markReservationExpiredInstance(
            InventoryId inventoryId,
            UserId userId) {
        MarkReservationExpired result = MarkReservationExpired.newBuilder()
                                                              .setInventoryId(inventoryId)
                                                              .setUserId(userId)
                                                              .build();
        return result;
    }

    public static ExtendLoanPeriod extendLoanPeriodInstance() {

        final ExtendLoanPeriod result = extendLoanPeriodInstance(INVENTORY_ID, LOAN.getLoanId(),
                                                                 USER_ID);

        return result;

    }

    public static ForbidLoansExtension forbidLoansExtensionInstance(InventoryId inventoryId,
                                                                    List<UserId> userIds) {

        final ForbidLoansExtension result = ForbidLoansExtension.newBuilder()
                                                                .setInventoryId(inventoryId)
                                                                .addAllBorrowers(userIds)
                                                                .build();
        return result;

    }

    public static AllowLoansExtension allowLoansExtensionInstance(InventoryId inventoryId,
                                                                  List<UserId> userIds) {

        final AllowLoansExtension result = AllowLoansExtension.newBuilder()
                                                              .setInventoryId(inventoryId)
                                                              .addAllBorrowers(userIds)
                                                              .build();
        return result;
    }

    public static ExtendLoanPeriod extendLoanPeriodInstance(InventoryId inventoryId, LoanId loanId,
                                                            UserId userId) {

        final ExtendLoanPeriod result = ExtendLoanPeriod.newBuilder()
                                                        .setInventoryId(inventoryId)
                                                        .setLoanId(loanId)
                                                        .setUserId(userId)
                                                        .build();

        return result;

    }

    public static LoanPeriodExtended loanPeriodExtended() {

        final LoanPeriodExtended result = loanPeriodExtended(INVENTORY_ID, LOAN.getLoanId(),
                                                             USER_ID);
        return result;
    }

    private static LoanPeriodExtended loanPeriodExtended(InventoryId inventoryId, LoanId loanId,
                                                         UserId userId) {
        LoanPeriodExtended result = LoanPeriodExtended.newBuilder()
                                                      .setInventoryId(inventoryId)
                                                      .setUserId(userId)
                                                      .setLoanId(loanId)
                                                      .build();
        return result;
    }

    public static BorrowOrReturnBook borrowOrReturnBookInstance(InventoryItemRecognizeToken token,
                                                                UserId userId) {
        final InventoryItemRecognizerId id = InventoryItemRecognizerId.newBuilder().setValue(token).build();
        final BorrowOrReturnBook result = BorrowOrReturnBook.newBuilder()
                                                            .setId(id)
                                                            .setUserId(userId)
                                                            .build();
        return result;
    }
}
