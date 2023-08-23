/**
 * @Name: Jiaming XIAN
 * @Student_ID: 1336110
 */

package main;

import entity.User;
import gui.WhiteBoardGUI;
import service.WhiteBoardServerService;
import service.WhiteBoardServerServiceImpl;
import service.WhiteBoardServerThread;
import shape.Shape;

import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;

public class CreateWhiteBoard {
    private final static String serverServiceName = "WhiteBoardServerService";
    private static String serverIP;
    private static int serverPort;
    private static String userName;
    private static final List<User> userList = new ArrayList<>();
    private static final List<Shape> shapeList = new ArrayList<>();
    private static final List<String> userNameList = new ArrayList<>();
    private static WhiteBoardGUI serverGUI;

    public static void main(String[] args) {
        if (args.length == 3) {
            serverIP = args[0];
            serverPort = Integer.parseInt(args[1]);
            userName = args[2];
        }
        else {
            System.out.println("Usage: java CreateWhiteBoard <serverIPAddress> <serverPort> username");
            System.exit(1);
        }

        try (ServerSocket serverSocket = new ServerSocket(serverPort)){

            userNameList.add(userName);

            WhiteBoardServerService serverService = new WhiteBoardServerServiceImpl(userList, userNameList, shapeList);
            Registry serverRegistry = LocateRegistry.createRegistry(1099);
            serverRegistry.bind(serverServiceName, serverService);

            serverGUI = new WhiteBoardGUI(userName, serverService, userList, userNameList, shapeList, true, null);

            while (true) {
                Socket socket = serverSocket.accept();
                new WhiteBoardServerThread(socket, userList, userNameList, serverService, serverGUI).start();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
