//package ro.mpp2024.matchclientfx.gui;
//
//import javafx.event.ActionEvent;
//import javafx.fxml.FXML;
//import javafx.fxml.FXMLLoader;
//import javafx.scene.Parent;
//import javafx.scene.Scene;
//import javafx.scene.control.TableCell;
//import javafx.scene.control.TableColumn;
//import javafx.scene.control.TableView;
//import javafx.scene.control.TextField;
//import javafx.scene.control.cell.PropertyValueFactory;
//import javafx.stage.Stage;
//import ro.mpp2024.*;
//
//import java.io.IOException;
//import java.sql.SQLException;
//import java.util.Optional;
//
//public class HomeViewController implements IMatchObserver {
//
////    private final ClientService clientService;
////    private final MatchService matchService;
////    private final ClientTicketService clientTicketService;
////    private final TicketService ticketService;
//
//    private IMatchServices server;
//
//    private HomeViewController appController;
//
//    public void setLoggedInUser(User user) {
//        this.loggedInUser = user;
//        initialize(user);
//    }
//
//    public void setServer(IMatchServices s){
//        if (s == null) {
//            throw new IllegalArgumentException("Server cannot be null");
//        }
//        server=s;
//    }
//
//    private User loggedInUser;
//
//    @FXML
//    private javafx.scene.control.Button logoutButton;
//
//
//    @FXML
//    private TableView<DTOMatch> table;
//
//    @FXML
//    private TableColumn<DTOMatch, String> column1;
//
//    @FXML
//    private TableColumn<DTOMatch, Float> column2;
//
//    @FXML
//    private TableColumn<DTOMatch, Integer> column3;
//
//    @FXML
//    private TextField clientName;
//
//    @FXML
//    private TextField nrOfTickets;
//
//    @FXML
//    public void initialize(User user) {
//        column1.setCellValueFactory(new PropertyValueFactory<>("matchString"));
//        column2.setCellValueFactory(new PropertyValueFactory<>("price"));
//        column3.setCellValueFactory(new PropertyValueFactory<>("seatsAvailable"));
//
//        // Set a custom cell factory for column3
//        column3.setCellFactory(column -> new TableCell<DTOMatch, Integer>() {
//            @Override
//            protected void updateItem(Integer item, boolean empty) {
//                super.updateItem(item, empty);
//
//                if (item == null || empty) {
//                    setText(null);
//                    setStyle("");
//                } else {
//                    // If seats available is 0, set text to "Sold Out" and color it red
//                    if (item == 0) {
//                        setText("Sold Out");
//                        setStyle("-fx-text-fill: red;");
//                    } else {
//                        setText(item.toString());
//                        setStyle("");
//                    }
//                }
//            }
//        });
//
//        for (Match match : matchService.findAll()) {
//            Optional<Ticket> ticket = ticketService.findOneTicketByMatch(match.getId());
//            if (ticket.isPresent()) {
//                Float price = ticket.get().getPrice();
//                Integer seatsAvailable = ticket.get().getAvailableSeats();
//                DTOMatch dtoMatch = new DTOMatch(match, price, seatsAvailable);
//                table.getItems().add(dtoMatch);
//            }
//        }
//    }
//
//    public void handleSellButtonAction(ActionEvent actionEvent) {
//        // Get the selected match from the table
//        DTOMatch selectedMatch = table.getSelectionModel().getSelectedItem();
//
//        if (selectedMatch != null && !clientName.getText().isEmpty() && !nrOfTickets.getText().isEmpty()) {
//            // Check if there are available seats
//            if (selectedMatch.getSeatsAvailable() > 0) {
//                // Get the client's name
//                String clientName = this.clientName.getText();
//
//                // Create a new client
//                Optional<Client> clientOptional = clientService.addClient(clientName, Integer.parseInt(nrOfTickets.getText()));
//
//                if (clientOptional.isPresent()) {
//                    Client client = clientOptional.get();
//                    if (client.getId() != null) {
//                        Long clientId = client.getId();
//
//                        // Get the ticket
//                        Optional<Ticket> ticketOptional = ticketService.findOneTicketByMatch(selectedMatch.getMatch().getId());
//                        if (ticketOptional.isPresent()) {
//                            Ticket ticket = ticketOptional.get();
//                            ticket.setAvailableSeats(ticket.getAvailableSeats() - Integer.parseInt(nrOfTickets.getText()));
//
//                            // Update the ticket
//                            ticketService.updateTicket(ticket);
//
//                            // Add a new client ticket
//                            try {
//                                clientTicketService.addClientTicket(ticket.getId(), clientId);
//                            } catch (SQLException e) {
//                                e.printStackTrace();
//                            }
//
//                            // Update the number of available seats for the selected match
//                            selectedMatch.setSeatsAvailable(selectedMatch.getSeatsAvailable() - Integer.parseInt(nrOfTickets.getText()));
//
//                            // Update the table
//                            table.refresh();
//
//                            System.out.println("Ticket sold to " + clientName);
//                        }
//                    }
//                }
//            } else {
//                System.out.println("No available seats for the selected match");
//            }
//        } else {
//            System.out.println("No match selected or client name/ticket number is empty");
//        }
//    }
//
//    public void handleLogoutButtonAction(ActionEvent actionEvent) {
//        try {
//            // Load the login view
//            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ro.mpp2024/login-view.fxml"));
//
//            // Set the controller factory
//            fxmlLoader.setControllerFactory(c -> new LoginController(clientService, matchService, clientTicketService, ticketService));
//
//            Parent loginView = fxmlLoader.load();
//
//            // Get the current stage
//            Stage stage = (Stage) logoutButton.getScene().getWindow();
//            // Create a new scene with the login view and set it on the stage
//            Scene scene = new Scene(loginView, 320, 240);
//            stage.setScene(scene);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void handleUnfiltreButtonAction(ActionEvent actionEvent) {
//        table.getItems().clear();
//        for (Match match : matchService.findAll()) {
//            Optional<Ticket> ticket = ticketService.findOneTicketByMatch(match.getId());
//            if (ticket.isPresent()) {
//                Float price = ticket.get().getPrice();
//                Integer seatsAvailable = ticket.get().getAvailableSeats();
//                DTOMatch dtoMatch = new DTOMatch(match, price, seatsAvailable);
//                table.getItems().add(dtoMatch);
//            }
//        }
//    }
//
//    public void handleCautareButtonAction(ActionEvent actionEvent) {
//        table.getItems().clear();
//        for (Match match : matchService.findAllWithAvailableSeats()) {
//            Optional<Ticket> ticket = ticketService.findOneTicketByMatch(match.getId());
//            if (ticket.isPresent()) {
//                Float price = ticket.get().getPrice();
//                Integer seatsAvailable = ticket.get().getAvailableSeats();
//                DTOMatch dtoMatch = new DTOMatch(match, price, seatsAvailable);
//                table.getItems().add(dtoMatch);
//            }
//        }
//    }
//
//    @Override
//    public void updateTicket(Ticket tickets) throws MatchException {
//
//    }
//}

