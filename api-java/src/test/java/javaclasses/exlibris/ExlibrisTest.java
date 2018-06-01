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
import javaclasses.exlibris.c.inventory.InventoryAggregate;
import javaclasses.exlibris.c.inventory.InventoryRepository;
import javaclasses.exlibris.q.BookDetailsView;
import javaclasses.exlibris.q.BookInventoryView;
import javaclasses.exlibris.q.BookReservationView;
import javaclasses.exlibris.q.BookStatus;
import javaclasses.exlibris.q.BookView;
import javaclasses.exlibris.q.BorrowedBookItem;
import javaclasses.exlibris.q.BorrowedBookItemStatus;
import javaclasses.exlibris.q.BorrowedBooksListView;
import javaclasses.exlibris.q.InventoryItemState;
import javaclasses.exlibris.q.LostBookView;
import javaclasses.exlibris.q.ReaderLoanView;
import javaclasses.exlibris.q.ReservedBookItem;
import javaclasses.exlibris.q.ReservedBookItemStatus;
import javaclasses.exlibris.q.ReservedBooksListView;
import javaclasses.exlibris.q.admin.BookDetailsViewProjection;
import javaclasses.exlibris.q.admin.BookInventoryViewProjection;
import javaclasses.exlibris.q.admin.BookReservationViewProjection;
import javaclasses.exlibris.q.admin.ReaderLoanViewProjection;
import javaclasses.exlibris.q.user.BookViewProjection;
import javaclasses.exlibris.q.user.BorrowedBooksListViewProjection;
import javaclasses.exlibris.q.user.ReservedBooksListViewProjection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static io.spine.protobuf.TypeConverter.toMessage;
import static javaclasses.exlibris.testdata.BookCommandFactory.createBookInstance;
import static javaclasses.exlibris.testdata.InventoryCommandFactory.appendInventoryInstance;
import static javaclasses.exlibris.testdata.InventoryCommandFactory.borrowBookInstance;
import static javaclasses.exlibris.testdata.InventoryCommandFactory.extendLoanPeriodInstance;
import static javaclasses.exlibris.testdata.InventoryCommandFactory.markLoanOverdue;
import static javaclasses.exlibris.testdata.InventoryCommandFactory.markLoanShouldReturnSoon;
import static javaclasses.exlibris.testdata.InventoryCommandFactory.reportLostBookInstance;
import static javaclasses.exlibris.testdata.InventoryCommandFactory.reserveBookInstance;
import static javaclasses.exlibris.testdata.InventoryCommandFactory.writeBookOffInstance;
import static javaclasses.exlibris.testdata.TestValues.AUTHOR;
import static javaclasses.exlibris.testdata.TestValues.BOOK_CATEGORY;
import static javaclasses.exlibris.testdata.TestValues.BOOK_DETAILS;
import static javaclasses.exlibris.testdata.TestValues.BOOK_ID;
import static javaclasses.exlibris.testdata.TestValues.BOOK_SYNOPSIS;
import static javaclasses.exlibris.testdata.TestValues.BOOK_TITLE;
import static javaclasses.exlibris.testdata.TestValues.BORROWED_ITEM_STATE_TODAY;
import static javaclasses.exlibris.testdata.TestValues.BORROWED_ITEM_STATE_TODAY2;
import static javaclasses.exlibris.testdata.TestValues.COVER_URL;
import static javaclasses.exlibris.testdata.TestValues.CURRENT_DATE;
import static javaclasses.exlibris.testdata.TestValues.INVENTORY_ID;
import static javaclasses.exlibris.testdata.TestValues.INVENTORY_ITEM_ID_1;
import static javaclasses.exlibris.testdata.TestValues.ISBN;
import static javaclasses.exlibris.testdata.TestValues.USER_EMAIL_2;
import static javaclasses.exlibris.testdata.TestValues.USER_ID;
import static javaclasses.exlibris.testdata.TestValues.USER_ID_2;
import static javaclasses.exlibris.testdata.TestValues.USER_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Exlibris Integration Test")
public class ExlibrisTest {

    private final ActorRequestFactory requestFactory =
            TestActorRequestFactory.newInstance(getClass());

    private BoundedContext boundedContext;
    private CommandBus commandBus;

    @Nested
    @DisplayName("Add Book -> Append inventory -> Borrow Book")
    class FirstFlow {

