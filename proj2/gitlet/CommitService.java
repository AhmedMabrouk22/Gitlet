package gitlet;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static gitlet.Utils.*;

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

    public Commit getCommitBySha1(String commitSha1) {
        if (commitSha1 == null || commitSha1.isEmpty()) return null;
        File commitFile = join(COMMIT_DIR,commitSha1);
        if (!commitFile.exists()) return null;
        return readObject(commitFile,Commit.class);
    }

    public List<Commit> getAllCommits() {
        List<String> commits = Objects.requireNonNull(plainFilenamesIn(COMMIT_DIR));
        return commits
                .stream()
                .map(this::getCommitBySha1)
                .collect(Collectors.toList());
    }

    public List<Commit> getAllCommitsByMessage(String commitMessage) {
        return getAllCommits()
                .stream()
                .filter(commit -> commit.getMessage().equals(commitMessage))
                .collect(Collectors.toList());
    }
}
