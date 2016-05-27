package cn.itcast.recorder;

import java.io.File;
import java.io.IOException;



import android.app.Activity;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.SurfaceHolder.Callback;
import android.widget.Button;
import android.widget.RelativeLayout;

public class MainActivity extends Activity {
    private SurfaceView surfaceView;
    private RelativeLayout layout;
    private Button recordbutton;
    private Button stopbutton;
    private MediaRecorder mediaRecorder;
    private Camera camera;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.main);
        surfaceView = (SurfaceView) this.findViewById(R.id.surfaceView);
        surfaceView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceView.getHolder().setFixedSize(176, 144);
        surfaceView.getHolder().setKeepScreenOn(true);
        surfaceView.getHolder().addCallback(new SurfaceCallback());
        layout = (RelativeLayout) this.findViewById(R.id.layout);
        recordbutton = (Button) this.findViewById(R.id.recordbutton);
        stopbutton = (Button) this.findViewById(R.id.stopbutton);
    }

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(event.getAction() == MotionEvent.ACTION_DOWN){
			layout.setVisibility(ViewGroup.VISIBLE);
		}
		return super.onTouchEvent(event);
	}
    
    public void record(View v) throws IOException{
    	switch (v.getId()) {
		case R.id.recordbutton:
			try{
				
				camera.release();
				File videoFile = new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis()+ ".3gp");
				mediaRecorder = new MediaRecorder();
				mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
				mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
				mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
				mediaRecorder.setVideoSize(320, 240);
				mediaRecorder.setVideoFrameRate(5);
				mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
				mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
				mediaRecorder.setOutputFile(videoFile.getAbsolutePath());
				mediaRecorder.setPreviewDisplay(surfaceView.getHolder().getSurface());
				mediaRecorder.prepare();
				mediaRecorder.start();
			}catch (Exception e) {
				e.printStackTrace();
			}
			recordbutton.setEnabled(false);
			stopbutton.setEnabled(true);
			break;

		case R.id.stopbutton:
			if(mediaRecorder!=null){
				mediaRecorder.stop();
				mediaRecorder.release();
				mediaRecorder = null;
			}
			
			openCamera(surfaceView.getHolder());
			recordbutton.setEnabled(true);
			stopbutton.setEnabled(false);
			
			break;
		}
    }
    
    
    private final class SurfaceCallback implements Callback{
		public void surfaceCreated(SurfaceHolder holder) {
			
				openCamera(holder);
			
		}
		public void surfaceChanged(SurfaceHolder holder, int format, int width,int height) {
		}
		public void surfaceDestroyed(SurfaceHolder holder) {
			if(camera!=null){
				camera.release();
				camera = null;
			}
			if(mediaRecorder!=null){
				mediaRecorder.release();
				mediaRecorder=null;
			}
		}
}
    
    //打开Camera
	private void openCamera(SurfaceHolder holder)  {
		try {
			
			camera = Camera.open();//打开摄像头
			
			Camera.Parameters parameters = camera.getParameters();
			//Log.i("parameter", parameters.flatten());
			parameters.setPreviewSize(640, 480);
			parameters.setPreviewFrameRate(5);
			parameters.setPictureSize(800,480);
			parameters.setJpegQuality(80);
			camera.setParameters(parameters);
			camera.setPreviewDisplay(holder);
			camera.startPreview();//开始预览
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
