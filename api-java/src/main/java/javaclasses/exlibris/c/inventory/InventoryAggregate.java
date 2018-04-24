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

import com.google.common.collect.Iterables;
import com.google.protobuf.Empty;
import com.google.protobuf.Timestamp;
import io.spine.core.React;
import io.spine.server.aggregate.Aggregate;
import io.spine.server.aggregate.Apply;
import io.spine.server.command.Assign;
import io.spine.server.tuple.EitherOfTwo;
import io.spine.server.tuple.Pair;
import io.spine.server.tuple.Triplet;
import javaclasses.exlibris.BookId;
import javaclasses.exlibris.Inventory;
import javaclasses.exlibris.InventoryId;
import javaclasses.exlibris.InventoryItem;
import javaclasses.exlibris.InventoryItemId;
import javaclasses.exlibris.InventoryVBuilder;
import javaclasses.exlibris.Isbn62;
import javaclasses.exlibris.Loan;
import javaclasses.exlibris.LoanId;
import javaclasses.exlibris.Reservation;
import javaclasses.exlibris.Rfid;
import javaclasses.exlibris.UserId;
import javaclasses.exlibris.WriteOffReason;
import javaclasses.exlibris.c.AppendInventory;
import javaclasses.exlibris.c.BookAdded;
import javaclasses.exlibris.c.BookBecameAvailable;
import javaclasses.exlibris.c.BookBorrowed;
import javaclasses.exlibris.c.BookLost;
import javaclasses.exlibris.c.BookReadyToPickup;
import javaclasses.exlibris.c.BookRemoved;
import javaclasses.exlibris.c.BookReturned;
import javaclasses.exlibris.c.BorrowBook;
import javaclasses.exlibris.c.CancelReservation;
import javaclasses.exlibris.c.ExtendLoanPeriod;
import javaclasses.exlibris.c.InventoryAppended;
import javaclasses.exlibris.c.InventoryCreated;
import javaclasses.exlibris.c.InventoryDecreased;
import javaclasses.exlibris.c.InventoryRemoved;
import javaclasses.exlibris.c.LoanBecameOverdue;
import javaclasses.exlibris.c.LoanBecameShouldReturnSoon;
import javaclasses.exlibris.c.LoanPeriodExtended;
import javaclasses.exlibris.c.LoansBecameExtensionAllowed;
import javaclasses.exlibris.c.MarkLoanOverdue;
import javaclasses.exlibris.c.MarkLoanShouldReturnSoon;
import javaclasses.exlibris.c.MarkReservationExpired;
import javaclasses.exlibris.c.ReportLostBook;
import javaclasses.exlibris.c.ReservationAdded;
import javaclasses.exlibris.c.ReservationBecameLoan;
import javaclasses.exlibris.c.ReservationCanceled;
import javaclasses.exlibris.c.ReservationPickUpPeriodExpired;
import javaclasses.exlibris.c.ReserveBook;
import javaclasses.exlibris.c.ReturnBook;
import javaclasses.exlibris.c.WriteBookOff;
import javaclasses.exlibris.c.rejection.BookAlreadyBorrowed;
import javaclasses.exlibris.c.rejection.BookAlreadyReserved;
import javaclasses.exlibris.c.rejection.CannotCancelMissingReservation;
import javaclasses.exlibris.c.rejection.CannotExtendLoanPeriod;
import javaclasses.exlibris.c.rejection.CannotReturnMissingBook;
import javaclasses.exlibris.c.rejection.CannotReturnNonBorrowedBook;
import javaclasses.exlibris.c.rejection.CannotWriteMissingBookOff;
import javaclasses.exlibris.c.rejection.NonAvailableBook;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static io.spine.time.Time.getCurrentTime;
import static javaclasses.exlibris.LoanStatus.LOAN_OVERDUE;
import static javaclasses.exlibris.LoanStatus.LOAN_RECENT;
import static javaclasses.exlibris.LoanStatus.LOAN_SOULD_RETURN_SOON;
import static javaclasses.exlibris.c.inventory.InventoryAggregateRejections.BorrowBookRejection.bookAlreadyBorrowed;
import static javaclasses.exlibris.c.inventory.InventoryAggregateRejections.BorrowBookRejection.nonAvailableBook;
import static javaclasses.exlibris.c.inventory.InventoryAggregateRejections.ReserveBookRejection.bookAlreadyBorrowed;
import static javaclasses.exlibris.c.inventory.InventoryAggregateRejections.ReserveBookRejection.bookAlreadyReserved;
import static javaclasses.exlibris.c.inventory.InventoryAggregateRejections.ReturnBookRejection.cannotReturnNonBorrowedBook;
import static javaclasses.exlibris.c.inventory.InventoryAggregateRejections.ReturnBookRejection.throwCannotReturnMissingBook;
import static javaclasses.exlibris.c.inventory.InventoryAggregateRejections.cannotCancelMissingReservation;
import static javaclasses.exlibris.c.inventory.InventoryAggregateRejections.cannotExtendLoanPeriod;
import static javaclasses.exlibris.c.inventory.InventoryAggregateRejections.cannotWriteMissingBookOff;

