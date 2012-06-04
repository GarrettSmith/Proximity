package ca.uwinnipeg.proximity.image;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ColorFuncTest {

  private final int BLACK  = 0xFF000000;
  private final int WHITE  = 0xFFFFFFFF;
  private final int RED    = 0xFFFF0000;
  private final int GREEN  = 0xFF00FF00;
  private final int BLUE   = 0xFF0000FF;
  private final int TRANS  = 0x00000000;

  private AlphaFunc alphaFunc = new AlphaFunc();
  private RedFunc   redFunc   = new RedFunc();
  private GreenFunc greenFunc = new GreenFunc();
  private BlueFunc  blueFunc  = new BlueFunc();

  @Test
  public void alpha() {
    assertEquals(alphaFunc.apply(BLACK), 1.0, Double.MIN_VALUE);
    assertEquals(alphaFunc.apply(TRANS), 0.0, Double.MIN_VALUE);
  }
  
  @Test
  public void red() {
    assertEquals(redFunc.apply(BLACK), 0.0, Double.MIN_VALUE);
    assertEquals(redFunc.apply(WHITE), 1.0, Double.MIN_VALUE);
    assertEquals(redFunc.apply(RED),   1.0, Double.MIN_VALUE);
    assertEquals(redFunc.apply(GREEN), 0.0, Double.MIN_VALUE);
    assertEquals(redFunc.apply(BLUE),  0.0, Double.MIN_VALUE);
  }
  
  @Test
  public void green() {
    assertEquals(greenFunc.apply(BLACK), 0.0, Double.MIN_VALUE);
    assertEquals(greenFunc.apply(WHITE), 1.0, Double.MIN_VALUE);
    assertEquals(greenFunc.apply(RED),   0.0, Double.MIN_VALUE);
    assertEquals(greenFunc.apply(GREEN), 1.0, Double.MIN_VALUE);
    assertEquals(greenFunc.apply(BLUE),  0.0, Double.MIN_VALUE);
  }
  
  @Test
  public void blue() {
    assertEquals(blueFunc.apply(BLACK), 0.0, Double.MIN_VALUE);
    assertEquals(blueFunc.apply(WHITE), 1.0, Double.MIN_VALUE);
    assertEquals(blueFunc.apply(RED),   0.0, Double.MIN_VALUE);
    assertEquals(blueFunc.apply(GREEN), 0.0, Double.MIN_VALUE);
    assertEquals(blueFunc.apply(BLUE),  1.0, Double.MIN_VALUE);
  }
}
