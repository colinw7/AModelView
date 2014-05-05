package org.colinw.modelview;

import android.content.Context;
import android.util.Log;

import java.util.Vector;

class ImportScene extends ImportModel {
  private static final String TAG = "ImportScene";

  static final int ORIENTATION_CMD = 1;
  static final int SCENE_CMD       = 2;
  static final int OBJECT_CMD      = 3;
  static final int PRIMITIVE_CMD   = 4;
  static final int COLORS_CMD      = 5;
  static final int COLOURS_CMD     = 6;
  static final int TEXTURES_CMD    = 7;

  static final String [] commands = {
    "Orientation",
    "Scene",
    "Object",
    "Primitive",
    "Colors",
    "Colours",
    "Textures",
    ""
  };

  static final int SCENE_OBJECT_CMD     = 1;
  static final int SCENE_PROJECTION_CMD = 2;
  static final int SCENE_EYE_CMD        = 3;
  static final int SCENE_WINDOW_CMD     = 4;
  static final int SCENE_END_CMD        = 5;

  static final String [] scene_commands = {
    "Object",
    "Projection",
    "Eye",
    "Window",
    "End",
    ""
  };

  static final int PRIMITIVE_FACES_CMD     = 1;
  static final int PRIMITIVE_SUB_FACES_CMD = 2;
  static final int PRIMITIVE_LINES_CMD     = 3;
  static final int PRIMITIVE_SUB_LINES_CMD = 4;
  static final int PRIMITIVE_POINTS_CMD    = 5;
  static final int PRIMITIVE_ROTATE_CMD    = 6;
  static final int PRIMITIVE_END_CMD       = 7;

  static final String [] primitive_commands = {
    "Faces",
    "SubFaces",
    "Lines",
    "SubLines",
    "Points",
    "Rotate",
    "End",
    ""
  };

  static final int OBJECT_PRIMITIVE_CMD       = 1;
  static final int OBJECT_FLIP_ORIENTATION    = 2;
  static final int OBJECT_FACE_COLOR_CMD      = 3;
  static final int OBJECT_FACE_COLOUR_CMD     = 4;
  static final int OBJECT_SUB_FACE_COLOR_CMD  = 5;
  static final int OBJECT_SUB_FACE_COLOUR_CMD = 6;
  static final int OBJECT_LINE_COLOR_CMD      = 7;
  static final int OBJECT_LINE_COLOUR_CMD     = 8;
  static final int OBJECT_SUB_LINE_COLOR_CMD  = 9;
  static final int OBJECT_SUB_LINE_COLOUR_CMD = 10;
  static final int OBJECT_TEXTURE_CMD         = 11;
  static final int OBJECT_COVER_TEXTURE_CMD   = 12;
  static final int OBJECT_MASK_CMD            = 13;
  static final int OBJECT_COVER_MASK_CMD      = 14;
  static final int OBJECT_TRANSFORMS_CMD      = 15;
  static final int OBJECT_END_CMD             = 16;

  static final String [] object_commands = {
    "Primitive",
    "Flip_Orientation",
    "Face_Color",
    "Face_Colour",
    "SubFace_Color",
    "SubFace_Colour",
    "Line_Color",
    "Line_Colour",
    "SubLine_Color",
    "SubLine_Colour",
    "Texture",
    "CoverTexture",
    "Mask",
    "CoverMask",
    "Transforms",
    "End",
    ""
  };

  static final int FACES_END_CMD = 1;

  static final String [] faces_commands = {
    "End",
    ""
  };

  static final int LINES_END_CMD = 1;

  static final String [] lines_commands = {
    "End",
    ""
  };

  static final int POINTS_END_CMD = 1;

  static final String [] points_commands = {
    "End",
    ""
  };

  static final int ROTATE_END_CMD = 1;

  static final String [] rotate_commands = {
    "End",
    ""
  };

  static final int COLORS_END_CMD = 1;

  static final String [] colors_commands = {
    "End",
    ""
  };

  static final int TRANSFORMS_TRANSLATE_CMD = 1;
  static final int TRANSFORMS_SCALE_CMD     = 2;
  static final int TRANSFORMS_ROTATE_X_CMD  = 3;
  static final int TRANSFORMS_ROTATE_Y_CMD  = 4;
  static final int TRANSFORMS_ROTATE_Z_CMD  = 5;
  static final int TRANSFORMS_END_CMD       = 6;

  static final String [] transforms_commands = {
    "Translate",
    "Scale",
    "RotateX",
    "RotateY",
    "RotateZ",
    "End",
    ""
    };

