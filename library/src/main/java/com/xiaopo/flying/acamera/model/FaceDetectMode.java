package com.xiaopo.flying.acamera.model;

import com.xiaopo.flying.acamera.ACameraCharacteristics;

import java.util.List;

/**
 * @author wupanjie
 */
public enum FaceDetectMode {
  FULL, SIMPLE, NONE;

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
