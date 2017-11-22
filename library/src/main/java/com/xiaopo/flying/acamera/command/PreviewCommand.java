package com.xiaopo.flying.acamera.command;

import android.graphics.Rect;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CaptureRequest;
import android.os.Handler;
import android.view.Surface;

import com.xiaopo.flying.acamera.ACameraCharacteristics;
import com.xiaopo.flying.acamera.base.Supplier;
import com.xiaopo.flying.acamera.model.FaceDetectMode;
import com.xiaopo.flying.acamera.model.FaceDetectModeSupplier;
import com.xiaopo.flying.acamera.model.ZoomedCropRegionSupplier;

/**
 * @author wupanjie
 */
public class PreviewCommand extends CameraCommand {

  private final Surface previewSurface;
  private final CameraDevice cameraDevice;
  private final ACameraCharacteristics cameraCharacteristics;
  private final CameraCaptureSession captureSession;
  private final Handler cameraHandler;

  public PreviewCommand(Surface previewSurface,
                        CameraDevice cameraDevice,
                        ACameraCharacteristics cameraCharacteristics,
                        CameraCaptureSession captureSession,
                        Handler cameraHandler) {
    this.previewSurface = previewSurface;
    this.cameraDevice = cameraDevice;
    this.cameraCharacteristics = cameraCharacteristics;
    this.captureSession = captureSession;
    this.cameraHandler = cameraHandler;
  }

  @Override
  public void run() throws Exception {
    CaptureRequest.Builder builder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
    builder.addTarget(previewSurface);

    builder.set(CaptureRequest.CONTROL_AF_MODE,
        CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
    builder.set(CaptureRequest.CONTROL_AE_MODE,
        CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
    builder.set(CaptureRequest.CONTROL_AE_EXPOSURE_COMPENSATION, 0);
    builder.set(CaptureRequest.CONTROL_MODE,
        CaptureRequest.CONTROL_MODE_USE_SCENE_MODE);
    builder.set(CaptureRequest.CONTROL_SCENE_MODE,
        CaptureRequest.CONTROL_SCENE_MODE_FACE_PRIORITY);
    Supplier<FaceDetectMode> faceDetectMode = new Supplier<FaceDetectMode>() {
      @Override
      public FaceDetectMode get() {
        return FaceDetectMode.highestFrom(cameraCharacteristics);
      }
    };
    builder.set(CaptureRequest.STATISTICS_FACE_DETECT_MODE,
        new FaceDetectModeSupplier(faceDetectMode).get());

    Supplier<Float> zoomSetting = new Supplier<Float>() {
      @Override
      public Float get() {
        return 1f;
      }
    };
    Supplier<Rect> cropRegion =
        new ZoomedCropRegionSupplier(cameraCharacteristics.getSensorInfoActiveArraySize(), zoomSetting);
    builder.set(CaptureRequest.SCALER_CROP_REGION, cropRegion.get());

    captureSession.setRepeatingRequest(builder.build(), null, cameraHandler);
  }
}
