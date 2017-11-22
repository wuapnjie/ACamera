package com.xiaopo.flying.acamera.preview;

import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.os.Handler;
import android.view.Surface;

import com.xiaopo.flying.acamera.ACameraCharacteristics;
import com.xiaopo.flying.acamera.base.Consumer;
import com.xiaopo.flying.acamera.base.Supplier;
import com.xiaopo.flying.acamera.command.CameraCommand;
import com.xiaopo.flying.acamera.command.CameraCommandBuilder;
import com.xiaopo.flying.acamera.command.CameraCommandCenter;
import com.xiaopo.flying.acamera.command.CameraCommandExecutor;
import com.xiaopo.flying.acamera.command.CameraCommandFactory;
import com.xiaopo.flying.acamera.command.CameraCommandType;
import com.xiaopo.flying.acamera.command.PreviewCommand;
import com.xiaopo.flying.acamera.util.ApiHelper;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * @author wupanjie
 */
public class PreviewStarter {

  private final List<Surface> outputSurfaces;
  private final CaptureSessionCreator captureSessionCreator;
  private final CameraDevice cameraDevice;
  private final ACameraCharacteristics cameraCharacteristics;
  private final Handler cameraHandler;

  public PreviewStarter(List<Surface> outputSurfaces,
                        CaptureSessionCreator captureSessionCreator,
                        CameraDevice cameraDevice,
                        ACameraCharacteristics cameraCharacteristics,
                        Handler cameraHandler) {
    this.outputSurfaces = outputSurfaces;
    this.captureSessionCreator = captureSessionCreator;
    this.cameraDevice = cameraDevice;
    this.cameraCharacteristics = cameraCharacteristics;
    this.cameraHandler = cameraHandler;
  }


  public Completable startPreview(final Surface previewSurface) {
    // When we have the preview surface, start the capture session.
    List<Surface> surfaceList = new ArrayList<>();

    // Workaround of the face detection failure on Nexus 5 and L. (b/21039466)
    // Need to create a capture session with the single preview stream first
    // to lock it as the first stream. Then resend the another session with preview
    // and JPEG stream.
    if (ApiHelper.isLorLMr1() && ApiHelper.IS_NEXUS_5) {
      surfaceList.add(previewSurface);
      captureSessionCreator.createCaptureSession(surfaceList).subscribe();
      surfaceList.addAll(outputSurfaces);
    } else {
      surfaceList.addAll(outputSurfaces);
      surfaceList.add(previewSurface);
    }

    return Completable.fromObservable(
        captureSessionCreator
            .createCaptureSession(surfaceList)
            .subscribeOn(Schedulers.io())
            .flatMap(new Function<CameraCaptureSession, ObservableSource<?>>() {
              @Override
              public ObservableSource<?> apply(final CameraCaptureSession captureSession) throws Exception {

                CameraCommandCenter commandCenter = CameraCommandCenter.getInstance();
                CameraCommandExecutor commandExecutor = new CameraCommandExecutor();

                commandCenter.registerExecutor(commandExecutor);

                CameraCommandFactory commandFactory = CameraCommandFactory.factory(new Consumer<CameraCommandBuilder>() {
                  @Override
                  public void accept(CameraCommandBuilder cameraCommandBuilder) {
                    cameraCommandBuilder.add(CameraCommandType.PREVIEW, new Supplier<CameraCommand>() {
                      @Override
                      public CameraCommand get() {
                        return new PreviewCommand(previewSurface,
                            cameraDevice,
                            cameraCharacteristics,
                            captureSession,
                            cameraHandler);
                      }
                    });
                  }
                });

                commandCenter.nextCommand(commandFactory.create(CameraCommandType.PREVIEW));

                return Observable.empty();
              }
            })
    );
  }

}
