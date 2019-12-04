package proto.tpool;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ppex.proto.tpool.ITask;
import ppex.proto.tpool.IThreadExecute;
import ppex.proto.tpool.ThreadExecute;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class ExecutorTest {

    IThreadExecute executor;

    @Before
    public void setup(){
        executor = new ThreadExecute();
        executor.start();
    }

    @After
    public void finish(){
        executor.stop();
    }

    @Test
    public void startTask(){
        ITask task = new TaskTest();
        executor.execute(task);
        IntStream.range(0,10).forEach(val -> executor.executeTimerTask(task,100));
        executor.executeTimerTask(task,100);
        executor.executeTimerTask(task,1000);
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
