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
import javaclasses.exlibris.ListViewId;
import javaclasses.exlibris.c.BookAdded;
import javaclasses.exlibris.c.InventoryAppended;
import javaclasses.exlibris.q.ExpectedSoonBooksListView;
import javaclasses.exlibris.q.ExpectedSoonBooksListViewVBuilder;
import javaclasses.exlibris.q.ExpectedSoonItem;
import javaclasses.exlibris.q.ExpectedSoonItemStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.stream.IntStream;

/**
 * A projection state of books that are expected in the library.
 *
 * <p>Contains the list of all books.
 *
 * @author Yurii Haidamaka
 */
public class ExpectedSoonBooksListViewProjection extends Projection<ListViewId, ExpectedSoonBooksListView, ExpectedSoonBooksListViewVBuilder> {

    /**
     * The {@link ExpectedSoonBooksListViewProjection} is a singleton.
     *
     * <p>The {@code ID} value should be the same for all JVMs
     * to support work with the same projection from execution to execution.
     */
    public static final ListViewId ID = ListViewId.newBuilder()
                                                  .setValue("ExpectedSoonBooksListViewProjection")
                                                  .build();

    /**
     * @see Projection#Projection(Object)
     */
    public ExpectedSoonBooksListViewProjection(ListViewId id) {
        super(id);
    }

    @Subscribe
    public void on(BookAdded event) {
        final BookDetails bookDetails = event.getDetails();
        final ExpectedSoonItem bookItem = ExpectedSoonItem.newBuilder()
                                                          .setBookId(event.getBookId())
                                                          .setIsbn(bookDetails.getIsbn())
                                                          .setTitle(bookDetails.getTitle())
                                                          .setAuthors(bookDetails.getAuthor())
                                                          .setCoverUrl(
                                                                  bookDetails.getBookCoverUrl())
                                                          .addAllCategories(
                                                                  bookDetails.getCategoriesList())
                                                          .setSynopsis(bookDetails.getSynopsis())
                                                          .setStatus(
                                                                  ExpectedSoonItemStatus.EXPECTED_SOON)
                                                          .build();

        getBuilder().addBookItem(bookItem);
    }

    @Subscribe
    public void on(InventoryAppended event) {
        final BookId id = event.getInventoryId()
                               .getBookId();
        final List<ExpectedSoonItem> items = new ArrayList<>(getBuilder().getBookItem());
        final int index = getIndexByBookId(items, id);
        getBuilder().removeBookItem(index);
    }

    private int getIndexByBookId(List<ExpectedSoonItem> items, BookId id) {
        final OptionalInt index = IntStream.range(0, items.size())
                                           .filter(i -> items.get(i)
                                                             .getBookId()
                                                             .equals(id))
                                           .findFirst();
        return index.isPresent() ? index.getAsInt() : -1;
    }

}
