import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Main {

	public static void main(String[] args) throws IOException {
		Harris harris = new Harris();
		
		int[][] imageArray1 = harris.getArrayFromBinaryImage("resim.PNG");
		BufferedImage img1 = harris.detector(imageArray1);
		ImageIO.write(img1, "png", new File("output1.png"));
		System.out.println("output1.png dosyasi yazdirildi.");
		
		int[][] imageArray2 = harris.getArrayFromBinaryImage("dd.png");
		BufferedImage img2 = harris.detector(imageArray2);
		ImageIO.write(img2, "png", new File("output2.png"));
		System.out.println("output2.png dosyasi yazdirildi.");
		
		int[][] imageArray3 = harris.getArrayFromBinaryImage("mc.jpg");
		BufferedImage img3 = harris.detector(imageArray3);
		ImageIO.write(img3, "png", new File("output3.png"));
		System.out.println("output3.png dosyasi yazdirildi.");
	}
}
