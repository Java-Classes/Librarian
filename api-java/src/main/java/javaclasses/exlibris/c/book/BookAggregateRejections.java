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

package javaclasses.exlibris.c.book;

import javaclasses.exlibris.c.AddBook;
import javaclasses.exlibris.c.RemoveBook;
import javaclasses.exlibris.c.UpdateBook;
import javaclasses.exlibris.c.rejection.BookAlreadyExists;
import javaclasses.exlibris.c.rejection.CannotRemoveMissingBook;
import javaclasses.exlibris.c.rejection.CannotUpdateMissingBook;

import static com.google.common.base.Preconditions.checkNotNull;
import static io.spine.time.Time.getCurrentTime;

/**
 * Rejections generator for {@link BookAggregate}.
 *
 * <p>To throw a rejection it is necessary to call static method.
 *
 * @author Paul Ageyev
 * @author Alexander Karpets
 * @see BookAggregate
 */
public class BookAggregateRejections {

    private BookAggregateRejections() {
    }

    /**
     * Throws a rejection when a librarian tries to add an existing book.
     */
    static BookAlreadyExists bookAlreadyExists(AddBook cmd) {
        checkNotNull(cmd);
        return new BookAlreadyExists(cmd.getBookId(), cmd.getBookDetails()
                                                         .getTitle(), cmd.getLibrarianId(),
                                     getCurrentTime());
    }

    /**
     * Throws a rejection when a librarian tries to update a missing book.
     */
    static CannotUpdateMissingBook cannotUpdateMissingBook(UpdateBook cmd) {
        checkNotNull(cmd);
        return new CannotUpdateMissingBook(cmd.getBookId(), cmd.getLibrarianId(),
                                           getCurrentTime());
    }

    /**
     * Throws a rejection when a librarian tries to remove a missing book.
     */
    static CannotRemoveMissingBook cannotRemoveMissingBook(RemoveBook cmd) {
        checkNotNull(cmd);
        return new CannotRemoveMissingBook(cmd.getBookId(), cmd.getLibrarianId(),
                                           cmd.getWhenRemoved());
    }
}
