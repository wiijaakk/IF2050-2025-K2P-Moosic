package com.controller;

import com.database.DatabaseRegister; 
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.service.RegisterService;


public class RegisterController implements Initializable {

    @FXML private StackPane rootPane;
    @FXML private AnchorPane formContainer;
    @FXML private Group scalableContentGroup;

    @FXML private TextField fullNameField;
    @FXML private TextField addressField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField emailField;
    @FXML private PasswordField reenterPasswordField;
    @FXML private Text errorUsernameText;
    @FXML private Text errorPasswordText;
    @FXML private Text errorReenterPasswordText;
    @FXML private Text errorEmailText;
    @FXML private Text errorRegistrationText;
    @FXML private Button registerButton;

    private static final double DESIGN_WIDTH = 800.0;
    private static final double DESIGN_HEIGHT = 500.0;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        rootPane.widthProperty().addListener((obs, oldVal, newVal) -> updateScale());
        rootPane.heightProperty().addListener((obs, oldVal, newVal) -> updateScale());
        javafx.application.Platform.runLater(this::updateScale);

        errorUsernameText.setVisible(false);
        errorPasswordText.setVisible(false);
        errorReenterPasswordText.setVisible(false);
        errorEmailText.setVisible(false);
        errorRegistrationText.setVisible(false);
    }

    private void updateScale() {
        if (rootPane != null && formContainer != null) {
            double currentWidth = rootPane.getWidth();
            double currentHeight = rootPane.getHeight();

            double scaleX = currentWidth / DESIGN_WIDTH;
            double scaleY = currentHeight / DESIGN_HEIGHT;

            double scale = Math.min(scaleX, scaleY);

            scalableContentGroup.setScaleX(scale);
            scalableContentGroup.setScaleY(scale);
        }
    }

    @FXML
    private void onRegisterClicked() {
        System.out.println("Register button clicked!");

        boolean isValid = true;

        errorUsernameText.setVisible(false);
        errorPasswordText.setVisible(false);
        errorReenterPasswordText.setVisible(false);
        errorEmailText.setVisible(false);
        errorRegistrationText.setVisible(false);

        String fullName = fullNameField.getText().trim();
        String address = addressField.getText().trim();
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String reenterPassword = reenterPasswordField.getText();
        String email = emailField.getText().trim();

        if (fullName.isEmpty()) {
            System.out.println("Full Name cannot be empty.");
            errorRegistrationText.setText("Nama Lengkap tidak boleh kosong.");
            errorRegistrationText.setVisible(true);
            isValid = false;
        }
        if (address.isEmpty()) {
            System.out.println("Address cannot be empty.");
            errorRegistrationText.setText("Alamat tidak boleh kosong.");
            errorRegistrationText.setVisible(true);
            isValid = false;
        }
        
        RegisterService service = new RegisterService();

        if (!service.isValidUsername(username)) {
            errorUsernameText.setText("*Username harus alphanumeric 6–15 karakter");
            errorUsernameText.setVisible(true);
            isValid = false;
        } else if (DatabaseRegister.isUsernameTaken(username)) {
            errorUsernameText.setText("*Username sudah terdaftar");
            errorUsernameText.setVisible(true);
            isValid = false;
        }
        
        if (!service.isValidPassword(password)) {
            errorPasswordText.setText("*Password harus mengandung huruf, angka, simbol, min. 6 karakter");
            errorPasswordText.setVisible(true);
            isValid = false;
        }
        
        if (!service.isPasswordMatch(password, reenterPassword)) {
            errorReenterPasswordText.setText("*Password tidak cocok");
            errorReenterPasswordText.setVisible(true);
            isValid = false;
        }
        
        if (!service.isValidEmail(email)) {
            errorEmailText.setText("*Email tidak valid");
            errorEmailText.setVisible(true);
            isValid = false;
        } else if (DatabaseRegister.isEmailTaken(email)) {
            errorEmailText.setText("*Email sudah terdaftar");
            errorEmailText.setVisible(true);
            isValid = false;
        }
        
        
        Pattern emailPattern = Pattern.compile("^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$");
        Matcher emailMatcher = emailPattern.matcher(email);
        if (!emailMatcher.matches()) {
            errorEmailText.setText("*Email tidak valid");
            errorEmailText.setVisible(true);
            isValid = false;
        } else if (DatabaseRegister.isEmailTaken(email)) { 
            errorEmailText.setText("*Email sudah terdaftar");
            errorEmailText.setVisible(true);
            isValid = false;
        }

        if (isValid) {
            String userRole = "customer"; 

            if (DatabaseRegister.insertUser(fullName, address, username, password, email, userRole)) {
                System.out.println("Registration successful! User: " + username);
                clearForm();
                errorRegistrationText.setText("Registrasi berhasil!");
                errorRegistrationText.setVisible(true);
                errorRegistrationText.setStyle("-fx-fill: green;");
            } else {
                errorRegistrationText.setText("Registrasi gagal. Mohon coba lagi.");
                errorRegistrationText.setVisible(true);
                errorRegistrationText.setStyle("-fx-fill: red;");
            }
        } else {
            System.out.println("Registration failed due to validation errors.");

            if (!errorRegistrationText.isVisible()) { 
                errorRegistrationText.setText("Terdapat kesalahan input.");
                errorRegistrationText.setVisible(true);
                errorRegistrationText.setStyle("-fx-fill: red;");
            }
        }
    }

    private void clearForm() {
        fullNameField.clear();
        addressField.clear();
        usernameField.clear();
        passwordField.clear();
        reenterPasswordField.clear();
        emailField.clear();

        errorUsernameText.setVisible(false);
        errorPasswordText.setVisible(false);
        errorReenterPasswordText.setVisible(false);
        errorEmailText.setVisible(false);
        errorRegistrationText.setVisible(false);
    }
}