package com.example.trojanplanner.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;


public class SerialBitmap implements Serializable {
    private byte[] byteArray;


    // Constructor to wrap Bitmap
    public SerialBitmap(Bitmap bitmap) {
        setBitmap(bitmap);
    }

    // Setter to set Bitmap
    public void setBitmap(Bitmap bitmap) {
        if (bitmap != null) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            this.byteArray = byteArrayOutputStream.toByteArray();
        }
        else {
            this.byteArray = null;
        }

    }

    // Getter to retrieve Bitmap
    public Bitmap getBitmap() {
        if (this.byteArray != null) {
            return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        }
        else {
            return null;
        }
    }

    // Serialize the object
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeInt(byteArray.length);
        out.write(byteArray);
    }

    // Deserialize the object
    private void readObject(ObjectInputStream in) throws IOException {
        int length = in.readInt();
        byteArray = new byte[length];
        in.readFully(byteArray);
    }


}