/**
 * The aggregate managing the state of a {@link Inventory}.
 *
 * @author Alexander Karpets
 * @author Paul Ageyev
 */

@SuppressWarnings({"ClassWithTooManyMethods", /* Inventory definition cannot be separated and should
                                                 process all commands and events related to it
                                                 according to the domain model.
                                                 The {@code Aggregate} does it with methods
                                                 annotated as {@code Assign} and {@code Apply}.
                                                 In that case class has too many methods.*/
        "OverlyCoupledClass"})  /* As each method needs dependencies  necessary to perform execution
                                                 that class also overly coupled.*/

public class InventoryAggregate extends Aggregate<InventoryId, Inventory, InventoryVBuilder> {

    /**
     * The loan period time in seconds.
     *
     * <p>This period equals to two weeks.
     * secondsInMinute * minutesInHour * hoursInDay * daysInTwoWeeks
     */
    private static final int LOAN_PERIOD = 60 * 60 * 24 * 14;

    /**
     * Borrow period.
     *
     * <p>secondsInMinute * minutesInHours * hoursInTwoDays
     */
    private static final int OPEN_FOR_BORROW_PERIOD = 60 * 60 * 48;

    /**
     * Creates a new instance.
     *
     * @param id the identifier for the new aggregate
     */
    public InventoryAggregate(InventoryId id) {
        super(id);
    }

    // @formatter:off

    /**
     * Handles a {@code AppendInventory} command.
     *
     * <p>For details see {@link AppendInventory}.
     * <p>Returns the following event combinations:
     *
     * <ul>
     * <li>{@code InventoryAppended, BookBecameAvailable, null} - when the reservations
     * list is empty.
     * <li>{@code InventoryAppended, BookReadyToPickup, null} - when the added item
     * becomes ready to pick up for the first in the reservations queue and other
     * readers still have reservations.
     * <li>{@code InventoryAppended, BookReadyToPickup, LoansBecameExtensionAllowed} - when the
     * added item becomes ready to pick up for the first in the reservations queue and there aro no
     * readers in the queue except him. In that case all recent loans of this book
     * become allowed for extension.
     * </ul>
     *
     * @param cmd command with the identifier of a specific item.
     * @return the {@link Triplet} of the events.
     */
    // @formatter:on
    @Assign
    Triplet<InventoryAppended,
            EitherOfTwo<BookBecameAvailable, BookReadyToPickup>,
            Optional<LoansBecameExtensionAllowed>> handle(AppendInventory cmd) {
        final InventoryId inventoryId = cmd.getInventoryId();
        final InventoryItemId inventoryItemId = cmd.getInventoryItemId();

        final InventoryAppended inventoryAppended = createInventoryAppendedEvent(cmd);

        if (!isBookReserved()) {
            final BookBecameAvailable becameAvailable =
                    createBookBecameAvailableEvent(inventoryId, inventoryItemId);
            final Triplet result = Triplet.withNullable(inventoryAppended,
                                                        becameAvailable,
                                                        null);
            return result;
        }

        final BookReadyToPickup bookReadyToPickup =
                createBookReadyToPickupEvent(inventoryId, inventoryItemId);

        if (isBookReservedBySingleUser() && isBookBorrowed()) {
            final LoansBecameExtensionAllowed loansBecameExtensionAllowed
                    = createLoansBecameExtensionAllowedEvent();
            final Triplet result = Triplet.of(
                    inventoryAppended, bookReadyToPickup,
                    loansBecameExtensionAllowed);
            return result;
        }

        Triplet result = Triplet.withNullable(inventoryAppended, bookReadyToPickup, null);
        return result;
    }

    /**
     * Handles a {@code WriteBookOff} command.
     *
     * <p>For details see {@link WriteBookOff}.
     *
     * @param cmd command with a reason of a book writing off.
     * @return a {@code WriteBookOff} event.
     * @throws CannotWriteMissingBookOff if that book is missing.
     */
    @Assign
    InventoryDecreased handle(WriteBookOff cmd) throws CannotWriteMissingBookOff {
        final List<InventoryItem> inventoryItems = getState().getInventoryItemsList();

        InventoryDecreased result = null;

        for (InventoryItem inventoryItem : inventoryItems) {
            if (inventoryItem.getInventoryItemId()
                             .equals(cmd.getInventoryItemId())) {

                final InventoryId inventoryId = cmd.getInventoryId();
                final InventoryItemId inventoryItemId = cmd.getInventoryItemId();
                final UserId librarianId = cmd.getLibrarianId();
                final WriteOffReason writeOffReason = cmd.getWriteBookOffReason();
                result = InventoryDecreased.newBuilder()
                                           .setInventoryId(inventoryId)
                                           .setInventoryItemId(inventoryItemId)
                                           .setWhenDecreased(getCurrentTime())
                                           .setLibrarianId(librarianId)
                                           .setWriteOffReason(writeOffReason)
                                           .build();
            }
        }
        if (result == null) {
            throw cannotWriteMissingBookOff(cmd);
        }
        return result;
    }