  public ImportScene(ModelViewRenderer renderer) {
    super(renderer);

    orientation_ = 1;

    colors_ = new Vector<RGBA>();

    textures_ = new Vector<Integer>();
  }

  protected boolean readContents() {
    String line;

    boolean end_command = false;

    while (! end_command && (line = file_.readLine()) != null) {
      if (line.equals("") || line.charAt(0) == ';' || line.charAt(0) == '#')
        continue;

      Vector<String> words = StrUtil.lineToWords(line);

      int command_num = lookupCommand(words.get(0), commands);

      switch (command_num) {
        case ORIENTATION_CMD:
          orientation_ = StrUtil.toInteger(words.get(1));

          break;
        case SCENE_CMD:
          readScene();

          break;
        case OBJECT_CMD:
          readObject(words.get(1));

          break;
        case PRIMITIVE_CMD:
          readPrimitive(words.get(1));

          break;
        case COLORS_CMD:
        case COLOURS_CMD:
          readColors();

          break;
        case TEXTURES_CMD:
          readTextures();

          break;
        default:
          Log.d(TAG, "Unrecognised Command " + words.get(0));
          break;
        }
    }

    return true;
  }

  void readScene() {
    String line;

    boolean end_command = false;

    while (! end_command && (line = file_.readLine()) != null) {
      if (line.equals("") || line.charAt(0) == ';' || line.charAt(0) == '#')
        continue;

      Vector<String> words = StrUtil.lineToWords(line);

      int command_num = lookupCommand(words.get(0), scene_commands);

      switch (command_num) {
        case SCENE_OBJECT_CMD: {
          Object object = getPrimitive(words.get(1));

          if (object == null) {
            Log.d(TAG, "Unrecognised Object " + words.get(1));
            return;
          }

          addObject(words.get(1));

          break;
        }
        case SCENE_PROJECTION_CMD:
          break;
        case SCENE_EYE_CMD:
          break;
        case SCENE_WINDOW_CMD:
          break;
        case SCENE_END_CMD:
          end_command = true;

          break;
        default:
          Log.d(TAG, "Unrecognised Scene Command " + words.get(0));
          break;
      }
    }
  }

  void addObject(String name) {
    Object primitive = getPrimitive(name);

    if (primitive == null) {
      if      (name.equals("Sphere")) {
        primitive = new Object(scene_, "sphere");

        primitive.addSphere(1, 40, 40);
      }
      else if (name.equals("Cube")) {
        primitive = new Object(scene_, "cube");

        primitive.addCube(0.0, 0.0, 0.0, 1.0);
      }
    }

    if (primitive == null) {
      Log.d(TAG, "Unrecognised Primitive " + name);
      return;
    }

    Object object1 = new Object(scene_, primitive);

    scene_.addObject(object1);
  }

  void readPrimitive(String name) {
    Object primitive = new Object(scene_, name);

    primitive.setName(name);

    String line;

    boolean end_command = false;

    while (! end_command && (line = file_.readLine()) != null) {
      if (line.equals("") || line.charAt(0) == ';' || line.charAt(0) == '#')
        continue;

      Vector<String> words = StrUtil.lineToWords(line);

      int command_num = lookupCommand(words.get(0), primitive_commands);

      switch (command_num) {
        case PRIMITIVE_FACES_CMD: {
          readFaces(primitive, -1);

          break;
        }
        case PRIMITIVE_SUB_FACES_CMD: {
          int face_num = StrUtil.toInteger(words.get(1));

          if (face_num <= 0 || face_num > (int) primitive.getNumFaces()) {
            Log.d(TAG, "SubFace Face " + face_num + " Not Found");
            break;
          }

          readFaces(primitive, face_num - 1);

          break;
        }
        case PRIMITIVE_LINES_CMD: {
          readLines(primitive, -1);

          break;
        }
        case PRIMITIVE_SUB_LINES_CMD: {
          int face_num = StrUtil.toInteger(words.get(1));

          if (face_num <= 0 || face_num > (int) primitive.getNumFaces()) {
            Log.d(TAG, "SubLine Face " + face_num + " Not Found");
            break;
          }

          readLines(primitive, face_num - 1);

          break;
        }
        case PRIMITIVE_POINTS_CMD: {
          readVertices(primitive);

          break;
        }
        case PRIMITIVE_ROTATE_CMD: {
          int num_patches = StrUtil.toInteger(words.get(1));

          readRotate(primitive, num_patches);

          break;
        }
        case PRIMITIVE_END_CMD:
          end_command = true;

          break;
        default:
          Log.d(TAG, "Unrecognised Primitive Command " + words.get(0));
          break;
      }
    }

    scene_.addPrimitive(primitive);
  }

