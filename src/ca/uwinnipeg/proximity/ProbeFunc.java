package ca.uwinnipeg.proximity;

/**
 * A probe function is a real-valued function when applied to a perceptual object represents a 
 * feature.
 * @author Garrett Smith
 *
 */
public abstract class ProbeFunc<T> {
  
  /**
   * Maps a perceptual object to a real value representing a feature.
   * @param t the perceptual object.
   * @return the real value representing the feature.
   */
  protected abstract double map(T t);
  
  /**
   * Returns the largest non-normalised value of this feature.
   * @return
   */
  public abstract double maximum();
  
  /**
   * Returns the smallest non-normalised value of this feature.
   * @return
   */
  public abstract double minimum();
  
  /**
   * Maps a perceptual object to a normalised real value representing a feature.
   * @param t the perceptual object.
   * @return the normalised real value representing the feature.
   */
  public double apply(T t) {
    return (map(t) - minimum()) / (maximum() - minimum());
  }
}
