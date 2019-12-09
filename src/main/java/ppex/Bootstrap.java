package ppex;

import ppex.client.Client;

public class Bootstrap {

    public static void main(String[] args) throws Exception {
        Client client = Client.getInstance();
        client.start();
    }
}
