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

import com.google.common.base.Optional;
import com.google.common.collect.Iterables;
import com.google.protobuf.Empty;
import com.google.protobuf.Timestamp;
import io.spine.core.React;
import io.spine.server.aggregate.Aggregate;
import io.spine.server.aggregate.Apply;
import io.spine.server.command.Assign;
import io.spine.server.tuple.Pair;
import javaclasses.exlibris.BookId;
import javaclasses.exlibris.Inventory;
import javaclasses.exlibris.InventoryId;
import javaclasses.exlibris.InventoryItem;
import javaclasses.exlibris.InventoryItemId;
import javaclasses.exlibris.InventoryVBuilder;
import javaclasses.exlibris.Isbn62;
import javaclasses.exlibris.Loan;
import javaclasses.exlibris.LoanId;
import javaclasses.exlibris.LoanStatus;
import javaclasses.exlibris.Reservation;
import javaclasses.exlibris.UserId;
import javaclasses.exlibris.WriteOffReason;
import javaclasses.exlibris.c.AllowLoansExtension;
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
import javaclasses.exlibris.c.ForbidLoansExtension;
import javaclasses.exlibris.c.InventoryAppended;
import javaclasses.exlibris.c.InventoryCreated;
import javaclasses.exlibris.c.InventoryDecreased;
import javaclasses.exlibris.c.InventoryRemoved;
import javaclasses.exlibris.c.LoanBecameOverdue;
import javaclasses.exlibris.c.LoanBecameShouldReturnSoon;
import javaclasses.exlibris.c.LoanPeriodExtended;
import javaclasses.exlibris.c.LoansExtensionAllowed;
import javaclasses.exlibris.c.LoansExtensionForbidden;
import javaclasses.exlibris.c.MarkBookAsAvailable;
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
import javaclasses.exlibris.c.SatisfyReservation;
import javaclasses.exlibris.c.WriteBookOff;
import javaclasses.exlibris.c.rejection.BookAlreadyBorrowed;
import javaclasses.exlibris.c.rejection.BookAlreadyReserved;
import javaclasses.exlibris.c.rejection.CannotCancelMissingReservation;
import javaclasses.exlibris.c.rejection.CannotExtendLoanPeriod;
import javaclasses.exlibris.c.rejection.CannotReserveAvailableBook;
import javaclasses.exlibris.c.rejection.CannotReturnMissingBook;
import javaclasses.exlibris.c.rejection.CannotReturnNonBorrowedBook;
import javaclasses.exlibris.c.rejection.CannotWriteBookOff;
import javaclasses.exlibris.c.rejection.NonAvailableBook;

import java.util.List;

import static io.spine.time.Time.getCurrentTime;
import static javaclasses.exlibris.LoanStatus.LOAN_OVERDUE;
import static javaclasses.exlibris.LoanStatus.LOAN_RECENT;
import static javaclasses.exlibris.LoanStatus.LOAN_SOULD_RETURN_SOON;
import static javaclasses.exlibris.c.inventory.InventoryAggregateRejections.BorrowBookRejection.bookAlreadyBorrowed;
import static javaclasses.exlibris.c.inventory.InventoryAggregateRejections.BorrowBookRejection.nonAvailableBook;
import static javaclasses.exlibris.c.inventory.InventoryAggregateRejections.ReserveBookRejection.bookAlreadyBorrowed;
import static javaclasses.exlibris.c.inventory.InventoryAggregateRejections.ReserveBookRejection.bookAlreadyReserved;
import static javaclasses.exlibris.c.inventory.InventoryAggregateRejections.ReserveBookRejection.cannotReserveAvailableBook;
import static javaclasses.exlibris.c.inventory.InventoryAggregateRejections.ReturnBookRejection.cannotReturnNonBorrowedBook;
import static javaclasses.exlibris.c.inventory.InventoryAggregateRejections.ReturnBookRejection.throwCannotReturnMissingBook;
import static javaclasses.exlibris.c.inventory.InventoryAggregateRejections.cannotCancelMissingReservation;
import static javaclasses.exlibris.c.inventory.InventoryAggregateRejections.cannotExtendLoanPeriod;
import static javaclasses.exlibris.c.inventory.InventoryAggregateRejections.cannotWriteBookOff;

