package com.xiaopo.flying.acamera.preview;

import android.hardware.camera2.CameraCaptureSession;
import android.util.Log;

import com.xiaopo.flying.acamera.base.Consumer;
import com.xiaopo.flying.acamera.base.SafeCloseable;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;

/**
 * @author wupanjie
 */
public class SessionManager implements SafeCloseable {
  private static final String TAG = "SessionManager";
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

  void emitSession(CameraCaptureSession captureSession) {
    subject.onNext(captureSession);
  }

  @Override
  public void close() {
    subject.onComplete();
    instance = null;
  }

  public void withSession(Consumer<CameraCaptureSession> consumer) {
    if (subject.hasValue()) {
      consumer.accept(subject.getValue());
    } else {
      Log.e(TAG, "withSession: there is no session now.");
    }
  }
}
