package com.xiaopo.flying.acamera.focus;

import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.support.annotation.NonNull;

import com.xiaopo.flying.acamera.model.AutoFocusState;
import com.xiaopo.flying.acamera.result.CaptureListener;

import io.reactivex.subjects.PublishSubject;

/**
 * TODO
 *
 * @author wupanjie
 */
public class AutoFocusStateListener implements CaptureListener {
  private final PublishSubject<AutoFocusState> afStateSubject;
  private AutoFocusState lastState;

  public AutoFocusStateListener(PublishSubject<AutoFocusState> afStateSubject) {
    this.afStateSubject = afStateSubject;
  }

  @Override
  public void onStart(long timestamp) {

  }

  @Override
  public void onProgressed(@NonNull CaptureResult partialResult) {

  }

  @Override
  public void onCompleted(@NonNull TotalCaptureResult result) {
    int afState = result.get(CaptureResult.CONTROL_AF_STATE);
    AutoFocusState currentState = AutoFocusState.from(afState);
    if (lastState == currentState) return;
    lastState = currentState;
    afStateSubject.onNext(currentState);
  }

  @Override
  public void onFailed(@NonNull CaptureFailure failure) {

  }
}
