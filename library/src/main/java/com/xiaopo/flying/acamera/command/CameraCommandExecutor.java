package com.xiaopo.flying.acamera.command;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @author wupanjie
 */
public class CameraCommandExecutor implements Observer<CameraCommand> {

  private Disposable disposable;
  private final Object lock = new Object();
  private boolean closed = false;

  @Override
  public void onSubscribe(Disposable d) {
    this.disposable = d;
  }

  @Override
  public void onNext(CameraCommand cameraCommand) {
    if (closed) return;

    cameraCommand
        .start()
        .subscribeOn(Schedulers.io())
        .subscribe();
  }

  @Override
  public void onError(Throwable e) {

  }

  @Override
  public void onComplete() {
    synchronized (lock) {
      closed = true;
      if (!disposable.isDisposed()) {
        disposable.dispose();
      }
    }

  }
}
