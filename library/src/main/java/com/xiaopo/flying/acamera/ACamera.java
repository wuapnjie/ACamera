package com.xiaopo.flying.acamera;

import android.util.Size;
import android.view.Surface;

import com.xiaopo.flying.acamera.base.SafeCloseable;

/**
 * @author wupanjie
 */
public interface ACamera extends SafeCloseable {

  ACameraCharacteristics getCharacteristic();

  void startPreview(final Surface previewSurface);

  Size pickPreviewSize(Size imageResolution);

  void triggerFocusAt(float x, float y);

  void close();
}
