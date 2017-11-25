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
import com.xiaopo.flying.acamera.characterisitics.ARealCameraCharacteristics;
import com.xiaopo.flying.acamera.command.CameraCommand;
import com.xiaopo.flying.acamera.command.CameraCommandBuilder;
import com.xiaopo.flying.acamera.command.CameraCommandCenter;
import com.xiaopo.flying.acamera.command.CameraCommandExecutor;
import com.xiaopo.flying.acamera.command.CameraCommandFactory;
import com.xiaopo.flying.acamera.command.CameraCommandType;
import com.xiaopo.flying.acamera.command.FullAFScanCommand;
import com.xiaopo.flying.acamera.command.PreviewCommand;
import com.xiaopo.flying.acamera.focus.FocusTrigger;
import com.xiaopo.flying.acamera.preview.CaptureSessionCreator;
import com.xiaopo.flying.acamera.preview.PreviewStarter;
import com.xiaopo.flying.acamera.request.RequestFactory;
import com.xiaopo.flying.acamera.state.CameraStateManager;

import java.util.ArrayList;

import io.reactivex.subjects.BehaviorSubject;

/**
 * @author wupanjie
 */
class ACameraFactory {

  private final CameraManager cameraManager;
  private final Handler cameraHandler;
  private final CameraDevice cameraDevice;

  private CaptureSessionCreator captureSessionCreator;
  private PreviewStarter previewStarter;
  private FocusTrigger focusTrigger;
  private CameraCommandFactory commandFactory;
  private RequestFactory requestFactory;
  private ARealCameraCharacteristics aCameraCharacteristics;
  private BehaviorSubject<Surface> previewSurfaceSubject;
  private Lifetime lifetime = new Lifetime();
  private CameraStateManager cameraStateManager;

  ACameraFactory(CameraManager cameraManager, Handler cameraHandler, CameraDevice cameraDevice) {
    this.cameraManager = cameraManager;
    this.cameraHandler = cameraHandler;
    this.cameraDevice = cameraDevice;
  }

  public ACamera create() {
    try {
      CameraCharacteristics characteristics =
          cameraManager.getCameraCharacteristics(cameraDevice.getId());

      captureSessionCreator =
          new CaptureSessionCreator(cameraDevice, cameraHandler);

      aCameraCharacteristics = new ARealCameraCharacteristics(characteristics);

      previewSurfaceSubject = BehaviorSubject.create();

      initRequestFactory();

      initCommandSystem();

      initPreviewStarter();

      initFocusTrigger();

      return new ARealCamera(
          lifetime,
          aCameraCharacteristics,
          previewStarter,
          focusTrigger);
    } catch (CameraAccessException e) {
      e.printStackTrace();
    }

    return null;
  }

  private void initFocusTrigger() {
    focusTrigger = new FocusTrigger(
        commandFactory,
        cameraStateManager,
        aCameraCharacteristics.getSensorOrientation()
    );
  }

  private void initPreviewStarter() {
    previewStarter = new PreviewStarter(
        new ArrayList<Surface>(0),
        captureSessionCreator,
        commandFactory,
        previewSurfaceSubject
    );

    lifetime.add(previewStarter);
    lifetime.add(new SafeCloseableHolder<>(cameraDevice));
  }

  private void initRequestFactory() {
    cameraStateManager = new CameraStateManager(aCameraCharacteristics);
    requestFactory =
        new RequestFactory(cameraDevice,
            cameraStateManager,
            previewSurfaceSubject);
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
            return new PreviewCommand(requestFactory.createPreviewTemplate().build(), cameraHandler);
          }
        });

        cameraCommandBuilder.add(CameraCommandType.SCAN_FAOCUS, new Supplier<CameraCommand>() {
          @Override
          public CameraCommand get() {
            return new FullAFScanCommand(requestFactory, cameraHandler);
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