  void readObject(String name) {
    Object object = new Object(scene_, name);

    object.setName(name);

    String line;

    boolean end_command = false;

    while (! end_command && (line = file_.readLine()) != null) {
      if (line.equals("") || line.charAt(0) == ';' || line.charAt(0) == '#')
        continue;

      Vector<String> words = StrUtil.lineToWords(line);

      int command_num = lookupCommand(words.get(0), object_commands);

      switch (command_num) {
        case OBJECT_PRIMITIVE_CMD: {
          String name1 = words.get(1);

          Object primitive = getPrimitive(name1);

          if (primitive == null) {
            if      (name1.equals("Sphere")) {
              primitive = new Object(scene_, "sphere");

              primitive.addSphere(1, 40, 40);
            }
            else if (name1.equals("Cube")) {
              primitive = new Object(scene_, "cube");

              primitive.addCube(0.0, 0.0, 0.0, 1.0);
            }
          }

          if (primitive == null) {
            Log.d(TAG, "Unrecognised Primitive " + name1);
            break;
          }

          object = new Object(scene_, primitive);

          object.setName(name);

          break;
        }
        case OBJECT_FLIP_ORIENTATION: {
          // TODO:
          //object.flip_orientation = true;

          break;
        }
        case OBJECT_FACE_COLOR_CMD:
        case OBJECT_FACE_COLOUR_CMD: {
          RGBA rgba;

          if (StrUtil.isInteger(words.get(1))) {
            int color_num = StrUtil.toInteger(words.get(1));

            rgba = getColor(color_num);
          }
          else
            rgba = new RGBA(words.get(1));

          object.setFaceColor(rgba);

          break;
        }
        case OBJECT_SUB_FACE_COLOR_CMD:
        case OBJECT_SUB_FACE_COLOUR_CMD: {
          int color_num = StrUtil.toInteger(words.get(1));

          RGBA rgba = getColor(color_num);

          object.setSubFaceColor(rgba);

          break;
        }
        case OBJECT_LINE_COLOR_CMD:
        case OBJECT_LINE_COLOUR_CMD:
          break;
        case OBJECT_SUB_LINE_COLOR_CMD:
        case OBJECT_SUB_LINE_COLOUR_CMD:
          break;
        case OBJECT_TEXTURE_CMD: {
          int texture_num = StrUtil.toInteger(words.get(1));

          if (texture_num >= 1 && texture_num <= textures_.size()) {
            int texture = textures_.get(texture_num - 1);

            object.setTexture(texture);
          }
          else
            Log.d(TAG, "Invalid texture number " + texture_num);

          break;
        }
        case OBJECT_COVER_TEXTURE_CMD: {
          int texture_num = StrUtil.toInteger(words.get(1));

          if (texture_num >= 1 && texture_num <= (int) textures_.size()) {
            int texture = textures_.get(texture_num - 1);

            //object.mapTexture(texture);
          }
          else
            Log.d(TAG, "Invalid texture number " + texture_num);

          break;
        }
        case OBJECT_MASK_CMD: {
          int mask_num = StrUtil.toInteger(words.get(1));

          if (mask_num >= 1 && mask_num <= (int) textures_.size()) {
            int texture = textures_.get(mask_num - 1);

            // TODO:
            //object.setMask(texture);
          }
          else
            Log.d(TAG, "Invalid mask number " + mask_num);

          break;
        }
        case OBJECT_COVER_MASK_CMD: {
          int mask_num = StrUtil.toInteger(words.get(1));

          if (mask_num >= 1 && mask_num <= (int) textures_.size()) {
            int texture = textures_.get(mask_num - 1);

            // TODO:
            //object.mapMask(texture);
          }
          else
            Log.d(TAG, "Invalid mask number " + mask_num);

          break;
        }
        case OBJECT_TRANSFORMS_CMD: {
          Matrix3D matrix = readTransforms();

          object.transform(matrix);

          break;
        }
        case OBJECT_END_CMD:
          end_command = true;

          break;
        default:
          Log.d(TAG, "Unrecognised Object Command " + words.get(0));
          break;
      }
    }

    scene_.addPrimitive(object);
  }

