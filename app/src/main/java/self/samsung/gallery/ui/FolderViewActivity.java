package self.samsung.gallery.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import self.samsung.gallery.R;
import self.samsung.gallery.adapter.FolderAdapter;
import self.samsung.gallery.util.Util;

/**
 * Activity with files from each activity
 * <p>
 * Created by subin on 3/17/2017.
 */
public class FolderViewActivity extends AppCompatActivity implements FolderAdapter.BackToMainListener {

    private String parentFolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent().getExtras() != null) {
            if (getIntent().hasExtra(Intent.EXTRA_TEXT)) {
                parentFolder = getIntent().getStringExtra(Intent.EXTRA_TEXT);
            }
        }
        setContentView(R.layout.activity_folder_view);
        this.overridePendingTransition(R.anim.anim_slide_in_left,
                R.anim.anim_slide_out_left);
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        RecyclerView folderRecyclerView = (RecyclerView) findViewById(R.id.file_names_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        folderRecyclerView.setLayoutManager(linearLayoutManager);
        folderRecyclerView.setHasFixedSize(true);

        FolderAdapter folderAdapter = new FolderAdapter(this, Util.listAssetsInFolder(this, parentFolder), parentFolder, this);
        folderRecyclerView.setAdapter(folderAdapter);
    }

    @Override
    public void onBackToMainPressed() {
        NavUtils.navigateUpFromSameTask(this);
    }

    @Override
    public void onBackPressed() {
        NavUtils.navigateUpFromSameTask(this);
    }
}
