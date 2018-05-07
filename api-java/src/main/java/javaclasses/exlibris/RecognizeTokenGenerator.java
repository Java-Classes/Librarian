package javaclasses.exlibris;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * The utility class for the {@code InventoryItem} identifying token generation.
 *
 * @author Yegor Udovchenko
 */
public class RecognizeTokenGenerator {

    /**
     * Creates the encrypted token for the specified inventory item.
     *
     * Uses `MD5` algorithm for encryption. The source string is composed of the {@link Isbn62}
     * and the {@link InventoryItem} sequential number. Both values are taken from the passed
     * {@link InventoryItemId} property.
     *
     * @param inventoryItemId the identifier of inventory item
     * @return {@code InventoryItemRecognizeToken} the inventory item recognize token
     */
    public InventoryItemRecognizeToken generateRecognizeToken(InventoryItemId inventoryItemId) {
        final String isbn62Value = inventoryItemId.getBookId()
                                                  .getIsbn62()
                                                  .getValue();
        final int itemSequentialNumber = inventoryItemId.getItemNumber();

        final String sourceToken = isbn62Value + String.valueOf(itemSequentialNumber);
        MessageDigest md5 = null;

        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        md5.update(StandardCharsets.UTF_8.encode(sourceToken));
        final String encryptedToken = String.format("%032x", new BigInteger(1, md5.digest()));
        final InventoryItemRecognizeToken itemRecognizeToken =
                InventoryItemRecognizeToken.newBuilder()
                                           .setValue(encryptedToken)
                                           .build();
        return itemRecognizeToken;
    }
}