    /**
     * Handles a {@code ReserveBook} command.
     *
     * <p>For details see {@link ReserveBook}.
     *
     * @param cmd command with the reason of the book writing off.
     * @return a {@code ReservationAdded} event.
     * @throws BookAlreadyBorrowed if a book already borrowed.
     * @throws BookAlreadyReserved if a reservation already exists.
     */
    @Assign
    ReservationAdded handle(ReserveBook cmd) throws BookAlreadyBorrowed,
                                                    BookAlreadyReserved {
        final InventoryId inventoryId = cmd.getInventoryId();
        final UserId userId = cmd.getUserId();

        if (isBookBorrowedByUser(userId)) {
            throw bookAlreadyBorrowed(cmd);
        }

        if (userHasReservation(userId)) {
            throw bookAlreadyReserved(cmd);

        }
        final ReservationAdded result = ReservationAdded.newBuilder()
                                                        .setInventoryId(inventoryId)
                                                        .setForWhomReserved(userId)
                                                        .setWhenCreated(getCurrentTime())
                                                        .build();
        return result;
    }

    /**
     * Handles a {@code BorrowBook} command.
     *
     * <p>For details see {@link BorrowBook}.
     *
     * <p>User can take a book if amount of books in the library is more than amount of reservations, or if
     * as many "next" reservations as books available and among these reservations
     * one belongs to the user.
     *
     * @param cmd command with the reason of the book writing off.
     * @return a {@code ReservationAdded} event.
     * @throws BookAlreadyBorrowed if a book already borrowed.
     * @throws NonAvailableBook    if non-available book.
     */
    @Assign
    Pair<BookBorrowed, Optional<ReservationBecameLoan>> handle(BorrowBook cmd) throws
                                                                               BookAlreadyBorrowed,
                                                                               NonAvailableBook {
        final List<InventoryItem> inventoryItems = getState().getInventoryItemsList();

        final int inLibraryCount = getInLibraryCount();
        final UserId userId = cmd.getUserId();

        for (InventoryItem inventoryItem : inventoryItems) {
            if (inventoryItem.getUserId()
                             .equals(userId)) {
                throw bookAlreadyBorrowed(cmd);
            }
        }

        if (getState().getReservationsList()
                      .size() >= inLibraryCount &&
                !(userHasReservation(userId, inLibraryCount))) {

            throw nonAvailableBook(cmd);

        }

        final InventoryId inventoryId = cmd.getInventoryId();
        final InventoryItemId inventoryItemId = cmd.getInventoryItemId();

        final LoanId loanId = LoanId.newBuilder()
                                    .setValue(getCurrentTime().getSeconds())
                                    .build();
        final BookBorrowed bookBorrowed = BookBorrowed.newBuilder()
                                                      .setInventoryId(inventoryId)
                                                      .setInventoryItemId(inventoryItemId)
                                                      .setWhoBorrowed(userId)
                                                      .setLoanId(loanId)
                                                      .setWhenBorrowed(getCurrentTime())
                                                      .build();
        Pair result = Pair.withNullable(bookBorrowed, null);

        if (userHasReservation(cmd.getUserId())) {
            final ReservationBecameLoan reservationBecameLoan =
                    ReservationBecameLoan.newBuilder()
                                         .setInventoryId(inventoryId)
                                         .setUserId(userId)
                                         .setWhenBecameLoan(getCurrentTime())
                                         .build();
            result = Pair.of(bookBorrowed, reservationBecameLoan);
        }
        return result;
    }

    /**
     * Checks for the availability of reservations from the user.
     *
     * @param userId the book that a user is going to borrow.
     * @return true if the such reservation exists.
     */
    private boolean userHasReservation(UserId userId) {
        final boolean userHasReservation =
                !getState().getReservationsList()
                           .isEmpty() &&
                        getState().getReservationsList()
                                  .get(0)
                                  .getWhoReserved()
                                  .equals(userId);
        return userHasReservation;
    }

    /**
     * Counts the quantity of books in a library.
     *
     * @return the quantity of books in a library.
     */
    private int getInLibraryCount() {
        final List<InventoryItem> inventoryItems = getState().getInventoryItemsList();
        int inLibraryCount = 0;

        for (InventoryItem inventoryItem : inventoryItems) {
            if (inventoryItem.getInLibrary()) {
                inLibraryCount++;
            }
        }
        return inLibraryCount;
    }

    /**
     * Handles a {@code Empty} event.
     *
     * @param event a {@code Empty} event message.
     */
    @Apply
    void emptyEvent(Empty event) {
        // Uses when command calls an empty event.
    }

    private boolean userHasReservation(UserId userId, int inLibraryCount) {
        final List<Reservation> topReservations = getState().getReservationsList()
                                                            .subList(0, inLibraryCount);

        final Optional<Reservation> reservationOptional =
                topReservations.stream()
                               .filter(reservation -> reservation.getWhoReserved()
                                                                 .equals(userId))
                               .findFirst();

        return reservationOptional.isPresent();
    }

