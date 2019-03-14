package webrtc.example.com.webrtc_android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.UiThread;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import webrtc.example.com.webrtc_android.response.ResponseEnum;
import webrtc.example.com.webrtc_android.response.ResponseVo;
import webrtc.example.com.webrtc_android.service.LoginService;
import webrtc.example.com.webrtc_android.utils.JwtUtil;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static LoginService loginService=new LoginService();
    EditText et_username;
    EditText et_password;
    Button bt_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        et_username = findViewById(R.id.et_username);
        et_password = findViewById(R.id.et_password);
        bt_login = findViewById(R.id.bt_login);

        bt_login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"登录中...",Toast.LENGTH_SHORT).show();
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        ResponseVo responseVo=loginService.login(et_username.getText().toString().trim(),et_password.getText().toString().trim());
                        final String result= (String) responseVo.getResult();
                        if(responseVo.getCode()== ResponseEnum.SUCCESS.getCode()){
                            JwtUtil.set(getBaseContext(),result);
                            //跳转到聊天室
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(),"登录成功",Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), FriendsActivity.class);
                                    startActivity(intent);
                                }
                            });
                        }else{
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(),result,Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                });


            }
        });
    }



}
