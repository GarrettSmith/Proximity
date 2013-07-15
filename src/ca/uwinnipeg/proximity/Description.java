package ca.uwinnipeg.proximity;

import java.util.Arrays;

public class Description {
  
  protected final double[] mValues;
  
  public Description(double[] values) {
    mValues = values;
  }    
  
  public double[] getValues() {
    return mValues;
  }
  
  public double getValue(int index) {
    return mValues[index];
  }
  
  protected Integer mHashCode = null;
  
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
  
  public boolean equals(Description other) {
    return Arrays.equals(mValues, other.mValues);
  }
  
  public double distance(Description other) {
    return Math.sqrt(distance(other));
  }
  
  public double squaredDistance(Description other) {
    double sum = 0;
    for (int i = 0; i < mValues.length; i++) {
      double tmp = mValues[i] - other.mValues[i];
      sum += tmp * tmp;
    }
    return sum;
  }
}