    /**
     * Handles a {@code MarkLoanOverdue} command.
     *
     * <p>For details see {@link MarkLoanOverdue}.
     *
     * @param cmd command from system that marks the loan as overdue.
     * @return a {@code LoanBecameOverdue} event.
     */
    @Assign
    LoanBecameOverdue handle(MarkLoanOverdue cmd) {
        final InventoryId inventoryId = cmd.getInventoryId();
        final LoanId loanId = cmd.getLoanId();
        final int loanPosition = getLoanIndexByLoanId(loanId);
        final Loan loan = getState().getLoans(loanPosition);
        final UserId whoBorrowed = loan.getWhoBorrowed();
        final InventoryItemId inventoryItemId = loan.getInventoryItemId();

        final LoanBecameOverdue result = LoanBecameOverdue.newBuilder()
                                                          .setInventoryId(inventoryId)
                                                          .setInventoryItemId(inventoryItemId)
                                                          .setLoanId(loanId)
                                                          .setUserId(whoBorrowed)
                                                          .setWhenBecameOverdue(getCurrentTime())
                                                          .build();
        return result;
    }

    /**
     * Handles a {@code MarkLoanShouldReturnSoon} command.
     *
     * <p>For details see {@link MarkLoanShouldReturnSoon}.
     *
     * @param cmd command from system that marks the loan as should be returned soon.
     * @return a {@code LoanBecameShouldReturnSoon} event.
     */
    @Assign
    LoanBecameShouldReturnSoon handle(MarkLoanShouldReturnSoon cmd) {
        final InventoryId inventoryId = cmd.getInventoryId();
        final LoanId loanId = cmd.getLoanId();
        final int loanPosition = getLoanIndexByLoanId(loanId);
        final Loan loan = getState().getLoans(loanPosition);
        final UserId whoBorrowed = loan.getWhoBorrowed();
        final InventoryItemId inventoryItemId = loan.getInventoryItemId();

        final LoanBecameShouldReturnSoon result =
                LoanBecameShouldReturnSoon.newBuilder()
                                          .setInventoryId(inventoryId)
                                          .setInventoryItemId(inventoryItemId)
                                          .setLoanId(loanId)
                                          .setUserId(whoBorrowed)
                                          .setWhenBecameShouldReturnSoon(getCurrentTime())
                                          .build();
        return result;
    }

    /**
     * Handles a {@code ExtendLoanPeriod} command.
     *
     * <p>For details see {@link ExtendLoanPeriod}.
     *
     * @param cmd command with the identifier of a loan
     *            that a user is going to extend.
     * @return a {@code LoanPeriodExtended} event.
     * @throws CannotExtendLoanPeriod if a loan period extension isn’t possible.
     */
    @Assign
    LoanPeriodExtended handle(ExtendLoanPeriod cmd) throws CannotExtendLoanPeriod {
        final UserId userId = cmd.getUserId();

        if (isBookReserved() || !isBookBorrowedByUser(userId)) {
            throw cannotExtendLoanPeriod(cmd);
        }

        final InventoryId inventoryId = cmd.getInventoryId();
        final LoanId loanId = cmd.getLoanId();

        final int loanPosition = getLoanIndexByLoanId(loanId);
        final Loan loan = getState().getLoans(loanPosition);
        final Timestamp previousDueDate = loan.getWhenDue();
        final InventoryItemId inventoryItemId = loan.getInventoryItemId();

        final long newDueDateInSeconds = previousDueDate.getSeconds() +
                LOAN_PERIOD;
        final Timestamp newDueDate = Timestamp.newBuilder()
                                              .setSeconds(newDueDateInSeconds)
                                              .build();

        final LoanPeriodExtended result = LoanPeriodExtended.newBuilder()
                                                            .setInventoryId(inventoryId)
                                                            .setInventoryItemId(inventoryItemId)
                                                            .setLoanId(loanId)
                                                            .setUserId(userId)
                                                            .setWhenExtended(getCurrentTime())
                                                            .setPreviousDueDate(previousDueDate)
                                                            .setNewDueDate(newDueDate)
                                                            .build();
        return result;
    }

    /**
     * Handles a {@code CancelReservation} command.
     *
     * <p>For details see {@link CancelReservation}.
     *
     * @param cmd command with the identifier of a reservation
     *            that a user is going to cancel.
     * @return a {@code ReservationCanceled} event.
     * @throws CannotCancelMissingReservation if a reservation is missing.
     */
    @Assign
    ReservationCanceled handle(CancelReservation cmd) throws CannotCancelMissingReservation {
        if (!userHasReservation(cmd.getUserId())) {
            throw cannotCancelMissingReservation(cmd);
        }

        final InventoryId inventoryId = cmd.getInventoryId();
        final UserId userId = cmd.getUserId();
        final ReservationCanceled result = ReservationCanceled.newBuilder()
                                                              .setInventoryId(inventoryId)
                                                              .setWhoCanceled(userId)
                                                              .setWhenCanceled(getCurrentTime())
                                                              .build();
        return result;
    }

