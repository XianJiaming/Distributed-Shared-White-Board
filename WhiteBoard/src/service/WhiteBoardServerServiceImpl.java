/**
 * @Name: Jiaming XIAN
 * @Student_ID: 1336110
 */

package service;

import entity.User;

import java.io.BufferedWriter;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.Key;
import java.util.Base64;
import java.util.List;

import org.json.simple.JSONObject;
import shape.Shape;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class WhiteBoardServerServiceImpl extends UnicastRemoteObject implements WhiteBoardServerService {
   
	private final List<User> userList;
    private final List<String> userNameList;
    private final List<Shape> shapeList;

    public WhiteBoardServerServiceImpl(List<User> userList, List<String> userNameList, List<Shape> shapeList) throws RemoteException {
        super();
        this.userList = userList;
        this.userNameList = userNameList;
        this.shapeList = shapeList;
    }

    @Override
    public List<String> getUserNames() throws RemoteException {
        return userNameList;
    }

    @Override
    public List<Shape> getShapes() throws RemoteException {
        return shapeList;
    }

    @Override
    public synchronized void addUserName(String userName) throws RemoteException {
        broadcastNewUser(userName);
    }

    @Override
    public synchronized void removeUserName(String userName) throws RemoteException {
        userNameList.remove(userName);
        broadcastRemovedUser(userName);
    }

    @Override
    public synchronized void addShape(Shape shape) throws RemoteException {
        shapeList.add(shape);
        broadcastNewShape(shape);
    }

    @Override
    public synchronized void addChatMessage(String chatMessage) throws RemoteException {
        broadcastChatMessage(chatMessage);
    }

    private void broadcastNewUser(String userName) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("response", "addUserName");
        jsonObject.put("userName", userName);
        for (User user: userList) {
            if (!userName.equals(user.getUserName())) {
                sendEncrypted(jsonObject.toJSONString(), user.getWriter());
            }
        }
    }

    private void broadcastRemovedUser(String userName) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("response", "removeUserName");
        jsonObject.put("userName", userName);
        for (User user: userList) {
            if (!userName.equals(user.getUserName())) {
                sendEncrypted(jsonObject.toJSONString(), user.getWriter());
            }
        }
    }

    private void broadcastNewShape(Shape shape) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("response", "addShape");
        jsonObject.put("Shape", shape.toJSONObject());
        for (User user: userList) {
            sendEncrypted(jsonObject.toJSONString(), user.getWriter());
        }
    }

    private void broadcastChatMessage(String chatMessage) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("response", "addChatMessage");
        jsonObject.put("chatMessage", chatMessage);
        for (User user: userList) {
           sendEncrypted(jsonObject.toJSONString(), user.getWriter());
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

}
