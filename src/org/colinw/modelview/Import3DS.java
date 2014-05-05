package org.colinw.modelview;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

class Import3DS extends ImportModel {
  private static final String TAG = "Import3DS";

  static final int M3D_VERSION_ID      = 0x0002;
  static final int COLOR_24_ID         = 0x0011;
  static final int LIN_COLOR_24_ID     = 0x0012;
  static final int INT_PERCENTAGE_ID   = 0x0030;
  static final int FLOAT_PERCENTAGE_ID = 0x0031;
  static final int MASTER_SCALE_ID     = 0x0100;
  static final int MDATA_ID            = 0x3d3d;
  static final int MESH_VERSION_ID     = 0x3d3e;
  static final int NAMED_OBJECT_ID     = 0x4000;
  static final int N_TRI_OBJECT_ID     = 0x4100;
  static final int POINT_ARRAY_ID      = 0x4110;
  static final int FACE_ARRAY_ID       = 0x4120;
  static final int MSH_MAT_GROUP_ID    = 0x4130;
  static final int MAP_COORDS_ID       = 0x4140;
  static final int SMOOTH_GROUP_ID     = 0x4150;
  static final int MESH_MATRIX_ID      = 0x4160;
  static final int M3DMAGIC_ID         = 0x4d4d;
  static final int MAT_NAME_ID         = 0xa000;
  static final int MAT_AMBIENT_ID      = 0xa010;
  static final int MAT_DIFFUSE_ID      = 0xa020;
  static final int MAT_SPECULAR_ID     = 0xa030;
  static final int MAT_SHININESS_ID    = 0xa040;
  static final int MAT_TRANSPARENCY_ID = 0xa050;
  static final int MAT_TWO_SIDED_ID    = 0xa081;
  static final int MAT_SHADING_ID      = 0xa100;
  static final int MAT_ENTRY_ID        = 0xafff;

  static final class ChunkName {
    ChunkName(int id, String name) {
      this.id   = id;
      this.name = name;
    }

    int    id;
    String name;
  }

  static final ChunkName [] chunk_names = {
    new ChunkName(M3D_VERSION_ID     , "M3D_VERSION"     ),
    new ChunkName(COLOR_24_ID        , "COLOR_24"        ),
    new ChunkName(LIN_COLOR_24_ID    , "LIN_COLOR_24"    ),
    new ChunkName(INT_PERCENTAGE_ID  , "INT_PERCENTAGE"  ),
    new ChunkName(FLOAT_PERCENTAGE_ID, "FLOAT_PERCENTAGE"),
    new ChunkName(MASTER_SCALE_ID    , "MASTER_SCALE"    ),
    new ChunkName(MDATA_ID           , "MDATA"           ),
    new ChunkName(MESH_VERSION_ID    , "MESH_VERSION"    ),
    new ChunkName(NAMED_OBJECT_ID    , "NAMED_OBJECT"    ),
    new ChunkName(N_TRI_OBJECT_ID    , "N_TRI_OBJECT"    ),
    new ChunkName(POINT_ARRAY_ID     , "POINT_ARRAY"     ),
    new ChunkName(FACE_ARRAY_ID      , "FACE_ARRAY"      ),
    new ChunkName(MSH_MAT_GROUP_ID   , "MSH_MAT_GROUP"   ),
    new ChunkName(MAP_COORDS_ID      , "MAP_COORDS_ID"   ),
    new ChunkName(SMOOTH_GROUP_ID    , "SMOOTH_GROUP_ID" ),
    new ChunkName(MESH_MATRIX_ID     , "MESH_MATRIX"     ),
    new ChunkName(M3DMAGIC_ID        , "M3DMAGIC"        ),
    new ChunkName(MAT_NAME_ID        , "MAT_NAME"        ),
    new ChunkName(MAT_AMBIENT_ID     , "MAT_AMBIENT"     ),
    new ChunkName(MAT_DIFFUSE_ID     , "MAT_DIFFUSE"     ),
    new ChunkName(MAT_SPECULAR_ID    , "MAT_SPECULAR"    ),
    new ChunkName(MAT_SHININESS_ID   , "MAT_SHININESS"   ),
    new ChunkName(MAT_TRANSPARENCY_ID, "MAT_TRANSPARENCY"),
    new ChunkName(MAT_TWO_SIDED_ID   , "MAT_TWO_SIDED_ID"),
    new ChunkName(MAT_SHADING_ID     , "MAT_SHADING"     ),
    new ChunkName(MAT_ENTRY_ID       , "MAT_ENTRY"       )
  };

