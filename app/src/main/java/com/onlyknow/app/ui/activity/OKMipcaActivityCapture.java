package com.onlyknow.app.ui.activity;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.onlyknow.app.R;
import com.onlyknow.app.ui.OKBaseActivity;
import com.onlyknow.app.ui.view.OKSEImageView;
import com.onlyknow.app.ui.view.OKViewfinderView;
import com.onlyknow.app.utils.zxing.camera.OKCameraManager;
import com.onlyknow.app.utils.zxing.decoding.OKCaptureActivityHandler;
import com.onlyknow.app.utils.zxing.decoding.OKInactivityTimer;

import java.io.IOException;
import java.util.Vector;

/**
 * Initial the camera
 *
 * @author Ryan.Tang
 */
public class OKMipcaActivityCapture extends OKBaseActivity implements Callback {
    private OKCaptureActivityHandler handler;
    private OKViewfinderView viewfinderView;
    private boolean hasSurface;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    private OKInactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private static final float BEEP_VOLUME = 0.10f;
    private boolean vibrate;

    private OKSEImageView buthuitui;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ok_activity_capture);

        initSystemBar(this);

        OKCameraManager.init(this);
        findView();
        SetListener();
        hasSurface = false;
        inactivityTimer = new OKInactivityTimer(this);
    }

    private void SetListener() {
        buthuitui.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                OKMipcaActivityCapture.this.finish();
            }
        });
    }

    private void findView() {
        viewfinderView = (OKViewfinderView) findViewById(R.id.QRCODE_Viewfinder_view);
        buthuitui = (OKSEImageView) findViewById(R.id.QRCODE_huitui_but);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.QRCODE_Preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        decodeFormats = null;
        characterSet = null;

        playBeep = true;
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        OKCameraManager.get().closeDriver();
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    public void handleDecode(Result result, Bitmap barcode) {
        inactivityTimer.onActivity();
        playBeepSoundAndVibrate();
        String resultString = result.getText();

        if (resultString.equals("")) {
            Toast.makeText(OKMipcaActivityCapture.this, "没有识别到信息", Toast.LENGTH_SHORT).show();
        } else {
            if (resultString.startsWith("WeiZhiUSER=")) {
                String string = resultString.substring(resultString.indexOf("=") + 1);
                try {
                    String[] items = string.split("&");
                    String USER_NAME = items[0];
                    String USER_NICKNAME = items[1];
                    Bundle bundle = new Bundle();
                    bundle.putString("USERNAME", USER_NAME);
                    bundle.putString("NICKNAME", USER_NICKNAME);
                    Intent intent = new Intent();
                    intent.putExtras(bundle);
                    intent.setClass(OKMipcaActivityCapture.this, OKHomePageActivity.class);
                    startActivity(intent);
                } catch (Exception e) {
                    Intent resultIntent = new Intent(this, OKCapTureActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("RESULT", resultString);
                    resultIntent.putExtras(bundle);
                    this.startActivity(resultIntent);
                }
            } else {
                Intent resultIntent = new Intent(this, OKCapTureActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("RESULT", resultString);
                resultIntent.putExtras(bundle);
                this.startActivity(resultIntent);
            }
        }
        OKMipcaActivityCapture.this.finish();
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            OKCameraManager.get().openDriver(surfaceHolder);
        } catch (IOException ioe) {
            return;
        } catch (RuntimeException e) {
            return;
        }
        if (handler == null) {
            handler = new OKCaptureActivityHandler(this, decodeFormats, characterSet);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
    }

    public OKViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();

    }

    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            // The volume on STREAM_SYSTEM is not adjustable, and users found it
            // too loud,
            // so we now play on the music stream.
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    private static final long VIBRATE_DURATION = 200L;

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final OnCompletionListener beepListener = new OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };
}
