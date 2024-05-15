package ro.mpp2024.rpcprotocol;

import ro.mpp2024.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MatchServicesRpcProxy implements IMatchServices {
    private String host;
    private int port;

    private IMatchObserver client;

    private ObjectInputStream input;
    private ObjectOutputStream output;
    private Socket connection;

    private BlockingQueue<Response> qresponses;
    private volatile boolean finished;

    public MatchServicesRpcProxy(String host, int port) {
        this.host = host;
        this.port = port;
        qresponses = new LinkedBlockingQueue<Response>();
    }

    private void closeConnection() {
        finished = true;
        try {
            input.close();
            output.close();
            connection.close();
            client = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendRequest(Request request)  {
        try {
            output.writeObject(request);
            output.flush();
        } catch (IOException e) {
            System.out.println("Error sending object " + e);
        }
    }

    private Response readResponse(){
        Response response = null;
        try {
            response = qresponses.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return response;
    }

    private void initializeConnection(){
        try {
            connection=new Socket(host,port);
            output=new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input=new ObjectInputStream(connection.getInputStream());
            finished=false;
            startReader();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startReader() {
        Thread tw = new Thread(new ReaderThread());
        tw.start();
    }

    private void handleUpdate(Response response) {
        if (response.type() == ResponseType.UPDATE) {
            System.out.println("Update received " + response.type());
            try {
                client.updateTicket((Ticket) response.data());
            } catch (MatchException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private boolean isUpdate(Response response) {
        return response.type() == ResponseType.UPDATE;
    }

    private class ReaderThread implements Runnable{
        public void run() {
            while(!finished){
                try {
                    Object response=input.readObject();
                    if (response instanceof Response) {
                        System.out.println("response received "+response);
                        if (isUpdate((Response)response)){
                            handleUpdate((Response)response);
                        }else{
                            try {
                                qresponses.put((Response)response);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        System.out.println("Unexpected object received: " + response);
                    }
                } catch (IOException e) {
                    System.out.println("Reading error "+e);
                } catch (ClassNotFoundException e) {
                    System.out.println("Reading error "+e);
                }
            }
        }
    }

    @Override
    public void login(User crtOrganiser, IMatchObserver client) throws MatchException, SQLException {
        System.out.println("Sent login request");
        initializeConnection();
        Request req = new Request.Builder().type(RequestType.LOGIN).data(crtOrganiser).build();
        sendRequest(req);
        Response response = readResponse();
        if (response.type() == ResponseType.OK) {
            if (response.data() != null) {
                this.client = client;
                return; // No need to return anything as the login method in IMatchServices interface doesn't return anything
            } else {
                throw new MatchException("Received null data in OK response");
            }
        }
        if (response.type() == ResponseType.ERROR) {
            String err = (String) response.data();
            throw new MatchException(err);
        }
        throw new MatchException("Received unknown response type");
    }

    @Override
    public Optional<User> findAccount(String username, String password) {
        Request request = new Request.Builder().type(RequestType.FIND_ACCOUNT).data(username + " " + password).build();
        sendRequest(request);
        Response response = null;
        response = readResponse();
        if (response.type() == ResponseType.ERROR) {
            return Optional.empty();
        }
        if (response.data() == null) {
            throw new RuntimeException("Received null data in OK response");
        }
        return Optional.of((User) response.data());
    }

    @Override
    public Optional<Client> addClient(String name, int nrOfTickets) {
        Request request = new Request.Builder().type(RequestType.ADD_CLIENT).data(name + " " + nrOfTickets).build();
        sendRequest(request);
        Response response = null;
        response = readResponse();
        if (response.type() == ResponseType.ERROR) {
            return Optional.empty();
        }
        if (response.data() == null) {
            throw new RuntimeException("Received null data in OK response");
        }
        return Optional.of((Client) response.data());
    }

    @Override
    public void addClientTicket(Long ticketId, Long clientId) throws SQLException {
        System.out.println(clientId + " " + ticketId);
        Request request = new Request.Builder().type(RequestType.ADD_CLIENT_TICKET).data(ticketId + " " + clientId).build();
        sendRequest(request);
        Response response = null;
        response = readResponse();
        if (response.type() == ResponseType.ERROR) {
            throw new SQLException((String) response.data());
        }
    }

    @Override
    public Iterable<Match> findAllWithAvailableSeats() {
        Request request = new Request.Builder().type(RequestType.FIND_ALL_WITH_AVAILABLE_SEATS).build();
        sendRequest(request);
        Response response = null;
        response = readResponse();
        if (response.type() == ResponseType.ERROR) {
            return Collections.emptyList();
        }
        return (Iterable<Match>) response.data();
    }

    @Override
    public Optional<Ticket> findOneTicketByMatch(Long matchId) {
        Request request = new Request.Builder().type(RequestType.FIND_ONE_TICKET_BY_MATCH).data(matchId).build();
        sendRequest(request);
        Response response = null;
        response = readResponse();
        if (response.type() == ResponseType.ERROR) {
            return Optional.empty();
        }
        return Optional.of((Ticket) response.data());
    }

    @Override
    public void updateTicket(Ticket ticket) {
        Request request = new Request.Builder().type(RequestType.UPDATE_TICKET).data(ticket).build();
        try {
            sendRequest(request);
            Response response = readResponse();
            if (response.type() == ResponseType.ERROR) {
                throw new MatchException((String) response.data());
            }
            // Notify the client about the changes
            client.updateTicket(ticket);
        } catch (MatchException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sellTicket(Long ticketId, Long clientId) throws SQLException {
        Request request = new Request.Builder().type(RequestType.ADD_CLIENT_TICKET).data(ticketId + " " + clientId).build();
        sendRequest(request);
        Response response = null;
        response = readResponse();
        if (response.type() == ResponseType.ERROR) {
            throw new SQLException((String) response.data());
        }
    }

    @Override
    public Iterable<Match> findAll() {
        Request request = new Request.Builder().type(RequestType.FIND_ALL).build();
        sendRequest(request);
        Response response = null;
        response = readResponse();
        if (response.type() == ResponseType.ERROR) {
            return Collections.emptyList();
        }
        return (Iterable<Match>) response.data();
    }

    @Override
    public void logout(User user) throws MatchException {
        Request req = new Request.Builder().type(RequestType.LOGOUT).data(user).build();
        sendRequest(req);

        Response response = readResponse();
        closeConnection();

        if (response.type()== ResponseType.ERROR){
            throw new MatchException("");
        }
        else{
            System.out.println("Successfully logged out!");

        }
    }
}