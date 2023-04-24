package at.logbait.retrofade;

import androidx.appcompat.app.AppCompatActivity;

import com.camerakit.CameraKitView;
import com.example.muttaapp.R;


import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;

public class Camera_besser extends AppCompatActivity {
    private CameraKitView cameraKitView;
    Button foto_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_besser);
        cameraKitView = findViewById(R.id.camera);
        foto_btn = findViewById(R.id.foto_btn);

        foto_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                SimpleDateFormat datum = new SimpleDateFormat("dd-MM-yyyy");
                String dateiname = datum + ".jpg";
                cameraKitView.captureImage(new CameraKitView.ImageCallback() {
                    @Override
                    public void onImage(CameraKitView cameraKitView, final byte[] capturedImage) {
                        File savedPhoto = new File(Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_PICTURES), dateiname);
                        try {
                            FileOutputStream outputStream = new FileOutputStream(savedPhoto.getPath());
                            outputStream.write(capturedImage);
                            outputStream.close();
                            Toast.makeText(Camera_besser.this, "Image captured successfully", Toast.LENGTH_SHORT).show();
                        } catch (java.io.IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        cameraKitView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraKitView.onResume();
    }

    @Override
    protected void onPause() {
        cameraKitView.onPause();
        super.onPause();
    }

    @Override
    protected void onStop() {
        cameraKitView.onStop();
        super.onStop();
    }




    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        cameraKitView.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}