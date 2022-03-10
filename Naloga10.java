import java.io.*;
import java.util.*;

import java.lang.*;

public class Naloga10 {
	public static class GraphVertex{
		
		Comparable value; double x; double y;
		GraphEdge firstEdge;
		GraphVertex nextVertex;
		int num_connections;
		int distance;
		boolean visited;
		GraphVertex parent;
		
		int id_group;
		
		public GraphVertex(Comparable value, double x, double y) {
			this.value = value;
			this.x = x;
			this.y = y;
			
			firstEdge = null;
			nextVertex = null;
			this.num_connections = 0;
			this.distance = 0;
			this.parent = null;
			this.visited = false;
			
			this.id_group = 0;
			this.help_ids = new ArrayList<>();
		}
		
		public String toString(){
			return String.valueOf(value);
		}
	}

	public static class GraphEdge
	{
		Comparable evalue;
		GraphVertex endVertex;
		GraphEdge nextEdge;
			
		public GraphEdge(Comparable eval, GraphVertex eVertex, GraphEdge nEdge)
		{
			evalue = eval;
			endVertex = eVertex;
			nextEdge = nEdge;
		}
	}
	
	public static class Edge implements Comparable<Edge>{
		
		double distance;	
		GraphVertex start_vertex;
		GraphVertex end_vertex;

		public Edge(double distance, GraphVertex sv, GraphVertex ev) {
			this.distance = distance;
			this.start_vertex = sv;
			this.end_vertex = ev;
		}
		
		@Override
		public int compareTo(Edge e1) {
			if(this.distance - e1.distance < 0) {
				return -1;
			} else return 1;
		}
		
		public String toString() {
			return String.valueOf(this.distance);
		}
		
	}
		

	public static class DirectedGraph {

		protected GraphVertex fVertex;
		protected List<List<Integer>> groups;
		protected int cur_groups;
		
		public DirectedGraph() {
			this.makenull();
		}
		
		public void makenull(){
			fVertex = null;
			this.groups = new ArrayList<>();
			this.cur_groups = 0;
		}
		
		public void insertVertex(GraphVertex v)
		{
			v.nextVertex = fVertex;
			fVertex = v;
		}
		
		public void insertEdge(GraphVertex v1, GraphVertex v2, Comparable eval)
		{
			GraphEdge newEdge = new GraphEdge(eval, v2, v1.firstEdge);
			v1.firstEdge = newEdge;
		}
		
		public GraphVertex firstVertex()
		{
			return fVertex;
		}
		
		public GraphVertex nextVertex(GraphVertex v)
		{
			return v.nextVertex;
		}
		
		public GraphEdge firstEdge(GraphVertex v)
		{
			return v.firstEdge;
		}
		
		public GraphEdge nextEdge(GraphVertex v, GraphEdge e)
		{
			return e.nextEdge;
		}
		
		public GraphVertex endPoint(GraphEdge e)
		{
			return e.endVertex;
		}
		
		public void print(){
			
			for (GraphVertex v = firstVertex(); v != null; v = nextVertex(v)) {
				System.out.print(v + ": ");
				for (GraphEdge e = firstEdge(v); e != null; e = nextEdge(v, e))
					System.out.print(endPoint(e) + "(" + e.evalue + ")" + ", ");
				System.out.println();
			}
		}
		
		
		public GraphVertex locateVertex(Object value) {
			for(GraphVertex v = this.firstVertex(); v != null; v = nextVertex(v)) {
				if(v.value.equals(value)) {
					return v;
				}
			}	
			return null;
		}
		
		public int num_vertexes() {
			int num = 0;
			for(GraphVertex v = this.firstVertex(); v != null; v = nextVertex(v)) {
				num ++;
			}
			return num;
		}
		
		/*
		 * premesti clanove jedne grupe u drugu grupu
		*/
		public void change_groups(int id1, int id2) {
			for(GraphVertex iter = this.fVertex; iter != null; iter = this.nextVertex(iter)) {
				if(iter.id_group == id2) {
					iter.id_group = id1;
				}
			}	
		}
		
