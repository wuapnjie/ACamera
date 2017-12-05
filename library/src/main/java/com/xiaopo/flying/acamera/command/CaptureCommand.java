package com.xiaopo.flying.acamera.command;

import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.os.Handler;
import android.util.Log;

import com.xiaopo.flying.acamera.base.Consumer;
import com.xiaopo.flying.acamera.base.Supplier;
import com.xiaopo.flying.acamera.model.Photo;
import com.xiaopo.flying.acamera.picturetaker.StillSurfaceReader;
import com.xiaopo.flying.acamera.preview.SessionManager;
import com.xiaopo.flying.acamera.request.RequestFactory;
import com.xiaopo.flying.acamera.request.RequestTemplate;

import java.util.Arrays;

import io.reactivex.Single;

/**
 * @author wupanjie
 */
public class CaptureCommand extends CameraCommand<Photo> implements Consumer<CameraCaptureSession> {
  private static final String TAG = "CaptureCommand";

  private final RequestFactory requestFactory;
  private final Handler cameraHandler;
  private final StillSurfaceReader surfaceReader;

  public CaptureCommand(RequestFactory requestFactory,
                        Handler cameraHandler,
                        StillSurfaceReader surfaceReader) {
    this.requestFactory = requestFactory;
    this.cameraHandler = cameraHandler;
    this.surfaceReader = surfaceReader;
  }

  @Override
  public void run() {
    SessionManager.getInstance()
        .withSession(this);
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

      // TODO process the image data
      byte[] image = surfaceReader.getPhotoBytes();
      Log.d(TAG, "accept: image -> " + image.length);

      // forward the result
      Single.just(new Photo(image))
          .subscribe(getDeferredResult());

      RequestTemplate previewBuilder = requestFactory.createPreviewTemplate().build();
      captureSession.setRepeatingBurst(
          Arrays.asList(previewBuilder.generateRequest()),
          null,
          cameraHandler
      );

    } catch (CameraAccessException e) {
      e.printStackTrace();
    }
  }
}
