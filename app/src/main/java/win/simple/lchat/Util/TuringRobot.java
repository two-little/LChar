package win.simple.lchat.Util;

import android.os.Handler;
import android.os.Message;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class TuringRobot {

    private URL RobotURL = null;
    private HttpURLConnection RobotHttpURLConnection = null;
    private BufferedWriter RobotBufferedWriter = null;
    private BufferedReader RobotBufferedReader = null;

    private static TuringRobot turingRobot = null;
    private Handler RobotHandler = null;
    private String APIKEY = "";
    private String USERID = "";

    private TuringRobot() {

    }

    public static TuringRobot getTuringRobot() {
        if(turingRobot == null) {
            turingRobot = new TuringRobot();
        }
        return turingRobot;
    }

    public void SendMessage(final String Content) {
        try {
            RobotURL = new URL("http://openapi.tuling123.com/openapi/api/v2");
            RobotHttpURLConnection = (HttpURLConnection) RobotURL.openConnection();
            RobotHttpURLConnection.setRequestMethod("POST");
            RobotHttpURLConnection.setRequestProperty("Content-Type", "application/json");

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        RobotBufferedWriter = new BufferedWriter(new OutputStreamWriter(RobotHttpURLConnection.getOutputStream(), "utf-8"));

                        JSONObject RobotRequestJson = new JSONObject();
                        RobotRequestJson.put("reqType", 0);
                        JSONObject perceptionJson = new JSONObject();
                        JSONObject inputTextJson = new JSONObject();
                        inputTextJson.put("text", Content);
                        perceptionJson.put("inputText", inputTextJson);
                        RobotRequestJson.put("perception", perceptionJson);
                        JSONObject userInfoJson = new JSONObject();
                        userInfoJson.put("apiKey", APIKEY);
                        userInfoJson.put("userId", USERID);
                        RobotRequestJson.put("userInfo", userInfoJson);
                        RobotBufferedWriter.write(RobotRequestJson.toString() + "\r\n");
                        RobotBufferedWriter.flush();
                        RobotBufferedWriter.close();

                        RobotBufferedReader = new BufferedReader(new InputStreamReader(RobotHttpURLConnection.getInputStream()));
                        StringBuffer stringBuffer = new StringBuffer();
                        String line = "";
                        while((line = RobotBufferedReader.readLine()) != null) {
                            stringBuffer.append(line);
                        }

                        Message message = new Message();
                        message.obj = stringBuffer.toString();
                        RobotHandler.sendMessage(message);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setApiKey(String APIKEY) {
        this.APIKEY = APIKEY;
    }

    public String getApiKey() {
        return APIKEY;
    }

    public void setUserId(String USERID) {
        this.USERID = USERID;
    }

    public String getUserId() {
        return USERID;
    }

    public void setRobotHandler(Handler robotHandler) {
        RobotHandler = robotHandler;
    }

    public Handler getRobotHandler() {
        return RobotHandler;
    }
}