        @BeforeEach
        public void setUp() {
            InventoryRepository.setNewInstance();

            boundedContext = BoundedContexts.create();
            commandBus = boundedContext.getCommandBus();

            final Command addBook = createCommand(createBookInstance());
            commandBus.post(addBook, StreamObservers.noOpObserver());

            final Command appendInventory = createCommand(appendInventoryInstance());
            commandBus.post(appendInventory, StreamObservers.noOpObserver());

            final Command borrowBook = createCommand(borrowBookInstance());
            commandBus.post(borrowBook, StreamObservers.noOpObserver());
        }

        @Test
        @DisplayName("update information in the BookViewProjection")
        void updateBookViewProjection() {
            testBookViewAfterBorrowing();
        }

        @Test
        @DisplayName("update information in the BorrowedBooksListViewProjection")
        void updateBorrowedBooksListViewProjection() {
            testBorrowedBooksListView();
        }

        @Test
        @DisplayName("update information in the BookDetailsViewProjection")
        void updateBookDetailsViewProjection() {
            final Optional<Repository> repository = boundedContext.findRepository(
                    BookDetailsView.class);
            assertTrue(repository.isPresent());
            final BookDetailsViewProjection projection =
                    (BookDetailsViewProjection) repository.get()
                                                          .find(BOOK_ID)
                                                          .get();
            final BookDetailsView state = projection.getState();

            final BookDetails bookDetails = state.getBookDetails();
            assertEquals(BOOK_DETAILS, bookDetails);
        }

        @Test
        @DisplayName("update information in the BookInventoryViewProjection")
        void updateBookInventoryViewProjection() {
            testBookInventoryViewByItemState(BORROWED_ITEM_STATE_TODAY);
        }

        @Test
        @DisplayName("update information in the ReaderLoanViewProjection")
        void updateReaderLoanViewProjection() {
            testReaderLoanView();
        }
    }

    @Nested
    @DisplayName("Add Book -> Append inventory -> Borrow Book -> Mark Loan Overdue -> Report Lost Book -> Write book off")
    class SecondFlow {

        @BeforeEach
        public void setUp() {
            InventoryRepository.setNewInstance();

            boundedContext = BoundedContexts.create();
            commandBus = boundedContext.getCommandBus();

            final Command addBook = createCommand(createBookInstance());
            commandBus.post(addBook, StreamObservers.noOpObserver());

            final Command appendInventory = createCommand(appendInventoryInstance());
            commandBus.post(appendInventory, StreamObservers.noOpObserver());

            final Command borrowBook = createCommand(borrowBookInstance());
            commandBus.post(borrowBook, StreamObservers.noOpObserver());

            final LoanId loanId = getLoanId();

            final Command markLoanOverdue = createCommand(
                    markLoanOverdue(loanId, INVENTORY_ID));
            commandBus.post(markLoanOverdue, StreamObservers.noOpObserver());

            final Command reportLostBook = createCommand(
                    reportLostBookInstance());
            commandBus.post(reportLostBook, StreamObservers.noOpObserver());

            final Command writeBookOff = createCommand(
                    writeBookOffInstance());
            commandBus.post(writeBookOff, StreamObservers.noOpObserver());
        }

        @Test
        @DisplayName("update information in the BookViewProjection")
        void updateBookViewProjection() {
            testBookViewAfterBorrowing();
        }

        @Test
        @DisplayName("delete book item from the BorrowedBooksListViewProjection")
        void updateBorrowedBooksListViewProjection() {
            final Optional<Repository> repository = boundedContext.findRepository(
                    BorrowedBooksListView.class);
            assertTrue(repository.isPresent());
            final BorrowedBooksListViewProjection borrowedBooksListViewProjection =
                    (BorrowedBooksListViewProjection) repository.get()
                                                                .find(USER_ID)
                                                                .get();
            final BorrowedBooksListView state = borrowedBooksListViewProjection.getState();
            assertEquals(0, state.getBookItemCount());
        }

        @Test
        @DisplayName("update information in the BookInventoryViewProjection")
        void updateBookInventoryViewProjection() {
            final Optional<Repository> repository = boundedContext.findRepository(
                    BookInventoryView.class);
            assertTrue(repository.isPresent());
            final BookInventoryViewProjection projection =
                    (BookInventoryViewProjection) repository.get()
                                                            .find(INVENTORY_ID)
                                                            .get();
            final BookInventoryView state = projection.getState();
            assertEquals(0, state.getItemStateList()
                                 .size());
        }

