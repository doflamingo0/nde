package problem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import ga.Configs;

public class IDPCNDU {
	private int numberOfNodes;
	private int numberOfDomains;
	public int numberOfEdges = 0;
	private int s; // source node
	private int t; // destination node
	public int[] domain; // d[i] = k: domain of node i is k
	public int[][] distance; // distance[i][j]: distance from node i to node j
	public ArrayList<ArrayList<Integer> > listDomain; // listDomain.get(k): cac node trong domain k
	public ArrayList<ArrayList<Integer> > adjDomain; // adjDomain.get(k): cac domain ke voi domain k
	public ArrayList<ArrayList<Integer> > borderNode; // borderNode.get(k): cac node bien cua domain k
	public ArrayList<ArrayList<Integer> > parentDomain; // parentDomain(k): cac domain la cha cua domain k
														// co canh noi voi domain k
	
	public ArrayList<ArrayList<Integer> > adjNode; // adjNode.get(k): cac node ke voi node k
	public ArrayList<ArrayList<Integer> > parentNode; // parentNode.get(k): cac node la cha cua node k
	public int[] indegreeNode; // indegreeNode[i]: bac vao cua node i
	public int[] outdegreeNode; // outdegreeNode[i]: bac ra cua node i
	public int[] indegreeDomain; // indegreeDomain[i]: bac vao cua mien i
	public int[] outdegreeDomain; // outdegreeDomain[i]: bac ra cua mien i
	
	public int getNumberOfNodes() {
		return numberOfNodes;
	}


	public void setNumberOfNodes(int numberOfNodes) {
		this.numberOfNodes = numberOfNodes;
	}


	public int getNumberOfDomains() {
		return numberOfDomains;
	}


	public void setNumberOfDomains(int numberOfDomains) {
		this.numberOfDomains = numberOfDomains;
	}


	public int getS() {
		return s;
	}


	public void setS(int s) {
		this.s = s;
	}


	public int getT() {
		return t;
	}


	public void setT(int t) {
		this.t = t;
	}


	public int[] getDomain() {
		return domain;
	}


	public void setDomain(int[] domain) {
		this.domain = domain;
	}


	public int[][] getDistance() {
		return distance;
	}


	public void setDistance(int[][] distance) {
		this.distance = distance;
	}


	public ArrayList<ArrayList<Integer>> getListDomain() {
		return listDomain;
	}


	public void setListDomain(ArrayList<ArrayList<Integer>> listDomain) {
		this.listDomain = listDomain;
	}


	public ArrayList<ArrayList<Integer>> getAdjDomain() {
		return adjDomain;
	}


	public void setAdjDomain(ArrayList<ArrayList<Integer>> adjDomain) {
		this.adjDomain = adjDomain;
	}


	public ArrayList<ArrayList<Integer>> getBorderNode() {
		return borderNode;
	}


	public void setBorderNode(ArrayList<ArrayList<Integer>> borderNode) {
		this.borderNode = borderNode;
	}


