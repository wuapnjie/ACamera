package com.xiaopo.flying.acamera.preview;

import android.hardware.camera2.CameraCaptureSession;

import com.xiaopo.flying.acamera.base.SafeCloseable;
import com.xiaopo.flying.acamera.base.Supplier;
import com.xiaopo.flying.acamera.base.optional.Optional;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;

/**
 * @author wupanjie
 */
public class SessionManager implements SafeCloseable, Supplier<Optional<CameraCaptureSession>> {

  private final BehaviorSubject<CameraCaptureSession> subject;

  private SessionManager() {
    //no instance
    subject = BehaviorSubject.create();
  }

  private static SessionManager instance;

  public static SessionManager getInstance() {
    SessionManager result = instance;
    if (instance == null) {
      synchronized (SessionManager.class) {
        result = instance;
        if (result == null) {
          result = instance = new SessionManager();
        }
      }
    }

    return result;
  }

  public Observable<CameraCaptureSession> getSession() {
    return subject;
  }

  public void emitSession(CameraCaptureSession captureSession) {
    subject.onNext(captureSession);
  }

  @Override
  public void close() {
    subject.onComplete();
  }

  @Override
  public Optional<CameraCaptureSession> get() {
    return Optional.fromNullable(subject.getValue());
  }
}
