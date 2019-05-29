package win.simple.lchat.Util;

import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class CommunicationClient {

    private static CommunicationClient communicationClient = null;

    private Socket CommunicationSocket = null;
    private BufferedWriter bufferedWriter = null;
    private BufferedReader bufferedReader = null;

    private Handler handler = null;
    private static String Address = null;
    private static int Port = 0;
    private boolean isConnection = false;

    private CommunicationClient() {

    }

    public static CommunicationClient getCommunicationClient() {
        if(communicationClient == null) {
            communicationClient = new CommunicationClient();
        }
        return communicationClient;
    }

    public void ConnectionServer(final String address, final int port) throws Exception{
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    CommunicationSocket = new Socket();
                    CommunicationSocket.connect(new InetSocketAddress(address, port), 2000);
                    bufferedWriter = new BufferedWriter(new OutputStreamWriter(CommunicationSocket.getOutputStream()));
                    bufferedReader = new BufferedReader(new InputStreamReader(CommunicationSocket.getInputStream()));

                    new Thread(new GetCommunicationMessageThread()).start();
                    isConnection = true;
                } catch(SocketTimeoutException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
        thread.start();
        thread.join();

        if(!isConnection) {
            throw new Exception("无法连接至服务器");
        }
    }

    public void setAddress(String address) {
        this.Address = address;
    }

    public void setPort(int port) {
        this.Port = port;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public void sendMessage(final String Content) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    bufferedWriter.write(Content);
                    bufferedWriter.write("\r\n");
                    bufferedWriter.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    class GetCommunicationMessageThread implements Runnable {

        @Override
        public void run() {
            String line = "";
            try {
                while((line = bufferedReader.readLine()) != null) {
                    if(handler != null) {
                        Message message = new Message();
                        message.obj = (String) line;
                        handler.sendMessage(message);
                    }
                }
            } catch (SocketException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
