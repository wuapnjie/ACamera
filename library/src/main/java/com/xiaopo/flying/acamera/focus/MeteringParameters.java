package com.xiaopo.flying.acamera.focus;

import android.graphics.Rect;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.MeteringRectangle;


public interface MeteringParameters {
  /**
   * @param cropRegion The current crop region, see
   *                   {@link CaptureRequest#SCALER_CROP_REGION}.
   * @return The current auto-focus metering regions.
   */
  MeteringRectangle[] getAFRegions(Rect cropRegion);

  /**
   * @param cropRegion The current crop region, see
   *                   {@link CaptureRequest#SCALER_CROP_REGION}.
   * @return The current auto-exposure metering regions.
   */
  MeteringRectangle[] getAERegions(Rect cropRegion);
}
