package com.quickembed.car.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.quickembed.car.BaseApplication;
import com.quickembed.car.R;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by allen_meng on 2018/8/8.
 * 短信发送验证码倒计时按钮
 */

@SuppressLint("AppCompatCustomView")
public class CountTimeButton extends TextView implements View.OnClickListener{

    private long lenght = 60 * 1000;// 倒计时长度,这里给了默认60秒
    private String textafter = "已发送";
    private String textbefore = "获取验证码";
    private int colorafter = R.color.white;  //默认白色
    private int colorbefore = R.color.white; //默认白色
    private final String TIME = "time";
    private final String CTIME = "ctime";
    private OnClickListener mOnclickListener;
    private Timer t;
    private TimerTask tt;
    private long time;
    Map<String, Long> map = new HashMap<String, Long>();

    public void setColorafter(int colorafter) {
        this.colorafter = colorafter;
    }

    public void setColorbefore(int colorbefore) {
        this.colorbefore = colorbefore;
    }

    public CountTimeButton(Context context) {
        super(context);
        setOnClickListener(this);
    }

    public CountTimeButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        setOnClickListener(this);
    }

    @SuppressLint("HandlerLeak")
    Handler han = new Handler() {
        public void handleMessage(android.os.Message msg) {
            CountTimeButton.this.setText(textafter + time / 1000 + "s");
            time -= 1000;
            if (time < 0) {
                CountTimeButton.this.setEnabled(true);
                CountTimeButton.this.setText(textbefore);
                CountTimeButton.this.setTextColor(getResources().getColor(colorbefore));
                clearTimer();
            }
        };
    };

    private void initTimer() {
        time = lenght;
        t = new Timer();
        tt = new TimerTask() {

            @Override
            public void run() {
                Logger.e("allen", time / 1000 + "");
                han.sendEmptyMessage(0x01);//十六进制的数字1
            }
        };
    }

    private void clearTimer() {
        if (tt != null) {
            tt.cancel();
            tt = null;
        }
        if (t != null)
            t.cancel();
        t = null;
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        if (l instanceof CountTimeButton) {
            super.setOnClickListener(l);
        } else
            this.mOnclickListener = l;
    }

    @Override
    public void onClick(View v) {
        if (mOnclickListener != null)
            mOnclickListener.onClick(v);
        initTimer();
        this.setText(textafter + time / 1000 +"s");
        this.setEnabled(false);
        this.setTextColor(getResources().getColor(colorafter));
        t.schedule(tt, 0, 1000);
    }

    /**
     * 和activity的onDestroy()方法同步
     */
    public void onDestroy() {
        if (BaseApplication.map == null)
            BaseApplication.map = new HashMap<String, Long>();
        BaseApplication.map.put(TIME, time);
        BaseApplication.map.put(CTIME, System.currentTimeMillis());
        clearTimer();
        Logger.e("allen", "onDestroy");
    }

    /**
     * 和activity的onCreate()方法同步
     */
    public void onCreate(Bundle bundle) {
        Logger.e("倒计时相关", BaseApplication.map + "");
        if (BaseApplication.map == null)
            return;
        if (BaseApplication.map.size() <= 0)// 这里表示没有上次未完成的计时
            return;
        long time = System.currentTimeMillis() - BaseApplication.map.get(CTIME)
                - BaseApplication.map.get(TIME);
        BaseApplication.map.clear();
        if (time > 0)
            return;
        else {
            initTimer();
            this.time = Math.abs(time);
            t.schedule(tt, 0, 1000);
            this.setText(time + textafter);
            this.setEnabled(false);
        }
    }

    /** * 设置计时时候显示的文本 */
    public CountTimeButton setTextAfter(String text1) {
        this.textafter = text1;
        return this;
    }

    /** * 设置点击之前的文本 */
    public CountTimeButton setTextBefore(String text0) {
        this.textbefore = text0;
        this.setText(textbefore);
        return this;
    }

    /**
     * 设置到计时长度
     * @param lenght
     * 时间 默认毫秒
     * @return
     */
    public CountTimeButton setLenght(long lenght) {
        this.lenght = lenght;
        return this;
    }


}
