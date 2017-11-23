package com.xiaopo.flying.acamera.command;

import android.util.Log;

import com.xiaopo.flying.acamera.base.SafeCloseable;

import io.reactivex.Observer;
import io.reactivex.disposables.CompositeDisposable;
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
  private CompositeDisposable compositeDisposable = new CompositeDisposable();

  @Override
  public void onSubscribe(Disposable d) {
    this.disposable = d;
  }

  @Override
  public void onNext(CameraCommand cameraCommand) {
    if (closed) return;

    compositeDisposable
        .add(cameraCommand
            .start()
            .subscribeOn(Schedulers.io())
            .subscribe());
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
        compositeDisposable.dispose();
      }
    }
  }
}
