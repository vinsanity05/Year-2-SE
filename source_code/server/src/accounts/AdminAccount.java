package accounts;

public class AdminAccount extends Account {

    public AdminAccount(String email, String password, String phoneNumber, String address) {
        super(email, password, phoneNumber, address, "admin");
    }
}
