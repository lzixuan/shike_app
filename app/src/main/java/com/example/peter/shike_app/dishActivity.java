package com.example.peter.shike_app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.view.inputmethod.*;

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
import java.util.ArrayList;

public class dishActivity extends AppCompatActivity {
    
    private int which, dishID;
    private AlertDialog alert = null;
    private AlertDialog.Builder builder = null;
    private Context mContext;
    private Button eventcontentret, deletebtn, reportbtn, comment_send, contactBtn;
    private TextView dish_content, dish_name, eventcontenttitle, dish_publisher, dish_canteen;
    private ImageButton newComment;
    private RelativeLayout rl_input;
    private TextView hide;
    private EditText comment_content;


    private ImageView testImage;
    private TextView dishScore;
    private RatingBar scoreBar;
    private Button scoreButton;
    private Button dish_tag;
    private ListView tag_recommend_listview;
    private Button tag_recommendadd;
    private Button tag_recommendret;

    private ExpandableListView tag_list;
    private boolean[] checkItems;
    private TextView tags;

    private int userScore;
    private double score;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dish_content);
        mContext = dishActivity.this;
        eventcontenttitle = (TextView) findViewById(R.id.eventcontenttitle);
        eventcontentret = (Button) findViewById(R.id.eventcontentret);
        eventcontentret.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        reportbtn = (Button) findViewById(R.id.reportbtn);
        rl_input = (RelativeLayout)findViewById(R.id.rl_input);
        newComment = (ImageButton)findViewById(R.id.newComment);
        hide = (TextView)findViewById(R.id.hide_down);
        comment_content = (EditText)findViewById(R.id.comment_content);
        comment_send = (Button) findViewById(R.id.comment_send);

        testImage = (ImageView)findViewById(R.id.testImage);
        dishScore = (TextView)findViewById(R.id.dishscore);
        scoreBar = (RatingBar)findViewById(R.id.scoreBar);
        dishScore.setText(String.valueOf(scoreBar.getRating()));
        dish_name = (TextView)findViewById(R.id.dish_name);
        dish_canteen = (TextView)findViewById(R.id.dish_canteen);
        dish_publisher = (TextView)findViewById(R.id.dish_publisher);
        scoreButton = (Button)findViewById(R.id.dish_score);
        dish_tag = (Button)findViewById(R.id.dish_tag);

        Bundle bd = getIntent().getExtras();
        dishID = bd.getInt("dishID");
        which = bd.getInt("which");
        final Dish dish = PreferenceUtil.getDish(dishID);
        dish_name.setText(dish.getName());
        dish_canteen.setText(PreferenceUtil.getCanteen(dish.getCanteenID()));
        dish_publisher.setText("匿名天使");

        score = dish.getRating();
        if (score == -1){
            scoreBar.setRating(0);
            dishScore.setText("0.0");
        }
        else{
            scoreBar.setRating((float)score);
            dishScore.setText(String.valueOf(score));
        }

        PreferenceUtil.gData.clear();
        PreferenceUtil.iData.clear();
        PreferenceUtil.lData.clear();

        int group_before = 0;
        for(int i = 0; i < PreferenceUtil.tagType.length; i ++) {
            PreferenceUtil.gData.add(PreferenceUtil.tagType[i]);
        }
        for(int i = 0; i < PreferenceUtil.tag.length; i ++) {
            Tag tag = new Tag(i);
            if(group_before != tag.getType()) {
                PreferenceUtil.iData.add(PreferenceUtil.lData);
                PreferenceUtil.lData = new ArrayList<Tag>();
            }
            group_before = tag.getType();
            PreferenceUtil.lData.add(tag);
        }
        PreferenceUtil.iData.add(PreferenceUtil.lData);


        /*Picasso.with(mContext)
                .load("http://i.imgur.com/DvpvklR.png")
                .fit()
                .into(testImage);*/
        //load the image
        if (dish.getPictureURL() != "") {
            Picasso.with(mContext)
                    .load("http://"+dish.getPictureURL())
                    .fit()
                    .into(testImage);
        }
        //点击评分按钮弹出对话框
        scoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!PreferenceUtil.islogged) {
                    Toast.makeText(mContext, "请先登录", Toast.LENGTH_LONG).show();
                    return;
                }
                final String[] scores = {"1分", "2分", "3分", "4分", "5分"};
                alert = null;
                builder = new AlertDialog.Builder(mContext);
                alert = builder.setTitle("请选择对菜品评分")
                        .setSingleChoiceItems(scores, 0, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                userScore = which;
                            }
                        })
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                /*String score = String.valueOf(userScore + 1);
                                Toast.makeText(mContext, score, Toast.LENGTH_SHORT).show();*/
                                int score = userScore + 1;
                                postRating(score, PreferenceUtil.userID, dishID);

                            }
                        }).create();
                alert.show();
            }
        });
        //点击查看标签按钮弹出对话框
        dish_tag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!PreferenceUtil.islogged) {
                    Toast.makeText(mContext, "请先登录", Toast.LENGTH_LONG).show();
                    return;
                }
                PreferenceUtil.tagAdapter1 = new tagAdapter(PreferenceUtil.tagData, mContext);
                alert = null;
                builder = new AlertDialog.Builder(mContext);
                final LayoutInflater inflater = getLayoutInflater();
                View view_custom = inflater.inflate(R.layout.tag_recommend, null, false);
                tag_recommend_listview = (ListView)view_custom.findViewById(R.id.tag_recommend_listview);
                tag_recommend_listview.setAdapter(PreferenceUtil.tagAdapter1);

                PreferenceUtil.tagData.clear();
                getTagAsyncHTTPClientPost();

                tag_recommendadd = (Button)view_custom.findViewById(R.id.tag_recommendadd);
                tag_recommendadd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alert.dismiss();

                        checkItems = new boolean[PreferenceUtil.tag.length];
                        for(int i = 0; i < checkItems.length; i ++)
                            checkItems[i] = false;

                        PreferenceUtil.tagAdapter = new MyBaseExpandableListAdapter(PreferenceUtil.gData, PreferenceUtil.iData, mContext);

                        alert = null;
                        builder = new AlertDialog.Builder(mContext);
                        final LayoutInflater inflater = getLayoutInflater();
                        View view_custom = inflater.inflate(R.layout.tag_selete, null, false);

                        builder.setView(view_custom);

                        tag_list = view_custom.findViewById(R.id.tag_list);
                        tag_list.setAdapter(PreferenceUtil.tagAdapter);

                        tag_list.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                            @Override
                            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                                PreferenceUtil.tagAdapter.select(groupPosition, childPosition);
                                Toast.makeText(mContext, "你点击了：" + PreferenceUtil.iData.get(groupPosition).get(childPosition).getName(), Toast.LENGTH_SHORT).show();
                                return true;
                            }
                        });

                        builder.setCancelable(true);
                        alert = builder.create();

                        view_custom.findViewById(R.id.tag_choose).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String result = " ";
                                for(int i = 0; i < PreferenceUtil.tagType.length; i ++) {
                                    for(int j = 0; j < PreferenceUtil.iData.get(i).size(); j ++) {
                                        Tag temp_tag = PreferenceUtil.tagAdapter.getChild(i, j);
                                        if(temp_tag.isSelected()) {
                                            result += temp_tag.getName() + " ";
                                            checkItems[temp_tag.getID()] = true;
                                            break;
                                        }
                                    }
                                }
                                Toast.makeText(mContext, "你选择了： " + result, Toast.LENGTH_SHORT).show();
                                addTagByAsyncHttpClientPost();
                                alert.dismiss();
                            }
                        });
                        alert.show();
                    }
                });

                tag_recommendret = (Button)view_custom.findViewById(R.id.tag_recommendret);
                tag_recommendret.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alert.dismiss();
                    }
                });
                builder.setView(view_custom);
                alert = builder.create();
                alert.show();
            }
        });

        FragmentManager fManager = getSupportFragmentManager();
        ListFragment nlFragment = new ListFragment(4);
        Bundle bd2 = new Bundle();
        bd2.putInt("fatherID", dishID);
        nlFragment.setArguments(bd2);
        FragmentTransaction ft = fManager.beginTransaction();
        ft.replace(R.id.comment_fl, nlFragment);
        ft.commit();

        newComment.setVisibility(View.GONE);
        if (which != 1) {
            newComment.setVisibility(View.VISIBLE);
            newComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 弹出输入法
                    InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                    // 显示评论框
                    newComment.setVisibility(View.GONE);
                    rl_input.setVisibility(View.VISIBLE);

                }
            });
            hide.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 隐藏评论框
                    newComment.setVisibility(View.VISIBLE);
                    rl_input.setVisibility(View.GONE);
                    // 隐藏输入法，然后暂存当前输入框的内容，方便下次使用
                    InputMethodManager im = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    im.hideSoftInputFromWindow(comment_content.getWindowToken(), 0);

                }
            });

            comment_send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (PreferenceUtil.islogged) {
                        final String comment = comment_content.getText().toString();
                        alert = null;
                        builder = new AlertDialog.Builder(mContext, R.style.AlertDialog);
                        alert = builder.setMessage("是否确定发布？")
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Toast.makeText(mContext, "你点击了取消按钮~", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (comment.equals("")) {
                                            Toast.makeText(mContext, "评论不能为空", Toast.LENGTH_SHORT).show();
                                            alert.dismiss();
                                        } else {
                                            postComment(comment, PreferenceUtil.userID, dishID);
                                            alert.dismiss();
                                            comment_content.setText("");
                                        }
                                    }
                                }).create();             //创建AlertDialog对象
                        alert.show();                    //显示对话框
                    } else {
                        Toast.makeText(mContext, "请先登录", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }
    public void addTagByAsyncHttpClientPost() {
        //创建异步请求对象
        AsyncHttpClient client = new AsyncHttpClient();
        //输入要请求的url
        String url = "http://ch.huyunfan.cn/PHP/tag/addTagging.php";
        JSONObject jsonObject = new JSONObject();
        //创建标签ID的Json数组{ID：xxx}
        JSONArray tagList = new JSONArray();

        for(int i = 0; i < checkItems.length; i ++) {
            if(checkItems[i]) {
                JSONObject tempObject = new JSONObject();
                try{
                    tempObject.put("ID", i + 6);
                }catch (Exception e){
                    e.printStackTrace();
                }
                try{
                    tagList.put(tempObject);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }

        //创建大的Json对象
        try {
            jsonObject.put("userID", PreferenceUtil.userID);
            jsonObject.put("dishID", dishID);
            jsonObject.put("tagList", tagList);
        }catch (Exception e) {
            e.printStackTrace();
        }

        ByteArrayEntity entity = null;
        try {
            entity = new ByteArrayEntity(jsonObject.toString().getBytes("UTF-8"));
            entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //Toast.makeText(mContext, "开始上传标签", Toast.LENGTH_LONG).show();

        client.post(mContext, url, entity, "application/json", new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    int result = (response.getInt("Status"));
                    if (result == 0){
                        Toast.makeText(mContext, "上传标签成功", Toast.LENGTH_LONG).show();
                    }
                    else
                        Toast.makeText(mContext, "上传标签失败", Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(mContext, "上传标签失败", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void getTagAsyncHTTPClientPost() {
        //创建异步请求对象
        AsyncHttpClient client = new AsyncHttpClient();
        //输入要请求的url
        String url = "http://ch.huyunfan.cn/PHP/dish/checkDishTags.php";
        //请求的参数对象
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("dishID", dishID);
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
                        Toast.makeText(mContext, "status code is:"+ statusCode+ "\n获取菜品标签失败!\n", Toast.LENGTH_LONG).show();
                    }
                    else if(status == 0) {
                        //Toast.makeText(mContext, response.toString(), Toast.LENGTH_LONG).show();
                        int count = response.getInt("tagNum");
                        if (count > 0) {
                            JSONArray tagList = response.getJSONArray("tagList");
                            //Toast.makeText(mContext, tagList.toString(), Toast.LENGTH_LONG).show();
                            for (int i = 0; i < count; i ++) {
                                JSONObject temp = tagList.getJSONObject(i);
                                PreferenceUtil.tagData.add(PreferenceUtil.tag[temp.getInt("tagID") - 6]);
                            }
                            PreferenceUtil.tagAdapter1.notifyDataSetChanged();
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
    public void postComment(final String comment, final int userID, final int dishID) {
        //创建异步请求对象
        AsyncHttpClient client = new AsyncHttpClient();
        //输入要请求的url
        String url = "http://ch.huyunfan.cn/PHP/comment/addComment.php";
        //请求的参数对象
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("comment", comment);
            jsonObject.put("userID", userID);
            jsonObject.put("dishID", dishID);
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
                    int status = response.getInt("Status");
                    if (status == 0){
                        Toast.makeText(mContext, "发布成功", Toast.LENGTH_SHORT).show();
                        Comment tmpcomment = new Comment();
                        tmpcomment.setCommentID(response.getInt("commentID"));
                        tmpcomment.setFatherID(dishID);
                        tmpcomment.setContent(comment);
                        tmpcomment.setPublisherID(userID);
                        tmpcomment.setUsername(PreferenceUtil.username);
                        PreferenceUtil.commentdatas.add(tmpcomment);
                        PreferenceUtil.myAdapterforComment.notifyDataSetChanged();
                        FragmentManager fManager = getSupportFragmentManager();
                        ListFragment nlFragment = new ListFragment(4);
                        Bundle bd2 = new Bundle();
                        bd2.putInt("fatherID", dishID);
                        nlFragment.setArguments(bd2);
                        FragmentTransaction ft = fManager.beginTransaction();
                        ft.replace(R.id.comment_fl, nlFragment);
                        ft.commit();
                    }
                    else{
                        Toast.makeText(mContext, "发布失败", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                //Toast.makeText(mContext, "connection error!Error number is:" + statusCode,  Toast.LENGTH_SHORT).show();
                Toast.makeText(mContext, "发布失败", Toast.LENGTH_SHORT).show();
            }
        });
        return;
    }
    public void postRating(final int rating, final int userID, final int dishID) {
        //创建异步请求对象
        AsyncHttpClient client = new AsyncHttpClient();
        //输入要请求的url
        String url = "http://ch.huyunfan.cn/PHP/rating/rate.php";
        //请求的参数对象
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userID", userID);
            //jsonObject.put("token", PreferenceUtil.token);
            jsonObject.put("dishID", dishID);
            jsonObject.put("rating", rating);
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
                    int status = response.getInt("Status");
                    if (status == 0){
                        Toast.makeText(mContext, "发布成功", Toast.LENGTH_SHORT).show();
                        score = response.getDouble("rating");
                        scoreBar.setRating((float)score);
                        dishScore.setText(String.valueOf(score));
                        PreferenceUtil.getDish(dishID).setRating(score);
                    }
                    else{
                        Toast.makeText(mContext, "发布失败", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                //Toast.makeText(mContext, "connection error!Error number is:" + statusCode,  Toast.LENGTH_SHORT).show();
                Toast.makeText(mContext, "发布失败", Toast.LENGTH_SHORT).show();
            }
        });
        return;
    }
}
