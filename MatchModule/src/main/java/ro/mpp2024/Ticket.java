package ro.mpp2024;

import java.util.Objects;

public class Ticket extends Entity<Long> {

    private Long id;
    private Float price;
    private int availableSeats;
    private Long matchId; // New field

    public Ticket(Float price, int availableSeats, Long matchId) {
        this.price = price;
        this.availableSeats = availableSeats;
        this.matchId = matchId; // Initialize new field
    }

    public Ticket(Long id, Float price, int availableSeats, Long matchId) {
        super.setId(id);
        this.id = id;
        this.price = price;
        this.availableSeats = availableSeats;
        this.matchId = matchId; // Initialize new field
    }

    public Long getId() {
        return id;
    }

    public Float getPrice() {
        return price;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public Long getMatchId() { // Getter for new field
        return matchId;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }

    public void setMatchId(Long matchId) { // Setter for new field
        this.matchId = matchId;
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "id=" + id +
                ", price=" + price +
                ", availableSeats=" + availableSeats +
                ", matchId=" + matchId +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Ticket)) return false;
        Ticket ticket = (Ticket) o;
        return getId().equals(ticket.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

}