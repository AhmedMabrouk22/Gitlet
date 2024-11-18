package gitlet;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static gitlet.Utils.*;

public class WorkDirService {
    private final File WORK_DIR;
    public WorkDirService (File workDir) {
        this.WORK_DIR = workDir;
    }

    public boolean fileExist(String fileName) {
        return getFile(fileName).exists();
    }

    public File getFile(String fileName) {
        return join(WORK_DIR,fileName);
    }

    public String getHashedFile(String fileName) {
        return sha1(readContentsAsString(getFile(fileName)));
    }

    public void deleteFile(String fileName) {
        File file = getFile(fileName);
        if (file != null) file.delete();
    }

    public void addFile(String content, String fileName) {
        File file = join(WORK_DIR,fileName);
        writeContents(file,content);
    }

    public List<File> getAllFiles() {
        return Objects.requireNonNull(plainFilenamesIn(WORK_DIR))
                .stream()
                .map(fileName -> join(WORK_DIR,fileName))
                .collect(Collectors.toList());
    }

    public void clear() {
        getAllFiles().stream()
                .map(File::getName)
                .forEach(this::deleteFile);
    }

}
