package com.example.peter.shike_app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.*;

import java.math.BigInteger;
import java.security.MessageDigest;

import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;
import org.apache.http.entity.ByteArrayEntity;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.AsyncHttpClient;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

public class Login extends Activity implements View.OnClickListener {

    private Button signinbtn = null;
    private Button signupbtn = null;
    private Button signinlater = null;
    private EditText username = null;
    private EditText passwd = null;
    Context mContext = this;

    public static String getMD5(String str) {
        try {
            // 生成一个MD5加密计算摘要
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 计算md5函数
            md.update(str.getBytes());
            // digest()最后确定返回md5 hash值，返回值为8为字符串。因为md5 hash值是16位的hex值，实际上就是8位的字符
            // BigInteger函数则将8位的字符串转换成16位hex值，用字符串来表示；得到字符串形式的hash值
            String md5=new BigInteger(1, md.digest()).toString(16);
            //BigInteger会把0省略掉，需补全至32位
            return fillMD5(md5);
        } catch (Exception e) {
            throw new RuntimeException("MD5加密错误:"+e.getMessage(),e);
        }
    }

    public static String fillMD5(String md5){
        return md5.length()==32?md5:fillMD5("0"+md5);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        bindViews();
    }

    private void bindViews() {
        signinbtn = (Button) findViewById(R.id.signinbtn);
        signupbtn = (Button) findViewById(R.id.signupbtn);
        signinlater = (Button) findViewById(R.id.signinlater);
        username = (EditText) findViewById(R.id.username);
        passwd = (EditText) findViewById(R.id.passwd);
        signinbtn.setOnClickListener(this);
        signupbtn.setOnClickListener(this);
        signinlater.setOnClickListener(this);
    }

    private void loginByAsyncHttpClientPost(String userName, String userPass, final String username) {
        //创建异步请求对象
        AsyncHttpClient client = new AsyncHttpClient();
        //输入要请求的url
        String url = "http://ch.huyunfan.cn/PHP/user/login/";
        //请求的参数对象
        JSONObject jsonObject = new JSONObject();
        userPass = getMD5(userPass);

        try {
            jsonObject.put("userName",userName);
            jsonObject.put("password",userPass);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //byte[] jo = RSA.encrypt(jsonObject.toString().getBytes());
        byte[] jo = jsonObject.toString().getBytes();
        //将参数加入到参数对象中
        ByteArrayEntity entity = null;
//        try {
        entity = new ByteArrayEntity(jo);
        entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
        //进行post请求
        client.post(mContext, url, entity, "application/json", new JsonHttpResponseHandler() {
            //如果成功
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    int status = response.getInt("loginStatus");
                    if (status == 1) {
                        String errMsg = response.getString("errMsg");
                        Toast.makeText(mContext,  errMsg,  Toast.LENGTH_LONG).show();
                    }
                    else if(status == 0) {
                        PreferenceUtil.islogged = true;
                        PreferenceUtil.userID = response.getInt("userID");
                        PreferenceUtil.username = username;
                        PreferenceUtil.token = response.getString("token");
                        Toast.makeText(Login.this, "登录成功", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Login.this, MainActivity.class));
                        finish();
                    }
                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(mContext, "connection error!Error number is:" + statusCode,  Toast.LENGTH_LONG).show();
            }
        });
        return;

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.signinbtn:
                String Username = username.getText().toString();
                String Passwd = passwd.getText().toString();
                if (Username.length() <= 0 || Passwd.length() <= 0) {
                    Toast.makeText(this, "不能为空", Toast.LENGTH_SHORT).show();
                }
                else {
                    String[] param = {Username, Passwd};
                    loginByAsyncHttpClientPost(param[0], param[1], Username);
                }
                break;
            case R.id.signupbtn:
                startActivity(new Intent(Login.this, Signup.class));
                finish();
                break;
            case R.id.signinlater:
                startActivity(new Intent(Login.this, MainActivity.class));
                finish();
                break;
        }
    }
}
