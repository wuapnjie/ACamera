package com.xiaopo.flying.acamera.command;

import io.reactivex.subjects.BehaviorSubject;

/**
 * @author wupanjie
 */
public class CameraCommandCenter {
  private final BehaviorSubject<CameraCommand> commandSubject;
  private static CameraCommandCenter instance;

  private CameraCommandCenter() {
    commandSubject = BehaviorSubject.create();
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

  public void registerExecutor(CameraCommandExecutor commandExecutor) {
    commandSubject.subscribe(commandExecutor);
  }


}
