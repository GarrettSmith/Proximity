package ca.uwinnipeg.proximity.image;

public class GreenFunc extends ColorFunc {

  @Override
  protected double map(Integer color) {
    return (color >> 8) & 0xFF;
  }
  
  @Override
  public String toString() {
    return "Green";
  }

}