package self.samsung.gallery.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import self.samsung.gallery.R;
import self.samsung.gallery.model.FolderCover;

/**
 * Pager Adapter written for the paging animation of the images.
 * <p>
 * Created by subin on 3/17/2017.
 */
class CustomPagerAdapter extends PagerAdapter {

    private FolderCover folderCover;
    private Context context;
    private LayoutInflater layoutInflater;

    CustomPagerAdapter(Context context, FolderCover folderCover) {
        this.context = context;
        this.folderCover = folderCover;
        layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        if (folderCover == null)
            return 0;
        else
            return folderCover.getFileNames().size();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = layoutInflater.inflate(R.layout.item_pager, container, false);

        ImageView imageView = (ImageView) itemView.findViewById(R.id.image);
        loadImage(imageView, position);

        container.addView(itemView);

        return itemView;
    }

    private void loadImage(ImageView imageView, int position) {
        // load image
        try {
            // get input stream
            InputStream ims = context.getAssets().open(folderCover.getFolderName() + File.separator + folderCover.getFileNames().get(position));
            // load image as Drawable
            Drawable d = Drawable.createFromStream(ims, null);
            // set image to ImageView
            imageView.setImageDrawable(d);
        } catch (IOException ex) {
            //catch exception here
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    void setDemoObject(FolderCover demoObject) {
        this.folderCover = demoObject;
    }
}