package com.xiaopo.flying.acamera.command;

import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.os.Handler;

import com.xiaopo.flying.acamera.base.Consumer;
import com.xiaopo.flying.acamera.picturetaker.StillSurfaceReader;
import com.xiaopo.flying.acamera.preview.SessionManager;
import com.xiaopo.flying.acamera.request.RequestFactory;
import com.xiaopo.flying.acamera.request.RequestTemplate;

import java.util.Arrays;

/**
 * @author wupanjie
 */
public class CaptureCommand extends CameraCommand implements Consumer<CameraCaptureSession> {

  private final RequestFactory requestFactory;
  private final Handler cameraHandler;
  private final StillSurfaceReader surfaceReader;

  public CaptureCommand(RequestFactory requestFactory, Handler cameraHandler, StillSurfaceReader surfaceReader) {
    this.requestFactory = requestFactory;
    this.cameraHandler = cameraHandler;
    this.surfaceReader = surfaceReader;
  }

  @Override
  public void run() {
    SessionManager.getInstance()
        .get()
        .ifPresent(this);
  }

  @Override
  public void accept(CameraCaptureSession captureSession) {
    try {
      RequestTemplate captureBuilder =
          requestFactory.createCaptureTemplate()
              .addSurface(surfaceReader.getSurface())
              .build();


      captureSession.captureBurst(
          Arrays.asList(captureBuilder.generateRequest()),
          captureBuilder.getCaptureCallback(),
          cameraHandler);

//      captureSession.capture(
//          captureBuilder.generateRequest(),
//          captureBuilder.getCaptureCallback(),
//          cameraHandler);


    } catch (CameraAccessException e) {
      e.printStackTrace();
    }
  }
}
