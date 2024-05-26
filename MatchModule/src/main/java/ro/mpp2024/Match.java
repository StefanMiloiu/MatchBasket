package ro.mpp2024;

public class Match extends Entity<Long>{

    private Long id;
    private String teamA;
    private String teamB;

    private String matchType;


    public Match(String teamA, String teamB, String matchType) {
        this.teamA = teamA;
        this.teamB = teamB;
        this.matchType = matchType;
    }

    // Default constructor
    public Match() {
    }
    public Match(Long id, String teamA, String teamB, String matchType) {
        super.setId(id);
        this.id = id;
        this.teamA = teamA;
        this.teamB = teamB;
        this.matchType = matchType;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getTeamA() {
        return teamA;
    }

    public String getTeamB() {
        return teamB;
    }

    public String getMatchType() {
        return matchType;
    }

    public void setMatchType(String matchType) {
        this.matchType = matchType;
    }

    @Override
    public String toString() {
        return "Match{" +
                "id=" + id +
                ", teamA='" + teamA + '\'' +
                ", teamB='" + teamB + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Match)) return false;
        Match match = (Match) o;
        return getId().equals(match.getId());
    }

}

