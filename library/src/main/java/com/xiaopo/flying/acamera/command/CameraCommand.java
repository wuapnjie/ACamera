package com.xiaopo.flying.acamera.command;

import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;

/**
 * A generic camera command which may take an arbitrary, indefinite amount of
 * time to execute. Camera commands typically interact with the camera device,
 * capture session, image reader, and other resources.
 * <p>
 * When shutting down, it is critical that commands gracefully exit when these
 * resources are no longer available.
 */
public abstract class CameraCommand implements Command {

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

}
