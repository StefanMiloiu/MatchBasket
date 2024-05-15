package ro.mpp2024;

public class DTOMatch {

    private HibernateMatch match;
    private Float price; // Change this line
    private int seatsAvailable;

    private String matchString;

    public DTOMatch(HibernateMatch s, float i, int i1) {
        this.match = s;
        this.price = i; // And this line
        this.seatsAvailable = i1;
        this.matchString = s.getTeamA() + " VS " + s.getTeamB();
    }

    public String getMatchString() {
        return this.matchString;
    }
    public HibernateMatch getMatch() {
        return match;
    }

    public Float getPrice() { // And this line
        return price;
    }

    public void setPrice(Float price) { // And this line
        this.price = price;
    }

    public int getSeatsAvailable() {
        return seatsAvailable;
    }

    public void setSeatsAvailable(int seatsAvailable) {
        this.seatsAvailable = seatsAvailable;
    }

    @Override
    public String toString() {
        return "DTOMatch{" +
                "match='" + match + '\'' +
                ", price=" + price +
                ", seatsAvailable=" + seatsAvailable +
                '}';
    }
}