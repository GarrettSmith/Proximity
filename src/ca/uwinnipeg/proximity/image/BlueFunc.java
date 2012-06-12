package ca.uwinnipeg.proximity.image;

public class BlueFunc extends ColorFunc {

  @Override
  protected double map(Integer pxl) {
    return pxl & 0xFF;
  }
  
  @Override
  public String toString() {
    return "Blue";
  }

}