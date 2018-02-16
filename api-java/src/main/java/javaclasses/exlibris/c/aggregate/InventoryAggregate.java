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

import com.google.protobuf.Message;
import com.google.protobuf.Timestamp;
import io.spine.server.aggregate.Aggregate;
import io.spine.server.aggregate.Apply;
import io.spine.server.command.Assign;
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
import javaclasses.exlibris.c.BookBorrowed;
import javaclasses.exlibris.c.BookLost;
import javaclasses.exlibris.c.BookReturned;
import javaclasses.exlibris.c.BorrowBook;
import javaclasses.exlibris.c.CancelReservation;
import javaclasses.exlibris.c.ExtendLoanPeriod;
import javaclasses.exlibris.c.InventoryAppended;
import javaclasses.exlibris.c.InventoryDecreased;
import javaclasses.exlibris.c.LoanBecameOverdue;
import javaclasses.exlibris.c.LoanPeriodExtended;
import javaclasses.exlibris.c.MarkLoanOverdue;
import javaclasses.exlibris.c.MarkReservationExpired;
import javaclasses.exlibris.c.ReportLostBook;
import javaclasses.exlibris.c.ReservationAdded;
import javaclasses.exlibris.c.ReservationCanceled;
import javaclasses.exlibris.c.ReservationPickUpPeriodExpired;
import javaclasses.exlibris.c.ReserveBook;
import javaclasses.exlibris.c.ReturnBook;
import javaclasses.exlibris.c.WriteBookOff;
import javaclasses.exlibris.c.rejection.CannotCancelMissingReservation;
import javaclasses.exlibris.c.rejection.CannotReserveBook;
import javaclasses.exlibris.c.rejection.CannotReturnMissingBook;
import javaclasses.exlibris.c.rejection.CannotReturnNonBorrowedBook;
import javaclasses.exlibris.c.rejection.CannotWriteMissingBookOff;

