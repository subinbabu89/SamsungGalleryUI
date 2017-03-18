package self.samsung.gallery.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.List;

import self.samsung.gallery.R;
import self.samsung.gallery.adapter.FolderCoverAdapter;
import self.samsung.gallery.model.FolderCover;
import self.samsung.gallery.peekview.PeekView;
import self.samsung.gallery.util.Util;

/**
 * Activity with folders and its covers
 * <p>
 * Created by subin on 3/17/2017.
 */
public class MainActivity extends AppCompatActivity implements FolderCoverAdapter.FolderCoverClickListener {

    private List<FolderCover> folderCovers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.overridePendingTransition(R.anim.anim_slide_in_right,
                R.anim.anim_slide_out_right);
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.folder_cover_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        folderCovers = Util.fetchFolderCovers(this);

        PeekView peekView = new PeekView(this, R.layout.pre_view, recyclerView);

        FolderCoverAdapter folderCoverAdapter = new FolderCoverAdapter(this, folderCovers, this, peekView);
        recyclerView.setAdapter(folderCoverAdapter);
    }

    @Override
    public void onItemClicked(int position) {
        Intent intent = new Intent(MainActivity.this, FolderViewActivity.class);
        intent.putExtra(Intent.EXTRA_TEXT, folderCovers.get(position).getFolderName());
        startActivity(intent);
    }
}
