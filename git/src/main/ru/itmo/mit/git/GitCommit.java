package ru.itmo.mit.git;

import com.google.gson.Gson;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class GitCommit {

    public final String hash;
    public String prevCommit;
    public final Map<String, String> prevChangedFiles;
    public final Map<String, String> files;
    public Date date;
    public String message;

    public GitCommit(Path gitPath, List<Path> changedFiles, String message, String prevCommit,
                     Map<Path, Path> renamedFiles, List<Path> removedFiles) throws IOException, GitException {
        this.prevCommit = prevCommit == null ? "" : prevCommit;
        this.message = message;
        date = new Date(System.currentTimeMillis());
        hash = generateHash();
        this.files = new HashMap<>();
        this.prevChangedFiles = new HashMap<>();
        if (prevCommit != null) {
            GitCommit prevGitCommit = GitApp.loadCommit(prevCommit, gitPath);
            prevChangedFiles.putAll(prevGitCommit.prevChangedFiles);
            for (String p : prevGitCommit.files.keySet()) {
                prevChangedFiles.put(p, prevCommit);
            }
        }
        for (Path p : changedFiles) {
            Path newName = gitPath.resolve(generateHash());
            if (renamedFiles.containsKey(p)) {
                this.files.put(renamedFiles.get(p).toString(), lastVersion(p, gitPath).toString());
            } else {
                this.files.put(p.toString(), newName.toString());
                if (!removedFiles.contains(p)) {
                    Files.copy(p, newName);
                }
            }
        }
        save(gitPath);
    }

    private void save(Path gitPath) throws IOException, GitException {
        Path file = gitPath.resolve("commits").resolve(hash);
        Files.createDirectories(file.getParent());
        try (BufferedWriter writer = Files.newBufferedWriter(file)) {
            writer.write(toString());
        } catch (IOException e) {
            throw new GitException("unable to write commit: \n" + e.getMessage(), e.getCause());
        }
    }

    public Path lastVersion(Path p, Path gitPath) throws GitException {
        if (files.containsKey(p.toString())) {
            return Path.of(files.get(p.toString()));
        } else if (prevChangedFiles.containsKey(p.toString())) {
            return Path.of(GitApp.loadCommit(prevChangedFiles.get(p.toString()), gitPath).files.get(p.toString()));
        } else {
            throw new GitException("Repository structure is broken");
        }
    }

    public Map<Path, Path> allFiles(Path gitPath) throws GitException {
        Map<String, String> map = new HashMap<>();
        for (Map.Entry<String, String> e : prevChangedFiles.entrySet()) {
            map.put(e.getKey(), GitApp.loadCommit(e.getValue(), gitPath).files.get(e.getKey()));
        }
        map.putAll(files);
        return map.entrySet().stream().filter(e -> Files.isRegularFile(Path.of(e.getValue())))
                .collect(Collectors.toMap(e -> Path.of(e.getKey()), e -> Path.of(e.getValue())));
    }

    private String generateHash() {
        char[] chars = "abcdef0123456789".toCharArray();
        Random random = new Random();
        char[] buf = new char[40];
        for (int i = 0; i < 40; i++) {
            buf[i] = chars[random.nextInt(chars.length)];
        }
        return new String(buf);
    }

    @Override
    public String toString() {
        final Gson gson = new Gson();
        return gson.toJson(this);
    }
}
