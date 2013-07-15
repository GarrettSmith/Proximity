/**
 * 
 */
package ca.uwinnipeg.proximity.image;

import java.util.HashMap;
import java.util.Map;

/**
 * @author garrett
 *
 */
public class HomogeneityFunc extends ImageFunc {
	
	protected static final int QUANTISATION_LEVEL = 64;
	protected static final int QUANTISATION_STEP = (int) Math.ceil((double) 0xFF / QUANTISATION_LEVEL);
	protected static final int SUBIMAGE_SIZE = 5;
	protected static final int[] SCALARS = {1, 2, 3, 4};
	protected static final int[][] VECTORS = {
	  {1, 0}, // 0
	  {1, 1}, // 45
	  {0, 1}, // 90
	  {-1, 1} // 135
	};
	
	protected static Map<Image, double[][][]> COOCCURRENCE_MAP = new HashMap<Image, double[][][]>();

	public HomogeneityFunc() {
		super(0, 1);
	}
	
	protected void grayscale(int[] pixels) {
		for (int i = 0; i < pixels.length; i++) {
			pixels[i] = PerceptualGrayScaleFunc.grayscale(pixels[i]);
		}
	}
	
	protected void quantise(int[] pixels, int step) {
		for (int i = 0; i < pixels.length; i++) {
			pixels[i] /= step;
		}
	}
	
	protected int getSubimageIndex(int i, int size, int width) {
    int rowSize = width * size;
    int cols = width / size;
	  int row = i / rowSize;
    int col = (i % width) / size;
    return row * cols + col;
	}
	
	protected int[][][] subimage(int[] pixels, int size, int width) {
	  int sqrSize = size * size;
    int rowSize = width * size;
    int cols = width / size;
    int[][][] result = new int[pixels.length / sqrSize][size][size];
    
    for (int i = 0; i < pixels.length; i++) {
      if (i % width < cols * size) {
        int row = i / rowSize;
        int col = (i % width) / size;
        int subimage = row * cols + col;
        // check if we are on to the non-complete subimages
        if (subimage >= result.length) {
          break;
        }
        else {
          int subimageRow = (i / width) % size;
          int subimageCol = (i % width) % size;
          result[subimage][subimageRow][subimageCol] = pixels[i];
        }    
      }
    }
    
	  return result;
	}
	
	protected double[][][] calcCooccurenceMatrix(Image image) {
    int[] pixels = image.getPixels();
    grayscale(pixels);
    quantise(pixels, QUANTISATION_STEP);
    int[][][] subimages = subimage(pixels, SUBIMAGE_SIZE, image.getWidth());
    double[][][] matrix = new double[subimages.length][QUANTISATION_LEVEL][QUANTISATION_LEVEL];
    
    // calc matrix for each subimage
    for (int i = 0; i < subimages.length; i++) {
      int[][] subimage = subimages[i];
      for (int y = 0; y < subimage.length; y++) {
        for (int x = 0; x < subimage[y].length; x++) {
          // the reference value
          int ref = subimage[y][x];
          // for each angle
          for (int[] vector : VECTORS) {
            // for each distance
            for (int scalar : SCALARS) {
              int nX = x + vector[0] * scalar;
              int nY = y + vector[1] * scalar;
              // make sure the neighbour is in this subimage
              if (0 <= nX && nX < subimage.length &&
                  0 <= nY && nY < subimage[y].length) {
                int neighbour = subimage[nY][nX];
                matrix[i][ref][neighbour]++;
              }
            }
          }
        }
      }
    }
    COOCCURRENCE_MAP.put(image, matrix);
    
    // find the sum of the first matrix, they are all equal
    int sum = 0;
    for (int y = 0; y < matrix[0].length; y++) {
      for (int x = 0; x < matrix[0][0].length; x++) {
        sum += matrix[0][y][x];
      }
    }
    
    // turn all matrix values into probabilities
    for (int i = 0; i < matrix.length; i++) {
      for (int j = 0; j < matrix[i].length; j++) {
        for (int k = 0; k < matrix[i][j].length; k++) {
          matrix[i][j][k] /= sum;
        }
      }
    }
    
    return matrix;
  }
  
  protected double[][][] getCooccurenceMatrix(Image image) {
    double[][][] rtn = COOCCURRENCE_MAP.get(image);
    if (rtn == null) {
      COOCCURRENCE_MAP.clear();
      rtn = calcCooccurenceMatrix(image);
    }
    return rtn;
  }
 
	/* (non-Javadoc)
	 * @see ca.uwinnipeg.proximity.ProbeFunc#map(int, ca.uwinnipeg.proximity.PerceptualSystem)
	 */
	@Override
	protected double map(int index, Image image) {
	  double[][][] matrices = getCooccurenceMatrix(image);
    int width = image.getWidth();
    int cols = width / SUBIMAGE_SIZE;
    int subimage = getSubimageIndex(index, SUBIMAGE_SIZE, width);
    
    // check if we are out of bounds
    if (subimage >= matrices.length || index % width >= cols * SUBIMAGE_SIZE) {
      return 0;
    }
    else {      
      double[][] p = matrices[subimage];   
      
      double homogeneity = 0;
      for (int i = 0; i < p.length; i++) {
        for (int j = 0; j < p[i].length; j++) {
          homogeneity += (double) p[i][j] / (1 + Math.abs(i - j));
        }
      }
      
      return homogeneity;
    }
	}
	
	@Override
	public String toString() {
		return "Homogeneity";
	}

}
