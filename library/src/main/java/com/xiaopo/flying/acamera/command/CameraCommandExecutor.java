package com.xiaopo.flying.acamera.command;

import android.util.Log;

import com.xiaopo.flying.acamera.base.SafeCloseable;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @author wupanjie
 */
public class CameraCommandExecutor implements Observer<CameraCommand>, SafeCloseable {
  private static final String TAG = "CameraCommandExecutor";

  private Disposable disposable;
  private final Object lock = new Object();
  private boolean closed = false;
  private Disposable currentCommandDisposable;

  @Override
  public void onSubscribe(Disposable d) {
    this.disposable = d;
  }

  @Override
  public void onNext(CameraCommand cameraCommand) {
    if (closed) return;

    if (currentCommandDisposable != null) {
      currentCommandDisposable.dispose();
    }

    currentCommandDisposable =
        cameraCommand
            .start(Schedulers.io());
  }

  @Override
  public void onError(Throwable e) {
    Log.e(TAG, "onError: ", e);
  }

  @Override
  public void onComplete() {
    close();
  }

  @Override
  public void close() {
    synchronized (lock) {
      closed = true;
      if (!disposable.isDisposed()) {
        disposable.dispose();
      }

      if (currentCommandDisposable != null) {
        currentCommandDisposable.dispose();
      }
    }
  }
}
