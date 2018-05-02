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

import com.google.protobuf.Timestamp;
import javaclasses.exlibris.InventoryId;
import javaclasses.exlibris.InventoryItemId;
import javaclasses.exlibris.LoanId;
import javaclasses.exlibris.QRcodeURL;
import javaclasses.exlibris.UserId;
import javaclasses.exlibris.WriteOffReason;
import javaclasses.exlibris.c.BookBecameAvailable;
import javaclasses.exlibris.c.BookBorrowed;
import javaclasses.exlibris.c.BookLost;
import javaclasses.exlibris.c.BookReadyToPickup;
import javaclasses.exlibris.c.BookReturned;
import javaclasses.exlibris.c.InventoryAppended;
import javaclasses.exlibris.c.InventoryDecreased;
import javaclasses.exlibris.c.LoanBecameOverdue;
import javaclasses.exlibris.c.LoanBecameShouldReturnSoon;
import javaclasses.exlibris.c.LoanPeriodExtended;
import javaclasses.exlibris.c.LoansExtensionAllowed;
import javaclasses.exlibris.c.LoansExtensionForbidden;
import javaclasses.exlibris.c.ReservationAdded;
import javaclasses.exlibris.c.ReservationBecameLoan;
import javaclasses.exlibris.c.ReservationCanceled;
import javaclasses.exlibris.c.ReservationPickUpPeriodExpired;

import java.util.ArrayList;
import java.util.List;

import static io.spine.time.Time.getCurrentTime;
import static javaclasses.exlibris.testdata.TestValues.DEFAULT_TIMESTAMP1;
import static javaclasses.exlibris.testdata.TestValues.DEFAULT_TIMESTAMP2;
import static javaclasses.exlibris.testdata.TestValues.DUE_TIMESTAMP;
import static javaclasses.exlibris.testdata.TestValues.INVENTORY_ID;
import static javaclasses.exlibris.testdata.TestValues.INVENTORY_ITEM_ID_1;
import static javaclasses.exlibris.testdata.TestValues.LOAN_ID;
import static javaclasses.exlibris.testdata.TestValues.QR_CODE_URL;
import static javaclasses.exlibris.testdata.TestValues.USER_ID;
import static javaclasses.exlibris.testdata.TestValues.WRITE_OFF_REASON;

/**
 * A factory of the book events for the test needs.
 *
 * @author Yurii Haidamaka
 */
public class InventoryEventFactory {

    private InventoryEventFactory() {
    }

    /**
     * Provides a pre-configured {@link InventoryAppended} event instance.
     *
     * @return the {@link InventoryAppended} instance
     */
    public static InventoryAppended inventoryAppendedInstance() {
        return inventoryAppendedInstance(INVENTORY_ID, INVENTORY_ITEM_ID_1, QR_CODE_URL, USER_ID,
                                         getCurrentTime());
    }

    /**
     * Provides the {@link InventoryAppended} event by inventory ID, inventory item ID, Or code URL,
     * user ID and time.
     *
     * @param inventoryId     the identifier of an inventory
     * @param inventoryItemId the identifier of an inventory item that was added
     * @param qRcodeURL       the url to QR code image
     * @param userId          the identifier of a user who added a book
     * @param whenAppended    time when inventory was appended
     * @return the {@code InventoryAppended} instance
     */
    public static InventoryAppended inventoryAppendedInstance(InventoryId inventoryId,
                                                              InventoryItemId inventoryItemId,
                                                              QRcodeURL qRcodeURL,
                                                              UserId userId,
                                                              Timestamp whenAppended) {
        final InventoryAppended result = InventoryAppended.newBuilder()
                                                          .setInventoryId(inventoryId)
                                                          .setInventoryItemId(inventoryItemId)
                                                          .setQrCodeUrl(qRcodeURL)
                                                          .setLibrarianId(userId)
                                                          .setWhenAppended(whenAppended)
                                                          .build();
        return result;
    }

