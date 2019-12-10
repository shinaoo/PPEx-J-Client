package client;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ppex.client.Client;
import ppex.client.process.DetectProcess;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class ClientTest {
    Client client;

    @Before
    public void setup() throws Exception {
        client = Client.getInstance();
        client.start();
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

    @Test
    public void startDetect() throws Exception {
        DetectProcess detectProcess = DetectProcess.getInstance();
        detectProcess.setClient(client);
        detectProcess.startDetect();
        TimeUnit.SECONDS.sleep(2);
//        detectProcess.startDetect();
        TimeUnit.SECONDS.sleep(2);
    }

    @Test
    public void startDetect2S1() throws Exception {
        DetectProcess detectProcess = DetectProcess.getInstance();
        detectProcess.setClient(client);
        detectProcess.send2S1();
        TimeUnit.SECONDS.sleep(2);
    }

    @Test
    public void startDetect2S2P1() throws Exception {
        DetectProcess detectProcess = DetectProcess.getInstance();
        detectProcess.setClient(client);
        detectProcess.send2S2P1();
        TimeUnit.SECONDS.sleep(3);
    }

    public void startDetect2S2P2() throws Exception {
        //模拟s1或者S2P1给S2p2发送信息

    }

    @Test
    public void multiThreadTest() throws Exception {
        //todo 多线程测试未通过
        IntStream.range(0, 50).forEach(val -> new Thread(() -> client.sendTest()).start());
        TimeUnit.SECONDS.sleep(20);
    }
}
