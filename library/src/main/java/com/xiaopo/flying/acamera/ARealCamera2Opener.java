package com.xiaopo.flying.acamera;

import android.annotation.SuppressLint;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.Surface;

import com.xiaopo.flying.acamera.model.CameraId;
import com.xiaopo.flying.acamera.preview.CaptureSessionCreator;
import com.xiaopo.flying.acamera.preview.PreviewStarter;
import com.xiaopo.flying.acamera.util.AndroidServices;

import java.util.ArrayList;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;

/**
 * @author wupanjie
 */
public class ARealCamera2Opener implements ACameraOpener {

  private final CameraManager cameraManager;

  public ARealCamera2Opener() {
    this.cameraManager = AndroidServices.instance().provideCameraManager();
  }

  @Override
  public Single<ACamera> open(final CameraId cameraId, final Handler cameraHandler) {
    return Single.create(new SingleOnSubscribe<ACamera>() {

      private boolean isFirstCallback = true;

      @SuppressLint("MissingPermission")
      @Override
      public void subscribe(final SingleEmitter<ACamera> emitter) throws Exception {


        cameraManager.openCamera(cameraId.getValue(), new CameraDevice.StateCallback() {
          @Override
          public void onOpened(@NonNull CameraDevice camera) {
            if (isFirstCallback) {
              final CaptureSessionCreator captureSessionCreator =
                  new CaptureSessionCreator(camera, cameraHandler);
              try {
                CameraCharacteristics characteristics =
                    cameraManager.getCameraCharacteristics(camera.getId());
                PreviewStarter previewStarter = new PreviewStarter(
                    new ArrayList<Surface>(0),
                    captureSessionCreator,
                    camera,
                    new ARealCameraCharacteristics(characteristics),
                    cameraHandler
                );
                ACamera aCamera = new ARealCamera(new ARealCameraCharacteristics(characteristics), previewStarter);

                emitter.onSuccess(aCamera);
              } catch (CameraAccessException e) {
                e.printStackTrace();
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
              emitter.onError(new Exception("error when open camera"));
            }
          }
        }, cameraHandler);
      }
    });
  }
}
