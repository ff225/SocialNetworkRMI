import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.Timestamp;
import java.util.*;

public class SocialNetworkServer extends UnicastRemoteObject implements SocialNetwork {
    private final Map<String, List<String>> friendRequests;
    private final Map<String, List<String>> friends;
    private final Map<String, List<String>> messages;

    private final Map<String, Map<String, List<String>>> posts;
    private static DataUser ds;

    public SocialNetworkServer() throws RemoteException {
        friendRequests = new HashMap<>();
        friends = new HashMap<>();
        messages = new HashMap<>();
        posts = new HashMap<>();
    }

    @Override
    public boolean createUser(String username, String pwd) throws RemoteException {
        System.out.println(username + ", " + pwd);

        for (User u : ds.getUsers()) {
            if (u.getNickname().equals(username) && u.getPwd().equals(pwd)) {
                if (!friendRequests.containsKey(username)) {
                    friendRequests.put(username, new ArrayList<>());
                    friends.put(username, new ArrayList<>());
                    messages.put(username, new ArrayList<>());
                    posts.put(username, new HashMap<>());

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
    }


    @Override
    public List<String> getFriends(String username) throws RemoteException {
        System.out.println(username + ", " + friends.size());
        return friends.get(username);
    }

    @Override
    public List<String> getPendingFriendRequests(String username) throws RemoteException {
        return friendRequests.get(username);
    }

    @Override
    public List<String> getMessages(String username) throws RemoteException {
        return messages.get(username);
    }

    @Override
    public List<String> deleteAllMessages(String username) throws RemoteException {
        return messages.put(username, new ArrayList<>());
    }

    @Override
    public boolean sendMessage(String sender, String receiver, String message) throws RemoteException {
        if (!friends.get(sender).contains(receiver))
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

    @Override
    public void post(String author, String content) throws RemoteException {
        String uuid = UUID.randomUUID().toString();
        String post = "postId:" + uuid + "\n" + content;
        posts.get(author).put(post, new ArrayList<>());
    }

    @Override
    public HashMap<String, List<String>> getPosts(String username) throws RemoteException {
        return (HashMap<String, List<String>>) posts.get(username);
    }


    @Override
    public void comment(String author, String postAuthor, String uuid, String content) throws RemoteException {
        String comment = author + ": " + content;
        for (String post :
                posts.get(postAuthor).keySet()) {
            if (post.contains(uuid)) {
                posts.get(postAuthor).get(post).add(comment);
            }
        }
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
