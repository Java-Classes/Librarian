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
import io.spine.net.Url;
import io.spine.people.PersonName;
import io.spine.time.LocalDate;
import io.spine.time.MonthOfYear;
import javaclasses.exlibris.AuthorName;
import javaclasses.exlibris.Book;
import javaclasses.exlibris.BookDetails;
import javaclasses.exlibris.BookDetailsChange;
import javaclasses.exlibris.BookId;
import javaclasses.exlibris.BookSynopsis;
import javaclasses.exlibris.BookTitle;
import javaclasses.exlibris.Category;
import javaclasses.exlibris.InventoryId;
import javaclasses.exlibris.InventoryItemId;
import javaclasses.exlibris.Isbn;
import javaclasses.exlibris.Isbn62;
import javaclasses.exlibris.Loan;
import javaclasses.exlibris.LoanId;
import javaclasses.exlibris.QRcodeURL;
import javaclasses.exlibris.Rfid;
import javaclasses.exlibris.UserId;
import javaclasses.exlibris.WriteOffReason;
import javaclasses.exlibris.q.InventoryItemState;
import javaclasses.exlibris.q.LoanDetails;

import java.util.function.Function;

import static io.spine.time.LocalDates.addDays;
import static io.spine.time.Time.getCurrentTime;
import static javaclasses.exlibris.Timestamps.toLocalDate;

/**
 * The utility class that provides constant instances of
 * value objects and entities of the domain model. These
 * values are used for test needs upon creation of command
 * instances.
 *
 * @author Yurii Haidamaka
 */
public class TestValues {

    public static final Isbn62 ISBN_62 = Isbn62.newBuilder()
                                               .setValue("0201485672")
                                               .build();

    public static final BookId BOOK_ID = BookId.newBuilder()
                                               .setIsbn62(ISBN_62)
                                               .build();

    public static final EmailAddress USER_EMAIL_1 = EmailAddress.newBuilder()
                                                                .setValue(
                                                                        "paulageyev@gmail.com")
                                                                .build();

    public static final EmailAddress USER_EMAIL_2 = EmailAddress.newBuilder()
                                                                .setValue(
                                                                        "petrVase4kin@gmail.com")
                                                                .build();
    private static final EmailAddress USER_EMAIL_3 = EmailAddress.newBuilder()
                                                                 .setValue(
                                                                         "petr3@gmail.com")
                                                                 .build();

    public static final EmailAddress LIBRARIAN_EMAIL = EmailAddress.newBuilder()
                                                                   .setValue(
                                                                           "smb@teamdev.com")
                                                                   .build();
    public static final BookTitle BOOK_TITLE = BookTitle.newBuilder()
                                                        .setTitle("Refactoring")
                                                        .build();

    public static final AuthorName AUTHOR_NAME = AuthorName.newBuilder()
                                                           .addAuthorName(
                                                                   PersonName.newBuilder()
                                                                             .setFamilyName(
                                                                                     "Fowler")
                                                                             .setGivenName(
                                                                                     "Martin"))
                                                           .build();

    public static final Url COVER_URL = Url.newBuilder()
                                           .setRaw("http://library.teamdev.com/book/1")
                                           .build();

    public static final BookSynopsis BOOK_SYNOPSIS = BookSynopsis.newBuilder()
                                                                 .setBookSynopsis(
                                                                         "As the application of object " +
                                                                                 "technology--particularly the Java programming language")
                                                                 .build();

    public static final Category BOOK_CATEGORY = Category.newBuilder()
                                                         .setValue("Programming")
                                                         .build();
    public static final BookTitle BOOK_TITLE_2 = BookTitle.newBuilder()
                                                          .setTitle("WHY SOFTWARE SUX")
                                                          .build();

    public static final AuthorName AUTHOR_NAME_2 = AuthorName.newBuilder()
                                                             .addAuthorName(
                                                                     PersonName.newBuilder()
                                                                               .setFamilyName(
                                                                                       "Cartman")
                                                                               .setGivenName(
                                                                                       "Eric"))
                                                             .build();

    public static final Url COVER_URL_2 = Url.newBuilder()
                                             .setRaw("http://library.teamdev.com/book/144")
                                             .build();

