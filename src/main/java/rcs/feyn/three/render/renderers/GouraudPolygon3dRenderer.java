package rcs.feyn.three.render.renderers;

import rcs.feyn.color.ColorUtils;
import rcs.feyn.color.FeynColor;
import rcs.feyn.math.MathUtils;
import rcs.feyn.math.linalg.Vector3d;
import rcs.feyn.three.gfx.Graphics3d;
import rcs.feyn.three.kernel.FeynApp3d;
import rcs.feyn.three.optics.LightingUtils;

public class GouraudPolygon3dRenderer {

  public static void render(
      Graphics3d graphics, 
      Vector3d[] viewPortCoords, 
      double[] intensities,
      int color) {
    int size = viewPortCoords.length;
    if (size < 3) {
      return;
    }
    
    int gw = (int) graphics.getRaster().getWidth();
    int gh = (int) graphics.getRaster().getHeight(); 
    
    for (int i = 1; i < size-1; i++) { 
      Vector3d sa = viewPortCoords[0];
      Vector3d sb = viewPortCoords[i];
      Vector3d sc = viewPortCoords[i+1];
      
      double za = sa.z();
      double zb = sb.z();
      double zc = sc.z();
      double zd = (za+zb+zc)/3;
      
      double xa = sa.x();
      double xb = sb.x();
      double xc = sc.x();
      double xd = (xa+xb+xc)/3;
      
      double ya = sa.y();
      double yb = sb.y();
      double yc = sc.y();
      double yd = (ya+yb+yc)/3; 
      
      double u1 = xb - xa;
      double v1 = xc - xa;
      double u2 = yb - ya;
      double v2 = yc - ya;
      double u3 = zb - za;
      double v3 = zc - za;
      
      double aa = u2*v3 - u3*v2;
      double bb = u3*v1 - u1*v3;
      double cc = u1*v2 - u2*v1;
      
      double dInvZdx = -aa/cc;
      double dZdy = -bb/cc;
       
      int ymin = MathUtils.roundToInt(MathUtils.max(MathUtils.min(ya, yb, yc), 0));
      int ymax = MathUtils.roundToInt(MathUtils.min(MathUtils.max(ya, yb, yc), gh));
      
      double A = Math.abs(cc) / 2; 
      double fa = intensities[0] / A; 
      double fb = intensities[i] / A; 
      double fc = intensities[i+1] / A; 
      double ak0 = yc*(xb-xc)-xc*(yb-yc);
      double ak1 = yb-yc;
      double ak2 = xb-xc;          
      double bk0 = yc*(xa-xc)-xc*(ya-yc);
      double bk1 = ya-yc;
      double bk2 = xa-xc;
        
      for (int y = ymin; y <= ymax; y++) {
        double ximin = Integer.MIN_VALUE;
        double xjmin = Integer.MIN_VALUE;
        double xkmin = Integer.MIN_VALUE;
        double ximax = Integer.MAX_VALUE;
        double xjmax = Integer.MAX_VALUE;
        double xkmax = Integer.MAX_VALUE; 
        
        if ((y - yb) * (y - yc) <= 0 && yb != yc) {
          ximax = xc + (y - yc)/(yb - yc) * (xb - xc);
          ximin = ximax;
        }
        if ((y - yc) * (y - ya) <= 0 && yc != ya) {
          xjmax = xa + (y - ya)/(yc - ya) * (xc - xa);
          xjmin = xjmax;
        }
        if ((y - ya) * (y - yb) <= 0 && ya != yb) {
          xkmax = xb + (y - yb)/(ya - yb) * (xa - xb);
          xkmin = xkmax;
        }
        
        int xmin = MathUtils.roundToInt(MathUtils.max(MathUtils.min(ximax, xjmax, xkmax), 0));
        int xmax = MathUtils.roundToInt(MathUtils.min(MathUtils.max(ximin, xjmin, xkmin), gw));

        double aMin = 0.5 * Math.abs(ak0 + xmin*ak1 - y*ak2); 
        double bMin = 0.5 * Math.abs(bk0 + xmin*bk1 - y*bk2);
        double shadeMin = fa*(aMin) + fb*(bMin) + fc*(A - aMin - bMin);
        
        double aMax = 0.5 * Math.abs(ak0 + xmax*ak1 - y*ak2); 
        double bMax = 0.5 * Math.abs(bk0 + xmax*bk1 - y*bk2);
        double shadeMax = fa*(aMax) + fb*(bMax) + fc*(A - aMax - bMax);
        
        double dShadeFactorDx = (shadeMax-shadeMin) / (xmax-xmin);
        double shadeFactor = shadeMin + (1-aMin/A)*dShadeFactorDx + (xmin-xmin)*dShadeFactorDx;

        double invZ = zd + (y-yd)*dZdy + (xmin-xd)*dInvZdx;
        
        for (int x = xmin; x < xmax; x++, invZ += dInvZdx, shadeFactor += dShadeFactorDx) {
          int source = ColorUtils.mulRGBA(color, shadeFactor);
          graphics.putPixel(x, y, invZ, source); 
        } 
      } 
    }
  }