    /**
     * Handles a {@code MarkReservationExpired} command.
     *
     * <p>For details see {@link MarkReservationExpired}.
     *
     * @param cmd system command that contains the identifier of an expired reservation.
     * @return a {@code ReservationPickUpPeriodExpired} event.
     */
    @Assign
    ReservationPickUpPeriodExpired handle(MarkReservationExpired cmd) {
        final InventoryId inventoryId = cmd.getInventoryId();
        final UserId userId = cmd.getUserId();
        final ReservationPickUpPeriodExpired result =
                ReservationPickUpPeriodExpired.newBuilder()
                                              .setInventoryId(inventoryId)
                                              .setUserId(userId)
                                              .setWhenExpired(getCurrentTime())
                                              .build();
        return result;
    }

    /**
     * Handles a {@code ReturnBook} command.
     *
     * <p>For details see {@link ReturnBook}.
     *
     * @param cmd command with an identifier of the book
     *            that the user is going to return.
     * @return a {@code InventoryAppended} event in pair with either {@code BookBecameAvailable} or
     * {@code BookBecameAvailable} events.
     * @throws CannotReturnNonBorrowedBook if a book isn’t borrowed by the user.
     * @throws CannotReturnMissingBook     if a book is missing.
     */
    @Assign
    Pair<InventoryAppended, EitherOfTwo<BookBecameAvailable, BookReadyToPickup>> handle(
            ReturnBook cmd) throws CannotReturnNonBorrowedBook,
                                   CannotReturnMissingBook {
        if (!inventoryItemExists(cmd.getInventoryItemId())) {
            throw throwCannotReturnMissingBook(cmd);
        }
        if (!isBookBorrowedByUser(cmd.getUserId())) {
            throw cannotReturnNonBorrowedBook(cmd);
        }

        final InventoryId inventoryId = cmd.getInventoryId();
        final InventoryItemId inventoryItemId = cmd.getInventoryItemId();
        final UserId userId = cmd.getUserId();

        final BookReturned bookReturned = BookReturned.newBuilder()
                                                      .setInventoryId(inventoryId)
                                                      .setInventoryItemId(inventoryItemId)
                                                      .setWhoReturned(userId)
                                                      .setWhenReturned(getCurrentTime())
                                                      .build();

        final Pair result = Pair.withEither(bookReturned,
                                            becameAvailableOrReadyToPickup(inventoryId,
                                                                           inventoryItemId));

        return result;
    }

    /**
     * Handles a {@code ReportLostBook} command.
     *
     * <p>For details see {@link ReportLostBook}.
     *
     * @param cmd command that contains the identifier of a lost book
     *            and a user who lost it.
     * @return a {@code BookLost} event.
     */
    @Assign
    BookLost handle(ReportLostBook cmd) {
        final InventoryId inventoryId = cmd.getInventoryId();
        final InventoryItemId inventoryItemId = cmd.getInventoryItemId();
        final UserId userId = cmd.getWhoLost();
        final BookLost result = BookLost.newBuilder()
                                        .setInventoryId(inventoryId)
                                        .setInventoryItemId(inventoryItemId)
                                        .setWhoLost(userId)
                                        .setWhenReported(getCurrentTime())
                                        .build();
        return result;
    }

    /**
     * Reacts on a {@code BookAdded} event.
     *
     * @param event stimulus for reacting.
     * @return a {@code InventoryCreated} event.
     */
    @React
    InventoryCreated on(BookAdded event) {
        final InventoryId inventoryId = InventoryId.newBuilder()
                                                   .setBookId(
                                                           event.getBookId())
                                                   .build();
        final InventoryCreated result = InventoryCreated.newBuilder()
                                                        .setInventoryId(inventoryId)
                                                        .setWhenCreated(getCurrentTime())
                                                        .build();
        return result;
    }

    /**
     * Handles a {@code InventoryCreated} event.
     *
     * <p>For details see {@link InventoryCreated}.
     *
     * @param event a {@code InventoryCreated} event message.
     */
    @Apply
    void inventoryCreated(InventoryCreated event) {
        getBuilder().setInventoryId(event.getInventoryId());
    }

    /**
     * Reacts on a {@code BookRemoved} event.
     *
     * @param event stimulus for reacting.
     * @return a {@code InventoryRemoved} event.
     */
    @React
    InventoryRemoved on(BookRemoved event) {
        final InventoryId inventoryId = InventoryId.newBuilder()
                                                   .setBookId(
                                                           event.getBookId())
                                                   .build();
        final InventoryRemoved result =
                InventoryRemoved.newBuilder()
                                .setInventoryId(inventoryId)
                                .setWhenRemoved(getCurrentTime())
                                .build();
        return result;
    }

    /**
     * Handles a {@code InventoryRemoved} event.
     *
     * <p>For details see {@link InventoryRemoved}.
     *
     * @param event the {@code InventoryRemoved} event message.
     */
    @Apply
    void inventoryRemoved(InventoryRemoved event) {
        getBuilder().clearInventoryId()
                    .clearInventoryItems();
    }

