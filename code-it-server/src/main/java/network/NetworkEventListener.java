package network;

import java.io.File;

/**
 * Created by tejp on 03/01/15.
 */
public interface NetworkEventListener {
    void newFileRecieved(String teamName, File f);
}