import java.util.List;
import static io.spine.time.Time.getCurrentTime;
import static java.util.Collections.singletonList;
import static javaclasses.exlibris.c.aggregate.rejection.InventoryAggregateRejections.CancelReservationRejection.throwCannotCancelMissingReservation;
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
        "OverlyCoupledClass"}) /* As each method needs dependencies  necessary to perform execution
                                                 that class also overly coupled.*/

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
    List<? extends Message> handle(AppendInventory cmd) {

        final InventoryId inventoryId = cmd.getInventoryId();
        final InventoryItemId inventoryItemId = cmd.getInventoryItemId();
        final Rfid rfid = cmd.getRfid();
        final UserId userId = cmd.getLibrarianId();
        final InventoryAppended result = InventoryAppended.newBuilder()
                                                          .setInventoryId(inventoryId)
                                                          .setInventoryItemId(inventoryItemId)
                                                          .setRfid(rfid)
                                                          .setWhenAppended(getCurrentTime())
                                                          .setLibrarianId(userId)
                                                          .build();
        return singletonList(result);
    }

    @Assign
    List<? extends Message> handle(WriteBookOff cmd) throws CannotWriteMissingBookOff,
                                                            CannotWriteMissingBookOff {

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
                break;
            }
        }

        if (result == null) {
            throwCannotWriteMissingBookOff(cmd);
        }

        return singletonList(result);
    }

    @Assign
    List<? extends Message> handle(ReserveBook cmd) throws CannotReserveBook {

        List<InventoryItem> inventoryItems = getState().getInventoryItemsList();

        for (int i = 0; i < inventoryItems.size(); i++) {
            if (inventoryItems.get(i)
                              .getInventoryItemId()
                              .getBookId()
                              .equals(cmd.getInventoryId()
                                         .getBookId())) {

                throwCannotReserveBook(cmd, true, false);
            }
        }

        List<Reservation> reservations = getState().getReservationsList();

        for (Reservation reservation : reservations) {
            if (reservation.getBookId()
                           .equals(cmd.getInventoryId()
                                      .getBookId()) || reservation.getWhoReserved()

                                                                  .equals(
                                                                          cmd.getUserId()
                                                                  )) {
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
        return singletonList(result);
    }

    @Assign
    List<? extends Message> handle(BorrowBook cmd) {

        final InventoryId inventoryId = cmd.getInventoryId();
        final InventoryItemId inventoryItemId = cmd.getInventoryItemId();
        final UserId userId = cmd.getUserId();

        final BookBorrowed result = BookBorrowed.newBuilder()
                                                .setInventoryId(inventoryId)
                                                .setInventoryItemId(inventoryItemId)
                                                .setWhoBorrowed(userId)
                                                .setLoanId(LoanId.newBuilder()
                                                                 .setValue(
                                                                         getCurrentTime().getSeconds())
                                                                 .build())
                                                .setWhenBorrowed(getCurrentTime())
                                                .build();
        return singletonList(result);
    }

    @Assign
    List<? extends Message> handle(MarkLoanOverdue cmd) {

        final InventoryId inventoryId = cmd.getInventoryId();
        final LoanId loanId = cmd.getLoanId();
        final LoanBecameOverdue result = LoanBecameOverdue.newBuilder()
                                                          .setInventoryId(inventoryId)
                                                          .setLoanId(loanId)
                                                          .setWhenExpected(getCurrentTime())
                                                          .build();
        return singletonList(result);
    }

    @Assign
    List<? extends Message> handle(ExtendLoanPeriod cmd) {

        final InventoryId inventoryId = cmd.getInventoryId();
        final LoanId loanId = cmd.getLoanId();
        final UserId userId = cmd.getUserId();
        final Timestamp newDueDate = cmd.getNewDueDate();

        // Two weeks before new due on date.
        final Timestamp previousDueDate = Timestamp.newBuilder()
                                                   .setSeconds(newDueDate.getSeconds() -
                                                                       60 * 60 * 24 * 14)
                                                   .build();
        final LoanPeriodExtended result = LoanPeriodExtended.newBuilder()
                                                            .setInventoryId(inventoryId)
                                                            .setLoanId(loanId)
                                                            .setUserId(userId)
                                                            .setPreviousDueDate(previousDueDate)
                                                            .setNewDueDate(newDueDate)
                                                            .build();

        return singletonList(result);
    }

    @Assign
    List<? extends Message> handle(CancelReservation cmd) throws CannotCancelMissingReservation {

        ReservationCanceled result = null;

        final List<Reservation> reservations = getState().getReservationsList();

        if (reservations.isEmpty()) {
            throwCannotCancelMissingReservation(cmd);
        }

        for (Reservation reservation :
                reservations) {
            if (reservation.getBookId()
                           .equals(cmd.getInventoryId()
                                      .getBookId()) && reservation.getWhoReserved()
                                                                  .getEmail()
                                                                  .equals(cmd.getUserId()
                                                                             .getEmail())) {
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

        if (result == null) {
            throwCannotCancelMissingReservation(cmd);
        }

        return singletonList(result);
    }

    @Assign
    List<? extends Message> handle(MarkReservationExpired cmd) {

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
        return singletonList(result);
    }

    @Assign
    List<? extends Message> handle(ReturnBook cmd) throws CannotReturnNonBorrowedBook,
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
        final BookReturned result = BookReturned.newBuilder()
                                                .setInventoryId(inventoryId)
                                                .setInventoryItemId(inventoryItemId)
                                                .setWhoReturned(userId)
                                                .setWhenReturned(getCurrentTime())
                                                .build();
        return singletonList(result);
    }

    @Assign
    List<? extends Message> handle(ReportLostBook cmd) {

        final InventoryId inventoryId = cmd.getInventoryId();
        final InventoryItemId inventoryItemId = cmd.getInventoryItemId();
        final UserId userId = cmd.getWhoLost();
        final BookLost result = BookLost.newBuilder()
                                        .setInventoryId(inventoryId)
                                        .setInventoryItemId(inventoryItemId)
                                        .setWhoLost(userId)
                                        .setWhenReported(getCurrentTime())
                                        .build();
        return singletonList(result);
    }

    @Apply
    private void inventoryAppended(InventoryAppended event) {

        final InventoryItem newInventoryItem = InventoryItem.newBuilder()
                                                            .setInLibrary(true)
                                                            .setInventoryItemId(
                                                                    event.getInventoryItemId())
                                                            .build();
        getBuilder().addInventoryItems(newInventoryItem);
    }

    @Apply
    private void inventoryDecreased(InventoryDecreased event) {
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
    private void reservationAdded(ReservationAdded event) {
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
    private void bookBorrowed(BookBorrowed event) {
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
    private void loanBecameOverdue(LoanBecameOverdue event) {
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
    private void loanPeriodExtended(LoanPeriodExtended event) {

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
    private void reservationCanceled(ReservationCanceled event) {
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
    private void reservationPickUpPeriodExpired(ReservationPickUpPeriodExpired event) {
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
    private void bookReturned(BookReturned event) {
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
    private void bookLost(BookLost event) {

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
}
