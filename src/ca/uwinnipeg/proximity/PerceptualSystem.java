/**
 * 
 */
package ca.uwinnipeg.proximity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A perceptual system consists of a List of perceptual objects and and a List of {@link ProbeFunc}.
 * @author Garrett Smith
 *
 */
// TODO: Switch over to arrays
// TODO: Look at sorting indices
// TODO: square epsilon not square root distance
public class PerceptualSystem<O> {
  
  // The perceptual objects within the system
  protected O[] mObjects;
  
  // The description of every perceptual object in the system.
  // To get a description use mDescriptions[Object index][ProbeFunc index].
  private List<double[]> mDescriptions = new ArrayList<double[]>();
  
  // The list of probe functions
  protected List<ProbeFunc<O>> mProbeFuncs = new ArrayList<ProbeFunc<O>>();
  int mProbeFuncCount = 0;
  
  protected boolean mCache = false;
  
  /**
   * A task that can watch the status of a running {@link PerceptualSystem} method.
   * @author Garrett Smith
   *
   */
  public interface PerceptualSystemSubscriber {
    /**
     * Set by the running method to tell the task the current progress of the calculation
     * @param progress
     */
    public void updateProgress(float progress);
    
    /**
     * Called by the running method to see it has been cancelled and should stop.
     * @return
     */
    public boolean isCancelled();
  }

  /**
   * Creates an empty perceptual system.
   * @param mSize
   */
  public PerceptualSystem() {}
  
  /**
   * Creates an empty perceptual system with the given number of objects.
   * @param size
   */
  public PerceptualSystem(int size) {
    mObjects = (O[]) new Object[size];
    if (mCache) mDescriptions = new ArrayList<double[]>(size);
  }
  
  /**
   * Determines if two perceptual objects are equal.
   * @param x
   * @param y
   * @return true if the two objects have the same features, false otherwise
   */
  public boolean equal(O x, O y) {    
    // Optimisation for comparing the same object
    if (x == y) {
      return true;
    }
    else {
      return equal(getDescription(x), getDescription(y));
    }
  }

  /**
   * Determines if two descriptions are equal.
   * @param x
   * @param y
   * @return true if the two descriptions have the same features, false otherwise
   */
  public boolean equal(double[] x, double[] y) {
    // compare the value of each probe function
    for (int i = 0; i < mProbeFuncCount; i++) {
      // These values need to be cast or they don't compare properly
      double xVal = x[i];
      double yVal = y[i];
      if (xVal != yVal) {
        return false;
      }
    }    
    // We've made it past every check so they are equal
    return true;
  }

  /**
   * Calculates the distance between the given objects in feature space.
   * @param x
   * @param y
   * @return the distance between the given objects in feature space
   */
  public double distance(O x, O y) {
    double[] descX = getDescription(x);
    double[] descY = getDescription(y);
    return distance(descX, descY);
  }

  /**
   * Calculates the distance between the given descriptions in feature space.
   * @param x
   * @param y
   * @return the distance between the given descriptions in feature space
   */
  public double distance(double[] x, double[] y) {
    return Math.sqrt(quickDistance(x, y));
  }

  /**
   * Calculates the distance between the given descriptions in feature space.
   * @param x
   * @param y
   * @return the distance between the given descriptions in feature space
   */
  public double quickDistance(double[] x, double[] y) {
    double sum = 0;
    for (int i = 0; i < mProbeFuncCount; i++) {
      double tmp = x[i] - y[i];
      sum += tmp * tmp;
    }
    return sum;
  }
  
  /**
   * Returns a description-based neighbourhood.
   * @param x the object to compare against
   * @param objs the List of all objects being compared
   * @return the List of objects within the neighbourhood
   */
  public List<O> getDescriptionBasedNeighbourhood(O x, List<O> region) {
    double[] descX = getDescription(x);
    List<O> neighbourhood = new ArrayList<O>();
    int size = region.size();
    for (int i = 0; i < size; i++) {
      O y = region.get(i);
      double[] descY = getDescription(y);
      if (equal(descX, descY)) {
        neighbourhood.add(y);
      }
    }
    return neighbourhood;
  }

