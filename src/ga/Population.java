package ga;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import problem.IDPCNDU;

public class Population {
	private ArrayList<Individual> population;
	private Individual bestIndividual;
	private IDPCNDU task;

	public Population(IDPCNDU task) {
		this.population = new ArrayList<>();
		this.bestIndividual = null;
		this.task = task;
	}

	public void initPopulation() {
		population.clear();

		while(population.size() < Configs.POPULATION_SIZE) {
			Individual i = new Individual();
			i.randomInit(task.adjDomain);
			i.updateFitness(task);

			if(i.getFitness() > -Configs.MAX_VALUE) population.add(i);
		}
	}

	public ArrayList<Individual> getPopulation() {
		return population;
	}

	public void setPopulation(ArrayList<Individual> population) {
		this.population = population;
	}

	public Individual getBestIndividual() {
		return bestIndividual;
	}

	public void setBestIndividual(Individual bestIndividual) {
		this.bestIndividual = bestIndividual;
	}

	public IDPCNDU getTask() {
		return task;
	}

	public void setTask(IDPCNDU task) {
		this.task = task;
	}

	public void evalPopulation() {
		for(Individual ind: this.population) {
			ind.updateFitness(task);
		}
	}

	public void updateBestIndividual() {
		for(Individual i: population) {
			if(bestIndividual == null || bestIndividual.getFitness() < i.getFitness()) 
				this.setBestIndividual(i);;
		}
	}

	// Edge Permutation Operation
	public void EPO(int va, int vb, ArrayList<NodeDepth> tmp, ArrayList<NodeDepth> chromosome) {
		NodeDepth nodeVB = new NodeDepth();
		for(NodeDepth nd: chromosome) {
			if(nd.getNode() == vb) {
				nodeVB = new NodeDepth(nd);
				break;
			}
		}

		// update depth
		int dp = tmp.get(0).getDepth(); // do sau goc cua cay con
		for(NodeDepth nd: tmp) {
			nd.setDepth(nd.getDepth()-dp+nodeVB.getDepth()+1);
		}

		int i;
		for(i = 0; i < chromosome.size(); i++) {
			if(chromosome.get(i).getNode() == vb) {
				break;
			}
		}
		
		// noi tmp vao vi tri sau cua node vb
		chromosome.addAll(i+1, tmp);
	}

	public Individual mutation(Individual p) {
		Individual offspring = new Individual(p);
		ArrayList<NodeDepth> chromosome = offspring.getChromosome();

		// lay ngau nhien 1 domain tren duong di lien mien
		ArrayList<Integer> path = offspring.decode(task);
		path.remove(0); // xoa di source domain
		int va = path.get(Configs.rd.nextInt(path.size())); // va: root of pruned subtree
		ArrayList<NodeDepth> tmp = new ArrayList<>(); // pruned subtree

		// prune subtree
		int i;
		// tim vi tri cua mien VA trong ma hoa node-depth
		for(i = 0; i < chromosome.size(); i++) {
			if(va == chromosome.get(i).getNode()) {
				break;
			}
		}
		// tim cac node con lai trong subtree
		tmp.add(new NodeDepth(chromosome.get(i)));
		for(int j = i+1; j < chromosome.size(); j++) {
			if(chromosome.get(j).getDepth() <= chromosome.get(i).getDepth()) {
				break;
			}
			tmp.add(new NodeDepth(chromosome.get(j)));
		}

		// xoa cac phan tu cua tmp trong chromosome
		for(NodeDepth xx: tmp) {
			for(int j = 0; j < chromosome.size(); j++) {
				if(chromosome.get(j).getNode() == xx.getNode()) {
					chromosome.remove(j);
					break;
				}
			}
		}

		// xoa cac node trong tmp trong parentDomain.get(va)
		ArrayList<Integer> parent = new ArrayList<>();
		for(int d: task.parentDomain.get(va)) {
			parent.add(d);
		}
		for(NodeDepth nd: tmp) {
			int node = nd.getNode();
			if(parent.contains(node)) {
				parent.remove(parent.indexOf(node));
			}
		}

		// chon ngau nhien vb de lam cha moi cua pruned subtree co goc va
		int vb = parent.get(Configs.rd.nextInt(parent.size()));
		
		EPO(va,vb,tmp,chromosome);

		return offspring;
	}


