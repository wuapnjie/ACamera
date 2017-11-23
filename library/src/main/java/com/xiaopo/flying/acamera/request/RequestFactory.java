package com.xiaopo.flying.acamera.request;

import android.graphics.Rect;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CaptureRequest;
import android.view.Surface;

import com.xiaopo.flying.acamera.ACameraCharacteristics;
import com.xiaopo.flying.acamera.base.Supplier;
import com.xiaopo.flying.acamera.model.FaceDetectMode;
import com.xiaopo.flying.acamera.model.FlashMode;
import com.xiaopo.flying.acamera.model.FocusMode;
import com.xiaopo.flying.acamera.model.ZoomedCropRegionSupplier;

import io.reactivex.subjects.BehaviorSubject;

/**
 * @author wupanjie
 */
public class RequestFactory {

  private final CameraDevice cameraDevice;
  private final ACameraCharacteristics aCameraCharacteristics;
  private final BehaviorSubject<Surface> previewSurfaceSubject;

  public RequestFactory(CameraDevice cameraDevice,
                        ACameraCharacteristics aCameraCharacteristics,
                        BehaviorSubject<Surface> previewSurfaceSubject) {
    this.cameraDevice = cameraDevice;
    this.aCameraCharacteristics = aCameraCharacteristics;
    this.previewSurfaceSubject = previewSurfaceSubject;
  }

  public RequestTemplate.Builder create(int templateType) {

    try {
      final CaptureRequest.Builder requestBuilder = cameraDevice.createCaptureRequest(templateType);
      return new RequestTemplate.Builder(requestBuilder);
    } catch (CameraAccessException e) {
      e.printStackTrace();
    }

    return null;
  }

  public RequestTemplate createPreviewTemplate() {
    Supplier<Float> zoomSetting = new Supplier<Float>() {
      @Override
      public Float get() {
        return 1f;
      }
    };
    Supplier<Rect> cropRegion =
        new ZoomedCropRegionSupplier(aCameraCharacteristics.getSensorInfoActiveArraySize(), zoomSetting);

    return create(CameraDevice.TEMPLATE_PREVIEW)
        .withFocusModeSupplier(new Supplier<FocusMode>() {
          @Override
          public FocusMode get() {
            return FocusMode.CONTINUOUS_PICTURE;
          }
        })
        .withFaceDetectModeSupplier(new Supplier<FaceDetectMode>() {
          @Override
          public FaceDetectMode get() {
            return FaceDetectMode.NONE;
          }
        })
        .withFlashModeSupplier(new Supplier<FlashMode>() {
          @Override
          public FlashMode get() {
            return FlashMode.AUTO;
          }
        })
        .withCropRegionModeSupplier(cropRegion)
        .addSurface(previewSurfaceSubject.getValue())
        .build();
  }
}
