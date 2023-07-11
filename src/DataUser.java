import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataUser {

    private List<User> users;

    public void setUsers() {
        users = new ArrayList<User>();
        try (BufferedReader br = new BufferedReader(new FileReader("/Users/francescofranco/IdeaProjects/SocialNetwork/users.csv"))) {
            String row;
            while ((row = br.readLine()) != null) {
                String[] values = row.split(",");
                users.add(new User(values[0], values[1]));
            }
            //users.forEach(user -> System.out.println(user.getNickname().strip()));
        } catch (IOException e) {
            throw new RuntimeException("Error reading CSV");
        }
    }

    public List<User> getUsers() {
        return users;
    }
}
