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
import javaclasses.exlibris.BookId;
import javaclasses.exlibris.InventoryId;
import javaclasses.exlibris.InventoryItemId;
import javaclasses.exlibris.Isbn62;
import javaclasses.exlibris.LoanId;
import javaclasses.exlibris.Rfid;
import javaclasses.exlibris.UserId;
import javaclasses.exlibris.WriteOffReason;
import javaclasses.exlibris.c.BookBorrowed;
import javaclasses.exlibris.c.BookReturned;
import javaclasses.exlibris.c.InventoryAppended;
import javaclasses.exlibris.c.InventoryDecreased;

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
    public static final Rfid RF_ID = Rfid.newBuilder()
                                         .setValue("4321")
                                         .build();
    public static final WriteOffReason WRITE_OFF_REASON = WriteOffReason.newBuilder()
                                                                        .setCustomReason(
                                                                                "Custom reason")
                                                                        .build();
    public static final LoanId LOAN_ID = LoanId.newBuilder()
                                               .setValue(1)
                                               .build();

    private InventoryEventFactory() {
    }

    /**
     * Provides a pre-configured {@link InventoryAppended} event instance.
     *
     * @return the {@link InventoryAppended} instance
     */
    public static InventoryAppended inventoryAppendedInstance() {
        return inventoryAppendedInstance(INVENTORY_ID, INVENTORY_ITEM_ID, RF_ID, USER_ID,
                                         getCurrentTime());
    }

    /**
     * Provides the {@link InventoryAppended} event by inventory ID, inventory item ID, user ID and time.
     *
     * @param inventoryId     the identifier of an inventory
     * @param inventoryItemId the identifier of an inventory item that was added
     * @param rfid            radio-frequency identification of the inventory item
     * @param userId          the identifier of a user who added a book
     * @param whenAppended    time when inventory was appended
     * @return the {@code InventoryAppended} instance
     */
    public static InventoryAppended inventoryAppendedInstance(InventoryId inventoryId,
                                                              InventoryItemId inventoryItemId,
                                                              Rfid rfid,
                                                              UserId userId,
                                                              Timestamp whenAppended) {
        final InventoryAppended result = InventoryAppended.newBuilder()
                                                          .setInventoryId(inventoryId)
                                                          .setInventoryItemId(inventoryItemId)
                                                          .setRfid(rfid)
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
                                          getCurrentTime(), WRITE_OFF_REASON);
    }

    /**
     * Provides the {@link InventoryDecreased} event by inventory ID, inventory item ID, user ID and time.
     *
     * @param inventoryId     the identifier of an inventory
     * @param inventoryItemId the identifier of an inventory item that was added
     * @param userId          the identifier of a user who added a book
     * @param whenAppended    time when inventory was appended
     * @param reason          reason for write book off
     * @return the {@code InventoryAppended} instance
     */
    public static InventoryDecreased inventoryDecreasedInstance(InventoryId inventoryId,
                                                                InventoryItemId inventoryItemId,
                                                                UserId userId,
                                                                Timestamp whenAppended,
                                                                WriteOffReason reason) {
        final InventoryDecreased result = InventoryDecreased.newBuilder()
                                                            .setInventoryId(inventoryId)
                                                            .setInventoryItemId(inventoryItemId)
                                                            .setLibrarianId(userId)
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
                                    getCurrentTime());
    }

    /**
     * Provides the {@link BookBorrowed} event by inventory ID, inventory item ID, user ID and time.
     *
     * @param inventoryId     the identifier of an inventory
     * @param inventoryItemId the identifier of an inventory item that was added
     * @param userId          the identifier of a user who added a book
     * @param whenBorrowed    time when book was borrowed
     * @param loanId          the identifier of a loan
     * @return the {@code InventoryAppended} instance
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
     * @return the {@code InventoryAppended} instance
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

}
