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

package javaclasses.exlibris.c.aggregate;

import com.google.common.collect.ImmutableList;
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
import javaclasses.exlibris.c.rejection.CannotBorrowBook;
import javaclasses.exlibris.c.rejection.CannotCancelMissingReservation;
import javaclasses.exlibris.c.rejection.CannotExtendLoanPeriod;
import javaclasses.exlibris.c.rejection.CannotReserveBook;
import javaclasses.exlibris.c.rejection.CannotReturnMissingBook;
import javaclasses.exlibris.c.rejection.CannotReturnNonBorrowedBook;
import javaclasses.exlibris.c.rejection.CannotWriteMissingBookOff;

import java.util.List;

import static io.spine.time.Time.getCurrentTime;
import static javaclasses.exlibris.c.aggregate.rejection.InventoryAggregateRejections.BorrowBookRejection.throwCannotBorrowBook;
import static javaclasses.exlibris.c.aggregate.rejection.InventoryAggregateRejections.CancelReservationRejection.throwCannotCancelMissingReservation;
import static javaclasses.exlibris.c.aggregate.rejection.InventoryAggregateRejections.ExtendLoanPeriodRejection.throwCannotExtendLoanPeriod;
import static javaclasses.exlibris.c.aggregate.rejection.InventoryAggregateRejections.ReserveBookRejection.throwCannotReserveBook;
import static javaclasses.exlibris.c.aggregate.rejection.InventoryAggregateRejections.ReturnBookRejection.throwCannotReturnMissingBook;
import static javaclasses.exlibris.c.aggregate.rejection.InventoryAggregateRejections.ReturnBookRejection.throwCannotReturnNonBorrowedBook;
import static javaclasses.exlibris.c.aggregate.rejection.InventoryAggregateRejections.WriteBookOffRejection.throwCannotWriteMissingBookOff;

/**
 * The aggregate managing the state of a {@link Inventory}.
 *
 * @author Alexander Karpets
 * @author Paul Ageyev
 */

@SuppressWarnings({"ClassWithTooManyMethods", /* Task definition cannot be separated and should
                                                 process all commands and events related to it
                                                 according to the domain model.
                                                 The {@code Aggregate} does it with methods
                                                 annotated as {@code Assign} and {@code Apply}.
                                                 In that case class has too many methods.*/
        "OverlyCoupledClass", /* As each method needs dependencies  necessary to perform execution
                                                 that class also overly coupled.*/


        "unused"})  /* Apply methods are private according to the spine design and not used because there is no directly usage.*/

public class InventoryAggregate extends Aggregate<InventoryId, Inventory, InventoryVBuilder> {
    /**
     * Creates a new instance.
     *
     * <p>Constructors of derived classes should have package access level
     * because of the following reasons:
     * <ol>
     * <li>These constructors are not public API of an application.
     * Commands and aggregate IDs are.
     * <li>These constructors need to be accessible from tests in the same package.
     * </ol>
     *
     * <p>Because of the last reason consider annotating constructors with
     * {@code @VisibleForTesting}. The package access is needed only for tests.
     * Otherwise aggregate constructors (that are invoked by {@link javaclasses.exlibris.repository.InventoryRepository}
     * via Reflection) may be left {@code private}.
     *
     * @param id the ID for the new aggregate
     */
    public InventoryAggregate(InventoryId id) {
        super(id);
    }

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

