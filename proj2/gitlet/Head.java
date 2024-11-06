package gitlet;

import java.io.File;

public class Head {
    private final File HEAD_FILE;

    public Head(File headFile) {
        HEAD_FILE = headFile;
    }


    public String getHead() {
        return Utils.readContentsAsString(HEAD_FILE);
    }

    /**
     * Write the current branch in head file
     * @param branch
     */
    public void setHead(Branch branch) {
        Utils.writeContents(HEAD_FILE,branch.getBranchName());
    }


}
