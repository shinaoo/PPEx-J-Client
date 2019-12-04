package ppex.client.rudp;

import ppex.proto.msg.Message;
import ppex.proto.rudp.ResponseListener;
import ppex.proto.rudp.RudpPack;

public class MsgResponse implements ResponseListener {
    @Override
    public void onResponse(RudpPack rudpPack, Message message) {

    }
}
