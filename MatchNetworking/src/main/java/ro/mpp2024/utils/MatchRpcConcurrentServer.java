package ro.mpp2024.utils;


import ro.mpp2024.IMatchServices;
import ro.mpp2024.rpcprotocol.MatchClientRpcReflectionWorker;

import java.net.Socket;


public class MatchRpcConcurrentServer extends AbsConcurrentServer {
    private IMatchServices triatlonServer;
    public MatchRpcConcurrentServer(int port, IMatchServices triatlonServer) {
        super(port);
        this.triatlonServer = triatlonServer;
        System.out.println("Chat- ChatRpcConcurrentServer");
    }

    @Override
    protected Thread createWorker(Socket client) {
       // ChatClientRpcWorker worker=new ChatClientRpcWorker(chatServer, client);
        MatchClientRpcReflectionWorker worker=new MatchClientRpcReflectionWorker(triatlonServer, client);

        Thread tw=new Thread(worker);
        return tw;
    }

    @Override
    public void stop(){
        System.out.println("Stopping services ...");
    }
}
