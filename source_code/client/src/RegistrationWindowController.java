import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

public class RegistrationWindowController {

    public Button registerButton;
    public PasswordField passwordTextField;
    public TextField emailTextField;
    public TextField addressTextField;
    public TextField phoneNumberTextField;
    public TextField nameTextField;
    public Hyperlink registerHyperlink;

    private Client client;

    private boolean emailText, passwordText, phoneNumberText, addressText, nameText;

    // Button presses
    public void onEmailKeyPressed() {
        emailText = !emailTextField.getText().isEmpty();
        toggleRegisterButton(emailText && passwordText && phoneNumberText && addressText && nameText);
    }

    public void onPasswordKeyPressed() {
        passwordText = !passwordTextField.getText().isEmpty();
        toggleRegisterButton(emailText && passwordText && phoneNumberText && addressText && nameText);
    }

    public void onPhoneNumberKeyPressed() {
        phoneNumberText = !phoneNumberTextField.getText().isEmpty();
        toggleRegisterButton(emailText && passwordText && phoneNumberText && addressText && nameText);
    }

    public void onAddressKeyPressed() {
        addressText = !addressTextField.getText().isEmpty();
        toggleRegisterButton(emailText && passwordText && phoneNumberText && addressText && nameText);
    }

    public void onNameKeyPressed() {
        nameText = !nameTextField.getText().isEmpty();
        toggleRegisterButton(emailText && passwordText && phoneNumberText && addressText && nameText);
    }

    public void setFieldText(String emailText, String passwordText) {
        emailTextField.setText(emailText);
        passwordTextField.setText(passwordText);
        this.emailText = true;
        this.passwordText = true;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public void onRegisterLinkPress() {
        // Open the link in the default browser
        try {
            Desktop.getDesktop().browse(new URL(registerHyperlink.getText()).toURI());
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
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
            StringBuilder address = new StringBuilder();
            String[] address_raw = addressTextField.getText().split(",");
            for (String s : address_raw) {
                address.append(s);
            }
            String command = "register," + emailTextField.getText() + "," + passwordTextField.getText()
                    + "," + phoneNumberTextField.getText() + "," + address + ","
                    + nameTextField.getText();
            boolean registerStatus = Boolean.parseBoolean(client.requestFromServer(command));
            // Check no account with this email exists
            if (!registerStatus) {
                showPopupWindow("ERROR", "An account with this e-mail address already exists.");
            }
            else {
                showPopupWindow("REGISTERED", "Registered successfully. Please log in.");
                Stage stage = (Stage) registerButton.getScene().getWindow();
                stage.close();
            }
        }
    }

    private void toggleRegisterButton(boolean value) {
        registerButton.setDisable(!value);
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
