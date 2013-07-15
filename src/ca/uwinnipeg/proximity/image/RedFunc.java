package ca.uwinnipeg.proximity.image;


public class RedFunc extends ColorFunc {

  @Override
  protected double map(int index, Image system) {
    return (system.getObject(index) >> 16) & 0xFF;
  }
  
  @Override
  public String toString() {
    return "Red";
  }

}