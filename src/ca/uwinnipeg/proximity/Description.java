package ca.uwinnipeg.proximity;

import java.util.Arrays;

/**
 * A description is a collection of values calculated via {@link ProbeFunc} corresponding to a 
 * perceptual object.
 * @author Garrett Smith
 *
 */
public class Description {
  
  // the calculated values
  protected final double[] mValues;
  
  // Cache the hash code to speed up comparisons
  protected Integer mHashCode = null;
  
  /**
   * Creates a description with the given values.
   * @param values
   */
  public Description(double[] values) {
    mValues = values;
  }    
  
  /**
   * Returns the array of values.
   * @return
   */
  public double[] getValues() {
    return mValues;
  }
  
  /**
   * Returns the value at the given index.
   * @param index
   * @return
   */
  public double getValue(int index) {
    return mValues[index];
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + Arrays.hashCode(mValues);
    return result;
  }
  
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Description other = (Description) obj;
    if (!Arrays.equals(mValues, other.mValues))
      return false;
    return true;
  }
  
  /**
   * Checks if this description is equal to another.
   * @param other
   * @return
   */
  public boolean equals(Description other) {
    return Arrays.equals(mValues, other.mValues);
  }
  
  /**
   * Calculates the distance between this description and another.
   * @param other
   * @return
   */
  public double distance(Description other) {
    return Math.sqrt(squaredDistance(other));
  }
  
  /**
   * Calculates the squared distance between this description and another.
   * <p>
   * This is faster than calculating the distance as it avoids taking the square root.
   * @param other
   * @return
   */
  public double squaredDistance(Description other) {
    double sum = 0;
    for (int i = 0; i < mValues.length; i++) {
      double tmp = mValues[i] - other.mValues[i];
      sum += tmp * tmp;
    }
    return sum;
  }
}