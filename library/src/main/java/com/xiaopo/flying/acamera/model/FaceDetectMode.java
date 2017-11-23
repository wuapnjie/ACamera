package com.xiaopo.flying.acamera.model;

import android.hardware.camera2.CameraMetadata;

import com.xiaopo.flying.acamera.ACameraCharacteristics;

import java.util.List;

/**
 * @author wupanjie
 */
public enum FaceDetectMode {
  FULL(CameraMetadata.STATISTICS_FACE_DETECT_MODE_FULL),
  SIMPLE(CameraMetadata.STATISTICS_FACE_DETECT_MODE_SIMPLE),
  NONE(CameraMetadata.STATISTICS_FACE_DETECT_MODE_OFF);

  public final int cameraFlashConstant;

  FaceDetectMode(int cameraFlashConstant) {
    this.cameraFlashConstant = cameraFlashConstant;
  }

  public static FaceDetectMode highestFrom(ACameraCharacteristics characteristics) {
    List<FaceDetectMode> faceDetectModes = characteristics.getSupportedFaceDetectModes();

    if (faceDetectModes.contains(FaceDetectMode.FULL)) {
      return FaceDetectMode.FULL;
    } else if (faceDetectModes.contains(FaceDetectMode.SIMPLE)) {
      return FaceDetectMode.SIMPLE;
    } else {
      return FaceDetectMode.NONE;
    }
  }
}
