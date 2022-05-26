import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

public class MainController {

    // Global Things
    public Tab homeTab, loginTab, adminTab;
    // Login Page Things
    public Button registerButton;
    public Button loginButton;
    public PasswordField passwordTextField;
    public TextField emailTextField;
    public Hyperlink loginHyperlink;
    // Home Page Things
    public Button nhsCovidInfoButton;
    public Button commonCovidSympButton;
    public Button testKitInfoButton;
    public Button settingsButton;
    public Hyperlink adminHyperlink;
    public TabPane mainTabPane;
    // Admin Page Things
    public Button getEmailsButton;
    public Button getSmsButton;
    public TextField messageIdTextField;
    public TextField accountTextField;
    public TextField adminUpdatePasswordField;
    public Button deleteAccountButton;
    public Button adminUpdatePasswordButton;
    // Contact Sharing & Account Management
    public Button contactSharingButton;
    public Button updateAccountButton;
    // Other Things
    private boolean emailText, passwordText, loggedIn, admin;
    private String currentLoginAs;
    private Client client;


    public MainController() {
        emailText = false; passwordText = false;
        client = new Client();
    }

    // Button presses
    public void onEmailKeyPressed() {
        emailText = !emailTextField.getText().isEmpty();
        toggleLoginRegisterButtons(emailText && passwordText);
    }

    public void onPasswordKeyPressed() {
        passwordText = !passwordTextField.getText().isEmpty();
        toggleLoginRegisterButtons(emailText && passwordText);
    }

    private void toggleLoginRegisterButtons(boolean value) {
        loginButton.setDisable(!value);
        registerButton.setDisable(!value);
    }

    public void onLoginButtonAction() throws IOException {
        // Validate the login
        String command = "login," + emailTextField.getText() + "," + passwordTextField.getText();
        String loginStatus = client.requestFromServer(command);
        if (loginStatus.equals("not found")) {
            showPopupWindow("ERROR", "No account with this email address exists. Try registering instead.");
        }
        else {
            if (loginStatus.equals("true")) {
                loggedIn = true;
                admin = false;
                enableAfterLogin();
                SelectionModel<Tab> selectionModel = mainTabPane.getSelectionModel();
                selectionModel.select(homeTab);
                currentLoginAs = emailTextField.getText();
            } else if (loginStatus.equals("admin")) {
                admin = true;
                loggedIn = false;
                enableAfterAdmin();
                SelectionModel<Tab> selectionModel = mainTabPane.getSelectionModel();
                selectionModel.select(adminTab);
                currentLoginAs = emailTextField.getText();
            } else {
                showPopupWindow("ERROR", "The password provided was incorrect.");
            }
        }
    }

    public void onRegisterButtonAction() throws IOException {
        // Validate that the email is correct
        boolean hasAt = false;
        // Validate the email
        for (char c : emailTextField.getText().toCharArray()) {
            if (("" + c).equals("@")) {
                hasAt = true;
                break;
            }
        }
        if (!hasAt) {
            showPopupWindow("ERROR", "Invalid e-mail address. Please enter a valid one.");
        }
        else {
            if (client.requestFromServer("checkaccount," + emailTextField.getText()).equals("true")) {
                showPopupWindow("Error", "An account with this email already exists. Try logging in.");
            }
            else { showRegistrationWindow(); }
        }
    }

    public void onEmailButtonAction() throws IOException {
        try {
            String response = client.requestFromServer("getemail," + Integer.parseInt(messageIdTextField.getText()));
            if (response.equalsIgnoreCase("null")) {
                showPopupWindow("ERROR", "Email with this id was not found.");
            } else {
                String[] emailContent = response.substring(0, response.length() - 1).split(",");
                StringBuilder emailString = new StringBuilder();
                for (String s : emailContent) {
                    emailString.append(s);
                    emailString.append("\n");
                }
                showPopupWindow("EMAIL", emailString.substring(0, emailString.length() - 1));
            }
        } catch (NumberFormatException e) {
            showPopupWindow("ERROR", "Please enter a valid message id.");
        }
    }

    public void onSmsButtonAction() throws IOException {
        try {
            String response = client.requestFromServer("getsms," + Integer.parseInt(messageIdTextField.getText()));
            if (response.equalsIgnoreCase("null")) {
                showPopupWindow("ERROR", "SMS with this id was not found.");
            } else {
                String[] emailContent = response.substring(0, response.length() - 1).split(",");
                StringBuilder smsString = new StringBuilder();
                for (String s : emailContent) {
                    smsString.append(s);
                    smsString.append("\n");
                }
                showPopupWindow("SMS", smsString.substring(0, smsString.length() - 1));
            }
        } catch (NumberFormatException e) {
            showPopupWindow("ERROR", "Please enter a valid message id.");
        }
    }

