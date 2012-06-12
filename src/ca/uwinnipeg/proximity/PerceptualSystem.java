/**
 * 
 */
package ca.uwinnipeg.proximity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A perceptual system consists of a List of perceptual objects and and a List of {@link ProbeFunc}.
 * @author Garrett Smith
 *
 */
public class PerceptualSystem<O> {
  
  // The perceptual objects within the system
  protected List<O> mObjects = new ArrayList<O>();
  
  // The description of every perceptual object in the system.
  // To get a description use mDescriptions[Object index][ProbeFunc index].
  private List<List<Double>> mDescriptions = new ArrayList<List<Double>>();
  
  // The list of probe functions
  protected List<ProbeFunc<O>> mProbeFuncs = new ArrayList<ProbeFunc<O>>();
  
  /**
   * Creates an empty perceptual system.
   * @param size
   */
  public PerceptualSystem() {}
  
  /**
   * Creates an empty perceptual system with the given number of objects.
   * @param size
   */
  public PerceptualSystem(int size) {
    mObjects = new ArrayList<O>(size);
    mDescriptions = new ArrayList<List<Double>>(size);
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
  public boolean equal(List<Double> x, List<Double> y) {
    // compare the value of each probe function
    int size = mProbeFuncs.size();
    for (int i = 0; i < size; i++) {
      // These values need to be cast or they don't compare properly
      double xVal = x.get(i);
      double yVal = y.get(i);
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
    List<Double> descX = getDescription(x);
    List<Double> descY = getDescription(y);
    return distance(descX, descY);
  }

  /**
   * Calculates the distance between the given descriptions in feature space.
   * @param x
   * @param y
   * @return the distance between the given descriptions in feature space
   */
  public double distance(List<Double> x, List<Double> y) {
    double sum = 0;
    int size = mProbeFuncs.size();
    for (int i = 0; i < size; i++) {
      double tmp = x.get(i) - y.get(i);
      sum += tmp * tmp;
    }
    return Math.sqrt(sum);
  }
  
  /**
   * Returns a description-based neighbourhood.
   * @param x the object to compare against
   * @param objs the List of all objects being compared
   * @return the List of objects within the neighbourhood
   */
  public List<O> getDescriptionBasedNeighbourhood(O x, List<O> region) {
    List<Double> descX = getDescription(x);
    List<O> neighbourhood = new ArrayList<O>();
    int size = region.size();
    for (int i = 0; i < size; i++) {
      O y = region.get(i);
      List<Double> descY = getDescription(y);
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
    List<Double> descX = getDescription(x);
    List<O> neighbourhood = new ArrayList<O>();    
    int size = region.size();
    for (int i = 0; i < size; i++) {
      O y = region.get(i);
      List<Double> descY = getDescription(y);
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
    List<Double> descX = getDescription(x);
    List<O> neighbourhood = new ArrayList<O>();
    for (int i = 0; i < indices.length; i++) {
      List<Double> descY = getDescription(indices[i]);
      if (distance(descX, descY) < epsilon) {
        neighbourhood.add(mObjects.get(indices[i]));
      }
    }
    return neighbourhood;
  }
  
  public List<Integer> getHybridNeighbourhoodIndices(int x, int[] indices, double epsilon) {
    List<Double> descX = getDescription(x);
    List<Integer> neighbourhood = new ArrayList<Integer>();
    for (int i = 0; i < indices.length; i++) {
      List<Double> descY = getDescription(indices[i]);
      if (distance(descX, descY) < epsilon) {
        neighbourhood.add(indices[i]);
      }
    }
    return neighbourhood;
  }

  public List<List<Double>> getDescriptionUnion(List<O> A, List<O> B) {
    // get union of objects
    List<O> objectUnion = new ArrayList<O>(A);
    objectUnion.addAll(B);
    
    // get the description of each object
    List<List<Double>> union = new ArrayList<List<Double>>();
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
  public List<List<Double>> getDescriptionBasedIntersect(List<List<O>> regions) {
    // calculate the objects in the intersect of the first two regions
    List<List<Double>> intersect = 
        new ArrayList<List<Double>>(
            getDescriptionBasedIntersect(regions.get(0), regions.get(1)));

    // calculate the intersect of the current intersect and the next region
    for (int i = 2; i < regions.size(); i++) {
      List<O> region = regions.get(i);
      List<List<Double>> newIntersect = new ArrayList<List<Double>>();

      for (O a : region) {
        List<Double> descA = getDescription(a);
        for (List<Double> descB : intersect) {
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
  public List<List<Double>> getDescriptionBasedIntersect(List<O> A, List<O> B) {
    List<List<Double>> intersect = new ArrayList<List<Double>>();
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
    List<List<Double>> descsB = new ArrayList<List<Double>>(sizeB);
    for (int i = 0; i < sizeB; i++) {
      descsB.add(i, getDescription(B.get(i)));
    }
    
    for (int i = 0; i < sizeA; i++) {
      O a = A.get(i);
      List<Double> descA = getDescription(a);
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
  public List<O> getDescriptionBasedIntersectIndices(int[] A, int[] B) {
    List<O> intersect = new ArrayList<O>(A.length + B.length);
    
    // get all the descriptions of objects in B only once
    List<List<Double>> descsB = new ArrayList<List<Double>>(B.length);
    for (int i = 0; i < B.length; i++) {
      descsB.add(i, getDescription(B[i]));
    }
    
    for (int i = 0; i < A.length; i++) {
      List<Double> descA = getDescription(A[i]);
      for (int j = 0; j < B.length; j++) {
        boolean added = false;
        if (equal(descA, descsB.get(j))) {
          if (!added) intersect.add(mObjects.get(A[i]));
          intersect.add(mObjects.get(B[j]));
          added = true;
        }
      }
    }
    return intersect;
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
  public List<List<Double>> getHybridIntersect(List<List<O>> regions, double epsilon) {
    // calculate the objects in the intersect of the first two regions
    List<List<Double>> intersect = 
        new ArrayList<List<Double>>(
            getDescriptionBasedIntersect(regions.get(0), regions.get(1)));

    // calculate the intersect of the current intersect and the next region
    for (int i = 2; i < regions.size(); i++) {
      List<O> region = regions.get(i);
      List<List<Double>> newIntersect = new ArrayList<List<Double>>();

      for (O a : region) {
        List<Double> descA = getDescription(a);
        for (List<Double> descB : intersect) {
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
  public List<List<Double>> getHybridIntersect(List<O> A, List<O> B, double epsilon) {
    List<List<Double>> intersect = new ArrayList<List<Double>>();
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
  public List<Double> getDescription(O obj) {
    // retrieve the description
    return mDescriptions.get(mObjects.indexOf(obj));
  }
  
  public List<Double> getDescription(int index) {
    // retrieve the description
    return mDescriptions.get(index);
  }

  /**
   * Calculates the description of a perceptual object by applying every probe function to the object.
   * @param obj the perceptual object
   * @return a map mapping each probe function to its applied value
   */
  private List<Double> calcDescription(O obj) {
    int funcSize = mProbeFuncs.size();
    List<Double> desc = new ArrayList<Double>(funcSize);
    for (int i = 0; i < funcSize; i++) {
      desc.add(i, mProbeFuncs.get(i).apply(obj));
    }
    return desc;
  }
  
  /**
   * Returns a List containing every perceptual object of the system.
   * @return every perceptual object of the system
   */
  public List<O> getObjects() {    
    return new ArrayList<O>(mObjects);
  }
  
  public void addObject(O obj) {
    // calculate description
    mDescriptions.add(calcDescription(obj));
    mObjects.add(obj);
  }
  
  public void addObject(int index, O obj) {
    // calculate description
    mDescriptions.add(index, calcDescription(obj));
    mObjects.add(index, obj);
  }  
  
  public void addObjects(List<O> objs) {
    // calculate description
    int size = objs.size();
    for (int i = 0; i < size; i++) {
      addObject(objs.get(i));
    }
  }
  
  public boolean removeObject(O obj) {
    // update cache
    mDescriptions.remove(mObjects.indexOf(obj));
    return mObjects.remove(obj);
  }
  
  public O removeObject(int index) {
    // update cache
    mDescriptions.remove(index);
    return mObjects.remove(index);
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
    for (int i = 0; i < mDescriptions.size(); i++) {
      mDescriptions.get(i).add(func.apply(mObjects.get(i)));
    }
    mProbeFuncs.add(func);
  }
  
  /**
   * Removes a probe function.
   * @param func
   * @return true if the probe function was removed
   */
  public boolean removeProbeFunc(ProbeFunc<O> func) {
    // remove cached feature
    int index = mProbeFuncs.indexOf(func);
    for (int i = 0; i < mDescriptions.size(); i++) {
      mDescriptions.get(i).remove(index);
    }
    return mProbeFuncs.remove(func);
  }
}
