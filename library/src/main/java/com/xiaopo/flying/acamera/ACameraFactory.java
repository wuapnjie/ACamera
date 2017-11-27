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
import com.xiaopo.flying.acamera.command.CaptureCommand;
import com.xiaopo.flying.acamera.command.FullAFScanCommand;
import com.xiaopo.flying.acamera.command.PreviewCommand;
import com.xiaopo.flying.acamera.focus.AutoFocusStateListener;
import com.xiaopo.flying.acamera.focus.AutoFocusTrigger;
import com.xiaopo.flying.acamera.picturetaker.PictureTaker;
import com.xiaopo.flying.acamera.picturetaker.StillSurfaceReader;
import com.xiaopo.flying.acamera.preview.CaptureSessionCreator;
import com.xiaopo.flying.acamera.preview.PreviewStarter;
import com.xiaopo.flying.acamera.request.RequestFactory;
import com.xiaopo.flying.acamera.result.CaptureListener;
import com.xiaopo.flying.acamera.state.CameraStateManager;

import java.util.Arrays;

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
  private AutoFocusTrigger focusTrigger;
  private CameraCommandFactory commandFactory;
  private RequestFactory requestFactory;
  private ARealCameraCharacteristics aCameraCharacteristics;
  private BehaviorSubject<Surface> previewSurfaceSubject;
  private Lifetime lifetime = new Lifetime();
  private CameraStateManager cameraStateManager;
  private CaptureListener defaultListener;
  private StillSurfaceReader stillSurfaceReader;
  private PictureTaker pictureTaker;

  ACameraFactory(CameraManager cameraManager, Handler cameraHandler, CameraDevice cameraDevice) {
    this.cameraManager = cameraManager;
    this.cameraHandler = cameraHandler;
    this.cameraDevice = cameraDevice;
  }

  public ACamera create() {
    try {
      CameraCharacteristics characteristics =
          cameraManager.getCameraCharacteristics(cameraDevice.getId());

      aCameraCharacteristics = new ARealCameraCharacteristics(characteristics);
      cameraStateManager = new CameraStateManager(aCameraCharacteristics);
      previewSurfaceSubject = BehaviorSubject.create();
      stillSurfaceReader = new StillSurfaceReader(cameraHandler, cameraStateManager);

      initRequestFactory();

      initCommandSystem();

      initPreviewStarter();

      initFocusTrigger();

      initPictureTaker();

      return new ARealCamera(
          lifetime,
          aCameraCharacteristics,
          previewStarter,
          focusTrigger,
          pictureTaker);
    } catch (CameraAccessException e) {
      e.printStackTrace();
    }

    return null;
  }

  private void initPictureTaker() {
    pictureTaker = new PictureTaker(commandFactory);
  }

  private void initFocusTrigger() {
    focusTrigger = new AutoFocusTrigger(
        commandFactory,
        cameraStateManager,
        aCameraCharacteristics.getSensorOrientation()
    );
  }

  private void initPreviewStarter() {
    captureSessionCreator =
        new CaptureSessionCreator(cameraDevice, cameraHandler);
    previewStarter = new PreviewStarter(
        Arrays.asList(stillSurfaceReader.getSurface()),
        captureSessionCreator,
        commandFactory,
        previewSurfaceSubject
    );

    lifetime.add(previewStarter);
    lifetime.add(new SafeCloseableHolder<>(cameraDevice));
  }

  private void initRequestFactory() {

    defaultListener = new AutoFocusStateListener();
    requestFactory =
        new RequestFactory(cameraDevice,
            cameraStateManager,
            previewSurfaceSubject,
            defaultListener);
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

        cameraCommandBuilder.add(CameraCommandType.SCAN_FOCUS, new Supplier<CameraCommand>() {
          @Override
          public CameraCommand get() {
            return new FullAFScanCommand(requestFactory, cameraHandler);
          }
        });

        cameraCommandBuilder.add(CameraCommandType.CAPTURE, new Supplier<CameraCommand>() {
          @Override
          public CameraCommand get() {
            return new CaptureCommand(requestFactory, cameraHandler, stillSurfaceReader);
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
