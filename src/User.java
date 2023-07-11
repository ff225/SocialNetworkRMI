public class User {
    private String nickname;
    private String pwd;

    public User(String nickname, String pwd/*, List<String> reqFriends, List<String> friends */) {
        setNickname(nickname);
        setPwd(pwd);
    }


    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

}
