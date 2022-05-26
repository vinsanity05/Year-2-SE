import accounts.Account;
import accounts.AccountManager;
import accounts.AdminAccount;
import contact_tracing.ContactTracingService;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketServer extends Thread {

    private ServerSocket middleman;
    private int port;
    private Socket client;
    private AccountManager accountManager;
    private ContactTracingService contactTracingService;

    public SocketServer(int port) {
        this.port = port;
        this.accountManager = new AccountManager();
        //this.communicationService = new CommunicationService();
        this.contactTracingService = new ContactTracingService();
    }

    @Override
    public void run() {
        try
        {
            while(true) {
                middleman = new ServerSocket(port);
                client = middleman.accept();
                middleman.close();
                PrintWriter out = new PrintWriter(client.getOutputStream(),true);
                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                String line;
                while((line = in.readLine()) != null)
                {
                    String[] params = splitInput(line);
                    if (params[0].equalsIgnoreCase("connected")) {
                        out.println("Welcome, client.");
                        System.out.println("A client has connected successfully.");
                    }
                    // Register account
                    else if (params[0].equalsIgnoreCase("register")) {
                        boolean registered = registerAccount(params);
                        out.println(registered);
                    }
                    // Login request
                    else if (params[0].equalsIgnoreCase("login")) {
                        String status = loginAccount(params);
                        out.println(status);
                    }
                    else if (params[0].equalsIgnoreCase("checkaccount")) {
                        if (accountManager.accountExists(params[1]) != null) {
                            out.println("true");
                        } else {out.println("false"); }
                    }
                    // Request message Id
                    else if (params[0].equalsIgnoreCase("getemail")) {
                        out.println(getEmail(params));
                    }
                    else if (params[0].equalsIgnoreCase("getsms")) {
                        out.println(getSms(params));
                    }
                    else if (params[0].equalsIgnoreCase("sharecontacts")) {
                        out.println(shareContacts(params));
                    }
                    else if (params[0].equalsIgnoreCase("deleteaccount")) {
                        out.println(deleteAccount(params));
                    }
                    else if (params[0].equalsIgnoreCase("editaccount")) {
                        out.println(editAccount(params));
                    }
                }
            }
        }
        catch(IOException e)
        {
            System.out.println(e);
        }
    }

    private boolean registerAccount(String[] params) {
        // Returns true if the account is registered, else returns false
        Account toRegister = new Account(params[1], params[2], params[3], params[4], params[5]);

        boolean registered = accountManager.registerAccount(toRegister);
        if (registered) {
            contactTracingService.communicationService.sendEmail("tts@nhs.com", params[1],
                    "Registration Confirmation", "Your NHS Track And Trace account has " +
                            "been created successfully. Please use the details you entered when registering" +
                            " to sign in. A COVID-19 test kit is on the way to your house and shall be there" +
                            " within the next few days.", null);
            contactTracingService.communicationService.sendSms("111", params[3],
                    "Your NHS Track And Trace account has been created successfully. Please use the details " +
                            "you entered when registering to sign in. A COVID-19 test kit is on the way to your house " +
                            "and shall be there within the next few days.");
        }
        contactTracingService.contactLabMemberForTestKitPrep(toRegister.getName(), toRegister.getAddress());
        return registered;
    }

    private boolean deleteAccount(String[] params) {
        // Delete account
        return accountManager.deleteAccount(params[1]);
    }

    private String editAccount(String[] params) {
        String email = params[1];
        String[] fields = new String[]{params[2], params[3]} ;
        return accountManager.editAccountData(email, fields, Boolean.parseBoolean(params[4]));
    }

    private String shareContacts(String[] params) {
        // Contacts list is params[1]
        System.out.println(params[1]);
        String[] contacts = params[1].split("\\$");
        for (String s : contacts) {
            contactTracingService.contactPersonInContact(s);
        }
        return "Shared contacts with contact tracers.";
    }

    private String loginAccount(String[] params) {
        // Returns true if the password is right, false if it isn't, and "not found" if the account doesn't exist
        String out = "";
        String email = params[1];
        String password = params[2];
        Account account = accountManager.accountExists(email);
        if (account != null) {
            if (account instanceof AdminAccount) {
                if (password.equals(account.getPassword())) {
                    out = "admin";
                }
            } else {
                if (password.equals(account.getPassword())) {
                    out = "true";
                } else {
                    out = "false";
                }
            }
        } else {
            out = "not found";
        }
        return out;
    }

    private String getEmail(String[] params) {
        int messageId = Integer.parseInt(params[1]);
        return contactTracingService.communicationService.getEmail(messageId);
    }

    private String getSms(String[] params) {
        int messageId = Integer.parseInt(params[1]);
        return contactTracingService.communicationService.getSms(messageId);
    }

    private String[] splitInput(String input) {
        return input.split(",");
    }
}
