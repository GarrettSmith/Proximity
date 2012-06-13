package ca.uwinnipeg.proximity;

/**
 * A probe function is a real-valued function when applied to a perceptual object represents a 
 * feature.
 * @author Garrett Smith
 *
 */
public abstract class ProbeFunc<T> {
  
  public final double MAXIMUM;
  public final double MINIMUM;
  
  /**
   * Maps a perceptual object to a real value representing a feature.
   * This value must return a double between minimum() and maximum() or it will be constrained.
   * @param t the perceptual object.
   * @return the real value representing the feature between minimum() and maximum().
   */
  protected abstract double map(T t);  

  
  /**
   * Creates a new probe function that maps perceptual objects within the given range.
   * @param min
   * @param max
   */
  public ProbeFunc(double min, double max) {
    MAXIMUM = max;
    MINIMUM = min;
  }
  
  /**
   * Maps a perceptual object to a normalised real value representing a feature.
   * @param t the perceptual object.
   * @return the normalised real value representing the feature.
   */
  public double apply(T t) {
    double result = map(t);
    //double max = maximum();
    //double min = minimum();
    
    // constrain to limits, this is no longer done to save time
    // Please make sure your values are within the limits
    //result = Math.min(max, result);
    //result = Math.max(min, result);
    
    // normalise
    return (result - MINIMUM) / (MAXIMUM - MINIMUM);
  }
}
