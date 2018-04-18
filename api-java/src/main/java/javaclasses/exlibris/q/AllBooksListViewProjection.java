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

package javaclasses.exlibris.q;

import io.spine.core.Subscribe;
import io.spine.server.projection.Projection;
import javaclasses.exlibris.AllBooksListId;
import javaclasses.exlibris.BookDetails;
import javaclasses.exlibris.c.BookAdded;

/**
 * A projection state of all books.
 *
 * <p>Contains the list of all books.
 *
 * @author Yurii Haidamaka
 */
public class AllBooksListViewProjection extends Projection<AllBooksListId, AllBooksListView, AllBooksListViewVBuilder> {

    /**
     * The {@link AllBooksListViewProjection} is a singleton.
     *
     * <p>The {@code ID} value should be the same for all JVMs
     * to support work with the same projection from execution to execution.
     */
    public static final AllBooksListId ID = AllBooksListId.newBuilder()
                                                          .setValue("AllBooksListViewProjection")
                                                          .build();

    /**
     * @see Projection#Projection(Object)
     */
    public AllBooksListViewProjection(AllBooksListId id) {
        super(id);
    }

    @Subscribe
    public void on(BookAdded event) {
        final BookDetails bookDetails = event.getDetails();
        BookItem bookItem = BookItem.newBuilder()
                                    .setBookId(event.getBookId())
                                    .setTitle(bookDetails.getTitle())
                                    .setAuthors(bookDetails.getAuthor())
                                    .setCoverUrl(bookDetails.getBookCoverUrl())
                                    .addAllCategories(bookDetails.getCategoriesList())
                                    .setSynopsis(bookDetails.getSynopsis())
                                    .setAvailableCount(0)
                                    .setStatus(BookItemStatus.EXPECTED)
                                    .build();

        getBuilder().addBookItem(bookItem);
    }
}
