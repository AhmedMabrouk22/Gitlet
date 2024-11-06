package gitlet;

import java.io.File;

import static gitlet.Utils.join;
import static gitlet.Utils.writeContents;

public class StageAreaService {
    private final File ADD_DIR;
    private final File REMOVE_DIR;

    public StageAreaService(File addDir, File removeDir) {
        ADD_DIR = addDir;
        REMOVE_DIR = removeDir;
    }

    public void addInAddition(String fileName, String content) {
        writeContents(join(ADD_DIR,fileName),content);
    }

    public File getFileFromAddition(String fileName) {
        File file = join(ADD_DIR,fileName);
        return file.exists() ? file : null;
    }

    public void deleteFromAddition(String fileName) {
        File file = getFileFromAddition(fileName);
        if (file != null) {
            file.delete();
        }
    }

    public File getFileFromRemoval(String fileName) {
        File file = join(REMOVE_DIR,fileName);
        return file.exists() ? file : null;
    }

    public void addInRemoval(String fileName, String content) {
        writeContents(join(REMOVE_DIR,fileName),content);
    }

    public void deleteFromRemoval(String fileName) {
        File file = getFileFromRemoval(fileName);
        if (file != null) file.delete();
    }



}