    /**
     * Provides a pre-configured {@link InventoryDecreased} event instance.
     *
     * @return the {@link InventoryDecreased} instance
     */
    public static InventoryDecreased inventoryDecreasedInstance() {
        return inventoryDecreasedInstance(INVENTORY_ID, INVENTORY_ITEM_ID_1, USER_ID,
                                          getCurrentTime(), WRITE_OFF_REASON, 0);
    }

    /**
     * Provides the {@link InventoryDecreased} event by inventory ID, inventory item ID,
     * user ID, time and reason.
     *
     * @param inventoryId     the identifier of an inventory
     * @param inventoryItemId the identifier of an inventory item that was added
     * @param userId          the identifier of a user who added a book
     * @param whenAppended    time when inventory was appended
     * @param reason          reason for write book off
     * @param count           available count og books
     * @return the {@code InventoryDecreased} instance
     */
    public static InventoryDecreased inventoryDecreasedInstance(InventoryId inventoryId,
                                                                InventoryItemId inventoryItemId,
                                                                UserId userId,
                                                                Timestamp whenAppended,
                                                                WriteOffReason reason, int count) {
        final InventoryDecreased result = InventoryDecreased.newBuilder()
                                                            .setInventoryId(inventoryId)
                                                            .setInventoryItemId(inventoryItemId)
                                                            .setLibrarianId(userId)
                                                            .setAvailableBooksCount(count)
                                                            .setWhenDecreased(whenAppended)
                                                            .setWriteOffReason(reason)
                                                            .build();
        return result;
    }

    /**
     * Provides a pre-configured {@link BookBorrowed} event instance.
     *
     * @return the {@link BookBorrowed} instance
     */
    public static BookBorrowed bookBorrowedInstance() {
        return bookBorrowedInstance(INVENTORY_ID, INVENTORY_ITEM_ID_1, USER_ID, LOAN_ID,
                                    DEFAULT_TIMESTAMP1, DUE_TIMESTAMP);
    }

    /**
     * Provides the {@link BookBorrowed} event by inventory ID, inventory item ID, user ID,
     * loan ID and time.
     *
     * @param inventoryId     the identifier of an inventory
     * @param inventoryItemId the identifier of an inventory item that was added
     * @param userId          the identifier of a user who added a book
     * @param whenBorrowed    time when book was borrowed
     * @param loanId          the identifier of a loan
     * @return the {@code BookBorrowed} instance
     */
    public static BookBorrowed bookBorrowedInstance(InventoryId inventoryId,
                                                    InventoryItemId inventoryItemId,
                                                    UserId userId,
                                                    LoanId loanId,
                                                    Timestamp whenBorrowed, Timestamp whenDue) {
        final BookBorrowed result = BookBorrowed.newBuilder()
                                                .setInventoryId(inventoryId)
                                                .setInventoryItemId(inventoryItemId)
                                                .setWhoBorrowed(userId)
                                                .setWhenBorrowed(whenBorrowed)
                                                .setWhenDue(whenDue)
                                                .setLoanId(loanId)
                                                .build();
        return result;
    }

    /**
     * Provides a pre-configured {@link BookReturned} event instance.
     *
     * @return the {@link BookReturned} instance
     */
    public static BookReturned bookReturnedInstance() {
        return bookReturnedInstance(INVENTORY_ID, INVENTORY_ITEM_ID_1, USER_ID,
                                    DEFAULT_TIMESTAMP1);
    }

    /**
     * Provides the {@link BookReturned} event by inventory ID, inventory item ID, user ID and time.
     *
     * @param inventoryId     the identifier of an inventory
     * @param inventoryItemId the identifier of an inventory item that was added
     * @param userId          the identifier of a user who added a book
     * @param whenReturned    time when book was returned
     * @return the {@code BookReturned} instance
     */
    public static BookReturned bookReturnedInstance(InventoryId inventoryId,
                                                    InventoryItemId inventoryItemId,
                                                    UserId userId,
                                                    Timestamp whenReturned) {
        final BookReturned result = BookReturned.newBuilder()
                                                .setInventoryId(inventoryId)
                                                .setInventoryItemId(inventoryItemId)
                                                .setWhoReturned(userId)
                                                .setWhenReturned(whenReturned)
                                                .build();
        return result;
    }

