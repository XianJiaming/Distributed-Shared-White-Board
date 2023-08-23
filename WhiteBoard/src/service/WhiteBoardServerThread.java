/**
 * @Name: Jiaming XIAN
 * @Student_ID: 1336110
 */

package service;

import entity.User;
import gui.WhiteBoardGUI;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.security.Key;
import java.util.Base64;
import java.util.List;

public class WhiteBoardServerThread extends Thread{
    private Socket socket;
    private final List<User> userList;
    private final List<String> userNameList;
    private final WhiteBoardGUI serverGUI;
    private final JSONParser parser = new JSONParser();

    public WhiteBoardServerThread (Socket socket, List<User> userList, List<String> userNameList, WhiteBoardServerService serverService, WhiteBoardGUI serverGUI) {
        this.socket = socket;
        this.userList = userList;
        this.userNameList = userNameList;
        this.serverGUI = serverGUI;
    }

    @Override
    public void run() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {

            while (socket != null && !socket.isClosed() && reader != null && writer != null) {
                String message = reader.readLine();
                message = decryptMessage(message);
                parseMessage(message, reader, writer);
            }

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    // ("request", "join"), ("request", "quit")
    private synchronized void parseMessage(String message, BufferedReader reader, BufferedWriter writer) throws ParseException, IOException {

        JSONObject inObject = (JSONObject) parser.parse(message);
        String request = (String) inObject.get("request");
        JSONObject outObject = new JSONObject();
        switch (request) {
            case "join":
                String UserName = (String) inObject.get("userName");
                String dialogMessage = UserName + " is requesting to join the session. Do you accept?" + System.lineSeparator() + System.lineSeparator()
                        + "IP address:     " + socket.getInetAddress().getHostAddress() + System.lineSeparator() + "Port:                 " + socket.getPort();
                String[] buttonLabels = {"Accept", "Decline"};
                int option = JOptionPane.showOptionDialog(null, dialogMessage, "Join request",
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, buttonLabels, buttonLabels[0]);
                if (option == JOptionPane.YES_OPTION) {
                    outObject.put("response", "acceptJoin");
                    String uniqueUserName = generateUniqueUserName(UserName);
                    outObject.put("userName", uniqueUserName);
                    sendEncrypted(outObject.toJSONString(), writer);
                    User user = new User(uniqueUserName, socket, reader, writer);
                    userList.add(user);
                    userNameList.add(uniqueUserName);
                    serverGUI.printUserNameList();
                } else {
                    outObject.put("response", "declineJoin");
                    sendEncrypted(outObject.toJSONString(), writer);

                    writer.close();
                    reader.close();
                    socket.close();
                }
                break;

            case "quit":
                writer.close();
                reader.close();
                socket.close();
                userList.removeIf(user -> user.getSocket().equals(socket));
                serverGUI.printUserNameList();
                break;

            case "addShape":
                serverGUI.canvasRepaint();
                break;

            case "addChatMessage":
                String chatMessage = (String) inObject.get("chatMessage");
                serverGUI.printChatMessage(chatMessage);
                break;

        }
    }

    private String generateUniqueUserName(String userName){
        if (!userNameList.contains(userName)) {
            return userName;
        }
        else {
            int index = 1;
            while (true) {
                String uniqueUserName = userName + "_" + index;
                if (!userNameList.contains(uniqueUserName)) {
                    return uniqueUserName;
                }
                index++;
            }
        }
    }

    private static void sendEncrypted(String message, BufferedWriter writer){
        String key = "5v8y/B?D(G+KbPeS";
        Key aesKey = new SecretKeySpec(key.getBytes(), "AES");
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, aesKey);
            byte[] encrypted = cipher.doFinal(message.getBytes("UTF-8"));
            String encodedText = Base64.getEncoder().encodeToString(encrypted);
            writer.write(encodedText + System.lineSeparator());
            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String decryptMessage(String message){
        try {
            String key = "5v8y/B?D(G+KbPeS";
            Key aesKey = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, aesKey);
            message = new String(cipher.doFinal(Base64.getDecoder().decode(message)), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return message;
    }

}
