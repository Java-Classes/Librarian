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
import io.spine.client.TestActorRequestFactory;
import io.spine.core.CommandEnvelope;
import io.spine.server.aggregate.AggregateCommandTest;
import javaclasses.exlibris.BookId;
import javaclasses.exlibris.InventoryId;
import javaclasses.exlibris.Isbn62;
import javaclasses.exlibris.c.aggregate.InventoryAggregate;

/**
 * @author Alexander Karpets
 */
public class InventoryCommandTest<C extends Message> extends AggregateCommandTest<C, InventoryAggregate> {
    private final TestActorRequestFactory requestFactory =
            TestActorRequestFactory.newInstance(getClass());

    InventoryAggregate aggregate;
    InventoryId inventoryId;

    private static InventoryId createBookId() {

        return InventoryId.newBuilder()
                          .setBookId(BookId.newBuilder()
                                           .setIsbn62(Isbn62.newBuilder()
                                                            .setValue("123456789")))
                          .build();
    }

    @Override
    protected InventoryAggregate createAggregate() {
        inventoryId = createBookId();
        return new InventoryAggregate(inventoryId);
    }

    @Override
    protected void setUp() {
        super.setUp();
        aggregate = aggregate().get();
    }

    CommandEnvelope envelopeOf(Message commandMessage) {
        return CommandEnvelope.of(requestFactory.command()
                                                .create(commandMessage));
    }
}