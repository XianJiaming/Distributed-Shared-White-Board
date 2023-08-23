/**
 * @Name: Jiaming XIAN
 * @Student_ID: 1336110
 */

package main;

import gui.WhiteBoardGUI;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import service.WhiteBoardServerService;
import shape.Line;
import shape.*;
import shape.Rectangle;
import shape.Shape;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.Key;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class JoinWhiteBoard {
    private static String serverIP;
    private static int serverPort;
    private static String userName;
    private static Socket socket;
    private static BufferedReader reader;
    private static BufferedWriter writer;
    private static List<String> userNameList = new ArrayList<>();
    private static List<Shape> shapeList = new ArrayList<>();
    private static WhiteBoardServerService serverService;
    private static WhiteBoardGUI clientGUI;
    private static final JSONParser parser = new JSONParser();
    public static void main(String[] args) {
        if (args.length == 3) {
            serverIP = args[0];
            serverPort = Integer.parseInt(args[1]);
            userName = args[2];
        }
        else {
            System.out.println("Usage: java JoinWhiteBoard <serverIPAddress> <serverPort> username");
            System.exit(1);
        }

        try {
            socket = new Socket(serverIP, serverPort);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            // request a join to server
            JSONObject outObject = new JSONObject();
            outObject.put("request", "join");
            outObject.put("userName", userName);
            sendEncrypted(outObject.toJSONString(), writer);

            String message = reader.readLine();
            message = decryptMessage(message);
            parseMessage(message);

            Registry serverRegistry = LocateRegistry.getRegistry(1099);
            serverService = (WhiteBoardServerService) serverRegistry.lookup("WhiteBoardServerService");

            userNameList.addAll(serverService.getUserNames());
            shapeList.addAll(serverService.getShapes());
            serverService.addUserName(userName);

            clientGUI = new WhiteBoardGUI(userName, serverService, null, userNameList, shapeList, false, writer);

            while (true) {
                message = reader.readLine();
                if (message != null) {
                    message = decryptMessage(message);
                    parseMessage(message);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static synchronized void parseMessage(String message) throws ParseException, IOException {
        JSONObject inObject = (JSONObject) parser.parse(message);
        String response = (String) inObject.get("response");
        JSONObject outObject;
        switch (response) {
            case "acceptJoin":
                userName = (String) inObject.get("userName");
                return;

            case "declineJoin":
                System.out.println("Join has been declined!");
                writer.close();
                reader.close();
                socket.close();
                System.exit(0);
                return;

            case "addUserName":
                String addedUserName = (String) inObject.get("userName");
                userNameList.add(addedUserName);
                clientGUI.printUserNameList();
                return;

            case "removeUserName":
                String removedUserName = (String) inObject.get("userName");
                userNameList.remove(removedUserName);
                clientGUI.printUserNameList();
                return;

            case "addShape":
                JSONObject shapeObject = (JSONObject) inObject.get("Shape");
                Shape newShape = parseShape(shapeObject);
                shapeList.add(newShape);
                clientGUI.canvasRepaint();
                return;

            case "addChatMessage":
                String chatMessage = (String) inObject.get("chatMessage");
                clientGUI.printChatMessage(chatMessage);
                return;

            case "newWhiteBoard":
                shapeList.clear();
                clientGUI.canvasRepaint();
                return;

            case "openWhiteBoard":
                shapeList.clear();
                shapeList.addAll(serverService.getShapes());
                clientGUI.canvasRepaint();
                return;

            case "kick":
                outObject = new JSONObject();
                outObject.put("request", "quit");
                sendEncrypted(outObject.toJSONString(), writer);
                writer.close();
                reader.close();
                socket.close();
                System.out.println("You have been kicked!");
                System.exit(0);
                return;

            case "quit":
                outObject = new JSONObject();
                outObject.put("request", "quit");
                sendEncrypted(outObject.toJSONString(), writer);
                writer.close();
                reader.close();
                socket.close();
                System.out.println("The server disconnected!");
                System.exit(0);
                return;

            default:
                System.out.println("Wrong JSON Command!");
        }
    }

    private static Shape parseShape(JSONObject shapeObject) {
        String shapeType = (String) shapeObject.get("type");
        return switch (shapeType) {
            case "Line" ->
                    new Line(((Long) shapeObject.get("x1")).intValue(), ((Long) shapeObject.get("y1")).intValue(),
                            ((Long) shapeObject.get("x2")).intValue(), ((Long) shapeObject.get("y2")).intValue(),
                            new Color(((Long) shapeObject.get("colorValue")).intValue()), ((Long) shapeObject.get("strokeValue")).intValue());
            case "Circle" ->
                    new Circle(((Long) shapeObject.get("x1")).intValue(), ((Long) shapeObject.get("y1")).intValue(), ((Long) shapeObject.get("diameter")).intValue(),
                            new Color(((Long) shapeObject.get("colorValue")).intValue()), ((Long) shapeObject.get("strokeValue")).intValue());
            case "Oval" ->
                    new Oval(((Long) shapeObject.get("x1")).intValue(), ((Long) shapeObject.get("y1")).intValue(),
                            ((Long) shapeObject.get("width")).intValue(), ((Long) shapeObject.get("height")).intValue(),
                            new Color(((Long) shapeObject.get("colorValue")).intValue()), ((Long) shapeObject.get("strokeValue")).intValue());
            case "Rectangle" ->
                    new Rectangle(((Long) shapeObject.get("x1")).intValue(), ((Long) shapeObject.get("y1")).intValue(),
                            ((Long) shapeObject.get("width")).intValue(), ((Long) shapeObject.get("height")).intValue(),
                            new Color(((Long) shapeObject.get("colorValue")).intValue()), ((Long) shapeObject.get("strokeValue")).intValue());
            case "Text" ->
                    new Text(((Long) shapeObject.get("x1")).intValue(), ((Long) shapeObject.get("y1")).intValue(),
                            (String) shapeObject.get("text"), new Color(((Long) shapeObject.get("colorValue")).intValue()),
                            (String) shapeObject.get("fontName"), ((Long) shapeObject.get("fontStyle")).intValue(), ((Long) shapeObject.get("fontSize")).intValue());
            default -> null;
        };
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
