package self.samsung.gallery.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import self.samsung.gallery.R;
import self.samsung.gallery.model.FolderCover;
import self.samsung.gallery.peekview.PeekView;

/**
 * Adapter class written for the recycler view on the main screen with folder covers
 * <p>
 * Created by subin on 3/17/2017.
 */

public class FolderCoverAdapter extends RecyclerView.Adapter<FolderCoverAdapter.FolderCoverViewHolder> {

    private Context context;
    private List<FolderCover> folderCovers;
    private FolderCoverClickListener folderCoverClickListener;

    private PeekView peekView;

    private ViewPager viewPager;
    private CustomPagerAdapter customPagerAdapter;

    public FolderCoverAdapter(Context context, List<FolderCover> folderCovers, FolderCoverClickListener folderCoverClickListener, PeekView peekView) {
        this.context = context;
        this.folderCovers = folderCovers;
        this.folderCoverClickListener = folderCoverClickListener;
        this.peekView = peekView;


        View peek = peekView.getPeekView();
        viewPager = (ViewPager) peek.findViewById(R.id.pager);
        viewPager.setOffscreenPageLimit(2);
        this.customPagerAdapter = new CustomPagerAdapter(peek.getContext(), null);
        viewPager.setAdapter(customPagerAdapter);

        setupPeekAndPopStandard();
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
        peekView.addLongClickView(holder.view, position);
    }

    private void loadImageFromAssets(Context context, FolderCoverViewHolder holder, FolderCover folderCover) {
        // load image
        try {
            // get input stream
            InputStream ims = context.getAssets().open(folderCover.getFolderName() + File.separator + folderCover.getFileNames().get(0));
            // load image as Drawable
            Drawable d = Drawable.createFromStream(ims, null);
            // set image to ImageView
            holder.imgv_cover_foldercover.setImageDrawable(d);
            holder.txtv_cover_foldername.setText(folderCover.getFolderName());
        } catch (IOException ex) {
            // catch exception here
        }
    }

    @Override
    public int getItemCount() {
        return folderCovers.size();
    }

    class FolderCoverViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public View view;
        ImageView imgv_cover_foldercover;
        TextView txtv_cover_foldername;

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

    public interface FolderCoverClickListener {
        void onItemClicked(int position);
    }

    private void setupPeekAndPopStandard() {
        this.peekView.setOnGeneralActionListener(new PeekView.OnGeneralActionListener() {
            @Override
            public void onPeek(View view, int position) {
                loadPeekAndPop(position);
            }

            @Override
            public void onHide(View view, int position) {
            }
        });

        this.peekView.setOnSweepAcrossListener(new PeekView.OnSweepAcrossListener() {
            @Override
            public void sweepLeft(int position) {
                scrollToNextImage(position);
            }

            @Override
            public void sweepRight(int position) {
                scrollToPreviousImage(position);
            }
        });
    }

    private void loadPeekAndPop(int position) {
        customPagerAdapter.setDemoObject(folderCovers.get(position));
        customPagerAdapter.notifyDataSetChanged();
    }

    private void scrollToNextImage(int position) {
        folderCovers.get(position).nextImage();
        if (viewPager.getCurrentItem() < viewPager.getAdapter().getCount() - 1) {
            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
        }
    }

    private void scrollToPreviousImage(int position) {
        folderCovers.get(position).previousImage();
        if (viewPager.getCurrentItem() > 0) {
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        }
    }
}
