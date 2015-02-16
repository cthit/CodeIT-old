package utils;

import javax.tools.JavaCompiler;
import javax.tools.SimpleJavaFileObject;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JavaSourceFromString extends SimpleJavaFileObject
{
    final String code;

    public JavaSourceFromString(String name, String code)
    {
        super(URI.create(new StringBuilder().append("string:///").append(name).toString()), Kind.SOURCE);
        this.code = code;
    }

    public CharSequence getCharContent(boolean ignoreEncodingErrors)
    {
        return this.code;
    }

    public static Object compile(String code, String className, String packageName) {
        File compilationPath = new File("compiled/");
        JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
        if (javaCompiler == null) {
            throw new RuntimeException("Couldn't instantiate the java compiler");
        }

        JavaSourceFromString jsfs = new JavaSourceFromString(className, code);

        Iterable fileObjects = Arrays.asList(new JavaSourceFromString[] { jsfs });

        List options = new ArrayList();
        options.add("-d");
        options.add(compilationPath.getAbsolutePath());
        options.add("-classpath");
        URLClassLoader urlClassLoader = (URLClassLoader)Thread.currentThread().getContextClassLoader();
        StringBuilder sb = new StringBuilder();
        for (URL url : urlClassLoader.getURLs()) {
            sb.append(url.getFile()).append(File.pathSeparator);
        }
        sb.append("source.jar" + File.pathSeparator);
        sb.append(compilationPath.getAbsolutePath());
        options.add(sb.toString());

        StringWriter output = new StringWriter();
        boolean success = javaCompiler.getTask(output, null, null, options, null, fileObjects).call().booleanValue();
        if (!success) {
            throw new RuntimeException(new StringBuilder().append("Compilation failed :").append(output).toString());
        }

        System.out.println("Class has been successfully compiled");
        File root = new File("compiled");
        // Load and instantiate compiled class.
        URLClassLoader classLoader = null;
        try {
            classLoader = URLClassLoader.newInstance(new URL[]{root.toURI().toURL()});
        } catch (MalformedURLException e) {
            throw new RuntimeException("MalformedURL for path: " + root);
        }
        className = className.substring(0, className.indexOf("."));
        Class<?> cls = null;
        try {
            cls = Class.forName(packageName + "." + className, true, classLoader);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Couldn't find class: " + packageName + "." + className);
        }
        Object instance = null;
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