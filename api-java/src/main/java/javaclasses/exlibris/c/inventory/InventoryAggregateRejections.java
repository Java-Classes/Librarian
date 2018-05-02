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

import javaclasses.exlibris.c.BorrowBook;
import javaclasses.exlibris.c.CancelReservation;
import javaclasses.exlibris.c.ExtendLoanPeriod;
import javaclasses.exlibris.c.ReserveBook;
import javaclasses.exlibris.c.ReturnBook;
import javaclasses.exlibris.c.WriteBookOff;
import javaclasses.exlibris.c.rejection.BookAlreadyBorrowed;
import javaclasses.exlibris.c.rejection.BookAlreadyReserved;
import javaclasses.exlibris.c.rejection.CannotCancelMissingReservation;
import javaclasses.exlibris.c.rejection.CannotExtendLoanPeriod;
import javaclasses.exlibris.c.rejection.CannotReturnMissingBook;
import javaclasses.exlibris.c.rejection.CannotReturnNonBorrowedBook;
import javaclasses.exlibris.c.rejection.CannotWriteBookOff;
import javaclasses.exlibris.c.rejection.NonAvailableBook;

import static com.google.common.base.Preconditions.checkNotNull;
import static io.spine.time.Time.getCurrentTime;

/**
 * Generate rejections for {@link InventoryAggregate}.
 *
 * <p>To throw a rejection it is necessary to call static method.
 *
 * @author Paul Ageyev
 * @author Alexander Karpets
 * @see InventoryAggregate
 */
class InventoryAggregateRejections {

    private InventoryAggregateRejections() {
    }

    /**
     * Holds 2 rejections:
     * <ol>
     * <li> {@link BookAlreadyBorrowed} a rejection when a user tries to reserve a book that he borrowed by himself.</li>
     * <li> {@link BookAlreadyReserved} a rejection when a user tries to reserve a book that he already reserved.</li>
     * </ol>
     */
    static class ReserveBookRejection {
        private ReserveBookRejection() {
        }

        /**
         * Throws a rejection when a user tries to reserve a book that he borrowed by himself.
         */
        static BookAlreadyBorrowed bookAlreadyBorrowed(ReserveBook cmd) throws BookAlreadyBorrowed {
            checkNotNull(cmd);
            throw new BookAlreadyBorrowed(cmd.getInventoryId(), cmd.getUserId(), getCurrentTime());
        }

        /**
         * Throws a rejection when a user tries to reserve a book that he already reserved.
         */
        static BookAlreadyReserved bookAlreadyReserved(ReserveBook cmd) throws BookAlreadyReserved {
            checkNotNull(cmd);
            throw new BookAlreadyReserved(cmd.getInventoryId(), cmd.getUserId(), getCurrentTime());
        }
    }

    /**
     * Throws a rejection when a user tries to cancel a missing reservation.
     */
    static CannotCancelMissingReservation cannotCancelMissingReservation(CancelReservation cmd)
            throws CannotCancelMissingReservation {
        checkNotNull(cmd);
        throw new CannotCancelMissingReservation(cmd.getInventoryId(), cmd.getUserId(),
                                                 getCurrentTime());
    }

    /**
     * Throws a rejection when a librarian tries to write a missing book off.
     */
    static CannotWriteBookOff cannotWriteBookOff(WriteBookOff cmd)
            throws CannotWriteBookOff {
        checkNotNull(cmd);
        throw new CannotWriteBookOff(cmd.getInventoryId(), cmd.getLibrarianId(),
                                     cmd.getInventoryItemId(), getCurrentTime());
    }

    /**
     * Holds two rejections:
     * <ol>
     * <li>{@link CannotReturnNonBorrowedBook} a rejection when a user tries to return a non-borrowed book.</li>
     * <li>{@link CannotReturnMissingBook} a rejection when a user tries to return the missing {@link javaclasses.exlibris.InventoryItem}.</li>
     * </ol>
     */
    static class ReturnBookRejection {
        private ReturnBookRejection() {
        }

        /**
         * Throws a rejection when a user tries to return a non-borrowed book.
         */
        static CannotReturnNonBorrowedBook cannotReturnNonBorrowedBook(ReturnBook cmd)
                throws CannotReturnNonBorrowedBook {
            checkNotNull(cmd);
            throw new CannotReturnNonBorrowedBook(cmd.getInventoryId(), cmd.getInventoryItemId(),
                                                  cmd.getUserId(), getCurrentTime());
        }

        /**
         * Throws a rejection when a user tries to return a missing {@link javaclasses.exlibris.InventoryItem}.
         */
        static CannotReturnMissingBook throwCannotReturnMissingBook(ReturnBook cmd)
                throws CannotReturnMissingBook {
            checkNotNull(cmd);
            throw new CannotReturnMissingBook(cmd.getInventoryId(), cmd.getInventoryItemId(),
                                              cmd.getUserId(), getCurrentTime());
        }
    }

    /**
     * Throws a rejection when a user tries to extend loan period but another user has already reserved book.
     */
    static CannotExtendLoanPeriod cannotExtendLoanPeriod(ExtendLoanPeriod cmd)
            throws CannotExtendLoanPeriod {
        checkNotNull(cmd);
        throw new CannotExtendLoanPeriod(cmd.getInventoryId(), cmd.getLoanId(), cmd.getUserId(),
                                         getCurrentTime());
    }

    /**
     * Holds two rejections:
     * <ol>
     * <li>{@link BookAlreadyBorrowed} a rejection when a user tries to borrow book that he has already borrowed.</li>
     * <li>{@link NonAvailableBook} a rejection when a user tries to borrow non available book.</li>
     * </ol>
     */
    static class BorrowBookRejection {
        private BorrowBookRejection() {
        }

        /**
         * Throws a rejection when a user tries to borrow book that he has already borrowed.
         */
        static BookAlreadyBorrowed bookAlreadyBorrowed(BorrowBook cmd) throws BookAlreadyBorrowed {
            checkNotNull(cmd);
            throw new BookAlreadyBorrowed(cmd.getInventoryId(), cmd.getUserId(), getCurrentTime());
        }

        /**
         * Throws a rejection when a user tries to borrow non available book.
         */
        static NonAvailableBook nonAvailableBook(BorrowBook cmd) throws NonAvailableBook {
            checkNotNull(cmd);
            throw new NonAvailableBook(cmd.getInventoryId(), cmd.getInventoryItemId(),
                                       cmd.getUserId(), getCurrentTime());
        }
    }
}
