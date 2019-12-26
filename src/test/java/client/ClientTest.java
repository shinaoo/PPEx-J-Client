package client;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ppex.client.Client;
import ppex.client.process.DetectProcess;
import ppex.proto.msg.type.PingTypeMsg;
import ppex.proto.msg.type.ProbeTypeMsg;
import ppex.utils.NatTypeUtil;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class ClientTest {
    Client client;

    @Before
    public void setup() throws Exception {
        client = Client.getInstance();
//        client.start();
        client.startTestClient();
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
        System.out.println("nattype:" + NatTypeUtil.NatType.getByValue(detectProcess.getClientNATType().getValue()));
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

    @Test
    public void startDetectServer12S2P2() throws Exception {

    }

    @Test
    public void startMultiMsgTest() throws Exception {
//        IntStream.range(0,100).forEach(val -> client.sendPingTypeMsg());
    }

    @Test
    public void multiThreadTest() throws Exception {
        //todo 多线程测试未通过
        IntStream.range(0, 50).forEach(val -> new Thread(() -> client.sendTest()).start());
        TimeUnit.SECONDS.sleep(20);
    }

    @Test
    public void Rudp2Test() throws Exception {
        IntStream.range(0, 1000).forEach(val -> client.sendTestRudp2());
//        client.sendTestRudp2();
        TimeUnit.SECONDS.sleep(2);
    }

}
