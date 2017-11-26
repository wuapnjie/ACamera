package com.xiaopo.flying.acamera.result;

import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.support.annotation.NonNull;

/**
 * @author wupanjie
 */
public interface CaptureListener {

  void onStart(long timestamp);

  void onProgressed(@NonNull CaptureResult partialResult);

  void onCompleted(@NonNull TotalCaptureResult result);

  void onFailed(@NonNull CaptureFailure failure);

}
