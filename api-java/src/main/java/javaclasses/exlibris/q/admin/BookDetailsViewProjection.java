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

import io.spine.core.Subscribe;
import io.spine.server.projection.Projection;
import javaclasses.exlibris.BookDetails;
import javaclasses.exlibris.BookId;
import javaclasses.exlibris.c.BookAdded;
import javaclasses.exlibris.c.BookRemoved;
import javaclasses.exlibris.c.BookUpdated;
import javaclasses.exlibris.q.BookDetailsView;
import javaclasses.exlibris.q.BookDetailsViewVBuilder;

/**
 * The projection state of a book details view.
 *
 * @author Yurii Haidamaka
 */
public class BookDetailsViewProjection extends Projection<BookId, BookDetailsView, BookDetailsViewVBuilder> {

    /**
     * @see Projection#Projection(Object)
     */
    public BookDetailsViewProjection(BookId id) {
        super(id);
    }

    @Subscribe
    public void on(BookAdded event) {
        final BookDetails bookDetails = event.getDetails();
        getBuilder().setBookDetails(bookDetails);
    }

    @Subscribe
    public void on(BookUpdated event) {
        final BookDetails bookDetails = event.getBookDetailsChange()
                                             .getNewBookDetails();
        getBuilder().setBookDetails(bookDetails);
    }

    @Subscribe
    public void on(BookRemoved event) {
        getBuilder().clearBookDetails()
                    .clearId();
    }
}