  static class Chunk {
    Chunk(Chunk parent1) {
      parent = parent1;
      id     = 0;
      len    = 0;
      left   = 0;
    }

    Chunk parent;
    int   id;
    int   len;
    int   left;
  }

  public Import3DS(ModelViewRenderer renderer) {
    super(renderer);

    materials_           = new Vector<Material>();
    vertexFaceList_      = new HashMap<Integer,Vector<Integer>>();
    smoothGroupFaceList_ = new HashMap<Integer,Vector<Integer>>();
  }

  protected boolean readContents() {
    Chunk chunk = new Chunk(null);

    readChunk(chunk);

    if (chunk.id != M3DMAGIC_ID) {
      Log.d(TAG, "Not a 3D Studio File");
      return false;
    }

    Chunk chunk1 = new Chunk(chunk);

    while (readChunk(chunk1)) {
      switch (chunk1.id) {
        case M3D_VERSION_ID:
          readM3DVersion(chunk1);
          break;
        case MDATA_ID:
          readMData(chunk1);
          break;
        default:
          skipChunk(chunk1);
          break;
      }
    }

    return true;
  }

  boolean readM3DVersion(Chunk chunk) {
    try {
      long version = readLong(chunk);

      return true;
    }
    catch (IOException e) {
      return false;
    }
  }

  boolean readMData(Chunk chunk) {
    Chunk chunk1 = new Chunk(chunk);

    while (readChunk(chunk1)) {
      switch (chunk1.id) {
        case MESH_VERSION_ID:
          readMeshVersion(chunk1);
          break;
        case MAT_ENTRY_ID:
          readMatEntry(chunk1);
          break;
        case MASTER_SCALE_ID: {
          try {
            readMasterScale(chunk1);
          } catch (IOException e) {
            return false;
          }

          break;
        }
        case NAMED_OBJECT_ID:
          readNamedObject(chunk1);
          break;
        default:
          skipChunk(chunk1);
          break;
      }
    }

    return true;
  }

  boolean readMeshVersion(Chunk chunk) {
    try {
      long version = readLong(chunk);

      return true;
    }
    catch (IOException e) {
      return false;
    }
  }

  boolean readMatEntry(Chunk chunk) {
    material_ = new Material();

    materials_.add(material_);

    Chunk chunk1 = new Chunk(chunk);

    while (readChunk(chunk1)) {
      switch (chunk1.id) {
        case MAT_NAME_ID:
          readMatName(chunk1);
          break;
        case MAT_AMBIENT_ID:
          readMatAmbient(chunk1);
          break;
        case MAT_DIFFUSE_ID:
          readMatDiffuse(chunk1);
          break;
        case MAT_SPECULAR_ID:
          readMatSpecular(chunk1);
          break;
        case MAT_SHININESS_ID:
          readMatShininess(chunk1);
          break;
        case MAT_TRANSPARENCY_ID:
          readMatTransparency(chunk1);
          break;
        case MAT_TWO_SIDED_ID:
          readMatTwoSided(chunk1);
          break;
        case MAT_SHADING_ID:
          readMatShading(chunk1);
          break;
        default:
          skipChunk(chunk1);
          break;
      }
    }

    if (debug_) {
      String pad = getChunkPad(chunk);

      Log.d(TAG, pad  + "Material '" + material_.name + "'");

      Log.d(TAG, pad  + "  Ambient  " + material_.getAmbient());
      Log.d(TAG, pad  + "  Diffuse  " + material_.getDiffuse());
      Log.d(TAG, pad  + "  Specular " + material_.getSpecular());

      Log.d(TAG, pad  + "  Shininess    " + material_.getShininess());
      Log.d(TAG, pad  + "  Transparency " + material_.transparency);
      Log.d(TAG, pad  + "  Shading      " + material_.shading);
    }

    return true;
  }

