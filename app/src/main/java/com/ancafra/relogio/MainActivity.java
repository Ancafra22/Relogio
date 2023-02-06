package com.ancafra.relogio;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private ViewHolder mViewHolder = new ViewHolder();
    private Runnable mRunnable;
    private Handler mHandler = new Handler();
    private boolean mTicker = false;

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
        this.startClock();
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.mTicker = false;
        this.unregisterReceiver(this.mReceiver);
    }

    private void startClock() {

        Calendar calendar = Calendar.getInstance();


        this.mRunnable = new Runnable() {
            @Override
            public void run() {

                if(!mTicker) {
                    return;
                }

                calendar.setTimeInMillis(System.currentTimeMillis());

                int hour = calendar.get(calendar.HOUR_OF_DAY);
                int minute = calendar.get(calendar.MINUTE);
                int second = calendar.get(calendar.SECOND);

                mViewHolder.textHourMinute.setText(String.format(Locale.getDefault(),"%d:%02d", hour, minute));
                mViewHolder.textSecond.setText(String.format(Locale.getDefault(),"%02d", second));

                long now = SystemClock.elapsedRealtime();
                long next = now + (1000 - (now % 1000));
                mHandler.postAtTime(mRunnable, next);
            }
        };
        mRunnable.run();
    }

    public static class ViewHolder{
        TextView textHourMinute;
        TextView textSecond;
        TextView textBattery;
    }

    private void initComponents() {
        this.mViewHolder.textHourMinute = findViewById(R.id.text_hour_minute);
        this.mViewHolder.textSecond = findViewById(R.id.text_seconds);
        this.mViewHolder.textBattery = findViewById(R.id.text_battery);

        this.mViewHolder.textHourMinute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),DetailsActivity.class));
            }
        });
    }
}