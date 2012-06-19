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
// TODO: throw exceptions for methods that add outside of image bounds
public class Image extends PerceptualSystem<Integer> {
  
  protected int mWidth, mHeight, mSize;
  
  public Image() {
    super();
  }
  
  public Image(int[] pixels, int width, int height) {
    super(width * height);    
    set(pixels, width, height);
  }
  
  public void set(int[] pixels, int width, int height) {    
    this.mWidth = width;
    this.mHeight = height;
    mSize = pixels.length;

    mObjects = new Integer[mSize];
    for (int i = 0; i < pixels.length; i++) {
      mObjects[i] = pixels[i];
    }
  }
  
  public int getWidth() {
    return mWidth;
  }
  
  public int getHeight() {
    return mHeight;
  }
  
  public int getSize() {
    return mSize;
  }

  public int getPixel(int x, int y) {
    return mObjects[y * mWidth + x];
  }
  

  public int getIndex(int x, int y) {
    return y * mWidth + x;
  }
  
  public int getX(int index) {
    return index % mWidth;
  }
  
  public int getY(int index) {
    return index / mWidth;
  }
  
  public int[] getPixels(int left, int top, int right, int bottom) {
    int w = (right - left);
    int h = (bottom - top);
    int s = w * h;
    int[] pxls = new int[s];
    for (int i = 0; i < mSize; i++) {
      pxls[i] = mObjects[(top + (i / w)) * mWidth + (left + (i % w))];
    }
    return pxls;
  }
  
  public int[] getIndices(int left, int top, int right, int bottom) {
    int w = (right - left);
    int h = (bottom - top);
    int s = w * h;
    int[] indices = new int[s];
    for (int i = 0; i < s; i++) {
      indices[i] = (top + (i / w)) * mWidth + (left + (i % w));
    }
    return indices;
  }

}
