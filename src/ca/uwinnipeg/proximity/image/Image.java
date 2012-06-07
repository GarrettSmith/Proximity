/**
 * 
 */
package ca.uwinnipeg.proximity.image;

/**
 * An immutable array of pixels.
 * @author Garrett Smith
 *
 */
public class Image {
  
  // The ARGB integers representing the pixels of the image.
  protected int[][] mPixels;
  
  public final int width, height;
  public final int size;
  
  /**
   * Creates a new image given the ARGB integer pixel values.
   * @param pixels
   */
  public Image(int[][] pixels) {
    width = pixels.length;
    height = pixels[0].length;
    size = width * height;
    
    mPixels = new int[width][height];
    
    for (int i = 0; i < width; i++) {
      for (int j = 0; j < height; j++) {
        mPixels[i][j] = pixels[i][j];
      }
    }
  }
  
  /**
   * Returns a {@link Pixel} object representing a pixel at the given coordinates.
   * @param x
   * @param y
   * @return
   */
  public Pixel getPixel(int x, int y) {
    return new Pixel(this, x, y);
  }

}
