package ca.uwinnipeg.proximity.image;

public class GreenFunc extends ColorFunc {

  @Override
  protected double map(Pixel pxl) {
    return (pxl.getColor() >> 8) & 0xFF;
  }
  
  @Override
  public String toString() {
    return "Green";
  }

}