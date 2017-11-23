package com.xiaopo.flying.acamera;

import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.os.Handler;
import android.view.Surface;

import com.xiaopo.flying.acamera.base.Consumer;
import com.xiaopo.flying.acamera.base.Lifetime;
import com.xiaopo.flying.acamera.base.SafeCloseable;
import com.xiaopo.flying.acamera.base.Supplier;
import com.xiaopo.flying.acamera.base.optional.Optional;
import com.xiaopo.flying.acamera.command.CameraCommand;
import com.xiaopo.flying.acamera.command.CameraCommandBuilder;
import com.xiaopo.flying.acamera.command.CameraCommandCenter;
import com.xiaopo.flying.acamera.command.CameraCommandExecutor;
import com.xiaopo.flying.acamera.command.CameraCommandFactory;
import com.xiaopo.flying.acamera.command.CameraCommandType;
import com.xiaopo.flying.acamera.command.PreviewCommand;
import com.xiaopo.flying.acamera.preview.CaptureSessionCreator;
import com.xiaopo.flying.acamera.preview.PreviewStarter;
import com.xiaopo.flying.acamera.request.RequestFactory;

import java.util.ArrayList;

import io.reactivex.subjects.BehaviorSubject;

/**
 * @author wupanjie
 */
class ACameraFactory {

  private final CameraManager cameraManager;
  private final Handler cameraHandler;
  private final CameraDevice cameraDevice;

  private CameraCommandFactory commandFactory;
  private RequestFactory requestFactory;
  private ARealCameraCharacteristics aCameraCharacteristics;
  private BehaviorSubject<Surface> previewSurfaceSubject;
  private Lifetime lifetime = new Lifetime();

  ACameraFactory(CameraManager cameraManager, Handler cameraHandler, CameraDevice cameraDevice) {
    this.cameraManager = cameraManager;
    this.cameraHandler = cameraHandler;
    this.cameraDevice = cameraDevice;
  }

  public ACamera create() {
    try {
      final CaptureSessionCreator captureSessionCreator =
          new CaptureSessionCreator(cameraDevice, cameraHandler);

      CameraCharacteristics characteristics =
          cameraManager.getCameraCharacteristics(cameraDevice.getId());
      aCameraCharacteristics = new ARealCameraCharacteristics(characteristics);

      previewSurfaceSubject = BehaviorSubject.create();

      initRequestFactory();

      initCommandSystem();


      PreviewStarter previewStarter = new PreviewStarter(
          new ArrayList<Surface>(0),
          captureSessionCreator,
          commandFactory
      );

      lifetime.add(previewStarter);
      lifetime.add(new SafeCloseableHolder<>(cameraDevice));

      return new ARealCamera(
          lifetime,
          aCameraCharacteristics,
          previewStarter,
          previewSurfaceSubject);
    } catch (CameraAccessException e) {
      e.printStackTrace();
    }

    return null;
  }

  private void initRequestFactory() {
    requestFactory = new RequestFactory(cameraDevice, aCameraCharacteristics, previewSurfaceSubject);
  }

  private void initCommandSystem() {
    CameraCommandCenter commandCenter = CameraCommandCenter.getInstance();

    CameraCommandExecutor commandExecutor = new CameraCommandExecutor();
    commandCenter.registerExecutor(commandExecutor);
    lifetime.add(commandExecutor);

    commandFactory = CameraCommandFactory.factory(new Consumer<CameraCommandBuilder>() {
      @Override
      public void accept(CameraCommandBuilder cameraCommandBuilder) {
        cameraCommandBuilder.add(CameraCommandType.PREVIEW, new Supplier<CameraCommand>() {
          @Override
          public CameraCommand get() {
            return new PreviewCommand(requestFactory.createPreviewTemplate(), cameraHandler);
          }
        });
      }
    });
  }

  class SafeCloseableHolder<T extends AutoCloseable> implements SafeCloseable {

    T t;

    SafeCloseableHolder(T t) {
      this.t = t;
    }


    @Override
    public void close() {
      try {
        t.close();
      } catch (Exception e) {
        e.printStackTrace();
      }

      t = null;
    }
  }

}
