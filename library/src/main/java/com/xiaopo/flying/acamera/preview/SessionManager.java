package com.xiaopo.flying.acamera.preview;

import android.hardware.camera2.CameraCaptureSession;
import android.util.Log;

import com.xiaopo.flying.acamera.base.Consumer;
import com.xiaopo.flying.acamera.base.SafeCloseable;

/**
 * @author wupanjie
 */
public class SessionManager implements SafeCloseable {
  private static final String TAG = "SessionManager";
  private CameraCaptureSession cameraCaptureSession;

  private SessionManager() {
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

  void emitSession(CameraCaptureSession captureSession) {
    cameraCaptureSession = captureSession;
  }

  @Override
  public void close() {
    cameraCaptureSession = null;
  }

  public void withSession(Consumer<CameraCaptureSession> consumer) {
    if (cameraCaptureSession != null) {
      consumer.accept(cameraCaptureSession);
    } else {
      Log.e(TAG, "withSession: there is no session now.");
    }
  }
}
