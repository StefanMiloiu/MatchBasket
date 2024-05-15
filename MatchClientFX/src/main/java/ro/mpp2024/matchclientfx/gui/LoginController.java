//package ro.mpp2024.matchclientfx.gui;
//
//import javafx.fxml.FXML;
//import javafx.fxml.FXMLLoader;
//import javafx.scene.Parent;
//import javafx.scene.Scene;
//import javafx.scene.control.PasswordField;
//import javafx.scene.control.TextField;
//import javafx.stage.Stage;
//import ro.mpp2024.User;
//
//import java.util.Optional;
//
//public class LoginController {
//
//    private final UserService userService  = new UserService();
//
//    private final ClientService clientService;
//    private final MatchService matchService;
//    private final ClientTicketService clientTicketService;
//    private final TicketService ticketService;
//
//    @FXML
//    private TextField usernameField;
//
//    @FXML
//    private PasswordField passwordField;
//
//    public LoginController(ClientService clientService, MatchService matchService, ClientTicketService clientTicketService, TicketService ticketService) {
//        this.clientService = clientService;
//        this.matchService = matchService;
//        this.clientTicketService = clientTicketService;
//        this.ticketService = ticketService;
//    }
//
//    @FXML
//    public void signInButtonClicked() {
//        String username = usernameField.getText();
//        String password = passwordField.getText();
//
//        // Aici poți adăuga logica de autentificare sau validare a datelor introduse
//        // Exemplu simplu: afișare un mesaj cu datele introduse
//        try{
//            Optional<User> user = userService.findUserByUsername(username);
//            if(user.isPresent()) {
//                if (user.get().getPassword().equals(password)) {
//                    System.out.println("Login successful");
//                    // Load the home view
//                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ro.mpp2024/home-view.fxml"));
//
//                    fxmlLoader.setControllerFactory(controllerClass -> {
//                        if (controllerClass == HomeViewController.class) {
//                            HomeViewController controller = new HomeViewController(clientService, matchService, clientTicketService, ticketService);
//                            controller.setLoggedInUser(user.get());
//                            return controller;
//                        } else {
//                            try {
//                                return controllerClass.newInstance();
//                            } catch (Exception exc) {
//                                throw new RuntimeException(exc);
//                            }
//                        }
//                    });
//
//                    Parent homeView = fxmlLoader.load();
//                    // Get the current stage
//                    Stage stage = (Stage) usernameField.getScene().getWindow();
//
//                    Scene scene = new Scene(homeView, 800, 600);
//
//                    stage.setTitle("Home");
//                    // Set the new scene
//                    stage.setScene(scene);
//                } else {
//                    System.out.println("Invalid password");
//                }
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        System.out.println("Username: " + username);
//        System.out.println("Password: " + password);
//    }
//}
package ro.mpp2024.matchclientfx.gui;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import ro.mpp2024.*;

import java.io.IOException;
import java.util.Optional;

public class LoginController implements IMatchObserver {
    private IMatchServices server;
    private HomeViewController appController;
    private User loggedInUser;
    Parent mainContestParent;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    public void setServer(IMatchServices s){
        if (s == null) {
            throw new IllegalArgumentException("Server cannot be null");
        }
        server=s;
    }

    public void setLoggedInUser(User user) {
        this.loggedInUser = user;
    }

    public void setParent(Parent p){
        mainContestParent=p;
    }

    public void setupLoginController() {
        LoginController loginController = new LoginController();
        loginController.setServer(server);
        loginController.setAppController(appController);
    }

    @FXML
    private void onLoginButton(ActionEvent event) {
        System.out.println("MAcacaca");
        String username = usernameField.getText();
        String password = passwordField.getText();
        loggedInUser = new User(username, password);

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    server.login(loggedInUser, appController);
                } catch (MatchException e) {
                    throw new RuntimeException(e);
                }
                return null;
            }
        };

        task.setOnSucceeded(e -> {
            Stage stage = new Stage();
            stage.setTitle("Welcome, " + loggedInUser.getUsername() + "!");
            stage.setScene(new Scene(mainContestParent));

            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
//                    appController.logout();
                    System.exit(0);
                }
            });

            stage.show();
            appController.setLoggedInUser(loggedInUser);
            ((Node)(event.getSource())).getScene().getWindow().hide();
        });

        task.setOnFailed(e -> {
            Throwable exception = task.getException();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("MPP chat");
            alert.setHeaderText("Authentication failure");
            alert.setContentText("Failed to login: " + exception.getMessage());
            alert.showAndWait();
        });

        new Thread(task).start();
    }

    public void setAppController(HomeViewController appController) {
        this.appController = appController;
    }

    @Override
    public void updateTicket(Ticket tickets) throws MatchException {

    }
}