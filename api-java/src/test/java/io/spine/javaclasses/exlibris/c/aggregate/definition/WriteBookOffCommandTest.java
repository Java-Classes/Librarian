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
import javaclasses.exlibris.c.InventoryDecreased;
import javaclasses.exlibris.c.ReserveBook;
import javaclasses.exlibris.c.WriteBookOff;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.spine.server.aggregate.AggregateMessageDispatcher.dispatchCommand;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class WriteBookOffCommandTest extends InventoryCommandTest<ReserveBook> {

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
    }

    private void appendInventory() {
        final AppendInventory appendInventory = InventoryCommandFactory.appendInventoryInstance();
        dispatchCommand(aggregate, envelopeOf(appendInventory));
    }

    @Test
    void produceEvent() {
        appendInventory();
        final WriteBookOff writeBookOff = InventoryCommandFactory.writeBookOffInstance();
        System.out.println(aggregate);
        final List<? extends Message> messageList = dispatchCommand(aggregate,
                                                                    envelopeOf(writeBookOff));

        assertNotNull(aggregate.getId());
        assertEquals(1, messageList.size());
        assertEquals(InventoryDecreased.class, messageList.get(0)
                                                          .getClass());

        final InventoryDecreased inventoryDecreased = (InventoryDecreased) messageList.get(0);

        assertEquals(InventoryCommandFactory.inventoryId, inventoryDecreased.getInventoryId());

        assertEquals("123456789", inventoryDecreased.getInventoryId()
                                                    .getBookId()
                                                    .getIsbn62()
                                                    .getValue());
        assertEquals(true, inventoryDecreased.getWriteOffReason()
                                             .getOutdated());
        assertEquals(false, inventoryDecreased.getWriteOffReason()
                                              .getLost());
        assertEquals("petr@gmail.com", inventoryDecreased.getLibrarianId()
                                                         .getEmail()
                                                         .getValue());

    }

    @Test
    void writeOffBook() {
        appendInventory();
        final Inventory inventoryBefore = aggregate.getState();
        assertEquals(1, inventoryBefore.getInventoryItemsList()
                                       .size());
        final WriteBookOff writeBookOff = InventoryCommandFactory.writeBookOffInstance();
        dispatchCommand(aggregate, envelopeOf(writeBookOff));

        final Inventory inventoryAfter = aggregate.getState();
        assertEquals(0, inventoryAfter.getInventoryItemsList()
                                      .size());
    }
}
