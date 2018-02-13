

package io.spine.javaclasses.exlibris.c.aggregate.definition;

import com.google.protobuf.Message;
import io.spine.javaclasses.exlibris.testdata.InventoryCommandFactory;
import javaclasses.exlibris.Inventory;
import javaclasses.exlibris.c.ReservationAdded;
import javaclasses.exlibris.c.ReserveBook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.spine.server.aggregate.AggregateMessageDispatcher.dispatchCommand;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
/**
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
 *
 * @author Alexander Karpets
 */
public class ReserveBookCommandTest extends InventoryCommandTest<ReserveBook> {

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
    }

    @Test
    void produceEvent() {
        final ReserveBook reserveBook = InventoryCommandFactory.reserveBookInstance();

        final List<? extends Message> messageList = dispatchCommand(aggregate, envelopeOf(reserveBook));

        assertNotNull(aggregate.getId());
        assertEquals(1, messageList.size());
        assertEquals(ReservationAdded.class, messageList.get(0)
                                                        .getClass());

        final ReservationAdded reservationAdded = (ReservationAdded) messageList.get(0);

        assertEquals(InventoryCommandFactory.inventoryId, reservationAdded.getInventoryId());

        assertEquals("petr@gmail.com", reservationAdded.getForWhomReserved().getEmail().getValue());
    }
    @Test
    void reserveBook() {
        final ReserveBook reserveBook = InventoryCommandFactory.reserveBookInstance();
        dispatchCommand(aggregate, envelopeOf(reserveBook));

        final Inventory inventory = aggregate.getState();
        assertEquals("123456789", inventory.getReservations(0).getBookId().getIsbn62().getValue());
        assertEquals("petr@gmail.com", inventory.getReservations(0).getWhoReserved().getEmail().getValue());
    }
}
