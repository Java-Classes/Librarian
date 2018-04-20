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

package javaclasses.exlibris.testdata;

import io.spine.net.Url;
import io.spine.people.PersonName;
import io.spine.server.event.EventEnricher;
import javaclasses.exlibris.AuthorName;
import javaclasses.exlibris.Book;
import javaclasses.exlibris.BookDetails;
import javaclasses.exlibris.BookSynopsis;
import javaclasses.exlibris.BookTitle;
import javaclasses.exlibris.Category;
import javaclasses.exlibris.InventoryId;
import javaclasses.exlibris.Isbn;

import java.util.function.Function;

/**
 * Provides event enricher for the test needs.
 *
 * @author Illia Shepilov
 */
public class EventEnricherFactory {
    public static final Isbn ISBN = Isbn.newBuilder()
                                        .setValue("0201485672")
                                        .build();

    public static final BookTitle TITLE = BookTitle.newBuilder()
                                                   .setTitle("Refactoring")
                                                   .build();

    public static final AuthorName AUTHOR = AuthorName.newBuilder()
                                                      .addAuthorName(
                                                              PersonName.newBuilder()
                                                                        .setFamilyName(
                                                                                "Fowler")
                                                                        .setGivenName(
                                                                                "Martin"))
                                                      .build();

    public static final Url COVER_URL = Url.newBuilder()
                                           .setRaw("http://library.teamdev.com/book/1")
                                           .build();

    public static final BookSynopsis SYNOPSIS = BookSynopsis.newBuilder()
                                                            .setBookSynopsis(
                                                                    "As the application of object " +
                                                                            "technology--particularly the Java programming language")
                                                            .build();

    public static final Category CATEGORY = Category.newBuilder()
                                                    .setValue("Programming")
                                                    .build();

    public static final BookDetails DETAILS = BookDetails.newBuilder()
                                                         .setIsbn(ISBN)
                                                         .setTitle(TITLE)
                                                         .setAuthor(AUTHOR)
                                                         .setBookCoverUrl(COVER_URL)
                                                         .setSynopsis(SYNOPSIS)
                                                         .addCategories(CATEGORY)
                                                         .build();
    private static final Book BOOK = Book.newBuilder()
                                         .setBookDetails(DETAILS)
                                         .build();

    private static final Function<InventoryId, Book> INVENTORY_ID_TO_BOOK = inventoryId -> BOOK;

    private EventEnricherFactory() {
    }

    /**
     * Provides a pre-configured {@link EventEnricher} event instance.
     *
     * @return {@code EventEnricher}
     */
    public static EventEnricher eventEnricherInstance() {
        final EventEnricher result = EventEnricher.newBuilder()
                                                  .add(InventoryId.class,
                                                       Book.class,
                                                       INVENTORY_ID_TO_BOOK::apply)
                                                  .build();
        return result;
    }
}
