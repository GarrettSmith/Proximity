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
  
  private final Image IMAGE = new Image(new int[][]{ { BLACK, WHITE },
                                                     { RED, GREEN },
                                                     { BLUE, TRANS }});
  
  private final Pixel BLACK_PIXEL = IMAGE.getPixel(0, 0);
  private final Pixel WHITE_PIXEL = IMAGE.getPixel(0, 1);
  private final Pixel RED_PIXEL   = IMAGE.getPixel(1, 0);
  private final Pixel GREEN_PIXEL = IMAGE.getPixel(1, 1);
  private final Pixel BLUE_PIXEL  = IMAGE.getPixel(2, 0);
  private final Pixel TRANS_PIXEL = IMAGE.getPixel(2, 1);

  private AlphaFunc alphaFunc = new AlphaFunc();
  private RedFunc   redFunc   = new RedFunc();
  private GreenFunc greenFunc = new GreenFunc();
  private BlueFunc  blueFunc  = new BlueFunc();

  @Test
  public void alpha() {
    assertEquals(alphaFunc.apply(BLACK_PIXEL), 1.0, Double.MIN_VALUE);
    assertEquals(alphaFunc.apply(TRANS_PIXEL), 0.0, Double.MIN_VALUE);
  }
  
  @Test
  public void red() {
    assertEquals(redFunc.apply(BLACK_PIXEL), 0.0, Double.MIN_VALUE);
    assertEquals(redFunc.apply(WHITE_PIXEL), 1.0, Double.MIN_VALUE);
    assertEquals(redFunc.apply(RED_PIXEL),   1.0, Double.MIN_VALUE);
    assertEquals(redFunc.apply(GREEN_PIXEL), 0.0, Double.MIN_VALUE);
    assertEquals(redFunc.apply(BLUE_PIXEL),  0.0, Double.MIN_VALUE);
  }
  
  @Test
  public void green() {
    assertEquals(greenFunc.apply(BLACK_PIXEL), 0.0, Double.MIN_VALUE);
    assertEquals(greenFunc.apply(WHITE_PIXEL), 1.0, Double.MIN_VALUE);
    assertEquals(greenFunc.apply(RED_PIXEL),   0.0, Double.MIN_VALUE);
    assertEquals(greenFunc.apply(GREEN_PIXEL), 1.0, Double.MIN_VALUE);
    assertEquals(greenFunc.apply(BLUE_PIXEL),  0.0, Double.MIN_VALUE);
  }
  
  @Test
  public void blue() {
    assertEquals(blueFunc.apply(BLACK_PIXEL), 0.0, Double.MIN_VALUE);
    assertEquals(blueFunc.apply(WHITE_PIXEL), 1.0, Double.MIN_VALUE);
    assertEquals(blueFunc.apply(RED_PIXEL),   0.0, Double.MIN_VALUE);
    assertEquals(blueFunc.apply(GREEN_PIXEL), 0.0, Double.MIN_VALUE);
    assertEquals(blueFunc.apply(BLUE_PIXEL),  1.0, Double.MIN_VALUE);
  }
}
