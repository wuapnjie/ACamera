package com.xiaopo.flying.acamera.preview;

import android.graphics.Point;
import android.util.Log;
import android.util.Size;
import android.view.WindowManager;

import com.xiaopo.flying.acamera.util.AndroidServices;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Picks a preview size.
 * function and write tests.
 */
public class Camera2PreviewSizeSelector implements PreviewSizeSelector {
  private static final String TAG = "PreviewSizeSelector";
  private final List<Size> supportedPreviewSizes;

  public Camera2PreviewSizeSelector(List<Size> supportedPreviewSizes) {
    this.supportedPreviewSizes = new ArrayList<>(supportedPreviewSizes);
  }

  public Size pickPreviewSize(Size pictureSize) {
    if (pictureSize == null) {
      // TODO The default should be selected by the caller, and
      // pictureSize should never be null.
      pictureSize = getLargestPictureSize();
    }
    float pictureAspectRatio = pictureSize.getWidth() / (float) pictureSize.getHeight();

    return getOptimalPreviewSize(
        supportedPreviewSizes.toArray(new Size[supportedPreviewSizes.size()]),
        pictureAspectRatio);
  }

  /**
   * @return The largest supported picture size.
   */
  private Size getLargestPictureSize() {
    return Collections.max(supportedPreviewSizes, new Comparator<Size>() {
      @Override
      public int compare(Size size1, Size size2) {
        int area1 = size1.getWidth() * size1.getHeight();
        int area2 = size2.getWidth() * size2.getHeight();
        return Integer.compare(area1, area2);
      }
    });
  }

  /**
   * Returns the best preview size based on the current display resolution,
   * the available preview sizes, the target aspect ratio (typically the
   * aspect ratio of the picture to be taken) as well as a maximum allowed
   * tolerance. If tolerance is 'null', a default tolerance will be used.
   */
  private Size getOptimalPreviewSize(Size[] sizes, double targetRatio) {
    // TODO(andyhuibers): Don't hardcode this but use device's measurements.
    final int MAX_ASPECT_HEIGHT = 1080;

    // Count sizes with height <= 1080p to mimic camera1 api behavior.
    int count = 0;
    for (Size s : sizes) {
      if (s.getHeight() <= MAX_ASPECT_HEIGHT) {
        count++;
      }
    }
    ArrayList<Size> camera1Sizes = new ArrayList<Size>(count);

    // Set array of all sizes with height <= 1080p
    for (Size s : sizes) {
      if (s.getHeight() <= MAX_ASPECT_HEIGHT) {
        camera1Sizes.add(new Size(s.getWidth(), s.getHeight()));
      }
    }

    int optimalIndex =
        getOptimalPreviewSizeIndex(camera1Sizes, targetRatio, null);

    if (optimalIndex == -1) {
      return null;
    }

    Size optimal = camera1Sizes.get(optimalIndex);
    for (Size s : sizes) {
      if (s.getWidth() == optimal.getWidth() && s.getHeight() == optimal.getHeight()) {
        return s;
      }
    }
    return null;
  }

  private int getOptimalPreviewSizeIndex(List<Size> previewSizes, double targetRatio,
                                         Double aspectRatioTolerance) {
    if (previewSizes == null) {
      return -1;
    }

    // If no particular aspect ratio tolerance is set, use the default
    // value.
    if (aspectRatioTolerance == null) {
      return getOptimalPreviewSizeIndex(previewSizes, targetRatio);
    }

    int optimalSizeIndex = -1;
    double minDiff = Double.MAX_VALUE;

    // Because of bugs of overlay and layout, we sometimes will try to
    // layout the viewfinder in the portrait orientation and thus get the
    // wrong size of preview surface. When we change the preview size, the
    // new overlay will be created before the old one closed, which causes
    // an exception. For now, just get the screen size.
    Size defaultDisplaySize = getDefaultDisplaySize();
    int targetHeight = Math.min(defaultDisplaySize.getWidth(), defaultDisplaySize.getHeight());
    // Try to find an size match aspect ratio and size
    for (int i = 0; i < previewSizes.size(); i++) {
      Size size = previewSizes.get(i);
      double ratio = (double) size.getWidth() / size.getHeight();
      if (Math.abs(ratio - targetRatio) > aspectRatioTolerance) {
        continue;
      }

      double heightDiff = Math.abs(size.getHeight() - targetHeight);
      if (heightDiff < minDiff) {
        optimalSizeIndex = i;
        minDiff = heightDiff;
      } else if (heightDiff == minDiff) {
        // Prefer resolutions smaller-than-display when an equally close
        // larger-than-display resolution is available
        if (size.getHeight() < targetHeight) {
          optimalSizeIndex = i;
          minDiff = heightDiff;
        }
      }
    }
    // Cannot find the one match the aspect ratio. This should not happen.
    // Ignore the requirement.
    if (optimalSizeIndex == -1) {
      Log.w(TAG, "No preview size match the aspect ratio. available sizes: " + previewSizes);
      minDiff = Double.MAX_VALUE;
      for (int i = 0; i < previewSizes.size(); i++) {
        Size size = previewSizes.get(i);
        if (Math.abs(size.getHeight() - targetHeight) < minDiff) {
          optimalSizeIndex = i;
          minDiff = Math.abs(size.getHeight() - targetHeight);
        }
      }
    }

    return optimalSizeIndex;
  }

  private int getOptimalPreviewSizeIndex(List<Size> sizes, double targetRatio) {
    // Use a very small tolerance because we want an exact match. HTC 4:3
    // ratios is over .01 from true 4:3, so this value must be above .01,
    // see b/18241645.
    final double aspectRatioTolerance = 0.02;

    return getOptimalPreviewSizeIndex(sizes, targetRatio, aspectRatioTolerance);
  }

  private Size getDefaultDisplaySize() {
    WindowManager windowManager = AndroidServices.instance().provideWindowManager();
    Point res = new Point();
    windowManager.getDefaultDisplay().getSize(res);
    return new Size(res.x, res.y);
  }
}
