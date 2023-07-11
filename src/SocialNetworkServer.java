import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SocialNetworkServer extends UnicastRemoteObject implements SocialNetwork {
    private final Map<String, List<String>> friendRequests;
    private final Map<String, List<String>> friends;
    private Map<String, List<String>> messages;
    private static DataUser ds;

    public SocialNetworkServer() throws RemoteException {
        friendRequests = new HashMap<>();
        friends = new HashMap<>();
        messages = new HashMap<>();
    }

    @Override
    public boolean createUser(String username, String pwd) throws RemoteException {
        System.out.println(username + ", " + pwd);

        for (User u : ds.getUsers()) {
            if (u.getNickname().equals(username) && u.getPwd().equals(pwd)) {
                if (!friendRequests.containsKey(username)) {
                    friendRequests.put(username, new ArrayList<>());
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean sendFriendRequest(String sender, String receiver) throws RemoteException {
        return !sender.equals(receiver) && friendRequests.containsKey(receiver) && friendRequests.get(receiver).add(sender);
    }

    @Override
    public void acceptFriendRequest(String sender, String receiver) throws RemoteException {
        if (friendRequests.containsKey(receiver) && friendRequests.get(receiver).contains(sender)) {
            friendRequests.get(receiver).remove(sender);

            if (!friends.containsKey(receiver) || !friends.get(receiver).contains(sender)) {
                if (!friends.containsKey(receiver)) {
                    friends.put(receiver, new ArrayList<>());
                }
                friends.get(receiver).add(sender);
            }

            if (!friends.containsKey(sender)) {
                friends.put(sender, new ArrayList<>());
            }
            friends.get(sender).add(receiver);
        }
    }

    @Override
    public boolean removeFriend(String sender, String friend) throws RemoteException {

        return friends.containsKey(sender) && friends.get(sender).remove(friend) && friends.get(friend).remove(sender);

        /*if (friends.containsKey(sender) && friends.get(sender).remove(friend)) {
            return friends.get(friend).remove(sender);
        }
        return false;

         */
    }


    @Override
    public List<String> getFriends(String username) throws RemoteException {
        System.out.println(username + ", " + friends.size());
        return friends.getOrDefault(username, new ArrayList<>());
    }

    @Override
    public List<String> getPendingFriendRequests(String username) throws RemoteException {
        return friendRequests.getOrDefault(username, new ArrayList<>());
    }

    @Override
    public List<String> getMessages(String username) throws RemoteException {
        return messages.getOrDefault(username, new ArrayList<>());
    }

    @Override
    public boolean sendMessage(String sender, String receiver, String message) throws RemoteException {
        if (!friends.getOrDefault(sender, new ArrayList<>()).contains(receiver))
            return false;

        String formattedMessage = sender + ": " + message;

        if (!messages.containsKey(receiver)) {
            messages.put(receiver, new ArrayList<>());
        }
        if (!messages.containsKey(sender))
            messages.put(sender, new ArrayList<>());

        messages.get(sender).removeIf(prevMsg -> prevMsg.contains(receiver));
        return messages.get(receiver).add(formattedMessage);
    }

    public static void main(String[] args) {
        ds = new DataUser();
        ds.setUsers();
        try {
            SocialNetworkServer server = new SocialNetworkServer();
            Naming.rebind("SocialNetwork", server);
            System.out.println("Server avviato");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
