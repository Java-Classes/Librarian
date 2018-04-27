///*
// * Copyright 2018, TeamDev Ltd. All rights reserved.
// *
// * Redistribution and use in source and/or binary forms, with or without
// * modification, must retain the above copyright notice and the following
// * disclaimer.
// *
// * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
// * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
// * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
// * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
// * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
// * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
// * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
// * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
// * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
// * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
// */
//
//package javaclasses.exlibris.q.admin;
//
//import javaclasses.exlibris.c.InventoryAppended;
//import javaclasses.exlibris.q.BookEventLogView;
//import javaclasses.exlibris.q.BookInventoryView;
//import javaclasses.exlibris.q.ProjectionTest;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//
//import static io.spine.server.projection.ProjectionEventDispatcher.dispatch;
//import static javaclasses.exlibris.testdata.BookEventFactory.AUTHOR;
//import static javaclasses.exlibris.testdata.BookEventFactory.BOOK_ID;
//import static javaclasses.exlibris.testdata.BookEventFactory.TITLE;
//import static javaclasses.exlibris.testdata.BookEventFactory.USER_EMAIL_ADRESS;
//import static javaclasses.exlibris.testdata.InventoryEventFactory.DEFAULT_TIMESTAMP1;
//import static javaclasses.exlibris.testdata.InventoryEventFactory.INVENTORY_ID;
//import static javaclasses.exlibris.testdata.InventoryEventFactory.USER_NAME;
//import static javaclasses.exlibris.testdata.InventoryEventFactory.inventoryAppendedInstance;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//class BookInventoryViewProjectionTest extends ProjectionTest {
//
//    private BookInventoryViewProjection projection;
//
//    @BeforeEach
//    void setUp() {
//        projection = new BookInventoryViewProjection(INVENTORY_ID);
//    }
//
//    @Nested
//    @DisplayName("InventoryAppended event should be interpreted by BookInventoryViewProjection and")
//    class BookBorrowedEvent {
//
//        @Test
//        @DisplayName("add information about inventory item")
//        void addInformation() {
//            final InventoryAppended inventoryAppended = inventoryAppendedInstance();
//            dispatch(projection, createEvent(inventoryAppended));
//
//            final BookInventoryView state = projection.getState();
//            assertEquals(TITLE, state.getTitle());
//            assertEquals(AUTHOR, state.getAuthor());
//        }
//
//    }
//
//}
