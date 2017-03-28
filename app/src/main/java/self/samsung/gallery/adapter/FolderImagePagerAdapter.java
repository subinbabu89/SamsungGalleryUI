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

import self.samsung.gallery.R;
import self.samsung.gallery.model.FolderCover;
import self.samsung.gallery.util.Util;

/**
 * Pager Adapter written for the paging animation of the images.
 * <p>
 * Created by subin on 3/17/2017.
 */
class FolderImagePagerAdapter extends PagerAdapter {

    private FolderCover folderCover;
    private final Context context;
    private final LayoutInflater layoutInflater;

    /**
     * Method used to intialize the pager adapter
     *
     * @param context calling context
     */
    FolderImagePagerAdapter(Context context) {
        this.context = context;
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

    /**
     * Method used to load the images from assets folders
     *
     * @param imageView imageView to load the image to
     * @param position  position for image to be loaded in the filename list
     */
    private void loadImage(ImageView imageView, int position) {
        try {
            StringBuilder fileNameBuilder;
            fileNameBuilder = new StringBuilder().append(folderCover.getFolderName()).append(File.separator).append(folderCover.getFileNames().get(position));
            Drawable drawable = Util.fetchDrawableFromAssets(context, fileNameBuilder.toString());

            imageView.setImageDrawable(drawable);
        } catch (IOException ex) {
            //catch exception here
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    /**
     * Method used to initalize the FolderCover for the pager.
     *
     * @param folderCover FolderCover to load the images in the loader to
     */
    void setFolderCover(FolderCover folderCover) {
        this.folderCover = folderCover;
    }
}