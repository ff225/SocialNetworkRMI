import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.Timestamp;
import java.util.*;

public class SocialNetworkServer extends UnicastRemoteObject implements SocialNetwork {
    private final Map<String, List<String>> friendRequests;
    private final Map<String, List<String>> friends;
    private final Map<String, List<Message>> messages;
    private final Map<String, List<Post>> posts;
    private static DataUser ds;

    public SocialNetworkServer() throws RemoteException {
        friendRequests = new HashMap<>();
        friends = new HashMap<>();
        messages = new HashMap<>();
        //messages = new ArrayList<>();
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
                    //messages.put(username, new ArrayList<>());
                    posts.put(username, new ArrayList<>());

                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean sendFriendRequest(String sender, String receiver) throws RemoteException {
        return !sender.equals(receiver)
                && !friends.get(sender).contains(receiver)
                && !friendRequests.get(sender).contains(receiver)
                && friendRequests.containsKey(receiver)
                && !friendRequests.get(receiver).contains(sender)
                && friendRequests.get(receiver).add(sender);
    }

    @Override
    public boolean acceptFriendRequest(String sender, String receiver) throws RemoteException {
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
            return friends.get(sender).add(receiver);
        }

        return false;
    }

    @Override
    public boolean deleteFriendRequest(String sender, String receiver) throws RemoteException {
        return friendRequests.get(receiver).removeIf(user -> user.equals(sender));
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
    public List<Message> getMessages(String username) throws RemoteException {
        return messages.get(username);
    }

    @Override
    public void deleteAllMessages(String username) throws RemoteException {
        messages.get(username).clear();
    }

    @Override
    public boolean sendMessage(String sender, String receiver, String message) throws RemoteException {
        if (!friends.get(sender).contains(receiver)) return false;

        System.out.println(sender + ": " + message);

        Message newMessage = new Message(sender, receiver, message);

        if (!messages.containsKey(receiver)) {
            messages.put(receiver, new ArrayList<>());
        }
        if (!messages.containsKey(sender)) messages.put(sender, new ArrayList<>());

        messages.get(sender).removeIf(prevMsg -> prevMsg.getSender().equals(receiver));
        return messages.get(receiver).add(newMessage);
    }

    @Override
    public void post(String author, String content) throws RemoteException {
        String uuid = UUID.randomUUID().toString();
        System.out.println("postId:" + uuid + "\n" + content);
        Post post = new Post(uuid, content);
        posts.get(author).add(post);
    }

    @Override
    public List<Post> getPosts(String username) throws RemoteException {
        return posts.get(username);
    }

    @Override
    public boolean deletePost(String username, String uuid) throws RemoteException {
        return posts.get(username).removeIf(post -> post.getPostId().equals(uuid));
    }

    @Override
    public void deleteAllPosts(String username) throws RemoteException {
        posts.get(username).clear();
    }


    @Override
    public void comment(String author, String postAuthor, String uuid, String content) throws RemoteException {
        System.out.println(author + ": " + content);
        for (Post post : posts.get(postAuthor)) {
            if (post.getPostId().equals(uuid)) {
                Comment comment = new Comment(author, content);
                post.addComment(comment);
                break;
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
