package com.xiaopo.flying.acamera.picturetaker;

import com.xiaopo.flying.acamera.command.CameraCommand;
import com.xiaopo.flying.acamera.command.CameraCommandCenter;
import com.xiaopo.flying.acamera.command.CameraCommandFactory;
import com.xiaopo.flying.acamera.command.CameraCommandType;

import java.util.concurrent.TimeUnit;

import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;

/**
 * @author wupanjie
 */
public class PictureTaker implements PictureTakeFunction, Consumer<Integer> {
  private final CameraCommandFactory commandFactory;
  private final PublishSubject<Integer> subject;

  public PictureTaker(CameraCommandFactory commandFactory) {
    this.commandFactory = commandFactory;
    this.subject = PublishSubject.create();
    this.subject
        .debounce(300, TimeUnit.MILLISECONDS)
        .subscribe(this);
  }

  @Override
  public void takePicture() {
    subject.onNext(0);
  }

  @Override
  public void accept(Integer delay) {
    CameraCommand captureCommand = commandFactory.create(CameraCommandType.CAPTURE);
    CameraCommandCenter.getInstance().nextCommand(captureCommand, delay, TimeUnit.SECONDS);
  }
}
