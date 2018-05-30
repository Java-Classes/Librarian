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

package javaclasses.exlibris.c.procman;

import io.spine.server.procman.ProcessManagerRepository;
import io.spine.server.route.RejectionRouting;
import javaclasses.exlibris.InventoryRejectionInspector;
import javaclasses.exlibris.InventoryRejectionInspectorId;
import javaclasses.exlibris.c.rejection.Rejections;

import static java.util.Collections.singleton;
import static javaclasses.exlibris.c.procman.InventoryRejectionInspectorProcman.ID;

/**
 * @author Yurii Haidamaka
 */
public class InventoryRejectionInspectorRepository extends ProcessManagerRepository<InventoryRejectionInspectorId, InventoryRejectionInspectorProcman, InventoryRejectionInspector> {
    public InventoryRejectionInspectorRepository() {
        super();
        setUpEventRoute();
    }

    protected void setUpEventRoute() {
        final RejectionRouting<InventoryRejectionInspectorId> routing = getRejectionRouting();
        routing.route(Rejections.NonAvailableBook.class, (message, context) -> singleton(ID));
    }
}
