package network;

import java.io.File;

/**
 * Created by tejp on 03/01/15.
 */
public interface NewFileFromClientListener {
    void newFileRecieved(String teamName, File f);
}
