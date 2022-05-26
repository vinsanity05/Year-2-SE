import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class PopupController {

    public TextArea messageText;
    public Label titleLabel;
    public Button okButton;

    public void onOkButtonPress() {
        Stage stage = (Stage) okButton.getScene().getWindow();
        stage.close();
    }

    public void setMessageText(String text) {
        // Make sure the text is fine
        int textLength = text.length();
        int maxLengthLine = 40;
        StringBuilder allText = new StringBuilder(text);
        if (textLength > maxLengthLine) {
            allText = new StringBuilder();
            StringBuilder lastLine = new StringBuilder();
            StringBuilder lastWord = new StringBuilder();
            int counter = 0;
            // Sort text
            for (char c : text.toCharArray()) {
                if (("" + c).equalsIgnoreCase("\n")) {
                    counter = maxLengthLine;
                    lastLine.append(lastWord);
                    lastWord = new StringBuilder();
                }
                else if (("" + c).equalsIgnoreCase(" ")) {
                    lastLine.append(lastWord);
                    lastLine.append(" ");
                    lastWord = new StringBuilder();
                }
                else {
                    lastWord.append(c);
                }
                if (counter == maxLengthLine) {
                    lastLine.append("\n");
                    allText.append(lastLine);
                    lastLine = new StringBuilder();
                    counter = 0;
                }
                counter++;
            }
            allText.append(lastLine);
            allText.append(lastWord);
        }
        messageText.setText(allText.toString());
    }

    public void setTitleLabel(String title) {
        titleLabel.setText(title);
    }
}
