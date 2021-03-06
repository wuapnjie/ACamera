package com.xiaopo.flying.acamera.command;

import com.xiaopo.flying.acamera.base.SafeCloseable;

import java.util.concurrent.TimeUnit;

import io.reactivex.subjects.PublishSubject;

/**
 * @author wupanjie
 */
public class CameraCommandCenter implements SafeCloseable{
  private final PublishSubject<CameraCommand> commandSubject;
  private static CameraCommandCenter instance;

  private CameraCommandCenter() {
    commandSubject = PublishSubject.create();
  }

  public static CameraCommandCenter getInstance() {
    CameraCommandCenter result = instance;
    if (result == null) {
      synchronized (CameraCommandCenter.class) {
        result = instance;

        if (result == null) {
          instance = result = new CameraCommandCenter();
        }
      }
    }

    return result;
  }

  public void nextCommand(CameraCommand cameraCommand) {
    commandSubject.onNext(cameraCommand);
  }

  public void nextCommand(CameraCommand cameraCommand, long delay, TimeUnit unit) {
    cameraCommand.setDelay(delay, unit);
    commandSubject.onNext(cameraCommand);
  }

  public void registerExecutor(CameraCommandExecutor commandExecutor) {
    commandSubject.subscribe(commandExecutor);
  }

  @Override
  public void close() {
    commandSubject.onComplete();
    instance = null;
  }
}
