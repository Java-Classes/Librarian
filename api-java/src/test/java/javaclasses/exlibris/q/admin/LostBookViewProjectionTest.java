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

import javaclasses.exlibris.c.BookLost;
import javaclasses.exlibris.c.InventoryDecreased;
import javaclasses.exlibris.q.LostBookView;
import javaclasses.exlibris.q.ProjectionTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static io.spine.server.projection.ProjectionEventDispatcher.dispatch;
import static javaclasses.exlibris.testdata.InventoryEventFactory.bookLostInstance;
import static javaclasses.exlibris.testdata.InventoryEventFactory.inventoryDecreasedInstance;
import static javaclasses.exlibris.testdata.TestValues.AUTHOR_NAME;
import static javaclasses.exlibris.testdata.TestValues.BOOK_TITLE;
import static javaclasses.exlibris.testdata.TestValues.DEFAULT_DATE1;
import static javaclasses.exlibris.testdata.TestValues.INVENTORY_ITEM_ID_1;
import static javaclasses.exlibris.testdata.TestValues.USER_EMAIL_1;
import static javaclasses.exlibris.testdata.TestValues.USER_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;

class LostBookViewProjectionTest extends ProjectionTest {

    private LostBookViewProjection projection;

    @BeforeEach
    void setUp() {
        projection = new LostBookViewProjection(INVENTORY_ITEM_ID_1);
    }

    @Nested
    @DisplayName("BookLost event should be interpreted by" +
            " ReaderLoanViewProjection and")
    class BookLostEvent {

        @Test
        @DisplayName("add information about losing a book")
        void addInformation() {
            final BookLost bookLost = bookLostInstance();
            dispatch(projection, createEvent(bookLost));

            final LostBookView state = projection.getState();
            assertEquals(BOOK_TITLE, state.getTitle());
            assertEquals(USER_NAME, state.getUserName());
            assertEquals(USER_EMAIL_1, state.getEmail());
            assertEquals(AUTHOR_NAME, state.getAuthors());
            assertEquals(INVENTORY_ITEM_ID_1, state.getItemId());
            assertEquals(DEFAULT_DATE1, state.getWhenReported());
        }
    }

    @Nested
    @DisplayName("InventoryDecreased event should be interpreted by" +
            " BookReservationViewProjection and")
    class InventoryDecreasedEvent {

        @Test
        @DisplayName("clear this projection")
        void clearProjection() {
            final BookLost bookLost = bookLostInstance();
            dispatch(projection, createEvent(bookLost));

            final InventoryDecreased inventoryDecreased =
                    inventoryDecreasedInstance();
            dispatch(projection, createEvent(inventoryDecreased));

            final LostBookView state = projection.getState();
            assertEquals("", state.toString());
        }
    }
}