  boolean readMatName(Chunk chunk) {
    String name = readString(chunk);

    if (debug_) {
      String pad = getChunkPad(chunk);

      Log.d(TAG, pad + "  Material '" + name + "'");
    }

    material_.name = name;

    return true;
  }

  boolean readMatAmbient(Chunk chunk) {
    Chunk chunk1 = new Chunk(chunk);

    RGBA rgba = new RGBA();

    while (readChunk(chunk1)) {
      switch (chunk1.id) {
        case COLOR_24_ID: {
          readColor(chunk, rgba);

          material_.setAmbient(rgba);

          break;
        }
        case LIN_COLOR_24_ID: {
          readColor(chunk, rgba);

          material_.setAmbient(rgba);

          break;
        }
        default:
          skipChunk(chunk1);
          break;
      }
    }

    return true;
  }

  boolean readMatDiffuse(Chunk chunk) {
    Chunk chunk1 = new Chunk(chunk);

    RGBA rgba = new RGBA();

    while (readChunk(chunk1)) {
      switch (chunk1.id) {
        case COLOR_24_ID: {
          readColor(chunk, rgba);

          material_.setDiffuse(rgba);

          break;
        }
        case LIN_COLOR_24_ID: {
          readColor(chunk, rgba);

          material_.setDiffuse(rgba);

          break;
        }
        default:
          skipChunk(chunk1);
          break;
      }
    }

    return true;
  }

  boolean readMatSpecular(Chunk chunk) {
    Chunk chunk1 = new Chunk(chunk);

    RGBA rgba = new RGBA();

    while (readChunk(chunk1)) {
      switch (chunk1.id) {
        case COLOR_24_ID: {
          readColor(chunk, rgba);

          material_.setSpecular(rgba);

          break;
        }
        case LIN_COLOR_24_ID: {
          readColor(chunk, rgba);

          material_.setSpecular(rgba);

          break;
        }
        default:
          skipChunk(chunk1);
          break;
      }
    }

    return true;
  }

  boolean readMatShininess(Chunk chunk) {
    Chunk chunk1 = new Chunk(chunk);

    int   shininess   = 0;
    float f_shininess = 0.0f;

    while (readChunk(chunk1)) {
      switch (chunk1.id) {
        case INT_PERCENTAGE_ID: {
          try {
            shininess = readIntPercentage(chunk);
            f_shininess = shininess;
          } catch (IOException e) {
            return false;
          }

          break;
        }
        case FLOAT_PERCENTAGE_ID: {
          try {
            f_shininess = readFloatPercentage(chunk);
          } catch (IOException e) {
            return false;
          }

          break;
        }
        default:
          skipChunk(chunk1);
          break;
      }
    }

    material_.setShininess(f_shininess);

    return true;
  }

  boolean readMatTransparency(Chunk chunk) {
    Chunk chunk1 = new Chunk(chunk);

    int   transparency = 0;
    float f_transparency = 0.0f;

    while (readChunk(chunk1)) {
      switch (chunk1.id) {
        case INT_PERCENTAGE_ID: {
          try {
            transparency = readIntPercentage(chunk);
            f_transparency = transparency;
          } catch (IOException e) {
            return false;
          }
          break;
        }
        case FLOAT_PERCENTAGE_ID: {
          try {
            f_transparency = readFloatPercentage(chunk);
          }
          catch(IOException e){
            return false;
          }
          break;
        }
        default:
          skipChunk(chunk1);
          break;
      }
    }

    material_.transparency = f_transparency;

    return true;
  }

  boolean readMatTwoSided(Chunk chunk) {
    material_.two_sided = true;

    return true;
  }

  boolean readMatShading(Chunk chunk) {
    try {
      int shading = readShort(chunk);

      material_.shading = shading;

      return true;
    }
    catch (IOException e) {
      return false;
    }
  }

  boolean readColor(Chunk chunk, RGBA rgba) {
    try {
      int r = readChar(chunk);
      int g = readChar(chunk);
      int b = readChar(chunk);

      rgba.setRGBA(r/255.0f, g/255.0f, b/255.0f, 1.0f);

      return true;
    }
    catch (IOException e) {
      return false;
    }
  }