        @Test
        @DisplayName("update information in the LostBookViewProjection")
        void updateLostBookViewProjection() {
            final Optional<Repository> repository = boundedContext.findRepository(
                    LostBookView.class);
            assertTrue(repository.isPresent());
            final Optional repositoryOpt = repository.get()
                                                     .find(INVENTORY_ITEM_ID_1);
            assertFalse(repositoryOpt.isPresent());
        }
    }

    @Nested
    @DisplayName("Add Book -> Append inventory -> User 1: Borrow Book ->" +
            " User 1: Mark loan Should Return Soon -> User 1: Extend Loan Period ->" +
            " User 2: Reserve book")
    class ThirdFlow {
        @BeforeEach
        public void setUp() {
            InventoryRepository.setNewInstance();

            boundedContext = BoundedContexts.create();
            commandBus = boundedContext.getCommandBus();

            final Command addBook = createCommand(createBookInstance());
            commandBus.post(addBook, StreamObservers.noOpObserver());

            final Command appendInventory = createCommand(appendInventoryInstance());
            commandBus.post(appendInventory, StreamObservers.noOpObserver());

            final Command borrowBook = createCommand(borrowBookInstance());
            commandBus.post(borrowBook, StreamObservers.noOpObserver());

            final LoanId loanId = getLoanId();

            final Command markLoanShouldReturnSoon = createCommand(
                    markLoanShouldReturnSoon(loanId, INVENTORY_ID));
            commandBus.post(markLoanShouldReturnSoon, StreamObservers.noOpObserver());

            final Command extendLoanPeriod = createCommand(
                    extendLoanPeriodInstance(INVENTORY_ID, loanId, USER_ID));
            commandBus.post(extendLoanPeriod, StreamObservers.noOpObserver());

            final Command reserveBookUser2 = createCommand(
                    reserveBookInstance(USER_ID_2, INVENTORY_ID));
            commandBus.post(reserveBookUser2, StreamObservers.noOpObserver());
        }

        @Test
        @DisplayName("update information in the ReservedBooksListViewProjection")
        void updateReservedBooksListViewProjection() {
            final Optional<Repository> repository = boundedContext.findRepository(
                    ReservedBooksListView.class);
            assertTrue(repository.isPresent());
            final ReservedBooksListViewProjection projection =
                    (ReservedBooksListViewProjection) repository.get()
                                                                .find(USER_ID_2)
                                                                .get();
            final ReservedBooksListView state = projection.getState();

            final ReservedBookItem bookItem = state.getBookItem(0);
            assertEquals(BOOK_ID, bookItem.getBookId());
            assertEquals(ISBN, bookItem.getIsbn());
            assertEquals(BOOK_TITLE, bookItem.getTitle());
            assertEquals(AUTHOR, bookItem.getAuthorsList()
                                         .get(0));
            assertEquals(COVER_URL, bookItem.getCoverUrl());
            assertEquals(BOOK_CATEGORY, bookItem.getCategorie(0));
            assertEquals(BOOK_SYNOPSIS, bookItem.getSynopsis());
            assertEquals(ReservedBookItemStatus.RESERVED, bookItem.getStatus());
            assertEquals(1, state.getNumberOfReservedBooks());
        }

        @Test
        @DisplayName("update information in the BookReservationViewProjection")
        void updateBookReservationViewProjection() {
            final Optional<Repository> repository = boundedContext.findRepository(
                    BookReservationView.class);
            assertTrue(repository.isPresent());
            final BookReservationViewId bookReservationViewIdUser2 = BookReservationViewId.newBuilder()
                                                                                          .setBookId(
                                                                                                  BOOK_ID)
                                                                                          .setUserId(
                                                                                                  USER_ID_2)
                                                                                          .build();
            final BookReservationViewProjection projection =
                    (BookReservationViewProjection) repository.get()
                                                              .find(bookReservationViewIdUser2)
                                                              .get();
            final BookReservationView state = projection.getState();
            assertEquals(BOOK_ID, state.getBookId());
            assertEquals(USER_NAME, state.getUserName());
            assertEquals(USER_EMAIL_2, state.getEmail());
        }

        @Test
        @DisplayName("update information in the BookInventoryViewProjection")
        void updateBookInventoryViewProjection() {
            testBookInventoryViewByItemState(BORROWED_ITEM_STATE_TODAY2);
        }

        @Test
        @DisplayName("update information in the ReaderLoanViewProjection")
        void updateReaderLoanViewProjection() {
            testReaderLoanView();
        }

        @Test
        @DisplayName("update information in the BorrowedBooksListViewProjection")
        void updateBorrowedBooksListViewProjection() {
            testBorrowedBooksListView();
        }
    }

