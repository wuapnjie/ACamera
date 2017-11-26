package com.xiaopo.flying.acamera.focus;

import android.graphics.PointF;
import android.util.Log;

import com.xiaopo.flying.acamera.command.CameraCommand;
import com.xiaopo.flying.acamera.command.CameraCommandCenter;
import com.xiaopo.flying.acamera.command.CameraCommandFactory;
import com.xiaopo.flying.acamera.command.CameraCommandType;
import com.xiaopo.flying.acamera.state.CameraState;
import com.xiaopo.flying.acamera.state.CameraStateManager;

import java.util.concurrent.TimeUnit;

import io.reactivex.functions.Consumer;
import io.reactivex.subjects.BehaviorSubject;

/**
 * @author wupanjie
 */
public class AutoFocusTrigger implements FocusFunction, Consumer<PointF> {
  private static final String TAG = "AutoFocusTrigger";

  private final CameraCommandFactory commandFactory;
  private final CameraStateManager cameraStateManager;
  private final int sensorOrientation;
  private final Settings3A settings3A;
  private final BehaviorSubject<PointF> subject;

  public AutoFocusTrigger(CameraCommandFactory commandFactory, CameraStateManager cameraStateManager, int sensorOrientation) {
    this.commandFactory = commandFactory;
    this.cameraStateManager = cameraStateManager;
    this.sensorOrientation = sensorOrientation;
    this.settings3A = new Settings3A();
    this.subject = BehaviorSubject.create();
    this.subject
        .debounce(300, TimeUnit.MILLISECONDS)
        .subscribe(this);
  }

  @Override
  public void triggerFocusAt(float x, float y) {
    subject.onNext(new PointF(x, y));
  }

  @Override
  public void accept(PointF point) throws Exception {
    Log.d(TAG, "accept: point -> " + point);

    CameraState<MeteringParameters> cameraState = cameraStateManager.getMeteringState();
    cameraState.update(PointMeteringParameters.createForNormalizedCoordinates(point /* afPoint */, point /* aePoint */,
        sensorOrientation, settings3A));

    CameraCommand focusCommand = commandFactory.create(CameraCommandType.SCAN_FAOCUS);
    CameraCommandCenter.getInstance().nextCommand(focusCommand);

    CameraCommand previewCommand = commandFactory.create(CameraCommandType.PREVIEW);
    CameraCommandCenter.getInstance().nextCommand(previewCommand, 3, TimeUnit.SECONDS);
  }
}
