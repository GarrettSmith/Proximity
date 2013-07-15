/**
 * 
 */
package ca.uwinnipeg.proximity.image;


/**
 * @author garrett
 *
 */
public class DifferentialExcitationFunc extends ImageFunc {
  
  private static final int WINDOW_SIZE = 3;

  public DifferentialExcitationFunc() {
    super(-Math.PI / 2, Math.PI / 2);
  }

  @Override
  protected double map(int index, Image image) {
    int sum = 0;
    double pixel = PerceptualGrayScaleFunc.grayscale(image.getObject(index));
    int size = 0;
    
    int halfWindow = WINDOW_SIZE / 2;
    int x = image.getX(index);
    int y = image.getY(index);
    
    int startX = Math.max(x - halfWindow, 0);
    int startY = Math.max(y - halfWindow, 0);
    
    int endX = Math.min(x + halfWindow, image.getWidth());
    int endY = Math.min(y + halfWindow, image.getHeight());
    
    for (int i = startY; i < endY; i++) {
      for (int j = startX; j < endX; j++) {
        sum += PerceptualGrayScaleFunc.grayscale(image.getPixel(j, i));
        ++size;
      }
    }
    sum -= pixel * size;
    
    if (pixel != 0) {
      return Math.atan((double)sum / pixel);
    }
    else if (sum > 0) {
      return MAXIMUM;
    }
    else {
      return MINIMUM;
    }
  }
  
  @Override
  public String toString() {
    return "Differential Excitation";
  }

}