    private void testBorrowedBooksListView() {
        final Optional<Repository> repository = boundedContext.findRepository(
                BorrowedBooksListView.class);
        assertTrue(repository.isPresent());
        final BorrowedBooksListViewProjection borrowedBooksListViewProjection =
                (BorrowedBooksListViewProjection) repository.get()

                                                            .find(USER_ID)
                                                            .get();
        final BorrowedBooksListView state = borrowedBooksListViewProjection.getState();

        final BorrowedBookItem bookItem = state.getBookItem(0);
        assertEquals(BOOK_ID, bookItem.getBookId());
        assertEquals(ISBN, bookItem.getIsbn());
        assertEquals(BOOK_TITLE, bookItem.getTitle());
        assertEquals(AUTHOR, bookItem.getAuthorList()
                                     .get(0));
        assertEquals(COVER_URL, bookItem.getCoverUrl());
        assertEquals(BOOK_CATEGORY, bookItem.getCategoriesList()
                                            .get(0));
        assertEquals(BOOK_SYNOPSIS, bookItem.getSynopsis());
        assertEquals(false, bookItem.getIsAllowedLoanExtension());
        assertEquals(BorrowedBookItemStatus.BORROWED, bookItem.getStatus());
        assertEquals(1, state.getNumberOfBorrowedBooks());
        assertEquals(0, state.getNumberOfOverdueBooks());
    }

    private void testBookInventoryViewByItemState(InventoryItemState borrowedItemStateToday2) {
        final Optional<Repository> repository = boundedContext.findRepository(
                BookInventoryView.class);
        assertTrue(repository.isPresent());
        final BookInventoryViewProjection projection =
                (BookInventoryViewProjection) repository.get()
                                                        .find(INVENTORY_ID)
                                                        .get();
        final BookInventoryView state = projection.getState();
        assertEquals(BOOK_TITLE, state.getTitle());
        assertEquals(AUTHOR, state.getAuthorList()
                                  .get(0));
        assertEquals(borrowedItemStateToday2, state.getItemState(0));
    }

    private void testBookViewAfterBorrowing() {
        final Optional<Repository> repository = boundedContext.findRepository(
                BookView.class);
        assertTrue(repository.isPresent());
        final BookViewProjection bookViewProjection = (BookViewProjection) repository.get()
                                                                                     .find(BOOK_ID)
                                                                                     .get();
        final BookView state = bookViewProjection.getState();

        assertEquals(ISBN, state.getIsbn());
        assertEquals(BOOK_TITLE, state.getTitle());
        assertEquals(AUTHOR, state.getAuthorList()
                                  .get(0));
        assertEquals(COVER_URL, state.getCoverUrl());
        assertEquals(BOOK_CATEGORY, state.getCategories(0));
        assertEquals(BOOK_SYNOPSIS, state.getSynopsis());
        assertEquals(0, state.getAvailableCount());
        assertEquals(BookStatus.EXPECTED, state.getStatus());
    }

    private void testReaderLoanView() {
        final LoanId loanId = getLoanId();
        final Optional<Repository> projectionRepo = boundedContext.findRepository(
                ReaderLoanView.class);
        assertTrue(projectionRepo.isPresent());

        final ReaderLoanViewId readerLoanViewId = ReaderLoanViewId.newBuilder()
                                                                  .setLoanId(loanId)
                                                                  .setUserId(USER_ID)
                                                                  .build();

        final ReaderLoanViewProjection projection =
                (ReaderLoanViewProjection) projectionRepo.get()
                                                         .find(readerLoanViewId)
                                                         .get();
        final ReaderLoanView state = projection.getState();
        assertEquals(BOOK_TITLE, state.getTitle());
        assertEquals(AUTHOR, state.getAuthorList()
                                  .get(0));
        assertEquals(INVENTORY_ITEM_ID_1, state.getItemId());
        assertEquals(CURRENT_DATE, state.getWhenTaken());
    }

    private LoanId getLoanId() {
        final Repository inventoryRepo = boundedContext.findRepository(Inventory.class)
                                                       .get();
        final InventoryAggregate inventoryAggregate =
                (InventoryAggregate) inventoryRepo.find(INVENTORY_ID)
                                                  .get();
        return inventoryAggregate.getState()
                                 .getLoans(0)
                                 .getLoanId();
    }

    private Command createCommand(GeneratedMessageV3 message) {
        return getRequestFactory().command()
                                  .create(toMessage(message));
    }

    private ActorRequestFactory getRequestFactory() {
        return requestFactory;
    }
}
