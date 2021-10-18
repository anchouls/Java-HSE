package ru.itmo.mit.git;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class GitRepository {

    private final List<Path> addFiles;
    private final List<Path> untrackedFiles;
    private final List<Path> modifiedFiles;
    private final List<Path> removedFiles;
    private final Map<Path, String> repFiles;
    private final Map<Path, Path> renamedFiles;
    private final Path workingDir;
    private final Map<String, String> branches;
    private String currentBranch;

    public GitRepository(Path workingDir) {
        addFiles = new ArrayList<>();
        untrackedFiles = new ArrayList<>();
        modifiedFiles = new ArrayList<>();
        removedFiles = new ArrayList<>();
        repFiles = new HashMap<>();
        renamedFiles = new HashMap<>();
        branches = new HashMap<>();
        this.workingDir = workingDir;
    }

    public void loadState(Path gitPath) throws GitException {
        try {
            final Gson gson = new Gson();
            Type typeToken = new TypeToken<GitState>() {
            }.getType();
            Path path = gitPath.resolve("state");
            JsonReader reader = new JsonReader(new FileReader(path.toString()));
            GitState state = gson.fromJson(reader, typeToken);
            addFiles.clear();
            addFiles.addAll(state.addFiles.stream().map(Path::of).collect(Collectors.toList()));
            branches.clear();
            branches.putAll(state.branches);
            currentBranch = state.currentBranch;
        } catch (IOException e) {
            throw new GitException("Unable to load commit");
        }
    }

    public void saveState(Path gitPath) throws GitException {
        new GitState(addFiles, branches, currentBranch).save(gitPath);
    }

    public List<Path> getAddFiles() {
        return addFiles;
    }

    public List<Path> getUntrackedFiles() {
        return untrackedFiles;
    }

    public List<Path> getModifiedFiles() {
        return modifiedFiles;
    }

    public List<Path> getRemovedFiles() {
        return removedFiles;
    }

    public Map<Path, String> getRepFiles() {
        return repFiles;
    }

    public Map<Path, Path> getRenamedFiles() {
        return renamedFiles;
    }

    public Map<String, String> getBranches() {
        return branches;
    }

    public String getCurrentBranch() {
        return currentBranch;
    }


    public void setCurrentBranch(String newCurrentBranch) {
        currentBranch = newCurrentBranch;
    }


    public void updateFiles(GitCommit HEAD) throws GitException {
        repFiles.clear();
        renamedFiles.clear();
        for (Map.Entry<Path, Path> e : HEAD.allFiles(workingDir.resolve(".git")).entrySet()) {
            repFiles.put(e.getKey(), GitApp.hash(e.getValue()));
        }
        untrackedFiles.clear();
        modifiedFiles.clear();
        removedFiles.clear();
        try {
            Files.walkFileTree(workingDir, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    if (dir.equals(workingDir.resolve(".git"))) {
                        return FileVisitResult.SKIP_SUBTREE;
                    }
                    return super.preVisitDirectory(dir, attrs);
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    String currentHash = GitApp.hash(file);
                    if (!repFiles.containsKey(file)) {
                        boolean renamed = false;
                        for (Map.Entry<Path, String> e : repFiles.entrySet()) {
                            if (e.getValue().equals(currentHash) && !Files.isRegularFile(e.getKey())) {
                                renamedFiles.put(e.getKey(), file);
                                modifiedFiles.add(file);
                                renamed = true;
                                break;
                            }
                        }
                        if (!addFiles.contains(file) && !renamed) {
                            untrackedFiles.add(file);
                        }
                    } else if (repFiles.containsKey(file) && !currentHash.equals(repFiles.get(file))) {
                        modifiedFiles.add(file);
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) {
                    return FileVisitResult.SKIP_SUBTREE;
                }
            });
        } catch (IOException e) {
            throw new GitException("error");
        }
        for (Path p : repFiles.keySet()) {
            if (!Files.isRegularFile(p) && !renamedFiles.containsKey(p)) {
                removedFiles.add(p);
            }
        }
    }

    @Override
    public String toString() {
        final Gson gson = new Gson();
        return gson.toJson(this);
    }


}
