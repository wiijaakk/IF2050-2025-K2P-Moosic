package com.controller;

import com.database.DatabaseRegister;
import com.service.RegisterService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


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
        Platform.runLater(this::updateScale);

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

        // Validate Full Name and Address
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

        // Create a new RegisterService instance for validation checks
        RegisterService service = new RegisterService();

        // Validate Username
        if (!service.isValidUsername(username)) {
            errorUsernameText.setText("*Username harus alphanumeric 6–15 karakter");
            errorUsernameText.setVisible(true);
            isValid = false;
        } else if (DatabaseRegister.isUsernameTaken(username)) {
            errorUsernameText.setText("*Username sudah terdaftar");
            errorUsernameText.setVisible(true);
            isValid = false;
        }

        // Validate Password
        if (!service.isValidPassword(password)) {
            errorPasswordText.setText("*Password harus mengandung huruf, angka, simbol, min. 6 karakter");
            errorPasswordText.setVisible(true);
            isValid = false;
        }

        // Validate Re-enter Password
        if (!service.isPasswordMatch(password, reenterPassword)) {
            errorReenterPasswordText.setText("*Password tidak cocok");
            errorReenterPasswordText.setVisible(true);
            isValid = false;
        }

        // Validate Email (using service and then additional regex for clarity)
        if (!service.isValidEmail(email)) { // This should ideally contain regex, but keeping for structure
            errorEmailText.setText("*Email tidak valid");
            errorEmailText.setVisible(true);
            isValid = false;
        } else {
            // Additional check using regex for robustness
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
        }


        if (isValid) {
            String userRole = "customer";

            if (DatabaseRegister.insertUser(fullName, address, username, password, email, userRole)) {
                System.out.println("Registration successful! User: " + username);
                clearForm();
                errorRegistrationText.setText("Registrasi berhasil!");
                errorRegistrationText.setVisible(true);
                errorRegistrationText.setStyle("-fx-fill: green;");

                // Navigate to login page with full screen after successful registration
                navigateToPage("/fxml/LoginView.fxml", "Moosic - Login", "/css/login.css");
                
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

    /**
     * Generic navigation method with FULL SCREEN enforcement
     * @param fxmlPath Path to FXML file
     * @param title Window title
     * @param cssPath Path to CSS file (can be null)
     */
    private void navigateToPage(String fxmlPath, String title, String cssPath) {
        try {
            System.out.println("🔍 Attempting to load FXML: " + fxmlPath);
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            // Get current stage
            Stage stage = (Stage) rootPane.getScene().getWindow();
            
            // Save current window state
            boolean wasMaximized = stage.isMaximized();
            double currentWidth = stage.getWidth();
            double currentHeight = stage.getHeight();
            
            // Create scene with appropriate size
            Scene scene;
            if (wasMaximized) {
                scene = new Scene(root, stage.getWidth(), stage.getHeight());
            } else {
                scene = new Scene(root, currentWidth, currentHeight);
                // Force maximize for navigation
                wasMaximized = true;
            }

            // Load CSS if provided
            if (cssPath != null) {
                try {
                    URL cssUrl = getClass().getResource(cssPath);
                    if (cssUrl != null) {
                        scene.getStylesheets().add(cssUrl.toExternalForm());
                        System.out.println("✅ Loaded CSS: " + cssPath);
                    } else {
                        System.out.println("⚠️ CSS not found: " + cssPath);
                    }
                } catch (Exception e) {
                    System.out.println("⚠️ Error loading CSS: " + cssPath + " - " + e.getMessage());
                }
            }

            // Set scene
            stage.setScene(scene);
            stage.setTitle(title);
            
            // Force maximize window with enhanced method
            ensureMaximizedWindow(stage);
            
            stage.show();
            System.out.println("✅ Successfully navigated to " + title + " (Full Screen)");

        } catch (IOException e) {
            System.err.println("❌ IO Error navigating to " + title + ": " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", 
                     "Could not navigate to " + title + ". File not found: " + fxmlPath);
        } catch (Exception e) {
            System.err.println("❌ Unexpected error navigating to " + title + ": " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Unexpected Error", 
                     "An unexpected error occurred: " + e.getMessage());
        }
    }

    /**
     * Enhanced method untuk memastikan window full screen
     * @param stage The JavaFX Stage to maximize
     */
    private void ensureMaximizedWindow(Stage stage) {
        try {
            // Method 1: Set maximized immediately
            stage.setMaximized(false); // Reset first
            stage.setMaximized(true);  // Then maximize
            
            // Method 2: Use Platform.runLater for delayed execution
            Platform.runLater(() -> {
                if (!stage.isMaximized()) {
                    stage.setMaximized(true);
                }
                stage.toFront();
                stage.requestFocus();
            });
            
            // Method 3: Use Timeline for multiple attempts (aggressive approach)
            Timeline maxChecker = new Timeline();
            maxChecker.getKeyFrames().addAll(
                new KeyFrame(Duration.millis(50), e -> {
                    if (!stage.isMaximized()) {
                        stage.setMaximized(true);
                        System.out.println("🔄 Maximized check 1 - Applied");
                    }
                }),
                new KeyFrame(Duration.millis(150), e -> {
                    if (!stage.isMaximized()) {
                        stage.setMaximized(true);
                        System.out.println("🔄 Maximized check 2 - Applied");
                    }
                }),
                new KeyFrame(Duration.millis(300), e -> {
                    if (!stage.isMaximized()) {
                        stage.setMaximized(true);
                        System.out.println("🔄 Final maximized attempt completed");
                    }
                    System.out.println("✅ Final window state - Maximized: " + stage.isMaximized());
                })
            );
            maxChecker.play();
            
        } catch (Exception e) {
            System.err.println("⚠️ Error ensuring maximized window: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * OPTIONAL: Method untuk navigasi kembali ke login (jika ada tombol back)
     */
    @FXML
    private void handleBackToLogin() {
        System.out.println("🔙 Navigating back to Login page (Full Screen)...");
        navigateToPage("/fxml/LoginView.fxml", "Moosic - Login", "/css/login.css");
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Legacy method untuk backward compatibility - now uses full screen navigation
     * @param fxmlPath The path to the FXML file (e.g., "/fxml/LoginView.fxml").
     */
    private void loadPage(String fxmlPath) {
        // Redirect to new navigation method with full screen
        String title = "Moosic Application";
        String cssPath = null;
        
        // Determine appropriate CSS based on path
        if (fxmlPath.contains("LoginView")) {
            title = "Moosic - Login";
            cssPath = "/css/login.css";
        } else if (fxmlPath.contains("homepage")) {
            title = "Moosic - Home";
            cssPath = "/css/homepage.css";
        }
        
        navigateToPage(fxmlPath, title, cssPath);
    }
}