//package ro.mpp2024.matchclientfx;
//
//
//import javafx.application.Application;
//import javafx.fxml.FXMLLoader;
//import javafx.scene.Parent;
//import javafx.scene.Scene;
//import javafx.stage.Stage;
//import ro.mpp2024.ClientDBRepository;
//import ro.mpp2024.ClientTicketDBRepository;
//
//import ro.mpp2024.IMatchServices;
//import ro.mpp2024.TicketDBRepository;
//import ro.mpp2024.matchclientfx.gui.LoginController;
//import ro.mpp2024.rpcprotocol.MatchServicesRpcProxy;
//
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.util.Properties;
//
//public class LoginView extends Application {
//    private Stage primaryStage;
//
//    private static int defaultContestPort = 55555;
//    private static String defaultServer = "localhost";
//    @Override
//    public void start(Stage stage) throws IOException {
//        Properties properties = new Properties();
//        try {
//            properties.load(Files.newBufferedReader(Paths.get("./bd.config")));
//            System.out.println("Client properties set. ");
//            properties.list(System.out);
//        } catch (IOException e) {
//            throw new RuntimeException("Failed to load database configuration", e);
//        }
//        String serverIP = properties.getProperty("ro.mpp2024.host", defaultServer);
//        int serverPort = defaultContestPort;
//
//        try {
//            serverPort = Integer.parseInt(properties.getProperty("ro.mpp2024.port"));
//        } catch (NumberFormatException ex) {
//            System.err.println("Wrong port number " + ex.getMessage());
//            System.out.println("Using default port: " + defaultContestPort);
//        }
//        System.out.println("Using server IP " + serverIP);
//        System.out.println("Using server port " + serverPort);
//
//        IMatchServices server = new MatchServicesRpcProxy(serverIP, serverPort);
//
//
//
//
//        String url = properties.getProperty("jdbc.url");
//        String username = properties.getProperty("jdbc.username");
//        String password = properties.getProperty("jdbc.password");
//
//        ClientDBRepository clientDBRepository = new ClientDBRepository(url, username, password);
//        ClientService clientService = new ClientService(clientDBRepository);
//
//        MatchDBRepository matchDBRepository = new MatchDBRepository(url, username, password);
//        MatchService matchService = new MatchService(matchDBRepository);
//
//        ClientTicketDBRepository clientTicketDBRepository = new ClientTicketDBRepository(url, username, password);
//        ClientTicketService clientTicketService = new ClientTicketService(clientTicketDBRepository);
//
//        TicketDBRepository ticketDBRepository = new TicketDBRepository(url, username, password);
//        TicketService ticketService = new TicketService(ticketDBRepository);
//
//
//        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ro.mpp2024/login-view.fxml"));
//        fxmlLoader.setControllerFactory(c -> new LoginController(clientService, matchService, clientTicketService, ticketService));
//
//        Parent root = fxmlLoader.load();
//        Scene scene = new Scene(root, 320, 240);
//        stage.setTitle("Login");
//        stage.setScene(scene);
//        stage.show();
//    }
//
//    public static void main(String[] args) {
//        launch();
//    }
//}

package ro.mpp2024.matchclientfx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ro.mpp2024.IMatchServices;
import ro.mpp2024.matchclientfx.gui.HomeViewController;
import ro.mpp2024.rpcprotocol.MatchServicesRpcProxy;
import ro.mpp2024.matchclientfx.gui.LoginController;

import java.io.IOException;
import java.util.Properties;

public class LoginView extends Application {
    private Stage primaryStage;

    private static int defaultContestPort = 55555;
    private static String defaultServer = "localhost";

    @Override
    public void start(Stage primaryStage) throws Exception {
        Properties clientProps = new Properties();
        try {
            clientProps.load(LoginView.class.getResourceAsStream("/ro/mpp2024/matchclientfx/bd.properties"));
            System.out.println("Client properties set. ");
            clientProps.list(System.out);
        } catch (IOException e) {
            System.err.println("Cannot find bd.properties " + e);
            return;
        }
        String serverIP = clientProps.getProperty("ro.mpp2024.host", defaultServer);
        int serverPort = defaultContestPort;

        try {
            serverPort = Integer.parseInt(clientProps.getProperty("ro.mpp2024.port"));
        } catch (NumberFormatException ex) {
            System.err.println("Wrong port number " + ex.getMessage());
            System.out.println("Using default port: " + defaultContestPort);
        }
        System.out.println("Using server IP " + serverIP);
        System.out.println("Using server port " + serverPort);

        IMatchServices server = new MatchServicesRpcProxy(serverIP, serverPort);

        FXMLLoader loginLoader = new FXMLLoader(getClass().getClassLoader().getResource("ro/mpp2024/matchclientfx/login-view.fxml"));
        Parent loginRoot = loginLoader.load();

        LoginController loginCtrl = loginLoader.getController();
        loginCtrl.setServer(server);
        loginCtrl.setupLoginController();

        FXMLLoader mainLoader = new FXMLLoader(getClass().getClassLoader().getResource("ro/mpp2024/matchclientfx/home-view.fxml"));
        Parent mainContestParent = mainLoader.load();

        HomeViewController appCtrl = mainLoader.getController();
        appCtrl.setServer(server);

        loginCtrl.setParent(mainContestParent);
        loginCtrl.setAppController(appCtrl);
        primaryStage.setTitle("Match");
        primaryStage.setScene(new Scene(loginRoot, 320, 240));
        System.out.println("Am ajuns aici");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}