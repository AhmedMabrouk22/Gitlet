package gitlet;

import java.io.File;

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

}
