package org.colinw.modelview;

class ImportModel {
  public ImportModel(ModelViewRenderer renderer) {
    renderer_  = renderer;
    debug_     = false;
    scene_     = new Scene(renderer_);
    file_      = null;
    modelName_ = "";
  }

  public Scene scene() { return scene_; }

  public boolean read(String filename) {
    file_ = new FileReader(renderer_.context(), filename);

    if (! file_.isValid())
      return false;

    modelName_ = filename;

    return readContents();
  }

  public String modelName() { return modelName_; }

  protected boolean readContents() { return false; }

  protected ModelViewRenderer renderer_;
  protected boolean           debug_;
  protected Scene             scene_;
  protected FileReader        file_;
  protected String            modelName_;
}
