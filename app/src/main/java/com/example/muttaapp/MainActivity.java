package com.example.muttaapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 22;
    Button btnpicture;
    ImageView imageView;

    private String mSelectedColor;
    private String mSelectedtextColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnpicture = findViewById(R.id.btncamera_id);
        imageView = findViewById(R.id.imageview);

        SharedPreferences sharedPref = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        mSelectedColor = sharedPref.getString("selected_color", null);

        SharedPreferences sharedPref_text = getSharedPreferences("user_text_prefs", Context.MODE_PRIVATE);
        mSelectedtextColor = sharedPref_text.getString("select_text_color", null);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date date = new Date();
        String filename_exists = dateFormat.format(date) + ".png";
       //System.out.println(filename_exists);
        TextView editText = (TextView) findViewById(R.id.TextView_check);
        if (CheckDate.checkIfPictureExists(this, filename_exists)) {
            editText.setText("Image today already taken");
        } else {
            editText.setText("Not an Image taken today");
        }

        btnpicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, REQUEST_CODE);

            }

        });



        Button btnSettings = findViewById(R.id.btn_settings);
        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingsIntent = new Intent(MainActivity.this, Settings.class);
                startActivity(settingsIntent);
            }
        });
    }
    private Bitmap mTakenImage;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(photo);


            // Format the current date and time as a string
            //SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            SimpleDateFormat dateFormat_text = new SimpleDateFormat("dd.MM.yyyy:hh:ss");
            String folderName = "MyAppImages";
            File myDir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES), folderName);
            myDir.mkdirs();



            ImageEditor imageEditor = new ImageEditor();

            int get_height = imageEditor.getheight(photo);
            int get_width = imageEditor.getwidth(photo);
            float float_border_width = (float) (get_width * 0.21);
            float float_border_height = (float) (get_width * 0.45);
            int border_width = (int) Math.round(float_border_width/10.0)*10;
            int border_height = (int) Math.round(float_border_height/10.0)*10;
            int text_x = border_height;
            int text_y = border_width/2;
            //System.out.println(border_width);
            int border_color = Color.BLACK;

            if (mSelectedColor != null){
                if(mSelectedColor.equals("Black")){
                    border_color = Color.BLACK;
                }else if(mSelectedColor.equals("White")){
                    border_color = Color.WHITE;
                }

            }

            int text_color = Color.WHITE;

            if (mSelectedtextColor != null){
                if(mSelectedtextColor.equals("Black")){
                    text_color = Color.BLACK;
                }else if(mSelectedtextColor.equals("White")){
                    text_color = Color.WHITE;
                }

            }

            System.out.println("selected_color ist " + mSelectedColor + " border_color ist " + border_color);
            System.out.println("selected_text_color ist " + mSelectedtextColor + " text_color ist "+ text_color);

            ImageEditor.setImage(photo);
            Bitmap bmpWithBorder = ImageEditor.addBorder(photo, border_width,border_height, border_color);
            String date = dateFormat.format(new Date());
            String fileName = date + ".png";
            File storageDir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES), "MyAppImages");
            File imageFile = new File(storageDir, fileName);
            Bitmap bmpWithText = ImageEditor.addText(bmpWithBorder, dateFormat_text, text_color);

            try {
                FileOutputStream fos = new FileOutputStream(imageFile);
                bmpWithText.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.close();
                Toast.makeText(this, "Image saved!", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, "Failed to save image!", Toast.LENGTH_SHORT).show();
            }


            imageView.setImageBitmap(bmpWithText);
            imageEditor.saveImage(this, dateFormat);

        } else {
            Toast.makeText(this, "ABGEBROCHEN", Toast.LENGTH_SHORT).show();
            super.onActivityResult(requestCode, resultCode, data);


        }
    }
}



