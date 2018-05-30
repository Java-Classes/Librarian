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

import io.spine.core.React;
import io.spine.server.procman.ProcessManager;
import javaclasses.exlibris.InventoryRejectionInspector;
import javaclasses.exlibris.InventoryRejectionInspectorId;
import javaclasses.exlibris.InventoryRejectionInspectorVBuilder;
import javaclasses.exlibris.c.BookWasNotBorrowed;

import static javaclasses.exlibris.c.rejection.Rejections.BookAlreadyBorrowed;
import static javaclasses.exlibris.c.rejection.Rejections.NonAvailableBook;

/**
 * @author Yurii Haidamaka
 */
public class InventoryRejectionInspectorProcman extends ProcessManager<InventoryRejectionInspectorId, InventoryRejectionInspector, InventoryRejectionInspectorVBuilder> {

    public static final InventoryRejectionInspectorId ID = InventoryRejectionInspectorId.newBuilder()
                                                                                        .setValue(
                                                                                                "InventoryRejectionInspectorProcman")
                                                                                        .build();

    public InventoryRejectionInspectorProcman(InventoryRejectionInspectorId id) {
        super(id);
    }

    @React
    BookWasNotBorrowed on(NonAvailableBook rejection) {
        final String cause = "This book is reserved by someone";
        final BookWasNotBorrowed event = BookWasNotBorrowed.newBuilder()
                                                           .setInventoryId(
                                                                   rejection.getInventoryId())
                                                           .setUserId(rejection.getUserId())
                                                           .setCause(cause)
                                                           .setWhenEmitted(
                                                                   rejection.getWhenRejected())
                                                           .build();

        return event;
    }

    @React
    BookWasNotBorrowed on(BookAlreadyBorrowed rejection) {
        final String cause = "This book is borrowed by someone";
        final BookWasNotBorrowed event = BookWasNotBorrowed.newBuilder()
                                                           .setInventoryId(
                                                                   rejection.getInventoryId())
                                                           .setUserId(rejection.getUserId())
                                                           .setCause(cause)
                                                           .setWhenEmitted(
                                                                   rejection.getWhenRejected())
                                                           .build();

        return event;
    }

}