  void readFaces(Object object, int pface_num) {
    String line;

    boolean end_command = false;

    while (! end_command && (line = file_.readLine()) != null) {
      if (line.equals("") || line.charAt(0) == ';' || line.charAt(0) == '#')
        continue;

      Vector<String> words = StrUtil.lineToWords(line);

      int command_num = lookupCommand(words.get(0), faces_commands);

      switch (command_num) {
        case FACES_END_CMD:
          end_command = true;

          break;
        default: {
          Vector<Integer> points = new Vector<Integer>();

          int i = 0;

          while (i < words.size()) {
            if (words.get(i).equals(":"))
              break;

            int point_num = StrUtil.toInteger(words.get(i));

            points.add(point_num);

            i++;
          }

          int num_points = points.size();

          Vector<Integer> face_points = new Vector<Integer>();

          if (orientation_ == 1) {
            for (int j = 0; j < num_points; ++j) {
              int point_num = points.get(j) - 1;

              face_points.add(point_num);
            }
          }
          else {
            for (int j = num_points - 1; j >= 0; --j) {
              int point_num = points.get(j) - 1;

              face_points.add(point_num);
            }
          }

          int face_num = 0;

          if (pface_num != -1) {
            Face face1 = new Face(object, face_points);

            face1.calcNormal();

            Face face2 = object.getFace(pface_num);

            face_num = face2.addSubFace(face1);
          }
          else {
            Face face1 = new Face(object, face_points);

            face1.calcNormal();

            face_num = object.addFace(face1);
          }

          if (i < words.size() && words.get(i).equals(":")) {
            i++;

            int face_color = StrUtil.toInteger(words.get(i));

            if (face_color >= 0 && face_color < colors_.size()) {
              RGBA rgba = getColor(face_color);

              if (pface_num != -1) {
                Face face1 = object.getFace(pface_num);
                Face face2 = face1.getSubFace(face_num);

                face2.setColor(rgba);
              }
              else {
                Face face1 = object.getFace(face_num);

                face1.setColor(rgba);
              }
            }
          }

          break;
        }
      }
    }
  }

  void readLines(Object object, int pface_num) {
    String line;

    boolean end_command = false;

    while (! end_command && (line = file_.readLine()) != null) {
      if (line.equals("") || line.charAt(0) == ';' || line.charAt(0) == '#')
        continue;

      Vector<String> words = StrUtil.lineToWords(line);

      int command_num = lookupCommand(words.get(0), lines_commands);

      switch (command_num) {
        case LINES_END_CMD:
          end_command = true;

          break;
        default: {
          int start = StrUtil.toInteger(words.get(0));
          int end   = StrUtil.toInteger(words.get(1));

          if (pface_num != -1) {
            Line line1 = new Line(object, start, end);

            Face face1 = object.getFace(pface_num);

            face1.addSubLine(line1);
          }
          else {
            Line line1 = new Line(object, start, end);

            object.addLine(line1);
          }

          break;
        }
      }
    }
  }

  void readVertices(Object object) {
    String line;

    boolean end_command = false;

    while (! end_command && (line = file_.readLine()) != null) {
      if (line.equals("") || line.charAt(0) == ';' || line.charAt(0) == '#')
        continue;

      Vector<String> words = StrUtil.lineToWords(line);

      int command_num = lookupCommand(words.get(0), points_commands);

      switch (command_num) {
        case POINTS_END_CMD:
          end_command = true;

          break;
        default: {
          double x = StrUtil.toReal(words.get(0));
          double y = StrUtil.toReal(words.get(1));
          double z = StrUtil.toReal(words.get(2));

          object.addVertex(new Vertex((float) x, (float) y, (float) z));

          break;
        }
      }
    }
  }

  void readRotate(Object object, int num_patches) {
    String line;

    Vector<Point3D> points = new Vector<Point3D>();

    boolean end_command = false;

    while (! end_command && (line = file_.readLine()) != null) {
      if (line.equals("") || line.charAt(0) == ';' || line.charAt(0) == '#')
        continue;

      Vector<String> words = StrUtil.lineToWords(line);

      int command_num = lookupCommand(words.get(0), rotate_commands);

      switch (command_num) {
        case ROTATE_END_CMD:
          end_command = true;

          break;
        default: {
          double x = StrUtil.toReal(words.get(0));
          double y = StrUtil.toReal(words.get(1));

          points.add(new Point3D((float) x, (float) y, 0));

          break;
        }
      }
    }

    int num_xy = points.size();

    if (num_xy < 2)
      return;

    double [] x = new double [num_xy];
    double [] y = new double [num_xy];

    for (int i = 0; i < num_xy; i++) {
      x[i] = points.get(i).x();
      y[i] = points.get(i).y();
    }

    object.addBodyRev(x, y, num_xy, num_patches);
  }

