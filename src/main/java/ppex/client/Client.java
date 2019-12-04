package ppex.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import ppex.client.rudp.ClientAddrManager;
import ppex.client.rudp.ClientOutput;
import ppex.client.rudp.ClientOutputManager;
import ppex.client.rudp.MsgResponse;
import ppex.proto.entity.Connection;
import ppex.proto.msg.type.TxtTypeMsg;
import ppex.proto.rudp.*;
import ppex.proto.tpool.IThreadExecute;
import ppex.proto.tpool.ThreadExecute;
import ppex.utils.MessageUtil;

import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class Client {

    private String HOST_SERVER1 = "10.5.11.55";
    private String HOST_SERVER2 = "127.0.0.1";
    private int PORT_1 = 9123;
    private int PORT_2 = 9124;
    private int PORT_3 = 9125;

    private InetSocketAddress addrLocal;
    private InetSocketAddress addrServer1;
    private InetSocketAddress addrServer2p1;
    private InetSocketAddress addrServer2p2;


    private String addrMac;
    private String name;
    private Connection connServer1;
    private Connection connServer2p1;
    private Connection connServer2p2;

    private IAddrManager addrManager;
    private IOutputManager outputManager;
    private IThreadExecute executor;
    private ResponseListener responseListener;

    private Channel channel;
    private Bootstrap bootstrap;
    private EventLoopGroup eventLoopGroup;
    private ClientHandler clientHandler;

    public Client(){
        try {
            start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void start() throws Exception {
        initParam();
        startBootstrap();
        initRudp();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> stop()));
    }

    private void initParam() {

        name = "Client1";
        addrMac = getMacAddress();

        addrServer1 = new InetSocketAddress(HOST_SERVER1, PORT_1);
        addrServer2p1 = new InetSocketAddress(HOST_SERVER2, PORT_1);
        addrServer2p2 = new InetSocketAddress(HOST_SERVER2, PORT_2);

        addrManager = new ClientAddrManager();
        executor = new ThreadExecute();
        outputManager = new ClientOutputManager();
        executor.start();
        clientHandler = new ClientHandler(this);
        responseListener = new MsgResponse();
    }

    private void startBootstrap() throws Exception {
        bootstrap = new Bootstrap();
        eventLoopGroup = new NioEventLoopGroup(2);
        bootstrap.channel(NioDatagramChannel.class).group(eventLoopGroup);
        bootstrap.option(ChannelOption.SO_BROADCAST, true).option(ChannelOption.SO_REUSEADDR, true)
                .option(ChannelOption.SO_RCVBUF, Rudp.MTU_DEFUALT);

        bootstrap.handler(clientHandler);
        channel = bootstrap.bind(PORT_3).sync().channel();
    }

    private void initRudp() {
        //默认的server1,server2p1,server2p2的Connection
        connServer1 = new Connection("unknown", new InetSocketAddress(HOST_SERVER1, PORT_1), "Server1", 0);
        //output需要Channel
        IOutput outputServer1 = new ClientOutput(channel, connServer1);
        outputManager.put(addrServer1,outputServer1);
        RudpPack rudpPack = addrManager.get(addrServer1);
        if (rudpPack == null) {
            rudpPack = new RudpPack(outputServer1, executor, responseListener);
            addrManager.New(addrServer1, rudpPack);
        }
        rudpPack.sendReset();
        RudpScheduleTask task = new RudpScheduleTask(executor, rudpPack, addrManager);
        executor.executeTimerTask(task, rudpPack.getInterval());
    }

    private void stop() {
        if (executor != null){
            executor.stop();
        }
        if (eventLoopGroup != null) {
            eventLoopGroup.shutdownGracefully();
        }
    }

    private String getMacAddress() {
        try {
            Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            byte[] mac = null;
            while (allNetInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
                if (netInterface.isLoopback() || netInterface.isVirtual() || netInterface.isPointToPoint() || !netInterface.isUp()) {
                    continue;
                } else {
                    mac = netInterface.getHardwareAddress();
                    if (mac != null) {
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < mac.length; i++) {
                            sb.append(String.format("%02X", mac[i]));
                        }
                        if (sb.length() > 0) {
                            return sb.toString();
                        }
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
            return "";
        }
        return "";
    }

    public void sendTest(){
        TxtTypeMsg msg = new TxtTypeMsg();
        msg.setContent("this is from client");
        msg.setFrom(new InetSocketAddress("127.0.0.1",PORT_3));
        msg.setTo(new InetSocketAddress("127.0.0.1",PORT_1));
        msg.setReq(true);
        this.getAddrManager().get(addrServer1).write(MessageUtil.txtmsg2Msg(msg));
    }

    public IAddrManager getAddrManager() {
        return addrManager;
    }

    public IOutputManager getOutputManager() {
        return outputManager;
    }

    public IThreadExecute getExecutor() {
        return executor;
    }

    public ResponseListener getResponseListener() {
        return responseListener;
    }

    public InetSocketAddress getAddrServer1() {
        return addrServer1;
    }

    public InetSocketAddress getAddrServer2p1() {
        return addrServer2p1;
    }

    public InetSocketAddress getAddrServer2p2() {
        return addrServer2p2;
    }

    public InetSocketAddress getAddrLocal() {
        return addrLocal;
    }

    public int getPORT_1() {
        return PORT_1;
    }

    public int getPORT_2() {
        return PORT_2;
    }

    public int getPORT_3() {
        return PORT_3;
    }
}
