package gitlet;

import static gitlet.Utils.checkNumArgs;
import static gitlet.Utils.systemExist;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author Ahmed Mabrouk
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {


        if (args.length == 0) {
            systemExist("Please enter a command.");
        }

        Repository repo = new Repository();
        String firstArg = args[0];
        try {
            switch(firstArg) {
                case "init":
                    checkNumArgs(args,0);
                    repo.init();
                    break;
                case "add":
                    checkNumArgs(args,1);
                    repo.add(args[1]);
                    break;
                case "commit":
                    repo.commit(args[1]);
                    break;
                case "rm":
                    checkNumArgs(args,1);
                    repo.rm(args[1]);
                    break;
                case "log":
                    checkNumArgs(args,0);
                    repo.log();
                    break;
                case "global-log":
                    checkNumArgs(args,0);
                    repo.globalLog();
                    break;
                case "find":
                    checkNumArgs(args,1);
                    repo.find(args[1]);
                    break;
                case "branch":
                    checkNumArgs(args,1);
                    repo.branch(args[1]);
                    break;
                case "status":
                    checkNumArgs(args,0);
                    repo.status();
                    break;
                default:
                    systemExist("No command with that name exists.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            systemExist(ex.getMessage());
        }
    }
}
