package com.xiaopo.flying.acamera.preview;

import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.Surface;


import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

/**
 * Asynchronously creates capture sessions.
 */
public class CaptureSessionCreator {
  private final CameraDevice cameraDevice;
  private final Handler cameraHandler;

  /**
   * @param cameraDevice
   * @param cameraHandler The handler on which to process capture session
   */
  public CaptureSessionCreator(CameraDevice cameraDevice,Handler cameraHandler) {
    this.cameraDevice = cameraDevice;
    this.cameraHandler = cameraHandler;
  }

  public Observable<CameraCaptureSession> createCaptureSession(final List<Surface> surfaces) {

    return Observable.create(new ObservableOnSubscribe<CameraCaptureSession>() {
      @Override
      public void subscribe(final ObservableEmitter<CameraCaptureSession> emitter) throws Exception {
        cameraDevice.createCaptureSession(surfaces, new CameraCaptureSession.StateCallback() {
          private boolean isEmitted = false;

          @Override
          public void onActive(@NonNull CameraCaptureSession session) {

          }

          @Override
          public void onClosed(@NonNull CameraCaptureSession session) {
            if (isEmitted) return;
            session.close();
            emitter.onComplete();
          }

          @Override
          public void onConfigureFailed(@NonNull CameraCaptureSession session) {
            if (isEmitted) return;
            session.close();
            emitter.onComplete();
          }

          @Override
          public void onConfigured(@NonNull CameraCaptureSession session) {
            if (isEmitted) return;
            emitter.onNext(session);
            emitter.onComplete();
            isEmitted = true;
          }

          @Override
          public void onReady(@NonNull CameraCaptureSession session) {

          }
        }, cameraHandler);
      }
    });
  }
}
