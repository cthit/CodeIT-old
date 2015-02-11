package utils;

import it.tejp.codeit.api.GameMechanic;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
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
//    super(URI.create(new StringBuilder().append("string:///").append(name.replace('.', '/')).append(Kind.SOURCE.extension).toString()), Kind.SOURCE);
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
        options.add("-Xdiags:verbose");
        options.add("-Xlint:unchecked");
        options.add("-classpath");
        URLClassLoader urlClassLoader = (URLClassLoader)Thread.currentThread().getContextClassLoader();
        StringBuilder sb = new StringBuilder();
        for (URL url : urlClassLoader.getURLs()) {
            sb.append(url.getFile()).append(File.pathSeparator);
        }
        sb.append("/home/tejp/projects/ohmsitsmaterial/CodeIT/source.jar:");
        sb.append(compilationPath.getAbsolutePath());
        options.add(sb.toString());

    options.stream().forEach(e -> System.out.println(" " + e + " "));

        StringWriter output = new StringWriter();
        boolean success = jc.getTask(output, null, null, options, null, fileObjects).call().booleanValue();
        if (!success) {
            throw new Exception(new StringBuilder().append("Compilation failed :").append(output).toString());
        }

        System.out.println("Class has been successfully compiled");
        File root = new File("compiled");
        // Load and instantiate compiled class.
        URLClassLoader classLoader = URLClassLoader.newInstance(new URL[] { root.toURI().toURL() });
        Class<?> cls = Class.forName("pong_sample.PongPaddle", true, classLoader); // Should print "hello".
        Object instance = cls.newInstance();


        System.out.println("tjenna " + ((GameMechanic)instance).createTestGame());

        return instance;

//        String packageFolder = packageName.replace('.', '/');
//        File clazz = new File(new StringBuilder().append("compiled/").append(packageFolder).append("/").append(className.substring(0, className.indexOf("."))).append(".class").toString());
//        File clazzFolder = new File(new StringBuilder().append("compiled/").append(packageFolder).append("/").toString());
//        System.out.println(new StringBuilder().append("Class: ").append(clazz.getPath()).append(", exists: ").append(clazz.exists()).append(", folder exists: ").append(clazzFolder.exists()).toString());

//        URLClassLoader loader = URLClassLoader.newInstance(new URL[] { clazzFolder.toURL() });
//
//        System.out.println("Tjnna: " + new StringBuilder().append(packageName).append(".").append(className.substring(0, className.indexOf("."))).toString());
//        Object newInstance = Class.forName(new StringBuilder().append(packageName).append(".").append(className.substring(0, className.indexOf("."))).toString(), true, loader).newInstance();
//        System.out.println(newInstance);
//        return newInstance;
    }

    public static void main(String[] args)
            throws Exception
    {
        compile("public class CustomProcessor { /*custom stuff*/ }", "CustomProcessor", "");
    }
}