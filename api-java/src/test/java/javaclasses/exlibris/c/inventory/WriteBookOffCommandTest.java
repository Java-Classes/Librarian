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

import com.google.common.base.Throwables;
import com.google.protobuf.Message;
import javaclasses.exlibris.Inventory;
import javaclasses.exlibris.c.AppendInventory;
import javaclasses.exlibris.c.BorrowBook;
import javaclasses.exlibris.c.InventoryDecreased;
import javaclasses.exlibris.c.WriteBookOff;
import javaclasses.exlibris.c.rejection.CannotWriteBookOff;
import javaclasses.exlibris.testdata.InventoryCommandFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.spine.server.aggregate.AggregateMessageDispatcher.dispatchCommand;
import static javaclasses.exlibris.testdata.TestValues.INVENTORY_ID;
import static javaclasses.exlibris.testdata.TestValues.USER_ID;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Alexander Karpets
 * @author Paul Ageyev
 */
@DisplayName("WriteBookOffCommand command should be interpreted by InventoryAggregate and")
public class WriteBookOffCommandTest extends InventoryCommandTest<WriteBookOff> {

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
    }

    @Test
    @DisplayName("produce InventoryDecreased event")
    void produceEvent() {
        appendInventory();

        final WriteBookOff writeBookOff = InventoryCommandFactory.writeBookOffInstance();
        final List<? extends Message> messageList = dispatchCommand(aggregate,
                                                                    envelopeOf(writeBookOff));
        assertEquals(1, messageList.size());
        assertEquals(InventoryDecreased.class, messageList.get(0)
                                                          .getClass());
        final InventoryDecreased inventoryDecreased = (InventoryDecreased) messageList.get(0);

        assertEquals(INVENTORY_ID, inventoryDecreased.getInventoryId());
        assertEquals(true, inventoryDecreased.getWriteOffReason()
                                             .getOutdated());
        assertEquals(false, inventoryDecreased.getWriteOffReason()
                                              .getLost());
        assertEquals(USER_ID, inventoryDecreased.getLibrarianId());
    }

    @Test
    @DisplayName("decrease inventory")
    void writeOffBook() {
        appendInventory();

        final Inventory inventoryBefore = aggregate.getState();
        assertEquals(1, inventoryBefore.getInventoryItemsCount());
        final WriteBookOff writeBookOff = InventoryCommandFactory.writeBookOffInstance();
        dispatchCommand(aggregate, envelopeOf(writeBookOff));

        final Inventory inventoryAfter = aggregate.getState();
        assertEquals(0, inventoryAfter.getInventoryItemsCount());
    }

    @Test
    @DisplayName("throw WriteBookOffRejection rejection upon " +
            "an attempt to write off a missing book")
    void writeOffMissingBook() {
        final WriteBookOff writeBookOff = InventoryCommandFactory.writeBookOffInstance();

        final Throwable t = assertThrows(Throwable.class,
                                         () -> dispatchCommand(aggregate,
                                                               envelopeOf(writeBookOff)));
        final Throwable cause = Throwables.getRootCause(t);
        assertThat(cause, instanceOf(CannotWriteBookOff.class));
    }

    @Test
    @DisplayName("throw WriteBookOffRejection rejection upon " +
            "an attempt to write off borrowed book")
    void writeOffBorrowedBook() {
        appendInventory();
        borrowBook();
        final WriteBookOff writeBookOff = InventoryCommandFactory.writeBookOffInstance();

        final Throwable t = assertThrows(Throwable.class,
                                         () -> dispatchCommand(aggregate,
                                                               envelopeOf(writeBookOff)));
        final Throwable cause = Throwables.getRootCause(t);
        assertThat(cause, instanceOf(CannotWriteBookOff.class));
    }

    private void appendInventory() {
        final AppendInventory appendInventory = InventoryCommandFactory.appendInventoryInstance();
        dispatchCommand(aggregate, envelopeOf(appendInventory));
    }

    private void borrowBook() {
        final BorrowBook borrowBook = InventoryCommandFactory.borrowBookInstance();
        dispatchCommand(aggregate, envelopeOf(borrowBook));
    }
}
