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

import io.spine.server.projection.ProjectionRepository;
import io.spine.server.route.EventRouting;
import javaclasses.exlibris.InventoryItemId;
import javaclasses.exlibris.c.BookLost;
import javaclasses.exlibris.c.InventoryDecreased;
import javaclasses.exlibris.q.LostBookView;

import static java.util.Collections.singleton;

/**
 * Repository for the {@link LostBookViewProjection}.
 *
 * @author Yurii Haidamaka
 */
public class LostBookViewRepository extends ProjectionRepository<InventoryItemId, LostBookViewProjection, LostBookView> {
    @Override
    public void onRegistered() {
        super.onRegistered();
        setUpEventRoute();
    }

    /**
     * Adds the {@link io.spine.server.route.EventRoute EventRoute}s to the repository.
     */
    protected void setUpEventRoute() {
        final EventRouting<InventoryItemId> routing = getEventRouting();
        routing.route(BookLost.class,
                      (message, context) -> singleton(message.getInventoryItemId()));
        routing.route(InventoryDecreased.class,
                      (message, context) -> singleton(message.getInventoryItemId()));
    }
}
