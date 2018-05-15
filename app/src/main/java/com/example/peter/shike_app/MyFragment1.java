package com.example.peter.shike_app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class MyFragment1 extends Fragment {

    private Button locbtn = null;
    private Context mContext;
    private AlertDialog alert = null;
    private AlertDialog.Builder builder = null;
    private Event[] eventList = new Event[1000];
    private int count = 0;
    private static final int msgKey1 = 1;
    private MyFragment1.TimeThread update_thread;
    private FrameLayout buttomfl;
    private Button tobuttom;
    private View nothing;
    private boolean isRun = true;



    public class TimeThread extends Thread {
        private final Object lock = new Object();
        private boolean pause = false;
        void pauseThread(){
            pause = true;
        }
        void resumeThread(){
            pause = false;
            synchronized (lock){
                lock.notifyAll();
            }
        }
        void onPause(){
            synchronized (lock){
                try{
                    lock.wait();
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        }
        @Override
        public void run() {
            do {
                try {
                    while (pause){
                        onPause();
                    }
                    Thread.sleep(5000);
                    if (pause){
                        onPause();
                    }
                    Message msg = new Message();
                    msg.what = msgKey1;
                    mHandler.sendMessage(msg);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while(isRun);
        }
    }
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage (Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case msgKey1:
                    getEventByTypeAsyncHttpClientPost(PreferenceUtil.maptype);
                    break;

                default:
                    break;
            }
        }
    };

    //test pictures loading
    private ImageView testImage;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fg_content1, container, false);
        mContext = getActivity();
        return view;
    }

    public void onStart(){
        super.onStart();
        getEventByTypeAsyncHttpClientPost(PreferenceUtil.maptype);
        update_thread = new MyFragment1.TimeThread();
        update_thread.start();
    }

    public void onDestroy(){
        super.onDestroy();
        isRun = false;
    }
    public void onResume(){
        super.onResume();
        update_thread.resumeThread();
        //设定地图显示范围
    }
    public void onPause(){
        super.onPause();
        update_thread.pauseThread();
    }
    private void getEventByTypeAsyncHttpClientPost(int type) {
        //创建异步请求对象
        AsyncHttpClient client = new AsyncHttpClient();
        //输入要请求的url
        String url = "http://120.25.232.47:8002/getEventByType/";
        //String url = "http://www.baidu.com";
        //请求的参数对象
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("type", type);
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
                        if (count > 0) {
                            JSONArray events = response.getJSONArray("events");
                            //Toast.makeText(mContext, events.toString(), Toast.LENGTH_LONG).show();
                            for (int i = 0; i < count; i++) {
                                JSONObject temp = events.getJSONObject(i);
                                eventList[i] = new Event();
                                eventList[i].setEventID(temp.getInt("eventID"));
                                //eventList[i].setBeginTime(temp.getString("beginTime"));
                                eventList[i].setDescription(temp.getString("description"));
                                //eventList[i].setEndTime(temp.getString("endTime"));
                                eventList[i].setOutdate(temp.getInt("outdate"));
                                eventList[i].setType(temp.getInt("type"));
                                if (eventList[i].getType() == 2) {
                                    eventList[i].setIshelped(temp.getInt("isHelped"));
                                    eventList[i].setHelper(temp.getInt("helperID"));
                                }
                                eventList[i].setPublisherID(temp.getInt("publisherID"));
                                eventList[i].setTitle(temp.getString("title"));
                                eventList[i].setUsername(temp.getString("username"));
                                Bundle bundle = new Bundle();
                                bundle.putInt("index", i);
                                //在地图上添加Marker，并显示
                            }
                        }
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
}