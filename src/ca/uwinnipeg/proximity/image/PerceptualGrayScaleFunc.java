/**
 * 
 */
package ca.uwinnipeg.proximity.image;

/**
 * @author garrett
 *
 */
public class PerceptualGrayScaleFunc extends ImageFunc {

  public PerceptualGrayScaleFunc() {
    super(0, 0xFF);
  }

  /* (non-Javadoc)
   * @see ca.uwinnipeg.proximity.ProbeFunc#map(int, ca.uwinnipeg.proximity.PerceptualSystem)
   */
  @Override
  protected double map(int index, Image system) {
    int pixel = system.getObject(index);
    return grayscale(pixel);
  }
  
  public static int grayscale(int color) {
    int r = (color >> 16) & 0xFF;
    int g = (color >> 8) & 0xFF;
    int b = color & 0xFF;
    return (3 * r + 4 * g + b) / 8;
  }
  
  @Override
  public String toString() {
    return "Perceptual Grayscale";
  }

}
