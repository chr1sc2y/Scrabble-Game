package com.company;

import com.network.Message;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        int port = 55555;

        Server server = new Server(port);
        server.Start();
    }
}
