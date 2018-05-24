package com.example.peter.shike_app;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * @ explain:
 * @ author：xujun on 2016-8-25 15:06
 * @ email：gdutxiaoxu@163.com
 */
public interface APi {

    @Headers({"Content-Type: application/json","Accept: application/json"})//需要添加头
    @POST ("login.php")
    Call<ResponseBody>getMessage(@Body RequestBody info);   // 请求体味RequestBody 类型

//    @Headers({"apikey:81bf9da930c7f9825a3c3383f1d8d766" ,"Content-Type:application/json"})
//    @GET("login.php")
//    Call<ResponseBody> getNews(@Query("userName") String userName, @Query("password") String password);
//
//    @FormUrlEncoded
//    @Headers({"Content-Type:application/json"})
//    @POST("login.php")
//    Call<ResponseBody> postNews(@Field("userName") String userName, @Field("password") String password);
//
//    @Headers({"apikey:81bf9da930c7f9825a3c3383f1d8d766" ,"Content-Type:application/json"})
//    @GET("{type}/{type}")
//    Call<ResponseBody> tiYu(@Path("type") String type, @Query("num") String num, @Query("page") String page);
//
//    @Headers({"apikey:81bf9da930c7f9825a3c3383f1d8d766" ,"Content-Type:application/json"})
//    @GET("{type1}/{type2}")
//    Call<ResponseBody> tiYu(@Path("type1") String type1, @Path("type2") String type2, @Query("num") String num, @Query("page") String page);
//
//    @FormUrlEncoded
//    @Headers({"apikey:81bf9da930c7f9825a3c3383f1d8d766" ,"Content-Type:application/json"})
//    @POST("keji/keji")
//    Call<ResponseBody> keji(@Query("num") String num, @Query("page") String page);

}