  /**
   * Returns a hybrid neighbourhood.
   * @param x the object to compare against
   * @param region the List of all objects being compared
   * @param epsilon the threshold objects must be under to be an element
   * @return the List of objects within the neighbourhood
   */
  public List<O> getHybridNeighbourhood(O x, List<O> region, double epsilon) {
    double[] descX = getDescription(x);
    List<O> neighbourhood = new ArrayList<O>();    
    int size = region.size();
    for (int i = 0; i < size; i++) {
      O y = region.get(i);
      double[] descY = getDescription(y);
      if (distance(descX, descY) < epsilon) {
        neighbourhood.add(y);
      }
    }
    return neighbourhood;
  }

  /**
   * Returns a hybrid neighbourhood.
   * @param x the object to compare against
   * @param region the List of all objects being compared
   * @param epsilon the threshold objects must be under to be an element
   * @return the List of objects within the neighbourhood
   */
  public List<O> getHybridNeighbourhood(int x, int[] indices, double epsilon) {
    double[] descX = getDescription(x);
    List<O> neighbourhood = new ArrayList<O>();
    for (int i = 0; i < indices.length; i++) {
      double[] descY = getDescription(indices[i]);
      if (distance(descX, descY) < epsilon) {
        neighbourhood.add(mObjects[indices[i]]);
      }
    }
    return neighbourhood;
  }
  
  public List<Integer> getHybridNeighbourhoodIndices(int x, int[] indices, double epsilon) {
    double[] descX = getDescription(x);
    List<Integer> neighbourhood = new ArrayList<Integer>();
    for (int i = 0; i < indices.length; i++) {
      double[] descY = getDescription(indices[i]);
      if (distance(descX, descY) < epsilon) {
        neighbourhood.add(indices[i]);
      }
    }
    return neighbourhood;
  }
  
  public List<Integer> getHybridNeighbourhoodIndices(int x, int[] indices, double epsilon, 
      PerceptualSystemSubscriber sub) {
    
    // check if we should stop
    if (sub.isCancelled()) return null;
    sub.updateProgress(0);
    
    double[] descX = getDescription(x);
    List<Integer> neighbourhood = new ArrayList<Integer>();
    for (int i = 0; i < indices.length; i++) {
      
      // check if we should stop
      if (sub.isCancelled()) return null;
      
      double[] descY = getDescription(indices[i]);
      if (distance(descX, descY) < epsilon) {
        neighbourhood.add(indices[i]);
      }
      
      //set progress
      sub.updateProgress((float)i / indices.length);
    }
    return neighbourhood;
  }

  public List<double[]> getDescriptionUnion(List<O> A, List<O> B) {
    // get union of objects
    List<O> objectUnion = new ArrayList<O>(A);
    objectUnion.addAll(B);
    
    // get the description of each object
    List<double[]> union = new ArrayList<double[]>();
    for (O o : objectUnion) { 
      union.add(getDescription(o));
    }
    
    return union;
  }

  /**
   * Returns the description-based intersection of a list of regions.
   * @param regions the list of regions
   * @return a List all descriptions within the intersect of the two regions
   */
  public List<double[]> getDescriptionBasedIntersect(List<List<O>> regions) {
    // calculate the objects in the intersect of the first two regions
    List<double[]> intersect = 
        new ArrayList<double[]>(
            getDescriptionBasedIntersect(regions.get(0), regions.get(1)));

    // calculate the intersect of the current intersect and the next region
    for (int i = 2; i < regions.size(); i++) {
      List<O> region = regions.get(i);
      List<double[]> newIntersect = new ArrayList<double[]>();

      for (O a : region) {
        double[] descA = getDescription(a);
        for (double[] descB : intersect) {
          if (equal(descA, descB)) {
            // If a match was found add to intersect and stop comparing to this object
            newIntersect.add(descA);
            break;
          }
        }
      }

        intersect = newIntersect;
      }

      return intersect;
    }

