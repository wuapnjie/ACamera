package com.xiaopo.flying.acamera.util;

import android.view.Surface;

import com.xiaopo.flying.acamera.characterisitics.ACameraCharacteristics;
import com.xiaopo.flying.acamera.characterisitics.ARealCameraCharacteristics;
import com.xiaopo.flying.acamera.model.LensFacing;

/**
 * @author wupanjie
 */
public final class OrientationUtil {

  /**
   * @return rotation of the screen in degrees.
   */
  public static int getScreenRotation() {
    int rotation = AndroidServices.instance()
        .provideWindowManager()
        .getDefaultDisplay()
        .getRotation();

    switch (rotation) {
      case Surface.ROTATION_90:
        return 90;
      case Surface.ROTATION_180:
        return 180;
      case Surface.ROTATION_270:
        return 270;
      case Surface.ROTATION_0:
      default:
        return 0;
    }
  }

  public static Integer calculateImageRotation(ACameraCharacteristics characteristics) {
    final boolean isFrontCamera = characteristics.getLensFacing() == LensFacing.FRONT;
    int deviceOrientation = getScreenRotation();
    final int sensorOrientation = characteristics.getSensorOrientation();


    // The sensor of front camera faces in the opposite direction from back camera.
    if (isFrontCamera) {
      deviceOrientation = (360 - deviceOrientation) % 360;
    }

    int degrees = (sensorOrientation + deviceOrientation) % 360;
    switch (degrees) {
      case (-1):  // UNKNOWN Orientation
        // Explicitly default to CLOCKWISE_0, when Orientation is UNKNOWN
        return 0;
      case 0:
        return 0;
      case 90:
        return 90;
      case 180:
        return 180;
      case 270:
        return 270;
      default:
        int normalizedDegrees = (Math.abs(degrees / 360) * 360 + 360 + degrees) % 360;
        if (normalizedDegrees > 315 || normalizedDegrees <= 45) {
          return 0;
        } else if (normalizedDegrees > 45 && normalizedDegrees <= 135) {
          return 90;
        } else if (normalizedDegrees > 135 && normalizedDegrees <= 225) {
          return 180;
        } else {
          return 270;
        }
    }
  }
}
