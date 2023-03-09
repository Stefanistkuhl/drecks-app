package at.logbait.retrofade;

import android.view.View;
import android.widget.ImageView;
import com.example.muttaapp.R;
import com.jsibbold.zoomage.ZoomageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class GalleryViewHolder extends RecyclerView.ViewHolder {

    public ZoomageView zoomageView;


    public GalleryViewHolder(@NonNull View itemView) {
        super(itemView);
        zoomageView = itemView.findViewById(R.id.image_view);
    }

    public void bind(int imageResId){
        zoomageView.setImageResource(imageResId);
    }

}