    @Assign
    InventoryDecreased handle(WriteBookOff cmd) throws CannotWriteMissingBookOff {

        List<InventoryItem> inventoryItems = getState().getInventoryItemsList();

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
            throwCannotWriteMissingBookOff(cmd);
        }
        return result;
    }

    @Assign
    ReservationAdded handle(ReserveBook cmd) throws CannotReserveBook {

        List<InventoryItem> inventoryItems = getState().getInventoryItemsList();

        for (InventoryItem inventoryItem : inventoryItems) {
            if (inventoryItem
                    .getUserId()
                    .equals(cmd.getUserId())) {
                throwCannotReserveBook(cmd, true, false);
            }
        }

        List<Reservation> reservations = getState().getReservationsList();

        for (Reservation reservation : reservations) {
            if (reservation.getWhoReserved()
                           .equals(cmd.getUserId())) {
                throwCannotReserveBook(cmd, false, true);
            }
        }

        final InventoryId inventoryId = cmd.getInventoryId();
        final UserId userId = cmd.getUserId();
        final ReservationAdded result = ReservationAdded.newBuilder()
                                                        .setInventoryId(inventoryId)
                                                        .setForWhomReserved(userId)
                                                        .setWhenCreated(getCurrentTime())
                                                        .build();
        return result;
    }

    @Assign
    List<? extends Message> handle(BorrowBook cmd) throws CannotBorrowBook {

        List<InventoryItem> inventoryItems = getState().getInventoryItemsList();

        int inLibraryCount = 0;

        for (InventoryItem inventoryItem1 : inventoryItems) {
            if (inventoryItem1.getInLibrary()) {
                inLibraryCount++;
            }
        }

        for (InventoryItem inventoryItem : inventoryItems) {
            if (inventoryItem.getUserId()
                             .equals(cmd.getUserId())) {
                throwCannotBorrowBook(cmd, true, false);
            }
        }

        if (getState().getReservationsList()
                      .size() >= inLibraryCount && !(userHasReservation(cmd, inLibraryCount))) {
            throwCannotBorrowBook(cmd, false, true);
        }

        final InventoryId inventoryId = cmd.getInventoryId();
        final InventoryItemId inventoryItemId = cmd.getInventoryItemId();
        final UserId userId = cmd.getUserId();
        final ImmutableList.Builder<Message> result = ImmutableList.builder();
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
        result.add(bookBorrowed);
        if (!getState().getReservationsList()
                       .isEmpty() && getState().getReservationsList()
                                               .get(0)
                                               .getWhoReserved()
                                               .getEmail()
                                               .getValue()
                                               .equals(cmd.getUserId()
                                                          .getEmail()
                                                          .getValue())) {
            final ReservationBecameLoan reservationBecameLoan = ReservationBecameLoan.newBuilder()
                                                                                     .setInventoryId(
                                                                                             inventoryId)
                                                                                     .setUserId(
                                                                                             userId)
                                                                                     .setWhenBecameLoan(
                                                                                             getCurrentTime())
                                                                                     .build();
            result.add(reservationBecameLoan);
        }
        return result.build();
    }

    private boolean userHasReservation(BorrowBook cmd, int inLibraryCount) {

        List<Reservation> topReservations = getState().getReservationsList()
                                                      .subList(0, inLibraryCount);
        for (Reservation reservation : topReservations) {
            if (reservation.getWhoReserved()
                           .equals(cmd.getUserId())) {
                return true;
            }
        }
        return false;
    }

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

    @Assign
    LoanPeriodExtended handle(ExtendLoanPeriod cmd) throws CannotExtendLoanPeriod {

        List<Reservation> reservations = getState().getReservationsList();

        if (!getState().getReservationsList()
                       .isEmpty()) {
            throwCannotExtendLoanPeriod(cmd);
        }

        final InventoryId inventoryId = cmd.getInventoryId();
        final LoanId loanId = cmd.getLoanId();
        final UserId userId = cmd.getUserId();
        int loanPosition = -1;

        List<Loan> loansList = getState().getLoansList();
        for (int i = 0; i < loansList.size(); i++) {
            if (loansList.get(i)
                         .getLoanId()
                         .equals(cmd.getLoanId())) {
                loanPosition = i;
            }
        }
        final Timestamp previousDueDate = getState().getLoans(loanPosition)
                                                    .getWhenDue();
        // Two weeks before new due on date.
        final Timestamp newDueDate = Timestamp.newBuilder()
                                              .setSeconds(previousDueDate.getSeconds() +
                                                                  60 * 60 * 24 * 14)
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

    @Assign
    ReservationCanceled handle(CancelReservation cmd) throws CannotCancelMissingReservation {

        ReservationCanceled result = null;

        final List<Reservation> reservations = getState().getReservationsList();

        if (!userHasReservation(cmd)) {
            throwCannotCancelMissingReservation(cmd);
        }

        for (Reservation reservation :
                reservations) {
            if (reservation.getWhoReserved()
                           .equals(cmd.getUserId())) {
                final InventoryId inventoryId = cmd.getInventoryId();
                final UserId userId = cmd.getUserId();
                result = ReservationCanceled.newBuilder()
                                            .setInventoryId(inventoryId)
                                            .setWhoCanceled(userId)
                                            .setWhenCanceled(
                                                    getCurrentTime())
                                            .build();
            }
        }
        return result;
    }

    @Assign
    ReservationPickUpPeriodExpired handle(MarkReservationExpired cmd) {

        final InventoryId inventoryId = cmd.getInventoryId();
        final UserId userId = cmd.getUserId();
        final ReservationPickUpPeriodExpired result = ReservationPickUpPeriodExpired.newBuilder()
                                                                                    .setInventoryId(
                                                                                            inventoryId)
                                                                                    .setUserId(
                                                                                            userId)
                                                                                    .setWhenExpired(
                                                                                            getCurrentTime())
                                                                                    .build();
        return result;
    }

    @Assign
    Pair<InventoryAppended, EitherOfTwo<BookBecameAvailable, BookReadyToPickup>> handle(
            ReturnBook cmd) throws CannotReturnNonBorrowedBook,
                                   CannotReturnMissingBook {
        if (!inventoryItemExists(cmd.getInventoryItemId())) {
            throwCannotReturnMissingBook(cmd);
        }
        if (!userBorrowedBook(cmd.getUserId())) {
            throwCannotReturnNonBorrowedBook(cmd);
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

    @React
    InventoryCreated on(BookAdded event) {

        final InventoryCreated result = InventoryCreated.newBuilder()
                                                        .setInventoryId(InventoryId.newBuilder()
                                                                                   .setBookId(
                                                                                           event.getBookId())
                                                                                   .build())
                                                        .setWhenCreated(getCurrentTime())
                                                        .build();
        return result;
    }

    @Apply
    void inventoryCreated(InventoryCreated event) {

        getBuilder().setInventoryId(event.getInventoryId());
    }

    @React
    InventoryRemoved on(BookRemoved event) {

        final InventoryRemoved result = InventoryRemoved.newBuilder()
                                                        .setInventoryId(InventoryId.newBuilder()
                                                                                   .setBookId(
                                                                                           event.getBookId())
                                                                                   .build())
                                                        .setWhenRemoved(getCurrentTime())
                                                        .build();
        return result;
    }

    @Apply
    void inventoryRemoved(InventoryRemoved event) {

        getBuilder().clearInventoryId()
                    .clearInventoryItems();
    }

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

    @Apply
    void bookBecameAvailable(BookBecameAvailable event) {

        int availableBookIndex = -1;

        List<InventoryItem> inventoryItems = getBuilder().getInventoryItems();

        for (int i = 0; i < inventoryItems.size(); i++) {
            InventoryItem item = inventoryItems.get(i);
            if (item.getInventoryItemId()
                    .equals(event.getInventoryItemId())) {
                availableBookIndex = i;
            }
        }
        getBuilder().setInventoryItems(availableBookIndex, InventoryItem.newBuilder()
                                                                        .setInventoryItemId(
                                                                                event.getInventoryItemId())
                                                                        .setInLibrary(true)
                                                                        .build());
    }

    /**
     * A book becomes available for a user.
     *
     * This method does not change the state of an aggregate but this event is necessary for the read side.
     *
     * @param event Book is ready to pickup for a user who is next in a queue.
     */
    @SuppressWarnings("all")
    @Apply
    void bookReadyToPickup(BookReadyToPickup event) {
    }

    @Apply
    void reservationBecameLoan(ReservationBecameLoan event) {

        List<Reservation> reservations = getBuilder().getReservations();

        for (int i = 0; i < reservations.size(); i++) {
            Reservation reservation = reservations.get(i);
            if (reservation.getWhoReserved()
                           .equals(event.getUserId())) {
                getBuilder().removeReservations(i);
            }
        }
    }

    @Apply
    void inventoryDecreased(InventoryDecreased event) {

        final List<InventoryItem> inventoryItems = getBuilder().getInventoryItems();

        int decreaseItemPosition = -1;

        for (int i = 0; i < inventoryItems.size(); i++) {
            InventoryItem item = inventoryItems.get(i);
            if (item.getInventoryItemId()
                    .equals(event.getInventoryItemId())
                    ) {
                decreaseItemPosition = i;
            }
        }
        getBuilder().removeInventoryItems(decreaseItemPosition);
    }

    @Apply
    void reservationAdded(ReservationAdded event) {
        final Reservation newReservation = Reservation.newBuilder()
                                                      .setBookId(BookId.newBuilder()
                                                                       .setIsbn62(
                                                                               event.getInventoryId()
                                                                                    .getBookId()
                                                                                    .getIsbn62()))
                                                      .setWhenCreated(event.getWhenCreated())
                                                      .setWhoReserved(event.getForWhomReserved())
                                                      .build();
        getBuilder().addReservations(newReservation);
    }

    @Apply
    void bookBorrowed(BookBorrowed event) {

        final List<InventoryItem> inventoryItems = getBuilder().getInventoryItems();
        int borrowItemPosition = -1;

        for (int i = 0; i < inventoryItems.size(); i++) {
            InventoryItem item = inventoryItems.get(i);
            if (item.getInventoryItemId()
                    .equals(event.getInventoryItemId())
                    ) {
                borrowItemPosition = i;
            }
        }

        final InventoryItem borrowedItem = InventoryItem.newBuilder()
                                                        .setBorrowed(true)
                                                        .setUserId(event.getWhoBorrowed())
                                                        .setInventoryItemId(
                                                                event.getInventoryItemId())
                                                        .build();

        // The loan period time in seconds.
        // This period is equals two weeks.
        // secondsInMinute * minutesInHour * hoursInDay * daysInTwoWeeks = 1209600.
        final int loanPeriod = 1209600;

        final Loan loan = Loan.newBuilder()
                              .setLoanId(event.getLoanId())
                              .setInventoryItemId(event.getInventoryItemId())
                              .setOverdue(false)
                              .setWhoBorrowed(event.getWhoBorrowed())
                              .setWhenTaken(getCurrentTime())
                              .setWhenDue(Timestamp.newBuilder()
                                                   .setSeconds(System.currentTimeMillis() / 1000 +
                                                                       loanPeriod)
                                                   .build())
                              .build();

        getBuilder().setInventoryItems(borrowItemPosition, borrowedItem)
                    .addLoans(loan);
    }

    @Apply
    void loanBecameOverdue(LoanBecameOverdue event) {

        final List<Loan> loans = getBuilder().getLoans();

        int loanPosition = -1;

        for (int i = 0; i < loans.size(); i++) {
            if (loans.get(i)
                     .getLoanId()
                     .equals(event.getLoanId())) {
                loanPosition = i;
            }
        }
        getBuilder().setLoans(loanPosition, Loan.newBuilder(loans.get(loanPosition))
                                                .setOverdue(true)
                                                .build());

    }

    @Apply
    void loanPeriodExtended(LoanPeriodExtended event) {

        final List<Loan> loans = getBuilder().getLoans();
        int loanPosition = -1;

        for (int i = 0; i < loans.size(); i++) {
            Loan loan = loans.get(i);
            if (loan.getLoanId()
                    .equals(event.getLoanId())) {
                loanPosition = i;
            }
        }

        Loan previousLoan = getBuilder().getLoans()
                                        .get(loanPosition);

        Loan loan = Loan.newBuilder(previousLoan)
                        .setWhenDue(event.getNewDueDate())
                        .setOverdue(false)
                        .build();

        getBuilder().setLoans(loanPosition, loan);
    }

    @Apply
    void reservationCanceled(ReservationCanceled event) {

        int reservationCancelIndex = -1;
        final List<Reservation> reservations = getBuilder().getReservations();

        for (int i = 0; i < reservations.size(); i++) {
            Reservation reservation = reservations.get(i);
            if (reservation.getWhoReserved()
                           .equals(event.getWhoCanceled())) {
                reservationCancelIndex = i;
            }
        }
        getBuilder().removeReservations(reservationCancelIndex);
    }

    @Apply
    void reservationPickUpPeriodExpired(ReservationPickUpPeriodExpired event) {

        final List<Reservation> reservations = getBuilder().getReservations();
        int reservationPosition = -1;

        for (int i = 0; i < reservations.size(); i++) {
            Reservation reservation = reservations.get(i);
            if (reservation.getWhoReserved()
                           .equals(event.getUserId()
                           )) {
                reservationPosition = i;

            }
        }

        getBuilder().removeReservations(reservationPosition);

    }

    @Apply
    void bookReturned(BookReturned event) {

        final List<InventoryItem> inventoryItems = getBuilder().getInventoryItems();
        int returnedItemPosition = -1;

        for (int i = 0; i < inventoryItems.size(); i++) {
            InventoryItem item = inventoryItems.get(i);
            if (item.getUserId()
                    .equals(event.getWhoReturned()
                    ) && item.getBorrowed()) {
                returnedItemPosition = i;
            }
        }
        final InventoryItem newInventoryItem = InventoryItem.newBuilder()
                                                            .setInventoryItemId(
                                                                    event.getInventoryItemId())
                                                            .setInLibrary(true)
                                                            .build();
        getBuilder().setInventoryItems(returnedItemPosition, newInventoryItem);
        int loanIndex = -1;
        List<Loan> loans = getBuilder().getLoans();
        for (int i = 0; i < loans.size(); i++) {
            Loan loan = loans.get(i);
            if (loan.getWhoBorrowed()

                    .equals(event.getWhoReturned()
                    )) {
                loanIndex = i;
            }
        }
        getBuilder().removeLoans(loanIndex);
    }

    @Apply
    void bookLost(BookLost event) {

        final List<InventoryItem> inventoryItems = getBuilder().getInventoryItems();
        int bookLostItemPosition = -1;

        for (int i = 0; i < inventoryItems.size(); i++) {
            InventoryItem item = inventoryItems.get(i);
            if (item.getInventoryItemId()
                    .equals(event.getInventoryItemId())) {
                bookLostItemPosition = i;
            }
        }

        InventoryItem inventoryItem = InventoryItem.newBuilder(
                inventoryItems.get(bookLostItemPosition))
                                                   .setLost(true)
                                                   .build();

        getBuilder().setInventoryItems(bookLostItemPosition, inventoryItem);
    }

    private boolean userBorrowedBook(UserId userId) {

        for (InventoryItem item : getState().getInventoryItemsList()) {
            if (item.getUserId()

                    .equals(userId)) {
                return true;
            }
        }
        return false;
    }

    private boolean inventoryItemExists(InventoryItemId inventoryItemId) {

        for (InventoryItem item : getState().getInventoryItemsList()) {
            if (item.getInventoryItemId()
                    .equals(inventoryItemId)) {
                return true;
            }
        }
        return false;
    }

    private boolean userHasReservation(CancelReservation cmd) {

        for (Reservation reservation : getState().getReservationsList()) {
            if (reservation.getWhoReserved()
                           .equals(cmd.getUserId())) {
                return true;
            }
        }
        return false;
    }

    private Message becameAvailableOrReadyToPickup(InventoryId inventoryId,
                                                   InventoryItemId inventoryItemId) {

        if (getState().getReservationsList()
                      .isEmpty()) {
            final BookBecameAvailable bookBecameAvailable = BookBecameAvailable.newBuilder()
                                                                               .setInventoryId(
                                                                                       inventoryId)
                                                                               .setInventoryItemId(
                                                                                       inventoryItemId)
                                                                               .setWhenBecameAvailable(
                                                                                       getCurrentTime())
                                                                               .build();
            return bookBecameAvailable;
        } else {
            final Timestamp currentTime = getCurrentTime();
            final UserId nextInQueue = getState().getReservationsList()
                                                 .get(0)
                                                 .getWhoReserved();
            final BookReadyToPickup bookReadyToPickup = BookReadyToPickup.newBuilder()
                                                                         .setInventoryId(
                                                                                 inventoryId)
                                                                         .setInventoryItemId(
                                                                                 inventoryItemId)
                                                                         .setForWhom(nextInQueue)
                                                                         .setWhenBecameReadyToPickup(
                                                                                 currentTime)
                                                                         .setPickUpDeadline(
                                                                                 // User has two days to pickup the book.
                                                                                 Timestamp.newBuilder()
                                                                                          .setSeconds(
                                                                                                  currentTime.getSeconds() +
                                                                                                          60 *
                                                                                                                  60 *
                                                                                                                  24 *
                                                                                                                  2)
                                                                                          .build())
                                                                         .build();
            return bookReadyToPickup;
        }
    }
}
