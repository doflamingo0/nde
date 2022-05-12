package main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

import ga.Configs;
import ga.GA;
import ga.Individual;
import problem.IDPCNDU;

public class Test {
	
	
	public static double mean(int[] rs) {
		double sum = 0;
		for(int i = 0; i < Configs.REPEAT; i++) {
			sum += rs[i];
		}
		return sum/Configs.REPEAT;
	}

	public static double std(int[] rs, double mean) {
		double s = 0;
		for(int i=0; i < Configs.REPEAT; i++) {
			s += Math.pow(rs[i]-mean, 2);
		}
		
		return Math.sqrt(s/(Configs.REPEAT-1));
	}
	
	public static void solver(FileWriter fw, String dataPath, String outputPath, String name) throws IOException {
		IDPCNDU task = new IDPCNDU();
		task.readData(dataPath);
		GA ga = new GA(task, outputPath, name);
		int BF = Integer.MAX_VALUE;

		int[] rs = new int[Configs.REPEAT];
		
		for(int seed = 0; seed < Configs.REPEAT; seed++) {
			Configs.rd.setSeed(seed);
			Individual best = ga.run(seed);
//			System.out.println("Seed " + seed + " Best distance: " + (-best.getFitness()));
			BF = Math.min(-best.getFitness(), BF);
			rs[seed] = -best.getFitness();
//			System.out.println("---------------------------------------");
		}
		double AVG = mean(rs);
		double STD = std(rs, AVG);
		String p = String.format("\t\t%d\t\t%.2f\t\t%.2f", BF, AVG, STD);
		fw.write(name + p +"\n");
		
	}
	
	public static void buildModel(FileWriter fw, String filePath, String dataPath) throws IOException {
		File f = new File(filePath+"\\file.txt");
		Scanner sc = new Scanner(f);
		sc.nextLine();
		while(sc.hasNextLine()) {
			String name = sc.nextLine();
			String out = filePath + "\\" + name;
			File folderOut = new File(out); 
			folderOut.mkdir();
			String dtPath = dataPath + "\\" + name + ".txt";

			solver(fw, dtPath, out, name);
		}
	}
	
	public static void main(String[] args) throws IOException {
		System.out.println("Running...");
		Configs.rd = new Random();
		String dataPath = "D:\\Documents\\Multitask Evolutionary Computing\\Data IDPCDU Nodes";
		String ouputPath = "D:\\Documents\\Multitask Evolutionary Computing\\Node_Depth_Results";
		String result = "D:\\Documents\\Multitask Evolutionary Computing\\Data IDPCDU Nodes\\ketqua.txt";
		FileWriter fw = new FileWriter(result);
		fw.write("Instances\t\t\t\t\tBF\t\tAVG\t\t\tSTD\n");
		File out = new File(ouputPath);
		File[] children = out.listFiles();
		for(File ff: children) {
			String filePath = ff.getAbsolutePath();
			buildModel(fw,filePath, dataPath);
		}
		fw.flush();
		System.out.println("DONE!");
	}
}
