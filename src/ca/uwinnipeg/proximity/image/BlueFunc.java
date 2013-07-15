package ca.uwinnipeg.proximity.image;


public class BlueFunc extends ColorFunc {

  @Override
  protected double map(int index, Image system) {
    return system.getObject(index) & 0xFF;
  }
  
  @Override
  public String toString() {
    return "Blue";
  }

}