package com.xiaopo.flying.sample;

import android.Manifest;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;

import com.xiaopo.flying.acamera.ACamera;
import com.xiaopo.flying.acamera.ACameraOpener;
import com.xiaopo.flying.acamera.model.CameraId;
import com.xiaopo.flying.acamera.util.AndroidServices;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionNo;
import com.yanzhenjie.permission.PermissionYes;

import java.util.List;

import io.reactivex.functions.Consumer;

public class MainActivity extends AppCompatActivity implements TextureView.SurfaceTextureListener {

  private static final String TAG = "MainActivity";
  private Handler cameraHandler;
  private CameraManager cameraManager;
  private TextureView textureView;
  private SurfaceTexture surfaceTexture;
  private Button btnClose;

  private ACamera camera;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    textureView = findViewById(R.id.preview_content);
    btnClose = findViewById(R.id.btn_close);
    btnClose.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        closeCamera();
      }
    });

    AndPermission.with(this)
        .permission(Manifest.permission.CAMERA)
        .requestCode(300)
        .callback(this)
        .start();

    HandlerThread thread = new HandlerThread("CaptureModule.mCameraHandler");
    thread.start();
    cameraHandler = new Handler(thread.getLooper());
    cameraManager = AndroidServices.instance().provideCameraManager();
  }


  // 成功回调的方法，用注解即可，这里的300就是请求时的requestCode。
  @PermissionYes(300)
  private void getPermissionYes(List<String> grantedPermissions) {
    if (surfaceTexture == null) {
      textureView.setSurfaceTextureListener(this);
    } else {
      openCamera();
    }
  }

  @PermissionNo(300)
  private void getPermissionNo(List<String> deniedPermissions) {

  }

  @Override
  protected void onResume() {
    super.onResume();

  }

  @Override
  protected void onPause() {
    super.onPause();
    closeCamera();
  }

  private String findFirstCameraIdFacing(int facing) {
    try {
      String[] cameraIds = cameraManager.getCameraIdList();
      for (String cameraId : cameraIds) {
        CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);
        if (characteristics.get(CameraCharacteristics.LENS_FACING) == facing) {
          return cameraId;
        }
      }
    } catch (CameraAccessException ex) {
      Log.w(TAG, "Unable to get camera ID", ex);
    }
    return null;
  }

  @Override
  public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
    this.surfaceTexture = surface;
    Matrix matrix = new Matrix();
    matrix.setValues(new float[]{1, 0, 0, 0, 1, 0, 0, 0, 1});
    textureView.setTransform(matrix);
    openCamera();
  }

  private void openCamera() {
    CameraId cameraId = CameraId.from(findFirstCameraIdFacing(CameraCharacteristics.LENS_FACING_BACK));

    ACameraOpener
        .with(cameraId, cameraHandler)
        .open()
        .subscribe(new Consumer<ACamera>() {
          @Override
          public void accept(ACamera aCamera) throws Exception {
            Log.d(TAG, "accept: aCamera");
            camera = aCamera;;

            surfaceTexture.setDefaultBufferSize(1280, 960);

            aCamera.startPreview(new Surface(surfaceTexture));
          }
        });
  }

  @Override
  public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

  }

  @Override
  public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
    return false;
  }

  @Override
  public void onSurfaceTextureUpdated(SurfaceTexture surface) {

  }

  private void closeCamera() {
    if (camera != null) {
      camera.close();
      camera = null;
    }
  }
}
