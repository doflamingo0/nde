package ga;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import problem.IDPCNDU;

public class GA {
	private IDPCNDU task;
	private String fileName;
	private String outputPath;
	
	public GA(IDPCNDU task, String outputPath, String fileName) {
		this.task = task;
		this.fileName = fileName;
		this.outputPath = outputPath;
	}
	
	public void save(int seed, Population pop, long t1, long t2) throws IOException {

		String outFileNameOpt = fileName + "_seed(" + String.valueOf(seed) + ").opt";

		FileWriter fwOpt = new FileWriter(outputPath+ "\\"+outFileNameOpt);
		fwOpt.write("Filename: "+fileName + "\n");
		fwOpt.write("Seed: " + String.valueOf(seed)+"\n");
		fwOpt.write("Fitness: " + String.valueOf(-pop.getBestIndividual().getFitness())+"\n");
		
		long duration = t2-t1;
		long diffHour = TimeUnit.MILLISECONDS.toHours(duration);
		long diffMinute = TimeUnit.MILLISECONDS.toMinutes(duration-diffHour*3600*1000);
		long diffSecond = TimeUnit.MILLISECONDS.toSeconds(duration-diffHour*3600*1000-diffMinute*60*1000);
		long diffMS = duration - diffHour*3600*1000 - diffMinute*60*1000 - diffSecond*1000;
		String time = String.format("%02d:%02d:%02d.%03d", diffHour, diffMinute, diffSecond, diffMS);
		fwOpt.write("Time: " + time +"\n");
		fwOpt.flush();

		fwOpt.close();
	}
	
	public Individual run(int seed) throws IOException {
		long t1 = System.currentTimeMillis();
		Population population = new Population(task);
		population.initPopulation(); // init and update fitness
		population.updateBestIndividual();
//		System.out.println("Init Population, Start Distance: " + (-population.getBestIndividual().getFitness()));
		int generation = 0;
		String outFileNameGen = fileName + "_seed(" + String.valueOf(seed) + ").gen";
		FileWriter fwGen = new FileWriter(outputPath+ "\\"+outFileNameGen);
		fwGen.write("Generations " + fileName +"\n");

		while(generation < Configs.MAX_GENERATIONS) {
			fwGen.write(String.valueOf(generation) + " " + String.valueOf(-population.getBestIndividual().getFitness())+"\n");
			fwGen.flush();
			// tao tap con
			ArrayList<Individual> offspring = reproduction(population.getPopulation());
			
			// gop quan the cu vs quan the moi
			ArrayList<Individual> imiPop = new ArrayList<>();
			
			imiPop.addAll(offspring);
			imiPop.addAll(population.getPopulation());
			
			// luau chon ca the cua the he sau
			population.setPopulation(imiPop);
			population.survivalSelection();
			generation++;
			
//			System.out.println("G " + generation + " best: " + (-population.getBestIndividual().getFitness()));
		}
		long t2 = System.currentTimeMillis();
		save(seed, population, t1, t2);
		return population.getBestIndividual();
	}
	
	// For get data of domain, node, edge when decode
	public Individual run2(int seed) throws IOException {

		Population population = new Population(task);
		population.initPopulation(); // init and update fitness
		population.updateBestIndividual();
//		System.out.println("Init Population, Start Distance: " + (-population.getBestIndividual().getFitness()));
		int generation = 0;
		String outFileNameOpt = fileName + "_seed(" + String.valueOf(seed) + ").opt";
//		String outFileNameOpt = fileName;
		@SuppressWarnings("resource")
		FileWriter fwGen = new FileWriter(outputPath+ "\\"+outFileNameOpt);
		fwGen.write("Gen\tTotal_domain\tDomain\tTotal_node\tNode\tTotal_edge\tEdge\n");

		while(generation < Configs.MAX_GENERATIONS) {
			
			Individual b = population.getBestIndividual();
			
			fwGen.write(String.valueOf(generation) + "\t" + String.valueOf(b.total_domain) + "\t" + String.valueOf(b.domain) + "\t" + String.valueOf(b.total_node) + "\t" + String.valueOf(b.node) + "\t" + String.valueOf(b.total_edge) + "\t" + String.valueOf(b.edge) + "\n");
			fwGen.flush();
			// tao tap con
			ArrayList<Individual> offspring = reproduction(population.getPopulation());
			
			// gop quan the cu vs quan the moi
			ArrayList<Individual> imiPop = new ArrayList<>();
			
			imiPop.addAll(offspring);
			imiPop.addAll(population.getPopulation());
			
			// luau chon ca the cua the he sau
			population.setPopulation(imiPop);
			population.survivalSelection();
			generation++;
			
//			System.out.println("G " + generation + " best: " + (-population.getBestIndividual().getFitness()));
		}

		return population.getBestIndividual();
	}
	
	// tournament with k = 2
	public Individual selectParent(ArrayList<Individual> parents) {
		int pos1 = Configs.rd.nextInt(parents.size());
		int pos2 = Configs.rd.nextInt(parents.size());
		while(pos1 == pos2) {
			pos2 = Configs.rd.nextInt(parents.size());
		}
		Individual p1 = parents.get(pos1);
		Individual p2 = parents.get(pos2);
		
		if(p1.getFitness() > p2.getFitness()) return p1;
		
		return p2;
	}
	
	public ArrayList<Individual> reproduction(ArrayList<Individual> parents){
		Population offspringPop = new Population(task);
		
		while(offspringPop.getPopulation().size() < Configs.POPULATION_SIZE) {
			Individual p1 = selectParent(parents);
			Individual p2 = selectParent(parents);
			
			if(Configs.rd.nextDouble() < Configs.CROSSOVER_RATE) {
				Individual o = offspringPop.crossover(p1, p2);
				if(Configs.rd.nextDouble() < Configs.MUTATION_RATE) {
					o = offspringPop.mutation(o);
				}
				o.updateFitness(task);
				offspringPop.getPopulation().add(o);
			}
			
		}
		
		return offspringPop.getPopulation();
	}
}
