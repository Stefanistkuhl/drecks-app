package com.example.muttaapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 22;
    Button btnpicture;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnpicture = findViewById(R.id.btncamera_id);
        imageView = findViewById(R.id.imageview);

        btnpicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent,REQUEST_CODE);

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(photo);

            // Format the current date and time as a string
            //SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

            // Create the file name
            String fileName = dateFormat.format(new Date()) + ".jpg";

            // Save the image to the phone's storage
            String savedImageURL = MediaStore.Images.Media.insertImage(
                    getContentResolver(),
                    photo,
                    fileName,
                    "Taken with Camera app"
            );

            // Check if the image was saved successfully
            if (savedImageURL == null) {
                // There was an error saving the image
                // You could display an error message or try saving the image to a different location
                return;
            }
        } else {
            Toast.makeText(this,"ABGEBROCHEN",Toast.LENGTH_SHORT).show();
            super.onActivityResult(requestCode, resultCode, data);
        }
    }




}