package com.example.trojanplanner.QRUtils;

import android.graphics.Bitmap;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * QRCodeUtil is a utility class for generating QR codes and hashing text.
 * This class provides methods to hash strings using SHA-256 and to generate
 * QR code bitmaps from text inputs.
 *
 * @author Dricmoy Bhattacharjee
 */
public class QRCodeUtil {

    /**
     * Hashes the provided text using the SHA-256 algorithm.
     * <p>
     * This method uses the SHA-256 cryptographic hash function to produce
     * a fixed-size (256-bit) hash value from the input text.
     * For more information about the SHA-256 algorithm, visit:
     * <a href="https://en.wikipedia.org/wiki/SHA-2">SHA-2 on Wikipedia</a>.
     *
     * @param text The input text to be hashed.
     * @return A hexadecimal representation of the hashed text, or null if an error occurs.
     *
     * @author Dricmoy Bhattacharjee
     */
    public static String hashText(String text) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(text.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString(); // Return the hashed string
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Generates a QR code bitmap from the given text.
     *
     * @param text The input text to be encoded into a QR code. It must not be null or empty.
     * @return A Bitmap representation of the generated QR code, or null if the input text is invalid
     *         or if an error occurs during generation.
     *
     * @author Dricmoy Bhattacharjee
     */
    public static Bitmap generateQRCode(String text) {
        if (text == null || text.trim().isEmpty()) {
            Log.e("QRCodeUtil", "Input text for QR code generation is empty or null.");
            return null;
        }

        try {
            // Encode the text into a QR code
            BitMatrix bitMatrix = new com.google.zxing.MultiFormatWriter().encode(
                    text,
                    BarcodeFormat.QR_CODE,
                    400, // Width of the QR code
                    400  // Height of the QR code
            );
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            return barcodeEncoder.createBitmap(bitMatrix); // Return the generated QR code as a Bitmap
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }
}
