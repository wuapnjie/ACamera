package com.xiaopo.flying.acamera;

import android.util.Size;
import android.view.Surface;

import com.xiaopo.flying.acamera.base.SafeCloseable;
import com.xiaopo.flying.acamera.characterisitics.ACameraCharacteristics;
import com.xiaopo.flying.acamera.focus.FocusFunction;
import com.xiaopo.flying.acamera.model.Photo;
import com.xiaopo.flying.acamera.picturetaker.PictureTakeFunction;
import com.xiaopo.flying.acamera.preview.PreviewSizeSelector;
import com.xiaopo.flying.acamera.state.CameraStateManager;

import io.reactivex.Single;

/**
 * @author wupanjie
 */
public interface ACamera extends
    SafeCloseable,
    FocusFunction,
    PictureTakeFunction,
    PreviewSizeSelector {

  ACameraCharacteristics getCharacteristic();

  void startPreview(final Surface previewSurface);

  Size pickPreviewSize(Size imageResolution);

  // TODO AF STATE Observable<FocusResult>
  void triggerFocusAt(float x, float y);

  @Override
  Single<Photo> takePicture();

  CameraStateManager getStateManager();

  void close();
}
