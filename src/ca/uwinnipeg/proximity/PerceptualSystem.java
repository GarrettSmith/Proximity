/**
 * 
 */
package ca.uwinnipeg.proximity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A perceptual system consists of a List of perceptual objects and and a List of {@link ProbeFunc}.
 * @author Garrett Smith
 *
 */
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
    public void onProgressSet(float progress);
    
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
  
//  /**
//   * Returns a description-based neighbourhood.
//   * @param x the object to compare against
//   * @param objs the List of all objects being compared
//   * @return the List of objects within the neighbourhood
//   */
//  public List<O> neighbourhood(O x, List<O> region) {
//    Description descX = getDescription(x);
//    return mapObjectsList(region).get(descX);
//  }
  
  /**
   * Returns a description-based neighbourhood.
   * @param x the index of the object to compare to, the pivot object
   * @param indices the list of indices of objects within the system to be considered
   * @param sub the subscriber interested in the progress of execution
   * @return the list of indices of objects within the neighbourhood
   */
  public List<Integer> neighbourhood(int x, List<Integer> indices, PerceptualSystemSubscriber sub) {
    Description descX = getDescription(x);
    Map<Description, List<Integer>> map = mapIndicesList(indices);
    return map.get(descX);
  }
  
  /**
   * Returns a hybrid description-based neighbourhood calculated using an epsilon.
   * @param x the index of the object to compare to, the pivot object
   * @param indices the list of indices of objects within the system to be considered
   * @param epsilon the epsilon used to calculate the property
   * @param sub the subscriber interested in the progress of execution
   * @return the list of indices of objects within the neighbourhood
   */
  public List<Integer> hybridNeighbourhood(int x, List<Integer> indices, double epsilon, 
      PerceptualSystemSubscriber sub) {
    
    // if epsilon is 0 use the faster non epsilon method
    if (epsilon == 0) return neighbourhood(x, indices, sub);
    
    // check if we should stop
    if (sub.isCancelled()) return null;
    sub.onProgressSet(0);
    
    double e2 = epsilon * epsilon;
    
    Description descX = getDescription(x);
    List<Integer> neighbourhood = new ArrayList<Integer>();
    for (int i = 0; i < indices.size(); i++) {
      
      // check if we should stop
      if (sub.isCancelled()) return null;
      
      Description descY = getDescription(indices.get(i));
      
      // if the objects are within an epsilon of each other in feature space
      if (descX.squaredDistance(descY) < e2) {
        neighbourhood.add(indices.get(i));
      }
      
      //set progress
      sub.onProgressSet((float)i / indices.size());
    }
    return neighbourhood;
  }

  /**
   * Returns the intersection of two lists of indices corresponding to perceptual objects in the 
   * perceptual system.
   * @param A the first list of perceptual objects
   * @param B the second list of perceptual objects
   * @param sub the subscriber interested in the progress of execution
   * @return a list containing the indices of objects within the intersection
   */
  public List<Integer> intersection(List<Integer> A, List<Integer> B, 
      PerceptualSystemSubscriber sub) {
    
    if (sub.isCancelled()) return null;
    Map<Description, List<Integer>> descsA = mapIndicesList(A);
    sub.onProgressSet(0.3f);
    
    if (sub.isCancelled()) return null;
    Map<Description, List<Integer>> descsB = mapIndicesList(B);
    sub.onProgressSet(0.6f);
    
    int i = 0;
    float size = descsA.size();
    List<Integer> rtn = new ArrayList<Integer>();
    for (Description descA : descsA.keySet()) {
      if (sub.isCancelled()) return null;
      i++;
      List<Integer> indicesB = descsB.get(descA);
      if (indicesB != null) {
        // put all the items in a set to trim duplicates
        Set<Integer> set = new HashSet<Integer>();
        set.addAll(descsA.get(descA));
        set.addAll(indicesB);
        rtn.addAll(set);
      }
      sub.onProgressSet(0.6f + (0.4f * (i/size)));
    }
    return rtn;
  }
  
  /**
   * Returns the intersection of two lists of indices corresponding to perceptual objects in the 
   * perceptual system using an epsilon.
   * @param A the first list of perceptual objects
   * @param B the second list of perceptual objects
   * @param epsilon the epsilon used to calculate the property
   * @param sub the subscriber interested in the progress of execution
   * @return a list containing the indices of objects within the intersection
   */
  // TODO: try sorting descriptions
  public List<Integer> hybridIntersection(List<Integer> A, List<Integer> B, double epsilon,
      PerceptualSystemSubscriber sub) {
    
    // check if we really want a description based intersect, ie. epsilon = 0, this is much faster
    if (epsilon == 0) return intersection(A, B, sub);

    if (sub.isCancelled()) return null;
    Map<Description, List<Integer>> descsMapA = mapIndicesList(A);
    
    if (sub.isCancelled()) return null;
    Map<Description, List<Integer>> descsMapB = mapIndicesList(B);
    
    Description[] descsA = new Description[descsMapA.size()];
    descsMapA.keySet().toArray(descsA);
    Description[] descsB = new Description[descsMapB.size()];
    descsMapB.keySet().toArray(descsB);
    
    double e2 = epsilon * epsilon;
    
    boolean[] matchesA = new boolean[descsA.length];
    Arrays.fill(matchesA, false);
    boolean[] matchesB = new boolean[descsB.length];
    Arrays.fill(matchesB, false);
    
    // FIXME: somewhere in here is the error
    for (int i = 0; i < descsA.length; i++) {
      
      if (sub.isCancelled()) return null;
      
      Description descA = descsA[i];
      boolean matched = false;
      
      for (int j = 0; j < descsB.length; j++) {
        
        // if either has not been matched
        if (!matched || !matchesB[j]) {
          
          // check the distance between the two
          Description descB = descsB[j];
          if (descA.squaredDistance(descB) <= e2) {
            matchesB[j] = true;
            matched = true;
          }
        }
      }
      if (matched) matchesA[i] = true;
      sub.onProgressSet(i / (float)descsA.length);
    }
    
    // combine results
    Set<Integer> rtn = new HashSet<Integer>();
    getIndices(matchesA, descsA, descsMapA, rtn);
    getIndices(matchesB, descsB, descsMapB, rtn);
    return new ArrayList<Integer>(rtn);
  }
  
  /**
   * Move the object indices of all objects whose descriptions map to a true value in the matches 
   * map into the destination set.
   * @param matches
   * @param descs
   * @param descMap
   * @param dest
   */
  private void getIndices(
      boolean[] matches, 
      Description[] descs,
      Map<Description, List<Integer>> descMap,
      Set<Integer> dest) {
    // for each description
    for (int i = 0; i < descs.length; i++) {
      // if it was matched
      if (matches[i]) {
        dest.addAll(descMap.get(descs[i]));
      }
    }
  }

  /**
   * Returns the difference of the second region from the first region.
   * @param A the list of indices corresponding to perceptual objects to take the difference from
   * @param B the list of indices corresponding to perceptual objects used to take the difference
   * @param sub the subscriber interested in the progress of execution
   * @return a list containing the indices of objects left after taking the difference
   */
  public List<Integer> difference(
      List<Integer> A, 
      List<Integer> B, 
      PerceptualSystemSubscriber sub) {
    List<Description> regionDescs = getIndicesDescriptions(B);
    sub.onProgressSet(0.25f);
    
    if (sub.isCancelled()) return null;    
    Map<Description, List<Integer>> compliment = mapIndicesList(A); 
    sub.onProgressSet(0.5f);
    
    int size = regionDescs.size();
    
    // remove all descriptions that are in the region
    for (int i = 0; i < size; i++) {
      if (sub.isCancelled()) return null;
      
      Description d = regionDescs.get(i);
      compliment.remove(d);
      
      sub.onProgressSet(0.5f + (0.5f * i/size));
    }
    
    // get all the remaining objects
    List<Integer> rtn = new ArrayList<Integer>();
    for (List<Integer> l : compliment.values()) {
      rtn.addAll(l);
    }
    return rtn;
  }

  /**
   * Returns the difference of the second region from the first region using an epsilon.
   * @param A the list of indices corresponding to perceptual objects to take the difference from
   * @param B the list of indices corresponding to perceptual objects used to take the difference
   * @param epsilon the epsilon used to calculate the property
   * @param sub the subscriber interested in the progress of execution
   * @return a list containing the indices of objects left after taking the difference
   */
  public List<Integer> hybridDifference(
      List<Integer> A, 
      List<Integer> B, 
      double epsilon,
      PerceptualSystemSubscriber sub) {
    
    // check if we really want a description based intersect, ie. epsilon = 0, this is much faster
    if (epsilon == 0) return difference(A, B, sub);
    
    Map<Description, List<Integer>> descsMapA = mapIndicesList(A);

    Description[] descsA = new Description[descsMapA.size()];
    descsMapA.keySet().toArray(descsA);
    
    List<Description> descsB = getIndicesDescriptions(B);

    double e2 = epsilon * epsilon;
    
    int sizeB = descsB.size();
    
    for (int i = 0; i < sizeB; i++) {

      // check if we were cancelled and should return
      if (sub.isCancelled()) return null;      
      
      Description descB = descsB.get(i);
      
      for (int j = 0; j < descsA.length; j++) {
        Description descA = descsA[j];
        if (descA != null && descA.squaredDistance(descB) < e2) {
          descsMapA.remove(descA);
          descsA[j] = null;
        }
      }
      
      // update the progress
      sub.onProgressSet(i / (float)sizeB);
    }

    // get all the remaining objects
    List<Integer> rtn = new ArrayList<Integer>();
    for (List<Integer> l : descsMapA.values()) {
      rtn.addAll(l);
    }
    return rtn;
  }
  
  /**
   * Returns the descriptive compliment of the given region within the perceptual system.
   * @param region the list of indices corresponding to objects to use to find the compliment
   * @param sub the subscriber interested in the progress of execution
   * @return a list containing the indices of objects within the compliment
   */
  public List<Integer> compliment(
      List<Integer> region, 
      PerceptualSystemSubscriber sub) {
    return difference(objectsIndicesList(), region, sub);
  }
  
  /**
   * Returns the hybrid descriptive compliment of the given region within the perceptual system.
   * @param region the list of indices corresponding to objects to use to find the compliment
   * @param epsilon the epsilon used to calculate the property
   * @param sub the subscriber interested in the progress of execution
   * @return a list containing the indices of objects within the compliment
   */
  public List<Integer> hybridCompliment(
      List<Integer> region, 
      double epsilon,
      PerceptualSystemSubscriber sub) {
    return hybridDifference(objectsIndicesList(), region, epsilon, sub);
  }
  
  /**
   * Returns a list containing the indices of all objects within the perceptual system.
   * @return
   */
  public List<Integer> objectsIndicesList() {
    List<Integer> indices = new ArrayList<Integer>(mObjects.length);
    for (int i = 0; i < mObjects.length; i++) {
      indices.add(i);
    }
    return indices;
  }
  
  /**
   * Gets the description of a perceptual object by applying every probe function to the object.
   * @param obj the perceptual object
   * @return the description of the object
   */
  public Description getDescription(O obj) {
    // retrieve the description
//    if (mCache) {
//      return mDescriptions.get(Arrays.);
//    }
//    else {
    return calcDescription(obj);
//    }
  }
  
  /**
   * Gets the description of a perceptual object at the given index within the perceptual system.
   * @param index the index corresponding to the perceptual object
   * @return the description of the object
   */
  public Description getDescription(int index) {
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
  private Description calcDescription(O obj) {
    double[] desc = new double[mProbeFuncCount];
    for (int i = 0; i < desc.length; i++) {
      desc[i] = mProbeFuncs.get(i).apply(obj);
    }
    return new Description(desc);
  }
  
  private Map<Description, List<Integer>> mapIndicesList(List<Integer> l) {
    
    Map<Description, List<Integer>> map = new HashMap<Description, List<Integer>>(l.size());
    
    for (Integer i : l) {
      Description desc = getDescription(i);
      List<Integer> list = map.get(desc); // get the corresponding list
      if (list == null) {
        list = new ArrayList<Integer>(); // create the list if this is the first one
        list.add(i);
        map.put(desc, list);
      }
      else {
        list.add(i);
      }
    }
    
    return map;
  }
  
  private Map<Description, List<O>> mapObjectsList(List<O> l) {
    Map<Description, List<O>> map = new HashMap<Description, List<O>>(l.size());

    for (O o : l) {
      Description desc = getDescription(o);
      List<O> list = map.get(desc); // get the corresponding list
      if (list == null) {
        list = new ArrayList<O>(); // create the list if this is the first one
        map.put(desc, list);
      }
      list.add(o);
    }

    return map;
  }
  
  private List<Description> getIndicesDescriptions(List<Integer> indices) {
    Set<Description> descs = new HashSet<Description>();
    for (Integer i : indices) {
      Description desc = getDescription(i);
      descs.add(desc);
    }
    return new ArrayList<Description>(descs);
  }

  /**
   * Returns a List containing every perceptual object of the system.
   * @return every perceptual object of the system
   */
  public O[] getObjects() {    
    return Arrays.copyOf(mObjects, mObjects.length);
  }
  
  /**
   * Returns the object at the given index
   * @param index
   * @return
   */
  public O getObject(int index) {
    return mObjects[index];
  }
  
  /**
   * Sets the perceptual object at the given index.
   * @param index the index to set
   * @param obj the perceptual object being added
   */
  public void addObject(int index, O obj) {
    // calculate description
//    if (mCache) {
//      mDescriptions.add(index, calcDescription(obj));
//    }
    mObjects[index] = obj;
  }
  
  /**
   * Removes the perceptual object at the given index.
   * @param index the index to remove
   * @return the removed object
   */
  public O removeObject(int index) {
    // update cache
    if (mCache) {
      mDescriptions.remove(index);
    }
    return mObjects[index] = null;
  }
  
  /**
   * Clears all perceptual objects from the system.
   */
  public void clearObjects() {
    if (mCache) {
      mDescriptions.clear();
    }
    Arrays.fill(mObjects, null);
  }
  
  /**
   * Sets the perceptual objects to the given array.
   * @param objs the array of perceptual objects to use
   */
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
    boolean removed = mProbeFuncs.remove(func);
    if (removed) mProbeFuncCount--;
    return removed;
  }
  
  /**
   * Returns the norm of the perceptual system.
   * @return the largest possible distance between two objects.
   */
  public double getNorm() {
    return Math.sqrt(mProbeFuncCount);
  }
}
