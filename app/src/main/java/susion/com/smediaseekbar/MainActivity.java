package susion.com.smediaseekbar;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import susion.com.mediaseekbar.MediaSeekBar;
import susion.com.smediaseekbar.R;

public class MainActivity extends AppCompatActivity {


    private int[] progress1 = new int[2];
    private int[] progress2 = new int[2];
    private int[] progress3 = new int[2];
    private int[] progress4 = new int[2];

    private MediaSeekBar seekBar1;
    private MediaSeekBar seekBar2;
    private MediaSeekBar seekBar3;
    private MediaSeekBar seekBar4;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

            seekBar1.setCurrentProgress(progress1[0]);seekBar1.setHasBufferProgress(progress1[1]);
            seekBar2.setCurrentProgress(progress2[0]);seekBar2.setHasBufferProgress(progress2[1]);
            seekBar3.setCurrentProgress(progress3[0]);seekBar3.setHasBufferProgress(progress3[1]);
            seekBar4.setCurrentProgress(progress4[0]);seekBar4.setHasBufferProgress(progress4[1]);


        }
    };
    private Random random;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        seekBar1 = (MediaSeekBar) findViewById(R.id.msb1);
        seekBar2 = (MediaSeekBar) findViewById(R.id.msb2);
        seekBar3 = (MediaSeekBar) findViewById(R.id.msb3);
        seekBar4 = (MediaSeekBar) findViewById(R.id.msb4);
        seekBar1.setMaxProgress(100);
        random = new Random();


        seekBar1.setMediaSeekBarListener(new MediaSeekBar.MediaSeekBarListener() {
            @Override
            public void onThumbClick() {
                Toast.makeText(MainActivity.this, "thumb被点击", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStartDragThumb(int currentProgress) {
                Toast.makeText(MainActivity.this, "thumb被开始拖拽", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDraggingThumb(int currentProgress) {
                Toast.makeText(MainActivity.this, "thumb正在被拖拽", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStopDragThumb(int cureentProgress) {
                Toast.makeText(MainActivity.this, "thumb停止拖拽", Toast.LENGTH_SHORT).show();
            }


            @Override
            public void onProgressChange(int currentProgress) {
                Toast.makeText(MainActivity.this, "进度条改变", Toast.LENGTH_SHORT).show();

            }

        });

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                progress1[0] = seekBar1.getCurrentProgress() + 1; progress1[1] = seekBar1.getHasBufferProgress() + random.nextInt(20);
                progress2[0] = seekBar2.getCurrentProgress() + 1; progress2[1] = seekBar2.getHasBufferProgress() + random.nextInt(20);
                progress3[0] = seekBar3.getCurrentProgress() + 1; progress3[1] = seekBar3.getHasBufferProgress() + random.nextInt(20);
                progress4[0] = seekBar4.getCurrentProgress() + 1; progress4[1] = seekBar4.getHasBufferProgress() + random.nextInt(20);
                handler.sendEmptyMessage(0);

            }

        }, 1000, 1000);


    }
}
