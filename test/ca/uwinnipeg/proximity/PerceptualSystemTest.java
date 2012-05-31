package ca.uwinnipeg.proximity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

public class PerceptualSystemTest {
  
  private PerceptualSystem<String> sys;
  
  private ProbeFunc<String> lengthFunc = new ProbeFunc<String>() {

    @Override
    public double apply(String str) {      
      return str.length();
    }
    
  };
  
  private ProbeFunc<String> emptyFunc = new ProbeFunc<String>() {

    @Override
    public double apply(String str) {
      if (str.isEmpty()) {
        return 0;
      }
      else {
        return 1;
      }
    }
    
  };
  
  @SuppressWarnings("unchecked")
  private ProbeFunc<String>[] funcs = (ProbeFunc<String>[]) new ProbeFunc[] {lengthFunc, emptyFunc};

  Set<ProbeFunc<String>> funcSet = new HashSet<ProbeFunc<String>>();
  
  private String str1 = "Test String one";
  private String str2 = "Test String two";
  private String str3 = "Test String three";  

  Set<String> objectSet = new HashSet<String>();
  
  private String str4 = "Test String four";
  private String str5 = "Test String five";  

  Set<String> objectSet2 = new HashSet<String>();
  
  @Before
  public void setUp() {
    sys = new PerceptualSystem<String>(funcs, true);
    objectSet.add(str1);
    objectSet.add(str2);
    objectSet.add(str3);
    sys.addRegion(objectSet);
    objectSet2.add(str4);
    objectSet2.add(str5);
    sys.addRegion(objectSet2);
    
    funcSet.add(emptyFunc);
    funcSet.add(lengthFunc);
  }
  
  @Test
  public void perceptualSystemProbeFullConstructor() { 
    sys = new PerceptualSystem<String>(funcs, true);
    assertTrue(sys.mProbeFuncs.contains(emptyFunc) && sys.mProbeFuncs.contains(lengthFunc));
  }

  @Test
  public void getDescriptionBasedNeighbourhoodEqual() {
    Set<String> nb = new HashSet<String>();
    nb.add(str1);
    nb.add(str2);
    Set<String> result = sys.getDescriptionBasedNeighbourhood(str1, objectSet);
    assertTrue(result.equals(nb));
  }

  @Test
  public void getDescriptionBasedNeighbourhoodUnequal() {
    assertTrue(!sys.getDescriptionBasedNeighbourhood(str1, objectSet).contains(str3));
  }

  @Test
  public void equalSame() {
    // They are the same
    assertTrue(sys.equal(str1, str1));
  }

  @Test
  public void equalDifferent() {
    // They are the same length and both non-empty
    assertTrue(sys.equal(str1, str2));
  }

  @Test
  public void notEqual() {
    // They are different length
    assertFalse(sys.equal(str1, str3));
  }

  @Test
  public void GetHybridNeighbourhood() {
    fail("Not yet implemented"); // TODO
  }

  @Test
  public void equalsDistance() {
    // They are equal so dist should == 0
    assertEquals(0.0, sys.distance(str1, str2), Double.MIN_VALUE);
  }

  @Test
  public void notEqualsDistance() {
    // They are not equal so dist should != 0
    assertTrue(sys.distance(str1, str3) >= Double.MIN_NORMAL);
  }
  
  @Test
  public void euclideanDistance() {    
    // Should equal euclidean distance
    double x = lengthFunc.apply(str1) - lengthFunc.apply(str3);
    double y = emptyFunc.apply(str1) - emptyFunc.apply(str3);
    Double dist = Math.sqrt(x * x + y * y);
    assertEquals(dist, sys.distance(str1, str3), Double.MIN_VALUE);
  }
  
  @Test
  public void positiveDistance() {
    // distance should be >= 0
    assertTrue(sys.distance(str2, str3) >= 0);
  }

  @Test
  public void getDescriptionBasedIntersectEmpty() {
    assertTrue(sys.getDescriptionBasedIntersect(objectSet, objectSet2).isEmpty());
  }

  @Test
  public void getDescriptionBasedIntersectNonempty() {
    Set<String> s1 = new HashSet<String>();
    s1.add(str1);
    s1.add(str3);
    s1.add(str5);
    
    Set<String> s2 = new HashSet<String>();
    s2.add(str2);
    s2.add(str4);
    
    Set<Map<ProbeFunc<String>, Double>> intersect = new HashSet<Map<ProbeFunc<String>,Double>>();
    intersect.add(sys.getDescription(str1));
    intersect.add(sys.getDescription(str4));
    
    Set<Map<ProbeFunc<String>, Double>> result = sys.getDescriptionBasedIntersect(s1, s2);
    
    assertTrue(result.equals(intersect));
  }

  @Test
  public void GetHybridIntersect() {
    fail("Not yet implemented"); // TODO
  }

  @Test
  public void getDescription() {
    assertTrue(sys.getDescription(str1).get(lengthFunc) == lengthFunc.apply(str1));
  }
  
  @Test
  public void getDescriptionKeys() {
    assertTrue(sys.getDescription(str1).keySet().equals(funcSet));
  }

  @Test
  public void getObjects() {
    Set<String> all = new HashSet<String>(objectSet);
    all.addAll(objectSet2);
    assertTrue(sys.getObjects().equals(all));
  }

  @Test
  public void getRegions() {
    List<Set<String>> all = new ArrayList<Set<String>>();
    all.add(objectSet2);
    all.add(objectSet);
    List<Set<String>> result = sys.getRegions();
    assertTrue(result.equals(all));
  }

  @Test
  public void addRegion() {
    sys = new PerceptualSystem<String>();
    sys.addRegion(objectSet);
    assertTrue(sys.mRegions.get(0).equals(objectSet));
  }

  @Test
  public void removeRegion() {
    sys.removeRegion(objectSet2);
    assertTrue(sys.mRegions.size() == 1 && !sys.mRegions.get(0).equals(objectSet2));
  }

  @Test
  public void getProbeFuncs() {
    assertTrue(sys.getProbeFuncs().equals(funcSet));
  }

  @Test
  public void getProbeFuncsEmpty() {
    sys = new PerceptualSystem<String>();
    assertTrue(sys.getProbeFuncs().isEmpty());
  }

  @Test
  public void addProbeFunc() {
    sys = new PerceptualSystem<String>();
    sys.addProbeFunc(emptyFunc);
    assertTrue(sys.mProbeFuncs.size() == 1 && sys.mProbeFuncs.contains(emptyFunc));
  }

  @Test
  public void removeProbeFunc() {
    sys.removeProbeFunc(emptyFunc);
    assertTrue(sys.mProbeFuncs.size() == 1 && !sys.mProbeFuncs.contains(emptyFunc));
  }

}
