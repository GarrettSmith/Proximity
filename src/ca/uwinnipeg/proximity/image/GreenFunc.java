package ca.uwinnipeg.proximity.image;

public class GreenFunc extends ColorFunc {

  @Override
  protected double map(Integer pxl) {
    return (pxl >> 8) & 0xFF;
  }
  
  @Override
  public String toString() {
    return "Green";
  }

}