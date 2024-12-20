package gitlet;

import java.io.File;
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

    public void addInAddition(String content,String fileName) {
        File file = join(ADD_DIR,fileName);
        writeContents(file,content);
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

    public void addInRemoval(String content,String fileName) {
        File file = join(REMOVE_DIR,fileName);
        writeContents(file,content);
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

    public String logAddition() {
        List<String> addition = getAdditionFilesNames();
        addition.sort(null);
        StringBuilder logBuilder = new StringBuilder();
        logBuilder.append("=== Staged Files ===\n");
        addition.forEach(file -> logBuilder.append(String.format("%s\n",file)));
        return logBuilder.toString();
    }

    public String logRemoval() {
        List<String> removal = getRemovalFilesNames();
        removal.sort(null);
        StringBuilder logBuilder = new StringBuilder();
        logBuilder.append("=== Removed Files ===\n");
        removal.forEach(file -> logBuilder.append(String.format("%s\n",file)));
        return logBuilder.toString();
    }

    public boolean isEmpty() {
        return getAdditionFilesNames().isEmpty() && getRemovalFilesNames().isEmpty();
    }

}
