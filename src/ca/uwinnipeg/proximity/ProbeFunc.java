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
   * This value must return a double between minimum() and maximum() or it will be constrained.
   * @param t the perceptual object.
   * @return the real value representing the feature between minimum() and maximum().
   */
  protected abstract double map(T t);
  
  /**
   * Returns the largest non-normalised value of this feature.
   * @return
   */
  protected abstract double maximum();
  
  /**
   * Returns the smallest non-normalised value of this feature.
   * @return
   */
  protected abstract double minimum();
  
  /**
   * Maps a perceptual object to a normalised real value representing a feature.
   * @param t the perceptual object.
   * @return the normalised real value representing the feature.
   */
  public double apply(T t) {
    double result = map(t);
    double max = maximum();
    double min = minimum();
    
    // constrain to limits
    result = Math.min(max, result);
    result = Math.max(min, result);
    
    // normalise
    return (result - min) / (max - min);
  }
}
