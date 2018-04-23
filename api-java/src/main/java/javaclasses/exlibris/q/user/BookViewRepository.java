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

import io.spine.server.projection.ProjectionRepository;
import javaclasses.exlibris.BookId;
import javaclasses.exlibris.c.BookAdded;
import javaclasses.exlibris.c.BookBorrowed;
import javaclasses.exlibris.c.BookLost;
import javaclasses.exlibris.c.BookRemoved;
import javaclasses.exlibris.c.InventoryAppended;
import javaclasses.exlibris.c.InventoryDecreased;
import javaclasses.exlibris.q.BookView;

import java.util.Collections;

/**
 * Repository for the {@link BookViewProjection}.
 *
 * @author Yurii Haidamaka
 */
public class BookViewRepository extends ProjectionRepository<BookId, BookViewProjection, BookView> {
    @Override
    public void onRegistered() {
        super.onRegistered();
        setUpEventRoute();
    }

    /**
     * Adds the {@link io.spine.server.route.EventRoute EventRoute}s to the repository.
     *
     * <p>Override this method in successor classes, otherwise all successors will use
     * {@code BookViewProjection.ID}.
     */
    protected void setUpEventRoute() {
        getEventRouting().replaceDefault(((message, context) -> {
            if (message instanceof BookAdded) {
                final BookAdded event = (BookAdded) message;
                return Collections.singleton(event.getBookId());
            }
            if (message instanceof BookRemoved) {
                final BookRemoved event = (BookRemoved) message;
                return Collections.singleton(event.getBookId());
            }
            if (message instanceof InventoryAppended) {
                final InventoryAppended event = (InventoryAppended) message;
                return Collections.singleton(event.getInventoryId()
                                                  .getBookId());
            }
            if (message instanceof InventoryDecreased) {
                final InventoryDecreased event = (InventoryDecreased) message;
                return Collections.singleton(event.getInventoryId()
                                                  .getBookId());
            }
            if (message instanceof BookBorrowed) {
                final BookBorrowed event = (BookBorrowed) message;
                return Collections.singleton(event.getInventoryId()
                                                  .getBookId());
            }
            if (message instanceof BookLost) {
                final BookLost event = (BookLost) message;
                return Collections.singleton(event.getInventoryId()
                                                  .getBookId());
            }
            return null;
        }));
    }
}
