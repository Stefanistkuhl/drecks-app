package at.logbait.retrofade

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.muttaapp.R
import java.io.File



class ImageSaveActivity : AppCompatActivity() {
    private lateinit var imageView: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_save)
        val bildpath = intent.getStringExtra("bild")
        val bild = File(bildpath)
        val bitmap_bild = BitmapFactory.decodeFile(bildpath)
        imageView = findViewById<ImageView>(R.id.image_view_save)
        imageView.setImageBitmap(bitmap_bild)
    }
}