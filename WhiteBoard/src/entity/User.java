/**
 * @Name: Jiaming XIAN
 * @Student_ID: 1336110
 */

package entity;

import java.io.*;
import java.net.Socket;
import java.util.Objects;

public class User implements Serializable {
    private final String userName;
    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;
    private final String IPAddress;
    private final int port;

    public User(String userName, Socket socket, BufferedReader reader, BufferedWriter writer) {
        this.userName = userName;
        this.socket = socket;
        this.reader = reader;
        this.writer = writer;
        IPAddress = socket.getInetAddress().getHostAddress();
        port = socket.getPort();
    }

    public Socket getSocket() {
        return socket;
    }

    public String getIPAddress() {
        return IPAddress;
    }

    public int getPort() {
        return port;
    }

    public String getUserName() {
        return userName;
    }

    public BufferedReader getReader() {
        return reader;
    }

    public BufferedWriter getWriter() {
        return writer;
    }

    public void close() {
        try {
            reader.close();
            writer.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof User)) {
            return false;
        }
        User other = (User) obj;
        return Objects.equals(IPAddress, other.IPAddress) && port == other.port;
    }

    @Override
    public int hashCode() {
        return Objects.hash(IPAddress, port);
    }
}
