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

import com.google.common.base.Function;
import com.google.common.base.Optional;
import io.spine.server.event.EventBus;
import io.spine.server.event.EventEnricher;
import javaclasses.exlibris.c.book.BookAggregate;
import javaclasses.exlibris.c.book.BookRepository;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Serves as class which adds enrichment fields to the {@link EventBus}.
 *
 * @author Yurii Haidamaka
 */
@SuppressWarnings("Guava") // Because com.google.common.base.Function is used
// until the migration of Spine to Java 8 is performed.
public class ExlibrisEnrichments {

    private final BookRepository bookRepo;

    private ExlibrisEnrichments(Builder builder) {
        this.bookRepo = builder.bookRepo;
    }

    EventEnricher createEnricher() {
        final EventEnricher enricher =
                EventEnricher.newBuilder()
                             .add(InventoryId.class, Book.class, inventoryIdToBook())
                             .build();
        return enricher;
    }

    private Function<InventoryId, Book> inventoryIdToBook() {
        final Function<InventoryId, Book> result = inventoryId -> {
            if (inventoryId == null) {
                return Book.getDefaultInstance();
            }
            final Optional<BookAggregate> aggregate = bookRepo.find(inventoryId.getBookId());
            if (!aggregate.isPresent()) {
                return Book.getDefaultInstance();
            }
            final Book state = aggregate.get()
                                        .getState();
            return state;
        };
        return result;
    }

    /**
     * Creates a new builder for (@code ExlibrisEnrichments).
     *
     * @return new builder instance
     */
    public static Builder newBuilder() {
        return new Builder();
    }

    /**
     * A builder for {@code EventEnricherSupplier} instances.
     */
    public static class Builder {

        private BookRepository bookRepo;

        private Builder() {
        }

        public Builder setBookRepository(BookRepository bookRepo) {
            checkNotNull(bookRepo);
            this.bookRepo = bookRepo;
            return this;
        }

        public ExlibrisEnrichments build() {
            return new ExlibrisEnrichments(this);
        }
    }
}
