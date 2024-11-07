package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static gitlet.Utils.*;

public class StageAreaService {
    private final File ADD_DIR;
    private final File REMOVE_DIR;

    public StageAreaService(File addDir, File removeDir) {
        ADD_DIR = addDir;
        REMOVE_DIR = removeDir;
    }

    public List<String> getAdditionFilesNames() {
        return Utils.plainFilenamesIn(ADD_DIR);
    }

    public List<String> getRemovalFilesNames() {
        return Utils.plainFilenamesIn(REMOVE_DIR);
    }

    public void addInAddition(File file) {
        writeContents(join(ADD_DIR,file.getName()),readContentsAsString(file));
    }

    public File getFileFromAddition(String fileName) {
        File file = join(ADD_DIR,fileName);
        return file.exists() ? file : null;
    }

    public void deleteFromAddition(String fileName) {
        File file = getFileFromAddition(fileName);
        if (file != null) {
            deleteFromAddition(file);
        }
    }

    public void deleteFromAddition(File file) {
        file.delete();
    }

    public File getFileFromRemoval(String fileName) {
        File file = join(REMOVE_DIR,fileName);
        return file.exists() ? file : null;
    }

    public void addInRemoval(File file){
        writeContents(join(REMOVE_DIR,file.getName()), readContentsAsString(file));
    }

    public void deleteFromRemoval(String fileName) {
        File file = getFileFromRemoval(fileName);
        if (file != null) file.delete();
    }

    public void clear() {
        List<String> additionFiles = getAdditionFilesNames();
        List<String> removalFiles = getRemovalFilesNames();
        additionFiles.forEach(this::deleteFromAddition);
        removalFiles.forEach(this::deleteFromRemoval);
    }


}
