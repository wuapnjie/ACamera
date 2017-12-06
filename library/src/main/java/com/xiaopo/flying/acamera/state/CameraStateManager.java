package com.xiaopo.flying.acamera.state;

import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.hardware.camera2.params.MeteringRectangle;
import android.util.Size;

import com.xiaopo.flying.acamera.base.SafeCloseable;
import com.xiaopo.flying.acamera.base.Supplier;
import com.xiaopo.flying.acamera.characterisitics.ACameraCharacteristics;
import com.xiaopo.flying.acamera.focus.AEMeteringRegionSupplier;
import com.xiaopo.flying.acamera.focus.AFMeteringRegionSupplier;
import com.xiaopo.flying.acamera.focus.GlobalMeteringParameters;
import com.xiaopo.flying.acamera.focus.MeteringParameters;
import com.xiaopo.flying.acamera.focus.ZoomedCropRegionSupplier;
import com.xiaopo.flying.acamera.model.FaceDetectMode;
import com.xiaopo.flying.acamera.model.FlashMode;
import com.xiaopo.flying.acamera.model.FocusMode;
import com.xiaopo.flying.acamera.util.OrientationUtil;

import java.util.Collections;
import java.util.Comparator;

/**
 * @author wupanjie
 */
public class CameraStateManager implements SafeCloseable{
  private final CameraState<FocusMode> focusModeState;
  private final CameraState<FlashMode> flashModeState;
  private final CameraState<FaceDetectMode> faceDetectModeState;
  private final CameraState<Float> zoomState;
  private final CameraState<MeteringParameters> meteringState;
  private final CameraState<Size> pictureSizeState;
  private final CameraState<Byte> jpegQualityState;
  private final CameraState<Integer> aeExposureCompensationState;

  private final Supplier<Rect> zoomedCropRegion;
  private final Supplier<MeteringRectangle[]> aeRegionSupplier;
  private final Supplier<MeteringRectangle[]> afRegionSupplier;
  private final Supplier<Integer> imageRotationSupplier;

  public CameraStateManager(final ACameraCharacteristics characteristics) {
    focusModeState = new CameraState<>(FocusMode.CONTINUOUS_PICTURE);
    flashModeState = new CameraState<>(FlashMode.AUTO);
    faceDetectModeState = new CameraState<>(FaceDetectMode.SIMPLE);
    zoomState = new CameraState<>(1.0f);
    meteringState = new CameraState<>(GlobalMeteringParameters.create());
    pictureSizeState = new CameraState<>(Collections.max(characteristics.getSupportedPictureSizes(ImageFormat.JPEG), new Comparator<Size>() {
      @Override
      public int compare(Size lhs, Size rhs) {
        return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
            (long) rhs.getWidth() * rhs.getHeight());
      }
    }));
    jpegQualityState = new CameraState<>((byte)90);
    aeExposureCompensationState = new CameraState<>(0);

    zoomedCropRegion = new ZoomedCropRegionSupplier(characteristics.getSensorInfoActiveArraySize(), zoomState);
    aeRegionSupplier = new AEMeteringRegionSupplier(meteringState, zoomedCropRegion);
    afRegionSupplier = new AFMeteringRegionSupplier(meteringState, zoomedCropRegion);

    imageRotationSupplier = new Supplier<Integer>() {
      @Override
      public Integer get() {
        return OrientationUtil.calculateImageRotation(characteristics);
      }
    };
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

  public CameraState<Size> getPictureSizeState() {
    return pictureSizeState;
  }

  public CameraState<Byte> getJpegQualityState() {
    return jpegQualityState;
  }

  public CameraState<Integer> getAeExposureCompensationState() {
    return aeExposureCompensationState;
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

  public Supplier<Integer> getImageRotationSupplier() {
    return imageRotationSupplier;
  }

  @Override
  public void close() {
    focusModeState.close();
    flashModeState.close();
    faceDetectModeState.close();
    zoomState.close();
    pictureSizeState.close();
    meteringState.close();
  }
}
