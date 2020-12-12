package communication;

public class AccountData extends SocketData{

    private String name;
    private String email;
    private String password;

    public AccountData(int type, int cpuFreq, String name, String email, String password) {
        super(type, cpuFreq);
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

}
