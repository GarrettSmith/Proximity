/**
 * 
 */
package ca.uwinnipeg.proximity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * A perceptual system consists of a set of perceptual objects and and a set of {@link ProbeFunc}.
 * @author Garrett Smith
 *
 */
// TODO: Test if this will be fast enough
// TODO: Implement unit tests
public class PerceptualSystem<O> {
  
  // The list of regions of interest
  protected List<Set<O>> mRegions = new ArrayList<Set<O>>();
  
  // The map of every object known to be contained in this system.
  // Objects do not need to be added to regions to be tested but it does allow their features
  // to be cached to speed up processing.
  // It uses a WeakHashMap so objects that are only referenced in the cache are garbage collected.
  private Map<O, Map<ProbeFunc<O>, Double>> mObjectsMap = 
      new WeakHashMap<O, Map<ProbeFunc<O>, Double>>();
  
  // The set of probe functions
  protected Set<ProbeFunc<O>> mProbeFuncs = new HashSet<ProbeFunc<O>>();
  
  // Turns caching on and off
  protected boolean mCache = true;
  
  /**
   * Creates an empty perceptual system.
   */
  public PerceptualSystem() {}
  
  /**
   * Creates a perceptual system described by the given probe functions.
   * @param funcs
   * @param cache whether the system should cache descriptions
   */
  public PerceptualSystem(ProbeFunc<O>[] funcs, boolean cache) {
    mCache = cache;
    for (ProbeFunc<O> f : funcs) {
      addProbeFunc(f);
    }
  }
  
  public boolean caching() {
    return mCache;
  }
  
  /**
   * Returns a description-based neighbourhood.
   * @param x the object to compare against
   * @param region the set of all objects being compared
   * @return the set of objects within the neighbourhood
   */
  // TODO: Used cached values
  public Set<O> getDescriptionBasedNeighbourhood(O x, Set<O> region) {
    Set<O> neighbourhood = new HashSet<O>();
    for (O y : region) {
      if (equal(x, y)) {
        neighbourhood.add(y);
      }
    }
    return neighbourhood;
  }
  
  /**
   * Determines if two perceptual objects are equal.
   * @param x
   * @param y
   * @return true if the two objects have the same features, false otherwise
   */
  public boolean equal(O x, O y) {    
    // Optimisation for comparing the same object
    if (x == y) return true;
    // compare the value of each probe function
    Map<ProbeFunc<O>, Double> descX = getDescription(x);
    Map<ProbeFunc<O>, Double> descY = getDescription(y);
    for (ProbeFunc<O> f : mProbeFuncs) {
      // These values need to be cast or they don't compare properly
      double xVal = descX.get(f);
      double yVal = descY.get(f);
      if (xVal != yVal) {
        return false;
      }
    }    
    // We've made it past every check so they are equal
    return true;
  }

  /**
   * Returns a hybrid neighbourhood.
   * @param x the object to compare against
   * @param region the set of all objects being compared
   * @param epsilon the threshold objects must be under to be an element
   * @return the set of objects within the neighbourhood
   */
  public Set<O> getHybridNeighbourhood(O x, Set<O> region, double epsilon) {
    Set<O> neighbourhood = new HashSet<O>();
    for (O y : region) {
      if (distance(x, y) < epsilon) {
        neighbourhood.add(y);
      }
    }
    return neighbourhood;
  }
  
  /**
   * Calculates the distance between the given objects in feature space.
   * @param x
   * @param y
   * @return
   */
  public double distance(O x, O y) {
    Map<ProbeFunc<O>, Double> descX = getDescription(x);
    Map<ProbeFunc<O>, Double> descY = getDescription(y);
    double sum = 0;
    for (ProbeFunc<O> f : mProbeFuncs) {
      sum += Math.pow((descX.get(f) - descY.get(f)), 2);
    }
    return Math.sqrt(sum);
  }
  
