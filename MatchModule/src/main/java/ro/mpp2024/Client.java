package ro.mpp2024;

public class Client extends Entity<Long>{

    private Long id;
    private String name;
    private int ticketsBought;

    public Client(String name, int ticketsBought) {
        this.name = name;
        this.ticketsBought = ticketsBought;
    }

    public Client(Long id, String name, int ticketsBought) {
        super.setId(id);
        this.id = id;
        this.name = name;
        this.ticketsBought = ticketsBought;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getTicketsBought() {
        return ticketsBought;
    }

    public void setTicketsBought(int ticketsBought) {
        this.ticketsBought = ticketsBought;
    }

    @Override
    public String toString() {
        return "Client{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", ticketsBought=" + ticketsBought +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Client)) return false;
        Client client = (Client) o;
        return getId().equals(client.getId());
    }
}
