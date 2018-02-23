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

import com.google.protobuf.Message;
import javaclasses.exlibris.Inventory;
import javaclasses.exlibris.c.AppendInventory;
import javaclasses.exlibris.c.InventoryAppended;
import javaclasses.exlibris.c.ReserveBook;
import javaclasses.exlibris.testdata.InventoryCommandFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.spine.server.aggregate.AggregateMessageDispatcher.dispatchCommand;
import static javaclasses.exlibris.testdata.InventoryCommandFactory.isbn62;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Alexander Karpets
 */
@DisplayName("AppendInventory command should be interpreted by InventoryAggregate and")
public class AppendInventoryCommandTest extends InventoryCommandTest<AppendInventory> {

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
    }

    @Test
    @DisplayName("produce InventoryAppended event")
    void produceEvent() {
        final AppendInventory appendInventory = InventoryCommandFactory.appendInventoryInstance();

        final List<? extends Message> messageList = dispatchCommand(aggregate,
                                                                    envelopeOf(appendInventory));

        assertNotNull(aggregate.getId());
        assertEquals(2, messageList.size());
        assertEquals(InventoryAppended.class, messageList.get(0)
                                                         .getClass());

        final InventoryAppended inventoryAppended = (InventoryAppended) messageList.get(0);

        assertEquals(InventoryCommandFactory.inventoryId, inventoryAppended.getInventoryId());
        assertEquals(InventoryCommandFactory.userId, inventoryAppended.getLibrarianId());

    }

    @Test
    @DisplayName("append inventory with no reservations")
    void appendInventoryWithoutReservation() {
        final AppendInventory appendInventory = InventoryCommandFactory.appendInventoryInstance();
        dispatchCommand(aggregate, envelopeOf(appendInventory));

        final Inventory inventory = aggregate.getState();
        assertEquals(1, inventory.getInventoryItemsList()
                                 .size());
        assertEquals(true, inventory.getInventoryItemsList()
                                    .get(0)
                                    .getInLibrary());
        assertEquals(false, inventory.getInventoryItemsList()
                                     .get(0)
                                     .getLost());
        assertEquals(false, inventory.getInventoryItemsList()
                                     .get(0)
                                     .getBorrowed());
        assertEquals(1, inventory.getInventoryItemsList()
                                 .get(0)
                                 .getInventoryItemId()
                                 .getItemNumber());
        assertEquals(isbn62, inventory.getInventoryItemsList()
                                      .get(0)
                                      .getInventoryItemId()
                                      .getBookId()
                                      .getIsbn62());
        assertEquals("", inventory.getInventoryItemsList()
                                  .get(0)
                                  .getUserId()
                                  .getEmail()
                                  .getValue());
    }

    @Test
    @DisplayName("append inventory with reservation")
    void appendInventoryWithReservation() {
        final ReserveBook reserveBook = InventoryCommandFactory.reserveBookInstance();
        dispatchCommand(aggregate, envelopeOf(reserveBook));

        final AppendInventory appendInventory = InventoryCommandFactory.appendInventoryInstance();
        dispatchCommand(aggregate, envelopeOf(appendInventory));
        final Inventory inventory = aggregate.getState();
        assertEquals(1, inventory.getInventoryItemsList()
                                 .size());
        assertTrue(inventory.getInventoryItemsList()
                            .get(0)
                            .getInLibrary());
        assertFalse(inventory.getInventoryItemsList()
                             .get(0)
                             .getLost());
        assertFalse(inventory.getInventoryItemsList()
                             .get(0)
                             .getBorrowed());
        assertEquals(1, inventory.getInventoryItemsList()
                                 .get(0)
                                 .getInventoryItemId()
                                 .getItemNumber());
        assertEquals(isbn62, inventory.getInventoryItemsList()
                                      .get(0)
                                      .getInventoryItemId()
                                      .getBookId()
                                      .getIsbn62());
        assertEquals("", inventory.getInventoryItemsList()
                                  .get(0)
                                  .getUserId()
                                  .getEmail()
                                  .getValue());
    }
}
