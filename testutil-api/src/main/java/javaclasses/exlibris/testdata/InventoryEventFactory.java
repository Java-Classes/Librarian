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
import io.spine.net.EmailAddress;
import io.spine.time.LocalDate;
import io.spine.time.MonthOfYear;
import javaclasses.exlibris.BookId;
import javaclasses.exlibris.InventoryId;
import javaclasses.exlibris.InventoryItemId;
import javaclasses.exlibris.Isbn62;
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
import javaclasses.exlibris.c.LoansBecameExtensionAllowed;
import javaclasses.exlibris.c.LoansBecameNotAllowedForExtension;
import javaclasses.exlibris.c.ReservationAdded;
import javaclasses.exlibris.c.ReservationBecameLoan;
import javaclasses.exlibris.c.ReservationCanceled;

import java.util.ArrayList;
import java.util.List;

import static io.spine.time.Time.getCurrentTime;

/**
 * A factory of the book events for the test needs.
 *
 * @author Yurii Haidamaka
 */
public class InventoryEventFactory {

    public static final Isbn62 ISBN_62 = Isbn62.newBuilder()
                                               .setValue("2mBSCRqZ")
                                               .build();

    public static final BookId BOOK_ID = BookId.newBuilder()
                                               .setIsbn62(ISBN_62)
                                               .build();
    public static final EmailAddress USER_EMAIL_ADRESS = EmailAddress.newBuilder()
                                                                     .setValue(
                                                                             "paulageyev@gmail.com")
                                                                     .build();
    public static final UserId USER_ID = UserId.newBuilder()
                                               .setEmail(USER_EMAIL_ADRESS)
                                               .build();

    public static final InventoryId INVENTORY_ID = InventoryId.newBuilder()
                                                              .setBookId(BOOK_ID)
                                                              .build();
    public static final InventoryItemId INVENTORY_ITEM_ID = InventoryItemId.newBuilder()
                                                                           .setBookId(BOOK_ID)
                                                                           .setItemNumber(1)
                                                                           .build();
    public static final QRcodeURL QR_CODE_URL = QRcodeURL.newBuilder()
                                                         .setValue("exlibris/4321")
                                                         .build();
    public static final WriteOffReason WRITE_OFF_REASON = WriteOffReason.newBuilder()
                                                                        .setCustomReason(
                                                                                "Custom reason")
                                                                        .build();
    public static final LoanId LOAN_ID = LoanId.newBuilder()
                                               .setValue(1)
                                               .build();
    // Timestamp on 01.01.1970 00:00
    public static final Timestamp DEFAULT_TIMESTAMP1 = Timestamp.newBuilder()
                                                                .setNanos(0)
                                                                .setSeconds(0)
                                                                .build();
    public static final Timestamp DEFAULT_TIMESTAMP2 = Timestamp.newBuilder()
                                                                .setNanos(0)
                                                                .setSeconds(213456789)
                                                                .build();

    public static final LocalDate DEFAULT_DATE1 = LocalDate.newBuilder()
                                                           .setDay(1)
                                                           .setMonth(
                                                                   MonthOfYear.valueOf(1))
                                                           .setYear(1970)
                                                           .build();
    public static final LocalDate DEFAULT_DATE2 = LocalDate.newBuilder()
                                                           .setDay(6)
                                                           .setMonth(
                                                                   MonthOfYear.valueOf(10))
                                                           .setYear(1976)
                                                           .build();
    public static final LocalDate DEFAULT_DUE_DATE = LocalDate.newBuilder()
                                                              .setDay(15)
                                                              .setMonth(
                                                                      MonthOfYear.valueOf(1))
                                                              .setYear(1970)
                                                              .build();

    private InventoryEventFactory() {
    }

