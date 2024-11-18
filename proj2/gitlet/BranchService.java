package gitlet;

import java.io.File;
import java.util.List;

import static gitlet.Utils.*;

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
        if (!branchFile.exists()) return null;
        return Utils.readObject(branchFile, Branch.class);
    }

    public boolean isExist(String branchName) {
        return getBranches().stream()
                .anyMatch(file -> file.equals(branchName));
    }
    public List<String> getBranches() {
        return plainFilenamesIn(BRANCH_DIR);
    }

    public String log(String curBranchName) {
        StringBuilder logBuilder = new StringBuilder();
        logBuilder.append("=== Branches ===\n");
        List<String> branches = getBranches();
        branches.sort(null);
        branches.forEach(branch -> {
            if (branch.equals(curBranchName))
                logBuilder.append(String.format("*%s\n",branch));
            else
                logBuilder.append(String.format("%s\n",branch));
        });
        return logBuilder.toString();
    }

    public void deleteBranch(String branchName) {
        File file = join(BRANCH_DIR,branchName);
        if (file.exists())
            file.delete();
    }
}
