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
public abstract class ColorFunc extends ProbeFunc<Integer> {

  public ColorFunc() {
    super(0, 255);
  }
}