    /**
   * Returns the description-based intersection of two regions.
   * @param A the first region
   * @param B the second region
   * @return a List all descriptions within the intersect of the two regions
   */
  public List<double[]> getDescriptionBasedIntersect(List<O> A, List<O> B) {
    List<double[]> intersect = new ArrayList<double[]>();
    for (O a : A) {
      for (O b : B) {
        if (equal(a, b)) {
          // If a match was found add to intersect and stop comparing to this object
          intersect.add(getDescription(a));
          break;
        }
      }
    }
    return intersect;
  }
  
  /**
   * Returns the objects with descriptions within the description-based intersection of two regions.
   * @param A the first region
   * @param B the second region
   * @return a List all descriptions within the intersect of the two regions
   */
  public Set<O> getDescriptionBasedIntersectObjects(List<O> A, List<O> B) {
    Set<O> intersect = new HashSet<O>();
    int sizeA = A.size();
    int sizeB = B.size();
    
    // get all the descriptions of objects in B only once
    List<double[]> descsB = new ArrayList<double[]>(sizeB);
    for (int i = 0; i < sizeB; i++) {
      descsB.add(i, getDescription(B.get(i)));
    }
    
    for (int i = 0; i < sizeA; i++) {
      O a = A.get(i);
      double[] descA = getDescription(a);
      for (int j = 0; j < sizeB; j++) {
        if (equal(descA, descsB.get(j))) {  
          intersect.add(a);
          intersect.add(B.get(j));
        }
      }
    }
    return intersect;
  }
  
  /**
   * Returns the objects with descriptions within the description-based intersection of two regions.
   * @param A the first region
   * @param B the second region
   * @return a List all descriptions within the intersect of the two regions
   */
  public List<Integer> getDescriptionBasedIntersectIndices(List<Integer> A, List<Integer> B) {
    int sizeA = A.size();
    int sizeB = B.size();
    
    List<Integer> intersect = new ArrayList<Integer>(sizeA + sizeB);
    
    // get all the descriptions of objects in B only once
    List<double[]> descsB = new ArrayList<double[]>(sizeB);
    for (int i = 0; i < sizeB; i++) {
      descsB.add(i, getDescription(B.get(i)));
    }
    
    for (int i = 0; i < sizeA; i++) {
      int a = A.get(i);
      double[] descA = getDescription(a);
      for (int j = 0; j < sizeB; j++) {
        boolean added = false;
        if (equal(descA, descsB.get(j))) {
          if (!added) intersect.add(a);
          int b = B.get(j);
          if (!intersect.contains(b)) intersect.add(b);
          added = true;
        }
      }
    }
    return intersect;
  }
  
  public List<Integer> getDescriptionBasedIntersectIndices(List<Integer> A, List<Integer> B, 
      PerceptualSystemSubscriber sub) {
    int sizeA = A.size();
    int sizeB = B.size();
    
    int marker = -1;
    int[] intersect = new int[sizeA + sizeB];
    Arrays.fill(intersect, marker);
    
    // get all the descriptions of objects in B only once
    double[][] descsB = new double[sizeB][mProbeFuncCount];
    for (int i = 0; i < sizeB; i++) {
      // check if should stop
      if (sub.isCancelled()) return null;
      descsB[i] = getDescription(B.get(i));
    }
    
    for (int i = 0; i < sizeA; i++) {
      
      // check if we should stop
      if (sub.isCancelled()) return null;
      
      int a = A.get(i);
      double[] descA = getDescription(a);
      for (int j = 0; j < sizeB; j++) {
        double[] descB = descsB[j];
        if (equal(descA, descB)) {
          intersect[i] = a;
          intersect[sizeA + j] = B.get(j);
        }
      }      
      
      // set the current progress
      sub.updateProgress((float)i / sizeA);
    }
    
    List<Integer> rtn = new ArrayList<Integer>(intersect.length);
    for (int i = 0; i < intersect.length; i++) {
      if (intersect[i] != marker) {
        rtn.add(intersect[i]);
      }
    }
    
    return rtn;
  }
  
