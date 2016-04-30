package cs213.androidapp91;

/**
 * Created by m95ch on 4/26/2016.
 */
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

public class vImageAdapter extends PagerAdapter {
    private Context context;
    private ArrayList<Bitmap> bitmap;

    public vImageAdapter(Context ctx, ArrayList<Bitmap> bitmap) {
        this.context = ctx;
        this.bitmap = bitmap;
    }

    @Override
    public int getCount() {
        return bitmap.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((ImageView) object);
    }

    @Override public Object instantiateItem(ViewGroup container, int position) {
        ImageView imageView = new ImageView(context);
        imageView.setImageBitmap(bitmap.get(position));
        ((ViewPager) container).addView(imageView, 0);
        return imageView;
    }

    @Override public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((ImageView) object);
    }
}
