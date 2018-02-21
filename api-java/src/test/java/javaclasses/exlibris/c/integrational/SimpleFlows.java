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

package javaclasses.exlibris.c.integrational;

import com.google.protobuf.Message;
import io.grpc.stub.StreamObserver;
import io.spine.client.TestActorRequestFactory;
import io.spine.core.Ack;
import io.spine.core.Command;
import io.spine.grpc.StreamObservers;
import io.spine.server.BoundedContext;
import io.spine.server.commandbus.CommandBus;
import javaclasses.exlibris.BookDetailsChange;
import javaclasses.exlibris.c.RemoveBook;
import javaclasses.exlibris.c.aggregate.InventoryCommandTest;
import javaclasses.exlibris.context.BoundedContexts;
import javaclasses.exlibris.testdata.BookCommandFactory;
import javaclasses.exlibris.testdata.BookRejectionsSubscriber;
import javaclasses.exlibris.testdata.InventoryCommandFactory;
import javaclasses.exlibris.testdata.InventoryRejectionsSubscriber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.spine.protobuf.TypeConverter.toMessage;
import static javaclasses.exlibris.testdata.BookCommandFactory.createBookInstance;
import static javaclasses.exlibris.testdata.BookCommandFactory.removeBookInstance;
import static javaclasses.exlibris.testdata.BookCommandFactory.updateBookInstance;
import static javaclasses.exlibris.testdata.InventoryCommandFactory.appendInventoryInstance;
import static javaclasses.exlibris.testdata.InventoryCommandFactory.borrowBookInstance;
import static javaclasses.exlibris.testdata.InventoryCommandFactory.extendLoanPeriodInstance;
import static javaclasses.exlibris.testdata.InventoryCommandFactory.reserveBookInstance;
import static javaclasses.exlibris.testdata.InventoryCommandFactory.returnBookInstance;
import static javaclasses.exlibris.testdata.InventoryCommandFactory.writeBookOffInstance;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Alexander Karpets
 * @author Paul Ageyev
 */
public class SimpleFlows extends InventoryCommandTest<Message> {

    private final TestActorRequestFactory requestFactory =
            TestActorRequestFactory.newInstance(getClass());

    private final BookDetailsChange newBookDetails = BookDetailsChange.newBuilder()
                                                                      .setNewBookDetails(
                                                                              BookCommandFactory.bookDetails2)
                                                                      .build();

    private final Command addBook = requestFactory.createCommand(toMessage(createBookInstance()));

    private final Command updateBook = requestFactory.createCommand(
            toMessage(updateBookInstance(BookCommandFactory.bookId,
                                         BookCommandFactory.userId2,
                                         newBookDetails)));
    private final Command appendInventory = requestFactory.createCommand(
            toMessage(appendInventoryInstance()));

    private final Command borrowBook = requestFactory.createCommand(
            toMessage(borrowBookInstance()));

    private final Command reserveBook = requestFactory.createCommand(
            toMessage(reserveBookInstance(InventoryCommandFactory.userId,
                                          InventoryCommandFactory.inventoryId)));

    private final Command reserveBook2 = requestFactory.createCommand(
            toMessage(reserveBookInstance(InventoryCommandFactory.userId2,
                                          InventoryCommandFactory.inventoryId)));

    private final Command returnBook = requestFactory.createCommand(
            toMessage(returnBookInstance()));

    private final Command borrowBook2 = requestFactory.createCommand(
            toMessage(borrowBookInstance(InventoryCommandFactory.inventoryId,
                                         InventoryCommandFactory.inventoryItemId,
                                         InventoryCommandFactory.userId2)));

    private final Command returnBook2 = requestFactory.createCommand(
            toMessage(returnBookInstance(InventoryCommandFactory.inventoryId,
                                         InventoryCommandFactory.inventoryItemId,
                                         InventoryCommandFactory.userId2)));
    private final Command writeBookOff = requestFactory.createCommand(
            toMessage(writeBookOffInstance()));

    private final Command removeBook = requestFactory.createCommand(
            toMessage(removeBookInstance(BookCommandFactory.bookId,
                                         BookCommandFactory.librarianId,
                                         RemoveBook.BookRemovalReasonCase.OUTDATED)));

    private final Command extendLoanPeriod = requestFactory.createCommand(
            toMessage(extendLoanPeriodInstance()));

