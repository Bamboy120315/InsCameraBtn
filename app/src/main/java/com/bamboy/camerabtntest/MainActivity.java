package com.bamboy.camerabtntest;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.animation.LinearInterpolator;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bamboy.camerabtntest.camerabtn.CameraBtn;

public class MainActivity extends AppCompatActivity {

    private CameraBtn cb_circular_ring;
    private SeekBar sb_seek;
    private TextView tv_state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cb_circular_ring = findViewById(R.id.cb_circular_ring);
        sb_seek = findViewById(R.id.sb_seek);
        tv_state = findViewById(R.id.tv_state);

        // 每完成一圈的回调
        cb_circular_ring.setOnCameraBtnListener(num -> tv_state.setText("第" + num + "圈完成"));

        // 拖动监听
        sb_seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                cb_circular_ring.setProgress(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        tv_state.setOnClickListener(view -> {
            ObjectAnimator anim = ObjectAnimator.ofInt(cb_circular_ring, "progress", 0, 3000);
            anim.setDuration(25000);
            anim.setInterpolator(new LinearInterpolator());
            anim.start();
        });
    }
}