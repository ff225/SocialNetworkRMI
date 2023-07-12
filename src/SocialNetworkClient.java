import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class SocialNetworkClient {
    private SocialNetwork socialNetwork;

    public SocialNetworkClient() {
        try {
            socialNetwork = (SocialNetwork) Naming.lookup("rmi://localhost/SocialNetwork");
            System.out.println("Connessione al server riuscita.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean initUser(String username, String pwd) {
        try {
            return socialNetwork.createUser(username, pwd);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void run(String username, String pwd) {
        Scanner scanner = new Scanner(System.in);
        if (!initUser(username, pwd)) throw new RuntimeException("username o password errati");

        while (true) {
            System.out.println("\nOpzioni:");
            System.out.println("1. Invia richiesta di amicizia");
            System.out.println("2. Richieste di amicizia");
            System.out.println("3. Accetta richiesta di amicizia");
            System.out.println("4. Rimuovi amico");
            System.out.println("5. Visualizza amici");
            System.out.println("6. Invia un messaggio");
            System.out.println("7. Visualizza messaggi");
            System.out.println("8. Cancella tutti i messaggi ricevuti");
            System.out.println("9. Scrivi un post");
            System.out.println("10. Visualizza  i tuoi post");
            System.out.println("11. Visualizza  i post dei tuoi amici");
            System.out.println("12. Commenta un post");
            System.out.println("0. Esci\n\n\n");

            System.out.print("Scelta: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            try {
                switch (choice) {
                    case 1 -> {
                        System.out.print("Username destinatario: ");
                        String receiver = scanner.nextLine();
                        if (socialNetwork.sendFriendRequest(username, receiver))
                            System.out.println("Richiesta di amicizia inviata.");
                        else System.out.println("Utente non trovato...");

                    }
                    case 2 -> {
                        List<String> pendingRequests = socialNetwork.getPendingFriendRequests(username);
                        System.out.println("Richieste di amicizia in attesa per " + username + ":");
                        for (String request : pendingRequests) {
                            System.out.println("- " + request);
                        }
                    }
                    case 3 -> {
                        System.out.print("Username mittente: ");
                        String sender = scanner.nextLine();
                        socialNetwork.acceptFriendRequest(sender, username);
                        System.out.println("Richiesta di amicizia accettata.");
                    }
                    case 4 -> {
                        System.out.print("Username amico da rimuovere: ");
                        String friend = scanner.nextLine();
                        if (socialNetwork.removeFriend(username, friend)) {
                            System.out.println("Amico rimosso.");
                        } else {
                            System.out.println("Non è stato trovato nessun amico " + friend + ".");
                        }
                    }
                    case 5 -> {
                        List<String> friends = socialNetwork.getFriends(username);
                        System.out.println("Amici di " + username + ":");
                        for (String friend : friends) {
                            System.out.println("- " + friend);
                        }
                    }
                    case 6 -> {
                        System.out.print("Username destinatario: ");
                        String messageReceiver = scanner.nextLine();
                        System.out.print("Contenuto del messaggio: ");
                        String messageContent = scanner.nextLine();
                        if (socialNetwork.sendMessage(username, messageReceiver, messageContent))
                            System.out.println("Messaggio inviato.");
                        else System.out.println("Impossibile inviare il messaggio. Non hai amici con questo username.");
                    }
                    case 7 -> {
                        List<String> messages = socialNetwork.getMessages(username);
                        System.out.println("Messaggi per " + username + ":");
                        for (String message : messages) {
                            System.out.println("- " + message);
                        }
                    }
                    case 8 -> socialNetwork.deleteAllMessages(username);
                    case 9 -> {
                        System.out.print("Contenuto del post: ");
                        String postContent = scanner.nextLine();
                        socialNetwork.post(username, postContent);
                        System.out.println("Post pubblicato.");
                    }
                    case 10 -> {
                        HashMap<String, List<String>> posts = socialNetwork.getPosts(username);
                        System.out.println("Post di " + username + ":");
                        for (String post : posts.keySet()) {
                            System.out.println(post);
                            if (!posts.get(post).isEmpty())
                                for (String comment :
                                        posts.get(post)) {
                                    System.out.println("commento: " + comment);
                                }


                        }
                    }
                    case 11 -> {
                        System.out.print("Username: ");
                        String usernameFriend = scanner.nextLine();
                        List<String> friends = socialNetwork.getFriends(username);
                        if (friends.contains(usernameFriend)) {
                            HashMap<String, List<String>> posts = socialNetwork.getPosts(usernameFriend);
                            System.out.println("Post di " + usernameFriend + ":");
                            for (String post : posts.keySet()) {
                                System.out.println(post);
                                if (!posts.get(post).isEmpty())
                                    for (String comment :
                                            posts.get(post)) {
                                        System.out.println("commento: " + comment);
                                    }
                                }
                        } else
                            System.out.println("Non hai amici con questo username.");
                    }
                    case 12 -> {
                        System.out.print("Autore del post: ");
                        String postAuthor = scanner.nextLine();
                        List<String> friends = socialNetwork.getFriends(username);
                        if (postAuthor.equals(username) || friends.contains(postAuthor)) {
                            System.out.print("Post id: ");
                            String uuid = scanner.nextLine();
                            System.out.print("Contenuto del commento: ");
                            String commentContent = scanner.nextLine();
                            socialNetwork.comment(username, postAuthor, uuid, commentContent);
                            System.out.println("Commento pubblicato.");
                        } else
                            System.out.println("Autore non trovato");
                    }
                    case 0 -> {
                        System.out.println("Arrivederci!");
                        System.exit(0);
                    }
                    default -> System.out.println("Scelta non valida.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        if (args.length != 2) throw new RuntimeException("Devi inserire Username e Password");
        SocialNetworkClient client = new SocialNetworkClient();
        client.run(args[0], args[1]);
    }
}
