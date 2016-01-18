package it.chalmers.digit.codeit.common.utils;

import javax.tools.JavaCompiler;
import javax.tools.SimpleJavaFileObject;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JavaSourceFromString extends SimpleJavaFileObject
{
    final String code;

    public JavaSourceFromString(String name, String code)
    {
        super(URI.create("string:///" + name), Kind.SOURCE);
        this.code = code;
    }

    public CharSequence getCharContent(boolean ignoreEncodingErrors)
    {
        return this.code;
    }

    public static Object compile(String compilePath, String code) {
        File compPath = new File(compilePath);
        String className = getClassName(code);
        String packageName = getPackageName(code);
        classCompile(className, code, compPath);

        System.out.println("Class has been successfully compiled");
        File root = new File("compiled");
        return createInstanceFromClass(root, className, packageName);
    }

    private static void classCompile (String className, String code, File compilationPath) {
        JavaSourceFromString jsfs = new JavaSourceFromString(className, code);

        Iterable<JavaSourceFromString> fileObjects = Collections.singletonList(jsfs);

        List<String> options = createCompileOptions(compilationPath);

        StringWriter output = new StringWriter();
        JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
        if (javaCompiler == null) {
            throw new RuntimeException("Couldn't instantiate the java compiler");
        }

        boolean success = javaCompiler.getTask(output, null, null, options, null, fileObjects).call();
        if (!success) {
            throw new RuntimeException("Compilation failed :" + output);
        }
    }

    private static List<String> createCompileOptions(File compilationPath) {
        List<String> options = new ArrayList<>();
        options.add("-d");
        options.add(compilationPath.getAbsolutePath());
        options.add("-classpath");

        URLClassLoader urlClassLoader = (URLClassLoader)Thread.currentThread().getContextClassLoader();
        StringBuilder sb = new StringBuilder();
        for (URL url : urlClassLoader.getURLs()) {
            sb.append(url.getFile()).append(File.pathSeparator);
        }
        sb.append("source.jar");
        sb.append(File.pathSeparator);
        sb.append(compilationPath.getAbsolutePath());
        options.add(sb.toString());
        return options;
    }
    
    private static String getClassName(String code) {
        Pattern p = Pattern.compile(".*public\\sclass\\s(([a-zA-Z_$][a-zA-Z\\d_$]*)+).*");
        Matcher m = p.matcher(code);

        if (m.find()) {
            System.out.println(m.group(1));
        }
        return null;
    }

    private static String getPackageName(String code) {
        Pattern p = Pattern.compile(".*package\\s([a-z]+(\\.[a-z]+)*;)");
        Matcher m = p.matcher(code);

        if (m.find()) {
            System.out.println(m.group(1));
        }
        return null;
    }

    //
    // Separate methods for creating a binary and creating an instance of that binary.

    public static Object createInstanceFromClass(File root, String className, String packageName) {
        // Load and instantiate compiled class.
        URLClassLoader classLoader;
        try {
            classLoader = URLClassLoader.newInstance(new URL[]{root.toURI().toURL()});
        } catch (MalformedURLException e) {
            throw new RuntimeException("MalformedURL for path: " + root);
        }
        className = className.substring(0, className.indexOf("."));
        Class<?> cls;
        try {
            cls = Class.forName(packageName + "." + className, true, classLoader);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Couldn't find class: " + packageName + "." + className);
        }
        Object instance;
        try {
            instance = cls.newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException("Couldn't instantiate class: " + packageName + "." + className);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Access denied for class: " + packageName + "." + className);
        }

        return instance;
    }
}