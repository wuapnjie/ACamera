package com.xiaopo.flying.acamera.result;

import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.support.annotation.NonNull;

/**
 * @author wupanjie
 */
public class ForwardCaptureCallback extends CameraCaptureSession.CaptureCallback {

  private final CaptureListener captureListener;

  public ForwardCaptureCallback(@NonNull CaptureListener captureListener) {
    this.captureListener = captureListener;
  }

  @Override
  public void onCaptureStarted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, long timestamp, long frameNumber) {
    super.onCaptureStarted(session, request, timestamp, frameNumber);
    captureListener.onStart(timestamp);
  }

  @Override
  public void onCaptureProgressed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureResult partialResult) {
    super.onCaptureProgressed(session, request, partialResult);
    captureListener.onProgressed(partialResult);
  }

  @Override
  public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
    super.onCaptureCompleted(session, request, result);
    captureListener.onCompleted(result);
  }

  @Override
  public void onCaptureFailed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureFailure failure) {
    super.onCaptureFailed(session, request, failure);
    captureListener.onFailed(failure);
  }
}
