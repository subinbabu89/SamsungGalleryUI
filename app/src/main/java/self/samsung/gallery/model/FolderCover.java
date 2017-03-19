package self.samsung.gallery.model;

import java.util.List;

/**
 * Model class to hold the FolderCover and its data
 * <p>
 * Created by subin on 3/17/2017.
 */

public class FolderCover {

    private String folderName;
    private List<String> listFileNames;

    private int currentImageIndex = 0;

    public FolderCover(String folderName, List<String> listFileNames) {
        this.folderName = folderName;
        this.listFileNames = listFileNames;
    }

    public String getFolderName() {
        return folderName;
    }

    public List<String> getFileNames() {
        return listFileNames;
    }

    public void nextImage() {
        if (currentImageIndex < listFileNames.size() - 1) {
            currentImageIndex++;
        }
    }

    public void previousImage() {
        if (currentImageIndex > 0) {
            currentImageIndex--;
        }
    }
}
