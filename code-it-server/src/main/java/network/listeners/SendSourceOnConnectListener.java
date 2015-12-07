package network.listeners;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import it.tejp.codeit.common.network.Message;
import it.tejp.codeit.common.network.SourceFile;

import java.io.File;

/**
 * Created by tejp on 19/10/15.
 */
public class SendSourceOnConnectListener extends Listener {

    private File sourceFile;

    public SendSourceOnConnectListener(File sourceFile) {
        this.sourceFile = sourceFile;
    }

    public void setSourceFile(File sourceFile) {
        this.sourceFile = sourceFile;
    }

    public File getSourceFile() {
        return sourceFile;
    }

    @Override
    public void connected(Connection connection) {
        sendSources(connection);
    }

    @Override
    public void received(Connection connection, Object object) {
        if (object instanceof Message) {
            Message message = (Message) object;

            if (message.equals(Message.REQUEST_SOURCES.message)) {
                sendSources(connection);
            }
        }
    }

    private void sendSources(Connection connection) {
        connection.sendTCP(new SourceFile(sourceFile));
    }
}