    /**
     * Handles a {@code InventoryAppended} event.
     *
     * <p>For details see {@link InventoryAppended}.
     *
     * @param event a {@code InventoryAppended} event message.
     */
    @Apply
    void inventoryAppended(InventoryAppended event) {
        final InventoryItemId inventoryItemId = event.getInventoryItemId();
        final InventoryItem newInventoryItem = InventoryItem.newBuilder()
                                                            .setInLibrary(true)
                                                            .setInventoryItemId(inventoryItemId)
                                                            .setInLibrary(true)
                                                            .build();
        getBuilder().addInventoryItems(newInventoryItem);
    }

    /**
     * Handles a {@code BookBecameAvailable} event.
     *
     * <p>For details see {@link BookBecameAvailable}.
     *
     * @param event a {@code BookBecameAvailable} event message.
     */
    @Apply
    void bookBecameAvailable(BookBecameAvailable event) {
        final List<InventoryItem> inventoryItems = getBuilder().getInventoryItems();

        final InventoryItemId inventoryItemId = event.getInventoryItemId();
        final int availableBookIndex = getInventoryItemIndexById(inventoryItemId);
        final InventoryItem inventoryItem = InventoryItem.newBuilder()
                                                         .setInventoryItemId(inventoryItemId)
                                                         .setInLibrary(true)
                                                         .build();
        getBuilder().setInventoryItems(availableBookIndex, inventoryItem);
    }

    /**
     * A book becomes available for a user.
     *
     * <p>This method does not change a state of an aggregate but this event is necessary for a read side.
     *
     * @param event A book is ready to pickup for a user who is next in a queue.
     */
    @SuppressWarnings("all")
    @Apply
    void bookReadyToPickup(BookReadyToPickup event) {
    }

    /**
     * Handles a {@code ReservationBecameLoan} event.
     *
     * <p>For details see {@link ReservationBecameLoan}.
     *
     * @param event a {@code ReservationBecameLoan} event message.
     */
    @Apply
    void reservationBecameLoan(ReservationBecameLoan event) {
        final List<Reservation> reservations = getBuilder().getReservations();

        final UserId userId = event.getUserId();
        final OptionalInt optionalPosition = IntStream.range(0, reservations.size())
                                                      .filter(position -> reservations.get(position)
                                                                                      .getWhoReserved()
                                                                                      .equals(userId))
                                                      .findFirst();
        int reservationPosition = -1;
        if (optionalPosition.isPresent()) {
            reservationPosition = optionalPosition.getAsInt();
        }
        getBuilder().removeReservations(reservationPosition);
    }

    /**
     * Handles a {@code InventoryDecreased} event.
     *
     * <p>For details see {@link InventoryDecreased}.
     *
     * @param event a {@code InventoryDecreased} event message.
     */
    @Apply
    void inventoryDecreased(InventoryDecreased event) {
        final List<InventoryItem> inventoryItems = getBuilder().getInventoryItems();

        final InventoryItemId inventoryItemId = event.getInventoryItemId();
        final int decreaseItemPosition = getInventoryItemIndexById(inventoryItemId);

        getBuilder().removeInventoryItems(decreaseItemPosition);
    }

    /**
     * Handles a {@code ReservationAdded} event.
     *
     * <p>For details see {@link ReservationAdded}.
     *
     * @param event a {@code ReservationAdded} event message.
     */
    @Apply
    void reservationAdded(ReservationAdded event) {
        final Isbn62 isbn62 = event.getInventoryId()
                                   .getBookId()
                                   .getIsbn62();
        final BookId.Builder bookId = BookId.newBuilder()
                                            .setIsbn62(isbn62);
        final Reservation newReservation =
                Reservation.newBuilder()
                           .setBookId(bookId)
                           .setWhenCreated(event.getWhenCreated())
                           .setWhoReserved(event.getForWhomReserved())
                           .build();
        getBuilder().addReservations(newReservation);
    }

    /**
     * Handles a {@code BookBorrowed} event.
     *
     * <p>For details see {@link BookBorrowed}.
     *
     * @param event a {@code BookBorrowed} event message.
     */
    @Apply
    void bookBorrowed(BookBorrowed event) {
        final List<InventoryItem> inventoryItems = getBuilder().getInventoryItems();

        final InventoryItemId inventoryItemId = event.getInventoryItemId();
        final int borrowItemPosition = getInventoryItemIndexById(inventoryItemId);

        final InventoryItem borrowedItem =
                InventoryItem.newBuilder()
                             .setBorrowed(true)
                             .setUserId(event.getWhoBorrowed())
                             .setInventoryItemId(inventoryItemId)
                             .build();

        final Timestamp whenDue = Timestamp.newBuilder()
                                           .setSeconds(System.currentTimeMillis() / 1000 +
                                                               LOAN_PERIOD)
                                           .build();
        final Loan loan = Loan.newBuilder()
                              .setLoanId(event.getLoanId())
                              .setInventoryItemId(inventoryItemId)
                              .setStatus(LOAN_RECENT)
                              .setWhoBorrowed(event.getWhoBorrowed())
                              .setWhenTaken(getCurrentTime())
                              .setWhenDue(whenDue)
                              .build();

        getBuilder().setInventoryItems(borrowItemPosition, borrowedItem)
                    .addLoans(loan);
    }

