package ppex.proto.rudp2;

import io.netty.util.Timeout;
import ppex.proto.msg.Message;
import ppex.proto.rudp.RudpPack;
import ppex.proto.tpool.ITask;

import java.util.concurrent.ConcurrentLinkedQueue;

public class SndTask implements ITask {

    private RudpPack rpkg;
    private String name;

    public static SndTask New(RudpPack rpkg,String name){
        SndTask sndTask = new SndTask();
        sndTask.rpkg = rpkg;
        sndTask.name = name;
        return sndTask;
    }

    @Override
    public void execute() {
        try {
            ConcurrentLinkedQueue<Message> msgs = rpkg.getQueue_snd();
            while(!msgs.isEmpty()){
                Message msg = msgs.poll();
                if (msg == null)
                    continue;
                this.rpkg.send2(msg);
            }
            long timeCur = System.currentTimeMillis();
            this.rpkg.flush2(timeCur);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run(Timeout timeout) throws Exception {
        run();
    }

    @Override
    public void run() {
        execute();
    }
}