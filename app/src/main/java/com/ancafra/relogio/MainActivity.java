package com.ancafra.relogio;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private ViewHolder mViewHolder = new ViewHolder();
    private Runnable mRunnable;
    private Handler mHandler = new Handler();
    private boolean mTicker = false;
    private boolean mLandscape = false;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            mViewHolder.textBattery.setText(String.format("%d%%", level));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.initComponents();

    }

    @Override
    protected void onResume() {
        super.onResume();
        this.registerReceiver(this.mReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        this.mTicker = true;
        this.mLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;

        this.startClock();
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.mTicker = false;
        this.unregisterReceiver(this.mReceiver);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_IMMERSIVE
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
            );
        }
    }

    private void showSystemUi() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );
    }

    private void startClock() {

        Calendar calendar = Calendar.getInstance();


        this.mRunnable = new Runnable() {
            @Override
            public void run() {

                if (!mTicker) {
                    return;
                }

                calendar.setTimeInMillis(System.currentTimeMillis());

                int hour = calendar.get(calendar.HOUR_OF_DAY);
                int minute = calendar.get(calendar.MINUTE);
                int second = calendar.get(calendar.SECOND);

                mViewHolder.textHourMinute.setText(String.format(Locale.getDefault(), "%d:%02d", hour, minute));
                mViewHolder.textSecond.setText(String.format(Locale.getDefault(), "%02d", second));

                if (mLandscape) {
                    if (hour >= 03 && hour <= 12) {
                        mViewHolder.textNight.setText("Good Morning");
                    } else if (hour > 12 && hour <= 18) {
                        mViewHolder.textNight.setText("Good Afternoon");
                    } else {
                        mViewHolder.textNight.setText("Good Night");
                    }
                }

                long now = SystemClock.elapsedRealtime();
                long next = now + (1000 - (now % 1000));
                mHandler.postAtTime(mRunnable, next);
            }
        };
        mRunnable.run();
    }

    public static class ViewHolder {
        TextView textHourMinute;
        TextView textSecond;
        TextView textBattery;
        TextView textNight;
    }

    private void initComponents() {

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        this.mViewHolder.textHourMinute = findViewById(R.id.text_hour_minute);
        this.mViewHolder.textSecond = findViewById(R.id.text_seconds);
        this.mViewHolder.textBattery = findViewById(R.id.text_battery);
        this.mViewHolder.textNight = findViewById(R.id.textNight);

        this.mViewHolder.textHourMinute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSystemUi();
            }
        });

    }
}