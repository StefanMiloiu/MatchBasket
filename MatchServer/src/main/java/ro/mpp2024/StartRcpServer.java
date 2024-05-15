package ro.mpp2024;

import ro.mpp2024.utils.AbstractServer;
import ro.mpp2024.utils.MatchRpcConcurrentServer;


import java.io.IOException;
import java.util.Properties;

public class StartRcpServer {
    private static int defaultPort = 55555;

    public static void main(String[] args) {
        Properties serverProps = new Properties();
        try {
            serverProps.load(StartRcpServer.class.getResourceAsStream("/bd.properties"));
            System.out.println("Server properties set. ");
            serverProps.list(System.out);

            // Get database connection details
            String url = serverProps.getProperty("jdbc.url");
            String username = serverProps.getProperty("jdbc.username");
            String password = serverProps.getProperty("jdbc.password");

            // Initialize repositories
            UserDBRepository userDBRepository = new UserDBRepository(url, username, password);
            ClientTicketDBRepository clientTicketsDBRepository = new ClientTicketDBRepository(url, username, password);
            MatchDBRepository matchDBRepository = new MatchDBRepository(url, username, password);
            HibernateMatchDBRepository hibernateMatchDBRepository = new HibernateMatchDBRepository();
            TicketDBRepository ticketDBRepository = new TicketDBRepository(url, username, password);
            ClientDBRepository clientDBRepository = new ClientDBRepository(url, username, password);

            // Initialize service
            IMatchServices matchServices = new MatchServicesImpl(userDBRepository, clientTicketsDBRepository, hibernateMatchDBRepository, ticketDBRepository, clientDBRepository);

            int serverPort = defaultPort;
            try {
                serverPort = Integer.parseInt(serverProps.getProperty("ro.mpp2024.port"));
            } catch (NumberFormatException nef) {
                System.err.println("Wrong Port Number" + nef.getMessage());
                System.err.println("Using default port " + defaultPort);
            }
            System.out.println("Starting server on port: " + serverPort);


            AbstractServer server = new MatchRpcConcurrentServer(serverPort, matchServices);
            try {
                server.start();
            } catch (ro.mpp2024.utils.ServerException e) {
                throw new RuntimeException(e);
            } finally {
                try {
                    server.stop();
                } catch (ro.mpp2024.utils.ServerException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (IOException e) {
            System.err.println("Cannot find bd.properties " + e);
            return;
        }
    }
}