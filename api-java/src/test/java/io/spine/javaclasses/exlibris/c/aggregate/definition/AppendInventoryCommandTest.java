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

package io.spine.javaclasses.exlibris.c.aggregate.definition;

import com.google.protobuf.Message;
import io.spine.javaclasses.exlibris.testdata.InventoryCommandFactory;
import javaclasses.exlibris.Inventory;
import javaclasses.exlibris.c.AppendInventory;
import javaclasses.exlibris.c.InventoryAppended;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.spine.server.aggregate.AggregateMessageDispatcher.dispatchCommand;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Alexander Karpets
 */
public class AppendInventoryCommandTest extends InventoryCommandTest<AppendInventory> {

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
    }

    @Test
    void produceEvent() {
        final AppendInventory appendInventory = InventoryCommandFactory.appendInventoryInstance();

        final List<? extends Message> messageList = dispatchCommand(aggregate, envelopeOf(appendInventory));

        assertNotNull(aggregate.getId());
        assertEquals(1, messageList.size());
        assertEquals(InventoryAppended.class, messageList.get(0)
                                                         .getClass());

        final InventoryAppended inventoryAppended = (InventoryAppended) messageList.get(0);

        assertEquals(InventoryCommandFactory.inventoryId, inventoryAppended.getInventoryId());

        assertEquals(InventoryCommandFactory.userId.getEmail()
                           .getValue(), inventoryAppended.getLibrarianId()
                                                 .getEmail()
                                                 .getValue());
    }
    @Test
    void appendInventory() {
        final AppendInventory appendInventory = InventoryCommandFactory.appendInventoryInstance();
        dispatchCommand(aggregate, envelopeOf(appendInventory));

        final Inventory inventory = aggregate.getState();
        System.out.println(inventory);
        assertEquals(1,inventory.getInventoryItemsList().size());
        assertEquals(true, inventory.getInventoryItemsList().get(0).getInLibrary());
        assertEquals(false, inventory.getInventoryItemsList().get(0).getLost());
        assertEquals(false, inventory.getInventoryItemsList().get(0).getBorrowed());
        assertEquals(1, inventory.getInventoryItemsList().get(0).getInventoryItemId().getItemNumber());
        assertEquals("123456789", inventory.getInventoryItemsList().get(0).getInventoryItemId().getBookId().getIsbn62().getValue());
        assertEquals("", inventory.getInventoryItemsList().get(0).getUserId().getEmail().getValue());
    }
}
