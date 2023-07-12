import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;

public interface SocialNetwork extends Remote {

    boolean createUser(String username, String pwd) throws RemoteException;

    boolean sendFriendRequest(String sender, String receiver) throws RemoteException;

    void acceptFriendRequest(String sender, String receiver) throws RemoteException;

    boolean removeFriend(String sender, String friend) throws RemoteException;

    List<String> getPendingFriendRequests(String username) throws RemoteException;

    List<String> getFriends(String username) throws RemoteException;

    List<String> getMessages(String username) throws RemoteException;

    List<String> deleteAllMessages(String username) throws RemoteException;

    boolean sendMessage(String sender, String receiver, String message) throws RemoteException;

    void post(String author, String content) throws RemoteException;

    HashMap<String, List<String>> getPosts(String username) throws RemoteException;
    
    void comment(String author, String postAuthor, String uuid, String content) throws RemoteException;
}
