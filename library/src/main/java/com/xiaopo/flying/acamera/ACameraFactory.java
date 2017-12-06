package com.xiaopo.flying.acamera;

import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.os.Handler;
import android.util.Size;
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
import com.xiaopo.flying.acamera.command.impl.CaptureCommand;
import com.xiaopo.flying.acamera.command.impl.FullAFScanCommand;
import com.xiaopo.flying.acamera.command.impl.PreviewCommand;
import com.xiaopo.flying.acamera.focus.AutoFocusStateListener;
import com.xiaopo.flying.acamera.focus.AutoFocusTrigger;
import com.xiaopo.flying.acamera.focus.MeteringParameters;
import com.xiaopo.flying.acamera.model.FaceDetectMode;
import com.xiaopo.flying.acamera.model.FlashMode;
import com.xiaopo.flying.acamera.model.FocusMode;
import com.xiaopo.flying.acamera.picturetaker.PictureTaker;
import com.xiaopo.flying.acamera.picturetaker.StillSurfaceReader;
import com.xiaopo.flying.acamera.preview.CaptureSessionCreator;
import com.xiaopo.flying.acamera.preview.PreviewStarter;
import com.xiaopo.flying.acamera.preview.SessionManager;
import com.xiaopo.flying.acamera.request.RequestFactory;
import com.xiaopo.flying.acamera.result.CaptureListener;
import com.xiaopo.flying.acamera.state.CameraStateManager;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

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

      lifetime.add(SessionManager.getInstance());

      initRequestFactory();

      initCommandSystem();

      initPreviewStarter();

      initFocusTrigger();

      initPictureTaker();

      registryStateObserver();

      return new ARealCamera(
          lifetime,
          aCameraCharacteristics,
          previewStarter,
          focusTrigger,
          pictureTaker,
          cameraStateManager);
    } catch (CameraAccessException e) {
      e.printStackTrace();
    }

    return null;
  }

  private void registryStateObserver() {
    cameraStateManager.getZoomState()
        .registryStateObserver(new io.reactivex.functions.Consumer<Float>() {
          @Override
          public void accept(Float zoom) throws Exception {
            CameraCommandCenter.getInstance()
                .nextCommand(commandFactory.create(CameraCommandType.PREVIEW));
          }
        });

    cameraStateManager.getFocusModeState()
        .registryStateObserver(new io.reactivex.functions.Consumer<FocusMode>() {
          @Override
          public void accept(FocusMode focusMode) throws Exception {
            CameraCommandCenter.getInstance()
                .nextCommand(commandFactory.create(CameraCommandType.PREVIEW));
          }
        });

    cameraStateManager.getFlashModeState()
        .registryStateObserver(new io.reactivex.functions.Consumer<FlashMode>() {
          @Override
          public void accept(FlashMode flashMode) throws Exception {
            CameraCommandCenter.getInstance()
                .nextCommand(commandFactory.create(CameraCommandType.PREVIEW));
          }
        });

    cameraStateManager.getFaceDetectModeState()
        .registryStateObserver(new io.reactivex.functions.Consumer<FaceDetectMode>() {
          @Override
          public void accept(FaceDetectMode faceDetectMode) throws Exception {
            CameraCommandCenter.getInstance()
                .nextCommand(commandFactory.create(CameraCommandType.PREVIEW));
          }
        });

    cameraStateManager.getMeteringState()
        .registryStateObserver(new io.reactivex.functions.Consumer<MeteringParameters>() {
          @Override
          public void accept(MeteringParameters parameters) throws Exception {
            CameraCommand focusCommand = commandFactory.create(CameraCommandType.SCAN_FOCUS);
            CameraCommandCenter.getInstance().nextCommand(focusCommand);

            CameraCommand previewCommand = commandFactory.create(CameraCommandType.PREVIEW);
            CameraCommandCenter.getInstance().nextCommand(previewCommand, 3, TimeUnit.SECONDS);
          }
        });

    cameraStateManager.getPictureSizeState()
        .registryStateObserver(new io.reactivex.functions.Consumer<Size>() {
          @Override
          public void accept(Size size) throws Exception {
            CameraCommandCenter.getInstance()
                .nextCommand(commandFactory.create(CameraCommandType.PREVIEW));
          }
        });

  }

  private void initPictureTaker() {
    pictureTaker = new PictureTaker(commandFactory);
  }

  private void initFocusTrigger() {
    focusTrigger = new AutoFocusTrigger(
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

    lifetime.add(commandCenter);
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
