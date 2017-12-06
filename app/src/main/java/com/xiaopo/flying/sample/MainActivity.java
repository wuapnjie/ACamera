package com.xiaopo.flying.sample;

import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.media.ExifInterface;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.xiaopo.flying.acamera.ACamera;
import com.xiaopo.flying.acamera.ACameraOpener;
import com.xiaopo.flying.acamera.model.CameraId;
import com.xiaopo.flying.acamera.model.Photo;
import com.xiaopo.flying.acamera.util.AndroidServices;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionNo;
import com.yanzhenjie.permission.PermissionYes;

import java.io.ByteArrayInputStream;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements TextureView.SurfaceTextureListener, TouchableTextureView.OnTapListener {

  private static final String TAG = "MainActivity";
  private Handler cameraHandler;
  private CameraManager cameraManager;
  private SurfaceTexture surfaceTexture;
  private Button btnTake;

  private ACamera camera;
  private ImageView ivPhoto;
  private TouchableTextureView previewContent;
  private SeekBar seekBar;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    initView();

    btnTake.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        takePicture();
      }
    });
    seekBar.setEnabled(false);
    AndPermission.with(this)
        .permission(Manifest.permission.CAMERA)
        .requestCode(300)
        .callback(this)
        .start();

    HandlerThread thread = new HandlerThread("CaptureModule.mCameraHandler");
    thread.start();
    cameraHandler = new Handler(thread.getLooper());
    cameraManager = AndroidServices.instance().provideCameraManager();

    previewContent.setOnTapListener(this);

  }

  private void takePicture() {
    camera.takePicture()
        .subscribeOn(Schedulers.io())
        .map(new Function<Photo, Bitmap>() {
          @Override
          public Bitmap apply(final Photo photo) throws Exception {
            byte[] bytes = photo.getBytes();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 4;

            ExifInterface exifInterface = new ExifInterface(new ByteArrayInputStream(bytes));

            int rotation = 0;
            int orientation = exifInterface.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
              case ExifInterface.ORIENTATION_ROTATE_90:
                rotation = 90;
                break;
              case ExifInterface.ORIENTATION_ROTATE_180:
                rotation = 180;
                break;
              case ExifInterface.ORIENTATION_ROTATE_270:
                rotation = 270;
                break;
            }

            final int imageRotation = rotation;

            ivPhoto.post(new Runnable() {
              @Override
              public void run() {
                Log.d(TAG, "run: imageRotation -> " + imageRotation);
                ivPhoto.setRotation(imageRotation);
              }
            });

            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
          }
        })
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Consumer<Bitmap>() {
          @Override
          public void accept(Bitmap photo) throws Exception {

            ivPhoto.setImageBitmap(photo);
          }
        });
  }


  @PermissionYes(300)
  private void getPermissionYes(List<String> grantedPermissions) {
    if (surfaceTexture == null) {
      previewContent.setSurfaceTextureListener(this);
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
    previewContent.setTransform(matrix);
    openCamera();
  }

  private void openCamera() {
    CameraId cameraId = CameraId.from(findFirstCameraIdFacing(CameraCharacteristics.LENS_FACING_BACK));

    ACameraOpener
        .with(cameraId, cameraHandler)
        .open()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Consumer<ACamera>() {
          @Override
          public void accept(final ACamera aCamera) throws Exception {
            Log.d(TAG, "accept: aCamera");
            camera = aCamera;

            seekBar.setEnabled(true);

            final float min = 1f;
            final float max = aCamera.getCharacteristic().getMaxZoomRatio();

            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
              @Override
              public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                float ratio = min + progress * ((max - min) / 100);
                aCamera.getStateManager().getZoomState().update(ratio);
              }

              @Override
              public void onStartTrackingTouch(SeekBar seekBar) {

              }

              @Override
              public void onStopTrackingTouch(SeekBar seekBar) {

              }
            });

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

  @Override
  public void onTap(float x, float y) {
    Log.d(TAG, "onTap: x : " + x + ", y : " + y);
    if (camera != null) {
      camera.triggerFocusAt(x, y);
    }
  }

  private void initView() {
    previewContent = findViewById(R.id.preview_content);
    btnTake = findViewById(R.id.btn_take);
    ivPhoto = findViewById(R.id.iv_photo);
    previewContent = findViewById(R.id.preview_content);
    seekBar = findViewById(R.id.seek_bar);
  }
}
