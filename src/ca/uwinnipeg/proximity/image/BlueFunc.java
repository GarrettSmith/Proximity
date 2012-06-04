package ca.uwinnipeg.proximity.image;

public class BlueFunc extends ColorFunc {

  @Override
  protected double map(Integer color) {
    return color & 0xFF;
  }

}