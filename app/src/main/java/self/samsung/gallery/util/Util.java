package self.samsung.gallery.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.TypedValue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import self.samsung.gallery.model.FolderCover;

/**
 * Util class needed for all utility functions
 * <p>
 * Created by subin on 3/17/2017.
 */

public class Util {
    public static List<FolderCover> fetchFolderCovers(Context context) {
        List<FolderCover> folderCovers = new ArrayList<>();
        List<String> foldersInAssets = listFoldersInAsset(context);
        for (String folder : foldersInAssets) {
            FolderCover folderCover = new FolderCover(folder, listAssetsInFolder(context, folder));
            folderCovers.add(folderCover);
        }
        return folderCovers;
    }

    public static List<String> listAssetsInFolder(Context context, String folderName) {
        List<String> files = new ArrayList<>();
        String[] list;
        try {
            list = context.getAssets().list(folderName);
            if (list.length > 0) {
                Collections.addAll(files, list);
            }
        } catch (IOException e) {
            //catch exception here
        }
        return files;
    }

    private static List<String> listFoldersInAsset(Context context) {
        List<String> folders = new ArrayList<>();
        String[] list;
        try {
            list = context.getAssets().list("");
            if (list.length > 0) {
                // This is a folder
                for (String file : list) {
                    if (!file.equalsIgnoreCase("images") && !file.equalsIgnoreCase("sounds") && !file.equalsIgnoreCase("webkit")) {
                        folders.add(file);
                    }
                }
            }
        } catch (IOException e) {
            //catch exception here
        }
        return folders;
    }

    public static int convertDpToPx(@NonNull Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

}
