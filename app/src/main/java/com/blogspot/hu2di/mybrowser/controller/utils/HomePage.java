package com.blogspot.hu2di.mybrowser.controller.utils;

import com.blogspot.hu2di.mybrowser.model.HomeItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

/**
 * Created by HUNGDH on 5/17/2017.
 */

public class HomePage {

    public static ArrayList<HomeItem> getHomeFromFile(String txtHomePath) {
        ArrayList<HomeItem> list = new ArrayList<HomeItem>();

        StringBuilder sb = new StringBuilder();
        try {
            File file = new File(txtHomePath);
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append('\n');
            }
            br.close();

            JSONObject root = new JSONObject(sb.toString());
            JSONArray items = root.getJSONArray("hompage");

            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);
                String titlelist = item.getString("titlelist");
                String iconfooter = item.getString("iconfooter");
                JSONArray listWeb = item.getJSONArray("list");

                for (int j = 0; j < listWeb.length(); j++) {
                    JSONObject itemWeb = listWeb.getJSONObject(j);
                    String title = itemWeb.getString("title");
                    String url = itemWeb.getString("url");
                    String icon = itemWeb.getString("icon");

                    HomeItem homeItem = new HomeItem(title, icon, url);
                    list.add(homeItem);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}
