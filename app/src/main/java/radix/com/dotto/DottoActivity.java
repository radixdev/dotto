package radix.com.dotto;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.PointF;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.thebluealliance.spectrum.SpectrumDialog;

import radix.com.dotto.controllers.UserGestureController;
import radix.com.dotto.models.WorldModel;
import radix.com.dotto.utils.enums.GameColor;
import radix.com.dotto.views.PixelGridSurfaceView;

public class DottoActivity extends AppCompatActivity {
  private static final String TAG = DottoActivity.class.toString();

  private PixelGridSurfaceView mGameView;
  private WorldModel mWorldModel;
  private GestureDetectorCompat mGestureDetector;
  private ScaleGestureDetector mScaleGestureDetector;
  private UserGestureController mUserGestureController;

  // View hybrids
  private TextView mTimeoutTextview;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    final Context context = this.getApplicationContext();

    requestWindowFeature(Window.FEATURE_NO_TITLE);
    getWindow().setFlags(
        WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);

    setContentView(R.layout.activity_main);
    setupGameState();

    final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabColorPreferenceButton);
    fab.setBackgroundTintList(ColorStateList.valueOf(mUserGestureController.getColorChoice().getColor()));
    fab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        createColorPicker(context, fab);
      }
    });

    // Listen for timeout changes
    mTimeoutTextview = (TextView) findViewById(R.id.timeoutTextView);
  }

  private void createColorPicker(Context context, final FloatingActionButton fab) {
    // R.style.Theme_Design is also good here
    new SpectrumDialog.Builder(context, R.style.Theme_Design_BottomSheetDialog)
        .setColors(GameColor.ALL_COLOR_VALUES)
        .setDismissOnColorSelected(true)
        .setPositiveButtonText("")
        .setNegativeButtonText("")
        .setSelectedColor(100) // a color that's not here so there's nothing selected
        .setOnColorSelectedListener(new SpectrumDialog.OnColorSelectedListener() {
          @Override
          public void onColorSelected(boolean positiveResult, @ColorInt int selectedColor) {
            if (positiveResult) {
              // get the color somehow?
              for (int i = 0; i < GameColor.ALL_COLOR_VALUES.length; i++) {
                if (GameColor.ALL_COLOR_VALUES[i] == selectedColor) {
                  // set the color
                  mUserGestureController.setUserColorChoice(GameColor.values()[i]);
                  Log.d(TAG, "Selected color of : " + GameColor.values()[i]);
                  fab.setBackgroundTintList(ColorStateList.valueOf(selectedColor));
                  break;
                }
              }
            }
          }
        })
        .build().show(getSupportFragmentManager(), "");
  }

  private void setupGameState() {
    mWorldModel = new WorldModel();
    mUserGestureController = new UserGestureController(mWorldModel);
    mGameView = (PixelGridSurfaceView) findViewById(R.id.gameView);
    mGameView.setModelAndController(mWorldModel, mUserGestureController);
    mUserGestureController.setViewInterface(mGameView);

    mGestureDetector = new GestureDetectorCompat(this, new GestureListener());
    mScaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      mScaleGestureDetector.setQuickScaleEnabled(false);
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      mScaleGestureDetector.setStylusScaleEnabled(false);
    }
  }

  @Override
  protected void onPause() {
    super.onPause();
    mWorldModel.setIsPlaying(false);
    mGameView.setPlaying(false);
  }

  @Override
  protected void onResume() {
    super.onResume();
    mWorldModel.setIsPlaying(true);
    mGameView.setPlaying(true);
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
//    boolean scaleHandled = mScaleGestureDetector.onTouchEvent(event);
//    if (!mScaleGestureDetector.isInProgress()) {
//      boolean touchHandled = this.mGestureDetector.onTouchEvent(event);
//      return scaleHandled || touchHandled;
//    }
//    return scaleHandled;

    boolean scaleHandled = mScaleGestureDetector.onTouchEvent(event);
    boolean touchHandled = mGestureDetector.onTouchEvent(event);
    return scaleHandled || touchHandled;
  }

  private class GestureListener extends GestureDetector.SimpleOnGestureListener {
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
      mUserGestureController.onUserScroll(distanceX, distanceY);
      return true;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
      Log.v(TAG, "single tap");
      mUserGestureController.onUserRequestFocus(getPointFromMotionEvent(motionEvent));
      return true;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {
      Log.v(TAG, "long press");
      mUserGestureController.onUserLongTap(getPointFromMotionEvent(motionEvent));
    }

    @Override
    public boolean onDoubleTap(MotionEvent motionEvent) {
      Log.v(TAG, "double tap");
      mUserGestureController.onUserRequestFocus(getPointFromMotionEvent(motionEvent));
      return true;
    }

    private PointF getPointFromMotionEvent(MotionEvent motionEvent) {
      return new PointF(motionEvent.getX(), motionEvent.getY());
    }
  }

  private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
    private float focusX, focusY;

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
      focusX = detector.getFocusX();
      focusY = detector.getFocusY();
      return true;
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
      mUserGestureController.onUserZoom(detector.getScaleFactor(), new PointF(focusX, focusY));
      return true;
    }
  }
}
