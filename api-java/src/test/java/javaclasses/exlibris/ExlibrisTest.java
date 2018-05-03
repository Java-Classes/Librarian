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
import com.google.protobuf.GeneratedMessageV3;
import io.spine.client.ActorRequestFactory;
import io.spine.client.TestActorRequestFactory;
import io.spine.core.Command;
import io.spine.grpc.StreamObservers;
import io.spine.server.BoundedContext;
import io.spine.server.commandbus.CommandBus;
import io.spine.server.entity.Repository;
import javaclasses.exlibris.c.inventory.InventoryRepository;
import javaclasses.exlibris.q.BookStatus;
import javaclasses.exlibris.q.BookView;
import javaclasses.exlibris.q.BorrowedBookItem;
import javaclasses.exlibris.q.BorrowedBookItemStatus;
import javaclasses.exlibris.q.BorrowedBooksListView;
import javaclasses.exlibris.q.user.BookViewProjection;
import javaclasses.exlibris.q.user.BorrowedBooksListViewProjection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static io.spine.protobuf.TypeConverter.toMessage;
import static javaclasses.exlibris.testdata.BookCommandFactory.createBookInstance;
import static javaclasses.exlibris.testdata.InventoryCommandFactory.appendInventoryInstance;
import static javaclasses.exlibris.testdata.InventoryCommandFactory.borrowBookInstance;
import static javaclasses.exlibris.testdata.TestValues.AUTHOR_NAME;
import static javaclasses.exlibris.testdata.TestValues.BOOK_CATEGORY;
import static javaclasses.exlibris.testdata.TestValues.BOOK_ID;
import static javaclasses.exlibris.testdata.TestValues.BOOK_SYNOPSIS;
import static javaclasses.exlibris.testdata.TestValues.BOOK_TITLE;
import static javaclasses.exlibris.testdata.TestValues.COVER_URL;
import static javaclasses.exlibris.testdata.TestValues.ISBN;
import static javaclasses.exlibris.testdata.TestValues.USER_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Exlibris Integration Test")
public class ExlibrisTest {

    private final ActorRequestFactory requestFactory =
            TestActorRequestFactory.newInstance(getClass());

    private final BoundedContext boundedContext = BoundedContexts.create();
    private final CommandBus commandBus = boundedContext.getCommandBus();

    private Command createCommand(GeneratedMessageV3 message) {
        return getRequestFactory().command()
                                  .create(toMessage(message));
    }

    private ActorRequestFactory getRequestFactory() {
        return requestFactory;
    }

    @Nested
    @DisplayName("Add Book -> Append inventory -> Borrow Book")
    class FirstFlow {

        @BeforeEach
        public void setUp() {
            final Command addBook = createCommand(createBookInstance());
            commandBus.post(addBook, StreamObservers.noOpObserver());

            final Command appendInventory = createCommand(appendInventoryInstance());
            commandBus.post(appendInventory, StreamObservers.noOpObserver());

            final Command borrowBook = createCommand(borrowBookInstance());
            commandBus.post(borrowBook, StreamObservers.noOpObserver());

            InventoryRepository.setNewInstance();
        }

        @Test
        @DisplayName("update information in the BookViewProjection")
        void updateBookViewProjection() {
            final Optional<Repository> bookViewRepoOpt = boundedContext.findRepository(
                    BookView.class);
            assertTrue(bookViewRepoOpt.isPresent());
            final BookViewProjection bookViewProjection = (BookViewProjection) bookViewRepoOpt.get()
                                                                                              .find(BOOK_ID)
                                                                                              .get();
            final BookView bookViewstate = bookViewProjection.getState();

            assertEquals(ISBN, bookViewstate.getIsbn());
            assertEquals(BOOK_TITLE, bookViewstate.getTitle());
            assertEquals(AUTHOR_NAME, bookViewstate.getAuthors());
            assertEquals(COVER_URL, bookViewstate.getCoverUrl());
            assertEquals(BOOK_CATEGORY, bookViewstate.getCategories(0));
            assertEquals(BOOK_SYNOPSIS, bookViewstate.getSynopsis());
            assertEquals(0, bookViewstate.getAvailableCount());
            assertEquals(BookStatus.EXPECTED, bookViewstate.getStatus());
        }

        @Test
        @DisplayName("update information in the BorrowedBooksListViewProjection")
        void updateBorrowedBooksListViewProjection() {
            final Optional<Repository> borrowebBookksListViewOtp = boundedContext.findRepository(
                    BorrowedBooksListView.class);
            assertTrue(borrowebBookksListViewOtp.isPresent());
            final BorrowedBooksListViewProjection borrowedBooksListViewProjection =
                    (BorrowedBooksListViewProjection) borrowebBookksListViewOtp.get()
                                                                               .find(USER_ID)
                                                                               .get();
            final BorrowedBooksListView state = borrowedBooksListViewProjection.getState();

            final BorrowedBookItem bookItem = state.getBookItem(0);
            assertEquals(BOOK_ID, bookItem.getBookId());
            assertEquals(ISBN, bookItem.getIsbn());
            assertEquals(BOOK_TITLE, bookItem.getTitle());
            assertEquals(AUTHOR_NAME, bookItem.getAuthors());
            assertEquals(COVER_URL, bookItem.getCoverUrl());
            assertEquals(BOOK_CATEGORY, bookItem.getCategoriesList()
                                                .get(0));
            assertEquals(BOOK_SYNOPSIS, bookItem.getSynopsis());
            assertEquals(false, bookItem.getIsAllowedLoanExtension());
            assertEquals(BorrowedBookItemStatus.BORROWED, bookItem.getStatus());
            assertEquals(BorrowedBookItemStatus.BORROWED, bookItem.getStatus());
            assertEquals(1, state.getNumberOfBorrowedBooks());
            assertEquals(0, state.getNumberOfOverdueBooks());
        }
    }
}
