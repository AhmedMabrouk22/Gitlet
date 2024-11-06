package gitlet;

import java.io.File;

import static gitlet.Utils.join;
import static gitlet.Utils.writeObject;

public class BranchService {
    private final File BRANCH_DIR;

    public BranchService(File branchDir) {
        BRANCH_DIR = branchDir;
    }

    /**
     * Save branch in branches dir
     * @param branch
     */
    public void saveBranch(Branch branch) {
        File branchFile = join(BRANCH_DIR,branch.getBranchName());
        writeObject(branchFile,branch);
    }

    /**
     * Read branch file and return branch object
     * @param branchName
     * @return
     */
    public Branch getBranch(String branchName) {
        File branchFile = join(BRANCH_DIR,branchName);
        return Utils.readObject(branchFile, Branch.class);
    }
}
