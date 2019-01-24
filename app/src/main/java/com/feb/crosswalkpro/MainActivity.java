package com.feb.crosswalkpro;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;

import org.xwalk.core.XWalkActivity;
import org.xwalk.core.XWalkSettings;
import org.xwalk.core.XWalkUIClient;
import org.xwalk.core.XWalkView;

public class MainActivity extends XWalkActivity {
    private XWalkView walkview;

    private ValueCallback mUploadMessage;
    private final int FILE_SELECTED = 10000;

    @Override
    protected void onXWalkReady() {
        XWalkSettings setting = walkview.getSettings();
        setting.setLoadWithOverviewMode(false);
        setting.setJavaScriptEnabled(true);//支持js
        setting.setJavaScriptCanOpenWindowsAutomatically(true);  //支持通过JS打开新窗口
        setting.setUseWideViewPort(true);                          //将图片调整到合适webview的大小
        setting.setLoadWithOverviewMode(true);                   //缩放至屏幕的大小
        setting.setLoadsImagesAutomatically(true);                 //支持自动加载图片
        setting.setSupportMultipleWindows(true);                        //支持多窗口
        setting.setSupportZoom(true);
        setting.setAllowFileAccess(true);
        setting.setDomStorageEnabled(true);
        setting.setAllowContentAccess(true);
        setting.setDomStorageEnabled(true);
        walkview.requestFocus();
        setting.setCacheMode(WebSettings.LOAD_NO_CACHE);
        walkview.setUIClient(new XWalkUIClient(walkview) {
            @Override
            public void openFileChooser(XWalkView view, ValueCallback<Uri> uploadFile, String acceptType, String capture) {
                super.openFileChooser(view, uploadFile, acceptType, capture);
                if (mUploadMessage != null) return;
                mUploadMessage = uploadFile;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("*/*");
                MainActivity.this.startActivityForResult(Intent.createChooser(i, "选择图片"), FILE_SELECTED);

            }
        });
        walkview.loadUrl("http://10.20.3.182:8020/test2/2.html");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        walkview = findViewById(R.id.walkview);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (walkview != null) {
            walkview.onActivityResult(requestCode, resultCode, data);
        }
        if (requestCode == FILE_SELECTED) {
            if (null == mUploadMessage) return;
            Uri result = data == null || resultCode != RESULT_OK ? null
                    : data.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
            return;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (walkview != null) {
            walkview.onDestroy();
        }
    }
}
