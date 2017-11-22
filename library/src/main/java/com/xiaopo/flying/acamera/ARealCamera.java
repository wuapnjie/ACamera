package com.xiaopo.flying.acamera;

import android.util.Size;
import android.view.Surface;

import com.xiaopo.flying.acamera.preview.Camera2PreviewSizeSelector;
import com.xiaopo.flying.acamera.preview.PreviewSizeSelector;
import com.xiaopo.flying.acamera.preview.PreviewStarter;

/**
 * @author wupanjie
 */
class ARealCamera implements ACamera {

  private final PreviewStarter previewStarter;
  private final ACameraCharacteristics characteristics;
  private final PreviewSizeSelector previewSizeSelector;

  public ARealCamera(ACameraCharacteristics characteristics, PreviewStarter previewStarter) {
    this.characteristics = characteristics;
    this.previewStarter = previewStarter;
    this.previewSizeSelector = new Camera2PreviewSizeSelector(characteristics.getSupportedPreviewSizes());
  }

  @Override
  public ACameraCharacteristics getCharacteristic() {
    return characteristics;
  }


  @Override
  public void startPreview(final Surface previewSurface) {
    previewStarter
        .startPreview(previewSurface)
        .subscribe();
  }

  @Override
  public Size pickPreviewSize(Size imageResolution) {
    return previewSizeSelector.pickPreviewSize(imageResolution);
  }

}
