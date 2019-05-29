package win.simple.lchat;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

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

import win.simple.lchat.Enum.MessageSender;
import win.simple.lchat.Util.CommunicationClient;
import win.simple.lchat.Util.TuringRobot;
import win.simple.lchat.Util.Variable;

public class ChatInteface extends AppCompatActivity {

    /*
    *
    *   聊天界面
    *
    * */

    CommunicationClient communicationClient = CommunicationClient.getCommunicationClient();     //获取通讯客户端

    ScrollView InfoScrollView = null;
    LinearLayout InfoLayout = null;
    EditText ConetxtEditText = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_inteface);

        this.setTitle("(" + Variable.Username + ") 群聊在线人数:[" + 0 + "]");

        ImageView imageView = (ImageView) findViewById(R.id.SendButton);
        ConetxtEditText = (EditText) findViewById(R.id.ConetxtEditText);
        InfoScrollView = (ScrollView) findViewById(R.id.InfoScrollView);

        Handler RobotHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                try {
                    String RobotContent = new JSONObject((String)msg.obj).getJSONArray("results").getJSONObject(0).getJSONObject("values").getString("text");
                    addNews("小艾机器人", RobotContent, MessageSender.Others);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        final TuringRobot turingRobot = TuringRobot.getTuringRobot();
        turingRobot.setApiKey("3ac9032a08ac4128a7448fd30c31765a");
        turingRobot.setUserId("284789");
        turingRobot.setRobotHandler(RobotHandler);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Content =  ConetxtEditText.getText().toString();
                if(!Content.equals("")) {
                    addNews(Variable.Username, Content, MessageSender.Own);
                    if(Content.indexOf("@小艾") != -1) {
                        turingRobot.SendMessage(Content);
                    } else {
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("Type", 2);
                            jsonObject.put("username", Variable.Username);
                            jsonObject.put("Messages", Content);
                            communicationClient.sendMessage(jsonObject.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    ConetxtEditText.setText("");
                }
            }
        });

        Handler CommunicationMessageHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                try {
                    HandlerMassage(new JSONObject((String)msg.obj));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        };

        communicationClient.setHandler(CommunicationMessageHandler);
    }

    public void HandlerMassage(JSONObject jsonObject) throws JSONException {
        switch (jsonObject.getInt("Type")) {
            case 2 :
                Messages(jsonObject);
                break;
            case 1 :
                People(jsonObject);
                break;
        }
    }

    public void People(JSONObject jsonObject) {
        try {
            this.setTitle("(" + Variable.Username + ")  群聊在线人数:[" + jsonObject.getInt("People") + "]");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void Messages(JSONObject jsonObject) {
        try {
            String Messages = jsonObject.getString("Messages");
            String UserName = jsonObject.getString("username");

            if(!UserName.equals(Variable.Username)) {
                addNews(UserName, Messages, MessageSender.Others);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void addNews(String username, String content, MessageSender messageSender) {     //添加聊天信息
        if(InfoLayout == null) {
            InfoLayout = (LinearLayout) findViewById(R.id.InfoLayout);
        }

        LinearLayout NewsLayout = new LinearLayout(this);
        NewsLayout.setOrientation(LinearLayout.VERTICAL);
        InfoLayout.addView(NewsLayout);
        LinearLayout.LayoutParams NewsLayoutParams = (LinearLayout.LayoutParams) NewsLayout.getLayoutParams();
        NewsLayoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
        NewsLayoutParams.bottomMargin = 30;
        NewsLayout.setLayoutParams(NewsLayoutParams);

        TextView UserNameTextView = new TextView(this);
        UserNameTextView.setTextSize(15);
        UserNameTextView.setTextColor(Color.WHITE);
        UserNameTextView.setText(username);
        NewsLayout.addView(UserNameTextView);

        LinearLayout NewsLinearLayout = new LinearLayout(this);
        LinearLayout.LayoutParams NewsLinearLayoutParams = (LinearLayout.LayoutParams) NewsLayout.getLayoutParams();
        NewsLinearLayoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;

        TextView NewsTextView = new TextView(this);
        NewsTextView.setAllCaps(false);
        NewsTextView.setPadding(20, 20, 20, 20);
        NewsTextView.setText(content);
        ViewGroup.LayoutParams NewsButtonParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        NewsTextView.setLayoutParams(NewsButtonParams);

        if(messageSender.equals(MessageSender.Others)) {
            NewsLayoutParams.gravity = Gravity.LEFT;
            NewsLayoutParams.leftMargin = 30;
            UserNameTextView.setGravity(Gravity.LEFT);
            NewsTextView.setTextColor(Color.BLACK);
            NewsLinearLayout.setBackground(getResources().getDrawable(R.drawable.othersbackground));
        } else {
            NewsLayoutParams.gravity = Gravity.RIGHT;
            NewsLayoutParams.rightMargin = 30;
            UserNameTextView.setGravity(Gravity.RIGHT);
            NewsTextView.setTextColor(Color.WHITE);
            NewsLinearLayout.setBackground(getResources().getDrawable(R.drawable.blackground));
        }
        NewsLinearLayout.setLayoutParams(NewsLinearLayoutParams);

        NewsLinearLayout.addView(NewsTextView);
        NewsLayout.addView(NewsLinearLayout);
        InfoScrollView.fullScroll(ScrollView.FOCUS_DOWN);
    }
}
