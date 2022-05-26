package accounts;

public class Account {
    // accounts.Account class
    private final String email;
    private String password;
    private String phoneNumber;
    private String address;
    private String name;

    public Account(String email, String password, String phoneNumber, String address, String name) {
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.name = name;
    }

    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getAddress() { return address; }
    public String getName() { return name; }

    public void setPassword(String password) { this.password = password; }
    public void setAddress(String address) { this.address = address; }
}
