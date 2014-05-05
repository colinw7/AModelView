package org.colinw.modelview;

class BBox {
  BBox() {
   set_ = false;
  }

  boolean isSet() { return set_; }

  void addPoint(double x, double y, double z) {
    if (! set_) {
      xmin_ = x; ymin_ = y; zmin_ = z;
      xmax_ = x; ymax_ = y; zmax_ = z;

      set_ = true;
    }
    else {
      xmin_ = Math.min(x, xmin_); ymin_ = Math.min(y, ymin_); zmin_ = Math.min(z, zmin_);
      xmax_ = Math.max(x, xmax_); ymax_ = Math.max(y, ymax_); zmax_ = Math.max(z, zmax_);
    }
  }

  void addBBox(BBox bbox) {
    if      (! set_) {
      xmin_ = bbox.xmin_; ymin_ = bbox.ymin_; zmin_ = bbox.zmin_;
      xmax_ = bbox.xmax_; ymax_ = bbox.ymax_; zmax_ = bbox.zmax_;

      set_ = bbox.set_;
    }
    else if (bbox.set_) {
      xmin_ = Math.min(bbox.xmin_, xmin_);
      ymin_ = Math.min(bbox.ymin_, ymin_);
      zmin_ = Math.min(bbox.zmin_, zmin_);

      xmax_ = Math.max(bbox.xmax_, xmax_);
      ymax_ = Math.max(bbox.ymax_, ymax_);
      zmax_ = Math.max(bbox.zmax_, zmax_);
    }
  }

  double xmin() { return xmin_; }
  double ymin() { return ymin_; }
  double zmin() { return zmin_; }

  double xmax() { return xmax_; }
  double ymax() { return ymax_; }
  double zmax() { return zmax_; }

  double xSize() { return xmax_ - xmin_; }
  double ySize() { return ymax_ - ymin_; }
  double zSize() { return zmax_ - zmin_; }

  double xMid() { return (xmax_ + xmin_)/2.0; }
  double yMid() { return (ymax_ + ymin_)/2.0; }
  double zMid() { return (zmax_ + zmin_)/2.0; }

  private boolean set_;
  private double  xmin_, ymin_, zmin_;
  private double  xmax_, ymax_, zmax_;
}
