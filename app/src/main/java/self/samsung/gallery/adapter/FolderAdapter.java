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
import java.io.InputStream;
import java.util.List;

import self.samsung.gallery.R;

/**
 * Adapter class for the recyclerview with files from each folder.
 * <p>
 * Created by subin on 3/17/2017.
 */

public class FolderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<String> fileNames;
    private String folderName;

    private BackToMainListener backToMainListener;

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

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

        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof FolderFileViewHolder) {
            FolderFileViewHolder vHolder = (FolderFileViewHolder) holder;
            // load image
            try {
                // get input stream
                InputStream ims = context.getAssets().open(folderName + File.separator + fileNames.get(position - 1));
                // load image as Drawable
                Drawable d = Drawable.createFromStream(ims, null);
                // set image to ImageView
                vHolder.imgv_file_image.setImageDrawable(d);
                vHolder.txtv_file_name.setText(fileNames.get(position - 1));
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

    private class FolderFileViewHolder extends RecyclerView.ViewHolder {
        public View view;
        private ImageView imgv_file_image;
        private TextView txtv_file_name;

        private FolderFileViewHolder(View itemView) {
            super(itemView);
            this.view = itemView;
            this.imgv_file_image = (ImageView) itemView.findViewById(R.id.imgv_file_image);
            this.txtv_file_name = (TextView) itemView.findViewById(R.id.txtv_file_name);
        }
    }

    private class FolderFileViewHeader extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imgv_get_back;

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

    public interface BackToMainListener {
        void onBackToMainPressed();
    }
}