  int readIntPercentage(Chunk chunk) throws IOException {
    return readShort(chunk);
  }

  float readFloatPercentage(Chunk chunk) throws IOException {
    return readFloat(chunk);
  }

  float readMasterScale(Chunk chunk) throws IOException {
    return readFloat(chunk);
  }

  boolean readNamedObject(Chunk chunk) {
    Chunk chunk1 = new Chunk(chunk);

    String name = readString(chunk);

    if (debug_) {
      String pad = getChunkPad(chunk);

      Log.d(TAG, pad + "Object " + name);
    }

    object_ = new Object(scene_);

    object_.setName(name);

    while (readChunk(chunk1)) {
      switch (chunk1.id) {
        case N_TRI_OBJECT_ID:
          readNTriObject(chunk1);
          break;
        default:
          skipChunk(chunk1);
          break;
      }
    }

    scene_.addObject(object_);

    return true;
  }

  boolean readNTriObject(Chunk chunk) {
    Chunk chunk1 = new Chunk(chunk);

    while (readChunk(chunk1)) {
      switch (chunk1.id) {
        case POINT_ARRAY_ID:
          readPointArray(chunk1);
          break;
        case FACE_ARRAY_ID:
          readFaceArray(chunk1);
          break;
        case MSH_MAT_GROUP_ID:
          readMshMatGroup(chunk1);
          break;
        case SMOOTH_GROUP_ID:
          readSmoothGroup(chunk1);
          break;
        case MESH_MATRIX_ID:
          readMeshMatrix(chunk1);
          break;
        default:
          skipChunk(chunk1);
          break;
      }
    }

    return true;
  }

  boolean readPointArray(Chunk chunk) {
    try {
      int num_points = readShort(chunk);

      for (int i = 0; i < num_points; ++i) {
        float x = readFloat(chunk);
        float y = readFloat(chunk);
        float z = readFloat(chunk);

        if (debug_) {
          String pad = getChunkPad(chunk);

          Log.d(TAG, pad + "  Point " + i + ": " + x + " " + y + " " + z);
        }

        Vertex vertex = new Vertex(x, y, z);

        object_.addVertex(vertex);
      }

      return true;
    }
    catch (IOException e) {
      return false;
    }
  }

  boolean readFaceArray(Chunk chunk) {
    try {
      vertexFaceList_.clear();

      int num_vertices = object_.getNumVertices();

      int num_faces = readShort(chunk);

      if (debug_) {
        String pad = getChunkPad(chunk);

        Log.d(TAG, pad  + "Num faces " + num_faces);
      }

      int [] point_num = new int [3];

      for (int i = 0; i < num_faces; ++i) {
        Vector<Integer> vertices = new Vector<Integer>();

        for (int j = 0; j < 3; j++) {
          point_num[j] = readShort(chunk);

          if (point_num[j] < num_vertices)
            vertices.add(point_num[j]);
          else
            Log.d(TAG, "Invalid Point Num " + point_num[j]);
        }

        // point flags
        int flags = readShort(chunk);

        if (debug_) {
          String pad = getChunkPad(chunk);

          Log.d(TAG, pad + "  Face " + i + ":");

          for (int j = 0; j < 3; j++)
            Log.d(TAG, " " + point_num[j]);

          Log.d(TAG, " (" + flags + ")");

          Log.d(TAG, "\n");
        }

        Vertex vertex1 = object_.getVertex(vertices.get(0));
        Vertex vertex2 = object_.getVertex(vertices.get(1));
        Vertex vertex3 = object_.getVertex(vertices.get(2));

        int orient =
          Math3D.PolygonOrientation(vertex1.x(), vertex1.y(), vertex1.z(),
                                    vertex2.x(), vertex2.y(), vertex2.z(),
                                    vertex3.x(), vertex3.y(), vertex3.z(),
                                    0, 0, 1);

        if (orient == 1)
          object_.swapVertices(0, 2);

        Face face = new Face(object_, vertices);

        int face_num = object_.addFace(face);

        face.calcNormal();

        for (int j = 0; j < 3; j++) {
          int n = point_num[j];

          if (! vertexFaceList_.containsKey(n))
            vertexFaceList_.put(n, new Vector<Integer>());

          vertexFaceList_.get(n).add(face_num);
        }
      }

      for (Map.Entry<Integer,Vector<Integer>> fe : vertexFaceList_.entrySet()) {
        Vertex vertex = object_.getVertex(fe.getKey());

        Iterator<Integer> fi1 = fe.getValue().iterator();

        Vector3D normal = new Vector3D(0.0f, 0.0f, 0.0f);

        while (fi1.hasNext()) {
          Face face = object_.getFace(fi1.next());

          normal.add(face.getNormal());
        }

        normal.scale(1.0f/fe.getValue().size());

        normal.normalize();

        vertex.setNormal(normal);
      }

      return true;
    }
    catch (IOException e) {
      return false;
    }
  }

