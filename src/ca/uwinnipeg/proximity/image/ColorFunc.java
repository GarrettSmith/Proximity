/**
 * 
 */
package ca.uwinnipeg.proximity.image;


/**
 * Returns a specific colour value of the given ARGB colour integer.
 * @author Garrett Smith
 *
 */
public abstract class ColorFunc extends ImageFunc {

  public ColorFunc() {
    super(0, 255);
  }
}
