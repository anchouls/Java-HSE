package ru.itmo.mit.git;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.*;

import static ru.itmo.mit.git.GitConstants.*;

public class GitCliImpl implements GitCli {

    private final GitApp app;
    private PrintStream out = System.out;

    public GitCliImpl(String workingDir) {
        app = new GitApp(workingDir, out);
    }

    @Override
    public void runCommand(@NotNull String command, @NotNull List<@NotNull String> arguments) throws GitException {
        try {
            switch (command) {
                case INIT:
                    checkSizeArguments(arguments, 0, 0);
                    app.init();
                    break;
                case COMMIT:
                    checkSizeArguments(arguments, 1, 1);
                    app.commit(arguments.get(0));
                    break;
                case RESET:
                    checkSizeArguments(arguments, 1, 1);
                    app.reset(arguments.get(0));
                    break;
                case LOG:
                    checkSizeArguments(arguments, 0, 1);
                    app.log(arguments.size() == 1 ? arguments.get(0) : "");
                    break;
                case CHECKOUT:
                    checkSizeArguments(arguments, 1, Integer.MAX_VALUE);
                    if (arguments.get(0).equals("--")) {
                        app.checkoutResetChanges(arguments.subList(1, arguments.size()));
                    } else {
                        app.checkout(arguments.get(0));
                    }
                    break;
                case STATUS:
                    checkSizeArguments(arguments, 0, 0);
                    app.status();
                    break;
                case ADD:
                    checkSizeArguments(arguments, 1, Integer.MAX_VALUE);
                    app.add(arguments);
                    break;
                case RM:
                    checkSizeArguments(arguments, 1, Integer.MAX_VALUE);
                    app.rm(arguments);
                    break;
                case BRANCH_CREATE:
                    checkSizeArguments(arguments, 1, 1);
                    app.branchCreate(arguments.get(0));
                    break;
                case BRANCH_REMOVE:
                    checkSizeArguments(arguments, 1, 1);
                    app.branchRemove(arguments.get(0));
                    break;
                case SHOW_BRANCHES:
                    checkSizeArguments(arguments, 0, 0);
                    app.showBranches();
                    break;
                case MERGE:
                    checkSizeArguments(arguments, 1, 1);
                    app.merge(arguments.get(0));
                    break;
                default:
                    throw new GitException("Command not found");
            }
        } catch (IOException e) {
            out.println(e.getMessage());
        }
    }

    @Override
    public void setOutputStream(@NotNull PrintStream outputStream) {
        out = outputStream;
    }

    @Override
    public @NotNull String getRelativeRevisionFromHead(int n) {
        return app.getRelativeRevisionFromHead(n);
    }

    private static void checkSizeArguments(@NotNull List<@NotNull String> arguments, int l, int r) throws GitException {
        if (arguments.size() < l || arguments.size() > r) {
            throw new GitException("Wrong number of arguments");
        }
    }
}
