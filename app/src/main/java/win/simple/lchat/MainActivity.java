package win.simple.lchat;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import win.simple.lchat.Util.CommunicationClient;
import win.simple.lchat.Util.Variable;

public class MainActivity extends AppCompatActivity {

    /**
     *
     *  登录界面
     *
     */


    CommunicationClient communicationClient = CommunicationClient.getCommunicationClient();     //获取通讯客户端

    Handler CommunicationMessageHandler = null;
    EditText UsernameEditText = null;
    EditText PasswordEditText = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.setTitle("LChat - 登录");

        UsernameEditText = (EditText) findViewById(R.id.UserNameEditText);
        PasswordEditText = (EditText) findViewById(R.id.PassWordEditText);
        ImageView ImageLoginButton = (ImageView) findViewById(R.id.LoginButton);
        TextView RegisterTextView = (TextView) findViewById(R.id.RegisterTextView);

        RegisterTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent RegisterIntefaceIntent = new Intent(MainActivity.this, register.class);
                startActivity(RegisterIntefaceIntent);
            }
        });

        ImageLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Username = UsernameEditText.getText().toString();
                String Password = PasswordEditText.getText().toString();

                if(!Username.equals("") && !Password.equals("")) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("Type", 3);
                        jsonObject.put("account_number", UsernameEditText.getText());
                        jsonObject.put("Password", PasswordEditText.getText());

                        communicationClient.sendMessage(jsonObject.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    Toast.makeText(MainActivity.this, "账号或密码不能为空", Toast.LENGTH_SHORT).show();
                }
            }

        });

        CommunicationMessageHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                try {
                    HandlerMassage(new JSONObject((String) msg.obj));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        };

        try {
            communicationClient.setHandler(CommunicationMessageHandler);
            //communicationClient.ConnectionServer("192.168.1.105", 2551);        //连接到通讯服务端
            communicationClient.ConnectionServer("10mc.win", 2551);        //连接到通讯服务端
        } catch (Exception e) {
            Toast.makeText(this, "无法连接至服务器", Toast.LENGTH_SHORT).show();
        }

    }

    public void HandlerMassage(JSONObject jsonObject) throws JSONException {
        switch (jsonObject.getInt("Type")) {
            case 4 :
                Login(jsonObject);
                break;
        }
    }

    public void Login(JSONObject jsonObject) {
        try {
            if(jsonObject.getBoolean("state")) {
                Variable.Username = UsernameEditText.getText().toString();
                Intent ChatIntefaceIntent = new Intent(MainActivity.this, ChatInteface.class);
                MainActivity.this.startActivity(ChatIntefaceIntent);
                MainActivity.this.finish();
            } else {
                Toast.makeText(this, "登录失败", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onStart() {
        if(CommunicationMessageHandler != null) {
            communicationClient.setHandler(CommunicationMessageHandler);
        }
        super.onStart();
    }
}
