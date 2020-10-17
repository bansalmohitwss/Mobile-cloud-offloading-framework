package com.example.clientframework.Tasks;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.IOException;

public class OcrTask {
    public static StringBuilder performTask(Uri uri, Context context, ContentResolver contentResolver) {
        StringBuilder stringBuilder = new StringBuilder();
        Bitmap bitmap;

        try {
            bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri);
        } catch (IOException e) {
            e.printStackTrace();
            return stringBuilder;
        }

        TextRecognizer textRecognizer = new TextRecognizer.Builder(context).build();
        if (!textRecognizer.isOperational())
            Log.e("ERROR", "Detector dependencies are not yet available");
        else {
            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
            SparseArray<TextBlock> items = textRecognizer.detect(frame);
            for (int i = 0; i < items.size(); ++i) {
                TextBlock item = items.valueAt(i);
                stringBuilder.append(item.getValue());
                stringBuilder.append("\n");
            }
            Log.i("Data", stringBuilder.toString());
        }
        return stringBuilder;
    }
}