/**
 * The aggregate managing the state of a {@link Inventory}.
 *
 * @author Alexander Karpets
 * @author Paul Ageyev
 * @author Yegor Udovchenko
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
     * Borrow period. After this time satisfied reservation becomes expired.
     *
     * <p>secondsInMinute * minutesInHours * hoursInDay * twoDays
     */
    private static final int OPEN_FOR_BORROW_PERIOD = 60 * 60 * 24 * 2;

    /**
     * Creates a new instance.
     *
     * @param id the identifier for the new aggregate
     */
    public InventoryAggregate(InventoryId id) {
        super(id);
    }

    /**
     * Handles a {@code AppendInventory} command.
     *
     * <p>For details see {@link AppendInventory}.
     *
     * @param cmd command with the identifier of a specific item.
     * @return the {@link InventoryAppended} event.
     */
    @Assign
    InventoryAppended handle(AppendInventory cmd) {
        final InventoryAppended inventoryAppended = createInventoryAppendedEvent(cmd);
        return inventoryAppended;
    }

    /**
     * Handles a {@code WriteBookOff} command.
     *
     * <p>For details see {@link WriteBookOff}.
     *
     * @param cmd command with a reason of a book writing off.
     * @return a {@code WriteBookOff} event.
     * @throws CannotWriteBookOff if that item is missing or borrowed.
     */
    @Assign
    InventoryDecreased handle(WriteBookOff cmd) throws CannotWriteBookOff {
        final InventoryItemId inventoryItemId = cmd.getInventoryItemId();
        if (!inventoryItemExists(inventoryItemId) || isInventoryItemBorrowed(inventoryItemId)) {
            throw cannotWriteBookOff(cmd);
        }
        final InventoryDecreased inventoryDecreased = createInventoryDecreasedEvent(cmd);
        return inventoryDecreased;
    }

    /**
     * Handles a {@code ReserveBook} command.
     *
     * <p>For details see {@link ReserveBook}.
     *
     * @param cmd command to reserve a book.
     * @return a {@code ReservationAdded} event.
     * @throws BookAlreadyBorrowed        if a book is already borrowed by a user.
     * @throws BookAlreadyReserved        if a reservation already exists.
     * @throws CannotReserveAvailableBook upon an attempt to reserve available book.
     */
    @Assign
    ReservationAdded handle(ReserveBook cmd) throws BookAlreadyBorrowed,
                                                    BookAlreadyReserved,
                                                    CannotReserveAvailableBook {
        final UserId userId = cmd.getUserId();

        if (isBookBorrowedByUser(userId)) {
            throw bookAlreadyBorrowed(cmd);
        }
        if (isBookReservedByUser(userId)) {
            throw bookAlreadyReserved(cmd);
        }
        if (isThereInventoryItemsFreeForBorrowing()) {
            throw cannotReserveAvailableBook(cmd);
        }
        final ReservationAdded reservationAdded = createReservationAddedEvent(cmd);
        return reservationAdded;
    }

    // @formatter:off
    /**
     * Handles a {@code BorrowBook} command.
     *
     * <p>For details see {@link BorrowBook}.
     *
     * <p>Emits the following event combinations:
     *
     * <ul>
     *      <li>{@code BookBorrowed, null} - when the book borrowing is allowed and
     *           it is not a consequence of reservation.
     *      <li>{@code BookBorrowed, ReservationBecameLoan} - when the book borrowing is
     *           allowed and book is reserved by a user (this reservation should be satisfied).
     * </ul>
     *
     * @param cmd command to borrow the book.
     * @return the {@link Pair} of events.
     * @throws BookAlreadyBorrowed if a book is already borrowed ba a user.
     * @throws NonAvailableBook in following cases:
     * <ul>
     *      <li>if this item is already borrowed by somebody
     *      <li>user has the reservation for this book and it is not satisfied
     *      <li>user has no reservation for this book but the satisfied reservations count for this
     *          book is greater or equal the `in library` items count(no items free for borrowing).
     * </ul>
     */ 
    // @formatter:on
    @Assign
    Pair<BookBorrowed, Optional<ReservationBecameLoan>> handle(BorrowBook cmd) throws
                                                                               BookAlreadyBorrowed,
                                                                               NonAvailableBook {
        final UserId userId = cmd.getUserId();
        final InventoryItemId inventoryItemId = cmd.getInventoryItemId();

        if (isBookBorrowedByUser(userId)) {
            throw bookAlreadyBorrowed(cmd);
        }
        if (inventoryItemExists(inventoryItemId) && isInventoryItemBorrowed(inventoryItemId)) {
            throw nonAvailableBook(cmd);
        }

        final List<Reservation> reservations = getState().getReservationsList();

        if (isBookReservedByUser(userId)) {
            final Reservation reservation = getReservationByUserId(userId, reservations);
            if (!reservation.getIsSatisfied()) {
                throw nonAvailableBook(cmd);
            }
            final int availableItemsCount = getAvailableInventoryItemsCount();
            final BookBorrowed bookBorrowedEvent =
                    createBookBorrowedEvent(cmd, availableItemsCount);
            final ReservationBecameLoan reservationBecameLoanEvent =
                    createReservationBecameLoanEvent(cmd);
            Pair result = Pair.of(bookBorrowedEvent, reservationBecameLoanEvent);
            return result;
        }

        if (!isThereInventoryItemsFreeForBorrowing()) {
            throw nonAvailableBook(cmd);
        }
        final int availableItemsCount = getAvailableInventoryItemsCount();
        final BookBorrowed bookBorrowedEvent =
                createBookBorrowedEvent(cmd, availableItemsCount - 1);
        final Pair result = Pair.withNullable(bookBorrowedEvent, null);
        return result;
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
        final LoanBecameOverdue becameOverdueEvent = createLoanBecameOverdueEvent(cmd);
        return becameOverdueEvent;
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
        final LoanBecameShouldReturnSoon shouldReturnSoonEvent =
                createLoanBecameShouldReturnSoonEvent(cmd);
        return shouldReturnSoonEvent;
    }

    /**
     * Handles a {@code ExtendLoanPeriod} command.
     *
     * <p>For details see {@link ExtendLoanPeriod}.
     *
     * @param cmd command with the identifier of a loan
     *            that a user is going to extend.
     * @return a {@code LoanPeriodExtended} event.
     * @throws CannotExtendLoanPeriod if a loan period extension isn’t allowed.
     */
    @Assign
    LoanPeriodExtended handle(ExtendLoanPeriod cmd) throws CannotExtendLoanPeriod {
        final LoanId loanId = cmd.getLoanId();

        if (!(loanExists(loanId) && isLoanAllowedForExtension(loanId))) {
            throw cannotExtendLoanPeriod(cmd);
        }

        final LoanPeriodExtended loanPeriodExtendedEvent = createLoanPeriodExtendedEvent(cmd);
        return loanPeriodExtendedEvent;
    }

    /**
     * Handles a {@code CancelReservation} command.
     *
     * <p>For details see {@link CancelReservation}.
     *
     * @param cmd command with the identifier of a user who is going to cancel a reservation.
     * @return the {@code ReservationCanceled} event.
     * @throws CannotCancelMissingReservation if a reservation is missing.
     */
    @Assign
    ReservationCanceled handle(CancelReservation cmd) throws CannotCancelMissingReservation {
        final UserId userId = cmd.getUserId();

        if (!isBookReservedByUser(userId)) {
            throw cannotCancelMissingReservation(cmd);
        }
        final ReservationCanceled reservationCanceledEvent = createReservationCanceledEvent(cmd);
        return reservationCanceledEvent;
    }

    /**
     * Handles a {@code MarkReservationExpired} command.
     *
     * <p>For details see {@link MarkReservationExpired}.
     *
     * @param cmd system command that contains the identifier of an expired reservation.
     * @return the {@code ReservationPickUpPeriodExpired} event.
     */
    @Assign
    ReservationPickUpPeriodExpired handle(MarkReservationExpired cmd) {
        final ReservationPickUpPeriodExpired reservationExpiredEvent =
                createReservationPickUpPeriodExpiredEvent(cmd);
        return reservationExpiredEvent;
    }

    /**
     * Handles a {@code ReturnBook} command.
     *
     * <p>For details see {@link ReturnBook}.
     *
     * @param cmd command with an identifier of the book that the user is going to return.
     * @return the {@code BookReturned} event.
     * @throws CannotReturnNonBorrowedBook if a book isn’t borrowed by the user.
     * @throws CannotReturnMissingBook     if a book is missing.
     */
    @Assign
    BookReturned handle(ReturnBook cmd) throws CannotReturnNonBorrowedBook,
                                               CannotReturnMissingBook {
        final InventoryItemId inventoryItemId = cmd.getInventoryItemId();
        final UserId userId = cmd.getUserId();
        if (!inventoryItemExists(inventoryItemId)) {
            throw throwCannotReturnMissingBook(cmd);
        }
        if (!isInventoryItemBorrowedByUser(inventoryItemId, userId)) {
            throw cannotReturnNonBorrowedBook(cmd);
        }

        final BookReturned bookReturnedEvent = createBookReturnedEvent(cmd);
        return bookReturnedEvent;
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
        final BookLost result = createBookLostEvent(cmd);
        return result;
    }

    /**
     * Handles a {@code AllowLoansExtension} command.
     *
     * <p>For details see {@link AllowLoansExtension}.
     *
     * @param cmd command to allow loans extension.
     * @return a {@code LoansExtensionAllowed} event.
     */
    @Assign
    LoansExtensionAllowed handle(AllowLoansExtension cmd) {
        final List<UserId> userIds = cmd.getBorrowersList();
        final LoansExtensionAllowed result = createLoansExtensionAllowedEvent(userIds);
        return result;
    }

    /**
     * Handles a {@code ForbidLoansExtension} command.
     *
     * <p>For details see {@link ForbidLoansExtension}.
     *
     * @param cmd command to forbid loans extension.
     * @return a {@code LoansExtensionForbidden} event.
     */
    @Assign
    LoansExtensionForbidden handle(ForbidLoansExtension cmd) {
        final List<UserId> userIds = cmd.getBorrowersList();
        final LoansExtensionForbidden result = createLoansExtensionForbiddenEvent(userIds);
        return result;
    }

    /**
     * Handles a {@code SatisfyReservation} command.
     *
     * <p>For details see {@link SatisfyReservation}.
     *
     * @param cmd command to satisfy users reservation.
     * @return a {@code BookReadyToPickup} event.
     */
    @Assign
    BookReadyToPickup handle(SatisfyReservation cmd) {
        final BookReadyToPickup result = createBookReadyToPickupEvent(cmd);
        return result;
    }

    /**
     * Handles a {@code MarkBookAsAvailable} command.
     *
     * <p>For details see {@link MarkBookAsAvailable}.
     *
     * @param cmd command to make book public available.
     * @return a {@code BookBecameAvailable} event.
     */
    @Assign
    BookBecameAvailable handle(MarkBookAsAvailable cmd) {
        final BookBecameAvailable result = createBookBecameAvailableEvent();
        return result;
    }

    /**
     * Reacts on a {@code BookAdded} event from the {@code BookAggregate}.
     *
     * @param event stimulus for reacting.
     * @return a {@code InventoryCreated} event.
     */
    @React
    InventoryCreated on(BookAdded event) {
        final InventoryId inventoryId = InventoryId.newBuilder()
                                                   .setBookId(event.getBookId())
                                                   .build();
        final InventoryCreated result = InventoryCreated.newBuilder()
                                                        .setInventoryId(inventoryId)
                                                        .setWhenCreated(getCurrentTime())
                                                        .build();
        return result;
    }

    /**
     * Reacts on a {@code BookRemoved} event from the {@code BookAggregate}.
     *
     * @param event stimulus for reacting.
     * @return a {@code InventoryRemoved} event.
     */
    @React
    InventoryRemoved on(BookRemoved event) {
        final InventoryId inventoryId = InventoryId.newBuilder()
                                                   .setBookId(event.getBookId())
                                                   .build();
        final InventoryRemoved result = InventoryRemoved.newBuilder()
                                                        .setInventoryId(inventoryId)
                                                        .setWhenRemoved(getCurrentTime())
                                                        .build();
        return result;
    }

    /*
     * Event appliers
     *****************/

    @Apply
    void emptyEvent(Empty event) {
        // Applier for empty event.
        //
        // Used when BorrowBook command handler returns two events:
        // BookBorrowed, Empty
        // Handle Empty event without changing aggregate state.
    }

    @Apply
    void inventoryCreated(InventoryCreated event) {
        getBuilder().setInventoryId(event.getInventoryId());
    }

    @Apply
    void inventoryRemoved(InventoryRemoved event) {
        getBuilder().clearInventoryId()
                    .clearInventoryItems()
                    .clearLoans()
                    .clearReservations();
    }

    @Apply
    void inventoryAppended(InventoryAppended event) {
        final InventoryItemId inventoryItemId = event.getInventoryItemId();
        final InventoryItem newInventoryItem = InventoryItem.newBuilder()
                                                            .setInLibrary(true)
                                                            .setInventoryItemId(inventoryItemId)
                                                            .build();
        getBuilder().addInventoryItems(newInventoryItem);
    }

    @Apply
    void bookBecameAvailable(BookBecameAvailable event) {
        // BookBecameAvailable event does not cause aggregate state changes.
        // Used to notify application read side about available items count change
    }

    @Apply
    void bookReadyToPickup(BookReadyToPickup event) {
        final List<Reservation> reservations = getBuilder().getReservations();
        final UserId forWhom = event.getForWhom();
        final Reservation reservation = getReservationByUserId(forWhom, reservations);
        final int reservationIndex = getReservationIndexByUserId(forWhom, reservations);

        final Reservation satisfiedReservation = Reservation.newBuilder(reservation)
                                                            .setIsSatisfied(true)
                                                            .build();
        getBuilder().setReservations(reservationIndex, satisfiedReservation);
    }

    @Apply
    void reservationBecameLoan(ReservationBecameLoan event) {
        final List<Reservation> reservations = getBuilder().getReservations();
        final UserId userId = event.getUserId();
        final int reservationIndex = getReservationIndexByUserId(userId, reservations);
        getBuilder().removeReservations(reservationIndex);
    }

    @Apply
    void inventoryDecreased(InventoryDecreased event) {
        final List<InventoryItem> inventoryItems = getBuilder().getInventoryItems();
        final InventoryItemId inventoryItemId = event.getInventoryItemId();
        final int itemIndex = getInventoryItemIndexById(inventoryItemId, inventoryItems);

        getBuilder().removeInventoryItems(itemIndex);
    }

    @Apply
    void reservationAdded(ReservationAdded event) {
        final Isbn62 isbn62 = event.getInventoryId()
                                   .getBookId()
                                   .getIsbn62();
        final BookId bookId = BookId.newBuilder()
                                    .setIsbn62(isbn62)
                                    .build();
        final Timestamp whenCreated = event.getWhenCreated();
        final UserId forWhomReserved = event.getForWhomReserved();
        final Reservation.Builder reservation = Reservation.newBuilder()
                                                           .setBookId(bookId)
                                                           .setWhenCreated(whenCreated)
                                                           .setWhoReserved(forWhomReserved)
                                                           .setIsSatisfied(false);
        if (event.hasWhenExpected()) {
            final Timestamp whenExpected = event.getWhenExpected();
            reservation.setWhenExpected(whenExpected);
        }
        getBuilder().addReservations(reservation.build());
    }

    @Apply
    void bookBorrowed(BookBorrowed event) {
        final List<InventoryItem> inventoryItems = getBuilder().getInventoryItems();
        final InventoryItemId inventoryItemId = event.getInventoryItemId();
        final int itemPosition = getInventoryItemIndexById(inventoryItemId, inventoryItems);
        final InventoryItem inventoryItem = inventoryItems.get(itemPosition);
        final UserId whoBorrowed = event.getWhoBorrowed();
        final InventoryItem borrowedItem = InventoryItem.newBuilder(inventoryItem)
                                                        .clearInLibrary()
                                                        .setBorrowed(true)
                                                        .setUserId(whoBorrowed)
                                                        .build();
        final Timestamp whenBorrowed = event.getWhenBorrowed();
        final Timestamp whenDue = event.getWhenDue();

        final Loan loan = Loan.newBuilder()
                              .setLoanId(event.getLoanId())
                              .setInventoryItemId(inventoryItemId)
                              .setStatus(LOAN_RECENT)
                              .setWhoBorrowed(whoBorrowed)
                              .setWhenTaken(whenBorrowed)
                              .setWhenDue(whenDue)
                              .setIsAllowedExtension(true)
                              .build();

        getBuilder().setInventoryItems(itemPosition, borrowedItem)
                    .addLoans(loan);
    }

    @Apply
    void loanBecameOverdue(LoanBecameOverdue event) {
        final LoanId loanId = event.getLoanId();
        updateLoanStatus(loanId, LOAN_OVERDUE);
    }

    @Apply
    void loanBecameShouldReturnSoon(LoanBecameShouldReturnSoon event) {
        final LoanId loanId = event.getLoanId();
        updateLoanStatus(loanId, LOAN_SOULD_RETURN_SOON);
    }

    private void updateLoanStatus(LoanId loanId, LoanStatus newLoanStatus) {
        final List<Loan> loans = getBuilder().getLoans();
        final int loanPosition = getLoanIndexByLoanId(loanId, loans);
        final Loan loan = loans.get(loanPosition);
        final Loan updatedLoan = Loan.newBuilder(loan)
                                     .setStatus(newLoanStatus)
                                     .build();
        getBuilder().setLoans(loanPosition, updatedLoan);
    }

    @Apply
    void loanPeriodExtended(LoanPeriodExtended event) {
        final LoanId loanId = event.getLoanId();
        final Timestamp newDueDate = event.getNewDueDate();
        final List<Loan> loans = getBuilder().getLoans();
        final int loanPosition = getLoanIndexByLoanId(loanId, loans);
        final Loan loan = loans.get(loanPosition);
        final Loan updatedLoan = Loan.newBuilder(loan)
                                     .setStatus(LOAN_RECENT)
                                     .setWhenDue(newDueDate)
                                     .build();
        getBuilder().setLoans(loanPosition, updatedLoan);
    }

    @Apply
    void reservationCanceled(ReservationCanceled event) {
        final UserId whoCanceled = event.getWhoCanceled();
        final List<Reservation> reservations = getBuilder().getReservations();
        final int reservationIndex = getReservationIndexByUserId(whoCanceled, reservations);

        getBuilder().removeReservations(reservationIndex);
    }

    @Apply
    void reservationPickUpPeriodExpired(ReservationPickUpPeriodExpired event) {
        final UserId userId = event.getUserId();
        final List<Reservation> reservations = getBuilder().getReservations();
        final int reservationPosition = getReservationIndexByUserId(userId, reservations);

        getBuilder().removeReservations(reservationPosition);
    }

    @Apply
    void bookReturned(BookReturned event) {
        final UserId whoReturned = event.getWhoReturned();
        final InventoryItemId inventoryItemId = event.getInventoryItemId();
        final List<InventoryItem> inventoryItems = getBuilder().getInventoryItems();
        final List<Loan> loans = getBuilder().getLoans();
        final int returnedItemPosition = getInventoryItemIndexById(inventoryItemId, inventoryItems);
        final InventoryItem inventoryItem = inventoryItems.get(returnedItemPosition);
        final int loanIndex = getLoanIndexByUserId(whoReturned, loans);
        final InventoryItem returnedItem = InventoryItem.newBuilder(inventoryItem)
                                                        .clearBorrowed()
                                                        .clearUserId()
                                                        .setInLibrary(true)
                                                        .build();
        getBuilder().setInventoryItems(returnedItemPosition, returnedItem);
        getBuilder().removeLoans(loanIndex);
    }

    @Apply
    void bookLost(BookLost event) {
        final UserId whoLost = event.getWhoLost();
        final InventoryItemId inventoryItemId = event.getInventoryItemId();
        final List<InventoryItem> inventoryItems = getBuilder().getInventoryItems();
        final List<Loan> loans = getBuilder().getLoans();
        final int returnedItemPosition = getInventoryItemIndexById(inventoryItemId, inventoryItems);
        final InventoryItem inventoryItem = inventoryItems.get(returnedItemPosition);
        final int loanIndex = getLoanIndexByUserId(whoLost, loans);
        final InventoryItem returnedItem = InventoryItem.newBuilder(inventoryItem)
                                                        .clearBorrowed()
                                                        .clearUserId()
                                                        .clearInLibrary()
                                                        .setLost(true)
                                                        .build();
        getBuilder().setInventoryItems(returnedItemPosition, returnedItem);
        getBuilder().removeLoans(loanIndex);
    }

    @Apply
    void loansExtensionAllowed(LoansExtensionAllowed event) {
        final List<UserId> borrowersList = event.getBorrowersList();
        final List<Loan> loans = getBuilder().getLoans();

        for (UserId borrower : borrowersList) {
            final int loanIndex = getLoanIndexByUserId(borrower, loans);
            final Loan loan = loans.get(loanIndex);
            final Loan updatedLoan = Loan.newBuilder(loan)
                                         .setIsAllowedExtension(true)
                                         .build();
            getBuilder().setLoans(loanIndex, updatedLoan);
        }
    }

    @Apply
    void loansExtensionForbidden(LoansExtensionForbidden event) {
        final List<UserId> borrowersList = event.getBorrowersList();
        final List<Loan> loans = getBuilder().getLoans();

        for (UserId borrower : borrowersList) {
            final int loanIndex = getLoanIndexByUserId(borrower, loans);
            final Loan loan = loans.get(loanIndex);
            final Loan updatedLoan = Loan.newBuilder(loan)
                                         .setIsAllowedExtension(false)
                                         .build();
            getBuilder().setLoans(loanIndex, updatedLoan);
        }
    }

    private BookBecameAvailable createBookBecameAvailableEvent() {
        final InventoryId inventoryId = getState().getInventoryId();
        final Timestamp currentTime = getCurrentTime();
        final int availableItemsCount = getAvailableInventoryItemsCount();
        final BookBecameAvailable bookBecameAvailable =
                BookBecameAvailable.newBuilder()
                                   .setInventoryId(inventoryId)
                                   .setWhenBecameAvailable(currentTime)
                                   .setAvailableBooksCount(availableItemsCount)
                                   .build();
        return bookBecameAvailable;
    }

    private BookReadyToPickup createBookReadyToPickupEvent(SatisfyReservation cmd) {
        final UserId userId = cmd.getUserId();
        final Timestamp currentTime = getCurrentTime();
        final long pickupDeadlineTimeSeconds = currentTime.getSeconds() + OPEN_FOR_BORROW_PERIOD;
        final Timestamp pickUpDeadline = Timestamp.newBuilder()
                                                  .setSeconds(pickupDeadlineTimeSeconds)
                                                  .build();
        final InventoryId inventoryId = cmd.getInventoryId();
        final BookReadyToPickup bookReadyToPickup =
                BookReadyToPickup.newBuilder()
                                 .setInventoryId(inventoryId)
                                 .setForWhom(userId)
                                 .setWhenBecameReadyToPickup(currentTime)
                                 .setPickUpDeadline(pickUpDeadline)
                                 .build();
        return bookReadyToPickup;
    }

    private InventoryAppended createInventoryAppendedEvent(AppendInventory cmd) {
        final InventoryId inventoryId = cmd.getInventoryId();
        final InventoryItemId inventoryItemId = cmd.getInventoryItemId();
        final UserId userId = cmd.getLibrarianId();

        final InventoryAppended inventoryAppended =
                InventoryAppended.newBuilder()
                                 .setInventoryId(inventoryId)
                                 .setInventoryItemId(inventoryItemId)
                                 .setWhenAppended(getCurrentTime())
                                 .setLibrarianId(userId)
                                 .build();
        return inventoryAppended;
    }

    private InventoryDecreased createInventoryDecreasedEvent(WriteBookOff cmd) {
        final InventoryId inventoryId = cmd.getInventoryId();
        final InventoryItemId inventoryItemId = cmd.getInventoryItemId();
        final UserId librarianId = cmd.getLibrarianId();
        final WriteOffReason writeOffReason = cmd.getWriteBookOffReason();
        final List<InventoryItem> inventoryItems = getState().getInventoryItemsList();
        final int index = getInventoryItemIndexById(inventoryItemId, inventoryItems);
        final InventoryItem inventoryItem = inventoryItems.get(index);

        // current available count should be decreased to match its value
        // after applying this event. If the book was lost by user it is already
        // non available.
        final int availableItemsCount = inventoryItem.getLost()
                                        ? getAvailableInventoryItemsCount()
                                        : getAvailableInventoryItemsCount() - 1;
        final InventoryDecreased inventoryDecreased =
                InventoryDecreased.newBuilder()
                                  .setInventoryId(inventoryId)
                                  .setInventoryItemId(inventoryItemId)
                                  .setWhenDecreased(getCurrentTime())
                                  .setLibrarianId(librarianId)
                                  .setWriteOffReason(writeOffReason)
                                  .setAvailableBooksCount(availableItemsCount)
                                  .build();
        return inventoryDecreased;
    }

    private ReservationAdded createReservationAddedEvent(ReserveBook cmd) {
        final InventoryId inventoryId = cmd.getInventoryId();
        final UserId userId = cmd.getUserId();
        final List<Loan> loans = getState().getLoansList();
        final List<Reservation> reservations = getState().getReservationsList();
        final Optional<Timestamp> readyToPickUpExpectedTime =
                getReadyToPickUpExpectedTime(loans, reservations);
        final ReservationAdded.Builder reservationAddedBuilder =
                ReservationAdded.newBuilder()
                                .setInventoryId(inventoryId)
                                .setForWhomReserved(userId)
                                .setWhenCreated(getCurrentTime());

        if (readyToPickUpExpectedTime.isPresent()) {
            reservationAddedBuilder.setWhenExpected(readyToPickUpExpectedTime.get());
        }
        return reservationAddedBuilder.build();
    }

    private LoansExtensionAllowed createLoansExtensionAllowedEvent(List<UserId> borrowers) {
        final InventoryId inventoryId = getState().getInventoryId();
        final LoansExtensionAllowed loansExtensionAllowed =
                LoansExtensionAllowed.newBuilder()
                                     .setInventoryId(inventoryId)
                                     .setWhenBecame(getCurrentTime())
                                     .addAllBorrowers(borrowers)
                                     .build();
        return loansExtensionAllowed;
    }

    private LoansExtensionForbidden createLoansExtensionForbiddenEvent(List<UserId> borrowers) {
        final InventoryId inventoryId = getState().getInventoryId();
        final LoansExtensionForbidden loansExtensionForbidden =
                LoansExtensionForbidden.newBuilder()
                                       .addAllBorrowers(borrowers)
                                       .setInventoryId(inventoryId)
                                       .setWhenBecame(getCurrentTime())
                                       .build();
        return loansExtensionForbidden;
    }

    private BookBorrowed createBookBorrowedEvent(BorrowBook cmd, int availableItemsCount) {
        final InventoryId inventoryId = cmd.getInventoryId();
        final InventoryItemId inventoryItemId = cmd.getInventoryItemId();
        final UserId userId = cmd.getUserId();
        final LoanId loanId = LoanId.newBuilder()
                                    .setValue(getCurrentTime().getSeconds())
                                    .build();
        final Timestamp whenBorrowed = getCurrentTime();
        final Timestamp whenDue = Timestamp.newBuilder()
                                           .setSeconds(whenBorrowed.getSeconds() + LOAN_PERIOD)
                                           .build();
        final BookBorrowed bookBorrowed = BookBorrowed.newBuilder()
                                                      .setInventoryId(inventoryId)
                                                      .setInventoryItemId(inventoryItemId)
                                                      .setWhoBorrowed(userId)
                                                      .setLoanId(loanId)
                                                      .setWhenBorrowed(whenBorrowed)
                                                      .setWhenDue(whenDue)
                                                      .setAvailableBooksCount(availableItemsCount)
                                                      .build();
        return bookBorrowed;
    }

    private ReservationBecameLoan createReservationBecameLoanEvent(BorrowBook cmd) {
        final InventoryId inventoryId = cmd.getInventoryId();
        final UserId userId = cmd.getUserId();
        final ReservationBecameLoan reservationBecameLoan =
                ReservationBecameLoan.newBuilder()
                                     .setInventoryId(inventoryId)
                                     .setUserId(userId)
                                     .setWhenBecameLoan(getCurrentTime())
                                     .build();
        return reservationBecameLoan;
    }

    private LoanBecameOverdue createLoanBecameOverdueEvent(MarkLoanOverdue cmd) {
        final InventoryId inventoryId = cmd.getInventoryId();
        final LoanId loanId = cmd.getLoanId();
        final List<Loan> loans = getState().getLoansList();
        final int loanPosition = getLoanIndexByLoanId(loanId, loans);
        final Loan loan = loans.get(loanPosition);
        final UserId whoBorrowed = loan.getWhoBorrowed();
        final InventoryItemId inventoryItemId = loan.getInventoryItemId();
        final boolean isAllowedExtension = loan.getIsAllowedExtension();

        final LoanBecameOverdue becameOverdue =
                LoanBecameOverdue.newBuilder()
                                 .setInventoryId(inventoryId)
                                 .setInventoryItemId(inventoryItemId)
                                 .setLoanId(loanId)
                                 .setUserId(whoBorrowed)
                                 .setWhenBecameOverdue(getCurrentTime())
                                 .setIsAllowedExtension(isAllowedExtension)
                                 .build();
        return becameOverdue;
    }

    private LoanBecameShouldReturnSoon createLoanBecameShouldReturnSoonEvent(
            MarkLoanShouldReturnSoon cmd) {
        final InventoryId inventoryId = cmd.getInventoryId();
        final LoanId loanId = cmd.getLoanId();
        final List<Loan> loans = getState().getLoansList();
        final int loanPosition = getLoanIndexByLoanId(loanId, loans);
        final Loan loan = loans.get(loanPosition);
        final UserId whoBorrowed = loan.getWhoBorrowed();
        final boolean isAllowedExtension = loan.getIsAllowedExtension();
        final InventoryItemId inventoryItemId = loan.getInventoryItemId();

        final LoanBecameShouldReturnSoon result =
                LoanBecameShouldReturnSoon.newBuilder()
                                          .setInventoryId(inventoryId)
                                          .setInventoryItemId(inventoryItemId)
                                          .setLoanId(loanId)
                                          .setUserId(whoBorrowed)
                                          .setWhenBecameShouldReturnSoon(getCurrentTime())
                                          .setIsAllowedExtension(isAllowedExtension)
                                          .build();
        return result;
    }

    private LoanPeriodExtended createLoanPeriodExtendedEvent(ExtendLoanPeriod cmd) {
        final InventoryId inventoryId = cmd.getInventoryId();
        final LoanId loanId = cmd.getLoanId();
        final UserId userId = cmd.getUserId();
        final List<Loan> loans = getState().getLoansList();
        final int loanPosition = getLoanIndexByLoanId(loanId, loans);
        final Loan loan = loans.get(loanPosition);
        final InventoryItemId inventoryItemId = loan.getInventoryItemId();
        final Timestamp previousDueDate = loan.getWhenDue();
        final long newDueDateInSeconds = previousDueDate.getSeconds() + LOAN_PERIOD;
        final Timestamp newDueDate = Timestamp.newBuilder()
                                              .setSeconds(newDueDateInSeconds)
                                              .build();
        final LoanPeriodExtended loanExtended =
                LoanPeriodExtended.newBuilder()
                                  .setInventoryId(inventoryId)
                                  .setInventoryItemId(inventoryItemId)
                                  .setLoanId(loanId)
                                  .setUserId(userId)
                                  .setWhenExtended(getCurrentTime())
                                  .setPreviousDueDate(previousDueDate)
                                  .setNewDueDate(newDueDate)
                                  .build();
        return loanExtended;
    }

    private ReservationCanceled createReservationCanceledEvent(CancelReservation cmd) {
        final InventoryId inventoryId = cmd.getInventoryId();
        final UserId userId = cmd.getUserId();
        final List<Reservation> reservations = getState().getReservationsList();
        final Reservation reservation = getReservationByUserId(userId, reservations);
        final boolean isSatisfied = reservation.getIsSatisfied();
        final ReservationCanceled reservationCanceledEvent =
                ReservationCanceled.newBuilder()
                                   .setInventoryId(inventoryId)
                                   .setWhoCanceled(userId)
                                   .setWhenCanceled(getCurrentTime())
                                   .setWasSatisfied(isSatisfied)
                                   .build();
        return reservationCanceledEvent;
    }

    private ReservationPickUpPeriodExpired createReservationPickUpPeriodExpiredEvent(
            MarkReservationExpired cmd) {
        final UserId userId = cmd.getUserId();
        final InventoryId inventoryId = cmd.getInventoryId();
        final ReservationPickUpPeriodExpired pickUpPeriodExpired =
                ReservationPickUpPeriodExpired.newBuilder()
                                              .setInventoryId(inventoryId)
                                              .setUserId(userId)
                                              .setWhenExpired(getCurrentTime())
                                              .build();
        return pickUpPeriodExpired;
    }

    private BookReturned createBookReturnedEvent(ReturnBook cmd) {
        final InventoryId inventoryId = cmd.getInventoryId();
        final InventoryItemId inventoryItemId = cmd.getInventoryItemId();
        final UserId userId = cmd.getUserId();
        final List<Loan> loans = getState().getLoansList();
        final int loanIndex = getLoanIndexByUserId(userId, loans);
        final Loan loan = loans.get(loanIndex);
        final LoanId loanId = loan.getLoanId();
        final BookReturned bookReturned = BookReturned.newBuilder()
                                                      .setInventoryId(inventoryId)
                                                      .setInventoryItemId(inventoryItemId)
                                                      .setWhoReturned(userId)
                                                      .setWhenReturned(getCurrentTime())
                                                      .setLoanId(loanId)
                                                      .build();
        return bookReturned;
    }

    private BookLost createBookLostEvent(ReportLostBook cmd) {
        final InventoryId inventoryId = cmd.getInventoryId();
        final InventoryItemId inventoryItemId = cmd.getInventoryItemId();
        final UserId userId = cmd.getWhoLost();
        final BookLost bookLost = BookLost.newBuilder()
                                          .setInventoryId(inventoryId)
                                          .setInventoryItemId(inventoryItemId)
                                          .setWhoLost(userId)
                                          .setWhenReported(getCurrentTime())
                                          .build();
        return bookLost;
    }

    private boolean isBookReservedByUser(UserId userId) {
        final List<Reservation> reservations = getState().getReservationsList();
        final boolean any = Iterables.any(reservations, item -> item.getWhoReserved()
                                                                    .equals(userId));
        return any;
    }

    private boolean isBookBorrowedByUser(UserId userId) {
        final List<Loan> loans = getState().getLoansList();
        final boolean any = Iterables.any(loans, item -> item.getWhoBorrowed()
                                                             .equals(userId));
        return any;
    }

    private boolean isInventoryItemBorrowedByUser(InventoryItemId itemId, UserId userId) {
        final List<InventoryItem> inventoryItems = getState().getInventoryItemsList();
        final boolean any = Iterables.any(inventoryItems,
                                          item -> item.getUserId()
                                                      .equals(userId) &&
                                                  item.getInventoryItemId()
                                                      .equals(itemId));
        return any;
    }

    private boolean isInventoryItemBorrowed(InventoryItemId itemId) {
        final List<InventoryItem> inventoryItems = getState().getInventoryItemsList();
        final int itemIndex = getInventoryItemIndexById(itemId, inventoryItems);
        final InventoryItem inventoryItem = inventoryItems.get(itemIndex);
        return inventoryItem.getBorrowed();
    }

    private boolean inventoryItemExists(InventoryItemId inventoryItemId) {
        final List<InventoryItem> inventoryItems = getState().getInventoryItemsList();
        final boolean any = Iterables.any(inventoryItems, item -> item.getInventoryItemId()
                                                                      .equals(inventoryItemId));
        return any;
    }

    private boolean loanExists(LoanId loanId) {
        final List<Loan> loans = getState().getLoansList();
        final boolean any = Iterables.any(loans, item -> item.getLoanId()
                                                             .equals(loanId));
        return any;
    }

    private Reservation getReservationByUserId(UserId userId, List<Reservation> reservations) {
        final int index = Iterables.indexOf(reservations, item -> item.getWhoReserved()
                                                                      .equals(userId));
        final Reservation reservation = reservations.get(index);
        return reservation;
    }

    private int getReservationIndexByUserId(UserId userId, List<Reservation> reservations) {
        final int index = Iterables.indexOf(reservations, item -> item.getWhoReserved()
                                                                      .equals(userId));
        return index;
    }

    private int getLoanIndexByUserId(UserId userId, List<Loan> loanList) {
        final int index = Iterables.indexOf(loanList, item -> item.getWhoBorrowed()
                                                                  .equals(userId));
        return index;
    }

    private int getLoanIndexByLoanId(LoanId loanId, List<Loan> loansList) {
        final int index = Iterables.indexOf(loansList, item -> item.getLoanId()
                                                                   .equals(loanId));
        return index;
    }

    private int getInventoryItemIndexById(InventoryItemId inventoryItemId,
                                          List<InventoryItem> inventoryItems) {
        final int index = Iterables.indexOf(inventoryItems, item -> item.getInventoryItemId()
                                                                        .equals(inventoryItemId));
        return index;
    }

    private boolean isLoanAllowedForExtension(LoanId loanId) {
        final List<Loan> loans = getState().getLoansList();
        final int loanIndex = getLoanIndexByLoanId(loanId, loans);
        final Loan loan = loans.get(loanIndex);
        final LoanStatus status = loan.getStatus();
        final boolean isAllowed =
                (status.equals(LOAN_OVERDUE) || status.equals(LOAN_SOULD_RETURN_SOON)) &&
                        loan.getIsAllowedExtension();
        return isAllowed;
    }

    private boolean isThereInventoryItemsFreeForBorrowing() {
        final int freeInventoryItemsCount = getAvailableInventoryItemsCount();
        return freeInventoryItemsCount > 0;
    }

    private int getAvailableInventoryItemsCount() {
        final List<InventoryItem> inventoryItems = getState().getInventoryItemsList();
        final List<Reservation> reservations = getState().getReservationsList();
        final int satisfiedReservationsCount = (int) reservations.stream()
                                                                 .filter(Reservation::getIsSatisfied)
                                                                 .count();
        final int inLibraryCount = (int) inventoryItems.stream()
                                                       .filter(InventoryItem::getInLibrary)
                                                       .count();
        return inLibraryCount - satisfiedReservationsCount;
    }

    // @formatter:off
    /**
     * Calculates the expected time when the reservation should be satisfied upon
     * the {@code ReserveBook} command handling.
     *
     * All following cases are provided when the reservation is allowed:
     * <ul>
     *      <li>if the loans list is empty(book has no items) the expected time cannot be calculated.
     *          {@code Optional.absent()} is returned.
     *      <li>if there are loans allowed for extension in the list, the expected time equals
     *          to the first allowed for extension loan due time.
     *      <li>if there are no loans allowed for extension, the expected time is calculated  as:
     *          lastLoanDueTime + (unsatisfiedReservationsCount - loansCount + 1)*LOAN_PERIOD
     *</ul>
     *
     * @param loans the list of loans
     * @param reservations the list of reservations
     * @return {@code Optional} expected time when the reservation should be satisfied.
     */
    // @formatter:on
    private Optional<Timestamp> getReadyToPickUpExpectedTime(List<Loan> loans,
                                                             List<Reservation> reservations) {
        if (loans.isEmpty()) {
            return Optional.absent();
        }

        final int index = Iterables.indexOf(loans, Loan::getIsAllowedExtension);
        if (index != -1) {
            final Loan firstAllowedForExtensionLoan = loans.get(index);
            return Optional.of(firstAllowedForExtensionLoan.getWhenDue());
        }

        final Loan lastLoan = loans.get(loans.size() - 1);
        final int unsatisfiedReservationsCount = (int) reservations.stream()
                                                                   .filter(item -> !item.getIsSatisfied())
                                                                   .count();
        final long expectedTimeSeconds = lastLoan.getWhenDue()
                                                 .getSeconds() +
                (unsatisfiedReservationsCount - loans.size() + 1) * LOAN_PERIOD;
        final Timestamp expectedTime = Timestamp.newBuilder()
                                                .setSeconds(expectedTimeSeconds)
                                                .build();
        return Optional.of(expectedTime);
    }
}
