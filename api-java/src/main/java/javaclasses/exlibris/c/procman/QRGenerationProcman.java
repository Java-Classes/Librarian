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

import io.spine.core.CommandContext;
import io.spine.core.EventContext;
import io.spine.core.React;
import io.spine.server.procman.CommandRouted;
import io.spine.server.procman.CommandRouter;
import io.spine.server.procman.ProcessManager;
import javaclasses.exlibris.InventoryId;
import javaclasses.exlibris.InventoryItemId;
import javaclasses.exlibris.InventoryItemRecognizeToken;
import javaclasses.exlibris.QRCodeImageURL;
import javaclasses.exlibris.QRGeneration;
import javaclasses.exlibris.QRGenerationId;
import javaclasses.exlibris.QRGenerationService;
import javaclasses.exlibris.QRGenerationVBuilder;
import javaclasses.exlibris.c.InventoryAppended;
import javaclasses.exlibris.c.SetBookQRCode;

public class QRGenerationProcman extends ProcessManager<QRGenerationId, QRGeneration, QRGenerationVBuilder> {
    /**
     * As long as the {@code QRGenerationProcman} is an {@code InventoryAggregate}
     * service and does not hold model state it is a singleton. All subscribed events
     * are routed to the single instance.
     */
    protected static final QRGenerationId ID = QRGenerationId.newBuilder()
                                                             .setValue("QRGenerationSingleton")
                                                             .build();

    /**
     * Creates a new instance.
     *
     * @param id an ID for the new instance
     * @throws IllegalArgumentException if the ID type is unsupported
     */
    protected QRGenerationProcman(QRGenerationId id) {
        super(id);
    }

    @React
    CommandRouted on(InventoryAppended event, EventContext eventContext) {
        final InventoryId inventoryId = event.getInventoryId();
        final CommandContext commandContext = eventContext.getCommandContext();
        final InventoryItemId inventoryItemId = event.getInventoryItemId();
        final String recognizeToken = inventoryId.getBookId()
                                                 .getIsbn62()
                                                 .getValue() + "/" +
                inventoryItemId.getItemNumber();
        String filePath = "D:\\qr\\" + recognizeToken + ".png";
        QRGenerationService.generateQRCode(recognizeToken, filePath);

        final QRCodeImageURL qrCodeImageURL = QRCodeImageURL.newBuilder()
                                                            .setValue(filePath)
                                                            .build();
        final InventoryItemRecognizeToken itemRecognizeToken =
                InventoryItemRecognizeToken.newBuilder()
                                           .setValue(recognizeToken)
                                           .build();
        final SetBookQRCode setBookQRCode = SetBookQRCode.newBuilder()
                                                         .setInventoryId(inventoryId)
                                                         .setInventoryItemId(inventoryItemId)
                                                         .setQrImageUrl(qrCodeImageURL)
                                                         .setRecognizeToken(itemRecognizeToken)
                                                         .build();
        final CommandRouter router = newRouterFor(setBookQRCode, commandContext).add(setBookQRCode);
        return router.routeAll();
    }
}
