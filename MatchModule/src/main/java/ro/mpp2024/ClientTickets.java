package ro.mpp2024;

import java.util.Objects;

public class ClientTickets extends Entity<Long> {

    private Long id;
    private Long ticketId;

    private Long clientId;

    public ClientTickets(Long ticketId, Long clientId) {
        this.ticketId = ticketId;
        this.clientId = clientId;
    }

    public ClientTickets(Long id, Long ticketId, Long clientId) {
        super.setId(id);
        this.id = id;
        this.ticketId = ticketId;
        this.clientId = clientId;
    }

    public Long getId() {
        return id;
    }

    public Long getTicketId() {
        return ticketId;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setTicketId(Long ticketId) {
        this.ticketId = ticketId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    @Override
    public String toString() {
        return "ClientTickets{" +
                "id=" + id +
                ", ticketId=" + ticketId +
                ", clientId=" + clientId +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClientTickets)) return false;
        ClientTickets clientTickets = (ClientTickets) o;
        return getId().equals(clientTickets.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
