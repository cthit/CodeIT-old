package it.chalmers.digit.codeit.common.utils;

import javax.tools.*;
import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class JavaCompilerHelper {

    private static final String SRC_DIRECTORY = ".";
    private static final String LIB_FOLDER = "lib/source.jar";

    public static Object compile(String code) {
        String packageName = extractPackageName(code);
        String className = extractClassName(code);
        String srcDir = createSourceDir(packageName);
        File sourceFile = createSourceFile(srcDir, code, className);
        compileSourceFile(sourceFile);
        return createInstanceOfSource(packageName, className);
    }

    private static String extractClassName(String code) {
        Pattern p = Pattern.compile(".*\\sclass\\s([a-zA-Z_$][a-zA-Z\\d_$]*)");
        Matcher m = p.matcher(code);
        m.find();
        return m.group(1);
    }

    private static String extractPackageName(String code) {
        Pattern p = Pattern.compile(".*package\\s*([a-zA-Z_$][a-zA-Z\\d_$]*)");
        Matcher m = p.matcher(code);
        if(m.find()){
            System.out.println(m.group(1));
            return m.group(1);
        } else {
            return "";
        }
    }

    private static Object createInstanceOfSource(String packageName, String className) {
        try {
            String qualifiedName = "";
            if(packageName.length() > 0){
                qualifiedName += packageName + ".";
            }
            File root = new File(SRC_DIRECTORY);
            URLClassLoader classLoader = URLClassLoader.newInstance(new URL[] { root.toURI().toURL() });

            qualifiedName += className;
            Class c = Class.forName(qualifiedName, true, classLoader);
            return c.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    private static void compileSourceFile(File sourceFile) {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

        List<String> options = createCompileOptions();

        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
        Iterable<File> jsfo = Collections.singletonList(sourceFile);
        Iterable<? extends JavaFileObject> compileFiles =
                fileManager.getJavaFileObjectsFromFiles(jsfo);
        compiler.getTask(null, null, null, options, null, compileFiles).call();
    }

    private static List<String> createCompileOptions() {
        List<String> options = new ArrayList<>();
        options.add("-classpath");

        URLClassLoader urlClassLoader = (URLClassLoader)Thread.currentThread().getContextClassLoader();
        StringBuilder sb = new StringBuilder();
        for (URL url : urlClassLoader.getURLs()) {
            sb.append(url.getFile()).append(File.pathSeparator);
        }
        sb.append(LIB_FOLDER);
        options.add(sb.toString());
        return options;
    }

    private static File createSourceFile(String srcDir, String code, String name) {
        String path = srcDir + "/" + name + ".java";
        File f = new File(path);

        PrintWriter pw = null;
        try {
            f.createNewFile();
            pw = new PrintWriter(f);
            pw.write(code);
            pw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return f;
    }

    private static String createSourceDir(String packageName) {
        String packageNamePath = packageName.replaceAll("\\.", "/");
        String path = SRC_DIRECTORY + "/" + packageNamePath;
        System.out.println(path);
        File sourceDir = new File(path);
        sourceDir.mkdirs();
        return path;
    }

}
