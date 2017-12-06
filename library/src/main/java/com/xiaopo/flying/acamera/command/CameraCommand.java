package com.xiaopo.flying.acamera.command;

import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.SingleSubject;

public abstract class CameraCommand<T> implements Command {

  long delay = 0L;
  TimeUnit unit = TimeUnit.SECONDS;
  private SingleSubject deferredResult;

  Disposable start(Scheduler scheduler) {
    Scheduler.Worker worker = scheduler.createWorker();
    return worker.schedule(this, delay, unit);
  }

  public Completable start() {
    return Completable.create(new CompletableOnSubscribe() {
      @Override
      public void subscribe(CompletableEmitter e) throws Exception {
        try {
          run();
        } catch (Exception exception) {
          e.onError(exception);
          return;
        }
        e.onComplete();
      }
    });
  }

  void setDelay(long delay, TimeUnit unit) {
    this.delay = delay;
    this.unit = unit;
  }

  @SuppressWarnings("unchecked")
  protected SingleSubject<T> getDeferredResult() {
    return (SingleSubject<T>) deferredResult;
  }

  public void setDeferredResult(SingleSubject<?> deferredResult) {
    this.deferredResult = deferredResult;
  }
}