	// vpb: cha cua vri tren cay B
	// kiem tra xem vpb co nam trong cay con cua vri tren cay A ko (A = p1)
	public boolean check(int vri, int vpb, Individual p1) {
		ArrayList<NodeDepth> chromosome = p1.getChromosome();

		int i;
		// tim vi tri cua vri trong ma hoa
		for(i = 0; i < chromosome.size(); i++) {
			if(chromosome.get(i).getNode() == vri) {
				break;
			}
		}

		int depth = chromosome.get(i).getDepth();
		
		// neu vpb nam trong cay con goc vri thi return false
		for(int j = i+1; j < chromosome.size(); j++) {
			if(chromosome.get(j).getDepth() == depth) break;
			if(depth < chromosome.get(j).getDepth() && chromosome.get(j).getNode() == vpb) {
				return false;
			}
		}


		return true;
	}

	// tim node cha cua vri tren cay p
	public int findParent(int vri, Individual p) {
		int rs = 0;
		int depth = 0; // do sau cua vri
		boolean found = false;
		for(int i = p.getChromosome().size()-1; i >= 0; i--) {
			if(p.getChromosome().get(i).getNode() == vri) {
				found = true;
				depth = p.getChromosome().get(i).getDepth();
			}
			if(found && p.getChromosome().get(i).getDepth() < depth) {
				rs = p.getChromosome().get(i).getNode();
				break;
			}
		}

		return rs;
	}

	// Edge Copy Operator (ECO)
	// p1: base individual, p2: reference individual
	public Individual crossover(Individual p1, Individual p2) {
		Individual offspring = new Individual(p1);
		int n = task.getNumberOfDomains(); 
		int i = (int) (Configs.rd.nextDouble()*n/2 + n/4); // so luong node dc doi vi tri
		ArrayList<Integer> vr = new ArrayList<>();

		while(vr.size() < i) {
			int tmp = Configs.rd.nextInt(n)+1;
			if(tmp > 1 && !vr.contains(tmp)) {
				vr.add(tmp);
			}
		}

		for(int j: vr) {
			int p = findParent(j, p2); // p la cha cua j trong cay p2

			if(check(j, p, offspring)) { // kiem tra xem p co nam trong cay con dc cat ra ko
				ArrayList<NodeDepth> tmp = new ArrayList<>();
				ArrayList<NodeDepth> chromosome = offspring.getChromosome();

				// prune subtree
				int ii;
				// tim vi tri cua mien VA trong ma hoa node-depth
				for(ii = 0; ii < chromosome.size(); ii++) {
					if(j == chromosome.get(ii).getNode()) {
						break;
					}
				}

				// tim cac node con lai trong subtree
				tmp.add(new NodeDepth(chromosome.get(ii)));

				for(int jj = ii+1; jj < chromosome.size(); jj++) {
					if(chromosome.get(jj).getDepth() <= chromosome.get(ii).getDepth()) {
						break;
					}
					tmp.add(new NodeDepth(chromosome.get(jj)));
				}

				// xoa cac phan tu cua tmp trong chromosome
				for(NodeDepth xx: tmp) {
					for(int jj = 0; jj < chromosome.size(); jj++) {
						if(chromosome.get(jj).getNode() == xx.getNode()) {
							chromosome.remove(jj);
							break;
						}
					}
				}

				EPO(j, p, tmp, chromosome);
			}
		}


		return offspring;
	}

	public void survivalSelection() {
		Collections.sort(this.population, new Comparator<Individual>() {
			@Override
			public int compare(Individual o1, Individual o2) {
				if(o1.getFitness() > o2.getFitness()) return -1;
				else if(o1.getFitness() < o2.getFitness()) return 1;
				return 0;
			}
		});

		while(this.population.size() > Configs.POPULATION_SIZE) {
			this.population.remove(population.size()-1);
		}
		
		this.setBestIndividual(population.get(0));
	}
}
