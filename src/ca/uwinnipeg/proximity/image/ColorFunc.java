/**
 * 
 */
package ca.uwinnipeg.proximity.image;

import ca.uwinnipeg.proximity.ProbeFunc;

/**
 * Returns a specific colour value of the given ARGB colour integer.
 * @author Garrett Smith
 *
 */
public abstract class ColorFunc extends ProbeFunc<Pixel> {

  @Override
  protected double maximum() {
    return 255;
  }

  @Override
  protected double minimum() {
    return 0;
  }
}
