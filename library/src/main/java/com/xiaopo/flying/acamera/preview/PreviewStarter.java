package com.xiaopo.flying.acamera.preview;

import android.hardware.camera2.CameraCaptureSession;
import android.view.Surface;

import com.xiaopo.flying.acamera.base.SafeCloseable;
import com.xiaopo.flying.acamera.command.CameraCommandCenter;
import com.xiaopo.flying.acamera.command.CameraCommandFactory;
import com.xiaopo.flying.acamera.command.CameraCommandType;
import com.xiaopo.flying.acamera.util.ApiHelper;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;

/**
 * @author wupanjie
 */
public class PreviewStarter implements SafeCloseable {

  private final List<Surface> outputSurfaces;
  private final CaptureSessionCreator captureSessionCreator;
  private final CameraCommandFactory commandFactory;
  private CameraCaptureSession currentPreviewSession;
  private BehaviorSubject<Surface> previewSurfaceSubject;

  public PreviewStarter(List<Surface> outputSurfaces,
                        CaptureSessionCreator captureSessionCreator,
                        CameraCommandFactory commandFactory,
                        BehaviorSubject<Surface> previewSurfaceSubject) {
    this.outputSurfaces = outputSurfaces;
    this.captureSessionCreator = captureSessionCreator;
    this.commandFactory = commandFactory;
    this.previewSurfaceSubject = previewSurfaceSubject;
  }

  public Completable startPreview() {
    // When we have the preview surface, start the capture session.
    List<Surface> surfaceList = new ArrayList<>();

    // Workaround of the face detection failure on Nexus 5 and L. (b/21039466)
    // Need to create a capture session with the single preview stream first
    // to lock it as the first stream. Then resend the another session with preview
    // and JPEG stream.
    if (ApiHelper.isLorLMr1() && ApiHelper.IS_NEXUS_5) {
      surfaceList.add(previewSurfaceSubject.getValue());
      captureSessionCreator.createCaptureSession(surfaceList).subscribe();
      surfaceList.addAll(outputSurfaces);
    } else {
      surfaceList.addAll(outputSurfaces);
      surfaceList.add(previewSurfaceSubject.getValue());
    }

    return Completable.fromObservable(
        captureSessionCreator
            .createCaptureSession(surfaceList)
            .subscribeOn(Schedulers.io())
            .flatMap(new Function<CameraCaptureSession, ObservableSource<?>>() {
              @Override
              public ObservableSource<?> apply(final CameraCaptureSession captureSession) throws Exception {

                currentPreviewSession = captureSession;
                CameraCommandCenter.getInstance()
                    .nextCommand(commandFactory.create(CameraCommandType.PREVIEW));

                return Observable.empty();
              }
            })
    );
  }

  public void setPreviewSurface(Surface previewSurface) {
    previewSurfaceSubject.onNext(previewSurface);
  }

  @Override
  public void close() {
    currentPreviewSession.close();
    previewSurfaceSubject.onComplete();
  }
}
