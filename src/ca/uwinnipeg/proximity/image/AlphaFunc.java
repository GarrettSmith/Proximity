package ca.uwinnipeg.proximity.image;

public class AlphaFunc extends ColorFunc {

  @Override
  protected double map(Integer pxl) {
    return (pxl >> 24) & 0xFF;
  }
  
  @Override
  public String toString() {
    return "Alpha";
  }

}