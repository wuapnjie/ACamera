package com.xiaopo.flying.acamera.request;

import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CaptureRequest;
import android.view.Surface;

import com.xiaopo.flying.acamera.state.CameraStateManager;

import io.reactivex.subjects.BehaviorSubject;

/**
 * @author wupanjie
 */
public class RequestFactory {

  private final CameraDevice cameraDevice;
  private final CameraStateManager cameraStateManager;
  private final BehaviorSubject<Surface> previewSurfaceSubject;

  public RequestFactory(CameraDevice cameraDevice,
                        CameraStateManager cameraStateManager,
                        BehaviorSubject<Surface> previewSurfaceSubject) {
    this.cameraDevice = cameraDevice;
    this.cameraStateManager = cameraStateManager;
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

  public RequestTemplate.Builder createPreviewTemplate() {

    return create(CameraDevice.TEMPLATE_PREVIEW)
        .withFocusModeSupplier(cameraStateManager.getFocusModeState())
        .withFaceDetectModeSupplier(cameraStateManager.getFaceDetectModeState())
        .withFlashModeSupplier(cameraStateManager.getFlashModeState())
        .withCropRegionModeSupplier(cameraStateManager.getZoomedCropRegion())
        .withAeRegionsSupplier(cameraStateManager.getAeRegionSupplier())
        .withAfRegionsSupplier(cameraStateManager.getAfRegionSupplier())
        .addSurface(previewSurfaceSubject.getValue());
  }

  public RequestTemplate.Builder createAFIdleTemplate() {

    return createPreviewTemplate()
        .withParam(CaptureRequest.CONTROL_MODE, CaptureRequest.CONTROL_MODE_AUTO)
        .withParam(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_AUTO)
        .withParam(CaptureRequest.CONTROL_AF_TRIGGER, CaptureRequest.CONTROL_AF_TRIGGER_IDLE);
  }

  public RequestTemplate.Builder createAFTriggerTemplate() {

    return createPreviewTemplate()
        .withParam(CaptureRequest.CONTROL_MODE, CaptureRequest.CONTROL_MODE_AUTO)
        .withParam(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_AUTO)
        .withParam(CaptureRequest.CONTROL_AF_TRIGGER, CaptureRequest.CONTROL_AF_TRIGGER_START);
  }

  public RequestTemplate.Builder createAFCancelTemplate() {

    return createPreviewTemplate()
        .withParam(CaptureRequest.CONTROL_MODE, CaptureRequest.CONTROL_MODE_AUTO)
        .withParam(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_AUTO)
        .withParam(CaptureRequest.CONTROL_AF_TRIGGER, CaptureRequest.CONTROL_AF_TRIGGER_CANCEL);
  }
}
