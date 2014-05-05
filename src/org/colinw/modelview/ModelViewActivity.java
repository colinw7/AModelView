// TODO:
//   Light stationary as model is rotated
//   More models
//   Speed (less new calls)
//   Map textures (transparency from map)
//   Move should be scaled to match model ?

package org.colinw.modelview;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.graphics.Point;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.List;

public class ModelViewActivity extends Activity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    //---

    Display display = getWindowManager().getDefaultDisplay();

    Point size = new Point();

    display.getSize(size);

    int width = size.x;
    int height = size.y;

    //---

    LinearLayout layout = new LinearLayout(this);

    layout.setOrientation(LinearLayout.VERTICAL);

    LinearLayout buttonLayout = new LinearLayout(this);

    int buttonsHeight = 60;

    buttonLayout.setOrientation(LinearLayout.HORIZONTAL);

    buttonLayout.setLayoutParams(
      new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, buttonsHeight, 0.0f));

    glSurfaceView_ = new GLSurfaceView(this);

    glSurfaceView_.setLayoutParams(
      new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
        height - buttonsHeight, 0.0f)
    );

    layout.addView(glSurfaceView_);

    //setContentView(glSurfaceView_);

    //---

    solidLineToggle_ = new ToggleButton(this);

    solidLineToggle_.setText("Solid");
    solidLineToggle_.setTextOff("Solid");
    solidLineToggle_.setTextOn("Line");

    solidLineToggle_.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        renderer_.setLineMode(solidLineToggle_.isChecked());
      }
    });

    solidLineToggle_.setLayoutParams(
      new LinearLayout.LayoutParams(80, buttonsHeight - 15, 0.0f));

    buttonLayout.addView(solidLineToggle_);

    //---

    depthCheck_ = new CheckBox(this);

    depthCheck_.setText("Depth");

    depthCheck_.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        renderer_.setDepthTest(depthCheck_.isChecked());
      }
    });

    depthCheck_.setLayoutParams(
      new LinearLayout.LayoutParams(100, buttonsHeight - 20, 0.0f));

    buttonLayout.addView(depthCheck_);

    //---

    cullCheck_ = new CheckBox(this);

    cullCheck_.setText("Cull");

    cullCheck_.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        renderer_.setCullFace(cullCheck_.isChecked());
      }
    });

    cullCheck_.setLayoutParams(
      new LinearLayout.LayoutParams(100, buttonsHeight - 20, 0.0f));

    buttonLayout.addView(cullCheck_);

    //---

    lightCheck_ = new CheckBox(this);

    lightCheck_.setText("Light");

    lightCheck_.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        renderer_.setLighted(lightCheck_.isChecked());
      }
    });

    lightCheck_.setLayoutParams(
      new LinearLayout.LayoutParams(100, buttonsHeight - 20, 0.0f));

    buttonLayout.addView(lightCheck_);

    //---

    textureCheck_ = new CheckBox(this);

    textureCheck_.setText("Texture");

    textureCheck_.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        renderer_.setTextured(textureCheck_.isChecked());
      }
    });

    textureCheck_.setLayoutParams(
      new LinearLayout.LayoutParams(100, buttonsHeight - 20, 0.0f));

    buttonLayout.addView(textureCheck_);

    //---

    rotateMoveToggle_ = new ToggleButton(this);

    rotateMoveToggle_.setText("Rotate");
    rotateMoveToggle_.setTextOff("Rotate");
    rotateMoveToggle_.setTextOn("Move");

    rotateMoveToggle_.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        renderer_.setMove(rotateMoveToggle_.isChecked());
      }
    });

    rotateMoveToggle_.setLayoutParams(
      new LinearLayout.LayoutParams(100, buttonsHeight - 15, 0.0f));

    buttonLayout.addView(rotateMoveToggle_);

    //---

    List<String> items = new ArrayList<String>();

    // scene
    items.add("Box_Star.scene");
    items.add("CubeTexture.scene");
    items.add("Cube_Pyramid.scene");
    items.add("Four_Houses.scene");
    items.add("Goblet.scene");
    items.add("Sphere.scene");
    items.add("SphereTexture.scene");
    // 3DS
    items.add("aircar.3ds");
    items.add("batwing.3ds");
    items.add("robocop2.3ds");
    items.add("space_station.3ds");
    // OBJ
    items.add("cessna.obj");
    // V3D
    items.add("F15.V3D");
    // X3D
    items.add("P38.X3D");

    Spinner nameSpinner_ = new Spinner(this);

    ArrayAdapter<String> adapter =
      new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items);
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    nameSpinner_.setAdapter(adapter);

    nameSpinner_.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        renderer_.setModelName((String) parent.getItemAtPosition(pos));
      }

      @Override
      public void onNothingSelected(AdapterView<?> parentView) {
      }
    });

    nameSpinner_.setLayoutParams(
      new LinearLayout.LayoutParams(200, buttonsHeight - 15, 0.0f));

    buttonLayout.addView(nameSpinner_);

    //---

    resetButton_ = new Button(this);

    resetButton_.setText("Reset");

    resetButton_.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        renderer_.resetView();
      }
    });

    resetButton_.setLayoutParams(
      new LinearLayout.LayoutParams(100, buttonsHeight - 20, 0.0f));

    buttonLayout.addView(resetButton_);

    //---

    layout.addView(buttonLayout);

    //---

    renderer_ = new ModelViewRenderer(this);

    if (!checkForES2()) return;

    // Request an OpenGL ES 2.0 compatible context.
    glSurfaceView_.setEGLContextClientVersion(2);

    // Assign our renderer.
    glSurfaceView_.setRenderer(renderer_);

    rendererSet_ = true;

    //---

    glSurfaceView_.setOnTouchListener(new OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        if (event != null) {
          // Convert touch coordinates into normalized device coordinates, keeping in
          // mind that Android's Y coordinates are inverted.
          final float normalizedX = (event.getX() / (float) v.getWidth());
          final float normalizedY = 1.0f - (event.getY() / (float) v.getHeight());

          if (event.getAction() == MotionEvent.ACTION_DOWN) {
            glSurfaceView_.queueEvent(new Runnable() {
              @Override
              public void run() {
                renderer_.handleTouchPress(normalizedX, normalizedY);
              }
            });
          } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            glSurfaceView_.queueEvent(new Runnable() {
              @Override
              public void run() {
                renderer_.handleTouchDrag(normalizedX, normalizedY);
              }
            });
          }

          return true;
        } else {
          return false;
        }
      }
    });

    //---

    setContentView(layout);
  }

  private boolean checkForES2() {
    // Check if the system supports OpenGL ES 2.0.
    ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

    ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();

    // Even though the latest emulator supports OpenGL ES 2.0,
    // it has a bug where it doesn't set the reqGlEsVersion so
    // the above check doesn't work. The below will detect if the
    // app is running on an emulator, and assume that it supports
    // OpenGL ES 2.0.
    supportsEs2_ =
      configurationInfo.reqGlEsVersion >= 0x20000 ||
        (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1 &&
          (Build.FINGERPRINT.startsWith("generic") || Build.FINGERPRINT.startsWith("unknown") ||
            Build.MODEL.contains("google_sdk") || Build.MODEL.contains("Emulator") ||
            Build.MODEL.contains("Android SDK built for x86")));

    if (!supportsEs2_) {
      /*
       * This is where you could create an OpenGL ES 1.x compatible
       * renderer if you wanted to support both ES 1 and ES 2. Since
       * we're not doing anything, the app will crash if the device
       * doesn't support OpenGL ES 2.0. If we publish on the market, we
       * should also add the following to AndroidManifest.xml:
       *
       * <uses-feature android:glEsVersion="0x00020000" android:required="true" />
       *
       * This hides our app from those devices which don't support OpenGL ES 2.0.
       */
      Toast.makeText(this, "This device does not support OpenGL ES 2.0.",
        Toast.LENGTH_LONG).show();
    }

    return supportsEs2_;
  }

  public void stopRender() {
    glSurfaceView_.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
  }

  public void startRender() {
    glSurfaceView_.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
  }

  @Override
  protected void onPause() {
    super.onPause();

    if (rendererSet_) {
      glSurfaceView_.onPause();
    }
  }

  @Override
  protected void onResume() {
    super.onResume();

    if (rendererSet_) {
      glSurfaceView_.onResume();
    }
  }

  private GLSurfaceView     glSurfaceView_;
  private ModelViewRenderer renderer_;
  private ToggleButton      solidLineToggle_;
  private CheckBox          depthCheck_;
  private CheckBox          cullCheck_;
  private CheckBox          lightCheck_;
  private CheckBox          textureCheck_;
  private ToggleButton      rotateMoveToggle_;
  private Spinner           nameSpinner_;
  private Button            resetButton_;
  private boolean           rendererSet_ = false;
  private boolean           supportsEs2_ = false;
}
