package com.example.peter.shike_app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.content.*;
import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;

import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.AsyncHttpClient;

public class Signup extends Activity{

    private Button su;
    private ImageButton suret;
    private EditText suusername, supasswd, supasswd2;
    private String phonenumber;
    Context mContext = this;
    private AlertDialog alert = null;
    private AlertDialog.Builder builder = null;

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

    private void loginByAsyncHttpClientPost(String userName, String userPass) {
        //创建异步请求对象
        AsyncHttpClient client = new AsyncHttpClient();
        //输入要请求的url
        String url = "http://ch.huyunfan.cn/PHP/user/login/";
        //请求的参数对象
        JSONObject jsonObject = new JSONObject();
        //userPass = getMD5(userPass);

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
                        PreferenceUtil.token = response.getString("token");
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

    public static String fillMD5(String md5){
        return md5.length()==32?md5:fillMD5("0"+md5);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);
        Intent it = getIntent();
        phonenumber = "18811321234";
        bindViews();
    }


    private void signupByAsyncHttpClientPost(String... param) {
        final String userName = param[0];
        final String password = param[1];
        String mobile = param[2];
        //创建异步请求对象
        AsyncHttpClient client = new AsyncHttpClient();
        //输入要请求的url
        String url = "http://ch.huyunfan.cn/PHP/user/signup.php";
        //String url = "http://www.baidu.com";
        //请求的参数对象
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userName",userName);
            jsonObject.put("password",password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //byte[] jo = RSA.encrypt(jsonObject.toString().getBytes());
        //将参数加入到参数对象中
        byte[] jo = jsonObject.toString().getBytes();
        ByteArrayEntity entity = null;
        entity = new ByteArrayEntity(jo);
        entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
//        try {
        //RSA
        /*
        entity = new ByteArrayEntity(jo);
        entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        */
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        try {
//            entity = new ByteArrayEntity(jsonObject.toString().getBytes("UTF-8"));
//            entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
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
                    int status = response.getInt("signupStatus");
                    if(status == 0) {
                        Toast.makeText(mContext, "进入手机验证", Toast.LENGTH_SHORT).show();
                        loginByAsyncHttpClientPost(userName, password);
//                        PreferenceUtil.token = response.getString("token");
                        Intent it = new Intent(Signup.this, Message.class);
//                        it.putExtra("userName", suusername.getText().toString());
                        startActivity(it);
                        finish();
                    }
                    else if (status == 1){
                        String s = response.getString("errMsg");
                        Toast.makeText(mContext, s,  Toast.LENGTH_LONG).show();
                    }
//                    else if (status == 2){
//                        String s = response.getString("errMsg");
//                        Toast.makeText(mContext, "手机号已存在, " + s,  Toast.LENGTH_LONG).show();
//                    }
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

    private void bindViews() {
        suret = (ImageButton) findViewById(R.id.suret);
        su = (Button) findViewById(R.id.su);
        suusername = (EditText) findViewById(R.id.suusername);
        supasswd = (EditText) findViewById(R.id.supasswd);
        supasswd2 = (EditText) findViewById(R.id.supasswd2);
        suret.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert = null;
                builder = new AlertDialog.Builder(mContext, R.style.AlertDialog);
                alert = builder.setMessage("是否确定重新验证手机？")
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(mContext, "你点击了取消按钮~", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent it = new Intent(Signup.this, Login.class);
                                startActivity(it);
                                //startActivity(new Intent(Signup.this, Message.class));
                                finish();
                            }
                        }).create();             //创建AlertDialog对象
                alert.show();                    //显示对话
            }
        });
        su.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Username = suusername.getText().toString();
                String UserPass = supasswd.getText().toString();
                String UserPassConf = supasswd2.getText().toString();
                String UserPhoneNumber = phonenumber;
                if(Username.length() == 0) {
                    Toast.makeText(mContext, "用户名不能为空", Toast.LENGTH_SHORT).show();
                }
                else if(Username.length() > 10) {
                    Toast.makeText(mContext, "用户名不能超过10个字符", Toast.LENGTH_SHORT).show();
                }
                /*else if(UserPhoneNumber.length() == 0){
                    Toast.makeText(mContext, "手机号不能为空", Toast.LENGTH_SHORT).show();
                }*/
                else if(UserPass.length() == 0){
                    Toast.makeText(mContext, "  请输入密码", Toast.LENGTH_SHORT).show();
                }
                else if(UserPass.length() > 32){
                    Toast.makeText(mContext, "  密码不能超过32个字符", Toast.LENGTH_SHORT).show();
                }
                else if(UserPassConf.length() == 0){
                    Toast.makeText(mContext, "  请输入确认密码", Toast.LENGTH_SHORT).show();
                }
                else if (!UserPass.equals(UserPassConf)) {
                    Toast.makeText(mContext, "前后输入的密码不一致，请再次尝试", Toast.LENGTH_SHORT).show();
                }
                else {
                    UserPass = getMD5(UserPass);

                    String[] param = {Username, UserPass, UserPhoneNumber};
                    signupByAsyncHttpClientPost(param);

                }
                //startActivity(new Intent(Signup.this, MainActivity.class));
                //finish();
            }
        });
    }

}