  /**
   * Returns the the description-based intersection of two regions.
   * @param A the first region
   * @param B the second region
   * @return a set all descriptions within the intersect of the two regions
   */
  public Set<Map<ProbeFunc<O>, Double>> getDescriptionBasedIntersect(Set<O> A, Set<O> B) {
    Set<Map<ProbeFunc<O>, Double>> intersect = new HashSet<Map<ProbeFunc<O>, Double>>();
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
   * Checks whether two regions are near using their description-based intersection.
   * @param A the first region
   * @param B the second region
   * @return true if their description-based intersect is non-empty
   */
  public boolean descriptionBasedNear(Set<O> A, Set<O> B) {
    return descriptionBasedDegree(A, B) > 0;
  }
  
  /**
   * Returns the degree using the description-based intersection.
   * @param A the first region
   * @param B the second region
   * @return the degree of the description-based intersection
   */
  public int descriptionBasedDegree(Set<O> A, Set<O> B) {
    return getDescriptionBasedIntersect(A, B).size();
  }
  
  /**
   * Returns the the hybrid intersection of two regions.
   * @param A the first region
   * @param B the second region
   * @param epsilon the threshold distance between objects must be under to be an element
   * @return a set all descriptions within the intersect of the two regions
   */
  public Set<Map<ProbeFunc<O>, Double>> getHybridIntersect(Set<O> A, Set<O> B, double epsilon) {
    Set<Map<ProbeFunc<O>, Double>> intersect = new HashSet<Map<ProbeFunc<O>, Double>>();
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
   * Checks whether two regions are near using their hybrid intersection.
   * @param A the first region
   * @param B the second region
   * @param epsilon the threshold distance between objects must be under to be an element
   * @return true if their hybrid intersect is non-empty
   */
  public boolean hybridNear(Set<O> A, Set<O> B, double epsilon) {
    return hybridDegree(A, B, epsilon) > 0;
  }
  
  /**
   * Returns the degree using the hybrid intersection.
   * @param A the first region
   * @param B the second region
   * @param epsilon the threshold distance between objects must be under to be an element
   * @return the degree of the hybrid intersection
   */
  public int hybridDegree(Set<O> A, Set<O> B, double epsilon) {
    return getHybridIntersect(A, B, epsilon).size();
  }

  /**
   * Gets the description map for every *known* perceptual object within the system.
   * An object must have been previously added to the system in a region to be cached and known.
   * @return a map of every object to its corresponding description map. 
   */
  public Map<O, Map<ProbeFunc<O>, Double>> getCachedDescriptions() {
    return new HashMap<O, Map<ProbeFunc<O>, Double>>(mObjectsMap);
  }
  
  /**
   * Gets the description of a perceptual object by applying every probe function to the object.
   * @param obj the perceptual object
   * @return a map mapping each probe function to its applied value
   */
  public Map<ProbeFunc<O>, Double> getDescription(O obj) {
    Map<ProbeFunc<O>, Double> rtn;
    // check if the object description has been cached and caching is enabled
    if (mCache && mObjectsMap.containsKey(obj)) {
      // retrieve the description
      rtn = mObjectsMap.get(obj);
    }
    else {
      // calculate and cache the description
      rtn = calcDescription(obj);
      if (mCache) mObjectsMap.put(obj, rtn);
    }
    return new HashMap<ProbeFunc<O>, Double>(rtn);
  }
  
  /**
   * Calculates the description of a perceptual object by applying every probe function to the object.
   * @param obj the perceptual object
   * @return a map mapping each probe function to its applied value
   */
  private Map<ProbeFunc<O>, Double> calcDescription(O obj) {
    Map<ProbeFunc<O>, Double> map = new HashMap<ProbeFunc<O>, Double>();
    for (ProbeFunc<O> f : mProbeFuncs) {
      map.put(f, f.apply(obj));
    }
    return map;
  }
  
  /**
   * Returns a set containing every perceptual object of the system.
   * @return
   */
  public Set<O> getObjects() {    
    Set<O> objs = new HashSet<O>();
    for (Set<O> region : mRegions) {
      objs.addAll(region);
    }
    return objs;
  }
  
  /**
   * Returns a list of all the regions of the system.
   * @return
   */
  public List<Set<O>> getRegions() {
    List<Set<O>> list = new ArrayList<Set<O>>();
    list.addAll(mRegions);
    return list;
  }
  
  /**
   * Creates a new region of interest using the given objects.
   * @param objs
   * @return the added region
   */
  public Set<O> addRegion(Set<O> objs) {
    Set<O> region = new HashSet<O>(objs);
    // cache object features
    if (mCache) {
      for (O o : region) {
        // if the object has not yet been cached
        if (!mObjectsMap.containsKey(o)) {
          mObjectsMap.put(o, calcDescription(o));
        }
      }
    }
    mRegions.add(region);
    return region;
  }
  
  /**
   * Removes the given region from the system.
   * @param region
   * @return true if the region was removed
   */
  public boolean removeRegion(Set<O> region) {
    return mRegions.remove(region);
  }
  
  /**
   * Returns a set containing the probe functions of the system.
   * @return
   */
  public Set<ProbeFunc<O>> getProbeFuncs() {
    return new HashSet<ProbeFunc<O>>(mProbeFuncs);
  }
  
  /**
   * Adds a probe function.
   * @param func
   */
  public void addProbeFunc(ProbeFunc<O> func) {
    // update cached features
    if (mCache) {
      for (O o : mObjectsMap.keySet()) {
        Map<ProbeFunc<O>, Double> desc = mObjectsMap.get(o);
        desc.put(func, func.apply(o));
      }
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
    if (mCache) {
      for (O o : mObjectsMap.keySet()) {
        Map<ProbeFunc<O>, Double> desc = mObjectsMap.get(o);
        desc.remove(o);
      }
    }
    return mProbeFuncs.remove(func);
  }
}
