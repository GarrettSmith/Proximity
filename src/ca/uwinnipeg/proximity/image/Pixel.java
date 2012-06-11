/**
 * 
 */
package ca.uwinnipeg.proximity.image;

/**
 * An immutable pixel belonging to an {@link Image}.
 * @author Garrett Smith
 *
 */
public class Pixel {
  
  // The Image this pixel belongs to.
  public final Image image;
  
  // the coordinates of the pixel in the image
  public final int x, y;
  
  /**
   * Creates a new Pixel belonging to the given image at the given coordinates.
   * @param img
   * @param x
   * @param y
   */
  protected Pixel(Image img, int x, int y) {
    image = img;
    this.x = x;
    this.y = y;
  }
  
  /**
   * Returns the integer representing this pixel's colour.
   * @return
   */
  public int getColor() {
    return image.mPixelInts[x + y * image.width];
  }

}
