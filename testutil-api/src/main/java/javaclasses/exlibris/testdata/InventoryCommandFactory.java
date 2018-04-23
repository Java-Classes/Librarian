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

import io.spine.net.EmailAddress;
import javaclasses.exlibris.BookId;
import javaclasses.exlibris.InventoryId;
import javaclasses.exlibris.InventoryItemId;
import javaclasses.exlibris.Isbn62;
import javaclasses.exlibris.Loan;
import javaclasses.exlibris.LoanId;
import javaclasses.exlibris.Rfid;
import javaclasses.exlibris.UserId;
import javaclasses.exlibris.WriteOffReason;
import javaclasses.exlibris.c.AppendInventory;
import javaclasses.exlibris.c.BorrowBook;
import javaclasses.exlibris.c.CancelReservation;
import javaclasses.exlibris.c.ExtendLoanPeriod;
import javaclasses.exlibris.c.LoanPeriodExtended;
import javaclasses.exlibris.c.MarkLoanOverdue;
import javaclasses.exlibris.c.MarkReservationExpired;
import javaclasses.exlibris.c.ReportLostBook;
import javaclasses.exlibris.c.ReserveBook;
import javaclasses.exlibris.c.ReturnBook;
import javaclasses.exlibris.c.WriteBookOff;

import static io.spine.time.Time.getCurrentTime;

/**
 * A factory of the task commands for the test needs.
 *
 * @author Alexander Karpets
 * @author Paul Ageyev
 */
public class InventoryCommandFactory {

    public static final Isbn62 isbn62 = Isbn62.newBuilder()
                                              .setValue("0201485672")
                                              .build();

    public static final EmailAddress userEmailAddress1 = EmailAddress.newBuilder()
                                                                     .setValue(
                                                                             "petr@gmail.com")
                                                                     .build();

    private static final EmailAddress userEmailAddress2 = EmailAddress.newBuilder()
                                                                      .setValue(
                                                                              "petr2@gmail.com")
                                                                      .build();

    private static final EmailAddress userEmailAddress3 = EmailAddress.newBuilder()
                                                                      .setValue(
                                                                              "petr3@gmail.com")
                                                                      .build();

    public static final BookId bookId = BookId.newBuilder()
                                              .setIsbn62(isbn62)
                                              .build();
    public static final InventoryId inventoryId = InventoryId.newBuilder()
                                                             .setBookId(bookId)
                                                             .build();
    public static final InventoryItemId inventoryItemId = InventoryItemId.newBuilder()
                                                                         .setBookId(bookId)
                                                                         .setItemNumber(1)
                                                                         .build();

    public static final InventoryItemId inventoryItemId2 = InventoryItemId.newBuilder()
                                                                          .setBookId(bookId)
                                                                          .setItemNumber(2)
                                                                          .build();
    public static final UserId userId = UserId.newBuilder()
                                              .setEmail(userEmailAddress1)
                                              .build();

    public static final UserId userId2 = UserId.newBuilder()
                                               .setEmail(userEmailAddress2)
                                               .build();

    public static final UserId userId3 = UserId.newBuilder()
                                               .setEmail(userEmailAddress3)
                                               .build();

    public static final Rfid rfid = Rfid.newBuilder()
                                        .setValue("4321")
                                        .build();

    private static final WriteOffReason reason = WriteOffReason.newBuilder()
                                                               .setOutdated(true)
                                                               .build();

    public static final Loan loan = Loan.newBuilder()
                                        .setLoanId(LoanId.newBuilder()
                                                         .setValue(1))
                                        .setInventoryItemId(inventoryItemId)
                                        .setWhenDue(getCurrentTime())
                                        .build();

    private InventoryCommandFactory() {
    }

    public static AppendInventory appendInventoryInstance() {

        final AppendInventory result = appendInventoryInstance(inventoryId, inventoryItemId, userId,
                                                               rfid);
        return result;
    }

    public static AppendInventory appendInventoryInstance(InventoryId inventoryId,
                                                          InventoryItemId inventoryItemId,
                                                          UserId userId, Rfid rfid) {

        AppendInventory result = AppendInventory.newBuilder()
                                                .setInventoryId(inventoryId)
                                                .setInventoryItemId(inventoryItemId)
                                                .setLibrarianId(userId)
                                                .setRfid(rfid)

                                                .build();
        return result;
    }

    public static BorrowBook borrowBookInstance() {

        return borrowBookInstance(inventoryId, inventoryItemId, userId);
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

    public static WriteBookOff writeBookOffInstance() {

        final WriteBookOff result = writeBookOffInstance(inventoryId, inventoryItemId, userId,
                                                         reason);
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

        final ReserveBook result = reserveBookInstance(userId, inventoryId);
        return result;
    }

    public static ReserveBook reserveBookInstance(UserId userId, InventoryId inventoryId) {
        ReserveBook result = ReserveBook.newBuilder()
                                        .setInventoryId(inventoryId)
                                        .setUserId(userId)
                                        .build();
        return result;
    }

    public static CancelReservation cancelReservationInstance() {

        final CancelReservation result = cancelReservationInstance(inventoryId, userId);
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

        final ReturnBook result = returnBookInstance(inventoryId, inventoryItemId, userId);
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
        return reportLostBook(inventoryId, inventoryItemId, userId);
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

    public static MarkReservationExpired reservationPickUpPeriodInstanceExpired() {

        final MarkReservationExpired result = reservationPickUpPeriodInstanceExpired(inventoryId,
                                                                                     userId);
        return result;
    }

    public static MarkReservationExpired reservationPickUpPeriodInstanceExpired(
            InventoryId inventoryId,
            UserId userId) {
        MarkReservationExpired result = MarkReservationExpired.newBuilder()
                                                              .setInventoryId(inventoryId)
                                                              .setUserId(userId)
                                                              .build();
        return result;
    }

    public static ExtendLoanPeriod extendLoanPeriodInstance() {

        final ExtendLoanPeriod result = extendLoanPeriodInstance(inventoryId, loan.getLoanId(),
                                                                 userId);

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

        final LoanPeriodExtended result = loanPeriodExtended(inventoryId, loan.getLoanId(),
                                                             userId);
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
}
