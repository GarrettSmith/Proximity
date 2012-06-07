package ca.uwinnipeg.proximity.image;

public class RedFunc extends ColorFunc {

  @Override
  protected double map(Pixel pxl) {
    return (pxl.getColor() >> 16) & 0xFF;
  }
  
  @Override
  public String toString() {
    return "Red";
  }

}