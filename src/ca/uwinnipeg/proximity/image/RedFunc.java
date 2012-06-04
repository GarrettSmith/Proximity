package ca.uwinnipeg.proximity.image;

public class RedFunc extends ColorFunc {

  @Override
  protected double map(Integer color) {
    return (color >> 16) & 0xFF;
  }

}