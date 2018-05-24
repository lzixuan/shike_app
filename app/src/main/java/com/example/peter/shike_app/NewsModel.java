package com.example.peter.shike_app;

import com.orhanobut.logger.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import okhttp3.ResponseBody;

public class NewsModel {

//    public static void getNews(String number, String page) {
//        APi api = Network.getInstance().getApi();
//        Call<ResponseBody> news = api.getNews(number, page);
//        news.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                ResponseBody body = response.body();
//                Logger.i("onResponse:   ="+body.toString());
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                Logger.i("onResponse:   ="+t.getMessage());
//
//            }
//        });
//
//    }
//
//    public static void postNews(String number, String page) {
//        APi api = Network.getInstance().getApi();
//        Call<ResponseBody> news = api.getMessage(number, page);
//        news.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                ResponseBody body = response.body();
//                Logger.i("onResponse:   ="+body.toString());
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                Logger.i("onResponse:   ="+t.getMessage());
//
//            }
//        });
//
//    }

//
//    public static void getTiYu(String type, String number, String page ){
//        APi api = Network.getInstance().getApi();
//        Call<News> news = api.tiYu(type,number, page);
//        news.enqueue(new Callback<News>() {
//            @Override
//            public void onResponse(Call<News> call, Response<News> response) {
//
//                News body = response.body();
//                Logger.i("onResponse:   ="+body.toString());
//            }
//
//            @Override
//            public void onFailure(Call<News> call, Throwable t) {
//
//                Logger.i("onResponse:   ="+t.getMessage());
//            }
//        });
//    }
}
