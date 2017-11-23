package com.xiaopo.flying.acamera.request;

import android.graphics.Rect;
import android.hardware.camera2.CaptureRequest;
import android.view.Surface;

import com.xiaopo.flying.acamera.base.Supplier;
import com.xiaopo.flying.acamera.model.FaceDetectMode;
import com.xiaopo.flying.acamera.model.FlashMode;
import com.xiaopo.flying.acamera.model.FocusMode;

import java.util.HashSet;
import java.util.Set;

/**
 * @author wupanjie
 */
public class RequestTemplate {
  private final CaptureRequest.Builder requestBuilder;

  private final Supplier<FocusMode> focusModeSupplier;
  private final Supplier<FlashMode> flashModeSupplier;
  private final Supplier<FaceDetectMode> faceDetectModeSupplier;
  private final Supplier<Rect> cropRegionSupplier;
  private final Set<Surface> surfaces;

  private RequestTemplate(Builder builder) {
    this.requestBuilder = builder.requestBuilder;

    this.focusModeSupplier = builder.focusModeSupplier;
    this.flashModeSupplier = builder.flashModeSupplier;
    this.faceDetectModeSupplier = builder.faceDetectModeSupplier;
    this.cropRegionSupplier = builder.cropRegionSupplier;
    this.surfaces = builder.surfaces;
  }

  public CaptureRequest.Builder getRequestBuilder() {
    return requestBuilder;
  }

  public Supplier<FocusMode> getFocusModeSupplier() {
    return focusModeSupplier;
  }

  public Supplier<FlashMode> getFlashModeSupplier() {
    return flashModeSupplier;
  }

  public Supplier<FaceDetectMode> getFaceDetectModeSupplier() {
    return faceDetectModeSupplier;
  }

  public Supplier<Rect> getCropRegionSupplier() {
    return cropRegionSupplier;
  }

  public Set<Surface> getSurfaces() {
    return surfaces;
  }

  public CaptureRequest generateRequest() {

    for (Surface surface : surfaces) {
      requestBuilder.addTarget(surface);
    }

    requestBuilder.set(CaptureRequest.CONTROL_AF_MODE,
        focusModeSupplier.get().cameraFocusConstant);
    requestBuilder.set(CaptureRequest.CONTROL_AE_MODE,
        flashModeSupplier.get().cameraFlashConstant);
    requestBuilder.set(CaptureRequest.STATISTICS_FACE_DETECT_MODE,
        faceDetectModeSupplier.get().cameraFlashConstant);
    requestBuilder.set(CaptureRequest.SCALER_CROP_REGION,
        cropRegionSupplier.get());

    // TODO
    requestBuilder.set(CaptureRequest.CONTROL_AE_EXPOSURE_COMPENSATION, 0);
    requestBuilder.set(CaptureRequest.CONTROL_MODE,
        CaptureRequest.CONTROL_MODE_USE_SCENE_MODE);
    requestBuilder.set(CaptureRequest.CONTROL_SCENE_MODE,
        CaptureRequest.CONTROL_SCENE_MODE_FACE_PRIORITY);



    return requestBuilder.build();
  }

  public static class Builder {
    private final CaptureRequest.Builder requestBuilder;

    private Supplier<FocusMode> focusModeSupplier;
    private Supplier<FlashMode> flashModeSupplier;
    private Supplier<FaceDetectMode> faceDetectModeSupplier;
    private Supplier<Rect> cropRegionSupplier;
    private Set<Surface> surfaces = new HashSet<>();

    public Builder(CaptureRequest.Builder requestBuilder) {
      this.requestBuilder = requestBuilder;
    }

    public Builder withFocusModeSupplier(Supplier<FocusMode> supplier) {
      this.focusModeSupplier = supplier;
      return this;
    }

    public Builder withFlashModeSupplier(Supplier<FlashMode> supplier) {
      this.flashModeSupplier = supplier;
      return this;
    }

    public Builder withFaceDetectModeSupplier(Supplier<FaceDetectMode> supplier) {
      this.faceDetectModeSupplier = supplier;
      return this;
    }

    public Builder withCropRegionModeSupplier(Supplier<Rect> supplier) {
      this.cropRegionSupplier = supplier;
      return this;
    }

    public Builder addSurface(Surface surface) {
      if (surface == null){
        return this;
      }
      this.surfaces.add(surface);
      return this;
    }

    public RequestTemplate build() {
      return new RequestTemplate(this);
    }
  }
}
