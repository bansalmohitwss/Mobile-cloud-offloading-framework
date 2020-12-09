package com.example.clientframework.OffloadingHandler;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.clientframework.MainActivity;
import com.example.clientframework.R;
import com.example.clientframework.Tasks.OcrTask;
import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Time;

import communication.OcrData;

public class OcrActivity extends AppCompatActivity {

    private Button offloadBtn;
    private Button closeBtn;
    private Button localBtn;
    private TextView resultTextView;
    private boolean localOcr;
    private int finalHour=-1, finalMinute=-1;

    private static final int CAMERA_REQUEST_CODE = 610;
    private static final int PICK_IMAGE_GALLERY_REQUEST_CODE = 609;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr);

        offloadBtn = (Button)findViewById(R.id.offloadBtn);
        closeBtn = (Button)findViewById(R.id.close);
        localBtn = (Button)findViewById(R.id.localBtn);
        resultTextView = (TextView)findViewById(R.id.textView4);
        resultTextView.setMovementMethod(new ScrollingMovementMethod());

        offloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                localOcr = false;
                resultTextView.setText("");
                isReadStoragePermissionGranted();
                isWriteStoragePermissionGranted();
                selectImage(OcrActivity.this);
            }
        });



        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OcrActivity.this, OffloadTask.class);
                startActivity(intent);
                finish();
            }
        });

        localBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                localOcr = true;
                resultTextView.setText("");
                isReadStoragePermissionGranted();
                isWriteStoragePermissionGranted();
                selectImage(OcrActivity.this);
            }
        });
    }

    private void selectImage(final Context context) {
        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose your profile picture");

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Take Photo")) {
                    Intent m_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File file = new File(Environment.getExternalStorageDirectory(), "MyPhoto.jpg");
                    Uri uri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file);
                    m_intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, uri);
                    startActivityForResult(m_intent, CAMERA_REQUEST_CODE);

                } else if (options[item].equals("Choose from Gallery")) {
                    Log.v("","Inside");
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(Intent.createChooser(intent, "Select a picture for your profile"), PICK_IMAGE_GALLERY_REQUEST_CODE);
                    }

                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            final Uri resultUri = UCrop.getOutput(data);
            this.performOcr(resultUri);
        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
        }
        else if(requestCode == PICK_IMAGE_GALLERY_REQUEST_CODE && resultCode == RESULT_OK) {
            final Uri sourceUri = data.getData();
            File tempCropped = new File(getCacheDir(), "tempImgCropped.png");
            Uri destinationUri = Uri.fromFile(tempCropped);
            this.performCrop(sourceUri, destinationUri);
        }
        else if(requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK){
            File file = new File(Environment.getExternalStorageDirectory(), "MyPhoto.jpg");
            Uri sourceUri = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", file);
            Log.i("Picture Taken",""+sourceUri);
            File tempCropped = new File(getCacheDir(), "tempImgCropped.png");
            Uri destinationUri = Uri.fromFile(tempCropped);
            this.performCrop(sourceUri, destinationUri);
        }
    }

    private void performOcr(Uri uri){
        Bitmap bitmap = this.convertUri(uri, this.getContentResolver());
        if(localOcr){
            this.performLocalOcr(bitmap);
        }else {
            this.performOffloadOcr(bitmap);
        }
    }

    private void performOffloadOcr(Bitmap bitmap){
        TimePickerDialog timePickerDialog = new TimePickerDialog(OcrActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                finalHour = i;
                finalMinute = i1;
            }
        }, 12, 0, true);
        timePickerDialog.updateTime(new Time(System.currentTimeMillis()).getHours(),
                new Time(System.currentTimeMillis()).getMinutes());
        timePickerDialog.show();
        Log.i("TimePickerDialog ", "Inside performOffloadOcr");
        double startTime = System.nanoTime();
        byte[] image = this.getBytesFromBitmap(bitmap);
        OcrData ocrData = new OcrData(MainActivity.OCR_TASK_REGISTRY,finalHour, finalMinute,image,null);
        OffloadingThread offloadingThread = new OffloadingThread((Object)ocrData);
        offloadingThread.start();

        synchronized (offloadingThread)
        {
            try {
                offloadingThread.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        ocrData = (OcrData)offloadingThread.getReceiveData();
        if(ocrData==null || ocrData.getType() != MainActivity.SUBMIT_RESULT){
            Toast.makeText(OcrActivity.this,"Some Error Occurred",Toast.LENGTH_LONG).show();
        }
        else{
            double duration = (System.nanoTime() - startTime)/1000000000;
            resultTextView.setText(ocrData.getResultText());
            Toast.makeText(OcrActivity.this,"Successfully Received the Result \n"+"Total Time : "+duration+"sec.",Toast.LENGTH_LONG).show();
        }
    }

    private void performLocalOcr(Bitmap bitmap){
        double startTime = System.nanoTime();
        String string = OcrTask.performTask(bitmap, getApplicationContext());
        resultTextView.setText(string);
        double duration = (System.nanoTime() - startTime)/1000000000;
        Toast.makeText(OcrActivity.this,"Successfully Received the Result \n"+"Total Time : "+duration+"sec.",Toast.LENGTH_LONG).show();
    }
    private void performCrop(Uri sourceUri, Uri destinationUri){
        UCrop.of(sourceUri, destinationUri)
                //.withAspectRatio(3, 2)
                //.withMaxResultSize(MAX_WIDTH, MAX_HEIGHT)
                .start(this);
    }

    private Bitmap convertUri(Uri uri, ContentResolver contentResolver){
        Bitmap bitmap;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return bitmap;
    }

    // convert from bitmap to byte array
    private byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        return stream.toByteArray();
    }

    public  boolean isReadStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("x","Permission is granted1");
                return true;
            } else {
                Log.v("x","Permission is revoked1");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
                return false;
            }
        }
        else {
            Log.v("","Permission is granted1");
            return true;
        }
    }

    public  boolean isWriteStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("x","Permission is granted2");
                return true;
            } else {
                Log.v("x","Permission is revoked2");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                return false;
            }
        }
        else {
            Log.v("","Permission is granted2");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}