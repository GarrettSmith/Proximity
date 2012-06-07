package ca.uwinnipeg.proximity.image;

public class AlphaFunc extends ColorFunc {

  @Override
  protected double map(Pixel pxl) {
    return (pxl.getColor() >> 24) & 0xFF;
  }
  
  @Override
  public String toString() {
    return "Alpha";
  }

}