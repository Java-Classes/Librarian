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
import io.spine.server.aggregate.Aggregate;
import io.spine.server.command.Assign;
import javaclasses.exlibris.Inventory;
import javaclasses.exlibris.InventoryId;
import javaclasses.exlibris.InventoryItemId;
import javaclasses.exlibris.InventoryVBuilder;
import javaclasses.exlibris.Rfid;
import javaclasses.exlibris.UserId;
import javaclasses.exlibris.WriteOffReason;
import javaclasses.exlibris.c.AppendInventory;
import javaclasses.exlibris.c.BookBorrowed;
import javaclasses.exlibris.c.BorrowBook;
import javaclasses.exlibris.c.InventoryAppended;
import javaclasses.exlibris.c.InventoryDecreased;
import javaclasses.exlibris.c.LoanBecameOverdue;
import javaclasses.exlibris.c.MarkLoanOverdue;
import javaclasses.exlibris.c.ReservationAdded;
import javaclasses.exlibris.c.ReserveBook;
import javaclasses.exlibris.c.WriteBookOff;

import java.util.List;

import static io.spine.time.Time.getCurrentTime;
import static java.util.Collections.singletonList;

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
    protected InventoryAggregate(InventoryId id) {
        super(id);
    }

    @Assign
    List<? extends Message> handle(AppendInventory cmd) {

        final InventoryId inventoryId = cmd.getIntentoryId();
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
    List<? extends Message> handle(WriteBookOff cmd) {

        final InventoryId inventoryId = cmd.getInventoryId();
        final InventoryItemId inventoryItemId = cmd.getInventoryItemId();
        final UserId userId = cmd.getLibrarianId();
        final WriteOffReason writeOffReason=cmd.getWriteBookOffReason();
        final InventoryDecreased result = InventoryDecreased.newBuilder()
                                                      .setInventoryId(inventoryId)
                                                      .setInventoryItemId(inventoryItemId)
                                                      .setWhenDecreased(getCurrentTime())
                                                      .setLibrarianId(userId)
                                                      .setWriteOffReason(writeOffReason)
                                                      .build();
        return singletonList(result);
    }
    @Assign
    List<? extends Message> handle(ReserveBook cmd) {

        final InventoryId inventoryId = cmd.getIntentoryId();
        final UserId userId =cmd.getUserId();
        final ReservationAdded result = ReservationAdded.newBuilder()
                                                        .setInventoryId(inventoryId)
                                                        .setForWhomReserved(userId)
                                                        .setWhenCreated(getCurrentTime())
                                                        .build();
        return singletonList(result);
    }
    @Assign
    List<? extends Message> handle(BorrowBook cmd) {

        final InventoryId inventoryId = cmd.getIntentoryId();
        final InventoryItemId inventoryItemId =cmd.getIntentoryItemId();
        final UserId userId =cmd.getUserId();
        final BookBorrowed result= BookBorrowed.newBuilder()
                                         .setInventoryId(inventoryId)
                                         .setInventoryItemId(inventoryItemId)
                                         .setWhoBorrowed(userId)
                                         .setWhenBorrowed(getCurrentTime()).build();
        return singletonList(result);
    }
    @Assign
    List<? extends Message> handle(MarkLoanOverdue cmd) {

        final InventoryId inventoryId = cmd.();
        final InventoryItemId inventoryItemId =cmd.getIntentoryItemId();
        final UserId userId =cmd.getUserId();
        final LoanBecameOverdue result= LoanBecameOverdue.newBuilder()
                                                         .setInventoryId(inventoryId)
                                                         .setInventoryItemId(inventoryItemId)
                                                         .setWhoBorrowed(userId)
                                                         .setWhenBorrowed(getCurrentTime()).build();
        return singletonList(result);
    }
}
