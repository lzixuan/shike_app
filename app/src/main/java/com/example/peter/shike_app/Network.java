package com.example.peter.shike_app;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @ explain:
 * @ author：xujun on 2016-8-25 15:19
 * @ email：gdutxiaoxu@163.com
 */
public class Network {

    private static volatile Network mInstance;
    private APi mApi;

    private Network(){

    }

    public static Network getInstance(){
        if(mInstance==null){
            synchronized (Network.class){
                if(mInstance==null){
                    mInstance=new Network();
                }
            }
        }
        return mInstance;
    }

    public APi getApi(){
        if(mApi==null){
            synchronized (Network.class){
                if(mApi==null){
                    Retrofit retrofit = new Retrofit.Builder()
                            //使用自定义的mGsonConverterFactory
                            .addConverterFactory(GsonConverterFactory.create())
                            .baseUrl("http://ch.huyunfan.cn/PHP/user/")
                            .build();
                    mApi = retrofit.create(APi.class);
                }
            }

        }

        return mApi;

    }
}
