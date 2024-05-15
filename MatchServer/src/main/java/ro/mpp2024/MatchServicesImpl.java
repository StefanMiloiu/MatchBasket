package ro.mpp2024;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MatchServicesImpl implements IMatchServices {
    private UserDBRepository userDBRepository;
    private     ClientTicketDBRepository clientTicketsDBRepository;

    private ClientDBRepository clientDBRepository;

//    private MatchDBRepository matchDBRepository;
    private HibernateMatchDBRepository matchDBRepository;
    private TicketDBRepository ticketDBRepository;
    private Map<String, List<IMatchObserver>> loggedUsers;
    private List<IMatchObserver> observers = new ArrayList<>();

    private int defaultThreads = 5;

    public MatchServicesImpl(UserDBRepository userDBRepository, ClientTicketDBRepository clientTicketsDBRepository, HibernateMatchDBRepository matchDBRepository, TicketDBRepository ticketDBRepository, ClientDBRepository clientDBRepository) {
        this.userDBRepository = userDBRepository;
        this.clientTicketsDBRepository = clientTicketsDBRepository;
        this.matchDBRepository = matchDBRepository;
        this.ticketDBRepository = ticketDBRepository;
        this.clientDBRepository = clientDBRepository;
        loggedUsers = new HashMap<>();
    }

    @Override
    public void login(User crtOrganiser, IMatchObserver client) throws MatchException, SQLException {
        Optional<User> user = userDBRepository.findOne(crtOrganiser.getUsername());
        if (user.isPresent()) {
            if (loggedUsers.containsKey(crtOrganiser.getUsername())) {
                throw new MatchException("User already logged in.");
            }
            loggedUsers.put(crtOrganiser.getUsername(), new ArrayList<>());
            loggedUsers.get(crtOrganiser.getUsername()).add(client);
            observers.add(client);
        } else {
            throw new MatchException("Authentication failed.");
        }
    }

    @Override
    public Optional<User> findAccount(String username, String password) {
        try {
            return userDBRepository.findOne(username);
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public Optional<Client> addClient(String name, int nrOfTickets) {
        try {
            Client client = new Client(name, nrOfTickets);
            System.out.println(client);
            Optional<Client> clientId = clientDBRepository.save(client);
            System.out.println(clientId);
            if (clientId.isPresent()) {
                System.out.println("Got client servicesImpl");
                return clientId;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
        return Optional.empty();
    }

    @Override
    public void addClientTicket(Long clientId, Long ticketId) throws SQLException {
        System.out.println(ticketId);
        Optional<Ticket> ticket = ticketDBRepository.findOne(ticketId);
        System.out.println(clientId);
        System.out.println("Clientul");
        Optional<Client> client = clientDBRepository.findOne(clientId);
        System.out.println(client);
        System.out.println(ticket);
        if (ticket.isPresent()) {
            ticket.get().setAvailableSeats(ticket.get().getAvailableSeats() - client.get().getTicketsBought());
            System.out.println(ticket.get().getAvailableSeats());
            ticketDBRepository.update(ticket.get());
            ClientTickets clientTickets = new ClientTickets(ticketId, clientId);
            clientTicketsDBRepository.save(clientTickets);
            notify(ticket.get());
        }
    }

    @Override
    public Iterable<HibernateMatch> findAllWithAvailableSeats() {
        try {
            return matchDBRepository.findAllWithAvailableSeats();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public Iterable<HibernateMatch> findAll() {
        return matchDBRepository.findAll();
    }

    @Override
    public Optional<Ticket> findOneTicketByMatch(Long matchId) {
        try {
            return ticketDBRepository.find(matchId);
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public void updateTicket(Ticket ticket) {
        try {
            ticketDBRepository.update(ticket);
            notify(ticket);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sellTicket(Long ticketId, Long clientId) throws SQLException {
        Optional<Ticket> ticket = ticketDBRepository.findOne(ticketId);
        if (ticket.isPresent()) {
            ticket.get().setAvailableSeats(ticket.get().getAvailableSeats() - 1);
            ticketDBRepository.update(ticket.get());
            ClientTickets clientTickets = new ClientTickets(ticketId, clientId);
            clientTicketsDBRepository.save(clientTickets);
            notify(ticket.get());
        }
    }

    @Override
    public void logout(User user) throws MatchException {
        if (!loggedUsers.containsKey(user.getUsername())) {
            throw new MatchException("User not logged in.");
        }
        loggedUsers.remove(user.getUsername());
    }

    // Notify all observers
    private void notify(Ticket ticket) {
        ExecutorService executor = Executors.newFixedThreadPool(defaultThreads);
        for (Map.Entry<String, List<IMatchObserver>> entry : loggedUsers.entrySet()) {
            for (IMatchObserver observer : entry.getValue()) {
                executor.execute(() -> {
                    try {
                        observer.updateTicket(ticket);
                    } catch (MatchException e) {
                        System.err.println("Error notifying observer " + observer);
                    }
                });
            }
        }
        executor.shutdown();
    }
}