    /**
     * Provides a pre-configured {@link BookBecameAvailable} event instance.
     *
     * @return the {@link BookBecameAvailable} instance
     */
    public static BookBecameAvailable bookBecameAvailableInstance() {
        return bookBecameAvailableInstance(INVENTORY_ID, 1,
                                           getCurrentTime());
    }

    /**
     * Provides the {@link BookBecameAvailable} event by inventory ID
     * available count and time.
     *
     * @param inventoryId         the identifier of an inventory
     * @param count               available book count in the library
     * @param whenBecameAvailable time when book became available
     * @return the {@code BookBecameAvailable} instance
     */
    public static BookBecameAvailable bookBecameAvailableInstance(InventoryId inventoryId,
                                                                  int count,
                                                                  Timestamp whenBecameAvailable) {
        final BookBecameAvailable result = BookBecameAvailable.newBuilder()
                                                              .setInventoryId(inventoryId)
                                                              .setAvailableBooksCount(count)
                                                              .setWhenBecameAvailable(
                                                                      whenBecameAvailable)
                                                              .build();
        return result;
    }

    /**
     * Provides a pre-configured {@link BookLost} event instance.
     *
     * @return the {@link BookLost} instance
     */
    public static BookLost bookLostInstance() {
        return bookLostInstance(INVENTORY_ID, INVENTORY_ITEM_ID_1, USER_ID,
                                DEFAULT_TIMESTAMP1);
    }

    /**
     * Provides the {@link BookReturned} event by inventory ID, inventory item ID, user ID and time.
     *
     * @param inventoryId     the identifier of an inventory
     * @param inventoryItemId the identifier of an inventory item that was lost
     * @param userId          the identifier of a user who lost a book
     * @param whenReported    time when information was reported
     * @return the {@code BookLost} instance
     */
    public static BookLost bookLostInstance(InventoryId inventoryId,
                                            InventoryItemId inventoryItemId,
                                            UserId userId,
                                            Timestamp whenReported) {
        final BookLost result = BookLost.newBuilder()
                                        .setInventoryId(inventoryId)
                                        .setInventoryItemId(inventoryItemId)
                                        .setWhoLost(userId)
                                        .setWhenReported(whenReported)
                                        .build();
        return result;
    }

    /**
     * Provides a pre-configured {@link LoanBecameOverdue} event instance.
     *
     * @return the {@link LoanBecameOverdue} instance
     */
    public static LoanBecameOverdue loanBecameOverdueInstance() {
        return loanBecameOverdueInstance(INVENTORY_ID, INVENTORY_ITEM_ID_1, LOAN_ID,
                                         DEFAULT_TIMESTAMP1, USER_ID);
    }

    /**
     * Provides the {@link LoanBecameOverdue} event by inventory ID, inventory item ID, loan ID,
     * user ID and time.
     *
     * @param inventoryId     the identifier of an inventory
     * @param inventoryItemId the identifier of an inventory item
     * @param loanId          the identifier of a loan
     * @param whenOverdue     time when loan became overdue
     * @param userId          the identifier of a user
     * @return the {@code LoanBecameOverdue} instance
     */
    public static LoanBecameOverdue loanBecameOverdueInstance(InventoryId inventoryId,
                                                              InventoryItemId inventoryItemId,
                                                              LoanId loanId,
                                                              Timestamp whenOverdue,
                                                              UserId userId) {
        final LoanBecameOverdue result = LoanBecameOverdue.newBuilder()
                                                          .setInventoryId(inventoryId)
                                                          .setInventoryItemId(inventoryItemId)
                                                          .setLoanId(loanId)
                                                          .setUserId(userId)
                                                          .setWhenBecameOverdue(whenOverdue)
                                                          .build();
        return result;
    }

