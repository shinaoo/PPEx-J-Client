package proto.tpool;

import io.netty.util.Timeout;
import ppex.proto.tpool.ITask;

import java.util.concurrent.atomic.AtomicInteger;

public class TaskTest implements ITask {

    private AtomicInteger num = new AtomicInteger(1);
    @Override
    public void execute() {
        System.out.println("this is TaskTest execute :" + num.getAndIncrement());
    }

    @Override
    public void run(Timeout timeout) throws Exception {
        execute();
    }

    @Override
    public void run() {
        execute();
    }
}
