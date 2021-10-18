package ru.hse.java.implementor;


import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class ImplementorImpl implements Implementor {

    private final Path outputDirectory;
    private final StringBuilder sb;
    private String className;
    private static final String EL = System.lineSeparator();
    private static final String IMPL = "Impl";
    private static final char FS = File.separatorChar;


    public ImplementorImpl(String outputDirectory) {
        this.outputDirectory = Path.of(outputDirectory);
        sb = new StringBuilder();
    }

    @Override
    public String implementFromDirectory(String directoryPath, String className) throws ImplementorException {
        Path path = Path.of(directoryPath);
        try {
            ClassLoader classLoader = URLClassLoader.newInstance(new URL[]{path.toUri().toURL()});
            return implement(classLoader.loadClass(className), className, true);
        } catch (final MalformedURLException | ClassNotFoundException e) {
            throw new ImplementorException(e.getMessage());
        }
    }

    @Override
    public String implementFromStandardLibrary(String className) throws ImplementorException {
        try {
            ClassLoader classLoader = ClassLoader.getSystemClassLoader();
            return implement(classLoader.loadClass(className), className, false);
        } catch (ClassNotFoundException e) {
            throw new ImplementorException(e.getMessage());
        }
    }

    private String implement(Class<?> clazz, String className, boolean fromDirectory) throws ImplementorException {
        if (Modifier.isPrivate(clazz.getModifiers()) || Modifier.isFinal(clazz.getModifiers())) {
            throw new ImplementorException("Private of final class");
        }
        this.className = clazz.getSimpleName() + IMPL;
        Path generatedFile;
        String returnName;
        if (fromDirectory) {
            generatedFile = outputDirectory.resolve(clazz.getPackageName().replace('.', FS) + FS + clazz.getSimpleName() + IMPL + ".java");
            returnName = className + IMPL;
        } else {
            generatedFile = outputDirectory.resolve(clazz.getSimpleName() + IMPL + ".java");
            returnName = clazz.getSimpleName() + IMPL;
        }
        try {
            Files.createDirectories(generatedFile.getParent());
            try (BufferedWriter writer = Files.newBufferedWriter(generatedFile, StandardCharsets.UTF_8)) {
                writer.write(toUnicode(generateClass(clazz, fromDirectory)));
            } catch (IOException e) {
                throw new ImplementorException("Can't write to file");
            }
        } catch (IOException e) {
            throw new ImplementorException("Can't create output directories");
        }
        return returnName;
    }

    private static String toUnicode(String in) {
        StringBuilder strBuilder = new StringBuilder();
        in.chars().forEachOrdered(c -> strBuilder.append(c >= 128 ? String.format("\\u%04X", c) : (char) c));
        return strBuilder.toString();
    }

    public String generateClass(Class<?> clazz, boolean fromDirectory) throws ImplementorException {
        sb.setLength(0);
        if (fromDirectory) {
            sb.append(String.format("package %s;", clazz.getPackageName()))
                    .append(EL);
        }
        String prefixName = clazz.getName().substring(0, clazz.getName().length() - clazz.getSimpleName().length());
        sb.append(String.format("public class %s%s %s %s ",
                clazz.getSimpleName(),
                IMPL,
                clazz.isInterface() ? "implements" : "extends",
                dollarToDot(prefixName) + clazz.getSimpleName()));
        sb.append("{").append(EL);
        if (!clazz.isInterface()) {
            addConstructors(clazz);
        }
        addMethods(clazz);
        sb.append("}").append(EL);
        return sb.toString();
    }

    private void addMethods(Class<?> clazz) {
        Set<Method> methods = new TreeSet<>(Comparator
                .comparing(Method::getName)
                .thenComparing(m -> m.getReturnType().toString())
                .thenComparing(m -> Arrays.toString(m.getParameterTypes())));
        methods.addAll(Arrays.asList(clazz.getMethods()));
        Class<?> sup = clazz;
        while (sup != null) {
            methods.addAll(Arrays.asList(sup.getDeclaredMethods()));
            sup = sup.getSuperclass();
        }
        for (Method method : methods) {
            if (Modifier.isAbstract(method.getModifiers())) {
                sb.append("@Override").append(EL);
                addExecutable(method, dollarToDot(method.getReturnType().getTypeName()), method.getName());
            }
        }
    }

    private void addConstructors(Class<?> clazz) throws ImplementorException {
        List<Constructor<?>> constructors = Arrays.stream(clazz.getDeclaredConstructors())
                .filter(c -> !Modifier.isPrivate(c.getModifiers()))
                .collect(Collectors.toList());
        if (constructors.isEmpty()) {
            throw new ImplementorException("No available constructors");
        }
        for (Constructor<?> constructor : constructors) {
            addExecutable(constructor, "", className);
        }
    }

    private void addExecutable(Executable e, String ret, String name) {
        String modifierVis = Modifier.isProtected(e.getModifiers()) ? "protected" : "public";
        sb.append(String.format("%s %s %s(%s)", modifierVis, ret, name, getArgs(e, true)));
        Class<?>[] exceptions = e.getExceptionTypes();
        if (exceptions.length > 0) {
            StringJoiner joiner = new StringJoiner(", ", " throws ", "");
            for (Class<?> exception : exceptions) {
                joiner.add(dollarToDot(exception.getTypeName()));
            }
            sb.append(joiner.toString());
        }
        sb.append("{").append(EL);
        if (e instanceof Method) {
            sb.append(String.format("return %s;", getReturnValue((Method) e, ret)));
        } else {
            sb.append(String.format("super(%s);", getArgs(e, false)));
        }
        sb.append("}").append(EL);
    }

    private static String getReturnValue(Method m, String returnType) {
        if (!m.getReturnType().isPrimitive()) {
            return "null";
        } else if (returnType.equals("boolean")) {
            return "false";
        } else if (returnType.equals("void")) {
            return "";
        } else {
            return "0";
        }
    }

    private static String dollarToDot(String s) {
        return s.replace('$', '.');
    }

    private static String getArgs(Executable m, boolean withTypes) {
        Class<?>[] arguments = m.getParameterTypes();
        StringJoiner argsBuilder = new StringJoiner(", ");
        for (int i = 0; i < arguments.length; i++) {
            if (withTypes) {
                argsBuilder.add(
                        String.format("%s a%d",
                                dollarToDot(arguments[i].getTypeName()),
                                i));
            } else {
                argsBuilder.add(String.format("a%d", i));
            }
        }
        return argsBuilder.toString();
    }

}
