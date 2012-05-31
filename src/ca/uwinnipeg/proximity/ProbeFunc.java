package ca.uwinnipeg.proximity;

/**
 * A probe function is a real-valued function when applied to a perceptual object represents a 
 * feature.
 * @author Garrett Smith
 *
 */
public interface ProbeFunc<T> {
  /**
   * Maps a perceptual object to a real value representing a feature.
   * @param t the perceptual object.
   * @return the real value representing the feature.
   */
  public double apply(T t);
}
