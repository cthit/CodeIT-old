/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Ivar
 */
final public class FileScanner {

    private FileScanner() {

    }

    public static List<File> getFiles(File path) {
        final List<File> files = new ArrayList<File>();
        final File[] entries = path.listFiles();
        
        if (entries != null) {
            for (final File f : entries) {
                if (!f.isDirectory()){
                    files.add(f);
                }
            }
        }
        return files;
    }
}
