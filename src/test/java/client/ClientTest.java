package client;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ppex.client.Client;

import java.util.concurrent.TimeUnit;

public class ClientTest {
    Client client;

    @Before
    public void setup() {
        client = new Client();
    }

    @After
    public void finish() {
        client = null;
    }

    @Test
    public void startTest() throws Exception {
        client.sendTest();
        TimeUnit.SECONDS.sleep(2);
    }
}