    public static final BookSynopsis BOOK_SYNOPSIS_2 = BookSynopsis.newBuilder()
                                                                   .setBookSynopsis(
                                                                           "Some info about software and why it sucks ")
                                                                   .build();

    public static final Category BOOK_CATEGORY_2 = Category.newBuilder()
                                                           .setValue("Architecture")
                                                           .build();

    public static final UserId USER_ID = UserId.newBuilder()
                                               .setEmail(USER_EMAIL_1)
                                               .build();

    public static final UserId USER_ID_2 = UserId.newBuilder()
                                                 .setEmail(USER_EMAIL_2)
                                                 .build();

    public static final UserId USER_ID_3 = UserId.newBuilder()
                                                 .setEmail(USER_EMAIL_3)
                                                 .build();

    public static final UserId LIBRARIAN_ID = UserId.newBuilder()
                                                    .setEmail(LIBRARIAN_EMAIL)
                                                    .build();
    public static final Isbn ISBN = Isbn.newBuilder()
                                        .setValue("0201485672")
                                        .build();

    public static final BookDetails BOOK_DETAILS = BookDetails.newBuilder()
                                                              .setIsbn(ISBN)
                                                              .setTitle(BOOK_TITLE)
                                                              .setAuthor(AUTHOR_NAME)
                                                              .setBookCoverUrl(COVER_URL)
                                                              .setSynopsis(BOOK_SYNOPSIS)
                                                              .addCategories(BOOK_CATEGORY)
                                                              .build();

    public static final BookDetails BOOK_DETAILS_2 = BookDetails.newBuilder()
                                                                .setTitle(BOOK_TITLE_2)
                                                                .setAuthor(AUTHOR_NAME_2)
                                                                .setBookCoverUrl(COVER_URL_2)
                                                                .setSynopsis(BOOK_SYNOPSIS_2)
                                                                .addCategories(BOOK_CATEGORY_2)
                                                                .build();

    public static final String CUSTOM_REASON = "The book was burned damaged";

    public static final InventoryId INVENTORY_ID = InventoryId.newBuilder()
                                                              .setBookId(BOOK_ID)
                                                              .build();
    public static final InventoryItemId INVENTORY_ITEM_ID_1 = InventoryItemId.newBuilder()
                                                                             .setBookId(BOOK_ID)
                                                                             .setItemNumber(1)
                                                                             .build();

    public static final InventoryItemId INVENTORY_ITEM_ID_2 = InventoryItemId.newBuilder()
                                                                             .setBookId(BOOK_ID)
                                                                             .setItemNumber(2)
                                                                             .build();

    public static final Rfid RFID = Rfid.newBuilder()
                                        .setValue("4321")
                                        .build();

    public static final WriteOffReason REASON = WriteOffReason.newBuilder()
                                                              .setOutdated(true)
                                                              .build();

    public static final Loan LOAN = Loan.newBuilder()
                                        .setLoanId(LoanId.newBuilder()
                                                         .setValue(1))
                                        .setInventoryItemId(INVENTORY_ITEM_ID_1)
                                        .setWhenDue(getCurrentTime())
                                        .build();

    public static final Isbn ISBN_2 = Isbn.newBuilder()
                                          .setValue("0256314523")
                                          .build();

    public static final BookDetailsChange DETAILS_CHANGE = BookDetailsChange.newBuilder()
                                                                            .setNewBookDetails(
                                                                                    BOOK_DETAILS_2)
                                                                            .setPreviousBookDetails(
                                                                                    BOOK_DETAILS)
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

    private static final int LOAN_PERIOD = 60 * 60 * 24 * 14;

    // Timestamp on 01.01.1970 00:00
    public static final Timestamp DEFAULT_TIMESTAMP1 = Timestamp.newBuilder()
                                                                .setNanos(0)
                                                                .setSeconds(0)
                                                                .build();
    public static final Timestamp DEFAULT_TIMESTAMP2 = Timestamp.newBuilder()
                                                                .setNanos(0)
                                                                .setSeconds(213456789)
                                                                .build();

    public static final Timestamp DUE_TIMESTAMP = Timestamp.newBuilder()
                                                           .setSeconds(LOAN_PERIOD)
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
    public static final PersonName USER_NAME = PersonName.newBuilder()
                                                         .setGivenName("Ivan")
                                                         .setFamilyName("Petrov")
                                                         .build();

