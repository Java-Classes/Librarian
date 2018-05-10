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

import com.google.common.base.Optional;
import io.spine.core.CommandContext;
import io.spine.core.React;
import io.spine.server.command.Assign;
import io.spine.server.procman.CommandRouted;
import io.spine.server.procman.ProcessManager;
import javaclasses.exlibris.InventoryId;
import javaclasses.exlibris.InventoryItem;
import javaclasses.exlibris.InventoryItemId;
import javaclasses.exlibris.InventoryItemRecognizer;
import javaclasses.exlibris.InventoryItemRecognizerId;
import javaclasses.exlibris.InventoryItemRecognizerVBuilder;
import javaclasses.exlibris.c.BorrowBook;
import javaclasses.exlibris.c.BorrowOrReturnBook;
import javaclasses.exlibris.c.QRCodeSet;
import javaclasses.exlibris.c.ReturnBook;
import javaclasses.exlibris.c.inventory.InventoryAggregate;
import javaclasses.exlibris.c.inventory.InventoryRepository;
import javaclasses.exlibris.c.rejection.ThereIsNoItemForThisToken;

import java.util.List;

import static io.spine.time.Time.getCurrentTime;

/**
 * @author Yurii Haidamaka
 */
public class InventoryItemRecognizerProcman extends ProcessManager<InventoryItemRecognizerId, InventoryItemRecognizer, InventoryItemRecognizerVBuilder> {

    public InventoryItemRecognizerProcman(InventoryItemRecognizerId id) {
        super(id);
    }

    @Assign
    CommandRouted handle(BorrowOrReturnBook cmd, CommandContext context) throws
                                                                         ThereIsNoItemForThisToken {
        if (!getState().hasInventoryItemId()) {
            throw new ThereIsNoItemForThisToken(cmd.getId().getValue(), cmd.getUserId(), getCurrentTime());
        }
        final InventoryId inventoryId = getState().getInventoryId();
        final InventoryRepository inventoryRepository = InventoryRepository.getRepository();
        final Optional<InventoryAggregate> inventoryOptional = inventoryRepository.find(
                inventoryId);
        if (!inventoryOptional.isPresent()) {
            throw new ThereIsNoItemForThisToken(cmd.getId().getValue(), cmd.getUserId(), getCurrentTime());
        }
        InventoryAggregate inventory = inventoryOptional.get();
        final InventoryItemId inventoryItemId = getState().getInventoryItemId();
        final java.util.Optional<InventoryItem> inventoryItem = getInventoryItem(inventory,
                                                                                 inventoryItemId);
        CommandRouted commandRouted;
        if (inventoryItem.get()
                         .getBorrowed()) {

            final ReturnBook returnBook = ReturnBook.newBuilder()
                                                    .setInventoryId(inventoryId)
                                                    .setInventoryItemId(inventoryItemId)
                                                    .setUserId(cmd.getUserId())
                                                    .build();
            commandRouted = newRouterFor(returnBook, context).add(returnBook)
                                                             .routeAll();
        } else {
            final BorrowBook borrowBook = BorrowBook.newBuilder()
                                                    .setInventoryId(inventoryId)
                                                    .setInventoryItemId(inventoryItemId)
                                                    .setUserId(cmd.getUserId())
                                                    .build();
            commandRouted = newRouterFor(borrowBook, context).add(borrowBook)
                                                             .routeAll();
        }

        return commandRouted;
    }

    private java.util.Optional<InventoryItem> getInventoryItem(InventoryAggregate inventory,
                                                               InventoryItemId inventoryItemId) {
        final List<InventoryItem> inventoryItemsList = inventory.getState()
                                                                .getInventoryItemsList();
        return inventoryItemsList.stream()
                                 .filter(i -> i.getInventoryItemId()
                                               .equals(inventoryItemId))
                                 .findFirst();
    }

    @React
    CommandRouted on(QRCodeSet event) {

        final InventoryItemRecognizerId id = InventoryItemRecognizerId.newBuilder()
                                                                      .setValue(
                                                                              event.getRecognizeToken())
                                                                      .build();
        getBuilder().setId(id)
                    .setInventoryId(event.getInventoryId())
                    .setInventoryItemId(event.getInventoryItemId());

        return CommandRouted.getDefaultInstance();
    }
}
