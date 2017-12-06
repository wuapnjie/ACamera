package com.xiaopo.flying.acamera.request;

import android.graphics.Rect;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.MeteringRectangle;
import android.view.Surface;

import com.xiaopo.flying.acamera.base.Supplier;
import com.xiaopo.flying.acamera.model.FaceDetectMode;
import com.xiaopo.flying.acamera.model.FlashMode;
import com.xiaopo.flying.acamera.model.FocusMode;
import com.xiaopo.flying.acamera.result.CaptureListener;
import com.xiaopo.flying.acamera.result.CompositeCaptureListener;
import com.xiaopo.flying.acamera.result.ForwardCaptureCallback;

import java.util.HashMap;
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
  private final Supplier<MeteringRectangle[]> aeRegionsSupplier;
  private final Supplier<MeteringRectangle[]> afRegionsSupplier;
  private final Supplier<Integer> imageRotationSupplier;
  private final Supplier<Integer> aeExposureCompensationSupplier;
  private final Supplier<Byte> jpegQualitySupplier;
  private final HashSet<Surface> surfaces;
  private final HashMap<CaptureRequest.Key, ?> params;
  private final CaptureListener captureListener;

  private RequestTemplate(Builder builder) {
    this.requestBuilder = builder.requestBuilder;

    this.focusModeSupplier = builder.focusModeSupplier;
    this.flashModeSupplier = builder.flashModeSupplier;
    this.faceDetectModeSupplier = builder.faceDetectModeSupplier;
    this.cropRegionSupplier = builder.cropRegionSupplier;
    this.aeRegionsSupplier = builder.aeRegionsSupplier;
    this.afRegionsSupplier = builder.afRegionsSupplier;
    this.imageRotationSupplier = builder.imageRotationSupplier;
    this.aeExposureCompensationSupplier = builder.aeExposureCompensationSupplier;
    this.jpegQualitySupplier = builder.jpegQualitySupplier;
    this.surfaces = builder.surfaces;
    this.params = builder.params;
    this.captureListener = builder.captureListener;
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

  public CameraCaptureSession.CaptureCallback getCaptureCallback() {
    return new ForwardCaptureCallback(captureListener);
  }

  public CaptureRequest generateRequest() {

    for (Surface surface : surfaces) {
      requestBuilder.addTarget(surface);
    }

    if (focusModeSupplier != null) {
      requestBuilder.set(CaptureRequest.CONTROL_AF_MODE,
          focusModeSupplier.get().cameraFocusConstant);
    }
    if (flashModeSupplier != null) {
      requestBuilder.set(CaptureRequest.CONTROL_AE_MODE,
          flashModeSupplier.get().cameraFlashConstant);
    }
    if (faceDetectModeSupplier != null) {
      requestBuilder.set(CaptureRequest.STATISTICS_FACE_DETECT_MODE,
          faceDetectModeSupplier.get().cameraFlashConstant);
    }
    if (cropRegionSupplier != null) {
      requestBuilder.set(CaptureRequest.SCALER_CROP_REGION,
          cropRegionSupplier.get());
    }
    if (aeRegionsSupplier != null) {
      requestBuilder.set(CaptureRequest.CONTROL_AE_REGIONS,
          aeRegionsSupplier.get());
    }
    if (afRegionsSupplier != null) {
      requestBuilder.set(CaptureRequest.CONTROL_AF_REGIONS,
          afRegionsSupplier.get());
    }
    if (imageRotationSupplier != null) {
      requestBuilder.set(CaptureRequest.JPEG_ORIENTATION,
          imageRotationSupplier.get());
    }
    if (aeExposureCompensationSupplier != null) {
      Integer value = aeExposureCompensationSupplier.get();
      if (value != 0) {
        requestBuilder.set(CaptureRequest.CONTROL_AE_LOCK, true);
      }
      requestBuilder.set(CaptureRequest.CONTROL_AE_EXPOSURE_COMPENSATION,
          value);
    }
    if (jpegQualitySupplier != null) {
      requestBuilder.set(CaptureRequest.JPEG_QUALITY,
          jpegQualitySupplier.get());
    }

    // TODO to be supplier
    requestBuilder.set(CaptureRequest.CONTROL_MODE,
        CaptureRequest.CONTROL_MODE_USE_SCENE_MODE);
    requestBuilder.set(CaptureRequest.CONTROL_SCENE_MODE,
        CaptureRequest.CONTROL_SCENE_MODE_FACE_PRIORITY);

    for (CaptureRequest.Key key : params.keySet()) {
      requestBuilder.set(key, params.get(key));
    }

    return requestBuilder.build();
  }


  public static class Builder {
    private final CaptureRequest.Builder requestBuilder;

    private Supplier<FocusMode> focusModeSupplier;
    private Supplier<FlashMode> flashModeSupplier;
    private Supplier<FaceDetectMode> faceDetectModeSupplier;
    private Supplier<Rect> cropRegionSupplier;
    private Supplier<MeteringRectangle[]> aeRegionsSupplier;
    private Supplier<MeteringRectangle[]> afRegionsSupplier;
    private Supplier<Integer> imageRotationSupplier;
    private Supplier<Integer> aeExposureCompensationSupplier;
    private Supplier<Byte> jpegQualitySupplier;
    private HashSet<Surface> surfaces = new HashSet<>();
    private HashMap<CaptureRequest.Key, Object> params = new HashMap<>();
    private CompositeCaptureListener captureListener = new CompositeCaptureListener();

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
      if (surface == null) {
        return this;
      }
      this.surfaces.add(surface);
      return this;
    }

    public Builder withAeRegionsSupplier(Supplier<MeteringRectangle[]> aeRegionsSupplier) {
      this.aeRegionsSupplier = aeRegionsSupplier;
      return this;
    }

    public Builder withAfRegionsSupplier(Supplier<MeteringRectangle[]> afRegionsSupplier) {
      this.afRegionsSupplier = afRegionsSupplier;
      return this;
    }

    public Builder withImageRotationSupplier(Supplier<Integer> imageRotationSupplier) {
      this.imageRotationSupplier = imageRotationSupplier;
      return this;
    }

    public Builder withAeExposureCompensationSupplier(Supplier<Integer> aeExposureCompensationSupplier) {
      this.aeExposureCompensationSupplier = aeExposureCompensationSupplier;
      return this;
    }

    public Builder withJpegQualitySupplier(Supplier<Byte> jpegQualitySupplier) {
      this.jpegQualitySupplier = jpegQualitySupplier;
      return this;
    }

    public <T> Builder withParam(CaptureRequest.Key<T> key, T value) {
      this.params.put(key, value);
      return this;
    }

    public Builder addListener(CaptureListener listener) {
      this.captureListener.add(listener);
      return this;
    }

    public RequestTemplate build() {
      return new RequestTemplate(this);
    }
  }
}