  void readColors() {
    String line;

    boolean end_command = false;

    while (! end_command && (line = file_.readLine()) != null) {
      if (line.equals("") || line.charAt(0) == ';' || line.charAt(0) == '#')
        continue;

      Vector<String> words = StrUtil.lineToWords(line);

      int command_num = lookupCommand(words.get(0), colors_commands);

      switch (command_num) {
        case COLORS_END_CMD:
          end_command = true;

          break;
        default:
          colors_.add(new RGBA(words.get(0)));

          break;
      }
    }
  }

  void readTextures() {
    String line;

    boolean end_command = false;

    while (! end_command && (line = file_.readLine()) != null) {
      if (line.equals("") || line.charAt(0) == ';' || line.charAt(0) == '#')
        continue;

      Vector<String> words = StrUtil.lineToWords(line);

      int command_num = lookupCommand(words.get(0), colors_commands);

      switch (command_num) {
        case COLORS_END_CMD: {
          end_command = true;

          break;
        }
        default: {
          //int textureId = renderer_.addImageTexture(words.get(0), false);
          int textureId = renderer_.getTexture(words.get(0));

          textures_.add(textureId);

          break;
        }
      }
    }
  }

  Matrix3D readTransforms() {
    Matrix3D transform_matrix = new Matrix3D();

    String line;

    boolean end_command = false;

    while (! end_command && (line = file_.readLine()) != null) {
      if (line.equals("") || line.charAt(0) == ';' || line.charAt(0) == '#')
        continue;

      Vector<String> words = StrUtil.lineToWords(line);

      int command_num = lookupCommand(words.get(0), transforms_commands);

      switch (command_num) {
        case TRANSFORMS_TRANSLATE_CMD: {
          double x = StrUtil.toReal(words.get(1));
          double y = StrUtil.toReal(words.get(2));
          double z = StrUtil.toReal(words.get(3));

          Matrix3D matrix1 = Matrix3D.newTranslation((float) x, (float) y, (float) z);

          transform_matrix = Matrix3D.multiply(matrix1, transform_matrix);

          break;
        }
        case TRANSFORMS_SCALE_CMD: {
          double x = StrUtil.toReal(words.get(1));
          double y = StrUtil.toReal(words.get(2));
          double z = StrUtil.toReal(words.get(3));

          Matrix3D matrix1 = Matrix3D.newScale((float) x, (float) y, (float) z);

          transform_matrix = Matrix3D.multiply(matrix1, transform_matrix);

          break;
        }
        case TRANSFORMS_ROTATE_X_CMD: {
          double angle = StrUtil.toReal(words.get(1));

          Matrix3D matrix1 = Matrix3D.newRotate((float) angle, 0.0f, 0.0f);

          transform_matrix = Matrix3D.multiply(matrix1, transform_matrix);

          break;
        }
        case TRANSFORMS_ROTATE_Y_CMD: {
          double angle = StrUtil.toReal(words.get(1));

          Matrix3D matrix1 = Matrix3D.newRotate(0.0f, (float) angle, 0.0f);

          transform_matrix = Matrix3D.multiply(matrix1, transform_matrix);

          break;
        }
        case TRANSFORMS_ROTATE_Z_CMD: {
          double angle = StrUtil.toReal(words.get(1));

          Matrix3D matrix1 = Matrix3D.newRotate(0.0f, 0.0f, (float) angle);

          transform_matrix = Matrix3D.multiply(matrix1, transform_matrix);

          break;
        }
        case TRANSFORMS_END_CMD:
          end_command = true;

          break;
        default:
          Log.d(TAG, "Unrecognised Transforms Command " + words.get(0));
          break;
      }
    }

    return transform_matrix;
  }

  Object getObject(String name) {
    return scene_.getObject(name);
  }

  Object getPrimitive(String name) {
    return scene_.getPrimitive(name);
  }

  int lookupCommand(String command, String [] commands) {
    for (int i = 0; commands[i] != ""; i++)
      if (commands[i].equals(command))
        return i + 1;

    return 0;
  }

  RGBA getColor(int color) {
    int num_colors = colors_.size();

    RGBA rgba;

    if (color >= 0 && color < num_colors)
      rgba = new RGBA(colors_.get(color));
    else
      rgba = new RGBA(1, 1, 1, 1);

    return rgba;
  }

  private Vector<RGBA>    colors_;
  private Vector<Integer> textures_;
  private int             orientation_;
}
