/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package plugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class PluginClassLoader extends ClassLoader{
    public static final String EXPORTED_PACKAGE = "scratch.construction.plugin.exported";

    private final String folderInUse;

    public PluginClassLoader(String folder) {
        folderInUse = folder;
    }

    @Override
    public Class<?> findClass (String name) {
        final byte[] data = loadClassData(name);
        return defineClass(EXPORTED_PACKAGE + "." + name, data, 0, data.length);
    }
    
    private byte[] loadClassData(String name){
        final File f = new File(folderInUse +name+".class");

        try {
            return Files.readAllBytes(f.toPath());
        } catch (IOException e){
            Logger.getLogger(PluginClassLoader.class.getName()).log(Level.SEVERE, null, e);
            return new byte[0];
        }
       
        
    }
}
