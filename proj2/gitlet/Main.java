package gitlet;

import static gitlet.Utils.checkNumArgs;
import static gitlet.Utils.systemExit;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author Ahmed Mabrouk
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {


        if (args.length == 0) {
            systemExit("Please enter a command.");
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
                case "checkout":
                    switch (args.length) {
                        case 2:
                            checkNumArgs(args,1);
                            repo.checkoutBranch(args[1]);
                            break;
                        case 3:
                            if (!args[1].equals("--"))
                                systemExit("Incorrect operands.");
                            checkNumArgs(args,2);
                            repo.checkout(args[2]);
                            break;
                        case 4:
                            if (!args[2].equals("--"))
                                systemExit("Incorrect operands.");
                            checkNumArgs(args,3);
                            repo.checkout(args[1],args[3]);
                            break;
                        default:
                            systemExit("Incorrect operands.");
                            break;
                    }
                    break;
                case "branch":
                    checkNumArgs(args,1);
                    repo.branch(args[1]);
                    break;
                case "rm-branch":
                    checkNumArgs(args,1);
                    repo.rmBranch(args[1]);
                    break;
                case "status":
                    checkNumArgs(args,0);
                    repo.status();
                    break;
                case "reset":
                    checkNumArgs(args,1);
                    repo.reset(args[1]);
                    break;
                case "merge":
                    checkNumArgs(args,1);
                    repo.merge(args[1]);
                    break;
                default:
                    systemExit("No command with that name exists.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            systemExit(ex.getMessage());
        }
    }
}
