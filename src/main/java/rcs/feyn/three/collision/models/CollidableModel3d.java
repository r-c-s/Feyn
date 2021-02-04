package rcs.feyn.three.collision.models;

import rcs.feyn.math.linalg.Matrix44;
import rcs.feyn.math.linalg.Vector3d;
import rcs.feyn.three.collision.BoundingObject3d;
import rcs.feyn.three.collision.Collidable3d;
import rcs.feyn.three.render.models.Model3d;
import rcs.feyn.three.render.models.Model3dFace;
import rcs.feyn.three.render.models.Model3dVertices;

public class CollidableModel3d extends Model3d implements Collidable3d {

  protected BoundingObject3d outerBoundingObject; 

  public CollidableModel3d(
      Model3dVertices vertices,
      Model3dFace[] faces, 
      BoundingObject3d outerBoundingObject3d) {
    super(vertices, faces);
    this.outerBoundingObject = outerBoundingObject3d;
  }  
  
  @Override
  public void translate(Vector3d v3d) {
    super.translate(v3d);
    outerBoundingObject.translate(v3d);
  }

  @Override
  public void transform(Matrix44 m4x4) {
    super.transform(m4x4);
    outerBoundingObject.transform(m4x4);
  } 
  
  @Override
  public final BoundingObject3d getOuterBoundingObject() { 
    return outerBoundingObject;
  }
}