    /**
     * Provides a pre-configured {@link LoanBecameShouldReturnSoon} event instance.
     *
     * @return the {@link LoanBecameShouldReturnSoon} instance
     */
    public static LoanBecameShouldReturnSoon loanBecameShouldReturnSoonInstance() {
        return loanBecameShouldReturnSoonInstance(INVENTORY_ID, INVENTORY_ITEM_ID_1, LOAN_ID,
                                                  DEFAULT_TIMESTAMP1, USER_ID, true);
    }

    /**
     * Provides the {@link LoanBecameShouldReturnSoon} event by inventory ID,
     * inventory item ID, loan ID, user ID and time.
     *
     * @param inventoryId     the identifier of an inventory
     * @param inventoryItemId the identifier of an inventory item
     * @param loanId          the identifier of a loan
     * @param whenBecame      time when loan became overdue
     * @param userId          the identifier of a user
     * @return the {@code LoanBecameShouldReturnSoon} instance
     */
    public static LoanBecameShouldReturnSoon loanBecameShouldReturnSoonInstance(
            InventoryId inventoryId,
            InventoryItemId inventoryItemId,
            LoanId loanId,
            Timestamp whenBecame,
            UserId userId, boolean isAllowedLoanextension) {
        final LoanBecameShouldReturnSoon result =
                LoanBecameShouldReturnSoon.newBuilder()
                                          .setInventoryId(inventoryId)
                                          .setInventoryItemId(inventoryItemId)
                                          .setLoanId(loanId)
                                          .setUserId(userId)
                                          .setWhenBecameShouldReturnSoon(whenBecame)
                                          .setIsAllowedExtension(isAllowedLoanextension)
                                          .build();
        return result;
    }

    /**
     * Provides a pre-configured {@link LoansExtensionAllowed} event instance.
     *
     * @return the {@link LoansExtensionAllowed} instance
     */
    public static LoansExtensionAllowed loansExtensionAllowedInstance() {
        return loansExtensionAllowedInstance(INVENTORY_ID, USER_ID, DEFAULT_TIMESTAMP1);
    }

    /**
     * Provides the {@link LoansExtensionAllowed} event by inventory ID, user ID and time.
     *
     * @param inventoryId the identifier of an inventory
     * @param whenBecame  time when loan became extension allowed
     * @param userId      the identifier of a user
     * @return the {@code LoansExtensionAllowed} instance
     */
    public static LoansExtensionAllowed loansExtensionAllowedInstance(
            InventoryId inventoryId,
            UserId userId,
            Timestamp whenBecame) {
        final List<UserId> borrowers = new ArrayList<>();
        borrowers.add(userId);
        final LoansExtensionAllowed result =
                LoansExtensionAllowed.newBuilder()
                                     .setInventoryId(inventoryId)
                                     .addAllBorrowers(borrowers)
                                     .setWhenBecame(whenBecame)
                                     .build();
        return result;
    }

    /**
     * Provides a pre-configured {@link LoansExtensionForbidden} event instance.
     *
     * @return the {@link LoansExtensionForbidden} instance
     */
    public static LoansExtensionForbidden loansExtensionForbiddenInstance() {
        return loansExtensionForbiddenInstance(INVENTORY_ID, USER_ID, DEFAULT_TIMESTAMP1);
    }

    /**
     * Provides the {@link LoansExtensionForbidden} event by inventory ID, user ID and time.
     *
     * @param inventoryId the identifier of an inventory
     * @param whenBecame  time when loan became not allowed for extension
     * @param userId      the identifier of a user
     * @return the {@code LoansExtensionForbidden} instance
     */
    public static LoansExtensionForbidden loansExtensionForbiddenInstance(
            InventoryId inventoryId,
            UserId userId,
            Timestamp whenBecame) {
        final List<UserId> borrowers = new ArrayList<>();
        borrowers.add(userId);
        final LoansExtensionForbidden result =
                LoansExtensionForbidden.newBuilder()
                                       .setInventoryId(inventoryId)
                                       .addAllBorrowers(borrowers)
                                       .setWhenBecame(whenBecame)
                                       .build();
        return result;
    }

