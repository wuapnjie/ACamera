package com.xiaopo.flying.acamera.state;

import android.graphics.Rect;
import android.hardware.camera2.params.MeteringRectangle;

import com.xiaopo.flying.acamera.ACameraCharacteristics;
import com.xiaopo.flying.acamera.base.Supplier;
import com.xiaopo.flying.acamera.focus.AEMeteringRegionSupplier;
import com.xiaopo.flying.acamera.focus.AFMeteringRegionSupplier;
import com.xiaopo.flying.acamera.focus.GlobalMeteringParameters;
import com.xiaopo.flying.acamera.focus.MeteringParameters;
import com.xiaopo.flying.acamera.model.FaceDetectMode;
import com.xiaopo.flying.acamera.model.FlashMode;
import com.xiaopo.flying.acamera.model.FocusMode;
import com.xiaopo.flying.acamera.focus.ZoomedCropRegionSupplier;

/**
 * @author wupanjie
 */
public class CameraStateManager {
  private final CameraState<FocusMode> focusModeState;
  private final CameraState<FlashMode> flashModeState;
  private final CameraState<FaceDetectMode> faceDetectModeState;
  private final CameraState<Float> zoomState;
  private final CameraState<MeteringParameters> meteringState;

  private final Supplier<Rect> zoomedCropRegion;
  private final Supplier<MeteringRectangle[]> aeRegionSupplier;
  private final Supplier<MeteringRectangle[]> afRegionSupplier;

  public CameraStateManager(ACameraCharacteristics characteristics) {
    focusModeState = new CameraState<>(FocusMode.CONTINUOUS_PICTURE);
    flashModeState = new CameraState<>(FlashMode.AUTO);
    faceDetectModeState = new CameraState<>(FaceDetectMode.SIMPLE);
    zoomState = new CameraState<>(1.0f);
    meteringState = new CameraState<>(GlobalMeteringParameters.create());

    zoomedCropRegion = new ZoomedCropRegionSupplier(characteristics.getSensorInfoActiveArraySize(), zoomState);
    aeRegionSupplier = new AEMeteringRegionSupplier(meteringState, zoomedCropRegion);
    afRegionSupplier = new AFMeteringRegionSupplier(meteringState, zoomedCropRegion);
  }

  public CameraState<FaceDetectMode> getFaceDetectModeState() {
    return faceDetectModeState;
  }

  public CameraState<FocusMode> getFocusModeState() {
    return focusModeState;
  }

  public CameraState<FlashMode> getFlashModeState() {
    return flashModeState;
  }

  public CameraState<Float> getZoomState() {
    return zoomState;
  }

  public CameraState<MeteringParameters> getMeteringState() {
    return meteringState;
  }

  public Supplier<Rect> getZoomedCropRegion() {
    return zoomedCropRegion;
  }

  public Supplier<MeteringRectangle[]> getAeRegionSupplier() {
    return aeRegionSupplier;
  }

  public Supplier<MeteringRectangle[]> getAfRegionSupplier() {
    return afRegionSupplier;
  }
}
