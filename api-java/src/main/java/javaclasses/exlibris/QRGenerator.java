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

package javaclasses.exlibris;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;

/**
 * The utility class for the QR code generation.
 *
 * @author Yegor Udovchenko
 */
public class QRGenerator {

    /**
     * The QR code image border size.
     */
    private static final int QR_BORDER_SIZE = 250;

    private static final String FILE_TYPE = "png";

    private static final String FILE_NAME_PATTERN = "%s_%d." + FILE_TYPE;

    // TODO 5/7/2018[yegor.udovchenko]: Find out should it be the FileSystem path or not.
    private static final String FILE_ROOT_PATH = "D:\\qr\\";

    /**
     * Creates the QR code image file for an inventory item.
     *
     * @param recognizeToken  the unique token to identify inventory item
     * @param inventoryItemId the identifier of inventory item
     * @return {@code QRCodeImageURL} the URL of the QR code image file
     */
    public QRCodeImageURL generateQRCode(InventoryItemRecognizeToken recognizeToken,
                                         InventoryItemId inventoryItemId) {
        final String isbn62Value = inventoryItemId.getBookId()
                                                  .getIsbn62()
                                                  .getValue();
        final int itemNumber = inventoryItemId.getItemNumber();

        final String fileName = String.format(FILE_NAME_PATTERN, isbn62Value, itemNumber);
        final String filePath = FILE_ROOT_PATH + fileName;

        // TODO 5/7/2018[yegor.udovchenko]: Find out how to form URL.
        final String sourceURL = "exlibris/book-qr-scan/" + recognizeToken.getValue();
        final File resultFile = new File(filePath);

        try {

            final Map<EncodeHintType, Object> hintMap = new EnumMap<EncodeHintType, Object>(
                    EncodeHintType.class);
            hintMap.put(EncodeHintType.CHARACTER_SET, "UTF-8");

            hintMap.put(EncodeHintType.MARGIN, 1);
            hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);

            final QRCodeWriter qrCodeWriter = new QRCodeWriter();
            final BitMatrix byteMatrix = qrCodeWriter.encode(sourceURL,
                                                             BarcodeFormat.QR_CODE,
                                                             QR_BORDER_SIZE,
                                                             QR_BORDER_SIZE,
                                                             hintMap);
            final int crunchifyWidth = byteMatrix.getWidth();
            final BufferedImage image = new BufferedImage(crunchifyWidth, crunchifyWidth,
                                                          BufferedImage.TYPE_INT_RGB);
            image.createGraphics();

            final Graphics2D graphics = (Graphics2D) image.getGraphics();
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, crunchifyWidth, crunchifyWidth);
            graphics.setColor(Color.BLACK);

            for (int i = 0; i < crunchifyWidth; i++) {
                for (int j = 0; j < crunchifyWidth; j++) {
                    if (byteMatrix.get(i, j)) {
                        graphics.fillRect(i, j, 1, 1);
                    }
                }
            }
            ImageIO.write(image, FILE_TYPE, resultFile);
        } catch (WriterException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // TODO 5/7/2018[yegor.udovchenko]: find out how to generate.
        final QRCodeImageURL qrCodeImageURL = QRCodeImageURL.newBuilder()
                                                            .setValue(
                                                                    "The url of the stored QR code image file.")
                                                            .build();
        return qrCodeImageURL;
    }
}
