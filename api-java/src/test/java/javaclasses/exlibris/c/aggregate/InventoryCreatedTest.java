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

import com.google.common.base.Optional;
import io.spine.core.Event;
import io.spine.server.BoundedContext;
import io.spine.server.command.TestEventFactory;
import io.spine.server.entity.Repository;
import javaclasses.exlibris.Inventory;
import javaclasses.exlibris.InventoryId;
import javaclasses.exlibris.c.AddBook;
import javaclasses.exlibris.c.BookAdded;
import javaclasses.exlibris.context.BoundedContexts;
import javaclasses.exlibris.testdata.BookCommandFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.spine.protobuf.AnyPacker.pack;
import static io.spine.protobuf.AnyPacker.unpack;
import static io.spine.server.command.TestEventFactory.newInstance;
import static io.spine.test.Tests.assertHasPrivateParameterlessCtor;
import static javaclasses.exlibris.context.BoundedContexts.create;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Dmytry Dyachenko
 */
@DisplayName("InventoryCreated event should be react on BookAdded and")
public class InventoryCreatedTest extends InventoryCommandTest<AddBook> {

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
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

        final Optional<InventoryAggregate> optional = repository.get()
                                                                .find(InventoryId.newBuilder()
                                                                                 .setBookId(
                                                                                         bookAdded.getBookId())
                                                                                 .build());

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

    private static Event bookAdded() {

        final TestEventFactory eventFactory = newInstance(pack(BookCommandFactory.bookId),
                                                          BookAggregate.class);
        return eventFactory.createEvent(BookAdded.newBuilder()
                                                 .setBookId(BookCommandFactory.bookId)
                                                 .build()
        );
    }
}