    public void onNhsCovidInfoButton() throws IOException {
        // Open the link in the default browser
        try {
            Desktop.getDesktop().browse(new URL("https://www.nhs.uk/conditions/coronavirus-covid-19/").toURI());
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void onCommonCovidSymptomsButton() throws IOException {
        showPopupWindow("Common Symptoms", "Common symptoms of COVID-19 include:\n" +
                "- High temperature (hot to the touch on your chest or back)\n" +
                "- A new and continuous cough (coughing a lot for more than an hour, or 3 more coughing episodes in" +
                " 24 hours)\n" +
                "- Loss or change to your sense of smell or taste (you cannot smell or taste anything, or things smell " +
                "different to normal).");
    }

    public void onTestKitInformationButton() throws IOException {
        showPopupWindow("Test Kit Information", "As soon as you sign up to the app, a test kit will be prepped" +
                "and sent to the address you used when signing up. Please contact us as soon as possible if you need to" +
                " change your address. The test kit itself will have directions for safe use. Please read them before " +
                "attempting to test yourself. If you need help or guidance, call us on 111.");
    }

    // Contact sharing service
    public void onShareContactDetailsButton() throws IOException {
        showContactSharingWindow();
    }

    // Update account
    public void onUpdateAccountButton() throws IOException {
        showUpdateAccountWindow(currentLoginAs);
    }

    public void onAdminUpdatePasswordButton() throws IOException {
        if (adminUpdatePasswordField.getText().equalsIgnoreCase("")) {
            showPopupWindow("Error", "Please enter a password first.");
        } else {
            String resp = client.requestFromServer("editaccount," + currentLoginAs + "," + "N/A" + "," + adminUpdatePasswordField.getText() + "," + "true");
            if (resp.equalsIgnoreCase("change")) {
                showPopupWindow("Success", "Account updated successfully.");
            } else {
                showPopupWindow("Error", "Error when updating account.");
            }
        }
    }

    public void onDeleteAccountButton() throws IOException {
        String response = client.requestFromServer("checkaccount," + accountTextField.getText());
        if (response.equalsIgnoreCase("true")) {
            response = client.requestFromServer("deleteaccount," + accountTextField.getText());
            if (response.equalsIgnoreCase("true")) {
                showPopupWindow("Deleted successfully", "Account deleted successfully.");
            }
            else {
                showPopupWindow("Error", "Unable to delete that account.");
            }
        } else {
            showPopupWindow("Invalid account", "No account with that email exists.");
        }
    }

    public void onLoginLinkPress() {
        // Open the link in the default browser
        try {
            Desktop.getDesktop().browse(new URL(loginHyperlink.getText()).toURI());
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void onHomeLinkPress() {
        // Open the link in the default browser
        try {
            Desktop.getDesktop().browse(new URL(loginHyperlink.getText()).toURI());
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    // Other functions
    private void enableAfterLogin() {
        // Enable the home tab after logging in
        if (loggedIn) {
            homeTab.setDisable(false);
        }
        if (!admin) {
            adminTab.setDisable(true);
        }
    }

    private void enableAfterAdmin() {
        if (admin) {
            adminTab.setDisable(false);
        }
        if (!loggedIn) {
            homeTab.setDisable(true);
        }
    }

    private void showRegistrationWindow() throws IOException {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("registrationWindow.fxml"));
        Parent root = loader.load();
        RegistrationWindowController registrationWindowController = (RegistrationWindowController) loader.getController();
        stage.setTitle("Register");
        Scene newScene = new Scene(root, 292.0, 400);
        stage.setScene(newScene);
        registrationWindowController.setFieldText(emailTextField.getText(), passwordTextField.getText());
        registrationWindowController.setClient(client);
        stage.show();
    }

    private void showContactSharingWindow() throws IOException {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("contactSharingWindow.fxml"));
        Parent root = loader.load();
        ContactSharingController contactSharing = (ContactSharingController) loader.getController();
        stage.setTitle("Contact Sharing");
        Scene newScene = new Scene(root, 292.0, 400);
        stage.setScene(newScene);
        contactSharing.setClient(client);
        stage.show();
    }

    private void showUpdateAccountWindow(String email) throws IOException {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("updateAccountWindow.fxml"));
        Parent root = loader.load();
        UpdateAccountWindowController contactSharing = (UpdateAccountWindowController) loader.getController();
        stage.setTitle("Update Account");
        Scene newScene = new Scene(root, 292.0, 150);
        stage.setScene(newScene);
        contactSharing.setClient(client);
        contactSharing.setEmail(email);
        stage.show();
    }

    private void showPopupWindow(String title, String text) throws IOException {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("popupScreen.fxml"));
        Parent root = loader.load();
        PopupController thisErrorController = (PopupController) loader.getController();
        thisErrorController.setMessageText(text);
        thisErrorController.setTitleLabel(title);
        stage.setTitle(title);
        Scene newScene = new Scene(root, 292.0, 150.0);
        stage.setScene(newScene);
        stage.show();
    }


}
