package ro.mpp2024.rpcprotocol;

import ro.mpp2024.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Optional;

public class MatchClientRpcReflectionWorker implements Runnable, IMatchObserver {
    private IMatchServices server;
    private Socket connection;

    private ObjectInputStream input;
    private ObjectOutputStream output;
    private volatile boolean connected;

    public MatchClientRpcReflectionWorker(IMatchServices server, Socket connection) {
        this.server = server;
        this.connection = connection;
        try {
            output = new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input = new ObjectInputStream(connection.getInputStream());
            connected = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while (connected) {
            try {
                Object request = input.readObject();
                Response response = handleRequest((Request) request);
                if (response != null) {
                    sendResponse(response);
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /*@Override
    public void updatedRezultate(Iterable<Rezultat> updatedRezultate) throws TriatlonException {
        try {
            Request request = new Request.Builder().type(RequestType.UPDATE_REZULTAT).data(updatedRezultate).build();
            sendRequest(request);
            Response response = readResponse();
            if (response.type() == ResponseType.ERROR) {
                String err = (String) response.data();
                throw new TriatlonException(err);
            }
        } catch (IOException e) {
            throw new TriatlonException("Error sending updated rezultate request " + e);
        }
    }*/

    private static Response okResponse = new Response.Builder().type(ResponseType.OK).build();

    private Response handleRequest(Request request) {
        System.out.println("Request received: " + request.type());
        Response response = null;
        String handlerName = "handle" + (request).type();
        System.out.println("HandlerName " + handlerName);
        try {
            Method method = this.getClass().getDeclaredMethod(handlerName, Request.class);
            response = (Response) method.invoke(this, request);
            System.out.println("Method " + handlerName + " invoked");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return response;
    }

    private void sendResponse(Response response) throws IOException {
        System.out.println("sending response " + response);
        synchronized (output) {
            output.writeObject(response);
            output.flush();
        }
    }

    private void sendRequest(Request request) throws IOException {
        output.writeObject(request);
        output.flush();
    }

    private Response handleLOGIN(Request request) {
        System.out.println("Login request...");
        User user = (User) request.data();
        try {
            server.login(user, this);
            Optional<User> account = server.findAccount(user.getUsername(), user.getPassword());
            if (account.isPresent()) {
                return new Response.Builder().type(ResponseType.OK).data(account.get()).build();
            } else {
                return new Response.Builder().type(ResponseType.ERROR).data("Account not found").build();
            }
        } catch (MatchException e) {
            connected = false;
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Response handleFIND_ALL(Request request) {
        System.out.println("Find all request...");
        Iterable<HibernateMatch> allMatches = server.findAll();
        return new Response.Builder().type(ResponseType.OK).data(allMatches).build();
    }

    private Response handleADD_CLIENT(Request request) {
        System.out.println("Add client request...");
        String nameAndNrOfTickets = (String) request.data();
        System.out.println("Name and nr of tickets: " + nameAndNrOfTickets);
        String[] tokens = nameAndNrOfTickets.split(" ");
        Optional<Client> client = server.addClient(tokens[0], Integer.parseInt(tokens[1]));
        return new Response.Builder().type(ResponseType.OK).data(client.orElse(null)).build();
    }

    private Response handleADD_CLIENT_TICKET(Request request) {
        String ids = (String) request.data();
        String[] tokens = ids.split(" ");
        if (tokens.length < 2) {
            return new Response.Builder().type(ResponseType.ERROR).data("Invalid request data format").build();
        }
        System.out.println("Adding client ticket..." + tokens[1] + " " + tokens[0]);
        try {
            server.addClientTicket(Long.parseLong(tokens[1]), Long.parseLong(tokens[0]));
            return okResponse;
        } catch (SQLException e) {
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }

    private Response handleFIND_ALL_WITH_AVAILABLE_SEATS(Request request) {
        Iterable<HibernateMatch> allMatches = server.findAllWithAvailableSeats();
        return new Response.Builder().type(ResponseType.OK).data(allMatches).build();
    }

    private Response handleLOGOUT(Request request) {
        System.out.println("Logout request...");
        User user = (User) request.data();
        try {
            server.logout(user);
            connected = false;
            return okResponse;
        } catch (MatchException e) {
            throw new RuntimeException(e);
        }
    }


    private Response handleFIND_ONE_TICKET_BY_MATCH(Request request) {
        Long matchId = (Long) request.data();
        Optional<Ticket> ticket = server.findOneTicketByMatch(matchId);
        return new Response.Builder().type(ResponseType.OK).data(ticket.orElse(null)).build();
    }

    private Response handleFIND_ACCOUNT(Request request){
        String usernameAndPassword = (String) request.data();
        String[] tokens = usernameAndPassword.split(" ");
        Optional<User> account = server.findAccount(tokens[0], tokens[1]);
        if (account.isPresent()) {
            return new Response.Builder().type(ResponseType.OK).data(account.get()).build();
        } else {
            return new Response.Builder().type(ResponseType.ERROR).data("Account not found").build();
        }
    }

    private Response handleUPDATE_TICKET(Request request) {
        Ticket ticket = (Ticket) request.data();
        server.updateTicket(ticket);
        return okResponse;
//        Participant participant = (Participant) request.data();
//        server.updateParticipant(participant);
//        // Fetch the updated Participant from the server
//        Iterable<Participant> updatedParticipanti = server.findAllParticipants();
//        // Return the updated Participant in the response
//        return new Response.Builder().type(ResponseType.OK).data(updatedParticipanti).build();
    }


//    private Response handleFIND_ALL_REZULTATE(Request request) {
////        Iterable<Rezultat> allRezultate = server.findAllRezultate();
////        return new Response.Builder().type(ResponseType.OK).data(allRezultate).build();
//    }
//
//    private Response handleUPDATE_REZULTAT(Request request) {
////        String[] tokens = ((String) request.data()).split(" ");
////        Long idparticipant = Long.parseLong(tokens[0]);
////        Long idproba = Long.parseLong(tokens[1]);
////        int puncte = Integer.parseInt(tokens[2]);
////        server.updateRezultat(idparticipant, idproba, puncte);
////
////        // Fetch the updated results from the server
////        Iterable<Rezultat> updatedRezultate = server.findAllRezultate();
////
////        // Return the updated results in the response
////        return new Response.Builder().type(ResponseType.OK).data(updatedRezultate).build();
//    }


    private Response readResponse() {
        Response response = null;
        try {
            response = (Response) input.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return response;
    }

    @Override
    public void updateTicket(Ticket tickets) throws MatchException {
        Response response = new Response.Builder().type(ResponseType.UPDATE).data(tickets).build();
        try {
            sendResponse(response);
        } catch (IOException e) {
            throw new MatchException("Sending error: " + e);
        }
    }
}