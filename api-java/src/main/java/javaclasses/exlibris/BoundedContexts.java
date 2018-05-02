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

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import io.spine.server.BoundedContext;
import io.spine.server.event.EventBus;
import io.spine.server.event.EventEnricher;
import io.spine.server.storage.StorageFactory;
import io.spine.server.storage.memory.InMemoryStorageFactory;
import javaclasses.exlibris.c.book.BookRepository;
import javaclasses.exlibris.c.inventory.InventoryRepository;
import javaclasses.exlibris.q.user.BookViewRepository;
import javaclasses.exlibris.q.user.BorrowedBooksListViewRepository;
import javaclasses.exlibris.q.user.ExpectedSoonBooksListViewRepository;
import javaclasses.exlibris.q.user.ReservedBooksListViewRepository;
import javaclasses.exlibris.c.procman.ReservationQueueRepository;

import static com.google.common.base.Preconditions.checkNotNull;
import static io.spine.util.Exceptions.newIllegalStateException;

/**
 * Utilities for creation the {@link BoundedContext} instances.
 *
 * @author Alexander Karpets
 */
public final class BoundedContexts {

    /** The default name of the {@code BoundedContext}. */
    private static final String NAME = "ExlibrisBoundedContext";

    private static final StorageFactory IN_MEMORY_FACTORY =
            InMemoryStorageFactory.newInstance(BoundedContext.newName(NAME), false);

    private BoundedContexts() {
        // Disable instantiation from outside.
    }

    /**
     * Creates the {@link BoundedContext} instance
     * using {@code InMemoryStorageFactory} for a single tenant.
     *
     * @return the {@link BoundedContext} instance
     */
    public static BoundedContext create() {
        final BoundedContext result = create(IN_MEMORY_FACTORY);
        return result;
    }

    /**
     * Creates a new instance of the {@link BoundedContext}
     * using the specified {@link StorageFactory}.
     *
     * @param storageFactory the storage factory to use
     * @return the bounded context created with the storage factory
     */
    public static BoundedContext create(StorageFactory storageFactory) {
        checkNotNull(storageFactory);

        final BookRepository bookRepository = new BookRepository();
        final InventoryRepository inventoryRepository = InventoryRepository.getRepository();
        final ReservationQueueRepository reservationQueueRepository = new ReservationQueueRepository();

        final BookViewRepository allBooksRepo = new BookViewRepository();
        final ExpectedSoonBooksListViewRepository expectedSoonRepo = new ExpectedSoonBooksListViewRepository();
        final BorrowedBooksListViewRepository borrowedRepo = new BorrowedBooksListViewRepository();
        final ReservedBooksListViewRepository reservedRepo = new ReservedBooksListViewRepository();

        final EventBus.Builder eventBus = createEventBus(storageFactory, bookRepository);

        final BoundedContext boundedContext = createBoundedContext(eventBus);
        boundedContext.getEventBus()
                      .register(reservationQueueRepository);

        boundedContext.register(bookRepository);
        boundedContext.register(inventoryRepository);
        boundedContext.register(reservationQueueRepository);

        boundedContext.register(allBooksRepo);
        boundedContext.register(expectedSoonRepo);
        boundedContext.register(borrowedRepo);
        boundedContext.register(reservedRepo);
        return boundedContext;
    }

    private static EventBus.Builder createEventBus(StorageFactory storageFactory,
                                                   BookRepository bookRepo) {
        final EventEnricher enricher = ExlibrisEnrichments.newBuilder()
                                                          .setBookRepository(bookRepo)
                                                          .build()
                                                          .createEnricher();
        final EventBus.Builder eventBus = EventBus.newBuilder()
                                                  .setEnricher(enricher)
                                                  .setStorageFactory(storageFactory);
        return eventBus;
    }

    @VisibleForTesting
    static BoundedContext createBoundedContext(EventBus.Builder eventBus) {
        checkNotNull(eventBus);

        final Optional<StorageFactory> storageFactory = eventBus.getStorageFactory();

        if (!storageFactory.isPresent()) {
            throw newIllegalStateException("EventBus does not specify a StorageFactory.");
        }

        return BoundedContext.newBuilder()
                             .setStorageFactorySupplier(storageFactory::get)
                             .setName(NAME)
                             .setEventBus(eventBus)
                             .build();
    }
}