package ro.mpp2024.matchclientfx.gui;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import ro.mpp2024.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class HomeViewController implements IMatchObserver {
    private IMatchServices server;
    private User loggedInUser;

    @FXML
    private TableView<DTOMatch> table;

    @FXML
    private TableColumn<DTOMatch, String> column1;

    @FXML
    private TableColumn<DTOMatch, Float> column2;

    @FXML
    private TableColumn<DTOMatch, Integer> column3;

    @FXML
    private TextField clientName;

    @FXML
    private TextField nrOfTickets;

    public void setServer(IMatchServices s){
        if (s == null) {
            throw new IllegalArgumentException("Server cannot be null");
        }
        server=s;
    }

    public void setLoggedInUser(User user) {
        this.loggedInUser = user;
        initialize(user);
    }

    @FXML
    public void initialize(User user) {
        System.out.println("Entered initialize method user: " + user);
        column1.setCellValueFactory(new PropertyValueFactory<>("matchString"));
        column2.setCellValueFactory(new PropertyValueFactory<>("price"));
        column3.setCellValueFactory(new PropertyValueFactory<>("seatsAvailable"));

        // Set a custom cell factory for column3
        column3.setCellFactory(column -> new TableCell<DTOMatch, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setText(null);
                    setStyle("");
                } else {
                    // If seats available is 0, set text to "Sold Out" and color it red
                    if (item == 0) {
                        setText("Sold Out");
                        setStyle("-fx-text-fill: red;");
                    } else {
                        setText(item.toString());
                        setStyle("");
                    }
                }
            }
        });

        // Load matches from the server
        loadMatches();
    }

    private void loadMatches() {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                server.findAll().forEach(match -> {
                    AtomicReference<Float> price = null;
                    Optional<Ticket> ticket = server.findOneTicketByMatch(match.getId());
                    if (ticket.isPresent()) {
                        int seatsAvailable = ticket.get().getAvailableSeats();
                        DTOMatch dtoMatch = new DTOMatch(match, ticket.get().getPrice(), seatsAvailable);
                        Platform.runLater(() -> table.getItems().add(dtoMatch));
                    }
                });
                return null;
            }
        };

        new Thread(task).start();
    }

    @FXML
    private void handleUnfiltreButtonAction(ActionEvent actionEvent) {
        table.getItems().clear();
        loadMatches();
    }

    @FXML
    private void handleCautareButtonAction(ActionEvent actionEvent) {
        table.getItems().clear();
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                List<DTOMatch> matches = new ArrayList<>();
                server.findAllWithAvailableSeats().forEach(match -> {
                    Optional<Ticket> ticket = server.findOneTicketByMatch(match.getId());
                    if (ticket.isPresent()) {
                        int seatsAvailable = ticket.get().getAvailableSeats();
                        DTOMatch dtoMatch = new DTOMatch(match, ticket.get().getPrice(), seatsAvailable);
                        matches.add(dtoMatch);
                    }
                    Platform.runLater(() -> table.getItems().addAll(matches));
                });
                return null;
            }
        };

        new Thread(task).start();
    }

    @FXML
    private void handleSellButtonAction(ActionEvent event) {
        // Get the selected match from the table
        DTOMatch selectedMatch = table.getSelectionModel().getSelectedItem();

        if (selectedMatch != null && !clientName.getText().isEmpty() && !nrOfTickets.getText().isEmpty()) {
            // Check if there are available seats
            if (selectedMatch.getSeatsAvailable() > 0) {
                // Get the client's name
                String clientName = this.clientName.getText();

                // Create a new client
                Client client = new Client(clientName, Integer.parseInt(nrOfTickets.getText()));

                Optional<Client> clientWithId = server.addClient(client.getName(), client.getTicketsBought());

                // Sell the ticket
                clientWithId.ifPresent(value -> sellTicket(value, selectedMatch));
            } else {
                System.out.println("No available seats for the selected match");
            }
        } else {
            System.out.println("No match selected or client name/ticket number is empty");
        }
    }

    private void sellTicket(Client client, DTOMatch selectedMatch) {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Optional<Ticket> ticket = server.findOneTicketByMatch(selectedMatch.getMatch().getId());
                if (ticket.isEmpty()) {
                    throw new RuntimeException("No ticket found for match " + selectedMatch.getMatch().getId());
                }
                System.out.println("Adding client ticket..." + ticket.get().getId() + " " + client.getId());
                server.addClientTicket(ticket.get().getId(), client.getId());
                return null;
            }
        };

        task.setOnSucceeded(e -> {
            // Update the number of available seats for the selected match
            selectedMatch.setSeatsAvailable(selectedMatch.getSeatsAvailable() - Integer.parseInt(nrOfTickets.getText()));

            // Update the table
            Platform.runLater(() -> table.refresh());

            System.out.println("Ticket sold to " + client.getName());
        });

        task.setOnFailed(e -> {
            Throwable exception = task.getException();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("MPP error");
            alert.setHeaderText("Selling failure");
            alert.setContentText("Failed to sell ticket: " + exception.getMessage());
            Platform.runLater(() -> alert.showAndWait());
        });

        new Thread(task).start();
    }

    @FXML
    private void handleLogoutButtonAction(ActionEvent actionEvent) {
        try {
            // Load the login view
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ro/mpp2024/matchclientfx/login-view.fxml"));

            Parent loginView = fxmlLoader.load();

            // Get the current stage
            Stage stage = (Stage) table.getScene().getWindow();
            // Create a new scene with the login view and set it on the stage
            server.logout(loggedInUser);
            Scene scene = new Scene(loginView, 320, 240);
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MatchException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateTicket(Ticket tickets) throws MatchException {
        // Refresh the table when a ticket is updated
        table.refresh();
    }
}