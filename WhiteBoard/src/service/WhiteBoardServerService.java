/**
 * @Name: Jiaming XIAN
 * @Student_ID: 1336110
 */

package service;

import shape.Shape;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface WhiteBoardServerService extends Remote {

    List<String> getUserNames() throws RemoteException;

    List<Shape> getShapes() throws RemoteException;

    void addUserName(String userName) throws RemoteException;

    void removeUserName(String userName) throws RemoteException;

    void addShape(Shape shape) throws RemoteException;

    void addChatMessage(String chatMessage) throws RemoteException;

}
