package it.chalmers.digit.codeit.server.utils;

import javax.tools.JavaCompiler;
import javax.tools.SimpleJavaFileObject;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.StringWriter;
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

    public static Object compile(String code, String className, String packageName) throws Exception {
        File compilationPath = new File("compiled/");
        JavaCompiler jc = ToolProvider.getSystemJavaCompiler();
        if (jc == null) throw new Exception("Compiler unavailable");

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
        sb.append("source_server.jar:");
        sb.append(compilationPath.getAbsolutePath());
        options.add(sb.toString());
        System.out.println("WORKING DIRECTORY: "+ System.getProperty("user.dir"));
        StringWriter output = new StringWriter();
        boolean success = jc.getTask(output, null, null, options, null, fileObjects).call().booleanValue();
        if (!success) {
            throw new Exception(new StringBuilder().append("Compilation failed :").append(output).toString());
        }

        System.out.println("Class has been successfully compiled");
        File root = new File("compiled");
        // Load and instantiate compiled class.
        URLClassLoader classLoader = URLClassLoader.newInstance(new URL[] { root.toURI().toURL() });
        className = className.substring(0, className.indexOf("."));
        Class<?> cls = Class.forName(packageName + "." + className, true, classLoader);
        Object instance = cls.newInstance();

        return instance;
    }

    public static void main(String[] args)
            throws Exception
    {
        compile("public class CustomProcessor { /*custom stuff*/ }", "CustomProcessor", "");
    }
}