package com.xiaopo.flying.acamera.focus;

import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.support.annotation.NonNull;
import android.util.Log;

import com.xiaopo.flying.acamera.result.CaptureListener;

/**
 * TODO
 * @author wupanjie
 */
public class AutoFocusStateListener implements CaptureListener{
  private static final String TAG = "AutoFocusStateListener";
  @Override
  public void onStart(long timestamp) {
    Log.d(TAG, "onStart: ");
  }

  @Override
  public void onProgressed(@NonNull CaptureResult partialResult) {
    Log.d(TAG, "onProgressed: ");
  }

  @Override
  public void onCompleted(@NonNull TotalCaptureResult result) {
    Log.d(TAG, "onCompleted: ");
  }

  @Override
  public void onFailed(@NonNull CaptureFailure failure) {
    Log.d(TAG, "onFailed: ");
  }
}
