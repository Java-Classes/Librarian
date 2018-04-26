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

package javaclasses.exlibris;

import com.google.common.base.Optional;
import com.google.protobuf.Any;
import com.google.protobuf.Message;
import io.spine.core.Enrichment;
import io.spine.core.Event;
import io.spine.core.EventEnvelope;
import io.spine.core.Versions;
import io.spine.server.event.EventEnricher;
import io.spine.server.event.EventFactory;
import io.spine.type.TypeName;
import javaclasses.exlibris.c.BookBorrowed;
import javaclasses.exlibris.c.BookEnrichment;
import javaclasses.exlibris.c.book.BookRepository;
import javaclasses.exlibris.c.inventory.InventoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.spine.core.EventEnvelope.of;
import static io.spine.protobuf.AnyPacker.unpack;
import static io.spine.server.command.TestEventFactory.newInstance;
import static io.spine.validate.Validate.isDefault;
import static javaclasses.exlibris.testdata.InventoryEventFactory.bookBorrowedInstance;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("ExlibrisEnrichmentsTest should")
class ExlibrisEnrichmentsTest {

    private static final EventFactory events = newInstance(ExlibrisEnrichmentsTest.class);

    private EventEnricher enricher;

    @BeforeEach
    void setUp() {
        final BookRepository bookRepo = mock(BookRepository.class);
        final InventoryRepository inventoryRepo = mock(InventoryRepository.class);
        when(bookRepo.find(any(BookId.class))).thenReturn(Optional.absent());
        when(inventoryRepo.find(any(InventoryId.class))).thenReturn(Optional.absent());
        enricher = ExlibrisEnrichments.newBuilder()
                                      .setBookRepository(bookRepo)
                                      .build()
                                      .createEnricher();
    }

    @Test
    @DisplayName("create EventEnricher that defaults absent Book to default message")
    void enricherDefaultsTest() {
        final BookBorrowed eventMsg = bookBorrowedInstance();
        final EventEnvelope envelope = enricher.enrich(of(event(eventMsg)));
        final EventEnvelope enriched = enricher.enrich(envelope);
        final Enrichment enrichment = enriched.getEnrichment();

        final TypeName labelsEnrName = TypeName.from(BookEnrichment.getDescriptor());
        final Any labelIds = enrichment.getContainer()
                                       .getItemsMap()
                                       .get(labelsEnrName.value());
        final BookEnrichment bookEnr = unpack(labelIds);
        assertTrue(isDefault(bookEnr.getBook()));
    }

    private static Event event(Message msg) {
        return events.createEvent(msg, Versions.zero());
    }
}