	public void readData(String filePath) throws IOException {
		try {
			Scanner inp = new Scanner(new File(filePath));
			numberOfNodes = inp.nextInt();
			numberOfDomains = inp.nextInt();
			s = inp.nextInt();
			t = inp.nextInt();

			// PGA ko su dung 2 dong nay
			indegreeNode = new int[numberOfNodes+1];
			outdegreeNode = new int[numberOfNodes+1];
			indegreeDomain = new int[numberOfDomains+1];
			outdegreeDomain = new int[numberOfDomains+1];
			
			// node, domain danh so tu 1, khong phai tu 0
			domain = new int[numberOfNodes+1];
			distance = new int[numberOfNodes+1][numberOfNodes+1];
			for(int i = 1; i <= numberOfNodes; i++) {
				Arrays.fill(distance[i], Configs.MAX_VALUE);
				distance[i][i] = 0;
			}
			listDomain = new ArrayList<ArrayList<Integer> >(numberOfDomains+1);
			listDomain.add(new ArrayList<>()); // listDomain.get(0) khong dung

			// startDomain chi gom s
			ArrayList<Integer> startDomain = new ArrayList<>();
			startDomain.add(s);
			listDomain.add(startDomain);

			domain[inp.nextInt()] = 1;
			inp.nextLine();

			for(int i = 2; i <= numberOfDomains-1; i++) {
				String aa = inp.nextLine();
				String[] tmp = aa.split(" ");
				
				ArrayList<Integer> lst = new ArrayList<>();
				for(String node: tmp) {
					domain[Integer.valueOf(node)] = i;
					lst.add(Integer.valueOf(node));
				}
				listDomain.add(lst);
			}

			// endDomain chi gom t
			ArrayList<Integer> endDomain = new ArrayList<>();
			endDomain.add(t);
			listDomain.add(endDomain);
			domain[inp.nextInt()] = numberOfDomains;
			
			// PGA ko su dung borderNode
			borderNode = new ArrayList<ArrayList<Integer> >(numberOfDomains+1);
			adjDomain = new ArrayList<ArrayList<Integer> >(numberOfDomains+1);
			parentDomain = new ArrayList<ArrayList<Integer> >(numberOfDomains+1);
			for(int i = 0; i <= numberOfDomains; i++) {
				borderNode.add(new ArrayList<>());
				adjDomain.add(new ArrayList<>());
				parentDomain.add(new ArrayList<>());
			}
			
			adjNode = new ArrayList<ArrayList<Integer>>();
			parentNode = new ArrayList<ArrayList<Integer>>();
			for(int i = 0; i <= numberOfNodes; i++) {
				adjNode.add(new ArrayList<>());
				parentNode.add(new ArrayList<>());
			}
			
			int i, j, w;
			while(inp.hasNextLine()) {
				i = inp.nextInt();
				j = inp.nextInt();
				w = inp.nextInt();
				
				if(!adjNode.get(i).contains(j)) adjNode.get(i).add(j);
				if(!parentNode.get(j).contains(i)) parentNode.get(j).add(i);
				
				distance[i][j] = Math.min(distance[i][j], w);
				numberOfEdges++;
				outdegreeNode[i]++;
				indegreeNode[j]++;
				
				// update adjacent list of domain and border node
				if(domain[i] != domain[j]) {
					
					outdegreeDomain[domain[i]]++;
					indegreeDomain[domain[j]]++;
					
					if(!adjDomain.get(domain[i]).contains(domain[j]))
						adjDomain.get(domain[i]).add(domain[j]);
					if(!borderNode.get(domain[i]).contains(i))
						borderNode.get(domain[i]).add(i);
					if(!borderNode.get(domain[j]).contains(j))
						borderNode.get(domain[j]).add(j);
					if(!parentDomain.get(domain[j]).contains(domain[i]))
						parentDomain.get(domain[j]).add(domain[i]);
				}
			}

			// su dung pre-processing cua PGA nen ko su dung 2 ham nay
			preFilterProcessing();
			floydWarshall();
			

//			int cnt = 0;
//			for(ArrayList<Integer> o: adjDomain) {
//				System.out.print("adjacent of domain " + cnt + ": ");
//				cnt++;
//				for(int oo: o) {
//					System.out.print(oo + " ");
//				}
//				System.out.println();
//			}
//			System.out.println("-------------------------");
//			
//			cnt =0;
//			for(ArrayList<Integer> o: borderNode) {
//				System.out.print("border nodes of domain " + cnt + ": ");
//				cnt++;
//				for(int oo: o) {
//					System.out.print(oo + " ");
//				}
//				System.out.println();
//			}
//			System.out.println("-------------------------");
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	// Apply Floyd-Warshall Algorithms for each domain
	public void floydWarshall() {
		for(int d = 1; d <= numberOfDomains; d++) {
			if(d == 1 || d == numberOfDomains) continue; // source and destination domain have only 1 node!
			
			ArrayList<Integer> lst = listDomain.get(d);
			for(int k: lst) {
				for(int i: lst) {
					for(int j: lst) {
						if(distance[i][j] > distance[i][k] + distance[k][j])
							distance[i][j] = distance[i][k] + distance[k][j];
					}
				}
			}
		}
	}
	
	
	public void updateIndegreeDomain(int d) {
		indegreeDomain[d] = -1;
		for(int dd: adjDomain.get(d)) {
			indegreeDomain[dd]--;
			parentDomain.get(dd).remove(parentDomain.get(dd).indexOf(d));
			
			if(indegreeDomain[dd] == 0) updateIndegreeDomain(dd);
		}
	}
	
	public void updateOutdegreeDomain(int d) {
		outdegreeDomain[d] = -1;
		for(int dd: parentDomain.get(d)) {
			outdegreeDomain[dd]--;
			adjDomain.get(dd).remove(adjDomain.get(dd).indexOf(d));
			
			if(outdegreeDomain[dd] == 0) updateOutdegreeDomain(dd);
		}
	}
	
	public void updateIndegree(int node) {
		indegreeNode[node] = -1; // danh dau nhung node da bo di
		for(int v: adjNode.get(node)) {
			indegreeNode[v]--;
			parentNode.get(v).remove(parentNode.get(v).indexOf(node));
			distance[node][v] = Configs.MAX_VALUE;
			
			if(domain[node] != domain[v]) { // neu node va v la 2 node o 2 mien khac nhau -> node la dinh bien
				int idx = borderNode.get(domain[node]).indexOf(node);
				if(idx != -1) borderNode.get(domain[node]).remove(idx); // xoa ra khoi ds dinh bien
				
				if(--outdegreeDomain[domain[node]] == 0) {
					updateOutdegreeDomain(domain[node]);
				}
				
				if(--indegreeDomain[domain[v]] == 0) {
					updateIndegreeDomain(domain[v]);
				}
			}
			if(indegreeNode[v] == 0) updateIndegree(v);
		}
	}
	
	public void updateOutdegree(int node) {
		outdegreeNode[node] = -1; // danh dau nhung node da bo di
		for(int v: parentNode.get(node)) {
			outdegreeNode[v]--;
			adjNode.get(v).remove(adjNode.get(v).indexOf(node)); 
			distance[v][node] = Configs.MAX_VALUE;
			
			if(domain[node] != domain[v]) { // neu node va v la 2 node o 2 mien khac nhau -> node la dinh bien
				int idx = borderNode.get(domain[node]).indexOf(node);
				if(idx != -1) borderNode.get(domain[node]).remove(idx); // xoa ra khoi ds dinh bien
				
				if(--indegreeDomain[domain[node]] == 0) {
					updateIndegreeDomain(domain[node]);
				}
				
				if(--outdegreeDomain[domain[v]] == 0) {
					updateOutdegreeDomain(domain[v]);
				}
			}
			if(indegreeNode[v] == 0) updateIndegree(v);
		}
	}
	
	// bo nhung node co indegree va outdegree = 0
	// neu ko con node nao co bac = 0 thi dung
	public void preFilterProcessing() {
		boolean stop = true;
		while(stop) {
			stop = true;
			for(int i = 2; i < numberOfNodes; i++) {
				if(indegreeNode[i] == 0) {
					stop = false;
					updateIndegree(i);
				}
				if(outdegreeNode[i] == 0) {
					stop = false;
					updateOutdegree(i);
				}
			}
		}
	}
}
