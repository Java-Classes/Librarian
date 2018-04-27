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

import javaclasses.exlibris.c.BookAdded;
import javaclasses.exlibris.c.BookUpdated;
import javaclasses.exlibris.q.BookDetailsView;
import javaclasses.exlibris.q.ProjectionTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static io.spine.server.projection.ProjectionEventDispatcher.dispatch;
import static javaclasses.exlibris.testdata.BookEventFactory.BOOK_ID;
import static javaclasses.exlibris.testdata.BookEventFactory.DETAILS;
import static javaclasses.exlibris.testdata.BookEventFactory.NEW_DETAILS;
import static javaclasses.exlibris.testdata.BookEventFactory.bookAddedInstance;
import static javaclasses.exlibris.testdata.BookEventFactory.bookUpdatedInstance;
import static org.junit.jupiter.api.Assertions.assertEquals;

class BookDetailsViewProjectionTest extends ProjectionTest {

    private BookDetailsViewProjection projection;

    @BeforeEach
    void setUp() {
        projection = new BookDetailsViewProjection(BOOK_ID);
    }

    @Nested
    @DisplayName("BookAdded event should be interpreted by BookDetailsViewProjection and")
    class BookAddedEvent {

        @Test
        @DisplayName("add book details")
        void addBookDetails() {
            final BookAdded bookAdded = bookAddedInstance();
            dispatch(projection, createEvent(bookAdded));

            final BookDetailsView state = projection.getState();
            assertEquals(DETAILS, state.getBookDetails());
        }
    }

    @Nested
    @DisplayName("BookUpdated event should be interpreted by BookDetailsViewProjection and")
    class BookUpdatedEvent {

        @Test
        @DisplayName("update book details")
        void updateBookDetails() {
            final BookAdded bookAdded = bookAddedInstance();
            dispatch(projection, createEvent(bookAdded));

            final BookUpdated bookUpdated = bookUpdatedInstance();
            dispatch(projection, createEvent(bookUpdated));

            final BookDetailsView state = projection.getState();
            assertEquals(NEW_DETAILS, state.getBookDetails());
        }
    }

}
