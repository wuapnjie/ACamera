package com.xiaopo.flying.acamera;

import android.util.Size;
import android.view.Surface;

import com.xiaopo.flying.acamera.base.Lifetime;
import com.xiaopo.flying.acamera.characterisitics.ACameraCharacteristics;
import com.xiaopo.flying.acamera.focus.AutoFocusTrigger;
import com.xiaopo.flying.acamera.model.AutoFocusState;
import com.xiaopo.flying.acamera.model.Photo;
import com.xiaopo.flying.acamera.picturetaker.PictureTaker;
import com.xiaopo.flying.acamera.preview.Camera2PreviewSizeSelector;
import com.xiaopo.flying.acamera.preview.PreviewSizeSelector;
import com.xiaopo.flying.acamera.preview.PreviewStarter;
import com.xiaopo.flying.acamera.state.CameraStateManager;

import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.subjects.PublishSubject;

/**
 * @author wupanjie
 */
class ARealCamera implements ACamera {

  private final Lifetime lifetime;
  private final PreviewStarter previewStarter;
  private final ACameraCharacteristics characteristics;
  private final PreviewSizeSelector previewSizeSelector;
  private final AutoFocusTrigger focusTrigger;
  private final PublishSubject<AutoFocusState> afStateSubject;
  private final PictureTaker pictureTaker;
  private final CameraStateManager stateManager;

  private Surface currentPreviewSurface;

  private boolean closed;

  ARealCamera(Lifetime lifetime,
              ACameraCharacteristics characteristics,
              PreviewStarter previewStarter,
              AutoFocusTrigger focusTrigger,
              PublishSubject<AutoFocusState> afStateSubject,
              PictureTaker pictureTaker,
              CameraStateManager stateManager) {
    this.lifetime = lifetime;
    this.characteristics = characteristics;
    this.previewStarter = previewStarter;
    this.previewSizeSelector = new Camera2PreviewSizeSelector(characteristics.getSupportedPreviewSizes());
    this.focusTrigger = focusTrigger;
    this.afStateSubject = afStateSubject;
    this.pictureTaker = pictureTaker;
    this.stateManager = stateManager;
  }

  @Override
  public ACameraCharacteristics getCharacteristic() {
    return characteristics;
  }


  @Override
  public void startPreview(final Surface previewSurface) {
    if (closed) return;

    currentPreviewSurface = previewSurface;

    previewStarter.setPreviewSurface(previewSurface);
    previewStarter
        .startPreview()
        .subscribe();
  }

  @Override
  public Size pickPreviewSize(Size imageResolution) {
    return previewSizeSelector.pickPreviewSize(imageResolution);
  }

  @Override
  public void triggerFocusAt(float x, float y) {
    if (closed) return;
    focusTrigger.triggerFocusAt(x, y);
  }

  @Override
  public void observeAFState(Observer<AutoFocusState> observer) {
    afStateSubject.subscribe(observer);
  }

  @Override
  public Single<Photo> takePicture() {
    if (closed) return Single.error(new ACameraException("The camera is closed"));
    return pictureTaker.takePicture();
  }

  @Override
  public CameraStateManager getStateManager() {
    return stateManager;
  }

  @Override
  public void close() {
    closed = true;
    lifetime.close();
    currentPreviewSurface.release();
    currentPreviewSurface = null;
  }
}