    /**
     * Handles a {@code LoanBecameOverdue} event.
     *
     * <p>For details see {@link LoanBecameOverdue}.
     *
     * @param event a {@code LoanBecameOverdue} event message.
     */
    @Apply
    void loanBecameOverdue(LoanBecameOverdue event) {
        final List<Loan> loans = getBuilder().getLoans();

        final int loanPosition = getLoanIndexByLoanId(event.getLoanId());
        getBuilder().setLoans(loanPosition, Loan.newBuilder(loans.get(loanPosition))
                                                .setStatus(LOAN_OVERDUE)
                                                .build());
    }

    /**
     * Handles a {@code LoanBecameShouldReturnSoon} event.
     *
     * <p>For details see {@link LoanBecameShouldReturnSoon}.
     *
     * @param event a {@code LoanBecameShouldReturnSoon} event message.
     */
    @Apply
    void loanBecameShouldReturnSoon(LoanBecameShouldReturnSoon event) {
        final List<Loan> loans = getBuilder().getLoans();

        final int loanPosition = getLoanIndexByLoanId(event.getLoanId());
        getBuilder().setLoans(loanPosition, Loan.newBuilder(loans.get(loanPosition))
                                                .setStatus(LOAN_SOULD_RETURN_SOON)
                                                .build());
    }

    /**
     * Handles a {@code LoanPeriodExtended} event.
     *
     * <p>For details see {@link LoanPeriodExtended}.
     *
     * @param event a {@code LoanPeriodExtended} event message.
     */
    @Apply
    void loanPeriodExtended(LoanPeriodExtended event) {
        final LoanId loanId = event.getLoanId();
        final int loanPosition = getLoanIndexByLoanId(loanId);

        final Loan previousLoan = getBuilder().getLoans()
                                              .get(loanPosition);

        final Loan loan = Loan.newBuilder(previousLoan)
                              .setWhenDue(event.getNewDueDate())
                              .setStatus(LOAN_RECENT)
                              .build();

        getBuilder().setLoans(loanPosition, loan);
    }

    /**
     * Handles a {@code ReservationCanceled} event.
     *
     * <p>For details see {@link ReservationCanceled}.
     *
     * @param event a {@code ReservationCanceled} event message.
     */
    @Apply
    void reservationCanceled(ReservationCanceled event) {
        final UserId whoCanceled = event.getWhoCanceled();
        final int reservationCancelIndex = getReservationIndexByUserId(whoCanceled);

        getBuilder().removeReservations(reservationCancelIndex);
    }

    /**
     * Handles a {@code ReservationPickUpPeriodExpired} event.
     *
     * <p>For details see {@link ReservationPickUpPeriodExpired}.
     *
     * @param event a {@code ReservationPickUpPeriodExpired} event message.
     */
    @Apply
    void reservationPickUpPeriodExpired(ReservationPickUpPeriodExpired event) {
        final UserId userId = event.getUserId();
        final int reservationPosition = getReservationIndexByUserId(userId);

        getBuilder().removeReservations(reservationPosition);
    }

    /**
     * Handles a {@code BookReturned} event.
     *
     * <p>For details see {@link BookReturned}.
     *
     * @param event a {@code BookReturned} event message.
     */
    @Apply
    void bookReturned(BookReturned event) {
        final int returnedItemPosition =
                getInventoryItemIndexByUserId(event.getWhoReturned());

        final InventoryItem newInventoryItem =
                InventoryItem.newBuilder()
                             .setInventoryItemId(event.getInventoryItemId())
                             .setInLibrary(true)
                             .build();
        getBuilder().setInventoryItems(returnedItemPosition, newInventoryItem);

        final int loanIndex = getLoanIndexByUserId(event.getWhoReturned());
        getBuilder().removeLoans(loanIndex);
    }

    /**
     * Handles a {@code BookLost} event.
     *
     * <p>For details see {@link BookLost}.
     *
     * @param event a {@code BookLost} event message.
     */
    @Apply
    void bookLost(BookLost event) {
        final List<InventoryItem> inventoryItems = getBuilder().getInventoryItems();

        final InventoryItemId inventoryItemId = event.getInventoryItemId();

        final int bookLostItemPosition = getInventoryItemIndexById(inventoryItemId);

        final InventoryItem inventoryItem =
                InventoryItem.newBuilder(inventoryItems.get(bookLostItemPosition))
                             .setLost(true)
                             .build();

        getBuilder().setInventoryItems(bookLostItemPosition, inventoryItem);
    }

    private BookBecameAvailable createBookBecameAvailableEvent(InventoryId inventoryId,
                                                               InventoryItemId inventoryItemId) {
        final Timestamp currentTime = getCurrentTime();
        final BookBecameAvailable bookBecameAvailable =
                BookBecameAvailable.newBuilder()
                                   .setInventoryId(inventoryId)
                                   .setInventoryItemId(inventoryItemId)
                                   .setWhenBecameAvailable(currentTime)
                                   .build();
        return bookBecameAvailable;
    }

