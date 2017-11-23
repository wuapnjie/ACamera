package com.xiaopo.flying.acamera.focus;

import android.graphics.Rect;
import android.hardware.camera2.params.MeteringRectangle;

import com.xiaopo.flying.acamera.base.Supplier;


/**
 * Computes the current AF metering rectangles based on the current metering
 * parameters and crop region.
 */
public class AFMeteringRegionSupplier implements Supplier<MeteringRectangle[]> {
  private final Supplier<MeteringParameters> mMeteringParameters;
  private final Supplier<Rect> mCropRegion;

  public AFMeteringRegionSupplier(Supplier<MeteringParameters> meteringParameters,
                                  Supplier<Rect> cropRegion) {
    mMeteringParameters = meteringParameters;
    mCropRegion = cropRegion;
  }

  @Override
  public MeteringRectangle[] get() {
    return mMeteringParameters.get().getAFRegions(mCropRegion.get());
  }
}
