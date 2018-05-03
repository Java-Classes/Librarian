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

import com.google.protobuf.Message;
import io.grpc.stub.StreamObserver;
import io.spine.client.TestActorRequestFactory;
import io.spine.core.Ack;
import io.spine.core.Command;
import io.spine.grpc.StreamObservers;
import io.spine.server.BoundedContext;
import io.spine.server.commandbus.CommandBus;
import javaclasses.exlibris.BoundedContexts;
import javaclasses.exlibris.c.inventory.InventoryCommandTest;
import javaclasses.exlibris.c.inventory.InventoryRepository;
import javaclasses.exlibris.testdata.BookRejectionsSubscriber;
import javaclasses.exlibris.testdata.InventoryRejectionsSubscriber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.spine.protobuf.TypeConverter.toMessage;
import static javaclasses.exlibris.testdata.BookCommandFactory.createBookInstance;
import static javaclasses.exlibris.testdata.InventoryCommandFactory.appendInventoryInstance;
import static javaclasses.exlibris.testdata.InventoryCommandFactory.borrowBookInstance;
import static javaclasses.exlibris.testdata.InventoryCommandFactory.cancelReservationInstance;
import static javaclasses.exlibris.testdata.InventoryCommandFactory.inventoryId;
import static javaclasses.exlibris.testdata.InventoryCommandFactory.inventoryItemId;
import static javaclasses.exlibris.testdata.InventoryCommandFactory.markReservationExpiredInstance;
import static javaclasses.exlibris.testdata.InventoryCommandFactory.reserveBookInstance;
import static javaclasses.exlibris.testdata.InventoryCommandFactory.returnBookInstance;
import static javaclasses.exlibris.testdata.InventoryCommandFactory.userId2;
import static javaclasses.exlibris.testdata.InventoryCommandFactory.userId3;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("ReservationQueueProcman should")
public class ReservationQueueProcmanTest extends InventoryCommandTest<Message> {
    private final TestActorRequestFactory requestFactory =
            TestActorRequestFactory.newInstance(getClass());

    private final Command addBook = requestFactory.createCommand(toMessage(createBookInstance()));

    private final Command appendInventoryItem1 =
            requestFactory.createCommand(toMessage(appendInventoryInstance()));

    private final Command borrowBookUser1Item1 =
            requestFactory.createCommand(toMessage(borrowBookInstance()));

    private final Command borrowBookUser2Item1 =
            requestFactory.createCommand(toMessage(borrowBookInstance(inventoryId,
                                                                      inventoryItemId,
                                                                      userId2)));

    private final Command borrowBookUser3Item1 =
            requestFactory.createCommand(toMessage(borrowBookInstance(inventoryId,
                                                                      inventoryItemId,
                                                                      userId3)));

    private final Command returnBookUser1Item1 =
            requestFactory.createCommand(toMessage(returnBookInstance()));

    private final Command reserveBookUser2 =
            requestFactory.createCommand(toMessage(reserveBookInstance(userId2,
                                                                       inventoryId)));

    private final Command reserveBookUser3 =
            requestFactory.createCommand(toMessage(reserveBookInstance(userId3,
                                                                       inventoryId)));

    private final Command cancelReservationUser2 =
            requestFactory.createCommand(toMessage(cancelReservationInstance(inventoryId,
                                                                             userId2)));

    private final Command expireReservationUser3 =
            requestFactory.createCommand(toMessage(markReservationExpiredInstance(inventoryId,
                                                                                  userId3)));

    private BoundedContext boundedContext;
    private CommandBus commandBus;
    private StreamObserver<Ack> observer;
    private BookRejectionsSubscriber bookRejectionsSubscriber;
    private InventoryRejectionsSubscriber inventoryRejectionsSubscriber;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
        InventoryRepository.setNewInstance();
        boundedContext = BoundedContexts.create();
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

    @Test
    @DisplayName("react on book returned event and make book available for borrowing.")
    void reactOnBookReturnedMakesAvailable() {
        commandBus.post(appendInventoryItem1, observer);
        commandBus.post(borrowBookUser1Item1, observer);
        commandBus.post(returnBookUser1Item1, observer);
        commandBus.post(borrowBookUser1Item1, observer);

        assertFalse(inventoryRejectionsSubscriber.wasCalled());
    }

    @Test
    @DisplayName("react on book returned event and make book ready to pick up for next in reservation queue.")
    void reactOnBookReturnedMakesReadyToPickUp() {
        commandBus.post(appendInventoryItem1, observer);
        commandBus.post(borrowBookUser1Item1, observer);
        commandBus.post(reserveBookUser2, observer);
        commandBus.post(returnBookUser1Item1, observer);
        commandBus.post(borrowBookUser1Item1, observer);

        assertTrue(inventoryRejectionsSubscriber.wasCalled());
    }

    @Test
    @DisplayName("react on reservation canceled event and make book available for borrowing.")
    void reactOnCancelReservationMakesBookAvailable() {
        commandBus.post(appendInventoryItem1, observer);
        commandBus.post(borrowBookUser1Item1, observer);
        commandBus.post(reserveBookUser2, observer);
        commandBus.post(returnBookUser1Item1, observer);
        commandBus.post(cancelReservationUser2, observer);
        commandBus.post(borrowBookUser1Item1, observer);

        assertFalse(inventoryRejectionsSubscriber.wasCalled());
    }

    @Test
    @DisplayName("react on reservation canceled event and make book ready to pick up for next in reservation queue.")
    void reactOnCancelReservationMakesReadyToPickUp() {
        commandBus.post(reserveBookUser2, observer);
        commandBus.post(reserveBookUser3, observer);
        commandBus.post(appendInventoryItem1, observer);
        commandBus.post(cancelReservationUser2, observer);
        commandBus.post(borrowBookUser3Item1, observer);

        assertFalse(inventoryRejectionsSubscriber.wasCalled());
    }

    @Test
    @DisplayName("react on reservation pickup period expired event and make book available for borrowing.")
    void reactOnReservationExpiredMakesBookAvailable() {
        commandBus.post(reserveBookUser3, observer);
        commandBus.post(appendInventoryItem1, observer);
        commandBus.post(expireReservationUser3, observer);
        commandBus.post(reserveBookUser2, observer);

        assertTrue(inventoryRejectionsSubscriber.wasCalled());
    }

    @Test
    @DisplayName("react on reservation pickup period expired event and make book ready to pick up for next in reservation queue.")
    void reactOnReservationExpiredMakesReadyToPickUp() {
        commandBus.post(reserveBookUser3, observer);
        commandBus.post(appendInventoryItem1, observer);
        commandBus.post(reserveBookUser2, observer);
        commandBus.post(expireReservationUser3, observer);
        commandBus.post(borrowBookUser1Item1, observer);

        assertTrue(inventoryRejectionsSubscriber.wasCalled());
        inventoryRejectionsSubscriber.clear();

        commandBus.post(borrowBookUser2Item1, observer);
        assertFalse(inventoryRejectionsSubscriber.wasCalled());
    }
}
