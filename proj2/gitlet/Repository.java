package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static gitlet.Utils.*;

/** Represents a gitlet repository.
 *  @author Ahmed Mabrouk
 */
public class Repository {

    private final File CWD;
    private final File GITLET_DIR;
    private final File COMMIT_DIR;
    private final File BRANCH_DIR;
    private final File STAGING_AREA_DIR;
    private final File ADDITION_DIR;
    private final File REMOVAL_DIR;
    private final File HEAD;

    CommitService commitService;
    BranchService branchService;
    Head head;
    public Repository() {
        CWD = new File(System.getProperty("user.dir"));
        GITLET_DIR = join(CWD, ".gitlet");
        COMMIT_DIR = join(GITLET_DIR,"commits");
        BRANCH_DIR = join(GITLET_DIR,"branches");
        STAGING_AREA_DIR = join(GITLET_DIR,"staging_area");
        ADDITION_DIR = join(STAGING_AREA_DIR,"addition");
        REMOVAL_DIR = join(STAGING_AREA_DIR,"removal");
        HEAD = join(GITLET_DIR,"HEAD");

        commitService = new CommitService(COMMIT_DIR);
        branchService = new BranchService(BRANCH_DIR);
        head = new Head(HEAD);
    }

    /**
     * create .gitlet directory
     * create branch master and add head pointer on it
     * create fist initial commit
     */
    public void init() throws IOException {
        // Check if the .gitlet dir exist or not
        if (GITLET_DIR.exists()) {
            systemExist("A Gitlet version-control system already exists in the current directory.");
        }

        // create .gitlet dir
        GITLET_DIR.mkdir();
        COMMIT_DIR.mkdir();
        BRANCH_DIR.mkdir();
        STAGING_AREA_DIR.mkdir();
        ADDITION_DIR.mkdir();
        REMOVAL_DIR.mkdir();
        HEAD.createNewFile();

        // create first commit
        Commit initCommit = new Commit("initial commit",new Date(0),"",null);
        commitService.saveCommit(initCommit);

        // create master branch
        Branch masterBranch = new Branch("master", initCommit.getCommitId());
        System.out.println(masterBranch);
        branchService.saveBranch(masterBranch);

        // Create Head pointer
        head.setHead(masterBranch);
    }

    /**
     * Display information about each commit backwards
     * it start from current head commit
     */
    public void log() {
        String branchName = head.getHead();
        Branch branch = branchService.getBranch(branchName);
        Commit commit = commitService.getCommitBySha1(branch.getCommitId());

        System.out.println("===");
        System.out.println("commit " + commit.getCommitId());
        System.out.println("Date: " + commit.getTimestamp());
        System.out.println(commit.getMessage());

    }
}
