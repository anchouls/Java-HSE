package ru.itmo.mit.git;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.apache.commons.codec.digest.DigestUtils.md5Hex;

public class GitApp {

    private static final String EL = System.lineSeparator();
    private final Path workingDir;
    private final Path gitPath;
    private GitCommit head;
    private final PrintStream out;
    private final GitRepository repository;

    public GitApp(String workingDir, PrintStream out) {
        this.workingDir = Path.of(workingDir);
        gitPath = this.workingDir.resolve(".git");
        repository = new GitRepository(this.workingDir);
        Path statePath = gitPath.resolve("state");
        if (Files.isRegularFile(statePath)) {
            try {
                repository.loadState(gitPath);
                head = loadCommit(repository.getBranches().get(repository.getCurrentBranch()), gitPath);
            } catch (GitException e) {
                out.println("Unable to load state");
            }
        }
        this.out = out;
    }

    public static GitCommit loadCommit(String hash, Path gitPath) throws GitException {
        try {
            final Gson gson = new Gson();
            Type typeToken = new TypeToken<GitCommit>() {
            }.getType();
            Path path = gitPath.resolve("commits").resolve(hash);
            JsonReader reader = new JsonReader(new FileReader(path.toString()));
            return gson.fromJson(reader, typeToken);
        } catch (JsonSyntaxException e) {
            throw new GitException("Unable to load commit");
        } catch (FileNotFoundException e){
            throw new GitException("Commit doesn't exist");
        }
    }

    public void init() throws IOException, GitException {
        if (head != null) {
            throw new GitException("Repository already exists");
        }
        Files.createDirectories(gitPath);
        repository.setCurrentBranch("master");
        commit("Initial commit");
        out.println("Project initialized");
    }

    public void add(List<String> files) throws GitException {
        if (isDetached()) {
            out.println("Error while performing add: Head is detached");
            return;
        }
        repository.updateFiles(head);
        List<Path> toAdd = new ArrayList<>();
        for (String s : files) {
            Path p = workingDir.resolve(s);
            if (repository.getUntrackedFiles().contains(p) || repository.getModifiedFiles().contains(p)
                    || repository.getRemovedFiles().contains(p)) {
                toAdd.add(p);
            } else {
                throw new GitException("file not found");
            }
        }
        repository.getAddFiles().addAll(toAdd);
        repository.saveState(gitPath);
        out.println("Add completed successful");
    }

    private boolean isDetached() {
        return !repository.getBranches().get(repository.getCurrentBranch()).equals(head.hash);
    }

    public void status() throws GitException {
        if (isDetached()) {
            out.println("Error while performing status: Head is detached");
            return;
        }
        out.println("Current branch is " + repository.getCurrentBranch());
        repository.updateFiles(head);
        if (repository.getUntrackedFiles().size() > 0) {
            out.println("Untracked files:");
            for (Path p : repository.getUntrackedFiles()) {
                out.println(p.toString());
            }
        }
        StringBuilder sb = new StringBuilder();
        for (Path p : repository.getModifiedFiles()) {
            if (!repository.getAddFiles().contains(p)) {
                sb.append(p).append('\n');
            }
        }
        if (sb.length() > 0) {
            out.println("Modified files: ");
            out.println(sb.toString());
        }
        StringBuilder sbRemove = new StringBuilder();
        for (Path p : repository.getRemovedFiles()) {
            if (!repository.getAddFiles().contains(p)) {
                sbRemove.append(p).append('\n');
            }
        }
        if (sbRemove.length() > 0) {
            out.println("Removed files: ");
            out.println(sbRemove.toString());
        }
        if (repository.getAddFiles().size() > 0) {
            out.println("Ready to commit:");
            for (Path p : repository.getAddFiles()) {
                if (!repository.getModifiedFiles().contains(p) && !repository.getRemovedFiles().contains(p)) {
                    out.println("New files: " + p.toString());
                }
            }
            for (Path p : repository.getModifiedFiles()) {
                if (repository.getAddFiles().contains(p)) {
                    out.println("Modified files: " + p.toString());
                }
            }
            for (Path p : repository.getRemovedFiles()) {
                if (repository.getAddFiles().contains(p)) {
                    out.println("Removed files: " + p.toString());
                }
            }
        }
        if (repository.getUntrackedFiles().size() == 0 && repository.getAddFiles().size() == 0
                && sbRemove.length() == 0 && sb.length() == 0) {
            out.println("Everything up to date");
        }
    }


