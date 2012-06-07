package ca.uwinnipeg.proximity.image;

public class BlueFunc extends ColorFunc {

  @Override
  protected double map(Pixel pxl) {
    return pxl.getColor() & 0xFF;
  }
  
  @Override
  public String toString() {
    return "Blue";
  }

}