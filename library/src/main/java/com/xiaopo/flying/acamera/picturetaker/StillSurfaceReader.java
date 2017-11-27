package com.xiaopo.flying.acamera.picturetaker;

import android.graphics.ImageFormat;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.util.Size;
import android.view.Surface;

import com.xiaopo.flying.acamera.state.CameraStateManager;

import java.nio.ByteBuffer;
import java.util.concurrent.CountDownLatch;

/**
 * Creates a {@link Surface} which can capture single events.
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class StillSurfaceReader {
  private static final String TAG = "StillSurfaceReader";

  private ImageReader imageReader;
  private final Handler cameraHandler;
  private final CameraStateManager stateManager;

  public StillSurfaceReader(Handler cameraHandler, CameraStateManager stateManager) {
    this.cameraHandler = cameraHandler;
    this.stateManager = stateManager;
  }

  /**
   * Returns a {@link Surface} which can be used as a target for still capture events.
   *
   * @return the new Surface
   */
  public Surface getSurface() {
    if (imageReader == null) {
      createImageReader();
    }
    return imageReader.getSurface();
  }

  /**
   * Returns the next available Image as a byte array.
   *
   * @return the Image as byte array.
   */
  public byte[] getPhotoBytes() {
    ImageCaptureAction imageCaptureAction = new ImageCaptureAction(imageReader, cameraHandler);

    return imageCaptureAction.getPhoto();
  }

  // TODO size changed
  private void createImageReader() {
    Size size = stateManager.getPictureSizeState().get();

    imageReader = ImageReader
        .newInstance(
            size.getWidth(),
            size.getHeight(),
            ImageFormat.JPEG,
            15
        );

  }

  private static class ImageCaptureAction implements ImageReader.OnImageAvailableListener {

    private final CountDownLatch countDownLatch = new CountDownLatch(1);
    private final ImageReader imageReader;
    private byte[] bytes;

    private ImageCaptureAction(ImageReader imageReader, Handler cameraHandler) {
      this.imageReader = imageReader;
      imageReader.setOnImageAvailableListener(
          this,
          cameraHandler
      );
    }

    private byte[] getPhoto() {
      Image image = imageReader.acquireLatestImage();
      if (image != null) {
        removeListener();
        bytes = imageToBytes(image);
        return bytes;
      }

      try {
        countDownLatch.await();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      return bytes;
    }

    @Override
    public void onImageAvailable(ImageReader reader) {
      Image image = reader.acquireLatestImage();
      bytes = imageToBytes(image);

      removeListener();

      countDownLatch.countDown();
    }

    private byte[] imageToBytes(Image image) {
      Image.Plane[] planes = image.getPlanes();

      ByteBuffer buffer = planes[0].getBuffer();

      byte[] result = new byte[buffer.remaining()];
      buffer.get(result);

      image.close();

      return result;
    }

    private void removeListener() {
      imageReader.setOnImageAvailableListener(null, null);
    }
  }

}
