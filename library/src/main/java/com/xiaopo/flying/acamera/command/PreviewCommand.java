package com.xiaopo.flying.acamera.command;

import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.os.Handler;

import com.xiaopo.flying.acamera.base.Consumer;
import com.xiaopo.flying.acamera.preview.SessionManager;
import com.xiaopo.flying.acamera.request.RequestTemplate;

import java.util.Arrays;


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
  public void run() {
    SessionManager.getInstance()
        .withSession(this);
  }

  @SuppressWarnings("ArraysAsListWithZeroOrOneArgument")
  @Override
  public void accept(CameraCaptureSession captureSession) {
    try {
      captureSession.setRepeatingBurst(Arrays.asList(previewRequest.generateRequest()), null, cameraHandler);
    } catch (CameraAccessException e) {
      e.printStackTrace();
    }
  }
}
