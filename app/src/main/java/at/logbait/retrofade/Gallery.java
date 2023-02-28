package at.logbait.retrofade;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import androidx.recyclerview.widget.GridLayoutManager;


import com.example.muttaapp.R;

import java.util.List;
import java.util.stream.Stream;

public class Gallery extends AppCompatActivity {

    private List<String> mImageList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/MyAppImages/";

        Log.d("Gallery", "Path: " + path);

        // Get a list of image file paths from the specified directory
        File directory = new File(path);
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            Log.d("Gallery", "files arrray" + Arrays.toString(files));
            assert files != null;
            int numFiles = files.length;
            Log.d("Gallery", "Number of image files found: " + numFiles);
            if (files != null && files.length > 0) {
                for (File file : files) {
                    if (file.isFile() && isImageFile(file.getName())) {
                        mImageList.add(file.getAbsolutePath());
                        Log.d("Gallery", "Added file: " + file.getAbsolutePath());
                    }
                }
            }
        }
        for (String imagePath : mImageList) {
            Log.d("Gallery", "Image path: " + imagePath);
        }
        // Set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        GalleryAdapter adapter = new GalleryAdapter(this, mImageList, 5);
        recyclerView.setAdapter(adapter);

    }

        private boolean isImageFile(String fileName) {
            String extension = fileName.substring(fileName.lastIndexOf(".") + 1);
            //return extension.equalsIgnoreCase("jpg") || extension.equalsIgnoreCase("jpeg") || extension.equalsIgnoreCase("png") || extension.equalsIgnoreCase("gif") || extension.equalsIgnoreCase("bmp");
            boolean isImage = extension.equalsIgnoreCase("jpg") || extension.equalsIgnoreCase("jpeg") || extension.equalsIgnoreCase("png") || extension.equalsIgnoreCase("gif") || extension.equalsIgnoreCase("bmp");
            Log.d("Gallery", "File: " + fileName + ", isImage: " + isImage);
            return isImage;
    }
}

