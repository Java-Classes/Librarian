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

package javaclasses.exlibris.q.admin;

import io.spine.Identifier;
import javaclasses.exlibris.BookLoanViewId;
import javaclasses.exlibris.c.BookBorrowed;
import javaclasses.exlibris.q.BookLoanView;
import javaclasses.exlibris.q.ProjectionTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static io.spine.server.projection.ProjectionEventDispatcher.dispatch;
import static javaclasses.exlibris.testdata.InventoryEventFactory.bookBorrowedInstance;
import static javaclasses.exlibris.testdata.TestValues.BOOK_ID;
import static javaclasses.exlibris.testdata.TestValues.DEFAULT_DATE1;
import static javaclasses.exlibris.testdata.TestValues.DEFAULT_DUE_DATE;
import static javaclasses.exlibris.testdata.TestValues.INVENTORY_ITEM_ID_1;
import static javaclasses.exlibris.testdata.TestValues.USER_EMAIL_1;
import static javaclasses.exlibris.testdata.TestValues.USER_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;

class BookLoanViewProjectionTest extends ProjectionTest {

    private BookLoanViewProjection projection;

    @BeforeEach
    void setUp() {
        final BookLoanViewId id = BookLoanViewId.newBuilder()
                                                .setValue(Identifier.newUuid())
                                                .build();
        projection = new BookLoanViewProjection(id);
    }

    @Nested
    @DisplayName("BookBorrowed event should be interpreted by BookLoanViewProjection and")
    class BookBorrowedEvent {

        @Test
        @DisplayName("add information about start loan period")
        void addInformation() {
            final BookBorrowed bookBorrowed = bookBorrowedInstance();
            dispatch(projection, createEvent(bookBorrowed));

            final BookLoanView state = projection.getState();
            assertEquals(BOOK_ID, state.getBookId());
            assertEquals(INVENTORY_ITEM_ID_1, state.getItemId());
            assertEquals(USER_NAME, state.getUserName());
            assertEquals(USER_EMAIL_1, state.getEmail());
            assertEquals(DEFAULT_DATE1, state.getWhenTaken());
            assertEquals(DEFAULT_DUE_DATE, state.getWhenDue());
        }
    }
}
