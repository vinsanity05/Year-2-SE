package accounts;

import file_path.FilePath;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class AccountManager {

    private List<Account> registeredAccounts;
    private List<AdminAccount> adminAccounts;
    private final String account_csv = FilePath.filePath + "accounts.csv";
    private final String admin_csv = FilePath.filePath + "admin_accounts.csv";

    public AccountManager() {
        registeredAccounts = new ArrayList<>();
        adminAccounts = new ArrayList<>();
        loadExistingAccountsFromDatabase();
        loadAdminAccountsFromDatabase();
    }

    public boolean registerAccount(Account account) {
        boolean exist = (accountExists(account.getEmail()) != null);
        if (!exist) {
            registeredAccounts.add(account);
            writeAccountToCsvFile(account, account_csv);
            System.out.println("Registered account successfully");
            return true;
        } else {
            System.out.println("An account with this email already exists.");
            return false;
        }
    }

    public boolean deleteAccount(String email) {
        Account account = accountExists(email);
        if (account instanceof AdminAccount) {
            return false;
        }
        if (account != null) {
            registeredAccounts.remove(account);
            System.out.println("Removing " + account.getEmail());
            saveWholeCsvFile(account_csv, false);
            return true;
        } else {
            return false;
        }
    }

    public String editAccountData(String email, String[] replacement, boolean admin) {
        Account existingAccount = accountExists(email);
        if (existingAccount != null) {
            boolean change = false;
            if (!replacement[0].equalsIgnoreCase("N/A")) {
                existingAccount.setAddress(replacement[0]);
                change = true;
            }
            if (!replacement[1].equalsIgnoreCase("N/A")) {
                existingAccount.setPassword(replacement[1]);
                change = true;
            }
            if (change) {
                if (admin) {
                    saveWholeCsvFile(admin_csv, true);
                } else {
                    saveWholeCsvFile(account_csv, false);
                }
                return "change";
            }
            else {
                return "no change";
            }
        }
        else {
            return "false";
        }
    }

    private void saveWholeCsvFile(String csv_file, boolean admin) {
        clearCsvFile(csv_file);
        if (admin) {
            for (AdminAccount account : adminAccounts) {
                writeAccountToCsvFile(account, csv_file);
            }
        } else {
            for (Account account : registeredAccounts) {
                writeAccountToCsvFile(account, csv_file);
            }
        }
    }

    public Account accountExists(String email) {
        for (Account existing : registeredAccounts) {
            if (existing.getEmail().equals(email)) {
                return existing;
            }
        }
        for (AdminAccount existing : adminAccounts) {
            if (existing.getEmail().equals(email)) {
                return existing;
            }
        }
        return null;
    }

    private void clearCsvFile(String csv_file) {
        File csvFile = new File(csv_file);
        try {
            FileWriter csvWriter = new FileWriter(csvFile, false);
            csvWriter.append("Email,Password,Phone Number,Address,Name\n");
            csvWriter.flush();
            csvWriter.close();
        } catch (IOException e) {
            System.out.println("Couldn't clear file " + csv_file + "!");
        }
    }

    private void writeAccountToCsvFile(Account account, String csv_file) {
        File csvFile = new File(csv_file);
        // Write the account to the file
        try {
            FileWriter csvWriter = new FileWriter(csvFile, true);
            csvWriter.append(account.getEmail()).append(",").append(account.getPassword()).append(",").
                    append(account.getPhoneNumber()).append((",")).append(account.getAddress()).append(",").
                    append(account.getName()).append("\n");
            csvWriter.flush();
            csvWriter.close();
        } catch (IOException e) {
            System.out.println("Unable to access file " + csv_file + "!");
        }
    }

    private void loadExistingAccountsFromDatabase() {
        // Fetch existing values from the database
        List<String[]> lines = getCsvData(account_csv, false);
        if (lines == null) {
            System.out.println("No .csv file found, so no account data has been loaded.");
        } else {
            // Headers were ignored
            for (String[] s : lines) {
                String email = s[0];
                String password = s[1];
                String phoneNumber = s[2];
                String address = s[3];
                String name = s[4];
                Account thisAccount = new Account(email, password, phoneNumber, address, name);
                registeredAccounts.add(thisAccount);
            }
            System.out.println("Loaded user accounts.");
        }
    }

    private void loadAdminAccountsFromDatabase() {
        // Fetch existing values from the database
        List<String[]> lines = getCsvData(admin_csv, true);
        if (lines == null) {
            System.out.println("No .csv file found, so no account data has been loaded.");
        } else {
            // Headers were ignored
            for (String[] s : lines) {
                String email = s[0];
                String password = s[1];
                String phoneNumber = s[2];
                String address = s[3];
                AdminAccount thisAccount = new AdminAccount(email, password, phoneNumber, address);
                adminAccounts.add(thisAccount);
            }
            System.out.println("Loaded admin accounts.");
        }
    }

    private List<String[]> getCsvData(String csv_path, boolean admin) {
        List<String[]> fileData = new ArrayList<>();
        try {
            BufferedReader csvReader = new BufferedReader(new FileReader(csv_path));
            int counter = 0;
            String line = csvReader.readLine(); // Read in the initial line
            while (line != null) { // While the lines aren't null, read in all of them
                if (counter != 0) { // Ignore the headers
                    String[] row_data = line.split(",");
                    fileData.add(row_data);
                }
                counter++;
                line = csvReader.readLine(); // Read in the next line - done here or else lines are skipped
            }
            csvReader.close(); // Close after reading the file
        } catch (FileNotFoundException e) {
            // Create the file
            try {
                File csvFile = new File(csv_path);
                csvFile.createNewFile();
                // Write the headers to the file
                FileWriter csvWriter = new FileWriter(csvFile);
                csvWriter.append("Email,Password,Phone Number,Address,Name\n");
                if (admin) {
                    csvWriter.append("admin@nhstts.com,AdminPassword123,111,N/A,admin");
                    adminAccounts.add(new AdminAccount("admin@nhstts.com","AdminPassword123","111","N/A"));
                }
                csvWriter.flush();
                csvWriter.close();
            }
            catch (IOException ex) {
                System.out.println("Unable to create a new .csv file.");
            }
        } catch (IOException e) {
            System.out.println("Unable to read the existing .csv file.");
            fileData = null;
        }
        return fileData;
    }
}
