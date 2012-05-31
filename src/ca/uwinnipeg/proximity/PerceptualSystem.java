/**
 * 
 */
package ca.uwinnipeg.proximity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A perceptual system consists of a set of perceptual objects and and a set of {@link ProbeFunc}.
 * @author Garrett Smith
 *
 */
// TODO: Test if this will be fast enough
public abstract class PerceptualSystem<O> {

  // The set of perceptual objects
  protected Set<O> mObjects;
  
  // The set of probe functions
  protected Set<ProbeFunc<O>> mProbeFuncs;
  
  /**
   * Creates an empty perceptual system.
   */
  public PerceptualSystem() {
    mObjects = new HashSet<O>();
    mProbeFuncs = new HashSet<ProbeFunc<O>>();
  }
  
  /**
   * Creates a perceptual system that contains the given perceptual objects and probe functions.
   * @param objs
   * @param funcs
   */
  public PerceptualSystem(O[] objs, ProbeFunc<O>[] funcs) {
    mObjects = new HashSet<O>(objs.length);
    add(objs);
    
    mProbeFuncs = new HashSet<ProbeFunc<O>>(funcs.length);
    add(funcs);
  }
  
  /**
   * Gets the description of every perceptual object within the system.
   * @return a map of every object to its corresponding description map. 
   */
  public Map<O, Map<ProbeFunc<O>, Double>> getDescriptions() {
    HashMap<O, Map<ProbeFunc<O>, Double>> map = new HashMap<O, Map<ProbeFunc<O>, Double>>();
    for (O o : mObjects) {
      map.put(o, getDescription(o));
    }
    return map;
  }
  
  /**
   * Gets the description of a perceptual object by applying every probe function to the object.
   * @param obj the perceptual object
   * @return a map mapping each probe function to its applied value
   */
  public Map<ProbeFunc<O>, Double> getDescription(O obj) {
    HashMap<ProbeFunc<O>, Double> map = new HashMap<ProbeFunc<O>, Double>();
    for (ProbeFunc<O> f : mProbeFuncs) {
      map.put(f, f.apply(obj));
    }
    return map;
  }
  
  /**
   * Returns a set containing the perceptual object of the system.
   * @return
   */
  public Set<O> getObjects() {    
    return new HashSet<O>(mObjects);
  }
  
  /**
   * Returns a set containing the probe functions of the system.
   * @return
   */
  public Set<ProbeFunc<O>> getProbeFuncs() {
    return new HashSet<ProbeFunc<O>>(mProbeFuncs);
  }
  
  /**
   * Adds a perceptual object.
   * @param obj
   */
  public void add(O obj) {
    mObjects.add(obj);
  }
  
  /**
   * Adds an array of perceptual objects.
   * @param objs
   */
  public void add(O[] objs) {
    for (O obj : objs) {
      mObjects.add(obj);
    }
  }
  
  /**
   * Removes a perceptual object.
   * @param obj
   */
  public void remove(O obj) {
    mObjects.remove(obj);
  }
  
  /**
   * Adds a probe function.
   * @param func
   */
  public void add(ProbeFunc<O> func) {
    mProbeFuncs.add(func);
  }
  
  /**
   * Adds an array of probe functions.
   * @param funcs
   */
  public void add(ProbeFunc<O>[] funcs) {
    for (ProbeFunc<O> f : funcs) {
      mProbeFuncs.add(f);
    }
  }
  
  /**
   * Remove a probe function.
   * @param func
   */
  public void remove(ProbeFunc<O> func) {
    mProbeFuncs.remove(func);
  }
}