  /**
   * Does not work with multiple colored light sources
   */
  public static void render(
      Graphics3d graphics, 
      Vector3d[] viewPortCoords, 
      double[] intensities,
      int[] colors) {
    
    int size = viewPortCoords.length;
    if (size < 3) {
      return;
    }

    double ambientLightIntensity = FeynApp3d.getAmbientLight().getIntensity();
    
    int gw = (int) graphics.getRaster().getWidth();
    int gh = (int) graphics.getRaster().getHeight(); 
    
    for (int i = 1; i < size-1; i++) { 
      Vector3d sa = viewPortCoords[0];
      Vector3d sb = viewPortCoords[i];
      Vector3d sc = viewPortCoords[i+1];
      
      // needs now to interpolate colors in order to support multiple colored light sources
      FeynColor colorA = new FeynColor(colors[0]);
      FeynColor colorB = new FeynColor(colors[i]);
      FeynColor colorC = new FeynColor(colors[i+1]);
      
      double za = sa.z();
      double zb = sb.z();
      double zc = sc.z();
      double zd = (za+zb+zc)/3;
      
      double xa = sa.x();
      double xb = sb.x();
      double xc = sc.x();
      double xd = (xa+xb+xc)/3;
      
      double ya = sa.y();
      double yb = sb.y();
      double yc = sc.y();
      double yd = (ya+yb+yc)/3; 
      
      double u1 = xb - xa;
      double v1 = xc - xa;
      double u2 = yb - ya;
      double v2 = yc - ya;
      double u3 = zb - za;
      double v3 = zc - za;
      
      double aa = u2*v3 - u3*v2;
      double bb = u3*v1 - u1*v3;
      double cc = u1*v2 - u2*v1;
      
      double dInvZdx = -aa/cc;
      double dZdy = -bb/cc;
       
      int ymin = MathUtils.roundToInt(MathUtils.max(MathUtils.min(ya, yb, yc), 0));
      int ymax = MathUtils.roundToInt(MathUtils.min(MathUtils.max(ya, yb, yc), gh));
      
      double A = Math.abs(cc) / 2; 
      double fa = intensities[0] / A; 
      double fb = intensities[i] / A; 
      double fc = intensities[i+1] / A; 
      double ak0 = yc*(xb-xc)-xc*(yb-yc);
      double ak1 = yb-yc;
      double ak2 = xb-xc;          
      double bk0 = yc*(xa-xc)-xc*(ya-yc);
      double bk1 = ya-yc;
      double bk2 = xa-xc;
        
      for (int y = ymin; y <= ymax; y++) {
        double ximin = Integer.MIN_VALUE;
        double xjmin = Integer.MIN_VALUE;
        double xkmin = Integer.MIN_VALUE;
        double ximax = Integer.MAX_VALUE;
        double xjmax = Integer.MAX_VALUE;
        double xkmax = Integer.MAX_VALUE; 
        
        if ((y - yb) * (y - yc) <= 0 && yb != yc) {
          ximax = xc + (y - yc)/(yb - yc) * (xb - xc);
          ximin = ximax;
        }
        if ((y - yc) * (y - ya) <= 0 && yc != ya) {
          xjmax = xa + (y - ya)/(yc - ya) * (xc - xa);
          xjmin = xjmax;
        }
        if ((y - ya) * (y - yb) <= 0 && ya != yb) {
          xkmax = xb + (y - yb)/(ya - yb) * (xa - xb);
          xkmin = xkmax;
        }
        
        int xmin = MathUtils.roundToInt(MathUtils.max(MathUtils.min(ximax, xjmax, xkmax), 0));
        int xmax = MathUtils.roundToInt(MathUtils.min(MathUtils.max(ximin, xjmin, xkmin), gw));

        double aMin = 0.5 * Math.abs(ak0 + xmin*ak1 - y*ak2); 
        double bMin = 0.5 * Math.abs(bk0 + xmin*bk1 - y*bk2);
        double shadeMin = fa*(aMin) + fb*(bMin) + fc*(A - aMin - bMin);
        
        double aMax = 0.5 * Math.abs(ak0 + xmax*ak1 - y*ak2); 
        double bMax = 0.5 * Math.abs(bk0 + xmax*bk1 - y*bk2);
        double shadeMax = fa*(aMax) + fb*(bMax) + fc*(A - aMax - bMax);
        
        double dShadeFactorDx = (shadeMax-shadeMin) / (xmax-xmin);
        double shadeFactor = shadeMin + (1-aMin/A)*dShadeFactorDx + (xmin-xmin)*dShadeFactorDx;

        double invZ = zd + (y-yd)*dZdy + (xmin-xd)*dInvZdx;
        
        for (int x = xmin; x < xmax; x++, invZ += dInvZdx, shadeFactor += dShadeFactorDx) {

          double[] t = cartesian2barycentric(x, y, sa, sb, sc);
          
          int color = new FeynColor(
              MathUtils.roundToInt(colorA.getRed() * t[0] + colorB.getRed() * t[1] + colorC.getRed() * t[2]),
              MathUtils.roundToInt(colorA.getGreen() * t[0] + colorB.getGreen() * t[1] + colorC.getGreen() * t[2]),
              MathUtils.roundToInt(colorA.getBlue() * t[0] + colorB.getBlue() * t[1] + colorC.getBlue() * t[2])
              
              ).getRGBA();
          
          graphics.putPixel(x, y, invZ, color); 
        } 
      } 
    }
  }
  
  static double[] cartesian2barycentric(int x, int y, Vector3d a, Vector3d b, Vector3d c) {
    
    double x1 = a.x();
    double x2 = b.x();
    double x3 = c.x();
    double y1 = a.y();
    double y2 = b.y();
    double y3 = c.y();
    
    double y2y3 = y2 - y3;
    double x3x2 = x3 - x2;
    double x1x3 = x1 - x3;
    double y1y3 = y1 - y3;
    double y3y1 = y3 - y1;
    double xx3  = x  - x3;
    double yy3  = y  - y3;
    
    double d = y2y3 * x1x3 + x3x2 * y1y3;
    double lambda1 = (y2y3 * xx3 + x3x2 * yy3) / d;
    double lambda2 = (y3y1 * xx3 + x1x3 * yy3) / d;
    
    return new double[] {
      lambda1,
      lambda2,
      1 - lambda1 - lambda2
    };
  }
}