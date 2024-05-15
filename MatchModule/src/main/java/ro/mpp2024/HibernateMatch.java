package ro.mpp2024;

import javax.persistence.*;
import javax.persistence.Entity;
import java.io.Serializable;

@Entity
@Table(name = "Matches")
public class HibernateMatch implements Serializable, ro.mpp2024.HibernateEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "team_a", nullable = false, length = 100)
    private String teamA;

    @Column(name = "team_b", nullable = false, length = 100)
    private String teamB;

    @Column(name = "match_type", nullable = false, length = 50)
    private String matchType;

    // Constructor fără argumente necesar pentru Hibernate
    public HibernateMatch() {}

    // Constructori pentru inițializarea obiectelor
    public HibernateMatch(String teamA, String teamB, String matchType) {
        this.teamA = teamA;
        this.teamB = teamB;
        this.matchType = matchType;
    }

    public HibernateMatch(Long id, String teamA, String teamB, String matchType) {
        this.id = id;
        this.teamA = teamA;
        this.teamB = teamB;
        this.matchType = matchType;
    }

    // Getteri și setteri
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTeamA() {
        return teamA;
    }

    public void setTeamA(String teamA) {
        this.teamA = teamA;
    }

    public String getTeamB() {
        return teamB;
    }

    public void setTeamB(String teamB) {
        this.teamB = teamB;
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
                ", matchType='" + matchType + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Match)) return false;
        Match match = (Match) o;
        return getId() != null && getId().equals(match.getId());
    }

    @Override
    public int hashCode() {
        return 31 * (id != null ? id.hashCode() : 0);
    }
}