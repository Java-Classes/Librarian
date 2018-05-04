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

package javaclasses.exlibris.c.procman;

import com.google.common.base.Optional;
import io.grpc.stub.StreamObserver;
import io.spine.client.TestActorRequestFactory;
import io.spine.core.Ack;
import io.spine.core.Command;
import io.spine.grpc.StreamObservers;
import io.spine.server.BoundedContext;
import io.spine.server.commandbus.CommandBus;
import io.spine.server.event.EventBus;
import io.spine.server.storage.StorageFactory;
import io.spine.server.storage.memory.InMemoryStorageFactory;
import javaclasses.exlibris.Inventory;
import javaclasses.exlibris.c.book.BookRepository;
import javaclasses.exlibris.c.inventory.InventoryAggregate;
import javaclasses.exlibris.c.inventory.InventoryRepository;
import javaclasses.exlibris.testdata.BookRejectionsSubscriber;
import javaclasses.exlibris.testdata.InventoryRejectionsSubscriber;

import static com.google.common.base.Preconditions.checkNotNull;
import static io.spine.protobuf.TypeConverter.toMessage;
import static io.spine.util.Exceptions.newIllegalStateException;
import static javaclasses.exlibris.testdata.BookCommandFactory.createBookInstance;
import static javaclasses.exlibris.testdata.InventoryCommandFactory.appendInventoryInstance;
import static javaclasses.exlibris.testdata.InventoryCommandFactory.borrowBookInstance;
import static javaclasses.exlibris.testdata.InventoryCommandFactory.cancelReservationInstance;
import static javaclasses.exlibris.testdata.InventoryCommandFactory.extendLoanPeriodInstance;
import static javaclasses.exlibris.testdata.InventoryCommandFactory.markReservationExpiredInstance;
import static javaclasses.exlibris.testdata.InventoryCommandFactory.reserveBookInstance;
import static javaclasses.exlibris.testdata.InventoryCommandFactory.returnBookInstance;
import static javaclasses.exlibris.testdata.TestValues.INVENTORY_ID;
import static javaclasses.exlibris.testdata.TestValues.INVENTORY_ITEM_ID_1;
import static javaclasses.exlibris.testdata.TestValues.INVENTORY_ITEM_ID_2;
import static javaclasses.exlibris.testdata.TestValues.USER_ID;
import static javaclasses.exlibris.testdata.TestValues.USER_ID_2;
import static javaclasses.exlibris.testdata.TestValues.USER_ID_3;

public class ProcessManagerTest {
    /** The name of the test {@code BoundedContext}. */
    private static final String NAME = "ExlibrisProcmanTestBoundedContext";

    private static final StorageFactory IN_MEMORY_FACTORY =
            InMemoryStorageFactory.newInstance(BoundedContext.newName(NAME), false);

    final TestActorRequestFactory requestFactory =
            TestActorRequestFactory.newInstance(getClass());

    final Command addBook = requestFactory.createCommand(toMessage(createBookInstance()));

    final Command appendInventoryItem1 =
            requestFactory.createCommand(toMessage(appendInventoryInstance()));

    final Command appendInventoryItem2 =
            requestFactory.createCommand(toMessage(appendInventoryInstance(INVENTORY_ID,
                                                                           INVENTORY_ITEM_ID_2,
                                                                           USER_ID)));
    final Command borrowBookUser1Item1 =
            requestFactory.createCommand(toMessage(borrowBookInstance()));

    final Command borrowBookUser2Item1 =
            requestFactory.createCommand(toMessage(borrowBookInstance(INVENTORY_ID,
                                                                      INVENTORY_ITEM_ID_1,
                                                                      USER_ID_2)));
    final Command borrowBookUser2Item2 =
            requestFactory.createCommand(toMessage(borrowBookInstance(INVENTORY_ID,
                                                                      INVENTORY_ITEM_ID_2,
                                                                      USER_ID_2)));
    final Command borrowBookUser3Item1 =
            requestFactory.createCommand(toMessage(borrowBookInstance(INVENTORY_ID,
                                                                      INVENTORY_ITEM_ID_1,
                                                                      USER_ID_3)));

    final Command returnBookUser1Item1 =
            requestFactory.createCommand(toMessage(returnBookInstance()));

    final Command returnBookUser2Item2 =
            requestFactory.createCommand(toMessage(returnBookInstance(INVENTORY_ID,
                                                                      INVENTORY_ITEM_ID_2,
                                                                      USER_ID_2)));
    final Command reserveBookUser2 =
            requestFactory.createCommand(toMessage(reserveBookInstance(USER_ID_2,
                                                                       INVENTORY_ID)));

    final Command reserveBookUser3 =
            requestFactory.createCommand(toMessage(reserveBookInstance(USER_ID_3,
                                                                       INVENTORY_ID)));

    final Command cancelReservationUser2 =
            requestFactory.createCommand(toMessage(cancelReservationInstance(INVENTORY_ID,
                                                                             USER_ID_2)));

    final Command expireReservationUser3 =
            requestFactory.createCommand(toMessage(markReservationExpiredInstance(INVENTORY_ID,
                                                                                  USER_ID_3)));

    final Command extendLoanPeriodUser1Item1 =
            requestFactory.createCommand(toMessage(extendLoanPeriodInstance()));

    protected BoundedContext boundedContext;
    protected CommandBus commandBus;
    protected StreamObserver<Ack> observer;
    protected BookRejectionsSubscriber bookRejectionsSubscriber;
    protected InventoryRejectionsSubscriber inventoryRejectionsSubscriber;

    public void setUp() {
        InventoryRepository.setNewInstance();
        boundedContext = createTestContext();
        commandBus = boundedContext.getCommandBus();
        observer = StreamObservers.noOpObserver();
        bookRejectionsSubscriber = new BookRejectionsSubscriber();
        inventoryRejectionsSubscriber = new InventoryRejectionsSubscriber();
        boundedContext.getRejectionBus()
                      .register(bookRejectionsSubscriber);
        boundedContext.getRejectionBus()
                      .register(inventoryRejectionsSubscriber);
        commandBus.post(addBook, observer);
    }

    /**
     * Creates a new instance of the {@link BoundedContext}
     * using the specified {@link StorageFactory}.
     *
     * @return the bounded context created with the storage factory
     */
    private static BoundedContext createTestContext() {

        final BookRepository bookRepository = new BookRepository();
        final InventoryRepository inventoryRepository = InventoryRepository.getRepository();
        final ReservationQueueProcmanRepository reservationQueueRepository = new ReservationQueueProcmanRepository();
        final LoansExtensionProcmanRepository loansExtensionRepository = new LoansExtensionProcmanRepository();

        final EventBus.Builder eventBus = createEventBus(IN_MEMORY_FACTORY);

        final BoundedContext boundedContext = createBoundedContext(eventBus);

        boundedContext.register(bookRepository);
        boundedContext.register(inventoryRepository);
        boundedContext.register(reservationQueueRepository);
        boundedContext.register(loansExtensionRepository);
        return boundedContext;
    }

    private static EventBus.Builder createEventBus(StorageFactory storageFactory) {
        final EventBus.Builder eventBus = EventBus.newBuilder()
                                                  .setStorageFactory(storageFactory);
        return eventBus;
    }

    private static BoundedContext createBoundedContext(EventBus.Builder eventBus) {
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

    protected Inventory getAggregateState() {
        final Optional<InventoryAggregate> aggregateOptional = InventoryRepository.getRepository()
                                                                                  .find(INVENTORY_ID);
        return aggregateOptional.isPresent() ? aggregateOptional.get()
                                                                .getState() : null;
    }
}
