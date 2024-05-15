
CREATE TABLE Users (
                       id INT IDENTITY PRIMARY KEY,
                       username VARCHAR(255) NOT NULL,
                       password VARCHAR(255) NOT NULL,
)

CREATE TABLE Matches(
                        id INT IDENTITY PRIMARY KEY,
                        team_a VARCHAR(255) NOT NULL,
                        team_b VARCHAR(255) NOT NULL,
                        match_type VARCHAR(255) NOT NULL,
)

CREATE TABLE Clients (
                         id INT IDENTITY PRIMARY KEY,
                         name VARCHAR(255) NOT NULL,
                         nr_of_tickets INT NOT NULL
)

CREATE TABLE Tickets (
                         id INT IDENTITY PRIMARY KEY,
                         price FLOAT,
                         available_seats INT NOT NULL,
                         match_id INT NOT NULL,
                         FOREIGN KEY (match_id) REFERENCES Matches(id)
)

drop table Tickets

CREATE TABLE ClientTickets (
                               id INT IDENTITY PRIMARY KEY,
                               ticket_id INT NOT NULL,
                               client_id INT NOT NULL,
                               FOREIGN KEY (ticket_id) REFERENCES Tickets(id),
                               FOREIGN KEY (client_id) REFERENCES Clients(id)
)

-- drop table Users
-- drop table Tickets_Matches