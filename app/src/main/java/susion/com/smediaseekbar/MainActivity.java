package susion.com.smediaseekbar;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import susion.com.mediaseekbar.MediaSeekBar;
import susion.com.smediaseekbar.R;

public class MainActivity extends AppCompatActivity {

    private MediaSeekBar seekBar;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

            seekBar.setCurrentProgress(msg.arg1);
            seekBar.setHasBufferProgress(msg.arg2);

        }
    };
    private Random random;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        seekBar = (MediaSeekBar) findViewById(R.id.msb);
        seekBar.setMaxProgress(100);
        random = new Random();


        seekBar.setMediaSeekBarListener(new MediaSeekBar.MediaSeekBarListener() {
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

                Message message = new Message();

                message.arg1 = seekBar.getCurrentProgress() + 1;
                message.arg2 = seekBar.getHasBufferProgress() + random.nextInt(20);

                handler.sendMessage(message);

            }

        }, 1000, 1000);


    }
}
