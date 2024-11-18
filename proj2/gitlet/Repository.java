package gitlet;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
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
            systemExit("A Gitlet version-control system already exists in the current directory.");
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
        if (file == null && curCommitFile == null) systemExit("No reason to remove the file.");

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
            systemExit("No changes added to the commit.");
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
     *  Like log, except displays information about all commits ever made.
     *  The order of the commits does not matter
     */
    public void globalLog() {
        checkGitletDir();
        commitService.getAllCommits()
                .stream()
                .map(Commit::log)
                .forEach(System.out::print);
    }

    /**
     * Print ids of all commits that have the commit message
     * @param commitMessage
     */
    public void find(String commitMessage) {
        checkGitletDir();
        List<Commit> commits = commitService.getAllCommitsByMessage(commitMessage);
        if (commits.isEmpty()) {
            systemExit("Found no commit with that message.");
        }
        commits.forEach(commit -> System.out.println(commit.getCommitId()));
    }

    /**
     * checkout -- [file name]
     * Takes the version of the file as it exists in the head commit and puts it in the working directory
     * overwriting the version of the file that’s already there if there is one
     * The new version of the file is not staged.
     * @param fileName
     */
    public void checkout(String fileName) {
        checkGitletDir();
        String currentCommit = getCurrentCommit().getCommitId();
        checkout(currentCommit,fileName);
    }

    /**
     * checkout [commit hash] -- [file name]
     * Takes the version of the file as it exists in the commit with the given id and  puts it in the working directory
     * overwriting the version of the file that’s already there if there is one
     * the new version of the file is not staged.
     * @param commitHash
     * @param fileName
     */
    public void checkout(String commitHash, String fileName) {
        checkGitletDir();
        Commit commit = commitService.getCommitBySha1(commitHash);
        if (commit == null) {
            systemExit("No commit with that id exists.");
        }

        String blobName = commit.getTrackedBlobs().get(fileName);
        if (blobName == null) {
            systemExit("File does not exist in that commit.");
        }

        File blob = blobService.getBlob(blobName);
        String content = readContentsAsString(blob);
        workDirService.addFile(content,fileName);
    }

    /**
     * Takes all files in the commit at the head of the given branch, and puts them in the working directory
     * overwriting the versions of the files that are already there if they exist
     * the given branch will now be considered the current branch (HEAD)
     * The staging area is cleared
     * If a working file is untracked in the current branch and would be overwritten by the checkout print "There is an untracked file in the way; delete it, or add and commit it first."
     * @param branchName
     */
    public void checkoutBranch(String branchName) {
        checkGitletDir();
        if (getCurrentBranch().getBranchName().equals(branchName)) {
            systemExit("No need to checkout the current branch.");
        }
        Branch branch = branchService.getBranch(branchName);
        if (branch == null) {
            systemExit("No such branch exists.");
        }

        Commit commit = commitService.getCommitBySha1(branch.getCommitId());
        checkoutCommit(commit);

        head.setHead(branch);
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
            systemExit("A branch with that name already exists.");
        }
        Branch branch = new Branch(branchName, getCurrentCommit().getCommitId());
        branchService.saveBranch(branch);
    }

    /**
     * rm-branch [branch name]
     * delete the branch with the given name
     * if a branch not exist print "A branch with that name does not exist."
     * if you try to remove the current branch print "Cannot remove the current branch."
     * @param branchName
     */
    public void rmBranch(String branchName) {
        checkGitletDir();
        Branch branch = branchService.getBranch(branchName);
        if (branch == null) {
            systemExit("A branch with that name does not exist.");
        }
        if (getCurrentBranch().getBranchName().equals(branchName)) {
            systemExit("Cannot remove the current branch.");
        }

        branchService.deleteBranch(branchName);
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
            systemExit("Not in an initialized Gitlet directory.");
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
            systemExit("File does not exist.");
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
    private void checkoutCommit(Commit commit) {
        // check if their untracked file in working dir
        Commit currentCommit = getCurrentCommit();
        List<File> workDirFiles = workDirService.getAllFiles();

        // check if this file exist in the current commit or not
        // if exist check is tracked or untracked
        // if untracked and the file exist in the target branch print error message and exit
        if (workDirFiles.stream()
                .map(File::getName)
                .filter(fileName ->
                        !currentCommit.getTrackedBlobs().containsKey(fileName) ||
                        !currentCommit.getTrackedBlobs().get(fileName)
                                .equals(workDirService.getHashedFile(fileName))
                ) // get files not untracked in the current commit
                .anyMatch(fileName -> commit.getTrackedBlobs().containsKey(fileName)) // and this file is exist the target branch, and would be overwritten
        ) {
            systemExit("There is an untracked file in the way; delete it, or add and commit it first.");
        }

        workDirService.clear();
        stageAreaService.clear();
        commit.getTrackedBlobs().keySet()
                .forEach(fileName -> checkout(commit.getCommitId(),fileName));
    }
}
