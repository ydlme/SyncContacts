package com.cn.justin.contacttools;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.commons.codec.binary.Hex;
import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginActivity extends AppCompatActivity {

    private EditText edit_user;
    private EditText edit_passwd;
    private int uid = 0;
    private final String RegisterAPI = "register";
    private final String LoginAPI = "login";
    private String md5OfString(String str) {
        MessageDigest messageDigest = null;
        try {
          messageDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        if (messageDigest == null)
            return "";
        messageDigest.update(str.getBytes());

        byte byteData[] = messageDigest.digest();

        //convert the byte to hex format method 1
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < byteData.length; i++) {
            sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }

        return sb.toString();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button btn_login = (Button)findViewById(R.id.login_btn);
        Button btn_reg = (Button)findViewById(R.id.register_btn);
        edit_user = (EditText)findViewById(R.id.edit_user);
        edit_passwd = (EditText)findViewById(R.id.edit_passwd);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = edit_user.getText().toString();
                String passwd = edit_passwd.getText().toString();
                String md5 = md5OfString(passwd);
                RequestParams params = new RequestParams();
                params.put("username", user);
                params.put("passwd", md5);
                AsyncHttpClient client = new AsyncHttpClient();
                String url = String.format("%s%s", ((AppConf)getApplication()).getHostIP(), LoginAPI);
                client.post(url,  params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int i, Header[] headers, byte[] bytes) {
                        setTitle("登陆成功");
                        String str = null;
                        try {
                            str = new String(bytes, "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        JSONObject obj = null;
                        try {
                            obj = new JSONObject(str);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        try {
                            uid = obj.getInt("uid");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (uid != 0) {
                            Intent intent = new Intent();
                            intent.putExtra("uid", uid);
                            setResult(1, intent);
                            finish();
                        } else {
                            setTitle("登陆失败");
                        }
                    }

                    @Override
                    public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                        setTitle("登陆失败");
                    }
                });
            }
        });

        btn_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = edit_user.getText().toString();
                String passwd = edit_passwd.getText().toString();
                String md5 = md5OfString(passwd);
                RequestParams params = new RequestParams();
                params.put("username", user);
                params.put("passwd", md5);
                String url = String.format("%s%s", ((AppConf)getApplication()).getHostIP(), RegisterAPI);
                AsyncHttpClient client = new AsyncHttpClient();
                client.post(url, params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int i, Header[] headers, byte[] bytes) {
                        setTitle("注册成功");
                        String str = null;
                        try {
                            str = new String(bytes, "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        JSONObject obj = null;
                        try {
                            obj = new JSONObject(str);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        try {
                            uid = obj.getInt("uid");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Intent intent = new Intent();
                        intent.putExtra("uid", uid);
                        setResult(1, intent);
                        finish();
                    }

                    @Override
                    public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                        setTitle("注册失败");
                        Intent intent = new Intent();
                        intent.putExtra("uid", uid);
                        setResult(1, intent);
                        finish();
                    }
                });
            }
        });

    }
}
