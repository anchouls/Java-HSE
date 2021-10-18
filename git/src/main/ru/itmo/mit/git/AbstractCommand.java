package ru.itmo.mit.git;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractCommand {
    abstract void runCommand(List<String> arguments, GitApp app) throws GitException, IOException;
}

class InitCommand extends AbstractCommand {
    @Override
    void runCommand(List<String> arguments, GitApp app) throws GitException, IOException {
        app.init();
    }
}

class CommitCommand extends AbstractCommand {
    @Override
    void runCommand(List<String> arguments, GitApp app) throws GitException {
        app.commit(arguments.get(0));
    }
}

class ResetCommand extends AbstractCommand {
    @Override
    void runCommand(List<String> arguments, GitApp app) throws GitException, IOException {
        app.reset(arguments.get(0));
    }
}

class LogCommand extends AbstractCommand {
    @Override
    void runCommand(List<String> arguments, GitApp app) throws GitException {
        app.log(arguments.size() == 1 ? arguments.get(0) : "");
    }
}

class CheckoutCommand extends AbstractCommand {
    @Override
    void runCommand(List<String> arguments, GitApp app) throws GitException, IOException {
        if (arguments.get(0).equals("--")) {
            app.checkoutResetChanges(arguments.subList(1, arguments.size()));
        } else {
            app.checkout(arguments.get(0));
        }
    }
}

class StatusCommand extends AbstractCommand {
    @Override
    void runCommand(List<String> arguments, GitApp app) throws GitException {
        app.status();
    }
}

class AddCommand extends AbstractCommand {
    @Override
    void runCommand(List<String> arguments, GitApp app) throws GitException {
        app.add(arguments);
    }
}

class RmCommand extends AbstractCommand {
    @Override
    void runCommand(List<String> arguments, GitApp app) throws GitException {
        app.rm(arguments);
    }
}

class BranchCreateCommand extends AbstractCommand {
    @Override
    void runCommand(List<String> arguments, GitApp app) throws GitException {
        app.branchCreate(arguments.get(0));
    }
}

class BranchRemoveCommand extends AbstractCommand {
    @Override
    void runCommand(List<String> arguments, GitApp app) throws GitException {
        app.branchRemove(arguments.get(0));
    }
}

class ShowBranchesCommand extends AbstractCommand {
    @Override
    void runCommand(List<String> arguments, GitApp app) {
        app.showBranches();
    }
}

class MergeCommand extends AbstractCommand {
    @Override
    void runCommand(List<String> arguments, GitApp app) throws GitException, IOException {
        app.merge(arguments.get(0));
    }
}


class Executor {
    private static final Map<String, AbstractCommand> commandMap = new HashMap<>();

    static {
        commandMap.put("init", new InitCommand());
        commandMap.put("commit", new CommitCommand());
        commandMap.put("reset", new ResetCommand());
        commandMap.put("log", new LogCommand());
        commandMap.put("checkout", new CheckoutCommand());
        commandMap.put("status", new StatusCommand());
        commandMap.put("add", new AddCommand());
        commandMap.put("rm", new RmCommand());
        commandMap.put("branch-create", new BranchCreateCommand());
        commandMap.put("branch-remove", new BranchRemoveCommand());
        commandMap.put("show-branches", new ShowBranchesCommand());
        commandMap.put("merge", new MergeCommand());
    }

    public static void main(String[] args) {
        String commandName = args[0];
        List<String> arguments = Arrays.asList(args);
        AbstractCommand command = commandMap.get(commandName);
        if (command == null) {
            System.out.println("Command not found");
        } else {
            try {
                command.runCommand(arguments, new GitApp(System.getProperty("user.dir"), System.out));
            } catch (GitException | IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

}
