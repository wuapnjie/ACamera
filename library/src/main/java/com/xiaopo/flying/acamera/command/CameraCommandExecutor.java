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

    Log.d(TAG, "onNext: camera command : " + cameraCommand.getClass().getSimpleName());

    DisposeLastCommand command = new DisposeLastCommand(cameraCommand, currentCommandDisposable);

    currentCommandDisposable = command.start(Schedulers.io());
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

  class DisposeLastCommand extends CameraCommand {

    final CameraCommand actualCommand;
    final Disposable lastCommandDisposable;

    DisposeLastCommand(CameraCommand actualCommand,
                       Disposable lastCommandDisposable) {
      this.actualCommand = actualCommand;
      this.lastCommandDisposable = lastCommandDisposable;

      setDelay(actualCommand.delay, actualCommand.unit);
    }

    @Override
    public void run() {
      if (lastCommandDisposable != null) {
        lastCommandDisposable.dispose();
        Log.d(TAG, "run: dispose last");
      }

      actualCommand.run();
    }
  }
}
