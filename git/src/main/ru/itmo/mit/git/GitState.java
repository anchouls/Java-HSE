package ru.itmo.mit.git;

import com.google.gson.Gson;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class GitState {

    public final List<String> addFiles;
    public final Map<String, String> branches;
    public String currentBranch;

    public GitState(List<Path> addFiles, Map<String, String> branches, String currentBranch) throws GitException {
        this.addFiles = addFiles.stream().map(Objects::toString).collect(Collectors.toList());
        this.branches = branches;
        this.currentBranch = currentBranch;
    }

    public void save(Path gitPath) throws GitException {
        Path file = gitPath.resolve("state");
        try (BufferedWriter writer = Files.newBufferedWriter(file)) {
            writer.write(toString());
        } catch (IOException e) {
            throw new GitException("unable to save current state: \n" + e.getMessage(), e.getCause());
        }
    }

    @Override
    public String toString() {
        final Gson gson = new Gson();
        return gson.toJson(this);
    }
}
