package self.samsung.gallery.model;

import java.util.List;

/**
 * Model class to hold the FolderCover and its data
 * <p>
 * Created by subin on 3/17/2017.
 */
public class FolderCover {

    private final String folderName;
    private final List<String> listFileNames;

    /**
     * Constructor used to initialize the model class with
     *
     * @param folderName    Name of the folder
     * @param listFileNames List of files inside the folder
     */
    public FolderCover(String folderName, List<String> listFileNames) {
        this.folderName = folderName;
        this.listFileNames = listFileNames;
    }

    /**
     * Getter for the folder name
     *
     * @return name of the folder for this FolderCover
     */
    public String getFolderName() {
        return folderName;
    }

    /**
     * Getter for list of files inside the Folder
     *
     * @return List of files
     */
    public List<String> getFileNames() {
        return listFileNames;
    }

}
