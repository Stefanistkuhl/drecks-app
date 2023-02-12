package com.example.muttaapp;

import android.content.Context;
import android.os.Environment;
import java.io.File;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

public class CheckDate {

    public static boolean checkIfPictureExists(Context context, String filename_exists) {

        SimpleDateFormat datum = new SimpleDateFormat("dd-MM-yyyy");
        String fileName = datum + ".png";
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "MyAppImages");
        File imageFile = new File(storageDir, filename_exists);

        return Files.exists(imageFile.toPath());


    }

}
