package com.xiaopo.flying.acamera.command;

import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.os.Handler;

import com.xiaopo.flying.acamera.base.Consumer;
import com.xiaopo.flying.acamera.preview.SessionManager;
import com.xiaopo.flying.acamera.request.RequestTemplate;


/**
 * @author wupanjie
 */
public class PreviewCommand extends CameraCommand implements Consumer<CameraCaptureSession> {

  private final RequestTemplate previewRequest;
  private final Handler cameraHandler;

  public PreviewCommand(RequestTemplate previewRequest,
                        Handler cameraHandler) {
    this.previewRequest = previewRequest;
    this.cameraHandler = cameraHandler;
  }

  @Override
  public void run() throws Exception {
    SessionManager.getInstance()
        .get()
        .ifPresent(this);
  }

  @Override
  public void accept(CameraCaptureSession captureSession) {
    try {
      captureSession.setRepeatingRequest(previewRequest.generateRequest(), null, cameraHandler);
    } catch (CameraAccessException e) {
      e.printStackTrace();
    }
  }
}
