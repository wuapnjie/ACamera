package com.xiaopo.flying.acamera;

import android.annotation.SuppressLint;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.os.Handler;
import android.support.annotation.NonNull;

import com.xiaopo.flying.acamera.model.CameraId;
import com.xiaopo.flying.acamera.util.AndroidServices;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;

/**
 * @author wupanjie
 */
class ARealCamera2Opener extends ACameraOpener implements SingleOnSubscribe<ACamera> {

  private final CameraManager cameraManager;
  private boolean isFirstCallback = true;
  private CameraId cameraId;
  private Handler cameraHandler;

  ARealCamera2Opener(CameraId cameraId, Handler cameraHandler) {
    this.cameraManager = AndroidServices.instance().provideCameraManager();
    this.cameraId = cameraId;
    this.cameraHandler = cameraHandler;
  }

  @Override
  public Single<ACamera> open() {
    return Single.create(this);
  }

  @SuppressLint("MissingPermission")
  @Override
  public void subscribe(final SingleEmitter<ACamera> emitter) throws Exception {
    cameraManager.openCamera(cameraId.getValue(), new CameraDevice.StateCallback() {
      @Override
      public void onOpened(@NonNull CameraDevice cameraDevice) {
        if (isFirstCallback) {
          ACameraFactory factory = new ACameraFactory(cameraManager, cameraHandler, cameraDevice);
          ACamera aCamera = factory.create();
          if (aCamera != null) {
            emitter.onSuccess(aCamera);
          } else {
            emitter.onError(new Exception("camera open failed"));
          }
        }
      }

      @Override
      public void onDisconnected(@NonNull CameraDevice camera) {
        if (isFirstCallback) {
          emitter.onError(new Exception("camera disconnect"));
        }
      }

      @Override
      public void onError(@NonNull CameraDevice camera, int error) {
        if (isFirstCallback) {
          emitter.onError(new ACameraException("error when open camera, code is " + error));
        }
      }
    }, cameraHandler);
  }
}
