package com.moreinfo.rsa;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import util.Base64Utils;
import util.MD5Util;
import util.RSAUtils;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static String PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDfsNLocbsmtQcAu0r8iDE2xxsa\n" +
            "p2vzySdzqqoV546JQBVRy3k6wGG0QSL/kbKwGTPJD83Ls/kMlAazPERdFWT6thts\n" +
            "ZPkTxFNiiYX3OhJsa2ex57Y0DOeID+dTL67z8Jb0TQ1Eb8tavVKVRsiYmGbfe8Vm\n" +
            "uoBHGkD1ZKVTtiKE4QIDAQAB";
    private static String PRIVATE_KEY = "MIICXgIBAAKBgQDfsNLocbsmtQcAu0r8iDE2xxsap2vzySdzqqoV546JQBVRy3k6\n" +
            "wGG0QSL/kbKwGTPJD83Ls/kMlAazPERdFWT6thtsZPkTxFNiiYX3OhJsa2ex57Y0\n" +
            "DOeID+dTL67z8Jb0TQ1Eb8tavVKVRsiYmGbfe8VmuoBHGkD1ZKVTtiKE4QIDAQAB\n" +
            "AoGAY2mscg2JhUxSzmpuiwjkBlo6m3/opDGzzN3+NTnxyOGoF+/78rZpNbj1Guvf\n" +
            "pA+vSh4x8eQMPSDUUXfpUg0eENFnE1Ac0KSxafXzUPDX9I/+kWncl7QMsGe4qGS6\n" +
            "dyWHyVHyXQNEHBNDOU/dXUSRc9GqIRsWAXDh0UXLnD0hoZkCQQD3gQYvsX+T/dGS\n" +
            "UuEOwOHXlQ+5UWwma3PyKkUG1bsZ/e+dtj0nppUUHh/XsZGBfSmp7jeorQlZl52P\n" +
            "oprr76m/AkEA516Jg3ON6uFCW9yyUfvnE9bXDfFzQLaTqg+ItO0oUHIb5CrBsMxE\n" +
            "x1vUhpG8PVV3ADbAxQN9icl5zDamOFk5XwJBAITc6WI+6ZqbPZ5MCW6vEk11Qmyc\n" +
            "De+N7T8Je+aQY5EvZSsHgkq/vPWAul1CgDa/5tbG/2/3+7Xxku5hPsL68+cCQQCH\n" +
            "OwFC/KpJS4n+qTrsokBInzypIKPNgilCr7umjCsRAfD9ZmQ5UtssbqcEVG/ipW5d\n" +
            "iKMZQ9X/T+xJiPOUYcofAkEAzXB2vxCFzZCYvXrGCiS8lYh9Krzta/CpfL5objjW\n" +
            "B88Dh5kKBHYIJ1g1SseEUoZ1wWQEGd1fpkU9tDwfYZLGOg==";


    private String afterencrypt;

    private Button btn_login;  //登录按钮

    private EditText et_number;  //编号

    private String timestamp;  //时间戳

    private String appkey;  //appkey

    private String appsecret;  //appsecret

    private String sign;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_login = findViewById(R.id.btn_login);
        et_number = findViewById(R.id.et_number);

        btn_login.setOnClickListener(this);



    }


    private void complexForData() {

        new Thread() {
            @Override
            public void run() {
                //1,创建对象
                OkHttpClient okHttpClient = new OkHttpClient();
                Log.e("密文123",afterencrypt);
                //2，创建请求对象
                RequestBody body = new FormBody.Builder()
                        .add("prisoner_number",afterencrypt)
                        .add("sign",sign)
                        .add("timestamp",timestamp)
                        .add("appkey",appkey)
                        .build();

                Request request = new Request.Builder()
                        .url("https://nw.biyi520.net/open/api/login")
                        .post(body)
                        .build();
                //3，创建call
                Call call = okHttpClient.newCall(request);
                //4,提交请求返回响应
                try {
                    Response response = call.execute();
                    Log.e("msg",response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();


    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btn_login:
                String source = et_number.getText().toString().trim();
                try {
                    // 从字符串中得到公钥
                    PublicKey publicKey = RSAUtils.loadPublicKey(PUBLIC_KEY);
                    // 从文件中得到公钥
                    //InputStream inPublic = getResources().getAssets().open("rsa_public_key.pem");
                    //PublicKey publicKey = RSAUtils.loadPublicKey(inPublic);
                    // 加密
                    byte[] encryptByte = RSAUtils.encryptData(source.getBytes(), publicKey);
                    // 为了方便观察把加密后的数据用base64加密转一下，要不然看起来是乱码,所以解密是也是要用Base64先转换
                    afterencrypt = Base64Utils.encode(encryptByte);

                    Log.e("rsa", "密文：" + afterencrypt);

                    //获取当前时间戳
                    timestamp = String.valueOf(new Date().getTime());

                    //appkey
                    appkey = "3XW4TQVKz2aiMJrOtYtThYP10i5wzT71";

                    //appsecret
                    appsecret = "8T9QOupfErLQcdvALL8VfdZnsLUbWJEjyrRoXgY2gFRSj34c4MeOGG0JInUWhcDy";


                    //1,拼接字符串
                    String encode = "appkey=" + appkey + "&prisoner_number=" + afterencrypt + "&timestamp=" + timestamp + appsecret;

                    //2,urlencode
                    String resultencode = URLEncoder.encode(encode,"GBK");

                    //3,MD5加密
                    sign = MD5Util.getMD5Str(resultencode);

                    //提交表单，执行登录
                    complexForData();

//                    OkHttpClient okHttpClient = new OkHttpClient();
//                    RequestBody body = new FormBody.Builder()
//                            .add("prisoner_number",afterencrypt)
//                            .add("sign","31681f184f37caecc44389dbd1d5d90f")
//                            .add("timestamp","1589774814")
//                            .build();
//
//                    final Request request = new Request.Builder()
//                            .url("http://nw.biyi520.net//open/api/login")
//                            .post(body)
//                            .build();
//
//                    Call call = okHttpClient.newCall(request);
//                    Response response = call.execute();
//                    String responsedata = response.body().string();
//
//                    Toast.makeText(this,responsedata,Toast.LENGTH_LONG);

//                    //点击加密，访问url
//                    String url = "http://nw.biyi520.net//open/api/login?prisoner_number=" + afterencrypt + "&timestamp=1589772081" + "&sign=31681f184f37caecc44389dbd1d5d90f" + "&appkey=3XW4TQVKz2aiMJrOtYtThYP10i5wzT71";
//
//                    //隐藏控件
//                    btn_encryption.setVisibility(btn_encryption.GONE);
//                    btn_decrypt.setVisibility(btn_decrypt.GONE);
//                    et1.setVisibility(et1.GONE);
//                    et2.setVisibility(et2.GONE);
//                    et3.setVisibility(et3.GONE);
//
//                    webView.loadUrl(url);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
//            case R.id.btn_decrypt:
//                String encryptContent = et2.getText().toString().trim();
//                try {
//                    // 从字符串中得到私钥
//                    PrivateKey privateKey = RSAUtils.loadPrivateKey(PRIVATE_KEY);
//                    // 从文件中得到私钥
//                    //InputStream inPrivate = getResources().getAssets().open("rsa_private_key.pem");
//                    //PrivateKey privateKey = RSAUtils.loadPrivateKey(inPrivate);
//                    // 因为RSA加密后的内容经Base64再加密转换了一下，所以先Base64解密回来再给RSA解密
//                    byte[] decryptByte = RSAUtils.decryptData(Base64Utils.decode(encryptContent), privateKey);
//                    String decryptStr = new String(decryptByte);
//                    et3.setText(decryptStr);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//                break;
        }

    }



//    @SuppressLint("SetJavaScriptEnabled")
//    public void setWebView() {
//        WebView.setWebContentsDebuggingEnabled(true);
//        webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
//        webView.getSettings().setJavaScriptEnabled(true);
//        webView.getSettings().setLoadWithOverviewMode(true);
//        webView.getSettings().setUseWideViewPort(true);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            webView.getSettings().setOffscreenPreRaster(true);
//        }
//
//        webView.getSettings().setDomStorageEnabled(true);
//        webView.getSettings().setDisplayZoomControls(false);
//        webView.getSettings().setBuiltInZoomControls(false);
//
//        webView.getSettings().setTextZoom(100);
//
//        webView.getSettings().setAppCacheEnabled(false);
//        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
//        webView.getSettings().setAllowFileAccess(true);
//
//        webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
//        webView.getSettings().setAllowFileAccessFromFileURLs(true);
//        webView.getSettings().setSavePassword(false);
//
//        //移除部分系统Javascript接口
//        webView.removeJavascriptInterface("searchBoxJavaBridge_");
//        webView.removeJavascriptInterface("accessibility");
//        webView.removeJavascriptInterface("accessibilityTraversal");
//
//        webView.removeJavascriptInterface("");
//        webView.getSettings().setLoadsImagesAutomatically(true);
//
//        webView.getSettings().setGeolocationEnabled(true);
//        webView.getSettings().setDomStorageEnabled(true);
//        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
//        webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
//        webView.isHorizontalScrollBarEnabled();
//        webView.isVerticalScrollBarEnabled();
//
//        webView.setWebChromeClient(new WebChromeClient());
//        webView.setWebViewClient(new WebViewClient());
//
//    }
//
//    @Override
//    public void onBackPressed() {
//        if (webView.canGoBack()) {
//            webView.goBack();
//        } else {
//            super.onBackPressed();
//        }
//
//
//    }
}