    public void commit(String message) throws GitException {
        if (head != null && isDetached()) {
            out.println("Error while performing commit: Head is detached");
            return;
        }
        try {
            head = new GitCommit(gitPath, repository.getAddFiles(), message,
                    head != null ? head.hash : null, repository.getRenamedFiles(), repository.getRemovedFiles());
            repository.getBranches().put(repository.getCurrentBranch(), head.hash);
            out.println("Files committed");
            for (Path p : repository.getAddFiles()) {
                repository.getRepFiles().put(p, hash(p));
            }
            repository.getAddFiles().clear();
        } catch (IOException e) {
            throw new GitException(e.getMessage(), e);
        }
        repository.saveState(gitPath);
    }

    public void log(String revision) throws GitException {
        GitCommit current = head;
        GitCommit from = null;
        if (!revision.equals("")) {
            from = revisionToCommit(revision);
        }
        while (current != from) {
            out.println("Commit " + current.hash);
            out.println("Author: " + System.getProperty("user.name"));
            out.println("Date: " + current.date);
            out.println(current.message + EL);
            if (current.prevCommit.equals("")) {
                break;
            }
            current = loadCommit(current.prevCommit, gitPath);
        }
    }

    public GitCommit revisionToCommit(String revision) throws GitException {
        if (repository.getBranches().containsKey(revision)) {
            return loadCommit(repository.getBranches().get(revision), gitPath);
        } else if (revision.startsWith("HEAD")) {
            int n = Integer.parseInt(revision.substring(5));
            return loadCommit(getRelativeRevisionFromHead(n), gitPath);
        } else {
            return loadCommit(revision, gitPath);
        }
    }

    public @NotNull String getRelativeRevisionFromHead(int n) {
        GitCommit curHead = head;
        for (int i = 0; i < n; i++) {
            try {
                curHead = loadCommit(curHead.prevCommit, gitPath);
            } catch (GitException e) {
                System.err.println("error while loading commit from file");
            }
        }
        return curHead.hash;
    }

    public void checkout(String revision) throws IOException, GitException {
        GitCommit target = revisionToCommit(revision);
        repository.updateFiles(head);
        if (!repository.getAddFiles().isEmpty() || !repository.getModifiedFiles().isEmpty()) {
            out.println("Stash your changes");
            return;
        }
        Map<Path, Path> curFiles = head.allFiles(gitPath);
        Map<Path, Path> targetFiles = target.allFiles(gitPath);
        for (Map.Entry<Path, Path> p : curFiles.entrySet()) {
            if (!targetFiles.containsKey(p.getKey()) || p.getValue() != targetFiles.get(p.getKey())) {
                Files.delete(p.getKey());
            }
        }
        for (Map.Entry<Path, Path> p : targetFiles.entrySet()) {
            if (!curFiles.containsKey(p.getKey()) || p.getValue() != curFiles.get(p.getKey())) {
                Files.copy(p.getValue(), p.getKey());
            }
        }
        head = target;
        if (repository.getBranches().containsKey(revision)) {
            repository.setCurrentBranch(revision);
        }
        repository.saveState(gitPath);
        out.println("Checkout completed successful");
    }

    public void checkoutResetChanges(List<String> files) throws GitException, IOException {
        for (String s : files) {
            Path p = workingDir.resolve(s);
            if (!repository.getAddFiles().contains(p) && repository.getRepFiles().containsKey(p)) {
                if (Files.isRegularFile(p)) {
                    Files.delete(p);
                }
                Path realFile = head.lastVersion(p, gitPath);
                if (Files.isRegularFile(realFile)) {
                    Files.copy(realFile, p);
                }
            }
        }
        repository.saveState(gitPath);
        out.println("Checkout completed successful");
    }

