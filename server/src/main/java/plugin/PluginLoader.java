/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package plugin;

import it.chalmers.digit.codeit.common.utils.FileScanner;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class PluginLoader {
    public static final String PLUGIN_PATH = "plugin/";
    public static final String IDE_PLUGIN_PATH = "target/classes/scratch/construction/plugin/exported/";

    private PluginLoader() {
    }

    private static List<Class<?>> getPluginClasses(Class annotationType) {
        final File folder = getValidFolder();
        if (folder == null) {
            return new ArrayList<>();
        }
        final List<File> files = FileScanner.getFiles(folder);
        final PluginClassLoader pluginClassLoader = new PluginClassLoader(folder.getPath() + "/");
        final List<Class<?>> classList = new ArrayList<>();
        for (final File file : files) {
            final String fileName = file.getName();
            final String strippedName = fileName.substring(0, fileName.indexOf(".class"));
            try {
                final Class loadedClass = pluginClassLoader.loadClass(strippedName);
                if (loadedClass.getAnnotation(annotationType) != null) {
                    classList.add(loadedClass);
                }
            } catch (ClassNotFoundException e) {
                Logger.getLogger(PluginLoader.class.getName()).log(Level.SEVERE, null, e);
            }
        }
        return classList;
    }

    private static List<Pluggable<?>> getPluginsFromPluginClasses(List<Class<?>> classList) {
        List<Pluggable<?>> pluginList = new ArrayList<>();
        for(final Class<?> aClass : classList ) {
            Object newInstance = null;

            try {
                newInstance = aClass.newInstance();
            } catch (IllegalAccessException ex) {
                Logger.getLogger(PluginLoader.class.getName()).log(Level.SEVERE, null, ex);
            } catch(InstantiationException exc) {
                Logger.getLogger(PluginLoader.class.getName()).log(Level.SEVERE, null, exc);
            }

            pluginList.add((Pluggable<?>) newInstance);
        }
        return pluginList;
    }

    public static List<Pluggable<?>> loadPlugins(Class anotationType) {
        return getPluginsFromPluginClasses(getPluginClasses(anotationType));
    }

    public static File getValidFolder() {
        File[] folderToCheck = {
                new File(PLUGIN_PATH),
                new File(IDE_PLUGIN_PATH)
        };
        for (File folder : folderToCheck) {
            if (folder.exists()) {
                return folder;
            }
        }
        return null;
    }
}
