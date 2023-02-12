package com.example.muttaapp;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Environment;

import androidx.annotation.NonNull;

import java.io.FileOutputStream;
import java.io.File;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ImageEditor {
    private static Bitmap image;
    public static void setImage(Bitmap image){
        ImageEditor.image = image;
    }
    public Bitmap getImage(){
        return image;
    }
    public void loadImageWithCurrentDate(@NonNull Context context, @NonNull SimpleDateFormat dateFormat){
        String fileName = dateFormat.format(new Date()) + ".jpg";
        File file = new File(context.getFilesDir(), fileName);
        image = BitmapFactory.decodeFile(file.getAbsolutePath());
        //System.out.println(file.getAbsolutePath());
    }
    public static Bitmap addBorder(@NonNull Bitmap bmp, int borderWidth, int borderHeight, int color){
        /*
        float aspectRatio = (float) bmp.getWidth() / bmp.getHeight();
        int newWidth = bmp.getWidth() + borderSize *2;
        int newHeight = Math.round(newWidth / aspectRatio);
        Bitmap bmpWithBorder = Bitmap.createBitmap(newWidth, newHeight ,bmp.getConfig());
        */
        Bitmap bmpWithBorder = Bitmap.createBitmap(bmp.getWidth() + borderWidth * 2, bmp.getHeight() + borderHeight * 2, bmp.getConfig());
        Canvas canvas = new Canvas(bmpWithBorder);
        canvas.drawColor(color);
        canvas.drawBitmap(bmp, borderWidth, borderHeight, null);
        return bmpWithBorder;
    }
    public static Bitmap addText(@NonNull Bitmap bmpWithBorder, SimpleDateFormat text, int color){
        Bitmap bmpWithText = Bitmap.createBitmap(bmpWithBorder.getWidth(), bmpWithBorder.getHeight(), bmpWithBorder.getConfig());
        Canvas canvas = new Canvas(bmpWithText);
        canvas.drawBitmap(bmpWithBorder, 0 , 0, null);
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setTextSize(10);
        canvas.drawText(text.format(new Date()), 55, 55, paint);
        return bmpWithText;
    }
    public void saveImage(@NonNull Context context, @NonNull SimpleDateFormat dateFormat){
        String fileName = dateFormat.format(new Date()) + ".jpg";
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File file = new File(path, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