  boolean readMshMatGroup(Chunk chunk) {
    try {
      String name = readString(chunk);

      if (debug_) {
        String pad = getChunkPad(chunk);

        Log.d(TAG, pad + "Mat Group " + name);
      }

      int num_faces = readShort(chunk);

      Vector<Integer> face_nums = new Vector<Integer>();

      for (int i = 0; i < num_faces; ++i) {
        int face_num = readShort(chunk);

        face_nums.add(face_num);
      }

      Material material = null;

      Iterator<Material> pmaterial = materials_.iterator();

      while (pmaterial.hasNext()) {
        Material material1 = pmaterial.next();

        if (material1.name.equals(name)) {
          material = material1;
          break;
        }
      }

      if (material != null) {
        if (debug_) {
          String pad = getChunkPad(chunk);

          Log.d(TAG, pad + "  Ambient  " + material.getAmbient());
          Log.d(TAG, pad + "  Diffuse  " + material.getDiffuse());
          Log.d(TAG, pad + "  Specular " + material.getSpecular());

          Log.d(TAG, pad + "  Shininess    " + material.getShininess());
          Log.d(TAG, pad + "  Transparency " + material.transparency);
          Log.d(TAG, pad + "  Shading      " + material.shading);
        }

        for (int i = 0; i < num_faces; ++i) {
          int face_num = face_nums.get(i);

          if (face_num < object_.getNumFaces()) {
            Face face = object_.getFace(face_num);

            face.setMaterial(material);

            face.setTwoSided(material.two_sided);
          }
          else
            Log.d(TAG, "Invalid face num " + face_num);
        }
      }

      return true;
    }
    catch (IOException e) {
      return false;
    }
  }

  boolean readSmoothGroup(Chunk chunk) {
    try {
      smoothGroupFaceList_.clear();

      int num_faces = chunk.left/4;

      if (debug_)
        Log.d(TAG, String.format("%d", num_faces));

      for (int i = 0; i < num_faces; ++i) {
        int smooth = (int) readLong(chunk);

        if (smooth == 0) continue;

        if (debug_)
          Log.d(TAG, "Smooth Groups");

        for (int b = 0; b < 32; ++b) {
          if ((smooth & (1 + b)) != 0) {
            if (! smoothGroupFaceList_.containsKey(b))
              smoothGroupFaceList_.put(b, new Vector<Integer>());

            smoothGroupFaceList_.get(b).add(i);

            if (debug_)
              Log.d(TAG, ":" + b);
          }
        }

        if (debug_)
          Log.d(TAG, "\n");
      }

      return true;
    }
    catch (IOException e) {
       return false;
    }
  }

