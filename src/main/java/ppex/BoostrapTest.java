package ppex;

import ppex.client.Client;

public class BoostrapTest {
    public static void main(String[] args){
        Client client = Client.getInstance();
        client.startTestClient();
    }
}