    /**
     * Provides a pre-configured {@link LoanPeriodExtended} event instance.
     *
     * @return the {@link LoanPeriodExtended} instance
     */
    public static LoanPeriodExtended loanPeriodExtendedInstance() {
        return loanPeriodExtendedInstance(INVENTORY_ID, INVENTORY_ITEM_ID_1, LOAN_ID, USER_ID,
                                          DEFAULT_TIMESTAMP1,
                                          DEFAULT_TIMESTAMP2);
    }

    /**
     * Provides the {@link LoanPeriodExtended} event by inventory ID, inventory item ID, loan ID,
     * user ID and time.
     *
     * @param inventoryId     the identifier of an inventory
     * @param loanId          the identifier of a loan
     * @param userId          the identifier of a user
     * @param previousDueDate previous due date
     * @param newDueDate      new due date
     * @return the {@code LoanPeriodExtended} instance
     */
    public static LoanPeriodExtended loanPeriodExtendedInstance(InventoryId inventoryId,
                                                                InventoryItemId inventoryItemId,
                                                                LoanId loanId,
                                                                UserId userId,
                                                                Timestamp previousDueDate,
                                                                Timestamp newDueDate) {
        final LoanPeriodExtended result = LoanPeriodExtended.newBuilder()
                                                            .setInventoryId(inventoryId)
                                                            .setInventoryItemId(inventoryItemId)
                                                            .setLoanId(loanId)
                                                            .setUserId(userId)
                                                            .setPreviousDueDate(previousDueDate)
                                                            .setNewDueDate(newDueDate)
                                                            .build();
        return result;
    }

    /**
     * Provides a pre-configured {@link ReservationAdded} event instance.
     *
     * @return the {@link ReservationAdded} instance
     */
    public static ReservationAdded reservationAddedInstance() {
        return reservationAddedInstance(INVENTORY_ID, USER_ID, DEFAULT_TIMESTAMP1, DUE_TIMESTAMP);
    }

    /**
     * Provides the {@link ReservationAdded} event by inventory ID, user ID and time.
     *
     * @param inventoryId  the identifier of an inventory
     * @param userId       the identifier of a user
     * @param whenCreated  the time when the reservation was added
     * @param whenExpected the time when book will be ready to pick up
     * @return the {@code ReservationAdded} instance
     */
    public static ReservationAdded reservationAddedInstance(InventoryId inventoryId,
                                                            UserId userId,
                                                            Timestamp whenCreated,
                                                            Timestamp whenExpected) {
        final ReservationAdded result = ReservationAdded.newBuilder()
                                                        .setInventoryId(inventoryId)
                                                        .setForWhomReserved(userId)
                                                        .setWhenCreated(whenCreated)
                                                        .setWhenExpected(whenExpected)
                                                        .build();
        return result;
    }

    /**
     * Provides a pre-configured {@link ReservationBecameLoan} event instance.
     *
     * @return the {@link ReservationBecameLoan} instance
     */
    public static ReservationBecameLoan reservationBecameLoanInstance() {
        return reservationBecameLoanInstance(INVENTORY_ID, USER_ID, DEFAULT_TIMESTAMP1);
    }

    /**
     * Provides the {@link ReservationBecameLoan} event by inventory ID, user ID and time.
     *
     * @param inventoryId    the identifier of an inventory
     * @param userId         the identifier of a user
     * @param whenBecameLoan the time when the reservation became loan
     * @return the {@code ReservationBecameLoan} instance
     */
    public static ReservationBecameLoan reservationBecameLoanInstance(InventoryId inventoryId,
                                                                      UserId userId,
                                                                      Timestamp whenBecameLoan) {
        final ReservationBecameLoan result = ReservationBecameLoan.newBuilder()
                                                                  .setInventoryId(inventoryId)
                                                                  .setUserId(userId)
                                                                  .setWhenBecameLoan(whenBecameLoan)
                                                                  .build();
        return result;
    }

