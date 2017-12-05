package com.xiaopo.flying.acamera.command;

import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.os.Handler;

import com.xiaopo.flying.acamera.base.Consumer;
import com.xiaopo.flying.acamera.preview.SessionManager;
import com.xiaopo.flying.acamera.request.RequestFactory;
import com.xiaopo.flying.acamera.request.RequestTemplate;

import java.util.Arrays;

/**
 * @author wupanjie
 */
public class FullAFScanCommand extends CameraCommand implements Consumer<CameraCaptureSession> {

  private final RequestFactory requestFactory;
  private final Handler cameraHandler;

  public FullAFScanCommand(RequestFactory requestFactory, Handler cameraHandler) {
    this.requestFactory = requestFactory;
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
      RequestTemplate idleBuilder = requestFactory.createAFIdleTemplate().build();
      captureSession.setRepeatingBurst(
          Arrays.asList(idleBuilder.generateRequest()),
          idleBuilder.getCaptureCallback(),
          cameraHandler);

      RequestTemplate cancelBuilder = requestFactory.createAFCancelTemplate().build();
      captureSession.captureBurst(
          Arrays.asList(cancelBuilder.generateRequest()),
          cancelBuilder.getCaptureCallback(),
          cameraHandler);

      idleBuilder = requestFactory.createAFIdleTemplate().build();
      captureSession.setRepeatingBurst(
          Arrays.asList(idleBuilder.generateRequest()),
          idleBuilder.getCaptureCallback(),
          cameraHandler);

      RequestTemplate triggerBuilder = requestFactory.createAFTriggerTemplate().build();
      captureSession.captureBurst(
          Arrays.asList(triggerBuilder.generateRequest()),
          triggerBuilder.getCaptureCallback(),
          cameraHandler);
    } catch (CameraAccessException e) {
      e.printStackTrace();
    }

  }
}
