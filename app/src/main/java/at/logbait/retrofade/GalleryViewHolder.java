package at.logbait.retrofade;

import android.view.View;
import android.widget.ImageView;
import com.example.muttaapp.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class GalleryViewHolder extends RecyclerView.ViewHolder {

    public ImageView imageView;


    public GalleryViewHolder(@NonNull View itemView) {
        super(itemView);
        imageView = itemView.findViewById(R.id.image_view);
    }

    public void bind(int imageResId){
        imageView.setImageResource(imageResId);
    }

}