  /**
   * Checks whether two regions are near using their description-based intersection.
   * @param A the first region
   * @param B the second region
   * @return true if their description-based intersect is non-empty
   */
  public boolean descriptionBasedNear(List<O> A, List<O> B) {
    return descriptionBasedDegree(A, B) > 0;
  }
  
  /**
   * Returns the degree using the description-based intersection.
   * @param A the first region
   * @param B the second region
   * @return the degree of the description-based intersection
   */
  public int descriptionBasedDegree(List<O> A, List<O> B) {
    return getDescriptionBasedIntersect(A, B).size();
  }
  
  
  /**
   * Returns the hybrid intersection of a list of regions.
   * @param regions the list of regions
   * @param epsilon the threshold distance between objects must be under to be an element
   * @return a List all descriptions within the intersect of the two regions
   */
  public List<double[]> getHybridIntersect(List<List<O>> regions, double epsilon) {
    // calculate the objects in the intersect of the first two regions
    List<double[]> intersect = 
        new ArrayList<double[]>(
            getDescriptionBasedIntersect(regions.get(0), regions.get(1)));

    // calculate the intersect of the current intersect and the next region
    for (int i = 2; i < regions.size(); i++) {
      List<O> region = regions.get(i);
      List<double[]> newIntersect = new ArrayList<double[]>();

      for (O a : region) {
        double[] descA = getDescription(a);
        for (double[] descB : intersect) {
          if (distance(descA, descB) < epsilon) {
            // If a match was found add to intersect and stop comparing to this object
            newIntersect.add(descA);
            break;
          }
        }
      }

        intersect = newIntersect;
      }

      return intersect;
    }
  
  /**
   * Returns the hybrid intersection of two regions.
   * @param A the first region
   * @param B the second region
   * @param epsilon the threshold distance between objects must be under to be an element
   * @return a List all descriptions within the intersect of the two regions
   */
  public List<double[]> getHybridIntersect(List<O> A, List<O> B, double epsilon) {
    List<double[]> intersect = new ArrayList<double[]>();
    for (O a : A) {
      for (O b : B) {
        if (distance(a, b) < epsilon) {
          // If a match was found add to intersect and stop comparing to this object
          intersect.add(getDescription(a));
          break;
        }
      }
    }
    return intersect;
  }
  
  /**
   * Returns the objects with the descriptions within the hybrid intersection of a list of regions.
   * @param regions the list of regions
   * @param epsilon the threshold distance between objects must be under to be an element
   * @return a List all descriptions within the intersect of the two regions
   */
  public List<O> getHybridIntersectObjects(List<List<O>> regions, double epsilon) {
    // calculate the objects in the intersect of the first two regions
    List<O> intersect = 
        new ArrayList<O>(getHybridIntersectObjects(regions.get(0), regions.get(1), epsilon));
    
    // calculate the intersect of the current intersect and the next region
    for (int i = 2; i < regions.size(); i++) {
      intersect = getHybridIntersectObjects(intersect, regions.get(i), epsilon);
    }
    
    return intersect;
  }
  
  /**
   * Returns the objects with the descriptions within the hybrid intersection of two regions.
   * @param A the first region
   * @param B the second region
   * @param epsilon the threshold distance between objects must be under to be an element
   * @return a List all descriptions within the intersect of the two regions
   */
  public List<O> getHybridIntersectObjects(List<O> A, List<O> B, double epsilon) {
    List<O> intersect = new ArrayList<O>();
    for (O a : A) {
      for (O b : B) {
        if (distance(a, b) < epsilon) {
          intersect.add(a);
          intersect.add(b);
        }
      }
    }
    return intersect;
  }
  
  /**
   * Checks whether two regions are near using their hybrid intersection.
   * @param A the first region
   * @param B the second region
   * @param epsilon the threshold distance between objects must be under to be an element
   * @return true if their hybrid intersect is non-empty
   */
  public boolean hybridNear(List<O> A, List<O> B, double epsilon) {
    return hybridDegree(A, B, epsilon) > 0;
  }
  
