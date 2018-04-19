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
import javaclasses.exlibris.BookDetails;
import javaclasses.exlibris.BookId;
import javaclasses.exlibris.ListViewId;
import javaclasses.exlibris.c.BookAdded;
import javaclasses.exlibris.c.BookBorrowed;
import javaclasses.exlibris.c.BookRemoved;
import javaclasses.exlibris.c.BookReturned;
import javaclasses.exlibris.c.InventoryAppended;
import javaclasses.exlibris.c.InventoryDecreased;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * A projection state of all books.
 *
 * <p>Contains the list of all books.
 *
 * @author Yurii Haidamaka
 */
public class AllBooksListViewProjection extends Projection<ListViewId, AllBooksListView, AllBooksListViewVBuilder> {

    /**
     * The {@link AllBooksListViewProjection} is a singleton.
     *
     * <p>The {@code ID} value should be the same for all JVMs
     * to support work with the same projection from execution to execution.
     */
    public static final ListViewId ID = ListViewId.newBuilder()
                                                    .setValue("AllBooksListViewProjection")
                                                    .build();

    /**
     * @see Projection#Projection(Object)
     */
    public AllBooksListViewProjection(ListViewId id) {
        super(id);
    }

    @Subscribe
    public void on(BookAdded event) {
        final BookDetails bookDetails = event.getDetails();
        BookItem bookItem = BookItem.newBuilder()
                                    .setBookId(event.getBookId())
                                    .setIsbn(bookDetails.getIsbn())
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

    @Subscribe
    public void on(BookRemoved event) {
        final BookId id = event.getBookId();

        final List<BookItem> items = new ArrayList<>(getBuilder().getBookItem());

        final int index = IntStream.range(0, items.size())
                                   .filter(i -> items.get(i)
                                                     .getBookId()
                                                     .equals(id))
                                   .findFirst()
                                   .getAsInt();
        items.remove(index);
        getBuilder().addAllBookItem(items);
    }

    @Subscribe
    public void on(InventoryAppended event) {
        final BookId id = event.getInventoryId()
                               .getBookId();
        final List<BookItem> items = getBuilder().getBookItem();
        final int index = getIndexByBookId(items, id);
        final BookItem oldBookItem = items.get(index);
        final int availableCount = oldBookItem.getAvailableCount();
        BookItem newBookItem = BookItem.newBuilder(oldBookItem)
                                       .setAvailableCount(availableCount + 1)
                                       .setStatus(BookItemStatus.AVAILABLE)
                                       .build();
        items.remove(index);
        items.add(newBookItem);
    }

    @Subscribe
    public void on(InventoryDecreased event) {
        final BookId id = event.getInventoryId()
                               .getBookId();
        final List<BookItem> items = getBuilder().getBookItem();
        final int index = getIndexByBookId(items, id);
        final BookItem oldBookItem = items.get(index);
        final int availableCount = oldBookItem.getAvailableCount();
        final BookItemStatus status =
                availableCount == 1 ? BookItemStatus.EXPECTED : BookItemStatus.AVAILABLE;
        BookItem newBookItem = BookItem.newBuilder(oldBookItem)
                                       .setAvailableCount(availableCount - 1)
                                       .setStatus(status)
                                       .build();
        items.remove(index);
        items.add(newBookItem);
    }

    @Subscribe
    public void on(BookBorrowed event) {
        final BookId id = event.getInventoryId()
                               .getBookId();
        final List<BookItem> items = getBuilder().getBookItem();
        final int index = getIndexByBookId(items, id);
        final BookItem oldBookItem = items.get(index);
        final int availableCount = oldBookItem.getAvailableCount();
        final BookItemStatus status =
                availableCount == 1 ? BookItemStatus.EXPECTED : BookItemStatus.AVAILABLE;
        BookItem newBookItem = BookItem.newBuilder(oldBookItem)
                                       .setAvailableCount(availableCount - 1)
                                       .setStatus(status)
                                       .build();
        items.remove(index);
        items.add(newBookItem);
    }

    @Subscribe
    public void on(BookReturned event) {
        final BookId id = event.getInventoryId()
                               .getBookId();
        final List<BookItem> items = getBuilder().getBookItem();
        final int index = getIndexByBookId(items, id);
        final BookItem oldBookItem = items.get(index);
        final int availableCount = oldBookItem.getAvailableCount();
        BookItem newBookItem = BookItem.newBuilder(oldBookItem)
                                       .setAvailableCount(availableCount + 1)
                                       .setStatus(BookItemStatus.AVAILABLE)
                                       .build();
        items.remove(index);
        items.add(newBookItem);
    }

    private int getIndexByBookId(List<BookItem> items, BookId id) {
        final int index = IntStream.range(0, items.size())
                                   .filter(i -> items.get(i)
                                                     .getBookId()
                                                     .equals(id))
                                   .findFirst()
                                   .getAsInt();
        return index;
    }
}
