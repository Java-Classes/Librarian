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

package javaclasses.exlibris.q.user;

import io.spine.core.Subscribe;
import io.spine.server.projection.Projection;
import javaclasses.exlibris.BookDetails;
import javaclasses.exlibris.BookId;
import javaclasses.exlibris.c.BookAdded;
import javaclasses.exlibris.c.BookBecameAvailable;
import javaclasses.exlibris.c.BookBorrowed;
import javaclasses.exlibris.c.BookRemoved;
import javaclasses.exlibris.c.InventoryDecreased;
import javaclasses.exlibris.q.BookStatus;
import javaclasses.exlibris.q.BookView;
import javaclasses.exlibris.q.BookViewVBuilder;

/**
 * A projection state of a one book from library.
 *
 * <p>Contains full information about one book.
 *
 * @author Yurii Haidamaka
 */
public class BookViewProjection extends Projection<BookId, BookView, BookViewVBuilder> {

    /**
     * @see Projection#Projection(Object)
     */
    public BookViewProjection(BookId id) {
        super(id);
    }

    @Subscribe
    public void on(BookAdded event) {
        final BookDetails bookDetails = event.getDetails();
        getBuilder().setBookId(event.getBookId())
                    .setIsbn(bookDetails.getIsbn())
                    .setTitle(bookDetails.getTitle())
                    .addAllAuthor(bookDetails.getAuthorList())
                    .setCoverUrl(bookDetails.getBookCoverUrl())
                    .addAllCategories(bookDetails.getCategoriesList())
                    .setSynopsis(bookDetails.getSynopsis())
                    .setAvailableCount(0)
                    .setStatus(BookStatus.EXPECTED);
    }

    @Subscribe
    public void on(BookRemoved event) {
        setDeleted(true);
    }

    @Subscribe
    public void on(BookBecameAvailable event) {
        getBuilder().setAvailableCount(event.getAvailableBooksCount())
                    .setStatus(BookStatus.AVAILABLE);
    }

    @Subscribe
    public void on(InventoryDecreased event) {
        final int inLibraryCount = event.getAvailableBooksCount();
        final BookStatus status =
                inLibraryCount == 0 ? BookStatus.EXPECTED : BookStatus.AVAILABLE;
        getBuilder().setAvailableCount(inLibraryCount)
                    .setStatus(status);
    }

    @Subscribe
    public void on(BookBorrowed event) {
        final int inLibraryCount = event.getAvailableBooksCount();
        final BookStatus status =
                inLibraryCount == 0 ? BookStatus.EXPECTED : BookStatus.AVAILABLE;
        getBuilder().setAvailableCount(inLibraryCount)
                    .setStatus(status);
    }
}
