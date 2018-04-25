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
import javaclasses.exlibris.LoanStatus;
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
import javaclasses.exlibris.c.LoansBecameNotAllowedForExtension;
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
import java.util.stream.Collectors;

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
     *      <li>{@code InventoryAppended, BookBecameAvailable, null} - when there are no
     *           unsatisfied reservations.
     *      <li>{@code InventoryAppended, BookReadyToPickup, null} - when the added item
     *          becomes ready to pick up for the first in the reservations queue and other
     *          readers still have reservations.
     *      <li>{@code InventoryAppended, BookReadyToPickup, LoansBecameExtensionAllowed} - when the
     *          added item becomes ready to pick up for the first in the reservations queue and there are no
     *          unsatisfied reservations except this one. In that case all recent loans of this book
     *          become allowed for extension.
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
        final List<Reservation> reservations = getState().getReservationsList();
        final InventoryAppended inventoryAppended = createInventoryAppendedEvent(cmd);

        if (!isThereUnsatisfiedReservations(reservations)) {
            final BookBecameAvailable becameAvailable = createBookBecameAvailableEvent();
            final Triplet result = Triplet.withNullable(inventoryAppended,
                                                        becameAvailable,
                                                        null);
            return result;
        }

        final BookReadyToPickup bookReadyToPickup = createBookReadyToPickupEvent();

        if (isThereSingleUnsatisfiedReservation(reservations) && isBookBorrowed()) {
            final LoansBecameExtensionAllowed loansBecameExtensionAllowed
                    = createLoansBecameExtensionAllowedEvent();
            final Triplet result = Triplet.of(inventoryAppended,
                                              bookReadyToPickup,
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
        final InventoryItemId inventoryItemId = cmd.getInventoryItemId();
        final List<InventoryItem> inventoryItems = getState().getInventoryItemsList();
        if (!inventoryItemExists(inventoryItemId, inventoryItems)) {
            throw cannotWriteMissingBookOff(cmd);
        }
        final InventoryDecreased inventoryDecreased = createInventoryDecreasedEvent(cmd);
        return inventoryDecreased;
    }

    // @formatter:off
    /**
     * Handles a {@code ReserveBook} command.
     *
     * <p>For details see {@link ReserveBook}.
     * <p>Returns the following event combinations:
     *
     * <ul>
     *      <li>{@code ReservationAdded, null} - when the book can be reserved
     *           and there are no active loans.
     *      <li>{@code ReservationAdded, LoansBecameNotAllowedForExtension} - when the book
     *           can be reserved and some loans should be set as not allowed for extension.
     * </ul>
     *
     * @param cmd command to reserve book.
     * @return a pair of events.
     * @throws BookAlreadyBorrowed if a book is already borrowed by a user.
     * @throws BookAlreadyReserved if a reservation already exists.
     */
    // @formatter:on
    @Assign
    Pair<ReservationAdded,
            Optional<LoansBecameNotAllowedForExtension>> handle(ReserveBook cmd)
            throws BookAlreadyBorrowed,
                   BookAlreadyReserved {
        final UserId userId = cmd.getUserId();
        final List<Reservation> reservations = getState().getReservationsList();
        final List<Loan> loans = getState().getLoansList();
        if (isBookBorrowedByUser(userId, loans)) {
            throw bookAlreadyBorrowed(cmd);
        }
        if (isBookReservedByUser(userId, reservations)) {
            throw bookAlreadyReserved(cmd);
        }
        final ReservationAdded reservationAdded = createReservationAddedEvent(cmd);

        if (isBookBorrowed() && !isThereUnsatisfiedReservations(reservations)) {
            final LoansBecameNotAllowedForExtension notAllowedForExtension = createLoansBecameNotAllowedForExtensionEvent();
            final Pair result = Pair.of(reservationAdded, notAllowedForExtension);
            return result;
        }
        final Pair result = Pair.withNullable(reservationAdded, null);
        return result;
    }

    // @formatter:off
    /**
     * Handles a {@code BorrowBook} command.
     *
     * <p>For details see {@link BorrowBook}.
     *
     * <p>Returns the following event combinations:
     *
     * <ul>
     *      <li>{@code BookBorrowed, null} - when the book borrowing is allowed and
     *           it is not a consequence of reservation.
     *      <li>{@code BookBorrowed, ReservationBecameLoan} - when the book borrowing is
     *           allowed and it satisfies the user reservation (this reservation should be the
     *           first in queue).
     * </ul>
     *
     * @param cmd command to borrow book.
     * @return the {@link Triplet} of the events.
     * @throws BookAlreadyBorrowed if a book is already borrowed ba a user.
     * @throws NonAvailableBook    if this item is already borrowed by somebody or the user's
     *                             reservation is not the first in the queue or he has no reservation
     *                             but somebody has.
     */
    // @formatter:on
    @Assign
    Pair<BookBorrowed, Optional<ReservationBecameLoan>> handle(BorrowBook cmd) throws
                                                                               BookAlreadyBorrowed,
                                                                               NonAvailableBook {
        final UserId userId = cmd.getUserId();
        final InventoryItemId inventoryItemId = cmd.getInventoryItemId();
        final List<Loan> loans = getState().getLoansList();
        final List<InventoryItem> inventoryItems = getState().getInventoryItemsList();
        if (isBookBorrowedByUser(userId, loans)) {
            throw bookAlreadyBorrowed(cmd);
        }
        if (isInventoryItemBorrowed(inventoryItemId, inventoryItems)) {
            throw nonAvailableBook(cmd);
        }

        final BookBorrowed bookBorrowedEvent = createBookBorrowedEvent(cmd);
        final List<Reservation> reservations = getState().getReservationsList();

        if (isBookReservedByUser(userId, reservations)) {
            if (isUserFirstInReservationsQueue(userId, reservations)) {
                throw nonAvailableBook(cmd);
            }
            final ReservationBecameLoan reservationBecameLoanEvent =
                    createReservationBecameLoanEvent(cmd);
            Pair result = Pair.of(bookBorrowedEvent, reservationBecameLoanEvent);
            return result;
        }

        if (isThereUnsatisfiedReservations(reservations)) {
            throw nonAvailableBook(cmd);
        }
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
     * @throws CannotExtendLoanPeriod if a loan period extension isn’t possible.
     */
    @Assign
    LoanPeriodExtended handle(ExtendLoanPeriod cmd) throws CannotExtendLoanPeriod {
        final LoanId loanId = cmd.getLoanId();
        final List<Loan> loans = getState().getLoansList();

        if (!loanExists(loanId, loans) || !isLoanAllowedForExtension(loanId, loans)) {
            throw cannotExtendLoanPeriod(cmd);
        }

        final LoanPeriodExtended loanPeriodExtendedEvent = createLoanPeriodExtendedEvent(cmd);
        return loanPeriodExtendedEvent;
    }

    // @formatter:off
    /**
     * Handles a {@code CancelReservation} command.
     *
     * <p>For details see {@link CancelReservation}.
     * <p>Returns the following event combinations:
     *
     * <ul>
     *      <li>{@code ReservationCanceled, null} - when the reservation can be canceled and it
     *           it was not satisfied.
     *      <li>{@code ReservationCanceled, BookReadyToPickup} - when this reservation was satisfied
     *          and there is someone next in the reservations queue with unsatisfied reservation.
     *      <li>{@code ReservationCanceled, BookBecameAvailable} - when the reservation was satisfied
     *          and there are no more unsatisfied reservations.
     * </ul>
     *
     * @param cmd command with the identifier of a reservation
     *            that a user is going to cancel.
     * @return the {@link Pair} of the events.
     * @throws CannotCancelMissingReservation if a reservation is missing.
     */
    // @formatter:on
    @Assign
    Pair<ReservationCanceled,
            Optional<EitherOfTwo<BookReadyToPickup,
                    BookBecameAvailable>>> handle(CancelReservation cmd)
            throws CannotCancelMissingReservation {
        final UserId userId = cmd.getUserId();
        final List<Reservation> reservations = getState().getReservationsList();
        if (!isBookReservedByUser(userId, reservations)) {
            throw cannotCancelMissingReservation(cmd);
        }
        final Reservation reservation = getReservationByUserId(userId, reservations);
        final ReservationCanceled reservationCanceledEvent = createReservationCanceledEvent(cmd);

        if (reservation.getIsSatisfied()) {
            if (isThereUnsatisfiedReservations(reservations)) {
                final BookReadyToPickup bookReadyToPickupEvent = createBookReadyToPickupEvent();
                Pair result = Pair.of(reservationCanceledEvent, bookReadyToPickupEvent);
                return result;
            }
            final BookBecameAvailable bookBecameAvailableEvent = createBookBecameAvailableEvent();
            Pair result = Pair.of(reservationCanceledEvent, bookBecameAvailableEvent);
            return result;
        }
        Pair result = Pair.withNullable(reservationCanceledEvent, null);
        return result;
    }

    // @formatter:off
    /**
     * Handles a {@code MarkReservationExpired} command.
     *
     * <p>For details see {@link MarkReservationExpired}.
     *
     * <p>Returns the following event combinations:
     * <ul>
     *      <li>{@code ReservationPickUpPeriodExpired, BookReadyToPickup} - when there is someone
     *          next in the reservations queue with unsatisfied reservation.
     *      <li>{@code ReservationPickUpPeriodExpired, BookBecameAvailable} - when there are no
     *          unsatisfied reservations.
     * </ul>
     *
     * @param cmd system command that contains the identifier of an expired reservation.
     * @return the {@link Pair} of the events.
     */
    // @formatter:on
    @Assign
    Pair<ReservationPickUpPeriodExpired,
            EitherOfTwo<BookReadyToPickup,
                    BookBecameAvailable>> handle(MarkReservationExpired cmd) {
        final ReservationPickUpPeriodExpired reservationExpiredEvent =
                createReservationPickUpPeriodExpiredEventEvent(cmd);
        final List<Reservation> reservations = getState().getReservationsList();
        if (isThereUnsatisfiedReservations(reservations)) {
            final BookReadyToPickup bookReadyToPickupEvent = createBookReadyToPickupEvent();
            Pair result = Pair.of(reservationExpiredEvent, bookReadyToPickupEvent);
            return result;
        }
        final BookBecameAvailable bookBecameAvailableEvent = createBookBecameAvailableEvent();
        Pair result = Pair.of(reservationExpiredEvent, bookBecameAvailableEvent);
        return result;
    }

    // @formatter:off
    /**
     * Handles a {@code ReturnBook} command.
     *
     * <p>For details see {@link ReturnBook}.
     * <p>Returns the following event combinations:
     *
     * <ul>
     *      <li>{@code BookReturned, BookBecameAvailable, null} - when there are no
     *           unsatisfied reservations.
     *      <li>{@code BookReturned, BookReadyToPickup, null} - when the returned item
     *          becomes ready to pick up for the first in the reservations queue and other
     *          readers still have reservations.
     *      <li>{@code BookReturned, BookReadyToPickup, LoansBecameExtensionAllowed} - when the
     *          added item becomes ready to pick up for the first in the reservations queue and there
     *          are no unsatisfied reservations except this one.
     *          In that case all recent loans of this book become allowed for extension.
     * </ul>
     *
     * @param cmd command with an identifier of the book that the user is going to return.
     * @return the {@link Triplet} of the events.
     * @throws CannotReturnNonBorrowedBook if a book isn’t borrowed by the user.
     * @throws CannotReturnMissingBook     if a book is missing.
     */
    // @formatter:on
    @Assign
    Triplet<BookReturned,
            EitherOfTwo<BookBecameAvailable, BookReadyToPickup>,
            Optional<LoansBecameExtensionAllowed>> handle(ReturnBook cmd) throws
                                                                          CannotReturnNonBorrowedBook,
                                                                          CannotReturnMissingBook {
        final InventoryItemId inventoryItemId = cmd.getInventoryItemId();
        final UserId userId = cmd.getUserId();
        final List<Loan> loans = getState().getLoansList();
        final List<InventoryItem> inventoryItems = getState().getInventoryItemsList();
        if (!inventoryItemExists(inventoryItemId, inventoryItems)) {
            throw throwCannotReturnMissingBook(cmd);
        }
        if (!isBookBorrowedByUser(userId, loans)) {
            throw cannotReturnNonBorrowedBook(cmd);
        }

        final BookReturned bookReturnedEvent = createBookReturnedEvent(cmd);
        final List<Reservation> reservations = getState().getReservationsList();
        if (!isThereUnsatisfiedReservations(reservations)) {
            final BookBecameAvailable becameAvailable = createBookBecameAvailableEvent();
            final Triplet result = Triplet.withNullable(bookReturnedEvent,
                                                        becameAvailable,
                                                        null);
            return result;
        }

        final BookReadyToPickup bookReadyToPickup = createBookReadyToPickupEvent();

        if (isThereSingleUnsatisfiedReservation(reservations) && isBookBorrowed()) {
            final LoansBecameExtensionAllowed loansBecameExtensionAllowed
                    = createLoansBecameExtensionAllowedEvent();
            final Triplet result = Triplet.of(bookReturnedEvent,
                                              bookReadyToPickup,
                                              loansBecameExtensionAllowed);
            return result;
        }

        Triplet result = Triplet.withNullable(bookReturnedEvent, bookReadyToPickup, null);
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
        final BookLost result = createBookLostEvent(cmd);
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
                                                   .setBookId(event.getBookId())
                                                   .build();
        final InventoryCreated result = InventoryCreated.newBuilder()
                                                        .setInventoryId(inventoryId)
                                                        .setWhenCreated(getCurrentTime())
                                                        .build();
        return result;
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
                                                   .setBookId(event.getBookId())
                                                   .build();
        final InventoryRemoved result =
                InventoryRemoved.newBuilder()
                                .setInventoryId(inventoryId)
                                .setWhenRemoved(getCurrentTime())
                                .build();
        return result;
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
     * Handles a {@code InventoryRemoved} event.
     *
     * <p>For details see {@link InventoryRemoved}.
     *
     * @param event the {@code InventoryRemoved} event message.
     */
    @Apply
    void inventoryRemoved(InventoryRemoved event) {
        getBuilder().clear();
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
    @SuppressWarnings("all")
    @Apply
    void bookBecameAvailable(BookBecameAvailable event) {
    }

    /**
     * A book becomes available for a user.
     *
     * @param event A book is ready to pickup for a user who is next in a queue.
     */
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
        final int reservationIndex = getReservationIndexByUserId(userId, reservations);
        getBuilder().removeReservations(reservationIndex);
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
        final List<Loan> loans = getBuilder().getLoans();
        final InventoryItemId inventoryItemId = event.getInventoryItemId();
        final int itemIndex = getInventoryItemIndexById(inventoryItemId, inventoryItems);
        final int loanIndex = getLoanIndexByInventoryItemId(inventoryItemId, loans);

        getBuilder().removeInventoryItems(itemIndex);
        if (loanIndex != -1) {
            getBuilder().removeLoans(loanIndex);
        }
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
        final int itemPosition = getInventoryItemIndexById(inventoryItemId, inventoryItems);
        final InventoryItem inventoryItem = inventoryItems.get(itemPosition);
        final InventoryItem borrowedItem = InventoryItem.newBuilder(inventoryItem)
                                                        .clearInLibrary()
                                                        .setBorrowed(true)
                                                        .setUserId(event.getWhoBorrowed())
                                                        .build();
        final Timestamp whenBorrowed = event.getWhenBorrowed();
        final Timestamp whenDue = Timestamp.newBuilder()
                                           .setSeconds(whenBorrowed.getSeconds() +
                                                               LOAN_PERIOD)
                                           .build();
        final Loan loan = Loan.newBuilder()
                              .setLoanId(event.getLoanId())
                              .setInventoryItemId(inventoryItemId)
                              .setStatus(LOAN_RECENT)
                              .setWhoBorrowed(event.getWhoBorrowed())
                              .setWhenTaken(whenBorrowed)
                              .setWhenDue(whenDue)
                              .build();

        getBuilder().setInventoryItems(itemPosition, borrowedItem)
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
        final LoanId loanId = event.getLoanId();
        updateLoanStatus(loanId, LOAN_OVERDUE);
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
        final LoanId loanId = event.getLoanId();
        updateLoanStatus(loanId, LOAN_SOULD_RETURN_SOON);
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
        updateLoanStatus(loanId, LOAN_RECENT);
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
        final List<Reservation> reservations = getBuilder().getReservations();
        final int reservationIndex = getReservationIndexByUserId(whoCanceled, reservations);

        getBuilder().removeReservations(reservationIndex);
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
        final List<Reservation> reservations = getBuilder().getReservations();
        final int reservationPosition = getReservationIndexByUserId(userId, reservations);

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

    /**
     * Handles a {@code BookLost} event.
     *
     * <p>For details see {@link BookLost}.
     *
     * @param event a {@code BookLost} event message.
     */
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
                                                        .setLost(true)
                                                        .build();
        getBuilder().setInventoryItems(returnedItemPosition, returnedItem);
        getBuilder().removeLoans(loanIndex);
    }

    private BookBecameAvailable createBookBecameAvailableEvent() {
        final InventoryId inventoryId = getState().getInventoryId();
        final Timestamp currentTime = getCurrentTime();
        final BookBecameAvailable bookBecameAvailable =
                BookBecameAvailable.newBuilder()
                                   .setInventoryId(inventoryId)
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
     * @return {@code BookReadyToPickup} event.
     */
    private BookReadyToPickup createBookReadyToPickupEvent() {
        final Timestamp currentTime = getCurrentTime();
        final List<Reservation> reservationsList = getState().getReservationsList();
        final int reservationIndex = Iterables.indexOf(reservationsList,
                                                       item -> !item.getIsSatisfied());
        final UserId nextInQueue = reservationsList.get(reservationIndex)
                                                   .getWhoReserved();

        final long expirationDate = currentTime.getSeconds() + OPEN_FOR_BORROW_PERIOD;

        final Timestamp pickUpDeadline = Timestamp.newBuilder()
                                                  .setSeconds(expirationDate)
                                                  .build();
        final InventoryId inventoryId = getState().getInventoryId();
        final BookReadyToPickup bookReadyToPickup =
                BookReadyToPickup.newBuilder()
                                 .setInventoryId(inventoryId)
                                 .setForWhom(nextInQueue)
                                 .setWhenBecameReadyToPickup(currentTime)
                                 .setPickUpDeadline(pickUpDeadline)
                                 .build();
        return bookReadyToPickup;
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

    private InventoryDecreased createInventoryDecreasedEvent(WriteBookOff cmd) {
        final InventoryId inventoryId = cmd.getInventoryId();
        final InventoryItemId inventoryItemId = cmd.getInventoryItemId();
        final UserId librarianId = cmd.getLibrarianId();
        final WriteOffReason writeOffReason = cmd.getWriteBookOffReason();
        final InventoryDecreased inventoryDecreased =
                InventoryDecreased.newBuilder()
                                  .setInventoryId(inventoryId)
                                  .setInventoryItemId(inventoryItemId)
                                  .setWhenDecreased(getCurrentTime())
                                  .setLibrarianId(librarianId)
                                  .setWriteOffReason(writeOffReason)
                                  .build();
        return inventoryDecreased;
    }

    private ReservationAdded createReservationAddedEvent(ReserveBook cmd) {
        final InventoryId inventoryId = cmd.getInventoryId();
        final UserId userId = cmd.getUserId();
        final ReservationAdded reservationAdded = ReservationAdded.newBuilder()
                                                                  .setInventoryId(inventoryId)
                                                                  .setForWhomReserved(userId)
                                                                  .setWhenCreated(getCurrentTime())
                                                                  .build();
        return reservationAdded;
    }

    private LoansBecameExtensionAllowed createLoansBecameExtensionAllowedEvent() {
        final InventoryId inventoryId = getState().getInventoryId();
        final List<Loan> loans = getBuilder().getLoans();
        final List<UserId> loanOwners = getLoanOwners(loans);
        final LoansBecameExtensionAllowed loansBecameExtensionAllowed =
                LoansBecameExtensionAllowed.newBuilder()
                                           .setInventoryId(inventoryId)
                                           .setWhenBecame(getCurrentTime())
                                           .addAllBorrowers(loanOwners)
                                           .build();
        return loansBecameExtensionAllowed;
    }

    private LoansBecameNotAllowedForExtension createLoansBecameNotAllowedForExtensionEvent() {
        final InventoryId inventoryId = getState().getInventoryId();
        final List<Loan> loans = getBuilder().getLoans();
        final List<UserId> loanOwners = getLoanOwners(loans);
        final LoansBecameNotAllowedForExtension notAllowedForExtension =
                LoansBecameNotAllowedForExtension.newBuilder()
                                                 .addAllBorrowers(loanOwners)
                                                 .setInventoryId(inventoryId)
                                                 .setWhenBecame(getCurrentTime())
                                                 .build();
        return notAllowedForExtension;
    }

    private BookBorrowed createBookBorrowedEvent(BorrowBook cmd) {
        final InventoryId inventoryId = cmd.getInventoryId();
        final InventoryItemId inventoryItemId = cmd.getInventoryItemId();
        final UserId userId = cmd.getUserId();
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

        final LoanBecameOverdue becameOverdue =
                LoanBecameOverdue.newBuilder()
                                 .setInventoryId(inventoryId)
                                 .setInventoryItemId(inventoryItemId)
                                 .setLoanId(loanId)
                                 .setUserId(whoBorrowed)
                                 .setWhenBecameOverdue(getCurrentTime())
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

    private LoanPeriodExtended createLoanPeriodExtendedEvent(ExtendLoanPeriod cmd) {
        final InventoryId inventoryId = cmd.getInventoryId();
        final LoanId loanId = cmd.getLoanId();
        final UserId userId = cmd.getUserId();
        final List<Loan> loans = getState().getLoansList();
        final int loanPosition = getLoanIndexByLoanId(loanId, loans);
        final Loan loan = loans.get(loanPosition);
        final InventoryItemId inventoryItemId = loan.getInventoryItemId();
        final Timestamp previousDueDate = loan.getWhenDue();
        final long newDueDateInSeconds = previousDueDate.getSeconds() +
                LOAN_PERIOD;
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
        final ReservationCanceled reservationCanceledEvent =
                ReservationCanceled.newBuilder()
                                   .setInventoryId(inventoryId)
                                   .setWhoCanceled(userId)
                                   .setWhenCanceled(getCurrentTime())
                                   .build();
        return reservationCanceledEvent;
    }

    private ReservationPickUpPeriodExpired createReservationPickUpPeriodExpiredEventEvent(
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
        final BookReturned bookReturned = BookReturned.newBuilder()
                                                      .setInventoryId(inventoryId)
                                                      .setInventoryItemId(inventoryItemId)
                                                      .setWhoReturned(userId)
                                                      .setWhenReturned(getCurrentTime())
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

    private void updateLoanStatus(LoanId loanId, LoanStatus loanStatus) {
        final List<Loan> loans = getBuilder().getLoans();
        final int loanPosition = getLoanIndexByLoanId(loanId, loans);
        final Loan loan = loans.get(loanPosition);
        final Loan updatedLoan = Loan.newBuilder(loan)
                                     .setStatus(loanStatus)
                                     .build();
        getBuilder().setLoans(loanPosition, updatedLoan);
    }

    private boolean isThereUnsatisfiedReservations(List<Reservation> reservations) {
        final boolean any = Iterables.any(reservations, item -> !item.getIsSatisfied());
        return any;
    }

    private boolean isBookBorrowed() {
        final boolean isBorrowed = getState().getLoansCount() > 0;
        return isBorrowed;
    }

    /**
     * Checks a book for reservations.
     *
     * @return true if the unsatisfied reservations count is 1.
     */
    private boolean isThereSingleUnsatisfiedReservation(List<Reservation> reservations) {
        final int count = (int) reservations.stream()
                                            .filter(item -> !item.getIsSatisfied())
                                            .count();
        final boolean result = count == 1;
        return result;
    }

    private boolean isUserFirstInReservationsQueue(UserId userId, List<Reservation> reservations) {
        final int reservationIndex = Iterables.indexOf(reservations,
                                                       item -> !item.getIsSatisfied());
        final Reservation reservation = reservations.get(reservationIndex);
        final UserId whoReserved = reservation.getWhoReserved();
        final boolean result = whoReserved.equals(userId);
        return result;
    }

    private boolean isBookReservedByUser(UserId userId, List<Reservation> reservations) {
        final boolean any = Iterables.any(reservations, item -> item.getWhoReserved()
                                                                    .equals(userId));
        return any;
    }

    private boolean isBookBorrowedByUser(UserId userId, List<Loan> loans) {
        final boolean any = Iterables.any(loans, item -> item.getWhoBorrowed()
                                                             .equals(userId));
        return any;
    }

    private boolean isInventoryItemBorrowed(InventoryItemId itemId,
                                            List<InventoryItem> inventoryItems) {
        final int itemIndex = getInventoryItemIndexById(itemId, inventoryItems);
        final InventoryItem inventoryItem = inventoryItems.get(itemIndex);
        return inventoryItem.getBorrowed();
    }

    private boolean inventoryItemExists(InventoryItemId inventoryItemId,
                                        List<InventoryItem> inventoryItemsList) {
        final boolean any = Iterables.any(inventoryItemsList, item -> item.getInventoryItemId()
                                                                          .equals(inventoryItemId));
        return any;
    }

    private boolean loanExists(LoanId loanId, List<Loan> loans) {
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

    private int getLoanIndexByInventoryItemId(InventoryItemId itemId, List<Loan> loansList) {
        final int index = Iterables.indexOf(loansList, item -> item.getInventoryItemId()
                                                                   .equals(itemId));
        return index;
    }

    private int getInventoryItemIndexById(InventoryItemId inventoryItemId,
                                          List<InventoryItem> inventoryItems) {
        final int index = Iterables.indexOf(inventoryItems, item -> item.getInventoryItemId()
                                                                        .equals(inventoryItemId));
        return index;
    }

    private List<UserId> getLoanOwners(List<Loan> loans) {
        final List<UserId> loanOwners = loans.stream()
                                             .map(Loan::getWhoBorrowed)
                                             .collect(Collectors.toList());
        return loanOwners;
    }

    private boolean isLoanAllowedForExtension(LoanId loanId, List<Loan> loans) {
        final int loanIndex = getLoanIndexByLoanId(loanId, loans);
        final Loan loan = loans.get(loanIndex);
        final boolean isAllowed = loan.getIsAllowedExtension();
        return isAllowed;
    }

    private int getInLibraryItemsCount() {
        final List<InventoryItem> inventoryItems = getState().getInventoryItemsList();

        final int count = (int) inventoryItems.stream()
                                              .filter(InventoryItem::getInLibrary)
                                              .count();
        return count;
    }
}
