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
import io.spine.people.PersonName;
import io.spine.server.projection.Projection;
import javaclasses.exlibris.ListViewId;
import javaclasses.exlibris.UserId;
import javaclasses.exlibris.c.BookBorrowed;
import javaclasses.exlibris.q.ReaderBooksCountItem;
import javaclasses.exlibris.q.ReadersListView;
import javaclasses.exlibris.q.ReadersListViewVBuilder;

import java.util.List;
import java.util.OptionalInt;
import java.util.stream.IntStream;

/**
 * The projection state of a readers list view.
 *
 * <p>Contains the collection of all readers with books count.
 *
 * @author Yegor Udovchenko
 */
public class ReadersListViewProjection extends Projection<ListViewId, ReadersListView, ReadersListViewVBuilder> {

    /**
     * The {@link javaclasses.exlibris.q.admin.ReadersListViewProjection} is a singleton.
     *
     * <p>The {@code ID} value should be the same for all JVMs
     * to support work with the same projection from execution to execution.
     */
    public static final ListViewId ID = ListViewId.newBuilder()
                                                  .setValue("ReadersListViewProjection")
                                                  .build();

    /**
     * @see Projection#Projection(Object)
     */
    public ReadersListViewProjection(ListViewId id) {
        super(id);
    }

    @Subscribe
    public void on(BookBorrowed event) {
        final UserId whoBorrowed = event.getWhoBorrowed();
        final List<ReaderBooksCountItem> readers = getBuilder().getReader();
        final int index = getIndexByUserId(readers, whoBorrowed);

        if (index != -1) {
            // TODO 4/20/2018[yegor.udovchenko]: find user name.
            final PersonName personName = PersonName.newBuilder()
                                                    .setGivenName("Temp")
                                                    .setFamilyName("Name")
                                                    .build();
            final ReaderBooksCountItem newReaderBooksCountItem = ReaderBooksCountItem
                    .newBuilder()
                    .setUserId(whoBorrowed)
                    .setEmail(whoBorrowed.getEmail())
                    .setUserName(personName)
                    .setCurrentlyReadingCount(1)
                    .setReservationsCount(0)
                    .setOverdueCount(0)
                    .build();
            getBuilder().addReader(newReaderBooksCountItem);
        } else {
            final ReaderBooksCountItem oldReaderBooksCountItem = readers.get(index);
            final int oldCurrentlyReadingCount = oldReaderBooksCountItem.getCurrentlyReadingCount();
            final ReaderBooksCountItem updatedReaderBooksCountItem = ReaderBooksCountItem
                    .newBuilder(oldReaderBooksCountItem)
                    .setCurrentlyReadingCount(oldCurrentlyReadingCount + 1)
                    .build();
            getBuilder().setReader(index, updatedReaderBooksCountItem);
        }
    }

    private int getIndexByUserId(List<ReaderBooksCountItem> items, UserId id) {
        final OptionalInt index = IntStream.range(0, items.size())
                                           .filter(i -> items.get(i)
                                                             .getUserId()
                                                             .equals(id))
                                           .findFirst();
        return index.isPresent() ? index.getAsInt() : -1;
    }
}