    /**
     * Searches for the first reservation in the reservation queue.
     *
     * <p>Creates the event signalizing about the book became available
     * to pick up for the first user in the reservations queue.
     *
     * @param inventoryId     the identifier of an inventory.
     * @param inventoryItemId the identifier of a specific item.
     * @return {@code BookReadyToPickup} event.
     */
    private BookReadyToPickup createBookReadyToPickupEvent(InventoryId inventoryId,
                                                           InventoryItemId inventoryItemId) {
        final Timestamp currentTime = getCurrentTime();
        final List<Reservation> reservationsList = getState().getReservationsList();
        final UserId nextInQueue = reservationsList.get(0)
                                                   .getWhoReserved();

        final long expirationDate = currentTime.getSeconds() + OPEN_FOR_BORROW_PERIOD;

        final Timestamp pickUpDeadline = Timestamp.newBuilder()
                                                  .setSeconds(expirationDate)
                                                  .build();

        final BookReadyToPickup bookReadyToPickup =
                BookReadyToPickup.newBuilder()
                                 .setInventoryId(inventoryId)
                                 .setInventoryItemId(inventoryItemId)
                                 .setForWhom(nextInQueue)
                                 .setWhenBecameReadyToPickup(currentTime)
                                 .setPickUpDeadline(pickUpDeadline)
                                 .build();
        return bookReadyToPickup;
    }

    private LoansBecameExtensionAllowed createLoansBecameExtensionAllowedEvent() {
        final Timestamp currentTime = getCurrentTime();
        final InventoryId inventoryId = getState().getInventoryId();
        final List<Loan> loans = getState().getLoansList();
        final List<UserId> borrowers = loans.stream()
                                            .map(Loan::getWhoBorrowed)
                                            .collect(Collectors.toList());
        final LoansBecameExtensionAllowed result =
                LoansBecameExtensionAllowed.newBuilder()
                                           .setInventoryId(inventoryId)
                                           .setWhenBecame(currentTime)
                                           .addAllBorrowers(borrowers)
                                           .build();
        return result;
    }

    private InventoryAppended createInventoryAppendedEvent(AppendInventory cmd) {
        final InventoryId inventoryId = cmd.getInventoryId();
        final InventoryItemId inventoryItemId = cmd.getInventoryItemId();
        final Rfid rfid = cmd.getRfid();
        final UserId userId = cmd.getLibrarianId();

        final InventoryAppended inventoryAppended =
                InventoryAppended.newBuilder()
                                 .setInventoryId(inventoryId)
                                 .setInventoryItemId(inventoryItemId)
                                 .setRfid(rfid)
                                 .setWhenAppended(getCurrentTime())
                                 .setLibrarianId(userId)
                                 .build();
        return inventoryAppended;
    }

    private boolean isBookReserved() {
        final boolean reservationExists = getState().getReservationsCount() > 0;
        return reservationExists;
    }

    private boolean isBookBorrowed() {
        final boolean isBorrowed = getState().getLoansCount() > 0;
        return isBorrowed;
    }

    /**
     * Checks a book for reservations.
     *
     * @return true if the reservation count is 1.
     */
    private boolean isBookReservedBySingleUser() {
        final boolean reservedOnce = getState().getReservationsCount() == 1;
        return reservedOnce;
    }

    private boolean isBookBorrowedByUser(UserId userId) {
        final List<Loan> loans = getState().getLoansList();
        final boolean any = Iterables.any(loans, item -> item.getWhoBorrowed()
                                                             .equals(userId));
        return any;
    }

    private boolean inventoryItemExists(InventoryItemId inventoryItemId) {
        final List<InventoryItem> inventoryItemsList = getState().getInventoryItemsList();
        final boolean any = Iterables.any(inventoryItemsList, item -> item.getInventoryItemId()
                                                                          .equals(inventoryItemId));
        return any;
    }

    private int getReservationIndexByUserId(UserId userId) {
        final List<Reservation> reservations = getState().getReservationsList();
        final int index = Iterables.indexOf(reservations, item -> item.getWhoReserved()
                                                                      .equals(userId));
        return index;
    }

    private int getLoanIndexByUserId(UserId userId) {
        final List<Loan> loanList = getState().getLoansList();
        final int index = Iterables.indexOf(loanList, item -> item.getWhoBorrowed()
                                                                  .equals(userId));
        return index;
    }

    private int getLoanIndexByLoanId(LoanId loanId) {
        final List<Loan> loansList = getState().getLoansList();
        final int index = Iterables.indexOf(loansList, item -> item.getLoanId()
                                                                   .equals(loanId));
        return index;
    }

    private int getInventoryItemIndexByUserId(UserId userId) {
        final List<InventoryItem> inventoryItems = getState().getInventoryItemsList();
        final int index = Iterables.indexOf(inventoryItems, item -> item.getUserId()
                                                                        .equals(userId));
        return index;
    }

    private int getInventoryItemIndexById(InventoryItemId inventoryItemId) {
        final List<InventoryItem> inventoryItems = getState().getInventoryItemsList();
        final int index = Iterables.indexOf(inventoryItems, item -> item.getInventoryItemId()
                                                                        .equals(inventoryItemId));
        return index;
    }
}
