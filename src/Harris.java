import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Harris {

	public Harris() {

	}

	public int getRowCount(int[][] matrix) {
		return matrix.length;
	}

	public int getColumnCount(int[][] matrix) {
		return matrix[0].length;
	}

	public int[][] getArrayFromBinaryImage(String fileName) {
		BufferedImage source;
		int[][] resultImage = new int[0][0];
		try {
			source = ImageIO.read(new File(fileName));
			int rowCount = source.getHeight();
			int colCount = source.getWidth();
			resultImage = new int[rowCount][colCount];
			for (int i = 0; i < rowCount; i++) {
				for (int j = 0; j < colCount; j++) {
					Color pixelColor = new Color(source.getRGB(j, i));
					int red = (int) (pixelColor.getRed());
					int green = (int) (pixelColor.getGreen());
					int blue = (int) (pixelColor.getBlue());
					if (red > 240 && green > 240 && blue > 240)
						resultImage[i][j] = 1;
					else
						resultImage[i][j] = 0;
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return resultImage;
	}

	public int[][] getNeighborhood(int[][] matrix, int x, int y, int n) {
		int[][] neighborhood = new int[n][n];
		int rowCount = getRowCount(matrix);
		int colCount = getColumnCount(matrix);
		int k = n % 2 != 0 ? n / 2 : -1;
		int nRow = 0, nCol = 0;
		for (int i = x - k; i <= x + k; i++) {
			for (int j = y - k; j <= y + k; j++) {
				if (i >= 0 && i < colCount && j >= 0 && j < rowCount) {
					neighborhood[nRow][nCol] = matrix[j][i];
				} else {
					neighborhood[nRow][nCol] = 0;
				}
				nRow++;
			}
			nRow = 0;
			nCol++;
		}
		return neighborhood;
	}

	public void printMatrix(int[][] matrix) {
		String result = "";
		for (int i = 0; i < getRowCount(matrix); i++) {
			for (int j = 0; j < getColumnCount(matrix); j++) {
				result += matrix[i][j] + " ";
			}
			result += "\n";
		}
		System.out.println(result);
	}

	public void printArray(int[] array) {
		String result = "";
		for (int i = 0; i < array.length; i++) {
			result += array[i] + " ";
		}
		System.out.println(result);
	}

	public int[] getMatrixAxis(int[][] matrix, boolean axisType, int axisNumber) {
		int[] result;
		if (axisType) {
			result = new int[getRowCount(matrix)];
			for (int i = 0; i < getColumnCount(matrix); i++) {
				result[i] = matrix[axisNumber][i];
			}
		} else {
			result = new int[getColumnCount(matrix)];
			for (int i = 0; i < getRowCount(matrix); i++) {
				result[i] = matrix[i][axisNumber];
			}
		}
		return result;
	}

	public int getDerivativeX(int[][] matrix, int axisNumber) {
		int[] xAxis = getMatrixAxis(matrix, true, axisNumber);
		int medianIndex = xAxis.length % 2 == 0 ? ((xAxis.length - 1) / 2) - 1 : (xAxis.length - 1) / 2;
		int leftSum = 0, rightSum = 0, derivative = 0;
		for (int i = 0; i < medianIndex; i++)
			leftSum += xAxis[i];
		for (int i = medianIndex + 1; i < xAxis.length; i++)
			rightSum += xAxis[i];
		derivative = Math.abs(leftSum - rightSum);
		return derivative;
	}

	public int getDerivativeY(int[][] matrix, int axisNumber) {
		int[] yAxis = getMatrixAxis(matrix, false, axisNumber);
		int medianIndex = yAxis.length % 2 == 0 ? ((yAxis.length - 1) / 2) - 1 : (yAxis.length - 1) / 2;
		int topSum = 0, bottomSum = 0, derivative = 0;
		for (int i = 0; i < medianIndex; i++)
			topSum += yAxis[i];
		for (int i = medianIndex + 1; i < yAxis.length; i++)
			bottomSum += yAxis[i];
		derivative = Math.abs(topSum - bottomSum);
		return derivative;
	}

	public int determinant2x2(int[][] matrix) {
		return (matrix[0][0] * matrix[1][1]) - (matrix[0][1] * matrix[1][0]);
	}

	public int trace(int[][] matrix) {
		int result = 0;
		if (getRowCount(matrix) == getColumnCount(matrix)) {
			for (int i = 0; i < getRowCount(matrix); i++) {
				for (int j = 0; j < getColumnCount(matrix); j++) {
					if (i == j)
						result += matrix[i][j];
				}
			}
		} else
			result = -1;
		return result;
	}

	public double response(int[][] matrix, double k) {
		return determinant2x2(matrix) - (k * (trace(matrix) ^ 2));
	}

	public BufferedImage detector(int[][] image) throws IOException {
		int width = getColumnCount(image);
		int height = getRowCount(image);
		BufferedImage outputImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		for (int i = 0; i < getRowCount(image); i++) {
			for (int j = 0; j < getColumnCount(image); j++) {
				int[][] neighborhood = getNeighborhood(image, j, i,5);
				int dx = getDerivativeX(neighborhood, 2);
				int dy = getDerivativeY(neighborhood, 2);
				int dx2 = dx * dx;
				int dy2 = dy * dy;
				int dxy = dx * dy;
				int[][] m = new int[][] { { dx2, dxy }, { dxy, dy2 } };
				double r = response(m, 0.04);
				//System.out.println(r);
				if (r == -0.08) /* flat */ {
					Color red = new Color(255, 255, 255);
					outputImage.setRGB(j, i, red.getRGB());
				} else if (r == -0.12)/* edge */ {
					Color green = new Color(255, 0, 0);
					outputImage.setRGB(j, i, green.getRGB());
				} else if (r == -0.24) /* edge */ {
					Color blue = new Color(255, 0, 0);
					outputImage.setRGB(j, i, blue.getRGB());
				} else if (r == 0)/* corner */ {
					Color blue = new Color(0, 0, 0);
					outputImage.setRGB(j, i, blue.getRGB());
				} else if (r == -0.4)/* corner */ {
					Color blue = new Color(0, 0, 0);
					outputImage.setRGB(j, i, blue.getRGB());
				} else if (r == -0.28)/* corner */ {
					Color blue = new Color(0, 0, 0);
					outputImage.setRGB(j, i, blue.getRGB());
				}
			}
		}
		return outputImage;
	}
}
