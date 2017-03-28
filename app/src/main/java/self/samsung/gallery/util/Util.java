package self.samsung.gallery.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.util.TypedValue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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

    /**
     * Needed to move all the image folders inside another folder at the root of the assets folder as additional folders would get added based on the phone manufacturer (namely webkit,sounds,etc)
     */
    private static final String WALLS_FOLDER = "Walls";

    private static final int RESIZE_WIDTH_HEIGHT = 500;

    /**
     * Method to create List of FolderCover for the main landing activity
     *
     * @param context calling context
     * @return List of FolderCover
     */
    public static List<FolderCover> fetchFolderCovers(Context context) {
        List<FolderCover> folderCovers = new ArrayList<>();
        List<String> foldersInAssets = listFoldersInAsset(context);
        for (String folder : foldersInAssets) {
            FolderCover folderCover = new FolderCover(folder, listAssetsInFolder(context, folder));
            folderCovers.add(folderCover);
        }
        return folderCovers;
    }

    /**
     * Method to List the names of all the files in a folder
     *
     * @param context    calling context
     * @param folderName name of the folder to list the files from
     * @return List the names of all the files in a folder
     */
    public static List<String> listAssetsInFolder(Context context, String folderName) {
        StringBuilder stringBuilder = new StringBuilder().append(WALLS_FOLDER).append(File.separator).append(folderName);

        List<String> files = new ArrayList<>();
        String[] list;
        try {
            list = context.getAssets().list(stringBuilder.toString());
            if (list.length > 0) {
                Collections.addAll(files, list);
            }
        } catch (IOException e) {
            //catch exception here
        }
        return files;
    }

    /**
     * Method used to list folders in the assets folder
     *
     * @param context calling context
     * @return list of folders in the assets folder
     */
    private static List<String> listFoldersInAsset(Context context) {
        List<String> folders = new ArrayList<>();
        String[] list;
        try {
            list = context.getAssets().list(WALLS_FOLDER);
            if (list.length > 0) {
                Collections.addAll(folders, list);
            }
        } catch (IOException e) {
            //catch exception here
        }
        return folders;
    }

    /**
     * Method to easily convert dp into pixels
     *
     * @param context calling context
     * @param dp      input value in dp
     * @return output value in pixels
     */
    public static int convertDpToPx(@NonNull Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    /**
     * Method to get drawables from the files in the assets folder.
     * Note: images load slower as they are loaded from the assets folder. Henced resized images to 500px, which i feel are a good tradeoff as of now.
     *
     * @param context  calling context
     * @param fileName filename with folder (if applicable)
     * @return Drawable object for the file
     * @throws IOException possible issue with file not found
     */
    public static Drawable fetchDrawableFromAssets(@NonNull Context context, String fileName) throws IOException {
        InputStream ims = context.getAssets().open(WALLS_FOLDER + File.separator + fileName);
        Drawable drawable = Drawable.createFromStream(ims, null);

        Bitmap b = ((BitmapDrawable) drawable).getBitmap();
        Bitmap bitmapResized = Bitmap.createScaledBitmap(b, RESIZE_WIDTH_HEIGHT, RESIZE_WIDTH_HEIGHT, false);
        return new BitmapDrawable(context.getResources(), bitmapResized);
    }

}
