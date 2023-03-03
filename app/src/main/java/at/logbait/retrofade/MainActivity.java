package at.logbait.retrofade;


import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PackageManagerCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.muttaapp.R;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

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



        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        List<String> permissionsToRequest = new ArrayList<>();

        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            }
        }

        if (!permissionsToRequest.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsToRequest.toArray(new String[0]), REQUEST_CODE);
        }

        btnpicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra("android.intent.extras.CAMERA_FACING", 1);
                cameraIntent.putExtra("android.intent.extras.CAMERA_FACING_FRONT", 0);
                //cameraIntent.putExtra("android.intent.extra.sizeLimit", 2097152);
                cameraIntent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
                cameraIntent.putExtra("compress", false);
                cameraIntent.putExtra("quality", 100);
                cameraIntent.putExtra("outputX", 1080);
                cameraIntent.putExtra("outputY", 1920);
                cameraIntent.putExtra("aspectX", 9);
                cameraIntent.putExtra("aspectY", 16);

                Intent chooserIntent = Intent.createChooser(cameraIntent, "Select Camera App");
                if (chooserIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(cameraIntent, REQUEST_CODE);
                }


            }

        });



        Button btnSettings = findViewById(R.id.btn_settings);
        Button btnGallery = findViewById(R.id.btn_gallery);

        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingsIntent = new Intent(MainActivity.this, Settings.class);
                startActivity(settingsIntent);
            }
        });

        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(MainActivity.this, Gallery.class);
                startActivity(galleryIntent);
            }
        });
    }
    private Bitmap mTakenImage;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode,resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(photo);

            int desiredWidth = 1080;
            int desiredHeight = 1920;

            Bitmap resizedBitmap = Bitmap.createScaledBitmap(photo, desiredWidth, desiredHeight, true);


            // Format the current date and time as a string
            //SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            SimpleDateFormat dateFormat_text = new SimpleDateFormat("dd.MM.yyyy:HH:mm");
            dateFormat_text.setTimeZone(TimeZone.getDefault());
            String folderName = "MyAppImages";
            File myDir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES), folderName);
            myDir.mkdirs();



            ImageEditor imageEditor = new ImageEditor();

            float float_height = imageEditor.getheight(resizedBitmap);
            float float_width = imageEditor.getwidth(resizedBitmap);
            float float_border_width2 = (float) (float_width * 0.21);
            float float_border_height2 = (float) (float_height * 0.45);

            int get_height = imageEditor.getheight(resizedBitmap);
            int get_width = imageEditor.getwidth(resizedBitmap);
            float float_border_width = (float) (get_width * 0.21);
            float float_border_height = (float) (get_width * 0.45);
            int border_width = (int) Math.round(float_border_width/10.0)*10;
            int border_height = (int) Math.round(float_border_height/10.0)*10;
            int text_x = border_height;
            int text_y = border_width/2;
            float text_size = (float) border_height/4;

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

            //System.out.println("selected_color ist " + mSelectedColor + " border_color ist " + border_color);
            //System.out.println("selected_text_color ist " + mSelectedtextColor + " text_color ist "+ text_color);

            ImageEditor.setImage(resizedBitmap);
            Bitmap bmpWithBorder = ImageEditor.addBorder(resizedBitmap, border_width,border_height, border_color);
            String date = dateFormat.format(new Date());
            String fileName = date + ".png";
            File storageDir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES), "MyAppImages");
            File imageFile = new File(storageDir, fileName);
            float middleX = bmpWithBorder.getWidth() / 2f;
            float middleY = bmpWithBorder.getHeight() / 2f;
            float hoehetext = (bmpWithBorder.getHeight() * -1)+border_height;
            float hoehetextv2 = border_width/2+(border_height/1.5f);
            //System.out.println("höhe text is "+hoehetext);

            Bitmap bmpWithText = ImageEditor.addText(bmpWithBorder, dateFormat_text, text_color, middleX, hoehetextv2, text_size);

            try {
                FileOutputStream fos = new FileOutputStream(imageFile);
                bmpWithText.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.close();
                //Toast.makeText(this, "Image saved!", Toast.LENGTH_SHORT).show();
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



