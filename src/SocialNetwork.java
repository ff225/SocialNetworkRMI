import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface SocialNetwork extends Remote {

    boolean createUser(String username, String pwd) throws RemoteException;

    boolean sendFriendRequest(String sender, String receiver) throws RemoteException;

    void acceptFriendRequest(String sender, String receiver) throws RemoteException;

    boolean removeFriend(String sender, String friend) throws RemoteException;

    List<String> getPendingFriendRequests(String username) throws RemoteException;

    List<String> getFriends(String username) throws RemoteException;

}
