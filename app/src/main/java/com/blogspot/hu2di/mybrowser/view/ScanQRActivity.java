package com.blogspot.hu2di.mybrowser.view;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.hu2di.mybrowser.R;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * Created by HUNGDH on 5/18/2017.
 */

public class ScanQRActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler, View.OnClickListener {

    private ZXingScannerView mScannerView;
    private boolean isFlash = false;

    private ImageView ivBack, ivFlash;

    private TextView tvAlbum;
    private final int PICK_IMAGE_REQUEST = 0;

    private RelativeLayout rlResult;
    private TextView tvResult;
    private Button btnSearch, btnCopy, btnBack;
    private boolean isResult = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hd_activity_scan_qrcode);

        initView();
        initResult();
    }

    private void initView() {
        ViewGroup contentFrame = (ViewGroup) findViewById(R.id.content_frame);
        mScannerView = new ZXingScannerView(this);
        contentFrame.addView(mScannerView);

        ivBack = (ImageView) findViewById(R.id.ivBack);
        ivBack.setOnClickListener(this);

        tvAlbum = (TextView) findViewById(R.id.tvAlbum);
        tvAlbum.setOnClickListener(this);

        ivFlash = (ImageView) findViewById(R.id.ivFlash);
        ivFlash.setOnClickListener(this);
    }

    private void initResult() {
        rlResult = (RelativeLayout) findViewById(R.id.rlResult);
        tvResult = (TextView) findViewById(R.id.tvResult);
        btnSearch = (Button) findViewById(R.id.btnSearch);
        btnCopy = (Button) findViewById(R.id.btnCopy);
        btnBack = (Button) findViewById(R.id.btnBack);

        rlResult.setVisibility(View.GONE);
        btnSearch.setOnClickListener(this);
        btnCopy.setOnClickListener(this);
        btnBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivBack:
                if (!isResult) {
                    finish();
                } else {
                    rlResult.setVisibility(View.GONE);
                }
                isResult = false;
                break;
            case R.id.tvAlbum:
                getImageAlbum();
                break;
            case R.id.ivFlash:
                isFlash = !isFlash;
                mScannerView.setFlash(isFlash);
                break;

            case R.id.btnSearch:
                search(tvResult.getText().toString());
                break;
            case R.id.btnCopy:
                copy(tvResult.getText().toString());
                break;
            case R.id.btnBack:
                rlResult.setVisibility(View.GONE);
                break;
        }
    }

    private void getImageAlbum() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            getCodeFromImage(uri);
        }
    }

    private void getCodeFromImage(Uri uri) {
        new GetQRAsync().execute(uri);
    }

    private class GetQRAsync extends AsyncTask<Uri, Void, String> {

        @Override
        protected String doInBackground(Uri... uris) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uris[0]);
                int width = bitmap.getWidth(), height = bitmap.getHeight();
                int[] pixels = new int[width * height];
                bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
                bitmap.recycle();
                RGBLuminanceSource source = new RGBLuminanceSource(width, height, pixels);
                BinaryBitmap bBitmap = new BinaryBitmap(new HybridBinarizer(source));
                MultiFormatReader reader = new MultiFormatReader();
                Result result = reader.decode(bBitmap);
                return result.getText();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null) {
                showResult(s);
            } else {
                Toast.makeText(ScanQRActivity.this, getString(R.string.cannot_detect_qr), Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(Result rawResult) {
        // Do something with the result here
        Log.v("myLog", rawResult.getText()); // Prints scan results
        Log.v("myLog", rawResult.getBarcodeFormat().toString()); // Prints the scan format (hd_qrcode, pdf417 etc.)

        // If you would like to resume scanning, call this method below:
        mScannerView.resumeCameraPreview(this);

        showResult(rawResult.getText());
    }

    private void showResult(String result) {
        rlResult.setVisibility(View.VISIBLE);
        isResult = true;

        tvResult.setText(result);
    }

    private void search(String text) {

    }

    private void copy(String text) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(getString(R.string.copied), text);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, getString(R.string.copied), Toast.LENGTH_SHORT).show();
        finish();
    }
}
