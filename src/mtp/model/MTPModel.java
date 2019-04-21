package mtp.model;

import java.io.*;

public class MTPModel {
    private Client client;
    private Server server;
    private String clientIP;
    private int port;

    public MTPModel() {
        clientIP = "127.0.0.1";
        port = 1234;
    }

    // Creates a server with its current IP and port settings
    public void createClient() {
        client = new Client();
    }

    // Creates a server with its current port settings
    public void createServer() throws IOException {
        server = new Server(port);
    }

    /** GETTERS AND SETTERS **/
    public void setClientIP(String clientIP) {
        this.clientIP = clientIP;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Client getClient() {
        return client;
    }

    public Server getServer() {
        return server;
    }

    public String getClientIP() {
        return clientIP;
    }

    public int getPort() {
        return port;
    }



}
