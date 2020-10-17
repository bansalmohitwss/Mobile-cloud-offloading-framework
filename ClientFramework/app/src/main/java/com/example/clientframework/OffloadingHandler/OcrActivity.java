package com.example.clientframework.OffloadingHandler;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.clientframework.R;
import com.example.clientframework.Tasks.OcrTask;
import com.yalantis.ucrop.UCrop;

import java.io.File;

public class OcrActivity extends AppCompatActivity {

    private Button offloadBtn;
    private Button closeBtn;
    private Button localBtn;
    private TextView resultTextView;

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
            this.performLocalOcr(resultUri);
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

    private void performLocalOcr(Uri uri){
        double startTime = System.nanoTime();
        StringBuilder stringBuilder = OcrTask.performTask(uri, getApplicationContext(), this.getContentResolver());
        resultTextView.setText(stringBuilder.toString());
        double duration = (System.nanoTime() - startTime)/1000000000;
        Toast.makeText(OcrActivity.this,"Successfully Received the Result \n"+"Total Time : "+duration+"sec.",Toast.LENGTH_LONG).show();
    }
    private void performCrop(Uri sourceUri, Uri destinationUri){
        UCrop.of(sourceUri, destinationUri)
                //.withAspectRatio(3, 2)
                //.withMaxResultSize(MAX_WIDTH, MAX_HEIGHT)
                .start(this);
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