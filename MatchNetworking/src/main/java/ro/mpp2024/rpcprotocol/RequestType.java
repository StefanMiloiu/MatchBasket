package ro.mpp2024.rpcprotocol;

public enum RequestType {
    LOGIN,
    ADD_CLIENT,
    ADD_CLIENT_TICKET,
    FIND_ALL_WITH_AVAILABLE_SEATS,
    FIND_ONE_TICKET_BY_MATCH,
//    UPDATE_PARTICIPANT,
//    ADD_REZULTAT,
//    FIND_ALL_REZULTATE,
    FIND_ACCOUNT,

    FIND_ALL,
    UPDATE_TICKET,
    LOGOUT
}