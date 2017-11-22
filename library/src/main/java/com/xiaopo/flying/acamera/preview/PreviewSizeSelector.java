package com.xiaopo.flying.acamera.preview;

import android.util.Size;

/**
 * Picks a preview size for a given image resolution choice.
 */
public interface PreviewSizeSelector {

  /**
   * Given an image capture resolution, pick a preview size.
   */
  Size pickPreviewSize(Size imageResolution);
}
