package at.logbait.retrofade;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.engine.Resource;
import com.example.muttaapp.R;
import java.util.List;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {

    private Context mContext;
    private List<String> mImageList;
    private int mColumnCount;

    public GalleryAdapter(Context context, List<String> imageList, int columnCount) {
        mContext = context;
        mImageList = imageList;
        mColumnCount = columnCount;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.gallery_item, parent, false);
        Log.d("GalleryAdapter", "Number of items: " + mImageList.size());


        int width = parent.getMeasuredWidth()/mColumnCount;
        view.getLayoutParams().width = width;
        int height = parent.getMeasuredHeight()/mColumnCount;
        view.getLayoutParams().width = width;
        view.getLayoutParams().height = height;


        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String imagePath = mImageList.get(position);
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        holder.mImageView.setImageBitmap(bitmap);
        holder.mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, FullscreenActivity.class);
                intent.putExtra("image_path", imagePath);
                mContext.startActivity(intent);
                Log.d("GalleryAdapter", "Image path: " + imagePath);
            }
        });


    }

    @Override
    public int getItemCount() {
        return mImageList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.image_view);
        }
    }
}