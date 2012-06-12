/**
 * 
 */
package ca.uwinnipeg.proximity.image;

import ca.uwinnipeg.proximity.PerceptualSystem;

/**
 * An immutable array of pixels.
 * @author Garrett Smith
 *
 */
public class Image extends PerceptualSystem<Integer> {
  
  public final int width, height;
  public final int size;
  
  public Image(int[] pixels, int width, int height) {
    super(width * height);
    
    this.width = width;
    this.height = height;
    size = pixels.length;

    for (int i = 0; i < size; i++) {
      addObject(pixels[i]);
    }
  }

  public int getPixel(int x, int y) {
    return mObjects.get(y * width + x);
  }
  

  public int getIndex(int x, int y) {
    return y * width + x;
  }
  
  public int getX(int index) {
    return index % width;
  }
  
  public int getY(int index) {
    return index / width;
  }
  
  public int[] getPixels(int left, int top, int right, int bottom) {
    int w = (right - left);
    int h = (bottom - top);
    int s = w * h;
    int[] pxls = new int[s];
    for (int i = 0; i < size; i++) {
      pxls[i] = mObjects.get((top + (i / w)) * width + (left + (i % w)));
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
