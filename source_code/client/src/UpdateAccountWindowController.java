import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class UpdateAccountWindowController {

    public Button updateAccountButton;
    public Button helpButton;
    public TextField addressTextField;
    public PasswordField passwordTextField;

    private String email;
    private Client client;

    public void setClient(Client client) {
        this.client = client;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void onHelpButton() throws IOException {
        showPopupWindow("Help", "Enter your new address and/or password and press 'Update Account Details'.");
    }

    public void onUpdateAccountButton() throws IOException {
        boolean a, p;
        a = false; p = false;
        StringBuilder address = new StringBuilder();
        String password = "";
        if (!addressTextField.getText().equalsIgnoreCase("")) {
            a = true;
            String[] address_raw = addressTextField.getText().split(",");
            for (String s : address_raw) {
                address.append(s);
            }
        } else {
            address = new StringBuilder("N/A");
        }
        if (!passwordTextField.getText().equalsIgnoreCase("")) {
            p = true;
            password = passwordTextField.getText();
        } else {
            password = "N/A";
        }
        if (a || p) {
            String resp = client.requestFromServer("editaccount," + email + "," + address + "," + password + "," + "false");
            if (resp.equalsIgnoreCase("change")) {
                showPopupWindow("Success", "Account updated successfully.");
            }
            else {
                showPopupWindow("Error", "Error when updating account.");
            }
        }
        Stage stage = (Stage) updateAccountButton.getScene().getWindow();
        stage.close();
    }

    private void showPopupWindow(String title, String text) throws IOException {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("popupScreen.fxml"));
        Parent root = loader.load();
        PopupController thisErrorController = (PopupController) loader.getController();
        thisErrorController.setMessageText(text);
        stage.setTitle(title);
        thisErrorController.setTitleLabel(title);
        Scene newScene = new Scene(root, 292.0, 150.0);
        stage.setScene(newScene);
        stage.show();
    }
}
