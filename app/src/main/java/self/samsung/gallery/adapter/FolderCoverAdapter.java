package self.samsung.gallery.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.List;

import self.samsung.gallery.R;
import self.samsung.gallery.model.FolderCover;
import self.samsung.gallery.peekview.PeekView;
import self.samsung.gallery.util.Util;
import self.samsung.gallery.view.SlowerViewPager;

/**
 * Adapter class written for the recycler view on the main screen with folder covers
 * <p>
 * Created by subin on 3/17/2017.
 */
public class FolderCoverAdapter extends RecyclerView.Adapter<FolderCoverAdapter.FolderCoverViewHolder> {

    private static final int VIEW_PAGER_OFFSCREEN_LIMIT = 2;
    private static final int VIEW_PAGER_SCROLL_DURATION_FACTOR = 4;
    private static final int VIEW_PAGER_MARGIN = 5;

    private final Context context;
    private final List<FolderCover> folderCovers;
    private final FolderCoverClickListener folderCoverClickListener;

    private final PeekView peekView;

    private SlowerViewPager viewPager;
    private final FolderImagePagerAdapter folderImagePagerAdapter;

    /**
     * Constructor to intialize the adapter which loads the folder covers in the main activity
     *
     * @param context                  calling context of the adapter
     * @param folderCovers             List of FolderCover to populate the list with
     * @param folderCoverClickListener Listener for FolderCoverClick
     * @param peekView                 PeekView associated with the folders in the recycler view.
     */
    public FolderCoverAdapter(Context context, List<FolderCover> folderCovers, FolderCoverClickListener folderCoverClickListener, PeekView peekView) {
        this.context = context;
        this.folderCovers = folderCovers;
        this.folderCoverClickListener = folderCoverClickListener;
        this.peekView = peekView;


        View peek = peekView.getPeekView();
        viewPager = (SlowerViewPager) peek.findViewById(R.id.pager);
        viewPager.setOffscreenPageLimit(VIEW_PAGER_OFFSCREEN_LIMIT);
        viewPager.setPageMargin(Util.convertDpToPx(context, VIEW_PAGER_MARGIN));
        this.folderImagePagerAdapter = new FolderImagePagerAdapter(peek.getContext());
        viewPager.setAdapter(folderImagePagerAdapter);
        viewPager.setScrollDurationFactor(VIEW_PAGER_SCROLL_DURATION_FACTOR);

        initPeekView();
    }

    @Override
    public FolderCoverViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.folder_cover_item, parent, false);
        return new FolderCoverViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FolderCoverViewHolder holder, int position) {
        loadImageFromAssets(context, holder, folderCovers.get(position));
        peekView.setChildViewCount(folderCovers.get(position).getFileNames().size());
        peekView.addLongClickView(holder.view, position);
    }

    /**
     * Fetches cover images from assets folder
     *
     * @param context     calling context
     * @param holder      viewHolder for folder cover
     * @param folderCover FolderCover to fetch image for
     */
    private void loadImageFromAssets(Context context, FolderCoverViewHolder holder, FolderCover folderCover) {
        try {
            StringBuilder fileNameBuilder;
            fileNameBuilder = new StringBuilder().append(folderCover.getFolderName()).append(File.separator).append(folderCover.getFileNames().get(0));
            Drawable drawable = Util.fetchDrawableFromAssets(context, fileNameBuilder.toString());

            holder.imgv_cover_foldercover.setImageDrawable(drawable);
            holder.txtv_cover_foldername.setText(folderCover.getFolderName());
        } catch (IOException ex) {
            // catch exception here
        }
    }

    @Override
    public int getItemCount() {
        return folderCovers.size();
    }

    /**
     * ViewHolder implementation for the Folder Cover View
     */
    class FolderCoverViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public final View view;
        final ImageView imgv_cover_foldercover;
        final TextView txtv_cover_foldername;

        /**
         * Constructor to initialize the viewholder
         *
         * @param view view used to intialize the components
         */
        FolderCoverViewHolder(View view) {
            super(view);
            this.view = view;
            this.imgv_cover_foldercover = (ImageView) view.findViewById(R.id.imgv_cover_foldercover);
            this.txtv_cover_foldername = (TextView) view.findViewById(R.id.txtv_cover_foldername);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            folderCoverClickListener.onItemClicked(clickedPosition);
        }
    }

    /**
     * Method used to initialize peekview over the current recycler view
     */
    private void initPeekView() {
        this.peekView.setOnPeekActionListener(new PeekView.OnPeekActionListener() {
            @Override
            public void onPeek(int position) {
                loadPeekView(position);
            }

            @Override
            public void onHide() {
                View peek = peekView.getPeekView();
                viewPager = (SlowerViewPager) peek.findViewById(R.id.pager);
                viewPager.setCurrentItem(0);
            }
        });

        this.peekView.setOnSweepAcrossListener(new PeekView.OnSweepAcrossListener() {
            @Override
            public void sweepLeft() {
                scrollToNextImage();
            }

            @Override
            public void sweepRight() {
                scrollToPreviousImage();
            }
        });
    }

    /**
     * Method used to load the peek view for a cover
     *
     * @param position position used to determine the current clicked Folder Cover
     */
    private void loadPeekView(int position) {
        folderImagePagerAdapter.setFolderCover(folderCovers.get(position));
        folderImagePagerAdapter.notifyDataSetChanged();
    }

    /**
     * Scrolls the page to the next image
     */
    private void scrollToNextImage() {
        if (viewPager.getCurrentItem() < viewPager.getAdapter().getCount() - 1) {
            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
        }
    }

    /**
     * Scrolls the image to the previous image
     */
    private void scrollToPreviousImage() {
        if (viewPager.getCurrentItem() > 0) {
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        }
    }

    /**
     * Listener interface used to notify the calling context of item clicks
     */
    public interface FolderCoverClickListener {

        /**
         * Notifies the positon of the item clicked
         *
         * @param position position of the item clicked
         */
        void onItemClicked(int position);
    }
}