  boolean readMeshMatrix(Chunk chunk) {
    try {
      float[][] matrix = new float[3][4];

      matrix[0][0] = readFloat(chunk);
      matrix[1][0] = readFloat(chunk);
      matrix[2][0] = readFloat(chunk);
      matrix[0][1] = readFloat(chunk);
      matrix[1][1] = readFloat(chunk);
      matrix[2][1] = readFloat(chunk);
      matrix[0][2] = readFloat(chunk);
      matrix[1][2] = readFloat(chunk);
      matrix[2][2] = readFloat(chunk);
      matrix[0][3] = readFloat(chunk);
      matrix[1][3] = readFloat(chunk);
      matrix[2][3] = readFloat(chunk);

      if (debug_) {
        String pad = getChunkPad(chunk);

        Log.d(TAG, pad + matrix[0][0] + " " + matrix[1][0] + " " + matrix[2][0]);
        Log.d(TAG, pad + matrix[0][1] + " " + matrix[1][1] + " " + matrix[2][1]);
        Log.d(TAG, pad + matrix[0][2] + " " + matrix[1][2] + " " + matrix[2][2]);
        Log.d(TAG, pad + matrix[0][3] + " " + matrix[1][3] + " " + matrix[2][3]);
      }

      return true;
    }
    catch (IOException e) {
      return false;
    }
  }

  boolean readChunk(Chunk chunk) {
    try {
      if (chunk.parent != null && chunk.parent.left <= 0)
        return false;

      chunk.left = 6;

      chunk.id   = readShort(chunk);
      chunk.len  = (int) readLong(chunk);
      chunk.left = chunk.len - 6;

      if (debug_)
        printChunk(chunk);

      return true;
    }
    catch (IOException e) {
      return false;
    }
  }

  boolean skipChunk(Chunk chunk) {
    if (debug_) {
      String pad = getChunkPad(chunk);

      Log.d(TAG, pad + "  !!Skip Chunk!! " + getChunkName(chunk));
    }

    for (int i = 0; i < chunk.left; ++i) {
      try {
        file_.readChar();
      } catch (IOException e) {
        break;
      }
    }

    adjustChunkLeft(chunk, chunk.left);

    return true;
  }

  int readChar(Chunk chunk) throws IOException {
    int c = file_.readChar();

    adjustChunkLeft(chunk, 1);

    return c;
  }

  int readShort(Chunk chunk) throws IOException {
    int s = file_.readShort();

    adjustChunkLeft(chunk, 2);

    return s;
  }

  long readLong(Chunk chunk) throws IOException {
    long l = file_.readLong();

    adjustChunkLeft(chunk, 4);

    return l;
  }

  String readString(Chunk chunk) {
    int     buffer_max = chunk.left + 32;
    char [] buffer     = new char [buffer_max + 1];

    int i = 0;

    while (chunk.left > 0) {
      int c;

      try {
        c = file_.readChar();
      }
      catch (IOException e) {
        break;
      }

      adjustChunkLeft(chunk, 1);

      if (c == 0)
        break;

      buffer[i++] = (char) c;
    }

    String str = new String(buffer, 0, i);

    return str;
  }

  float readFloat(Chunk chunk) throws IOException {
    float f = file_.readFloat();

    adjustChunkLeft(chunk, 4);

    return f;
  }

  String getChunkName(Chunk chunk) {
    if (chunk == null)
      return new String("<NULL>");

    for (int i = 0; i < chunk_names.length; ++i) {
      if (chunk_names[i].id != chunk.id) continue;

      return String.format("%s (%x)", chunk_names[i].name, chunk.id);
    }

    return String.format("%x", chunk.id);
  }

  void adjustChunkLeft(Chunk chunk, int offset) {
    chunk.left -= offset;

    if (chunk.parent != null)
      adjustChunkLeft(chunk.parent, offset);
  }

  void printChunk(Chunk chunk) {
    String pad = getChunkPad(chunk);

    Log.d(TAG, pad + "Chunk " + getChunkName(chunk) + " " + chunk.left);
  }

  int getChunkDepth(Chunk chunk) {
    int depth = 0;

    Chunk parent = chunk.parent;

    while (parent != null) {
      ++depth;

      parent = parent.parent;
    }

    return depth;
  }

  String getChunkPad(Chunk chunk) {
    int depth = getChunkDepth(chunk);

    String pad = new String();

    for (int i = 0; i < depth; ++i)
      pad += "  ";

    return pad;
  }

  private Object                           object_;
  private Material                         material_;
  private Vector<Material>                 materials_;
  private HashMap<Integer,Vector<Integer>> vertexFaceList_;
  private HashMap<Integer,Vector<Integer>> smoothGroupFaceList_;
}
