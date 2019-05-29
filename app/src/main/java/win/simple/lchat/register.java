package win.simple.lchat;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import win.simple.lchat.Util.CommunicationClient;

public class register extends AppCompatActivity {

    /**
     *
     *  注册界面
     *
     */

    CommunicationClient communicationClient = CommunicationClient.getCommunicationClient();     //获取通讯客户端

    EditText UserNameEditText = null;
    EditText PassWordEditText = null;
    EditText RepeatPassWordEditText = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        this.setTitle("LChat - 注册");

        UserNameEditText = (EditText) findViewById(R.id.UserNameEditText);
        PassWordEditText = (EditText) findViewById(R.id.PassWordEditText);
        RepeatPassWordEditText = (EditText) findViewById(R.id.RepeatPassWordEditText);

        TextView LoginTextView = (TextView) findViewById(R.id.LoginTextView);
        ImageView RegisterButton = (ImageView) findViewById(R.id.RegisterButton);

        LoginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        RegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Username = UserNameEditText.getText().toString();
                String PassWord = PassWordEditText.getText().toString();
                String RepeatPassWord = RepeatPassWordEditText.getText().toString();

                if(!Username.equals("") && !PassWord.equals("") && !RepeatPassWord.equals("")) {
                    if(RepeatPassWord.equals(PassWord)) {
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("Type", 5);
                            jsonObject.put("ReName", Username);
                            jsonObject.put("RePassword", PassWord);
                            communicationClient.sendMessage(jsonObject.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } else {
                        Toast.makeText(register.this, "两次输入的密码不一致", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(register.this, "注册信息不能为空", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Handler CommunicationMessageHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                try {
                    HandlerMassage(new JSONObject((String) msg.obj));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        };

        communicationClient.setHandler(CommunicationMessageHandler);
    }

    public void HandlerMassage(JSONObject jsonObject) throws JSONException {
        switch (jsonObject.getInt("Type")) {
            case 5 :
                Register(jsonObject);
                break;
        }
    }

    public void Register(JSONObject jsonObject) {
        try {
            if(jsonObject.getBoolean("state")) {
                finish();
            } else {
                Toast.makeText(register.this, "注册失败，可能用户名已存在", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
