package gitlet;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.List;

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
    private final File BLOB_DIR;
    private final File HEAD;

    private final CommitService commitService;
    private final BranchService branchService;
    private final WorkDirService workDirService;
    private final StageAreaService stageAreaService;
    private final BlobService blobService;
    private final Head head;
    public Repository() {
        CWD = new File(System.getProperty("user.dir"));
        GITLET_DIR = join(CWD, ".gitlet");
        COMMIT_DIR = join(GITLET_DIR,"commits");
        BRANCH_DIR = join(GITLET_DIR,"branches");
        STAGING_AREA_DIR = join(GITLET_DIR,"staging_area");
        ADDITION_DIR = join(STAGING_AREA_DIR,"addition");
        REMOVAL_DIR = join(STAGING_AREA_DIR,"removal");
        BLOB_DIR = join(GITLET_DIR,"blobs");
        HEAD = join(GITLET_DIR,"HEAD");

        workDirService = new WorkDirService(CWD);
        commitService = new CommitService(COMMIT_DIR);
        branchService = new BranchService(BRANCH_DIR);
        stageAreaService = new StageAreaService(ADDITION_DIR,REMOVAL_DIR);
        blobService = new BlobService(BLOB_DIR);
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
        BLOB_DIR.mkdir();
        HEAD.createNewFile();

        // create first commit
        Commit initCommit = new Commit("initial commit",new Date(0),"",null);
        commitService.saveCommit(initCommit);

        // create master branch
        Branch masterBranch = new Branch("master", initCommit.getCommitId());
        branchService.saveBranch(masterBranch);

        // Create Head pointer
        head.setHead(masterBranch);
    }

    /**
     * add [file name]
     * add a copy of files at it currently exist to addition staging ares
     * .gitlet dir must be before exist to run this command
     * if the file not exist print 'File does not exist.'
     * if the current file is the same file in current commit. not added it and remove it form staging area
     * if not the same overwrite file that exist in staging area
     * @param fileName
     */
    public void add(String fileName) {
        checkGitletDir();
        checkFileExist(fileName);

        File curFile = workDirService.getFile(fileName);
        String hashedFile = workDirService.getHashedFile(fileName);

        Commit currentCommit = getCurrentCommit();
        String currentCommitFile = currentCommit.getTrackedBlobs().getOrDefault(fileName,null);
        if (currentCommitFile == null || !currentCommitFile.equals(hashedFile)) {
            stageAreaService.addInAddition(curFile);
        } else {
            stageAreaService.deleteFromAddition(fileName);
        }

        // if this file staged for remove before, delete it
        stageAreaService.deleteFromRemoval(fileName);

    }

    /**
     * rm [file name]
     * unstage file if it is currently staged for addition
     * if the file is tracked in the current commit remove it
     * @param fileName
     */
    public void rm(String fileName) {
        checkGitletDir();
        checkFileExist(fileName);
        File file = stageAreaService.getFileFromAddition(fileName);
        Commit curCommit = getCurrentCommit();
        String curCommitFile = curCommit.getTrackedBlobs().getOrDefault(fileName,null);
        if (file == null && curCommitFile == null) systemExist("No reason to remove the file.");

        if (file != null) {
            stageAreaService.deleteFromAddition(file);
        }

        if (curCommitFile != null) {
            // add it in removal and remove it form working dir
            file = workDirService.getFile(fileName);
            stageAreaService.addInRemoval(file);
            workDirService.deleteFile(fileName);
        }
    }

    /**
     * commit [message]
     * create new commit tracked files in the current commit and staging area
     * a commit will only update the contents of files it is tracking that have been stage for addition
     * The staging area is cleared after a commit
     *
     * @param message
     */
    public void commit(String message) {
        checkGitletDir();
        List<String> additionFiles = stageAreaService.getAdditionFilesNames();
        List<String> removalFiles = stageAreaService.getRemovalFilesNames();

        if (additionFiles.isEmpty() && removalFiles.isEmpty()) {
            systemExist("No changes added to the commit.");
        }

        Commit currentCommit = getCurrentCommit();
        Map<String,String> blobs = new HashMap<>(currentCommit.getTrackedBlobs());

        // create blob for each file in addition area and store it in blob file
        additionFiles.forEach(fileName -> {
            File file = stageAreaService.getFileFromAddition(fileName);
            String blobId = blobService.saveBlob(file);
            blobs.put(fileName,blobId);
        });

        // remove file that staged in removal
        removalFiles.forEach(blobs::remove);

        stageAreaService.clear();

        // create new commit
        Commit newCommit = new Commit(message, Date.from(Instant.now()), currentCommit.getCommitId(),blobs);
        commitService.saveCommit(newCommit);

        // change current commit
        Branch branch = getCurrentBranch();
        branch.setCommitId(newCommit.getCommitId());
        branchService.saveBranch(branch);
    }

    /**
     * Display information about each commit backwards
     * it start from current head commit
     */
    public void log() {
        checkGitletDir();
        getCommits(getCurrentCommit())
                .stream()
                .map(Commit::log)
                .forEach(System.out::print);
    }

    /**
     * branch [branch name]
     * Creates a new branch with the given name
     * Points it at the current head commit
     * This command does NOT immediately switch to the newly created branch
     * If a branch with the given name already exists, print the error message "A branch with that name already exists."
     *
     * @param branchName
     */
    public void branch(String branchName) {
        checkGitletDir();
        if (branchService.isExist(branchName)) {
            systemExist("A branch with that name already exists.");
        }
        Branch branch = new Branch(branchName, getCurrentCommit().getCommitId());
        branchService.saveBranch(branch);
    }

    /**
     * Displays what branches currently exist and marks the current branch with a *
     * displays what files have been staged for addition or removal
     * Entries listed in lexicographic order
     *
     */
    public void status() {
        checkGitletDir();
        String logBuilder = String.format("%s\n", branchService.log(getCurrentBranch().getBranchName())) +
                String.format("%s\n", stageAreaService.logAddition()) +
                String.format("%s\n", stageAreaService.logRemoval()) +
                "=== Modifications Not Staged For Commit ===\n\n" + // TODO
                "=== Untracked Files ===\n\n"; // TODO

        System.out.println(logBuilder);
    }

    private void checkGitletDir() {
        if (!GITLET_DIR.exists()) {
            systemExist("Not in an initialized Gitlet directory.");
        }
    }

    private Branch getCurrentBranch() {
        return branchService.getBranch(head.getHead());
    }
    private Commit getCurrentCommit() {
        return commitService.getCommitBySha1(getCurrentBranch().getCommitId());
    }

    private void checkFileExist(String fileName) {
        if (!workDirService.fileExist(fileName)) {
            systemExist("File does not exist.");
        }
    }

    private List<Commit> getCommits(Commit commit) {
        List<Commit> res = new ArrayList<>();
        Commit cur = commit;
        while (cur != null) {
            res.add(cur);
            String parentSha1 = cur.getParent();
            if (!parentSha1.isEmpty())
                cur = commitService.getCommitBySha1(parentSha1);
            else cur = null;
        }
        return res;
    }
}
