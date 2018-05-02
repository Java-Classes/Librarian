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

package javaclasses.exlibris.testdata;

import io.spine.server.event.EventEnricher;
import javaclasses.exlibris.Book;
import javaclasses.exlibris.InventoryId;

import static javaclasses.exlibris.testdata.TestValues.INVENTORY_ID_TO_BOOK;

/**
 * Provides event enricher for the test needs.
 *
 * @author Illia Shepilov
 */
public class EventEnricherFactory {

    private EventEnricherFactory() {
    }

    /**
     * Provides a pre-configured {@link EventEnricher} event instance.
     *
     * @return {@code EventEnricher}
     */
    public static EventEnricher eventEnricherInstance() {
        final EventEnricher result = EventEnricher.newBuilder()
                                                  .add(InventoryId.class,
                                                       Book.class,
                                                       INVENTORY_ID_TO_BOOK::apply)
                                                  .build();
        return result;
    }
}
