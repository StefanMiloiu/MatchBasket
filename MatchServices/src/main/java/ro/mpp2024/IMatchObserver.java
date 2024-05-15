package ro.mpp2024;

public interface IMatchObserver {
    void updateTicket(Ticket tickets) throws MatchException;
}