    private final Command extendLoanPeriod2 = requestFactory.createCommand(
            toMessage(extendLoanPeriodInstance(InventoryCommandFactory.inventoryId,
                                               InventoryCommandFactory.loan.getLoanId(),
                                               InventoryCommandFactory.userId2)));

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
    }

    @Test
    @DisplayName("simple book lifecycle that doesn't throw a rejection")
    void useCase() {

        final BoundedContext boundedContext = BoundedContexts.create();
        final CommandBus commandBus = boundedContext.getCommandBus();
        final StreamObserver<Ack> observer = StreamObservers.noOpObserver();
        final BookRejectionsSubscriber bookRejectionsSubscriber = new BookRejectionsSubscriber();
        final InventoryRejectionsSubscriber inventoryRejectionsSubscriber = new InventoryRejectionsSubscriber();
        boundedContext.getRejectionBus()
                      .register(bookRejectionsSubscriber);
        boundedContext.getRejectionBus()
                      .register(inventoryRejectionsSubscriber);
        commandBus.post(addBook, observer);
        commandBus.post(updateBook, observer);
        commandBus.post(appendInventory, observer);
        commandBus.post(borrowBook, observer);
        commandBus.post(reserveBook2, observer);
        commandBus.post(returnBook, observer);
        commandBus.post(borrowBook2, observer);
        commandBus.post(returnBook2, observer);
        commandBus.post(writeBookOff, observer);
        commandBus.post(removeBook, observer);
        assertFalse(bookRejectionsSubscriber.wasCalled());
        assertFalse(inventoryRejectionsSubscriber.wasCalled());

    }

    @Test
    @DisplayName("second simple book lifecycle that doesn't throw a rejection")
    void secondUseCase() {

        final BoundedContext boundedContext = BoundedContexts.create();
        final CommandBus commandBus = boundedContext.getCommandBus();

        final StreamObserver<Ack> observer = StreamObservers.noOpObserver();

        final BookRejectionsSubscriber bookRejectionsSubscriber = new BookRejectionsSubscriber();
        final InventoryRejectionsSubscriber inventoryRejectionsSubscriber = new InventoryRejectionsSubscriber();

    }

    @Test
    @DisplayName("simple flow that throw rejections")
    void rejectionsThrow() {
        final BoundedContext boundedContext = BoundedContexts.create();
        final CommandBus commandBus = boundedContext.getCommandBus();
        final StreamObserver<Ack> observer = StreamObservers.noOpObserver();
        final BookRejectionsSubscriber bookRejectionsSubscriber = new BookRejectionsSubscriber();
        final InventoryRejectionsSubscriber inventoryRejectionsSubscriber = new InventoryRejectionsSubscriber();
        boundedContext.getRejectionBus()
                      .register(bookRejectionsSubscriber);
        boundedContext.getRejectionBus()
                      .register(inventoryRejectionsSubscriber);

        commandBus.post(borrowBook, observer);
        assertTrue(InventoryRejectionsSubscriber.wasCalled());
        InventoryRejectionsSubscriber.clear();

        commandBus.post(updateBook, observer);
        assertTrue(BookRejectionsSubscriber.wasCalled());
        BookRejectionsSubscriber.clear();

        commandBus.post(addBook, observer);
        assertFalse(BookRejectionsSubscriber.wasCalled());

        commandBus.post(returnBook, observer);
        assertTrue(InventoryRejectionsSubscriber.wasCalled());
        InventoryRejectionsSubscriber.clear();

        commandBus.post(appendInventory, observer);
        assertFalse(InventoryRejectionsSubscriber.wasCalled());

        commandBus.post(reserveBook, observer);
        assertFalse(InventoryRejectionsSubscriber.wasCalled());

        commandBus.post(reserveBook, observer);
        assertTrue(InventoryRejectionsSubscriber.wasCalled());
        InventoryRejectionsSubscriber.clear();

        commandBus.post(returnBook, observer);
        assertTrue(InventoryRejectionsSubscriber.wasCalled());
        InventoryRejectionsSubscriber.clear();

        commandBus.post(borrowBook, observer);
        assertFalse(InventoryRejectionsSubscriber.wasCalled());

        commandBus.post(borrowBook, observer);
        assertTrue(InventoryRejectionsSubscriber.wasCalled());
        InventoryRejectionsSubscriber.clear();
    }

    @Test
    @DisplayName("simple loan flows")
    void loanFlow() {

        final BoundedContext boundedContext = BoundedContexts.create();
        final CommandBus commandBus = boundedContext.getCommandBus();
        final StreamObserver<Ack> observer = StreamObservers.noOpObserver();
        final InventoryRejectionsSubscriber inventoryRejectionsSubscriber = new InventoryRejectionsSubscriber();

        boundedContext.getRejectionBus()
                      .register(inventoryRejectionsSubscriber);
        commandBus.post(addBook, observer);
        commandBus.post(appendInventory, observer);
        commandBus.post(borrowBook, observer);
        commandBus.post(extendLoanPeriod, observer);
        commandBus.post(extendLoanPeriod, observer);
        commandBus.post(returnBook, observer);
        commandBus.post(borrowBook2, observer);
        commandBus.post(extendLoanPeriod2, observer);
        commandBus.post(extendLoanPeriod2, observer);
        commandBus.post(returnBook2, observer);
        assertFalse(inventoryRejectionsSubscriber.wasCalled());
    }
}
