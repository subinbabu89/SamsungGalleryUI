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
import self.samsung.gallery.util.Util;

/**
 * Adapter class for the recyclerview with files from each folder.
 * <p>
 * Created by subin on 3/17/2017.
 */
public class FolderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    private final List<String> fileNames;
    private final String folderName;

    private final BackToMainListener backToMainListener;

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    /**
     * Constructor to initialize the adapter
     *
     * @param context            calling context of the adapter
     * @param fileNames          List of names for files in the particular folder
     * @param folderName         name of the folder
     * @param backToMainListener Listener interface to navigate back to the parent activity
     */
    public FolderAdapter(Context context, List<String> fileNames, String folderName, BackToMainListener backToMainListener) {
        this.context = context;
        this.fileNames = fileNames;
        this.folderName = folderName;
        this.backToMainListener = backToMainListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.file_item, parent, false);
            return new FolderFileViewHolder(view);
        } else if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.file_item_header, parent, false);
            return new FolderFileViewHeader(view);
        }

        throw new RuntimeException(context.getString(R.string.no_matching_type_exception_message, viewType));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof FolderFileViewHolder) {
            FolderFileViewHolder vHolder = (FolderFileViewHolder) holder;
            try {
                String filename = fileNames.get(position - 1);

                StringBuilder fileNameBuilder;
                fileNameBuilder = new StringBuilder().append(folderName).append(File.separator).append(filename);
                Drawable drawable = Util.fetchDrawableFromAssets(context, fileNameBuilder.toString());

                vHolder.imgv_file_image.setImageDrawable(drawable);
                vHolder.txtv_file_name.setText(filename);
            } catch (IOException ex) {
                //catch exception here
            }
        }
    }

    @Override
    public int getItemCount() {
        return fileNames.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return TYPE_HEADER;
        return TYPE_ITEM;
    }

    /**
     * ViewHolder implementation for the files view
     */
    private class FolderFileViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imgv_file_image;
        private final TextView txtv_file_name;

        /**
         * Constructor to initialize the viewholder
         *
         * @param itemView view used to intialize the components
         */
        private FolderFileViewHolder(View itemView) {
            super(itemView);
            this.imgv_file_image = (ImageView) itemView.findViewById(R.id.imgv_file_image);
            this.txtv_file_name = (TextView) itemView.findViewById(R.id.txtv_file_name);
        }
    }

    /**
     * ViewHolder implementation for the header view
     */
    private class FolderFileViewHeader extends RecyclerView.ViewHolder implements View.OnClickListener {
        final ImageView imgv_get_back;

        /**
         * Constructor to initialize the viewholder
         *
         * @param itemView view used to intialize the components
         */
        FolderFileViewHeader(View itemView) {
            super(itemView);
            this.imgv_get_back = (ImageView) itemView.findViewById(R.id.imgv_get_back);
            this.imgv_get_back.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            backToMainListener.onBackToMainPressed();
        }
    }

    /**
     * Interface listeners to navigate back to the parent activity
     */
    public interface BackToMainListener {

        /**
         * Navigate to parent activity of the current calling context
         */
        void onBackToMainPressed();
    }
}
