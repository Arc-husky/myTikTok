package com.example.mainacticity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RecordActivity extends AppCompatActivity implements SurfaceHolder.Callback{

    private SurfaceView mSurfaceView;
    private Camera mCamera;
    private MediaRecorder mMediaRecorder;
    private SurfaceHolder mHolder;
    private String mp4Path = "";
    private boolean isRecording = false;
    private String MY_ID;
    Thread mThread;
    private long currentValue = 0;
    private long beginValue;
    RoundProgressBar bar;
    ImageButton rbtn;
    private boolean forceBreak=false;
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if(msg.what == 100) {
                bar.setValue(0);
                bar.setVisibility(View.GONE);
                rbtn.setImageResource(R.drawable.circle);
                @SuppressLint("HandlerLeak") Intent intent = new Intent(RecordActivity.this,UploadActivity.class);
                intent.putExtra(UploadActivity.VIDEO_OUTER_PATH, mp4Path);
                intent.putExtra(MainActivity.MY_ID_SAVE_KEY,MY_ID);
                startActivity(intent);
//                finish();
            }
        }
    };

    public void exit(View view) {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        Intent intent = getIntent();
        MY_ID = intent.getStringExtra(MainActivity.MY_ID_SAVE_KEY);
        bar = findViewById(R.id.barStroke);
        bar.setMax(15*1000);
        rbtn = findViewById(R.id.recordbtn);
        bar.setVisibility(View.GONE);
        mSurfaceView = findViewById(R.id.surfaceview);
        mHolder = mSurfaceView.getHolder();
        initCamera();
        mHolder.addCallback(this);

        rbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isRecording) {
                    mMediaRecorder.setOnErrorListener(null);
                    mMediaRecorder.setOnInfoListener(null);
                    mMediaRecorder.setPreviewDisplay(null);
                    try {
                        mMediaRecorder.stop();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
//            mMediaRecorder.stop();
                    mMediaRecorder.reset();
                    mMediaRecorder.release();
                    mMediaRecorder = null;
                    mCamera.lock();
                    forceBreak = true;
                }else {
                    if(prepareVideoRecorder()) {
                        mMediaRecorder.start();
                    }
                    rbtn.setImageResource(R.drawable.square);
                    bar.setVisibility(View.VISIBLE);
                    beginValue = new Date().getTime();
                    mThread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while(!forceBreak) {
                                currentValue = new Date().getTime() - beginValue;
                                if (currentValue>=15*1000) {
                                    break;
                                }
                                bar.setValue((int) currentValue);
                                try {
                                    Thread.sleep(50);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            isRecording = false;
                            beginValue = 0;
                            forceBreak = false;
                            mHandler.sendEmptyMessage(100);
                        }
                    });
                    mThread.start();
                }
                isRecording = !isRecording;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mCamera == null) {
            initCamera();
        }
        mCamera.startPreview();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCamera.stopPreview();
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        if (holder.getSurface() == null) {
            return;
        }
        //停止预览效果
        mCamera.stopPreview();
        //重新设置预览效果
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        mCamera.stopPreview();
        mCamera.release();
        mCamera=null;
    }
    private void initCamera() {
        mCamera = Camera.open();
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setPictureFormat(ImageFormat.JPEG);
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        parameters.set("orientation", "portrait");
        parameters.set("rotation", 90);
        mCamera.setParameters(parameters);
        mCamera.setDisplayOrientation(90);
    }

    private boolean prepareVideoRecorder() {
        mMediaRecorder = new MediaRecorder();

        // Step 1: Unlock and set camera to MediaRecorder
        mCamera.unlock();
        mMediaRecorder.setCamera(mCamera);

        // Step 2: Set sources
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
        mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));

        // Step 4: Set output file
        mp4Path = getOutputMediaPath();
        mMediaRecorder.setOutputFile(mp4Path);

        // Step 5: Set the preview output
        mMediaRecorder.setPreviewDisplay(mHolder.getSurface());
        mMediaRecorder.setOrientationHint(90);

        // Step 6: Prepare configured MediaRecorder
        try {
            mMediaRecorder.prepare();
        } catch (IllegalStateException | IOException e) {
            releaseMediaRecorder();
            return false;
        }
        return true;
    }

    private void releaseMediaRecorder() {
        // todo
        if(isRecording){
            isRecording = false;
            mMediaRecorder.stop();
        }
        mMediaRecorder.reset();
        mMediaRecorder.release();
        mMediaRecorder=null;
        mCamera.stopPreview();
        mCamera.lock();
        mCamera.release();
    }

    private String getOutputMediaPath() {
        File mediaStorageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = new File(mediaStorageDir, "IMG_" + timeStamp + ".mp4");
        if (!mediaFile.exists()) {
            mediaFile.getParentFile().mkdirs();
        }
        return mediaFile.getAbsolutePath();
    }
}