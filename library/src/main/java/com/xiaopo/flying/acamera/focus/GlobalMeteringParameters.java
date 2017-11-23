package com.xiaopo.flying.acamera.focus;

import android.graphics.Rect;
import android.hardware.camera2.params.MeteringRectangle;


public final class GlobalMeteringParameters implements MeteringParameters {
  /**
   * Zero weight 3A region, to reset regions per API.
   */
  private static final MeteringRectangle[] ZERO_WEIGHT_3A_REGION = new MeteringRectangle[]{
      new MeteringRectangle(0, 0, 0, 0, 0)
  };

  private static class Singleton {
    private static final GlobalMeteringParameters INSTANCE = new GlobalMeteringParameters();
  }

  public static MeteringParameters create() {
    return Singleton.INSTANCE;
  }

  @Override
  public MeteringRectangle[] getAFRegions(Rect cropRegion) {
    return ZERO_WEIGHT_3A_REGION;
  }

  @Override
  public MeteringRectangle[] getAERegions(Rect cropRegion) {
    return ZERO_WEIGHT_3A_REGION;
  }
}
