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
import javaclasses.exlibris.BookDetailsChange;
import javaclasses.exlibris.c.AddBook;
import javaclasses.exlibris.c.AppendInventory;
import javaclasses.exlibris.c.UpdateBook;
import javaclasses.exlibris.c.aggregate.InventoryCommandTest;
import javaclasses.exlibris.testdata.BookCommandFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static javaclasses.exlibris.testdata.BookCommandFactory.createBookInstance;
import static javaclasses.exlibris.testdata.BookCommandFactory.updateBookInstance;
import static javaclasses.exlibris.testdata.InventoryCommandFactory.appendInventoryInstance;

/**
 * @author Alexander Karpets
 */
public class SimpleFlows extends InventoryCommandTest<Message> {

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
    }

    @Test
    @DisplayName("d")
    void useCase() {
        final BookDetailsChange newBookDetails = BookDetailsChange.newBuilder()
                                                                  .setNewBookDetails(
                                                                          BookCommandFactory.bookDetails2)
                                                                  .build();
        final AddBook addBook = createBookInstance();
        final UpdateBook updateBook = updateBookInstance(BookCommandFactory.bookId2,
                                                         BookCommandFactory.userId2,
                                                         newBookDetails);
        final AppendInventory appendInventory = appendInventoryInstance();

    }
}