    /**
     * Provides a pre-configured {@link ReservationCanceled} event instance.
     *
     * @return the {@link ReservationCanceled} instance
     */
    public static ReservationCanceled reservationCanceledInstance() {
        return reservationCanceledInstance(INVENTORY_ID, USER_ID, DEFAULT_TIMESTAMP1);
    }

    /**
     * Provides the {@link ReservationCanceled} event by inventory ID, user ID and time.
     *
     * @param inventoryId  the identifier of an inventory
     * @param userId       the identifier of a user
     * @param whenCanceled the time when the reservation was canceled
     * @return the {@code ReservationCanceled} instance
     */
    public static ReservationCanceled reservationCanceledInstance(InventoryId inventoryId,
                                                                  UserId userId,
                                                                  Timestamp whenCanceled) {
        final ReservationCanceled result = ReservationCanceled.newBuilder()
                                                              .setInventoryId(inventoryId)
                                                              .setWhoCanceled(userId)
                                                              .setWhenCanceled(whenCanceled)
                                                              .build();
        return result;
    }

    /**
     * Provides a pre-configured {@link ReservationPickUpPeriodExpired} event instance.
     *
     * @return the {@link ReservationPickUpPeriodExpired} instance
     */
    public static ReservationPickUpPeriodExpired reservationPickUpPeriodExpiredInstance() {
        return reservationPickUpPeriodExpiredInstance(INVENTORY_ID, USER_ID, DEFAULT_TIMESTAMP1);
    }

    /**
     * Provides the {@link ReservationPickUpPeriodExpired} event by inventory ID, user ID and time.
     *
     * @param inventoryId the identifier of an inventory
     * @param userId      the identifier of a user
     * @param whenExpired the time when the reservation was expired
     * @return the {@code ReservationPickUpPeriodExpired} instance
     */
    public static ReservationPickUpPeriodExpired reservationPickUpPeriodExpiredInstance(
            InventoryId inventoryId,
            UserId userId,
            Timestamp whenExpired) {
        final ReservationPickUpPeriodExpired result = ReservationPickUpPeriodExpired.newBuilder()
                                                                                    .setInventoryId(
                                                                                            inventoryId)
                                                                                    .setUserId(
                                                                                            userId)
                                                                                    .setWhenExpired(
                                                                                            whenExpired)
                                                                                    .build();
        return result;
    }

    /**
     * Provides a pre-configured {@link BookReadyToPickup} event instance.
     *
     * @return the {@link BookReadyToPickup} instance
     */
    public static BookReadyToPickup bookReadyToPickUpInstance() {
        return bookReadyToPickUpInstance(INVENTORY_ID, USER_ID,
                                         DEFAULT_TIMESTAMP1, DEFAULT_TIMESTAMP2);
    }

    /**
     * Provides the {@link BookReadyToPickup} event by inventory ID,
     * user ID and time.
     *
     * @param inventoryId             the identifier of an inventory
     * @param userId                  the identifier of a user
     * @param whenBecameReadyToPickUp the time when the book became ready to pick up.
     * @param pickUpDeadline          the time when the book became ready to pick up.
     * @return the {@code BookReadyToPickup} instance
     */
    public static BookReadyToPickup bookReadyToPickUpInstance(InventoryId inventoryId,
                                                              UserId userId,
                                                              Timestamp whenBecameReadyToPickUp,
                                                              Timestamp pickUpDeadline) {
        final BookReadyToPickup result = BookReadyToPickup.newBuilder()
                                                          .setInventoryId(inventoryId)
                                                          .setForWhom(userId)
                                                          .setWhenBecameReadyToPickup(
                                                                  whenBecameReadyToPickUp)
                                                          .setPickUpDeadline(pickUpDeadline)
                                                          .build();
        return result;
    }
}
