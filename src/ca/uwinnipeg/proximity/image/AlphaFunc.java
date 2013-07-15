package ca.uwinnipeg.proximity.image;


public class AlphaFunc extends ColorFunc {

  @Override
  protected double map(int index, Image system) {
    return (system.getObject(index) >> 24) & 0xFF;
  }
  
  @Override
  public String toString() {
    return "Alpha";
  }

}