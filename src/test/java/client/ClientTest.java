package client;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ppex.client.Client;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

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

    public void startDetect() throws Exception {

    }

    @Test
    public void multiThreadTest() throws Exception {
        //todo 多线程测试未通过
        IntStream.range(0, 50).forEach(val -> new Thread(()-> client.sendTest()).start());
        TimeUnit.SECONDS.sleep(20);
    }
}
