/**
 * 
 */
package ca.uwinnipeg.proximity.image;

import java.util.ArrayList;
import java.util.List;

import ca.uwinnipeg.proximity.PerceptualSystem;

/**
 * An immutable array of pixels.
 * @author Garrett Smith
 *
 */
public class Image extends PerceptualSystem<Pixel> {
  
  // The ARGB integers representing the pixels of the image.
  protected int[] mPixelInts;
  
  public final int width, height;
  public final int size;
  
  public Image(int[] pixels, int width, int height) {
    super(width * height);
    
    this.width = width;
    this.height = height;
    size = pixels.length;

    mPixelInts = new int[size];

    for (int i = 0; i < size; i++) {
        mPixelInts[i] = pixels[i];
        addObject(i, new Pixel(this, i % width, i / width));
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
    return mObjects.get(y * width + x);
  }
  
  /**
   * Returns a {@link Pixel} list of all the pixels within the given rectangle.
   * @param left
   * @param top
   * @param right
   * @param bottom
   * @return
   */
  public List<Pixel> getPixels(int left, int top, int right, int bottom) {
    int width = (right - left);
    int height = (bottom - top);
    List<Pixel> pxls = new ArrayList<Pixel>(width * height);
    for (int x = left; x < right; x++) {
      for (int y = top; y < bottom; y++) {
        pxls.add(getPixel(x, y));
      }
    }
    return pxls;
  }
  
  public int[] getIndices(int left, int top, int right, int bottom) {
    int w = (right - left);
    int h = (bottom - top);
    int s = w * h;
    int[] indices = new int[s];
    for (int i = 0; i < s; i++) {
      indices[i] = (top + (i / w)) * width + (left + (i % w));
    }
    return indices;
  }

}
