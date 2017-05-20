package com.blogspot.hu2di.mybrowser.view;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.blogspot.hu2di.mybrowser.model.MyConstants;
import com.blogspot.hu2di.mybrowser.controller.utils.MySharePref;
import com.blogspot.hu2di.mybrowser.R;
import com.blogspot.hu2di.mybrowser.controller.adapter.RVAdapterHome;
import com.blogspot.hu2di.mybrowser.controller.adapter.RVAdapterNews;
import com.blogspot.hu2di.mybrowser.controller.utils.XMLDOMParser;
import com.blogspot.hu2di.mybrowser.controller.utils.FileUtils;
import com.blogspot.hu2di.mybrowser.controller.utils.HomePage;
import com.blogspot.hu2di.mybrowser.model.GoogleNews;
import com.blogspot.hu2di.mybrowser.model.HomeItem;
import com.thin.downloadmanager.DefaultRetryPolicy;
import com.thin.downloadmanager.DownloadRequest;
import com.thin.downloadmanager.DownloadStatusListenerV1;
import com.thin.downloadmanager.RetryPolicy;
import com.thin.downloadmanager.ThinDownloadManager;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private FileUtils fileUtils = FileUtils.getInstance(this);

    private ImageView ivQRCode;

    private RecyclerView rvHome, rvNews;
    private RVAdapterHome adapterHome;
    private RVAdapterNews adapterNews;
    private ArrayList<HomeItem> listHome;
    private ArrayList<GoogleNews> listNews;

    private LinearLayout llNews;
    private ImageView ivMoreNews;
    private Switch swNews;

    private RelativeLayout rlManageCards;
    private ImageView ivManageCards;
    private TextView tvManageCards;

    private boolean isOpenManageCard = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hd_activity_main);

        initBar();
        initView();

        initHome();
        initNews();
    }

    private void initBar() {
        ivQRCode = (ImageView) findViewById(R.id.ivQRCode);
        ivQRCode.setOnClickListener(this);
    }

    private void initView() {
        rvHome = (RecyclerView) findViewById(R.id.rvHome);
        rvHome.setHasFixedSize(true);
        RecyclerView.LayoutManager homeLayoutManager = new GridLayoutManager(this, 5);
        rvHome.setLayoutManager(homeLayoutManager);

        rvNews = (RecyclerView) findViewById(R.id.rvNews);
        rvNews.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        rvNews.setHasFixedSize(true);
        RecyclerView.LayoutManager newsLayoutManager = new LinearLayoutManager(this);
        rvNews.setLayoutManager(newsLayoutManager);
        rvNews.setItemAnimator(new DefaultItemAnimator());

        listNews = new ArrayList<>();
        adapterNews = new RVAdapterNews(this, listNews);
        rvNews.setAdapter(adapterNews);

        llNews = (LinearLayout) findViewById(R.id.llNews);
        ivMoreNews = (ImageView) findViewById(R.id.ivMoreNews);
        ivMoreNews.setOnClickListener(this);
        swNews = (Switch) findViewById(R.id.swNews);
        swNews.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    MySharePref.putIsNews(MainActivity.this, true);
                } else {
                    MySharePref.putIsNews(MainActivity.this, false);
                }
            }
        });

        rlManageCards = (RelativeLayout) findViewById(R.id.rlManageCards);
        rlManageCards.setOnClickListener(this);
        ivManageCards = (ImageView) findViewById(R.id.ivManageCards);
        tvManageCards = (TextView) findViewById(R.id.tvManageCards);
    }

    private void initHome() {
        String txtHomePath = fileUtils.getAppDirPath() + "homepage";
        File f = new File(txtHomePath);
        if (f.exists()) {
            setHome(txtHomePath);
        } else {
            downloadHome(txtHomePath);
        }
    }

    private void downloadHome(final String txtHomePath) {
        Uri downloadUri = Uri.parse(MyConstants.URL_HOME);
        RetryPolicy retryPolicy = new DefaultRetryPolicy();
        Uri destinationUri = Uri.parse(txtHomePath);
        final DownloadRequest downloadRequest = new DownloadRequest(downloadUri)
                .setDestinationURI(destinationUri).setPriority(DownloadRequest.Priority.IMMEDIATE)
                .setRetryPolicy(retryPolicy)
                .setStatusListener(new DownloadStatusListenerV1() {
                    @Override
                    public void onDownloadComplete(DownloadRequest downloadRequest) {
                        setHome(txtHomePath);
                    }

                    @Override
                    public void onDownloadFailed(DownloadRequest downloadRequest, int errorCode, String errorMessage) {
                    }

                    @Override
                    public void onProgress(DownloadRequest downloadRequest, long totalBytes, long downloadedBytes, int progress) {
                    }
                });
        ThinDownloadManager downloadManager = new ThinDownloadManager();
        downloadManager.add(downloadRequest);
    }

    private void setHome(String homePath) {
        listHome = HomePage.getHomeFromFile(homePath);
        adapterHome = new RVAdapterHome(this, listHome);
        rvHome.setAdapter(adapterHome);
    }

    private void initNews() {
        if (MySharePref.getIsNews(this)) {
            llNews.setVisibility(View.VISIBLE);
            ivMoreNews.setVisibility(View.VISIBLE);
            swNews.setChecked(true);
            swNews.setVisibility(View.GONE);
            rvNews.setVisibility(View.VISIBLE);

            runRSS();
        } else {
            llNews.setVisibility(View.GONE);
            ivMoreNews.setVisibility(View.GONE);
            swNews.setChecked(false);
            swNews.setVisibility(View.VISIBLE);
            rvNews.setVisibility(View.GONE);
        }
    }

    private void runRSS() {
        String country_Code = "";

        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String countryTelephone = tm.getSimCountryIso().toLowerCase();

        String countryCode = this.getResources().getConfiguration().locale.getCountry().toLowerCase();
        String countryLanguage = this.getResources().getConfiguration().locale.getCountry().toLowerCase();

        if (!countryTelephone.equals("")) {
            int count = 0;
            for (int i = 0; i < MyConstants.COUNTRY_CODE.length; i++) {
                if (countryTelephone.equals(MyConstants.COUNTRY_CODE[i].substring(MyConstants.COUNTRY_CODE[i].length() - 2, MyConstants.COUNTRY_CODE[i].length()))) {
                    country_Code = MyConstants.COUNTRY_CODE[i];
                    count++;
                }
            }
            if (count == 2) {
                if (countryLanguage.equals("us")) country_Code = "us";
                else country_Code = "es_us";
            }
        } else {
            int count = 0;
            for (int i = 0; i < MyConstants.COUNTRY_CODE.length; i++) {
                if (countryCode.equals(MyConstants.COUNTRY_CODE[i].substring(MyConstants.COUNTRY_CODE[i].length() - 2, MyConstants.COUNTRY_CODE[i].length()))) {
                    country_Code = MyConstants.COUNTRY_CODE[i];
                    count++;
                }
                if (count == 2) {
                    if (countryLanguage.equals("us")) country_Code = "us";
                    else country_Code = "es_us";
                }
            }
        }

        String url = MyConstants.URL_GOOGLE_ALL + country_Code + MyConstants.URL_GOOGLE_ALL_2;
        new ReadRSS().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
    }

    private class ReadRSS extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... strings) {
            return ReadRSS(strings[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            if (s != null) {
                listNews.clear();

                XMLDOMParser parser = new XMLDOMParser();
                Document document = parser.getDocument(s);
                NodeList nodeList = document.getElementsByTagName("item");

                for (int i = 0; i < nodeList.getLength(); i++) {
                    Element element = (Element) nodeList.item(i);
                    GoogleNews googleNews = new GoogleNews(parser.getValue(element, "category"), parser.getValue(element, "title"), parser.getValue(element, "link"), parser.getDescription(element, "description"), parser.getValue(element, "pubDate"));
                    listNews.add(googleNews);
                }

                adapterNews.notifyDataSetChanged();
            }
        }
    }

    private String ReadRSS(String theUrl) {
        StringBuilder content = new StringBuilder();
        try {
            URL url = new URL(theUrl);
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                content.append(line + "\n");
            }
            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return content.toString();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivQRCode:
                //scanQRCode();
                scanZxing();
                break;
            case R.id.ivMoreNews:
                break;
            case R.id.rlManageCards:
                if (isOpenManageCard) {
                    isOpenManageCard = false;
                    ivManageCards.setImageResource(R.drawable.hd_manage_cards);
                    tvManageCards.setText(getString(R.string.manage_cards));
                    tvManageCards.setTextColor(getResources().getColor(R.color.gray3));

                    initNews();
                } else {
                    isOpenManageCard = true;
                    ivManageCards.setImageResource(R.drawable.hd_manage_cards_done);
                    tvManageCards.setText(getString(R.string.done));
                    tvManageCards.setTextColor(getResources().getColor(R.color.blue));

                    llNews.setVisibility(View.VISIBLE);
                    ivMoreNews.setVisibility(View.GONE);
                    swNews.setVisibility(View.VISIBLE);
                    rvNews.setVisibility(View.GONE);
                }
                break;
        }
    }

    private void scanZxing() {
        startActivity(new Intent(MainActivity.this, ScanQRActivity.class));
    }
}

