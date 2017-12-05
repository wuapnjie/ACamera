package com.xiaopo.flying.acamera.picturetaker;

import com.xiaopo.flying.acamera.command.CameraCommand;
import com.xiaopo.flying.acamera.command.CameraCommandCenter;
import com.xiaopo.flying.acamera.command.CameraCommandFactory;
import com.xiaopo.flying.acamera.command.CameraCommandType;
import com.xiaopo.flying.acamera.model.Photo;

import java.util.concurrent.TimeUnit;

import io.reactivex.Single;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.SingleSubject;

/**
 * @author wupanjie
 */
public class PictureTaker implements PictureTakeFunction, Consumer<SingleSubject<Photo>> {
  private final CameraCommandFactory commandFactory;
  private final PublishSubject<SingleSubject<Photo>> subject;

  public PictureTaker(CameraCommandFactory commandFactory) {
    this.commandFactory = commandFactory;
    this.subject = PublishSubject.create();
    this.subject
        .debounce(300, TimeUnit.MILLISECONDS)
        .subscribe(this);
  }

  @Override
  public Single<Photo> takePicture() {
    SingleSubject<Photo> defer = SingleSubject.create();
    subject.onNext(defer);
    return defer;
  }

  @Override
  public void accept(SingleSubject<Photo> deferred) {
    CameraCommand captureCommand = commandFactory.create(CameraCommandType.CAPTURE);
    captureCommand.setDeferredResult(deferred);
    CameraCommandCenter.getInstance().nextCommand(captureCommand);
  }
}
