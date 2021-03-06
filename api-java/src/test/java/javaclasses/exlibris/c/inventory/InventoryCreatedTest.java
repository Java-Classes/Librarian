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

import com.google.common.base.Optional;
import io.spine.core.Event;
import io.spine.server.BoundedContext;
import io.spine.server.command.TestEventFactory;
import io.spine.server.entity.Repository;
import javaclasses.exlibris.BoundedContexts;
import javaclasses.exlibris.Inventory;
import javaclasses.exlibris.InventoryId;
import javaclasses.exlibris.c.BookAdded;
import javaclasses.exlibris.c.book.BookAggregate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.spine.protobuf.AnyPacker.pack;
import static io.spine.protobuf.AnyPacker.unpack;
import static io.spine.server.command.TestEventFactory.newInstance;
import static io.spine.test.Tests.assertHasPrivateParameterlessCtor;
import static javaclasses.exlibris.BoundedContexts.create;
import static javaclasses.exlibris.testdata.TestValues.BOOK_DETAILS;
import static javaclasses.exlibris.testdata.TestValues.BOOK_ID;
import static javaclasses.exlibris.testdata.TestValues.DEFAULT_TIMESTAMP1;
import static javaclasses.exlibris.testdata.TestValues.LIBRARIAN_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Dmytry Dyachenko
 */
@DisplayName("InventoryCreated event should be react on BookAdded and")
public class InventoryCreatedTest {

    private static Event bookAdded() {
        final TestEventFactory eventFactory = newInstance(pack(BOOK_ID),
                                                          BookAggregate.class);
        return eventFactory.createEvent(BookAdded.newBuilder()
                                                 .setBookId(BOOK_ID)
                                                 .setDetails(BOOK_DETAILS)
                                                 .setLibrarianId(LIBRARIAN_ID)
                                                 .setWhenAdded(DEFAULT_TIMESTAMP1)
                                                 .build()
        );
    }

    @Test
    @DisplayName("create InventoryAggregate")
    void produceEvent() {
        final BoundedContext sourceContext = create();

        final Event event = bookAdded();
        sourceContext.getEventBus()
                     .post(event);

        final BookAdded bookAdded = unpack(event.getMessage());

        final Optional<Repository> repository = sourceContext.findRepository(Inventory.class);

        final InventoryId inventoryId = InventoryId.newBuilder()
                                                   .setBookId(bookAdded.getBookId())
                                                   .build();
        final Optional<InventoryAggregate> optional = repository.get()
                                                                .find(inventoryId);
        assertNotNull(optional);

        assertEquals(optional.get()
                             .getState()
                             .getInventoryId()
                             .getBookId(), bookAdded.getBookId());
    }

    @Test
    @DisplayName("has the private parameterless constructor")
    void hasPrivateCtor() {
        assertHasPrivateParameterlessCtor(BoundedContexts.class);
    }
}
