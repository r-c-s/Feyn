package rcs.feyn.three.render.patches;

import rcs.feyn.three.gfx.Graphics3d;
import rcs.feyn.three.kernel.Pipeline3d;
import rcs.feyn.three.render.renderers.Line3dRenderer;
import rcs.feyn.color.FeynColor;
import rcs.feyn.math.linalg.Matrix44;
import rcs.feyn.math.linalg.Vector3d;

public class Line3dPatch extends Patch3d {
  
  protected Vector3d a, b;

  public Line3dPatch(Vector3d a, Vector3d b, FeynColor color) {
    super(color);
    this.a = a;
    this.b = b;
  }

  @Override
  protected Vector3d getCenter() {
    return a.midPoint(b);
  }

  @Override
  public final void render(Graphics3d graphics, Matrix44 view, Matrix44 projection, Matrix44 viewPort) {
    Vector3d[] viewVertices = Pipeline3d
        .worldToViewSpaceCoordinates(new Vector3d[]{ a, b }, view);
    
    Vector3d[] clippedVertices = Pipeline3d
        .clipViewSpaceCoordinates(viewVertices);
    
    if (clippedVertices.length < 2) {
      return;
    }
    
    Vector3d[] ndcVertices = Pipeline3d
        .viewToNormalizedDeviceCoordinates(clippedVertices, projection); 
    
    Vector3d[] vpcVertices = Pipeline3d
        .ndcToDeviceCoordinates(ndcVertices, viewPort);

    int colorWithLighting = Pipeline3d.applyLightning(color.getRGBA());
    
    Line3dRenderer.render(
        graphics,
        vpcVertices[0], 
        vpcVertices[1],
        colorWithLighting);
  }
}
