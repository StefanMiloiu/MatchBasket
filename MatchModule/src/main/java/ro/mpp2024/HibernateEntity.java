package ro.mpp2024;

public interface HibernateEntity<ID> {
    void setId(ID id);
    ID getId();
}