    public static final InventoryItemState IN_LIBRARY_ITEM_STATE = InventoryItemState.newBuilder()
                                                                                     .setItemId(
                                                                                             INVENTORY_ITEM_ID_1)
                                                                                     .setInLibrary(
                                                                                             true)
                                                                                     .build();

    public static final LoanDetails LOAN_DETAILS = LoanDetails.newBuilder()
                                                              .setUserId(USER_ID)
                                                              .setUserName(USER_NAME)
                                                              .setEmail(USER_EMAIL_1)
                                                              .setWhenTaken(DEFAULT_DATE1)
                                                              .setWhenDue(DEFAULT_DUE_DATE)
                                                              .setOverdue(false)
                                                              .build();
    /**
     * today's date
     */
    public static final LocalDate CURRENT_DATE = toLocalDate(getCurrentTime());

    /**
     * 14 days after today
     */
    public static final LocalDate DUE_DATE = addDays(CURRENT_DATE, 14);
    public static final LoanDetails LOAN_DETAILS_TODAY = LoanDetails.newBuilder()
                                                                    .setUserId(USER_ID)
                                                                    .setUserName(USER_NAME)
                                                                    .setEmail(USER_EMAIL_1)
                                                                    .setWhenTaken(CURRENT_DATE)
                                                                    .setWhenDue(DUE_DATE)
                                                                    .setOverdue(false)
                                                                    .build();

    public static final LoanDetails LOAN_DETAILS_TODAY2 = LoanDetails.newBuilder()
                                                                     .setUserId(USER_ID)
                                                                     .setUserName(USER_NAME)
                                                                     .setEmail(USER_EMAIL_1)
                                                                     .setWhenTaken(CURRENT_DATE)
                                                                     .setWhenDue(
                                                                             addDays(CURRENT_DATE,
                                                                                     14 * 2))
                                                                     .setOverdue(false)
                                                                     .build();
    public static final InventoryItemState BORROWED_ITEM_STATE = InventoryItemState.newBuilder()
                                                                                   .setItemId(
                                                                                           INVENTORY_ITEM_ID_1)
                                                                                   .setLoanDetails(
                                                                                           LOAN_DETAILS)
                                                                                   .build();

    public static final InventoryItemState BORROWED_ITEM_STATE_TODAY = InventoryItemState.newBuilder()
                                                                                         .setItemId(
                                                                                                 INVENTORY_ITEM_ID_1)
                                                                                         .setLoanDetails(
                                                                                                 LOAN_DETAILS_TODAY)
                                                                                         .build();
    public static final InventoryItemState BORROWED_ITEM_STATE_TODAY2 = InventoryItemState.newBuilder()
                                                                                          .setItemId(
                                                                                                  INVENTORY_ITEM_ID_1)
                                                                                          .setLoanDetails(
                                                                                                  LOAN_DETAILS_TODAY2)
                                                                                          .build();

    public static final InventoryItemState LOST_ITEM_STATE = InventoryItemState.newBuilder()
                                                                               .setItemId(
                                                                                       INVENTORY_ITEM_ID_1)
                                                                               .setLost(true)
                                                                               .build();

    public static final LoanDetails OVERDUE_LOAN_DETAILS = LoanDetails.newBuilder()
                                                                      .setUserId(USER_ID)
                                                                      .setUserName(USER_NAME)
                                                                      .setEmail(USER_EMAIL_1)
                                                                      .setWhenTaken(DEFAULT_DATE1)
                                                                      .setWhenDue(DEFAULT_DUE_DATE)
                                                                      .setOverdue(true)
                                                                      .build();
    public static final InventoryItemState OVERDUE_ITEM_STATE = InventoryItemState.newBuilder()
                                                                                  .setItemId(
                                                                                          INVENTORY_ITEM_ID_1)
                                                                                  .setLoanDetails(
                                                                                          OVERDUE_LOAN_DETAILS)
                                                                                  .build();

    private static final Book BOOK = Book.newBuilder()
                                         .setBookDetails(BOOK_DETAILS)
                                         .build();

    public static final Function<InventoryId, Book> INVENTORY_ID_TO_BOOK = inventoryId -> BOOK;

}
