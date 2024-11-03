package com.example.trojanplanner;

import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class QRCodeUtil {

    // Method to hash the text using SHA-256
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

    // Method to generate a QR code bitmap from a given text
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
