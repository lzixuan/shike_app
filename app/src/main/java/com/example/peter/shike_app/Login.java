package com.example.peter.shike_app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.*;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.lang.String;

import org.apache.http.protocol.HTTP;

import org.json.JSONObject;
import org.json.JSONException;
//import org.json.simple.JSONObject;
//import org.json.simple.JSONValue;
//import org.json.simple.parser.JSONParser;
//import net.sf.json.JSONObject;
//import net.sf.json.JSONException;
import org.apache.http.entity.ByteArrayEntity;

import com.google.gson.Gson;
//import com.loopj.android.http.JsonHttpResponseHandler;
//import com.loopj.android.http.AsyncHttpClient;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static android.content.ContentValues.TAG;
import static com.example.peter.shike_app.MainActivity.BASE_LOGIN_URL;

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
                    TextView displaytxt = (TextView) findViewById(R.id.display_txt);
                    //loginByAsyncHttpClientPost(param[0], param[1], Username);
                    Info info=new Info(param[0], param[1]); /*** 利用Gson 将对象转json字符串*/
                    Gson gson=new Gson();
                    String obj=gson.toJson(info);
                    RequestBody body=RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),obj);
                    Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_LOGIN_URL).build();
                    //NewsModel.postNews(param[0],param[1]);
                    final APi login = retrofit.create(APi.class);
                    retrofit2.Call<ResponseBody> data = login.getMessage(body);
                    data.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            try {
                                //Log.d(TAG, "onResponse: --ok--" + response.body());
                                JSONObject obj1 = new JSONObject(response.body().toString());
                                int status = (Integer)obj1.get("loginStatus");
                                if (status == 0) {
                                    Toast.makeText(Login.this, "login success!\n", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(Login.this, MainActivity.class));
                                    finish();
                                } else {
                                    JSONObject obj2 = new JSONObject(response.body().toString());

                                    String errMsg = (String)obj2.get("errMsg");// print {"host":120,"sum":100}
                                    Toast.makeText(Login.this, "fail to login!\n", Toast.LENGTH_SHORT).show();
                                    Toast.makeText(Login.this, errMsg, Toast.LENGTH_SHORT).show();
                                }
                            }
                            catch(Exception e)
                            {
                                Toast.makeText(mContext, "connection error!\n",  Toast.LENGTH_LONG).show();
                            }
                        }
                        @Override
                        public void onFailure(retrofit2.Call<ResponseBody> call, Throwable t) {
//                            Log.d(TAG, "onResponse: --err--"+t.toString());
                            Toast.makeText(mContext, "retrofit error!\n"+t.toString(),  Toast.LENGTH_LONG).show();
                        } });}
                break;
            case R.id.signupbtn:
                startActivity(new Intent(Login.this, Message.class));
                finish();
                break;
            case R.id.signinlater:
                startActivity(new Intent(Login.this, MainActivity.class));
                finish();
                break;
        }
    }
}
