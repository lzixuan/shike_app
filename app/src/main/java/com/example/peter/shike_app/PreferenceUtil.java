package com.example.peter.shike_app;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;

public class PreferenceUtil {

    /**
     * 是否显示欢迎界面,true表示显示，false表示不显示
     */
    public static MyAdapter myAdapter = null;
    public static MyAdapter myAdapter2 = null;
    public static MyAdapter myAdapterloc = null;
    public static ReuseableAdapter dishAdapter = null;
    public static MyBaseExpandableListAdapter tagAdapter = null;
    public static MyAdapterforComment myAdapterforComment = null;
    public static ArrayList<Event> datas = new ArrayList<Event>();
    public static ArrayList<Event> mydatas = new ArrayList<Event>();
    public static ArrayList<Event> locdatas = new ArrayList<Event>();

    public static ArrayList<String> gData = new ArrayList<String>();
    public static ArrayList<ArrayList<Tag>> iData = new ArrayList<ArrayList<Tag>>();
    public static ArrayList<Tag> lData = new ArrayList<Tag>();
    public static ArrayList<Dish> dishDatas = new ArrayList<Dish>();
    public static ArrayList<Dish> myDishDatas = new ArrayList<Dish>();
    public static ArrayList<Dish> locDishdatas = new ArrayList<Dish>();

    public static ArrayList<Comment> commentdatas = new ArrayList<Comment>();
    public static int maptype = 0;
    public static boolean islogged = false;
    public static int userID;
    public static String username;
    public static final String SHOW_GUIDE = "showguide";

    public static final String[] canteen = {"学一食堂", "学五食堂", "艺园食堂",
            "艺园二楼", "农园一楼", "农园二楼", "农园三楼", "勺园一楼",
            "勺园二楼", "燕南食堂", "佟园食堂", "畅春园食堂", "医学部",
            "松林包子"};
    public static final String[] tag = {"清淡", "浓厚", "重口", "过咸", "过淡", "油腻",
            "酸", "甜", "辣", "苦", "鲜", "腥",
            "较硬", "较软", "较干", "有渣滓", "脆爽", "柔软", "q弹",
            "新鲜", "不新鲜", "有怪味", "焦糊", "没熟",
            "排队久",
            "物美价廉", "贵",
            "主食", "素菜", "荤菜", "甜点"};
    public static final String[] tagType = {"味道", "口感", "质量", "等待时长", "性价比", "类别"};



    public static Event getEvent(int eventID) {
        for (int i = 0; i < datas.size(); i++) {
            if (datas.get(i).getEventId() == eventID) {
                return datas.get(i);
            }
        }
        return null;
    }

    public static Dish getDish(int dishID){
        for (int i = 0; i < dishDatas.size(); i++)
        {
            if (dishDatas.get(i).getID() == dishID){
                return dishDatas.get(i);
            }
        }
        return null;
    }

    public static void deletebyID(int eventID) {
        for (int i = 0; i < datas.size(); i++) {
            if (datas.get(i).getEventId() == eventID) {
                datas.remove(i);
                break;
            }
        }
        for (int i = 0; i < mydatas.size(); i++) {
            if (mydatas.get(i).getEventId() == eventID) {
                mydatas.remove(i);
                break;
            }
        }
    }
    public static void deletebyDishID(int dishID) {
        for (int i = 0; i < dishDatas.size(); i++) {
            if (dishDatas.get(i).getID() == dishID) {
                dishDatas.remove(i);
                break;
            }
        }
        for (int i = 0; i < myDishDatas.size(); i++) {
            if (myDishDatas.get(i).getID() == dishID) {
                myDishDatas.remove(i);
                break;
            }
        }
    }

    public static int getPlace(String p) {
        for (int i = 0; i < canteen.length; i++) {
            if (canteen[i] == p)
                return i;
        }
        return -1;
    }
    /**
     * 保存到Preference
     */
    public static void setBoolean(Context context, String key, boolean value) {
        // 得到SharedPreferences
        SharedPreferences preferences = context.getSharedPreferences(
                "preference", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    /**
     * 从Preference取出数据
     */
    public static boolean getBoolean(Context context, String key) {
        SharedPreferences preferences = context.getSharedPreferences(
                "preference", Context.MODE_PRIVATE);
        // 返回key值，key值默认值是false
        return preferences.getBoolean(key, false);

    }
}
