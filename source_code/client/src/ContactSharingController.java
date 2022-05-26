import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.IOException;

public class ContactSharingController {

    public Button helpButton;
    public Button shareDetailsButton;
    public TextArea contactDetailsField;
    private Client client;

    public void onHelpButton() throws IOException {
        showPopupWindow("Help",
                "Enter the details of each contact. Separate their details with a pipe '|' and separate each " +
                "person with a new line. Please include their full name, their e-mail address and phone number.");
    }

    public void onShareDetailsButton() throws IOException {
        String[] lines = contactDetailsField.getText().split("\n"); // Splits on new line
        // Each line is a contact
        // Turn into one big string and separate with pipes
        StringBuilder allContacts = new StringBuilder();
        boolean valid = true;
        for (String line : lines) {
            String[] params = line.split("\\|");
            if (params.length != 3) {
                valid = false;
                break;
            }
            allContacts.append(line);
            allContacts.append("$");
        }
        if (!valid) {
            showPopupWindow("Error", "Invalid contact details were entered. Please check and try again.");
        }
        else {
            System.out.println(allContacts);
            client.requestFromServer("sharecontacts," + allContacts.substring(0, allContacts.toString().length() - 2));
        }
        Stage thisStage = (Stage) contactDetailsField.getScene().getWindow();
        thisStage.close();
    }

    public void setClient(Client client) {
        this.client = client;
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
