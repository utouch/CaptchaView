package com.captchaview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.widget.Toast;

import com.captcha.CaptchaView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CaptchaView captchaView = (CaptchaView) findViewById(R.id.captchaView);
        //设置间距
        captchaView.setDivideWidth(20);
        //设置输入框个数
        captchaView.setNumber(5);
        //设置显示位置
        captchaView.setGravity(Gravity.CENTER);
        //输入类型过滤
        //这里InputType必须为 CaptchaView.INPUT_TYPE_NUMBER_TEXT
        captchaView.setInputType(CaptchaView.INPUT_TYPE_NUMBER_TEXT, "*1234@");
        captchaView.setOnInputCompleteListener(new CaptchaView.OnInputCompleteCallback() {
            @Override
            public void onInputCompleteListener(String captcha) {
                Toast.makeText(MainActivity.this, captcha, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(MainActivity.this, "这里不能输入 " + error, Toast.LENGTH_LONG).show();
            }
        });
    }
}
