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

import com.google.protobuf.Empty;
import com.google.protobuf.Message;
import com.google.protobuf.Timestamp;
import io.spine.core.React;
import io.spine.server.aggregate.Aggregate;
import io.spine.server.aggregate.Apply;
import io.spine.server.command.Assign;
import io.spine.server.tuple.EitherOfTwo;
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
import javaclasses.exlibris.c.LoanPeriodExtended;
import javaclasses.exlibris.c.MarkLoanOverdue;
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
import java.util.stream.IntStream;

import static io.spine.time.Time.getCurrentTime;

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
     * This period is equals two weeks.
     * secondsInMinute * minutesInHour * hoursInDay * daysInTwoWeeks
     */
    private static final int LOAN_PERIOD = 60 * 60 * 24 * 14;

    /**
     * User has two days to borrow the book.
     * secondsInMinute * minutesInHours * hoursInTwoDays
     */
    private static final int OPEN_FOR_BORROW_PERIOD = 60 * 60 * 48;

    /**
     * Creates a new instance.
     *
     * @param id the ID for the new aggregate
     */
    public InventoryAggregate(InventoryId id) {
        super(id);
    }

    /**
     * {@code AppendInventory} command handler. For details see {@link AppendInventory}.
     *
     * @param cmd command with the identifier of a specific item.
     * @return the {@code InventoryAppended} event in pair with either {@code BookBecameAvailable} or
     * {@code BookBecameAvailable} events.
     */
    @Assign
    Pair<InventoryAppended, EitherOfTwo<BookBecameAvailable, BookReadyToPickup>> handle(
            AppendInventory cmd) {
        final InventoryId inventoryId = cmd.getInventoryId();
        final InventoryItemId inventoryItemId = cmd.getInventoryItemId();
        final Rfid rfid = cmd.getRfid();
        final UserId userId = cmd.getLibrarianId();

        final InventoryAppended inventoryAppended = InventoryAppended.newBuilder()
                                                                     .setInventoryId(inventoryId)
                                                                     .setInventoryItemId(
                                                                             inventoryItemId)
                                                                     .setRfid(rfid)
                                                                     .setWhenAppended(
                                                                             getCurrentTime())
                                                                     .setLibrarianId(userId)
                                                                     .build();

        final Pair result = Pair.of(inventoryAppended,
                                    becameAvailableOrReadyToPickup(inventoryId, inventoryItemId));
        return result;
    }

    /**
     * {@code WriteBookOff} command handler. For details see {@link WriteBookOff}.
     *
     * @param cmd command with a reason of a book writing off.
     * @return the {@code WriteBookOff} event.
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
                                           .setInventoryItemId(
                                                   inventoryItemId)
                                           .setWhenDecreased(
                                                   getCurrentTime())
                                           .setLibrarianId(librarianId)
                                           .setWriteOffReason(
                                                   writeOffReason)
                                           .build();
            }
        }
        if (result == null) {
            InventoryAggregateRejections.WriteBookOffRejection.throwCannotWriteMissingBookOff(cmd);
        }
        return result;
    }

    /**
     * {@code ReserveBook} command handler. For details see {@link ReserveBook}.
     *
     * @param cmd command with the reason of the book writing off.
     * @return the {@code ReservationAdded} event.
     * @throws BookAlreadyBorrowed if a book already borrowed.
     * @throws BookAlreadyReserved if a reservation already exists.
     */
    @Assign
    ReservationAdded handle(ReserveBook cmd) throws BookAlreadyBorrowed,
                                                    BookAlreadyReserved {
        final List<InventoryItem> inventoryItems = getState().getInventoryItemsList();
        final List<Reservation> reservations = getState().getReservationsList();
        final InventoryId inventoryId = cmd.getInventoryId();
        final UserId userId = cmd.getUserId();

        for (InventoryItem inventoryItem : inventoryItems) {
            if (inventoryItem
                    .getUserId()
                    .equals(cmd.getUserId())) {
                InventoryAggregateRejections.ReserveBookRejection.throwBookAlreadyBorrowed(cmd);
            }
        }

        for (Reservation reservation : reservations) {
            if (reservation.getWhoReserved()
                           .equals(cmd.getUserId())) {
                InventoryAggregateRejections.ReserveBookRejection.throwBookAlreadyReserved(cmd);
            }
        }

        final ReservationAdded result = ReservationAdded.newBuilder()
                                                        .setInventoryId(inventoryId)
                                                        .setForWhomReserved(userId)
                                                        .setWhenCreated(getCurrentTime())
                                                        .build();
        return result;
    }

    /**
     * {@code BorrowBook} command handler. For details see {@link BorrowBook}.
     * You can take a book if amount of books in the library is more than amount of reservations, or if
     * as many "next" reservations as books available and among these reservations
     * one belongs to the user.
     *
     * @param cmd command with the reason of the book writing off.
     * @return the {@code ReservationAdded} event.
     * @throws BookAlreadyBorrowed if a book is already borrowed.
     * @throws NonAvailableBook    if a book is not available.
     */
    @Assign
    Pair<BookBorrowed, Optional<ReservationBecameLoan>> handle(BorrowBook cmd) throws
                                                                               BookAlreadyBorrowed,
                                                                               NonAvailableBook {
        final List<InventoryItem> inventoryItems = getState().getInventoryItemsList();

        final int inLibraryCount = getInLibraryCount(inventoryItems);
        final UserId userId = cmd.getUserId();

        for (InventoryItem inventoryItem : inventoryItems) {
            if (inventoryItem.getUserId()
                             .equals(userId)) {
                InventoryAggregateRejections.BorrowBookRejection.throwBookAlreadyBorrowed(cmd);
            }
        }

        if (getState().getReservationsList()
                      .size() >= inLibraryCount &&
                !(userHasReservation(cmd.getUserId(), inLibraryCount))) {

            InventoryAggregateRejections.BorrowBookRejection.throwNonAvailableBook(cmd);

        }

        final InventoryId inventoryId = cmd.getInventoryId();
        final InventoryItemId inventoryItemId = cmd.getInventoryItemId();

        final BookBorrowed bookBorrowed = BookBorrowed.newBuilder()
                                                      .setInventoryId(inventoryId)
                                                      .setInventoryItemId(inventoryItemId)
                                                      .setWhoBorrowed(userId)
                                                      .setLoanId(LoanId.newBuilder()
                                                                       .setValue(
                                                                               getCurrentTime().getSeconds())
                                                                       .build())
                                                      .setWhenBorrowed(getCurrentTime())
                                                      .build();
        Pair result = Pair.withNullable(bookBorrowed, null);

        if (userHasReservation(cmd.getUserId())) {
            final ReservationBecameLoan reservationBecameLoan = ReservationBecameLoan.newBuilder()
                                                                                     .setInventoryId(
                                                                                             inventoryId)
                                                                                     .setUserId(
                                                                                             userId)
                                                                                     .setWhenBecameLoan(
                                                                                             getCurrentTime())
                                                                                     .build();
            result = Pair.of(bookBorrowed, reservationBecameLoan);
        }
        return result;
    }

    /**
     * Checks for the availability of reservations from the user
     *
     * @param userId — the book that the user is going to borrow.
     * @return true if the such reservation exists.
     */
    private boolean userHasReservation(UserId userId) {
        final boolean userHasReservation = !getState().getReservationsList()
                                                      .isEmpty() &&
                getState().getReservationsList()
                          .get(0)
                          .getWhoReserved()
                          .equals(userId);
        return userHasReservation;
    }

    /**
     * Count the quantity of books in a library.
     *
     * @param inventoryItems — all existing items.
     * @return the quantity of books in a library.
     */
    private int getInLibraryCount(List<InventoryItem> inventoryItems) {
        int inLibraryCount = 0;

        for (InventoryItem inventoryItem : inventoryItems) {
            if (inventoryItem.getInLibrary()) {
                inLibraryCount++;
            }
        }
        return inLibraryCount;
    }

    /**
     * {@code Empty} event handler.
     *
     * @param event the {@code Empty} event message.
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
     * {@code MarkLoanOverdue} command handler. For details see {@link MarkLoanOverdue}.
     *
     * @param cmd command from system that marks the loan as overdue.
     * @return the {@code LoanBecameOverdue} event.
     */
    @Assign
    LoanBecameOverdue handle(MarkLoanOverdue cmd) {
        final InventoryId inventoryId = cmd.getInventoryId();
        final LoanId loanId = cmd.getLoanId();
        final LoanBecameOverdue result = LoanBecameOverdue.newBuilder()
                                                          .setInventoryId(inventoryId)
                                                          .setLoanId(loanId)
                                                          .setWhenExpected(getCurrentTime())
                                                          .build();
        return result;
    }

    /**
     * {@code ExtendLoanPeriod} command handler. For details see {@link ExtendLoanPeriod}.
     *
     * @param cmd command with the ID of a loan
     *            that a user is going to extend.
     * @return the {@code LoanPeriodExtended} event.
     * @throws CannotExtendLoanPeriod if a loan period extension isn’t possible.
     */

    @Assign
    LoanPeriodExtended handle(ExtendLoanPeriod cmd) throws CannotExtendLoanPeriod {
        final UserId userId = cmd.getUserId();

        if (isBookReserved() || !userHasLoan(userId)) {
            InventoryAggregateRejections.ExtendLoanPeriodRejection.throwCannotExtendLoanPeriod(cmd);
        }

        final InventoryId inventoryId = cmd.getInventoryId();
        final LoanId loanId = cmd.getLoanId();

        final List<Loan> loansList = getState().getLoansList();
        final int loanPosition = getLoanPosition(loansList, loanId);
        final Timestamp previousDueDate = getState().getLoans(loanPosition)
                                                    .getWhenDue();

        final long newDueDateInSeconds = previousDueDate.getSeconds() +
                LOAN_PERIOD;
        final Timestamp newDueDate = Timestamp.newBuilder()
                                              .setSeconds(newDueDateInSeconds)
                                              .build();

        final LoanPeriodExtended result = LoanPeriodExtended.newBuilder()
                                                            .setInventoryId(inventoryId)
                                                            .setLoanId(loanId)
                                                            .setUserId(userId)
                                                            .setPreviousDueDate(previousDueDate)
                                                            .setNewDueDate(newDueDate)
                                                            .build();
        return result;
    }

    /**
     * Returns a position of a necessary loan.
     *
     * @param loansList all existing loans.
     * @param loanId    an identifier of the necessary loan.
     * @return the loan position.
     */
    private int getLoanPosition(List<Loan> loansList, LoanId loanId) {
        final OptionalInt optionalPosition =
                IntStream.range(0, loansList.size())
                         .filter(loanPos -> loansList.get(loanPos)
                                                     .getWhoBorrowed()
                                                     .equals(loanId))
                         .findFirst();

        int position = 0;

        if (optionalPosition.isPresent()) {
            position = optionalPosition.getAsInt();
        }
        return position;
    }

    /**
     * {@code CancelReservation} command handler. For details see {@link CancelReservation}.
     *
     * @param cmd command with the ID of a reservation
     *            that a user is going to cancel.
     * @return the {@code ReservationCanceled} event.
     * @throws CannotCancelMissingReservation if a reservation is missing.
     */
    @Assign
    ReservationCanceled handle(CancelReservation cmd) throws CannotCancelMissingReservation {
        if (!userHasReservation(cmd.getUserId())) {
            InventoryAggregateRejections.CancelReservationRejection.throwCannotCancelMissingReservation(
                    cmd);
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
     * {@code MarkReservationExpired} command handler. For details see {@link MarkReservationExpired}.
     *
     * @param cmd system command that contains an identifier of the expired reservation.
     * @return the {@code ReservationPickUpPeriodExpired} event.
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
     * {@code ReturnBook} command handler. For details see {@link ReturnBook}.
     *
     * @param cmd command with an identifier of the book
     *            that the user is going to return.
     * @return the {@code InventoryAppended} event in pair with either {@code BookBecameAvailable} or
     * {@code BookBecameAvailable} events.
     * @throws CannotReturnNonBorrowedBook if a book isn’t borrowed by the user.
     * @throws CannotReturnMissingBook     if a book is missing.
     */
    @Assign
    Pair<InventoryAppended, EitherOfTwo<BookBecameAvailable, BookReadyToPickup>> handle(
            ReturnBook cmd) throws CannotReturnNonBorrowedBook,
                                   CannotReturnMissingBook {
        if (!inventoryItemExists(cmd.getInventoryItemId())) {
            InventoryAggregateRejections.ReturnBookRejection.throwCannotReturnMissingBook(cmd);
        }
        if (!hasUserBorrowedBook(cmd.getUserId())) {
            InventoryAggregateRejections.ReturnBookRejection.throwCannotReturnNonBorrowedBook(cmd);
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

        final Pair result = Pair.of(bookReturned,
                                    becameAvailableOrReadyToPickup(inventoryId, inventoryItemId));

        return result;
    }

    /**
     * {@code ReportLostBook} command handler. For details see {@link ReportLostBook}.
     *
     * @param cmd command that contains the identifier of a lost book
     *            and a user who lost it.
     * @return the {@code BookLost} event.
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
     * React on a {@code BookAdded} event.
     *
     * @param event stimulus for reacting.
     * @return the {@code InventoryCreated} event.
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
     * {@code InventoryCreated} event handler. For details see {@link InventoryCreated}.
     *
     * @param event the {@code InventoryCreated} event message.
     */
    @Apply
    void inventoryCreated(InventoryCreated event) {
        getBuilder().setInventoryId(event.getInventoryId());
    }

    /**
     * React on a {@code BookRemoved} event.
     *
     * @param event stimulus for reacting.
     * @return the {@code InventoryRemoved} event.
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
     * {@code InventoryRemoved} event handler. For details see {@link InventoryRemoved}.
     *
     * @param event the {@code InventoryRemoved} event message.
     */
    @Apply
    void inventoryRemoved(InventoryRemoved event) {
        getBuilder().clearInventoryId()
                    .clearInventoryItems();
    }

    /**
     * {@code InventoryAppended} event handler. For details see {@link InventoryAppended}.
     *
     * @param event the {@code InventoryAppended} event message.
     */
    @Apply
    void inventoryAppended(InventoryAppended event) {
        final InventoryItem newInventoryItem = InventoryItem.newBuilder()
                                                            .setInLibrary(true)
                                                            .setInventoryItemId(
                                                                    event.getInventoryItemId())
                                                            .setInLibrary(true)
                                                            .build();
        getBuilder().addInventoryItems(newInventoryItem);
    }

    /**
     * {@code BookBecameAvailable} event handler. For details see {@link BookBecameAvailable}.
     *
     * @param event the {@code BookBecameAvailable} event message.
     */
    @Apply
    void bookBecameAvailable(BookBecameAvailable event) {
        final List<InventoryItem> inventoryItems = getBuilder().getInventoryItems();

        final InventoryItemId inventoryItemId = event.getInventoryItemId();
        final int availableBookIndex = getItemPosition(inventoryItems, inventoryItemId);
        getBuilder().setInventoryItems(availableBookIndex, InventoryItem.newBuilder()
                                                                        .setInventoryItemId(
                                                                                inventoryItemId)
                                                                        .setInLibrary(true)
                                                                        .build());
    }

    /**
     * A book becomes available for a user.
     *
     * This method does not change a state of an aggregate but this event is necessary for a read side.
     *
     * @param event A book is ready to pickup for a user who is next in a queue.
     */
    @SuppressWarnings("all")
    @Apply
    void bookReadyToPickup(BookReadyToPickup event) {
    }

    /**
     * {@code ReservationBecameLoan} event handler. For details see {@link ReservationBecameLoan}.
     *
     * @param event the {@code ReservationBecameLoan} event message.
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
     * {@code InventoryDecreased} event handler. For details see {@link InventoryDecreased}.
     *
     * @param event the {@code InventoryDecreased} event message.
     */
    @Apply
    void inventoryDecreased(InventoryDecreased event) {
        final List<InventoryItem> inventoryItems = getBuilder().getInventoryItems();

        final InventoryItemId inventoryItemId = event.getInventoryItemId();
        final int decreaseItemPosition = getItemPosition(inventoryItems, inventoryItemId);

        getBuilder().removeInventoryItems(decreaseItemPosition);
    }

    /**
     * {@code ReservationAdded} event handler. For details see {@link ReservationAdded}.
     *
     * @param event the {@code ReservationAdded} event message.
     */
    @Apply
    void reservationAdded(ReservationAdded event) {
        final Isbn62 isbn62 = event.getInventoryId()
                                   .getBookId()
                                   .getIsbn62();
        final Reservation newReservation = Reservation.newBuilder()
                                                      .setBookId(BookId.newBuilder()
                                                                       .setIsbn62(isbn62))
                                                      .setWhenCreated(event.getWhenCreated())
                                                      .setWhoReserved(event.getForWhomReserved())
                                                      .build();
        getBuilder().addReservations(newReservation);
    }

    /**
     * {@code BookBorrowed} event handler. For details see {@link BookBorrowed}.
     *
     * @param event the {@code BookBorrowed} event message.
     */
    @Apply
    void bookBorrowed(BookBorrowed event) {
        final List<InventoryItem> inventoryItems = getBuilder().getInventoryItems();

        final InventoryItemId inventoryItemId = event.getInventoryItemId();
        final int borrowItemPosition = getItemPosition(inventoryItems, inventoryItemId);

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
                              .setOverdue(false)
                              .setWhoBorrowed(event.getWhoBorrowed())
                              .setWhenTaken(getCurrentTime())
                              .setWhenDue(whenDue)
                              .build();

        getBuilder().setInventoryItems(borrowItemPosition, borrowedItem)
                    .addLoans(loan);
    }

    /**
     * Give the position of a necessary item.
     *
     * @param inventoryItems  all existing items.
     * @param inventoryItemId an identifier of the necessary item.
     * @return the item position.
     */
    private int getItemPosition(List<InventoryItem> inventoryItems,
                                InventoryItemId inventoryItemId) {
        final OptionalInt optionalPosition =
                IntStream.range(0, inventoryItems.size())
                         .filter(itemPos -> inventoryItems.get(itemPos)
                                                          .getInventoryItemId()
                                                          .equals(inventoryItemId))
                         .findFirst();

        int position = 0;

        if (optionalPosition.isPresent()) {
            position = optionalPosition.getAsInt();
        }
        return position;
    }

    /**
     * {@code LoanBecameOverdue} event handler. For details see {@link LoanBecameOverdue}.
     *
     * @param event the {@code LoanBecameOverdue} event message.
     */
    @Apply
    void loanBecameOverdue(LoanBecameOverdue event) {
        final List<Loan> loans = getBuilder().getLoans();

        final int loanPosition = getLoanPosition(loans, event.getLoanId());
        getBuilder().setLoans(loanPosition, Loan.newBuilder(loans.get(loanPosition))
                                                .setOverdue(true)
                                                .build());
    }

    /**
     * {@code LoanPeriodExtended} event handler. For details see {@link LoanPeriodExtended}.
     *
     * @param event the {@code LoanPeriodExtended} event message.
     */
    @Apply
    void loanPeriodExtended(LoanPeriodExtended event) {
        final List<Loan> loans = getBuilder().getLoans();

        final LoanId loanId = event.getLoanId();
        final int loanPosition = getLoanPosition(loans, loanId);

        final Loan previousLoan = getBuilder().getLoans()
                                              .get(loanPosition);

        final Loan loan = Loan.newBuilder(previousLoan)
                              .setWhenDue(event.getNewDueDate())
                              .setOverdue(false)
                              .build();

        getBuilder().setLoans(loanPosition, loan);
    }

    /**
     * {@code ReservationCanceled} event handler. For details see {@link ReservationCanceled}.
     *
     * @param event the {@code ReservationCanceled} event message.
     */
    @Apply
    void reservationCanceled(ReservationCanceled event) {
        final List<Reservation> reservations = getBuilder().getReservations();

        final UserId whoCanceled = event.getWhoCanceled();
        final int reservationCancelIndex = getReservationPosition(reservations, whoCanceled);

        getBuilder().removeReservations(reservationCancelIndex);
    }

    /**
     * Returns a position of a necessary reservation.
     *
     * @param reservations — all existing reservations.
     * @param whoCanceled  — an identifier of the necessary user.
     * @return the reservation position.
     */
    private int getReservationPosition(List<Reservation> reservations, UserId whoCanceled) {
        int reservationCancelIndex = 0;
        for (int i = 0; i < reservations.size(); i++) {
            Reservation reservation = reservations.get(i);
            if (reservation.getWhoReserved()
                           .equals(whoCanceled)) {
                reservationCancelIndex = i;
            }
        }
        return reservationCancelIndex;
    }

    /**
     * {@code ReservationPickUpPeriodExpired} event handler. For details see {@link ReservationPickUpPeriodExpired}.
     *
     * @param event the {@code ReservationPickUpPeriodExpired} event message.
     */
    @Apply
    void reservationPickUpPeriodExpired(ReservationPickUpPeriodExpired event) {
        final List<Reservation> reservations = getBuilder().getReservations();

        final UserId userId = event.getUserId();
        final int reservationPosition = getReservationPosition(reservations, userId);

        getBuilder().removeReservations(reservationPosition);
    }

    /**
     * {@code BookReturned} event handler. For details see {@link BookReturned}.
     *
     * @param event the {@code BookReturned} event message.
     */
    @Apply
    void bookReturned(BookReturned event) {
        final List<InventoryItem> inventoryItems = getBuilder().getInventoryItems();

        final int returnedItemPosition = getReturnedItemPosition(event, inventoryItems);
        final InventoryItem newInventoryItem = InventoryItem.newBuilder()
                                                            .setInventoryItemId(
                                                                    event.getInventoryItemId())
                                                            .setInLibrary(true)
                                                            .build();
        getBuilder().setInventoryItems(returnedItemPosition, newInventoryItem);

        final int loanIndex = getLoanIndex(event);
        getBuilder().removeLoans(loanIndex);
    }

    /**
     * Returns a position of a returned book.
     *
     * @param event          — {@code BookReturned} event.
     * @param inventoryItems — an identifier of the necessary item.
     * @return the item position.
     */
    private int getReturnedItemPosition(BookReturned event, List<InventoryItem> inventoryItems) {
        int returnedItemPosition = 0;
        for (int i = 0; i < inventoryItems.size(); i++) {
            InventoryItem item = inventoryItems.get(i);
            if (item.getUserId()
                    .equals(event.getWhoReturned()
                    ) && item.getBorrowed()) {
                returnedItemPosition = i;
            }
        }
        return returnedItemPosition;
    }

    /**
     * Returns a position of a returned book loan.
     *
     * @param event {@code BookReturned} event.
     * @return the loan position.
     */
    private int getLoanIndex(BookReturned event) {
        int loanIndex = 0;
        List<Loan> loans = getBuilder().getLoans();
        for (int i = 0; i < loans.size(); i++) {
            Loan loan = loans.get(i);
            if (loan.getWhoBorrowed()
                    .equals(event.getWhoReturned()
                    )) {
                loanIndex = i;
            }
        }
        return loanIndex;
    }

    /**
     * {@code BookLost} event handler. For details see {@link BookLost}.
     *
     * @param event the {@code BookLost} event message.
     */
    @Apply
    void bookLost(BookLost event) {
        final List<InventoryItem> inventoryItems = getBuilder().getInventoryItems();

        final InventoryItemId inventoryItemId = event.getInventoryItemId();

        final int bookLostItemPosition = getItemPosition(inventoryItems, inventoryItemId);

        final InventoryItem inventoryItem =
                InventoryItem.newBuilder(inventoryItems.get(bookLostItemPosition))
                             .setLost(true)
                             .build();

        getBuilder().setInventoryItems(bookLostItemPosition, inventoryItem);
    }

    /**
     * Checks if a user borrowed a book.
     *
     * @param userId the identifier of a user who could borrow a book.
     * @return true if the user did.
     */
    private boolean hasUserBorrowedBook(UserId userId) {
        final List<InventoryItem> inventoryItemsList = getState().getInventoryItemsList();

        final Optional<InventoryItem> inventoryItem =
                inventoryItemsList.stream()
                                  .filter(item -> item.getUserId()
                                                      .equals(userId))
                                  .findFirst();

        return inventoryItem.isPresent();
    }

    /**
     * Checks if an inventory item exists.
     *
     * @param inventoryItemId the identifier of an item that should exist.
     * @return true if an item exists.
     */
    private boolean inventoryItemExists(InventoryItemId inventoryItemId) {
        final List<InventoryItem> inventoryItemsList = getState().getInventoryItemsList();

        final Optional<InventoryItem> inventoryItem =
                inventoryItemsList.stream()
                                  .filter(item -> item.getInventoryItemId()
                                                      .equals(inventoryItemId))
                                  .findFirst();
        return inventoryItem.isPresent();
    }

    /**
     * Checks a book for reservations.
     *
     * If a book has no reservations then the book {@code becameAvailable},
     * in other way — {@code readyToPickUp}.
     *
     * @param inventoryId     the identifier of an inventory.
     * @param inventoryItemId the identifier of a specific item.
     * @return either {@code BookBecameAvailable} or {@code BookReadyToPickup}.
     */
    private Message becameAvailableOrReadyToPickup(InventoryId inventoryId,
                                                   InventoryItemId inventoryItemId) {
        if (!isBookReserved()) {
            final BookBecameAvailable bookBecameAvailable =
                    BookBecameAvailable.newBuilder()
                                       .setInventoryId(inventoryId)
                                       .setInventoryItemId(inventoryItemId)
                                       .setWhenBecameAvailable(getCurrentTime())
                                       .build();
            return bookBecameAvailable;
        }

        final Timestamp currentTime = getCurrentTime();
        final UserId nextInQueue = getState().getReservationsList()
                                             .get(0)
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

    /**
     * Checks a book for reservations.
     *
     * @return true if the reservation exists.
     */
    private boolean isBookReserved() {
        final boolean reservationExists = getState().getReservationsList()
                                                    .size() > 0;
        return reservationExists;
    }

    /**
     * Checks if a user has a loan for the book.
     *
     * @param userId — the identifier of a user who has loan for the book.
     * @return true if the user has a loan for the book.
     */
    private boolean userHasLoan(UserId userId) {
        final List<Loan> loans = getState().getLoansList();

        final Optional<Loan> userLoan = loans.stream()
                                             .filter(loan -> loan.getWhoBorrowed()
                                                                 .equals(userId))
                                             .findFirst();
        return userLoan.isPresent();
    }
}
