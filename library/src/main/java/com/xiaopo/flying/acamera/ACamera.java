package com.xiaopo.flying.acamera;

import android.util.Size;
import android.view.Surface;

/**
 * @author wupanjie
 */
public interface ACamera {

  ACameraCharacteristics getCharacteristic();

  void startPreview(final Surface previewSurface);

  Size pickPreviewSize(Size imageResolution);
}
