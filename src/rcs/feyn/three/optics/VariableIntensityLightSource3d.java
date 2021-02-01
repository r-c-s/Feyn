package rcs.feyn.three.optics;

import rcs.feyn.color.FeynColor;
import rcs.feyn.math.linalg.Matrix44;
import rcs.feyn.math.linalg.Vector3d;

public class VariableIntensityLightSource3d extends ConstantLightSource3d {
	
	private double intensity;

  public VariableIntensityLightSource3d(double intensity) {
    this(FeynColor.white, intensity);
  }
  
  public VariableIntensityLightSource3d(FeynColor color, double intensity) {
    super(color);
    this.intensity = intensity;
  }

  @Override
  public double getIntensityAt(Vector3d position, Vector3d normal) {
    Vector3d direction = this.position.sub(position);
    double distSquared = direction.lengthSquared();
    
    direction.divLocal(Math.sqrt(distSquared));
    
    return intensity * direction.dotProd(normal.normalize()) / distSquared;
  } 

  @Override
  public double getIntensityAt(Vector3d position, Vector3d normal, Matrix44 view) {
    Vector3d direction = this.position.affineTransform(view).subLocal(position);
    double distSquared = direction.lengthSquared();
    
    direction.divLocal(Math.sqrt(distSquared));
    
    return intensity * direction.dotProd(normal.normalize()) / distSquared;
  } 
}