    public void rm(List<String> files) throws GitException {
        if (isDetached()) {
            out.println("Error while performing rm: Head is detached");
            return;
        }
        List<Path> toRemove = new ArrayList<>();
        for (String s : files) {
            Path path = workingDir.resolve(s);
            if (repository.getRepFiles().containsKey(path) || repository.getAddFiles().contains(path)) {
                toRemove.add(path);
            } else {
                throw new GitException(s + " is not present in repository");
            }
        }
        repository.getAddFiles().removeAll(toRemove);
        for (Path p : toRemove) {
            repository.getRepFiles().remove(p);
        }
        repository.saveState(gitPath);
        out.println("Rm completed successful");
    }

    public void reset(String revision) throws IOException, GitException {
        GitCommit target = revisionToCommit(revision);
        repository.updateFiles(head);
        Map<Path, Path> curFiles = head.allFiles(gitPath);
        Map<Path, Path> targetFiles = target.allFiles(gitPath);
        for (Map.Entry<Path, Path> p : curFiles.entrySet()) {
            if (targetFiles.containsKey(p.getKey())) {
                if (p.getValue() != targetFiles.get(p.getKey())) {
                    Files.delete(p.getKey());
                } else if (!repository.getRepFiles().get(p.getKey()).equals(hash(targetFiles.get(p.getKey())))) {
                    Files.delete(p.getKey());
                    Files.copy(p.getValue(), p.getKey());
                }
            } else {
                Files.delete(p.getKey());
            }
        }
        for (Map.Entry<Path, Path> p : targetFiles.entrySet()) {
            if (!curFiles.containsKey(p.getKey()) || p.getValue() != curFiles.get(p.getKey())) {
                Files.copy(p.getValue(), p.getKey());
            }
        }
        head = target;
        repository.getBranches().put(repository.getCurrentBranch(), target.hash);
        repository.saveState(gitPath);
        out.println("Reset successful");
    }

    public void branchCreate(String s) throws GitException {
        if (repository.getBranches().containsKey(s)) {
            out.println("Branch " + s + " already exists");
        } else {
            repository.getBranches().put(s, head.hash);
            repository.setCurrentBranch(s);
            repository.saveState(gitPath);
            out.println("Branch " + s + " created successfully");
        }
    }

    public void branchRemove(String s) throws GitException {
        repository.getBranches().remove(s);
        repository.saveState(gitPath);
        out.println("Branch " + s + " removed successfully");
    }

    public void showBranches() {
        out.println("Available branches:");
        for (String s : repository.getBranches().keySet()) {
            out.println(s);
        }
    }

    public void merge(String branch) throws GitException, IOException {
        GitCommit target = loadCommit(repository.getBranches().get(branch), gitPath);
        repository.updateFiles(head);
        if (!repository.getAddFiles().isEmpty() || !repository.getModifiedFiles().isEmpty()) {
            out.println("Stash your changes");
            return;
        }
        Map<Path, Path> curFiles = head.allFiles(gitPath);
        Map<Path, Path> targetFiles = target.allFiles(gitPath);
        for (Map.Entry<Path, Path> p : curFiles.entrySet()) {
            if (!targetFiles.containsKey(p.getKey())) {
                repository.getAddFiles().add(p.getKey());
            } else if (p.getValue() != targetFiles.get(p.getKey())) {
                fixConflicts(p.getKey(), targetFiles.get(p.getKey()));
                repository.getAddFiles().add(p.getKey());
            }
        }
        for (Map.Entry<Path, Path> p : targetFiles.entrySet()) {
            if (!curFiles.containsKey(p.getKey())) {
                Files.copy(p.getValue(), p.getKey());
                repository.getAddFiles().add(p.getKey());
            }
        }
        out.println("Continue?");
//        Scanner userInput = new Scanner(System.in); // ждем отлкил пользователя
//        while(!userInput.hasNextLine());
        commit("Merge " + repository.getCurrentBranch() + " with " + branch);
        out.println("Merge completed successful");
    }

    private void fixConflicts(Path firstFile, Path secondFile) throws IOException {
        byte[] old = Files.readAllBytes(secondFile);
        Files.write(firstFile, old, StandardOpenOption.APPEND);
        out.println("Fix " + firstFile.toString());
    }


    static String hash(Path file) {
        try (InputStream is = Files.newInputStream(file)) {
            return md5Hex(is);
        } catch (IOException e) {
            return "";
        }
    }
}