    /**
     * Provides a pre-configured {@link InventoryAppended} event instance.
     *
     * @return the {@link InventoryAppended} instance
     */
    public static InventoryAppended inventoryAppendedInstance() {
        return inventoryAppendedInstance(INVENTORY_ID, INVENTORY_ITEM_ID, QR_CODE_URL, USER_ID,
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
        return inventoryDecreasedInstance(INVENTORY_ID, INVENTORY_ITEM_ID, USER_ID,
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
        return bookBorrowedInstance(INVENTORY_ID, INVENTORY_ITEM_ID, USER_ID, LOAN_ID,
                                    DEFAULT_TIMESTAMP1);
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
                                                    Timestamp whenBorrowed) {
        final BookBorrowed result = BookBorrowed.newBuilder()
                                                .setInventoryId(inventoryId)
                                                .setInventoryItemId(inventoryItemId)
                                                .setWhoBorrowed(userId)
                                                .setWhenBorrowed(whenBorrowed)
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
        return bookReturnedInstance(INVENTORY_ID, INVENTORY_ITEM_ID, USER_ID,
                                    getCurrentTime());
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
        return bookBecameAvailableInstance(INVENTORY_ID, INVENTORY_ITEM_ID, 1,
                                           getCurrentTime());
    }

    /**
     * Provides the {@link BookBecameAvailable} event by inventory ID, inventory item ID,
     * available count and time.
     *
     * @param inventoryId         the identifier of an inventory
     * @param inventoryItemId     the identifier of an inventory item that was added
     * @param count               available book count in the library
     * @param whenBecameAvailable time when book became available
     * @return the {@code BookBecameAvailable} instance
     */
    public static BookBecameAvailable bookBecameAvailableInstance(InventoryId inventoryId,
                                                                  InventoryItemId inventoryItemId,
                                                                  int count,
                                                                  Timestamp whenBecameAvailable) {
        final BookBecameAvailable result = BookBecameAvailable.newBuilder()
                                                              .setInventoryId(inventoryId)
                                                              .setInventoryItemId(inventoryItemId)
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
        return bookLostInstance(INVENTORY_ID, INVENTORY_ITEM_ID, USER_ID,
                                getCurrentTime());
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
        return loanBecameOverdueInstance(INVENTORY_ID, INVENTORY_ITEM_ID, LOAN_ID,
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
        return loanBecameShouldReturnSoonInstance(INVENTORY_ID, INVENTORY_ITEM_ID, LOAN_ID,
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
     * Provides a pre-configured {@link LoansBecameExtensionAllowed} event instance.
     *
     * @return the {@link LoansBecameExtensionAllowed} instance
     */
    public static LoansBecameExtensionAllowed loansBecameExtensionAllowedInstance() {
        return loansBecameExtensionAllowedInstance(INVENTORY_ID, USER_ID, DEFAULT_TIMESTAMP1);
    }

    /**
     * Provides the {@link LoansBecameExtensionAllowed} event by inventory ID, user ID and time.
     *
     * @param inventoryId the identifier of an inventory
     * @param whenBecame  time when loan became extension allowed
     * @param userId      the identifier of a user
     * @return the {@code LoansBecameExtensionAllowed} instance
     */
    public static LoansBecameExtensionAllowed loansBecameExtensionAllowedInstance(
            InventoryId inventoryId,
            UserId userId,
            Timestamp whenBecame) {
        final List<UserId> borrowers = new ArrayList<>();
        borrowers.add(userId);
        final LoansBecameExtensionAllowed result =
                LoansBecameExtensionAllowed.newBuilder()
                                           .setInventoryId(inventoryId)
                                           .addAllBorrowers(borrowers)
                                           .setWhenBecame(whenBecame)
                                           .build();
        return result;
    }

    /**
     * Provides a pre-configured {@link LoansBecameNotAllowedForExtension} event instance.
     *
     * @return the {@link LoansBecameNotAllowedForExtension} instance
     */
    public static LoansBecameNotAllowedForExtension loansBecameNotAllowedForExtensionInstance() {
        return loansBecameNotAllowedForExtensionInstance(INVENTORY_ID, USER_ID, DEFAULT_TIMESTAMP1);
    }

    /**
     * Provides the {@link LoansBecameNotAllowedForExtension} event by inventory ID, user ID and time.
     *
     * @param inventoryId the identifier of an inventory
     * @param whenBecame  time when loan became not allowed for extension
     * @param userId      the identifier of a user
     * @return the {@code LoansBecameNotAllowedForExtension} instance
     */
    public static LoansBecameNotAllowedForExtension loansBecameNotAllowedForExtensionInstance(
            InventoryId inventoryId,
            UserId userId,
            Timestamp whenBecame) {
        final List<UserId> borrowers = new ArrayList<>();
        borrowers.add(userId);
        final LoansBecameNotAllowedForExtension result =
                LoansBecameNotAllowedForExtension.newBuilder()
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
        return loanPeriodExtendedInstance(INVENTORY_ID, INVENTORY_ITEM_ID, LOAN_ID, USER_ID,
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
        return reservationAddedInstance(INVENTORY_ID, USER_ID, DEFAULT_TIMESTAMP1, DEFAULT_DATE1);
    }

    /**
     * Provides the {@link ReservationAdded} event by inventory ID, user ID and time.
     *
     * @param inventoryId       the identifier of an inventory
     * @param userId            the identifier of a user
     * @param whenCreated       the time when the reservation was added
     * @param whenReadyToPickUp the time when book will be ready to pick up
     * @return the {@code ReservationAdded} instance
     */
    public static ReservationAdded reservationAddedInstance(InventoryId inventoryId,
                                                            UserId userId,
                                                            Timestamp whenCreated,
                                                            LocalDate whenReadyToPickUp) {
        final ReservationAdded result = ReservationAdded.newBuilder()
                                                        .setInventoryId(inventoryId)
                                                        .setForWhomReserved(userId)
                                                        .setWhenCreated(whenCreated)
                                                        .setWhenExpected(whenReadyToPickUp)
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
     * Provides a pre-configured {@link BookReadyToPickup} event instance.
     *
     * @return the {@link BookReadyToPickup} instance
     */
    public static BookReadyToPickup bookReadyToPickUpInstance() {
        return bookReadyToPickUpInstance(INVENTORY_ID, INVENTORY_ITEM_ID, USER_ID,
                                         DEFAULT_TIMESTAMP1, DEFAULT_TIMESTAMP2);
    }

    /**
     * Provides the {@link BookReadyToPickup} event by inventory ID, inventory item ID,
     * user ID and time.
     *
     * @param inventoryId             the identifier of an inventory
     * @param userId                  the identifier of a user
     * @param whenBecameReadyToPickUp the time when the book became ready to pick up.
     * @param pickUpDeadline          the time when the book became ready to pick up.
     * @return the {@code BookReadyToPickup} instance
     */
    public static BookReadyToPickup bookReadyToPickUpInstance(InventoryId inventoryId,
                                                              InventoryItemId inventoryItemId,
                                                              UserId userId,
                                                              Timestamp whenBecameReadyToPickUp,
                                                              Timestamp pickUpDeadline) {
        final BookReadyToPickup result = BookReadyToPickup.newBuilder()
                                                          .setInventoryId(inventoryId)
                                                          .setInventoryItemId(inventoryItemId)
                                                          .setForWhom(userId)
                                                          .setWhenBecameReadyToPickup(
                                                                  whenBecameReadyToPickUp)
                                                          .setPickUpDeadline(pickUpDeadline)
                                                          .build();
        return result;
    }
}
