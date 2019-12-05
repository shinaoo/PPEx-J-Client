package ppex.client.process;

import com.alibaba.fastjson.JSON;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import ppex.client.Client;
import ppex.proto.entity.Connection;
import ppex.proto.entity.through.Connect;
import ppex.proto.entity.through.ConnectMap;
import ppex.proto.msg.type.ThroughTypeMsg;
import ppex.proto.rudp.IAddrManager;
import ppex.proto.rudp.RudpPack;
import ppex.utils.MessageUtil;

import java.util.ArrayList;
import java.util.List;

public class ThroughProcess {

    private static String TAG = ThroughProcess.class.getName();
    private Client client;

    public ThroughProcess(Client client) {
        this.client = client;
    }

    public void sendSaveInfo() {
        try {
            ThroughTypeMsg throughTypeMsg = new ThroughTypeMsg();
            throughTypeMsg.setAction(ThroughTypeMsg.ACTION.SAVE_CONNINFO.ordinal());
            Client.getInstance().localConnection.setAddress(Client.getInstance().address);
            Client.getInstance().localConnection.setPeerName("Client1");
            throughTypeMsg.setContent(JSON.toJSONString(Client.getInstance().localConnection));
//            this.channel.writeAndFlush(MessageUtil.throughmsg2Packet(throughTypeMsg, Client.getInstance().SERVER1));
            RudpPack rudpPack = addrManager.get(Client.getInstance().SERVER1);
            rudpPack.write(MessageUtil.throughmsg2Msg(throughTypeMsg));
            if (!channel.closeFuture().await(2000)) {
                System.out.println("查询超时");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getConnectionsFromServer(Channel ch) {
        try {
            ThroughTypeMsg throughTypeMsg = new ThroughTypeMsg();
            throughTypeMsg.setAction(ThroughTypeMsg.ACTION.GET_CONNINFO.ordinal());
            throughTypeMsg.setContent("");
//            ch.writeAndFlush(MessageUtil.throughmsg2Packet(throughTypeMsg, Client.getInstance().SERVER1));
            RudpPack rudpPack = addrManager.get(Client.getInstance().SERVER1);
            rudpPack.write(MessageUtil.throughmsg2Msg(throughTypeMsg));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getConnectionsFromServer(ChannelHandlerContext ctx) {
        try {
            ThroughTypeMsg throughTypeMsg = new ThroughTypeMsg();
            throughTypeMsg.setAction(ThroughTypeMsg.ACTION.GET_CONNINFO.ordinal());
            throughTypeMsg.setContent("");
            ctx.writeAndFlush(MessageUtil.throughmsg2Packet(throughTypeMsg, Client.getInstance().SERVER1));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void connectPeer(Channel channel, Connection connection, IAddrManager addrManager) {
        try {
//            ThroughTypeMsg throughTypeMsg = new ThroughTypeMsg();
//            throughTypeMsg.setAction(ThroughTypeMsg.ACTION.CONNECT_CONN.ordinal());
//            Connect connect = new Connect();
////            connect.setType(Connect.TYPE.REQUEST_CONNECT_SERVER.ordinal());
//            List<Connection> connections = new ArrayList<>();
//            connections.add(Client.getInstance().localConnection);
//            connections.add(connection);
//            connect.setContent(JSON.toJSONString(connections));
//            throughTypeMsg.setContent(JSON.toJSONString(connect));
//            channel.writeAndFlush(MessageUtil.throughmsg2Packet(throughTypeMsg, Client.getInstance().SERVER1));
            ThroughTypeMsg throughTypeMsg = new ThroughTypeMsg();
            throughTypeMsg.setAction(ThroughTypeMsg.ACTION.CONNECT_CONN.ordinal());
            Connect.TYPE connectType = Client.judgeConnectType(Client.getInstance().localConnection, connection);
            Connect connect = new Connect();

            List<Connection> connections = new ArrayList<>();
            connections.add(Client.getInstance().localConnection);
            connections.add(connection);
            String connectionsStr = JSON.toJSONString(connections);
            //将建立连接的两边保存,保存在进行中的map中
            ConnectMap connectMap = new ConnectMap(connectType.ordinal(), connections);
            Client.getInstance().connectingMaps.add(connectMap);

            //Rudppack
            RudpPack rudpPack;

            if (connectType == Connect.TYPE.DIRECT) {
                connect.setType(Connect.TYPE.CONNECT_PING.ordinal());
                connect.setContent("");
                throughTypeMsg.setContent(JSON.toJSONString(connect));
                //等待返回pong就确认建立连接
//                channel.writeAndFlush(MessageUtil.throughmsg2Packet(throughTypeMsg, connection.getAddress()));
                rudpPack = addrManager.get(connection.getAddress());
                if (rudpPack == null){
                    DisruptorExectorPool disruptorExectorPool = new DisruptorExectorPool();
                    disruptorExectorPool.createDisruptorProcessor("1");
                    IMessageExecutor executor = disruptorExectorPool.getAutoDisruptorProcessor();
//                    Connection con = new Connection("", connections.get(0).getAddress(), "other", Constants.NATTYPE.SYMMETIC_NAT.ordinal(), ctx.channel());
                    connection.setChannel(channel);
                    Output output = new ClientOutput();
                    rudpPack = new RudpPack(output, connection, executor, null,null);
                    addrManager.New(connection.getAddress(), rudpPack);
                }
                rudpPack.write(MessageUtil.throughmsg2Msg(throughTypeMsg));

                //发送给Server端，表明正在建立连接
                connect.setType(Connect.TYPE.CONNECTING.ordinal());
                connect.setContent(connectionsStr);
                throughTypeMsg.setContent(JSON.toJSONString(connect));
//                channel.writeAndFlush(MessageUtil.throughmsg2Packet(throughTypeMsg, Client.getInstance().SERVER1));
                rudpPack = addrManager.get(Client.getInstance().SERVER1);
                rudpPack.write(MessageUtil.throughmsg2Msg(throughTypeMsg));
            } else if (connectType == Connect.TYPE.HOLE_PUNCH) {
                connect.setType(connectType.ordinal());
                connect.setContent(connectionsStr);
                throughTypeMsg.setContent(JSON.toJSONString(connect));
                //先将消息发给服务，由服务转发给target connection打洞
//                channel.writeAndFlush(MessageUtil.throughmsg2Packet(throughTypeMsg, Client.getInstance().SERVER1));
                rudpPack = addrManager.get(Client.getInstance().SERVER1);
                rudpPack.write(MessageUtil.throughmsg2Msg(throughTypeMsg));

                connect.setType(Connect.TYPE.CONNECTING.ordinal());
                throughTypeMsg.setContent(JSON.toJSONString(connect));
//                channel.writeAndFlush(MessageUtil.throughmsg2Packet(throughTypeMsg, Client.getInstance().SERVER1));
                rudpPack = addrManager.get(Client.getInstance().SERVER1);
                rudpPack.write(MessageUtil.throughmsg2Msg(throughTypeMsg));
            } else if (connectType == Connect.TYPE.REVERSE) {
                //首先向B 打洞
                connect.setType(Connect.TYPE.CONNECT_PING.ordinal());
                connect.setContent(connectionsStr);
                throughTypeMsg.setContent(JSON.toJSONString(connect));
                //率先打洞
//                channel.writeAndFlush(MessageUtil.throughmsg2Packet(throughTypeMsg, connection.getAddress()));
                rudpPack = addrManager.get(connection.getAddress());
                if (rudpPack == null){
                    DisruptorExectorPool disruptorExectorPool = new DisruptorExectorPool();
                    disruptorExectorPool.createDisruptorProcessor("1");
                    IMessageExecutor executor = disruptorExectorPool.getAutoDisruptorProcessor();
//                    Connection con = new Connection("", connections.get(0).getAddress(), "other", Constants.NATTYPE.SYMMETIC_NAT.ordinal(), ctx.channel());
                    connection.setChannel(channel);
                    Output output = new ClientOutput();
                    rudpPack = new RudpPack(output, connection, executor, null,null);
                    addrManager.New(connection.getAddress(), rudpPack);
                }
                rudpPack.write(MessageUtil.throughmsg2Msg(throughTypeMsg));

                //让server给B转发，由B 再通信
                connect.setType(connectType.ordinal());
                connect.setContent(connectionsStr);
                throughTypeMsg.setContent(JSON.toJSONString(connect));
//                channel.writeAndFlush(MessageUtil.throughmsg2Packet(throughTypeMsg, Client.getInstance().SERVER1));
                rudpPack = addrManager.get(Client.getInstance().SERVER1);
                rudpPack.write(MessageUtil.throughmsg2Msg(throughTypeMsg));

                connect.setType(Connect.TYPE.CONNECTING.ordinal());
                connect.setContent(connectionsStr);
                throughTypeMsg.setContent(JSON.toJSONString(connect));
//                channel.writeAndFlush(MessageUtil.throughmsg2Packet(throughTypeMsg, Client.getInstance().SERVER1));
                rudpPack = addrManager.get(Client.getInstance().SERVER1);
                rudpPack.write(MessageUtil.throughmsg2Msg(throughTypeMsg));
            } else if (connectType == Connect.TYPE.FORWARD) {
                connect.setType(Connect.TYPE.FORWARD.ordinal());
                connect.setContent(connectionsStr);
                throughTypeMsg.setContent(JSON.toJSONString(connect));
//                channel.writeAndFlush(MessageUtil.throughmsg2Packet(throughTypeMsg, Client.getInstance().SERVER1));
                rudpPack = addrManager.get(Client.getInstance().SERVER1);
                rudpPack.write(MessageUtil.throughmsg2Msg(throughTypeMsg));

                connect.setType(Connect.TYPE.CONNECTING.ordinal());
                throughTypeMsg.setContent(JSON.toJSONString(connect));
//                channel.writeAndFlush(MessageUtil.throughmsg2Packet(throughTypeMsg, Client.getInstance().SERVER1));
                rudpPack = addrManager.get(Client.getInstance().SERVER1);
                rudpPack.write(MessageUtil.throughmsg2Msg(throughTypeMsg));
            } else {
                throw new Exception("unknown connect operate:" + connectionsStr);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void testConnectPeer() {
        try {

        } catch (Exception e) {

        }
    }


}
