package com.example.peter.shike_app;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class MyFragment3 extends Fragment implements View.OnClickListener{

    private Button myupload = null;
    private Button pwd = null;
    private Context mContext = null;
    private TextView name = null;
    private Button logout, about;
    private Button myfocustag, myrate;

    private AlertDialog alert = null;
    private AlertDialog.Builder builder = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fg_content3, container, false);
        mContext = getActivity();
        name = (TextView) view.findViewById(R.id.name);
        if (PreferenceUtil.islogged)
            name.setText(PreferenceUtil.username);
        myupload = (Button) view.findViewById(R.id.myupload);
        pwd = (Button) view.findViewById(R.id.pwd);
        about = (Button)view.findViewById(R.id.about);
        myrate = (Button)view.findViewById(R.id.myrate);
        pwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (PreferenceUtil.islogged)
                    startActivity(new Intent(getActivity(), ChangePwd.class));
                else
                    Toast.makeText(getActivity(), "请先登录", Toast.LENGTH_SHORT).show();
            }
        });
        logout = (Button)view.findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreferenceUtil.islogged = false;
                Toast.makeText(getActivity(), "退出登录",  Toast.LENGTH_SHORT);
                startActivity(new Intent(getActivity(), Login.class));
                getActivity().finish();
            }
        });
        myupload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "功能即将上线，敬请期待!", Toast.LENGTH_SHORT).show();
            }
        });
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert = null;
                builder = new AlertDialog.Builder(mContext);
                alert = builder.setTitle("应用信息")
                        .setMessage("应用名称：食刻+\n版本号：V1.0\nPKU EECS，2018")
                        .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).create();
                alert.show();
            }
        });
        myfocustag = (Button)view.findViewById(R.id.myfocustag);
        myfocustag.setOnClickListener(this);
        myrate.setOnClickListener(this);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        /*if (PreferenceUtil.islogged)
            getEventByIDAsyncHttpClientPost(PreferenceUtil.userID);*/
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(mContext, "功能即将上线，敬请期待！", Toast.LENGTH_SHORT).show();
    }

    /*public void getEventByIDAsyncHttpClientPost(int userID) {
        //创建异步请求对象
        AsyncHttpClient client = new AsyncHttpClient();
        //输入要请求的url
        String url = "http://120.25.232.47:8002/getEventByID/";
        //请求的参数对象
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userID", userID);
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
                    int status = response.getInt("getStatus");
                    if (status == 1) {
                        Toast.makeText(mContext, "status code is:"+ statusCode+ "\n更新失败!\n", Toast.LENGTH_LONG).show();
                    }
                    else if(status == 0) {
                        //Toast.makeText(mContext, response.toString(), Toast.LENGTH_LONG).show();
                        int count = response.getInt("eventNum");
                        myupload.setText(count+"\n发布事件");
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

    }*/
}
