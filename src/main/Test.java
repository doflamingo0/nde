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
		
		long t1 = System.currentTimeMillis();
		IDPCNDU task = new IDPCNDU();
		System.out.println(dataPath);
		task.readData(dataPath);
		GA ga = new GA(task, outputPath, name);
		int BF = Integer.MAX_VALUE;

		int[] rs = new int[Configs.REPEAT];
		
		for(int seed = 0; seed < Configs.REPEAT; seed++) {
			Configs.rd.setSeed(seed);
			Individual best = ga.run(seed);
			System.out.println("Seed " + seed + " Best distance: " + (-best.getFitness()));
			BF = Math.min(-best.getFitness(), BF);
			rs[seed] = -best.getFitness();
			System.out.println("---------------------------------------");
		}
		long t2 = System.currentTimeMillis();
		double time = (t2-t1)/1000/60/Configs.REPEAT; // minutes
		double AVG = mean(rs);
		double STD = std(rs, AVG);
		String p = String.format("\t%d\t%.2f\t%.2f\t%.2f", BF, AVG, STD, time);
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
		Configs.rd = new Random();
		System.out.println("Running...");
		
		// Folder chua cac file data
		String dataPath = "data";
		
		// Folder chua output .gen, .opt
		String ouputPath = "output";
		
		// File tong hop ket qua
		String result = "output\\all_result.txt";
		
		FileWriter fw = new FileWriter(result);
		fw.write("Instances\tBF\t\tAVG\tSTD\tTime\n");
		
		File out = new File(ouputPath);
		File[] children = out.listFiles();
		for(File ff: children) {
			String filePath = ff.getAbsolutePath();
			if (filePath.contains(".txt")) {
				continue;
			}
			
			buildModel(fw,filePath, dataPath);
		}
		fw.flush();
		System.out.println("DONE!");
		
		
//		System.out.println("Start...");
//		Configs.rd = new Random();
//		
//		ArrayList<String> file_url = new ArrayList<>();
//
//		file_url.add("data\\idpc_ndu_302_12_4930.txt");
//		file_url.add("data\\idpc_ndu_1002_12_82252.txt");
//		file_url.add("data\\idpc_ndu_1506_9_130556.txt");
//		file_url.add("data\\idpc_ndu_2494_12_276620.txt");
//		file_url.add("data\\idpc_ndu_2715_22_592246.txt");
//		
//		for(int i = 0; i < file_url.size(); i++) {
//			System.out.println(file_url.get(i));
//			solve(file_url.get(i), "output\\");
//		}
//		System.out.println("DONE...");
	}
	
	
	public static void solve(String url, String output) throws IOException {
		
		IDPCNDU task = new IDPCNDU();
		task.readData(url);
		GA ga = new GA(task, output, url);
	
		int BF = Integer.MAX_VALUE;

		int[] rs = new int[Configs.REPEAT];
		
		for(int seed = 0; seed < Configs.REPEAT; seed++) {
			Configs.rd.setSeed(seed);
			System.out.println(seed);
			Individual best = ga.run2(seed);
//			System.out.println("Seed " + seed + " Best distance: " + (-best.getFitness()));
//			BF = Math.min(-best.getFitness(), BF);
//			rs[seed] = -best.getFitness();
//			System.out.println("---------------------------------------");
		}
	}
}
