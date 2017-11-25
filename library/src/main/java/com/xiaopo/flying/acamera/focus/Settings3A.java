package com.xiaopo.flying.acamera.focus;

import android.hardware.camera2.params.MeteringRectangle;

/**
 * Contains 3A parameters common to all camera flavors.
 */
public class Settings3A {


  /**
   * Width of touch metering region in [0,1] relative to shorter edge of the
   * current crop region. Multiply this number by the number of pixels along
   * shorter edge of the current crop region's width to get a value in pixels.
   * <p>
   * This value has been tested on Nexus 5 and Shamu, but will need to be
   * tuned per device depending on how its ISP interprets the metering box and
   * weight.
   * </p>
   * <p>
   * Was fixed at 300px x 300px prior to L release.
   * </p>
   */
  private static final float GCAM_METERING_REGION_FRACTION = 0.1225f;


  /**
   * @return The weight to use for {@link MeteringRectangle}s for 3A.
   */
  public int getMeteringWeight() {
    // TODO Determine the optimal metering region for non-HDR photos.
    int weightMin = MeteringRectangle.METERING_WEIGHT_MIN;
    int weightRange = MeteringRectangle.METERING_WEIGHT_MAX - MeteringRectangle.METERING_WEIGHT_MIN;
    return (int) (weightMin + GCAM_METERING_REGION_FRACTION * weightRange);
  }

  /**
   * @return The size of (square) metering regions, normalized with respect to
   * the smallest dimension of the current crop-region.
   */
  public float getMeteringRegionFraction() {
    // TODO Determine the optimal metering weight for non-HDR photos.
    return GCAM_METERING_REGION_FRACTION;
  }

}
