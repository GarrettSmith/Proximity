package ca.uwinnipeg.proximity.image;


public class GreenFunc extends ColorFunc {

  @Override
  protected double map(int index, Image system) {
    return (system.getObject(index) >> 8) & 0xFF;
  }
  
  @Override
  public String toString() {
    return "Green";
  }

}