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

package javaclasses.exlibris.c.aggregate.rejection;

import javaclasses.exlibris.c.CancelReservation;
import javaclasses.exlibris.c.ReserveBook;
import javaclasses.exlibris.c.WriteBookOff;
import javaclasses.exlibris.c.rejection.CannotCancelMissingReservation;
import javaclasses.exlibris.c.ReturnBook;
import javaclasses.exlibris.c.rejection.CannotReserveBook;
import javaclasses.exlibris.c.rejection.CannotWriteMissingBookOff;
import javaclasses.exlibris.c.rejection.CannotReturnMissingBook;
import javaclasses.exlibris.c.rejection.CannotReturnNonBorrowedBook;

import static io.spine.time.Time.getCurrentTime;

/**
 * @author Paul Ageyev, Alexander Karpets
 */
public class InventoryAggregateRejections {

    private InventoryAggregateRejections() {
    }

    public static class ReserveBookRejection {

        private ReserveBookRejection() {
        }

        public static void throwCannotReserveBook(ReserveBook cmd, boolean borrowed,
                                                  boolean reserved) throws CannotReserveBook {

            throw new CannotReserveBook(cmd.getInventoryId(), cmd.getUserId(), getCurrentTime(),
                                        borrowed, reserved);
        }
    }

    public static class CancelReservationRejection {

        private CancelReservationRejection() {
        }

        public static void throwCannotCancelMissingReservation(CancelReservation cmd) throws
                                                                                      CannotCancelMissingReservation {

            throw new CannotCancelMissingReservation(cmd.getInventoryId(), cmd.getUserId(),
                                                     getCurrentTime());
        }

    }

    public static class WriteBookOffRejection {

        private WriteBookOffRejection() {
        }

        public static void throwCannotWriteMissingBookOff(WriteBookOff cmd) throws CannotWriteMissingBookOff {

            throw new CannotWriteMissingBookOff(cmd.getInventoryId(), cmd.getLibrarianId(), cmd.getInventoryItemId(), getCurrentTime());

        }
    }

    public static class ReturnBookRejection {
        private ReturnBookRejection() {
        }

        public static void throwCannotReturnNonBorrowedBook(ReturnBook cmd) throws
                                                                            CannotReturnNonBorrowedBook {
            throw new CannotReturnNonBorrowedBook(cmd.getInventoryId(), cmd.getInventoryItemId(),
                                                  cmd.getUserId(), getCurrentTime());
        }

        public static void throwCannotReturnMissingBook(ReturnBook cmd) throws
                                                                        CannotReturnMissingBook {
            throw new CannotReturnMissingBook(cmd.getInventoryId(), cmd.getInventoryItemId(),
                                              cmd.getUserId(), getCurrentTime());
        }
    }
}
