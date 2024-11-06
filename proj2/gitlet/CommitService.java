package gitlet;

import java.io.File;

import static gitlet.Utils.join;
import static gitlet.Utils.writeObject;

public class CommitService {
    private final File COMMIT_DIR;

    public CommitService(File commitDir) {
        COMMIT_DIR = commitDir;
    }

    /**
     * Save commit file in commits dir
     * @param commit
     */
    public void saveCommit(Commit commit) {
        File commitFile = join(COMMIT_DIR,commit.getCommitId());
        writeObject(commitFile,commit);
    }
}
