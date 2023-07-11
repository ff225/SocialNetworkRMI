import java.rmi.Naming;
import java.rmi.RemoteException;
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
        if (!initUser(username, pwd))
            throw new RuntimeException("username o password errati");

        while (true) {
            System.out.println("\nOpzioni:");
            System.out.println("1. Invia richiesta di amicizia");
            System.out.println("2. Richieste di amicizia");
            System.out.println("3. Accetta richiesta di amicizia");
            System.out.println("4. Rimuovi amico");
            System.out.println("5. Visualizza amici");
            System.out.println("0. Esci");

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
                        else
                            System.out.println("Utente non trovato...");

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
        if (args.length != 2)
            throw new RuntimeException("Devi inserire Username e Password");
        SocialNetworkClient client = new SocialNetworkClient();
        client.run(args[0], args[1]);
    }
}
