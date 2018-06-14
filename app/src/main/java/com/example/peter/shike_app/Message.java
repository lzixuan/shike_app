package com.example.peter.shike_app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.LogHandler;
import com.mob.MobSDK;

import org.apache.http.Header;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.gui.RegisterPage;

public class Message extends Activity {

    private TimeCount time;
    private Button getcode, next;
    private ImageButton messageret;
    private TextView phone, code;
    private Context mContext;
    private AlertDialog alert = null;
    private AlertDialog.Builder builder = null;
    private String token;
    private String codestr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message);
        mContext = Message.this;
        time = new TimeCount(60000, 1000);
        phone = (TextView) findViewById(R.id.phone);
        code = (TextView) findViewById(R.id.code);
        messageret = (ImageButton) findViewById(R.id.messageret);
        MobSDK.init(this);
        messageret.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert = null;
                builder = new AlertDialog.Builder(mContext, R.style.AlertDialog);
                alert = builder.setMessage("是否确定退出注册？")
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(mContext, "你点击了取消按钮~", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(Message.this, Login.class));
                                finish();
                            }
                        }).create();             //创建AlertDialog对象
                alert.show();                    //显示对话框
            }
        });
        next = (Button) findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Bundle bd = getIntent().getExtras();
//                userName = bd.getString("userName");
                messageAsyncHttpClientPost(phone.getText().toString(), code.getText().toString());
//                submitCode("86", phone.getText().toString(), code.getText().toString());
//
//                if (code.getText().toString().equals(codestr)) {
//                    Intent it = new Intent(Message.this, Signup.class);
//                    it.putExtra("phonenumber", phone.getText().toString());
//                    startActivity(it);
//                    finish();
//                }
//                else
//                    Toast.makeText(Message.this, "未进行验证或验证码不正确", Toast.LENGTH_SHORT).show();
            }
        });
        getcode = (Button) findViewById(R.id.getcode);
        getcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //messageAsyncHttpClientPost(phone.getText().toString());
                sendCode("86", phone.getText().toString());
                time.start();
            }
        });
    }

    class TimeCount extends CountDownTimer {

        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            getcode.setClickable(false);
            getcode.setText("("+millisUntilFinished / 1000 +") 秒后\n重新发送");
        }

        @Override
        public void onFinish() {
            getcode.setText("重新获取验证码");
            getcode.setClickable(true);

        }
    }

    public void messageAsyncHttpClientPost(String phonenumber, String code) {
        //创建异步请求对象
        AsyncHttpClient client = new AsyncHttpClient();
        //输入要请求的url
        String url = "http://ch.huyunfan.cn/PHP/user/phoneVerify.php";
        //请求的参数对象
        JSONObject jsonObject = new JSONObject();
        token = PreferenceUtil.token;
        try {
            jsonObject.put("token", token);
            jsonObject.put("mobile", phonenumber);
            jsonObject.put("code", code);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        //将参数加入到参数对象中
        ByteArrayEntity entity = null;
        try {
            entity = new ByteArrayEntity(jsonObject.toString().getBytes("UTF-8"));
            entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //进行post请求
        client.post(mContext, url, entity, "application/json", new JsonHttpResponseHandler() {
            //如果成功
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    int status = response.getInt("status");
                    if (status == 1) {
//                        String errMsg = response.getString("errMsg");
                        Toast.makeText(mContext, "手机验证失败", Toast.LENGTH_SHORT).show();
                    }
                    else if(status == 0) {
                        Toast.makeText(mContext, "注册成功！", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Message.this, Login.class));
                        finish();
                    }
                    else if (status == 2) {
//                        String errMsg = response.getString("errMsg");
                        Toast.makeText(mContext, "手机号已存在", Toast.LENGTH_SHORT).show();
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


    // 请求验证码，其中country表示国家代码，如“86”；phone表示手机号码，如“13800138000”
    public void sendCode(String country, String phone) {
        // 注册一个事件回调，用于处理发送验证码操作的结果
        SMSSDK.registerEventHandler(new EventHandler() {
            public void afterEvent(int event, int result, Object data) {
                if (result == SMSSDK.RESULT_COMPLETE) {
                    // TODO 处理成功得到验证码的结果
//                    Looper.prepare();
                    Toast.makeText(mContext, "已发送验证码，请耐心等候", Toast.LENGTH_LONG).show();
//                    Looper.loop();
                    // 请注意，此时只是完成了发送验证码的请求，验证码短信还需要几秒钟之后才送达
                } else{
                    // TODO 处理错误的结果
//                    Looper.prepare();
                    Toast.makeText(mContext, "发送验证码失败，请输入正确验证码", Toast.LENGTH_LONG).show();
//                    Looper.loop();
                }
            }
        });
        // 触发操作LONG
        SMSSDK.getVerificationCode(country, phone);
    }

    // 提交验证码，其中的code表示验证码，如“1357”
    public void submitCode(String country, final String phone, String code) {
        // 注册一个事件回调，用于处理提交验证码操作的结果
        SMSSDK.registerEventHandler(new EventHandler() {
            public void afterEvent(int event, int result, Object data) {
                if (result == SMSSDK.RESULT_COMPLETE) {
                    // TODO 处理验证成功的结果
//                    Looper.prepare();
                    Toast.makeText(mContext, "验证成功", Toast.LENGTH_LONG).show();
//                    Looper.loop();
//                    Intent it = new Intent(Message.this, Login.class);
//                    it.putExtra("phonenumber", phone);
//                    startActivity(it);
//                    finish();
                } else{
                    // TODO 处理错误的结果
//                    Looper.prepare();
                    Toast.makeText(mContext, "未进行验证或验证码不正确", Toast.LENGTH_LONG).show();
//                    Looper.loop();
                }

            }
        });
        // 触发操作
        SMSSDK.submitVerificationCode(country, phone, code);
    }

    protected void onDestroy() {
        super.onDestroy();
        //用完回调要注销掉，否则可能会出现内存泄露
        SMSSDK.unregisterAllEventHandler();
    };


//    public void sendCode(Context context) {
//        RegisterPage page = new RegisterPage();
//        //如果使用我们的ui，没有申请模板编号的情况下需传null
//        page.setTempCode(null);
//        page.setRegisterCallback(new EventHandler() {
//            public void afterEvent(int event, int result, Object data) {
//                if (result == SMSSDK.RESULT_COMPLETE) {
//                    // 处理成功的结果
//                    HashMap<String,Object> phoneMap = (HashMap<String, Object>) data;
//                    String country = (String) phoneMap.get("country"); // 国家代码，如“86”
//                    String phone = (String) phoneMap.get("phone"); // 手机号码，如“13800138000”
//                    // TODO 利用国家代码和手机号码进行后续的操作
//                } else{
//                    // TODO 处理错误的结果
//                }
//            }
//        });
//        page.show(context);
//    }
}
