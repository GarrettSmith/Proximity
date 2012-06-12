package ca.uwinnipeg.proximity.image;

public class RedFunc extends ColorFunc {

  @Override
  protected double map(Integer pxl) {
    return (pxl >> 16) & 0xFF;
  }
  
  @Override
  public String toString() {
    return "Red";
  }

}