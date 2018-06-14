package com.example.peter.shike_app;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class ListFragment extends Fragment implements AdapterView.OnItemClickListener{

    private ListView list_event;
    private int which;
    private int myCanteen;
    private Context mContext;
    private AlertDialog alert;
    private AlertDialog.Builder builder;

    public ListFragment() {}
    @SuppressLint("ValidFragment")
    public ListFragment(int which, int myCanteen) {
        this.myCanteen = myCanteen;
        this.which = which;
    }

    @SuppressLint("ValidFragment")
    public ListFragment(int which) {
        this.which = which;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.eventlist, container, false);
        //mContext = getActivity();
        list_event = (ListView) view.findViewById(R.id.list_event);

        if (which == 0) {
            PreferenceUtil.myAdapter = new MyAdapter(PreferenceUtil.dishDatas, getActivity());
            list_event.setAdapter(PreferenceUtil.myAdapter);
            PreferenceUtil.dishDatas.clear();
            getDishAsyncPHPClientPost();
            list_event.setOnItemClickListener(this);
            //list_event.setOnItemLongClickListener(this);
        }
        if (which == 4) {
            PreferenceUtil.myAdapterforComment = new MyAdapterforComment(PreferenceUtil.commentdatas, getActivity());
            list_event.setAdapter(PreferenceUtil.myAdapterforComment);
            PreferenceUtil.commentdatas.clear();
            Bundle bd = getArguments();
            getCommentByFather(bd.getInt("fatherID"));
        }
        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Bundle bd = new Bundle();
        int dishID = 0;
        switch (which) {
            case 0:
                dishID = PreferenceUtil.dishDatas.get(position).getID();
                break;
            case 1:
                dishID = PreferenceUtil.myDishDatas.get(position).getID();
                break;
            case 3:
                dishID = PreferenceUtil.locDishdatas.get(position).getID();
                break;
        }
        bd.putInt("dishID", dishID);
        bd.putInt("which", which);
        Intent it = new Intent(getActivity(), dishActivity.class);
        it.putExtras(bd);
        startActivity(it);
    }

    private void getDishAsyncPHPClientPost() {
        //创建异步请求对象
        AsyncHttpClient client = new AsyncHttpClient();
        //输入要请求的url
        String url = "http://ch.huyunfan.cn/PHP/dish/searchDish.php";
        //请求的参数对象
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("canteenID", this.myCanteen);
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
                    int status = response.getInt("searchDishStatus");
                    if (status == 1) {
                        Toast.makeText(mContext, "status code is:"+ statusCode+ "\n更新失败!\n", Toast.LENGTH_LONG).show();
                    }
                    else if(status == 0) {
                        //Toast.makeText(mContext, response.toString(), Toast.LENGTH_LONG).show();
                        int count = response.getInt("dishNum");
                        if (count > 0) {
                            JSONArray dishList = response.getJSONArray("dishList");
                            //Toast.makeText(mContext, dishList.toString(), Toast.LENGTH_LONG).show();
                            for (int i = 0; i < count; i ++) {
                                JSONObject temp = dishList.getJSONObject(i);
                                Dish dish = new Dish();
                                dish.setID(temp.getInt("dishID"));
                                dish.setCanteenID(temp.getInt("canteenID"));
                                dish.setName(temp.getString("dishName"));
                                dish.setPictureURL(temp.getString("photo"));
                                dish.setRating(temp.getDouble("rating"));
                                dish.setPublisherName(temp.getString("userName"));
                                PreferenceUtil.dishDatas.add(dish);
                            }
                            PreferenceUtil.myAdapter.notifyDataSetChanged();
                        }
                    }

                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(mContext, "connection error!Error number is:" + statusCode,  Toast.LENGTH_SHORT).show();
            }
        });
        return;
    }

    private void getCommentByFather(final int fatherID) {
        //创建异步请求对象
        AsyncHttpClient client = new AsyncHttpClient();
        //输入要请求的url
        String url = "http://ch.huyunfan.cn/PHP/dish/checkComments.php";
        //String url = "http://www.baidu.com";
        //请求的参数对象
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("dishID", fatherID);
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
                    int status = response.getInt("checkStatus");
                    if (status == 1) {
                        Toast.makeText(mContext, "status code is:"+ statusCode+ "\n更新失败!\n", Toast.LENGTH_LONG).show();
                    }
                    else if(status == 0) {
                        int count = response.getInt("commentNum");
                        if (count != 0) {
                            JSONArray comments = response.getJSONArray("commentList");
                            for (int i = 0; i < count; i++) {
                                JSONObject temp = comments.getJSONObject(i);
                                Comment comment = new Comment();
                                //comment.setCommentID(temp.getInt("commentID"));
                                comment.setContent(temp.getString("comment"));
                                comment.setFatherID(fatherID);
                                comment.setPublisherID(temp.getInt("userID"));
                                comment.setUsername(temp.getString("userName"));
                                PreferenceUtil.commentdatas.add(comment);
                            }
                            PreferenceUtil.myAdapterforComment.notifyDataSetChanged();
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