  /**
   * Returns the degree using the hybrid intersection.
   * @param A the first region
   * @param B the second region
   * @param epsilon the threshold distance between objects must be under to be an element
   * @return the degree of the hybrid intersection
   */
  public int hybridDegree(List<O> A, List<O> B, double epsilon) {
    return getHybridIntersect(A, B, epsilon).size();
  }
  
  /**
   * Gets the description of a perceptual object by applying every probe function to the object.
   * @param obj the perceptual object
   * @return a map mapping each probe function to its applied value
   */
  public double[] getDescription(O obj) {
    // retrieve the description
//    if (mCache) {
//      return mDescriptions.get(Arrays.);
//    }
//    else {
    return calcDescription(obj);
//    }
  }
  
  public double[] getDescription(int index) {
    // retrieve the description
//    if (mCache) {
//      return mDescriptions.get(index);
//    }
//    else {
    return calcDescription(mObjects[index]);
//    }
  }

  /**
   * Calculates the description of a perceptual object by applying every probe function to the object.
   * @param obj the perceptual object
   * @return a map mapping each probe function to its applied value
   */
  private double[] calcDescription(O obj) {
    double[] desc = new double[mProbeFuncCount];
    for (int i = 0; i < desc.length; i++) {
      desc[i] = mProbeFuncs.get(i).apply(obj);
    }
    return desc;
  }
  
  /**
   * Returns a List containing every perceptual object of the system.
   * @return every perceptual object of the system
   */
  public O[] getObjects() {    
    return Arrays.copyOf(mObjects, mObjects.length);
  }
  
//  public void addObject(O obj) {
//    // calculate description
//    if (mCache) {
//      mDescriptions.add(calcDescription(obj));
//    }
//    mObjects.add(obj);
//  }
  
  public void addObject(int index, O obj) {
    // calculate description
//    if (mCache) {
//      mDescriptions.add(index, calcDescription(obj));
//    }
    mObjects[index] = obj;
  }  
  
//  public void addObjects(List<O> objs) {
//    // calculate description
//    int size = objs.size();
//    for (int i = 0; i < size; i++) {
//      addObject(objs.get(i));
//    }
//  }
  
//  public boolean removeObject(O obj) {
//    // update cache
//    if (mCache) {
//      mDescriptions.remove(mObjects.indexOf(obj));
//    }
//    return mObjects.remove(obj);
//  }
  
  public O removeObject(int index) {
    // update cache
    if (mCache) {
      mDescriptions.remove(index);
    }
    return mObjects[index] = null;
  }
  
  public void clearObjects() {
    if (mCache) {
      mDescriptions.clear();
    }
    Arrays.fill(mObjects, null);
  }
  
  public void setObjects(O[] objs) {
    mObjects = Arrays.copyOf(objs, objs.length);
  }

  /**
   * Returns a List containing the probe functions of the system.
   * @return a List containing the probe functions of the system
   */
  public List<ProbeFunc<O>> getProbeFuncs() {
    return new ArrayList<ProbeFunc<O>>(mProbeFuncs);
  }

  /**
   * Adds a probe function.
   * @param func
   */
  public void addProbeFunc(ProbeFunc<O> func) {
    // update cached features
//    if (mCache) {
//      for (int i = 0; i < mDescriptions.size(); i++) {
//        mDescriptions.get(i).add(func.apply(mObjects[i]));
//      }
//    }
    mProbeFuncs.add(func);
    mProbeFuncCount++;
  }
  
  /**
   * Removes a probe function.
   * @param func
   * @return true if the probe function was removed
   */
  public boolean removeProbeFunc(ProbeFunc<O> func) {
    // remove cached feature
//    if (mCache) {
//      int index = mProbeFuncs.indexOf(func);
//      for (int i = 0; i < mDescriptions.size(); i++) {
//        mDescriptions.get(i).remove(index);
//      }
//    }
    mProbeFuncCount--;
    return mProbeFuncs.remove(func);
  }
}
