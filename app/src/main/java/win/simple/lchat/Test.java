package win.simple.lchat;

import android.os.Handler;
import android.os.Message;

public class Test {

    private static Test test = new Test();
    private Handler handler = null;

    private Test() {
        cs();
    }

    public static Test getTest() {
        return test;
    }

    public void handler(Handler handler) {
        this.handler = handler;
    }

    public void cs() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    try {
                        Message message = new Message();
                        message.obj = 1;
                        handler.sendMessage(message);
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}
