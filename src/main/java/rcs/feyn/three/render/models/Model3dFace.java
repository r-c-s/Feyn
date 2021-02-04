package rcs.feyn.three.render.models;

import rcs.feyn.color.AbstractColorable;
import rcs.feyn.color.FeynColor;
import rcs.feyn.three.render.renderers.RenderOptions3d;

public class Model3dFace extends AbstractColorable {
  
  protected RenderOptions3d options = RenderOptions3d.defaults();
  
  protected int[] indices;

  public Model3dFace(int[] indices, FeynColor color) {
    super(color);
    this.indices = indices;
  }
  
  public void inverse() {
    int len = indices.length;
    int[] reversed = new int[len];
    for (int i = 0; i < len; i++) {
      reversed[i] = indices[len - 1 - i];
    }
    indices = reversed;
  }

  public int[] getIndices() {
    return indices.clone();
  }

  public RenderOptions3d getRenderOptions() {
    return options;
  }
  
  public void setRenderOptions(RenderOptions3d options) {
    this.options = options;
  }
}