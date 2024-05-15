package ro.mpp2024;


import java.sql.SQLException;
import java.util.Optional;

public interface IMatchServices {
    void login(User crtOrganiser, IMatchObserver client) throws MatchException, SQLException;

    public Optional<User> findAccount(String username, String password);

    public Optional<Client> addClient(String name, int nrOfTickets);

    public void addClientTicket(Long ticketId, Long clientId) throws SQLException;

    public Iterable<HibernateMatch> findAllWithAvailableSeats();

    public Iterable<HibernateMatch> findAll();

    public Optional<Ticket> findOneTicketByMatch(Long matchId);

    public void updateTicket(Ticket ticket);

    public void sellTicket(Long ticketId, Long clientId) throws SQLException;

    void logout(User organiser) throws MatchException;
}

