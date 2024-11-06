package gitlet;

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
                    repo.init();
                    break;
                case "add":
                    // TODO: handle the `add [filename]` command
                    break;
                case "log":
                    repo.log();
                    break;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            systemExist(ex.getMessage());
        }
    }
}
