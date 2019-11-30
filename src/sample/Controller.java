package sample;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import sample.entity.User;
import sample.excel.ExcelParser;
import sample.mail.MailSender;

import javax.mail.MessagingException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class Controller {

    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label messageLabel;
    @FXML
    private Label excelName;
    @FXML
    private Label errorLabel;
    //    @FXML
//    private TextField subjectField;
//    @FXML
//    private TextArea textField;
    //     @FXML
//    private Label attachmentName;
    @FXML
    private ProgressIndicator sendingIndicator;
    @FXML
    private Label progressStatus;

    private ExcelParser excelParser;
    //    private File attachmentFile;
    private boolean mailSending = false;
    private ArrayList<User> users;
    private MailSender mailSender;
    private boolean stoping;

    @FXML
    public void buttonClick() {
        if (!mailSending) {
            mailSending = true;
            stoping = false;
            errorLabel.setText("");
            if (emailField.getText().isEmpty() || passwordField.getText().isEmpty()) {
                messageLabel.setText("You didnt't input login or password");
            } else if (excelParser == null) {
                messageLabel.setText("Yuo didn't choice excel file");
            } else {
                messageLabel.setText("Parsing excel file...");
                try {
                    users = (ArrayList<User>) excelParser.getUsers();
                } catch (IOException e) {
                    messageLabel.setText("Error from parsing email");
                    mailSending = false;
                    e.printStackTrace();
                    return;
                }
                messageLabel.setText("Mails are sending...");
                mailSender = new MailSender(emailField.getText(), passwordField.getText());
                SendTask sendTask = new SendTask();
                sendingIndicator.progressProperty().unbind();
                sendingIndicator.progressProperty().bind(sendTask.progressProperty());
                progressStatus.textProperty().unbind();
                progressStatus.textProperty().bind(sendTask.messageProperty());
                new Thread(sendTask).start();
            }
        }
    }

    @FXML
    public void selectExcel() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select excel file");
        File file = fileChooser.showOpenDialog(new Stage());
        if (file != null) {
            excelName.setText(file.getName());
            excelParser = new ExcelParser(file.getAbsolutePath());
        }
    }

    @FXML
    public void stop() {
        stoping = true;
    }

//    @FXML
//    public void selectAttachment() {
//        FileChooser fileChooser = new FileChooser();
//        fileChooser.setTitle("Select excel file");
//        File file = fileChooser.showOpenDialog(new Stage());
//        if (file != null) {
//            attachmentName.setText(file.getName());
//            attachmentFile = file;
//        }
//    }

    private String getTextByUser(User user) {
        return user.getName() + '\n' +
                user.getCode() +
                "\n\n" +
                user.getRules() +
                "\n-----\n" +
                user.getSignature();
    }

    class SendTask extends Task<Void> {

        @Override
        protected Void call() throws Exception {
            for (int i = 0; i < users.size(); i++) {
                if (!stoping) {
                    User user = users.get(i);
                    try {
                        mailSender.sendMessage(user.getEmail(), user.getSubject(), getTextByUser(user));
                        updateMessage("Mail has sent to " + user.getEmail());
                        updateProgress(i + 1, users.size());
                    } catch (MessagingException messagingException) {
                        messagingException.printStackTrace();
                        updateMessage("Error from sending message to " + user.getEmail());
                        updateProgress(i + 1, users.size());
                        Platform.runLater(() -> {
                            if (errorLabel.getText().isEmpty()) {
                                errorLabel.setText("Not send: \n");
                            }
                            errorLabel.setText(errorLabel.getText() + user.getEmail() + ' '
                                    + messagingException.getMessage() + '\n');
                        });
                    }
                }else{
                    break;
                }
            }

            mailSending = false;
            mailSender.clean();
            Platform.runLater(() -> {
                messageLabel.setText("All mails have sent");
            });
            return null;
        }
    }
}
