package cs213.androidapp91;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;

public class ImageAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Bitmap> bitmap;

    public ImageAdapter(Context ctx, ArrayList<Bitmap> bitmap) {
        this.context = ctx;
        this.bitmap = bitmap;
    }

    public ArrayList<Bitmap> getBitmap() {
        return bitmap;
    }

    public int getCount() {
        return bitmap.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;

        if (convertView == null) {
            imageView = new ImageView(context);
            imageView.setLayoutParams(new GridView.LayoutParams(300, 300));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        }
        else {
            imageView = (ImageView) convertView;
        }
        //imageView.setImageResource(mThumbIds[position]);
        imageView.setImageBitmap(bitmap.get(position));

        return imageView;
    }
}

