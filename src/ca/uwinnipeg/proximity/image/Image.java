/**
 * 
 */
package ca.uwinnipeg.proximity.image;

import java.util.HashSet;
import java.util.Set;

/**
 * An immutable array of pixels.
 * @author Garrett Smith
 *
 */
public class Image {
  
  // The ARGB integers representing the pixels of the image.
  protected int[][] mPixelInts;
  
  // The array of pixel objets
  protected Pixel[][] mPixels;
  
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
    
    mPixelInts = new int[width][height];
    mPixels = new Pixel[width][height];
    
    for (int i = 0; i < width; i++) {
      for (int j = 0; j < height; j++) {
        mPixelInts[i][j] = pixels[i][j];
      }
    }
  }
  
  public Image(int[] pixels, int width, int height) {
    this.width = width;
    this.height = height;
    size = pixels.length;

    mPixelInts = new int[width][height];
    mPixels = new Pixel[width][height];

    for (int i = 0; i < width; i++) {
      for (int j = 0; j < height; j++) {
        mPixelInts[i][j] = pixels[j * width + i];
      }
    }
  }
  
  /**
   * Returns a {@link Pixel} object representing a pixel at the given coordinates, requesting a 
   * pixel from the same position multiple times will return the same pixel object.
   * @param x
   * @param y
   * @return
   */
  public Pixel getPixel(int x, int y) {
    Pixel p = mPixels[x][y];
    
    // check if the pixel was cached, if not create it and cache it
    if (p == null) {
      p = new Pixel(this, x, y);
      mPixels[x][y] = p;
    }
    
    return p;
  }
  
  /**
   * Returns a {@link Pixel} set of all the pixels within the given rectangle.
   * @param left
   * @param top
   * @param right
   * @param bottom
   * @return
   */
  public Set<Pixel> getPixels(int left, int top, int right, int bottom) {
    int width = (right - left);
    int height = (bottom - top);
    Set<Pixel> pxls = new HashSet<Pixel>(width * height);
    for (int i = left; i < right; i++) {
      for (int j = top; j < bottom; j++) {
        pxls.add(getPixel(i, j));
      }
    }
    return pxls;
  }

}
