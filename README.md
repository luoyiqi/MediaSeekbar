### MediaSeekBar

![](http://7xrbxa.com1.z0.glb.clouddn.com/git_gifseekbar.gif)

> - 一个用于媒体播放器进度条的seekbar。
> - 内部进度条分为3种： 背景进度条， 缓冲进度条， 已播放进度条。
>
>
> - 可以自定义进度条宽度、颜色以及 thumb样式。
> - 可以非常方便定义出简洁的进度条。
> - thumb的灵敏度要比andriod自带的灵敏



```java

    <susion.com.mediaseekbar.MediaSeekBar
        android:layout_marginTop="30dp"
        android:id="@+id/msb1"
        android:layout_gravity="center_horizontal"
        android:layout_width="200dp"
        android:layout_height="30dp"
        msb:canClickProgress="true"
        msb:maxProgress="100"
        msb:originBackgroundColor="@color/origin_background_color"
        msb:hasBufferColor="@color/has_buffer_color"
        msb:progressWidth="2dp"
        msb:customThumb="@drawable/custom_seekbar_thumb"
        msb:hasPlayColor="#000000">
    </susion.com.mediaseekbar.MediaSeekBar>

```