		/*
		 * na pocetku je svako svoja grupa, spajamo ih dok 
		 * ne dodjemo do zeljenog broja grupa.
		 */
		public void make_group(List<Edge> edges_list, int num_groups) {	
		
			for(int ii = 0; ii < vertices.length; ii++) {
				vertices[ii].id_group = ii;
			}

			this.cur_groups = vertices.length;
			
			while (!edges_list.isEmpty() && this.cur_groups != num_groups) {
				Edge cur_edge = edges_list.get(0);
				edges_list.remove(0);
				
				GraphVertex v1 = cur_edge.start_vertex;
				GraphVertex v2 = cur_edge.end_vertex;
				
				//oba su vec smestena u grupu, ako su razlicite grupe, spajamo grupe
				if(v1.id_group != v2.id_group) {
					int free_idx = v2.id_group;
					this.change_groups(v1.id_group, free_idx);
					this.cur_groups --; //brisemo v2 grupu
				}
			}
		}
		
		public String write_groups(GraphVertex[] vertices) {
			//postavljamo da nijedan node nije posecen jos uvek
			for(int i = 0; i < vertices.length; i++) {
				vertices[i].visited = false;
			}
			
			/*
			 * ako zovemo vise puta funkciju, dodavace se u groups
			 * zato sada ponovo inicijaliziramo
			 */
			this.groups = new ArrayList<>();
		
			for(int i = 0; i < vertices.length; i++) {
				GraphVertex iter = vertices[i];
				
				if(!iter.visited) {
					List<Integer> group = new ArrayList<>();
					group.add((Integer)iter.value);
					iter.visited = true;
					for(int j = i + 1; j < vertices.length; j++) {
						GraphVertex iter2 = vertices[j];
						if(iter.id_group == iter2.id_group) {
							group.add((Integer) iter2.value);
							iter2.visited = true;
						}
					}
				
					this.groups.add(group);
				}
			}
			
			
			StringBuilder sb = new StringBuilder();
			for(int i = 0; i < this.groups.size(); i++) {
				for(int j = 0; j < this.groups.get(i).size(); j++) {
					sb.append(this.groups.get(i).get(j));
					if(j != this.groups.get(i).size() - 1)
						sb.append(",");
				}
				sb.append("\n");
			}
			return sb.toString();
		}
	}
	
	
	public static double distance (GraphVertex v1, GraphVertex v2) {
		return Math.sqrt(Math.pow(v1.x - v2.x, 2) + Math.pow(v1.y - v2.y, 2));
	}

	static GraphVertex[] vertices ;
	
	public static void main(String[] args) {
		
		long start2 = System.currentTimeMillis(); 
		
		try(FileReader reader = new FileReader(args[0]);
				BufferedReader br = new BufferedReader(reader)){
				
			try(FileWriter writer = new FileWriter(args[1]);
				BufferedWriter bw = new BufferedWriter(writer)){
					
				int num_ver = Integer.parseInt(br.readLine());
				
				DirectedGraph graph = new DirectedGraph();
				vertices = new GraphVertex [num_ver];
				
				double[][] coordinates = new double [num_ver][2];
				
				for(int i = 0; i < num_ver; i++) {
					String vrstica = br.readLine();
					String str[] = vrstica.split(",");
					
					coordinates[i][0] = Double.parseDouble(str[0]);
					coordinates[i][1] = Double.parseDouble(str[1]);
					
					vertices[i] = new GraphVertex(i + 1, coordinates[i][0], coordinates[i][1]);
					graph.insertVertex(vertices[i]);
				}
				
				List<Edge> edges_list = new ArrayList<>();
				//kreiranje grafa
				for(int i = 0; i < num_ver - 1; i++) {
					for(int j = i + 1; j < num_ver; j++) {
						
						double edge_value = distance(vertices[i], vertices[j]);
						graph.insertEdge(vertices[i], vertices[j], edge_value);
						graph.insertEdge(vertices[j], vertices[i], edge_value);
						
						Edge edge = new Edge(edge_value, vertices[i], vertices[j]);
						edges_list.add(edge);
					}
				}
			
				edges_list.sort(null);			
				
				int num_groups = Integer.parseInt(br.readLine());
				
				graph.make_group(edges_list, num_groups);
				bw.write(graph.write_groups(vertices));
				
				
				bw.close(); writer.close();		
				
				} catch(IOException e) {
					System.err.format("IOException: %s%n", e);
				}		
				
			br.close(); reader.close();
			} catch(IOException e) {
				System.err.format("IOException: %s%n", e);
			}		
		
		long end2 = System.currentTimeMillis();
		System.out.println(end2 - start2);
	}
}
