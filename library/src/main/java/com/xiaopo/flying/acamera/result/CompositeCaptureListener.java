package com.xiaopo.flying.acamera.result;

import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.support.annotation.NonNull;

import java.util.HashSet;
import java.util.Set;

/**
 * @author wupanjie
 */
public class CompositeCaptureListener implements CaptureListener {

  private final Set<CaptureListener> listeners = new HashSet<>();

  public void add(CaptureListener listener) {
    listeners.add(listener);
  }

  public void remove(CaptureListener listener) {
    listeners.remove(listener);
  }

  public void clear() {
    listeners.clear();
  }

  @Override
  public void onStart(long timestamp) {
    for (CaptureListener listener : listeners) {
      listener.onStart(timestamp);
    }
  }

  @Override
  public void onProgressed(@NonNull CaptureResult partialResult) {
    for (CaptureListener listener : listeners) {
      listener.onProgressed(partialResult);
    }
  }

  @Override
  public void onCompleted(@NonNull TotalCaptureResult result) {
    for (CaptureListener listener : listeners) {
      listener.onCompleted(result);
    }
  }

  @Override
  public void onFailed(@NonNull CaptureFailure failure) {
    for (CaptureListener listener : listeners) {
      listener.onFailed(failure);
    }
  }
}
