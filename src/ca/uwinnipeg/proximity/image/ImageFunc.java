/**
 * 
 */
package ca.uwinnipeg.proximity.image;

import ca.uwinnipeg.proximity.ProbeFunc;

/**
 * @author garrett
 *
 */
public abstract class ImageFunc extends ProbeFunc<Integer, Image> {

  public ImageFunc(double min, double max) {
    super(min, max);
  }

}
