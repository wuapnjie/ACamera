package com.xiaopo.flying.sample;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.TextureView;

/**
 * @author wupanjie
 */
public class TouchableTextureView extends TextureView {

  private float downX;
  private float downY;

  private OnTapListener onTapListener;

  public void setOnTapListener(OnTapListener onTapListener) {
    this.onTapListener = onTapListener;
  }

  public TouchableTextureView(Context context) {
    this(context, null);
  }

  public TouchableTextureView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public TouchableTextureView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    switch (event.getActionMasked()) {
      case MotionEvent.ACTION_DOWN:
        downX = event.getX();
        downY = event.getY();
        if (onTapListener != null) {
          onTapListener.onTap(downX, downY);
        }
        break;
    }
    return true;
  }


  public interface OnTapListener {
    void onTap(float x, float y);
  }
}
