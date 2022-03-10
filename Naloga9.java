import java.util.*;
import java.lang.*;
import java.io.*;

public class Naloga9 {
	
public static class GraphVertex{
		
		Comparable value;
		GraphEdge firstEdge;
		GraphVertex nextVertex;
		int num_connections;
		int distance;
		boolean visited;
		GraphVertex parent;
		int num_passengers;
		
		public GraphVertex(Comparable val) {
			value = val;
			firstEdge = null;
			nextVertex = null;
			this.num_connections = 0;
			this.distance = 0;
			this.parent = null;
			this.visited = false;
			this.num_passengers = 0;
		}
		
		public String toString(){
			return value.toString();
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
		

	public static class DirectedGraph {

		protected GraphVertex fVertex;
		private int min_distance;
		Map<List<Integer>, Integer> road2pass;
		List<List<Integer>> max_pass;
		int max_passenegers;
		
		public DirectedGraph() {
			this.makenull();
		}
		
		public void makenull(){
			fVertex = null;
			this.min_distance = 0;
			this.road2pass = new HashMap<>();
			this.max_pass = new ArrayList<>();
			this.max_passenegers = 0;
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
		
		public void print()
		{
			for (GraphVertex v = firstVertex(); v != null; v = nextVertex(v)) 
			{
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
		
		
		public void chain(int starting_point, int end_point){
			
			GraphVertex start = this.locateVertex(starting_point);
			GraphVertex end = this.locateVertex(end_point);
			
			if(start == null || end == null)
				return;
			
			boolean found = false;
			
			for(GraphVertex iter = this.firstVertex(); iter != null; iter = iter.nextVertex) {
				iter.visited = false;
				iter.distance = 0;
				iter.parent = null;
			}	
			
			start.visited = true;
			start.parent = null;
			start.distance = 0;
			
			
			Queue <GraphVertex> q = new ArrayDeque<>();
			//current node and successor
			GraphVertex v, w;
			GraphEdge e; // current connection
			
			q.add(start);
			while(!q.isEmpty() && !found) {
				v = q.poll();
				
				//list za konekcije v-a
				List<Integer> connections = new ArrayList<>();
				
				e = this.firstEdge(v);
				while(e != null) {
					connections.add((int) e.endVertex.value);	
					e = e.nextEdge;
				}
				
				connections.sort(null);
				
				for(int i = 0; i < connections.size() && !found; i++) {
					w = this.locateVertex(connections.get(i));
					if(!w.visited) {
						w.visited = true;
						w.parent = v;
						w.distance = v.distance + 1;
						q.add(w);
						if(w.equals(end))
							found = true;
					}
				}
			}
		}
		
		
		public GraphVertex getPriority (Set<GraphVertex> gf) {
			GraphVertex ver_min = null;
			for(GraphVertex ver : gf) {
				if(ver_min == null || ver.value.compareTo(ver_min.value) < 0) {
					ver_min = ver;
				}
			}
			return ver_min;
		}
		
		public void update_passengers(int end, int num) {
			GraphVertex station2 = this.locateVertex(end);
			
			int previous = -1;
			while(station2 != null) {
				if(previous == -1) {
					previous = (int) station2.value;
				} else {
					List<Integer> short_road = new ArrayList<>();
					short_road.add(previous);
					short_road.add((int) station2.value);

					short_road.sort(null);
					
					int new_pass = this.road2pass.get(short_road) + num;
					this.road2pass.put(short_road, new_pass);
					
					if(this.max_pass.isEmpty() || new_pass > this.max_passenegers) {
						this.max_pass.clear();
						this.max_pass.add(short_road);
						this.max_passenegers = new_pass;
					} else if (new_pass == this.max_passenegers) {
						int i = 0;
						for(i = 0; i < this.max_pass.size(); i++) {
							List<Integer> element = this.max_pass.get(i);
							if(short_road.get(0) < element.get(0) || (short_road.get(0) == element.get(0) && short_road.get(1) < element.get(1)))
								break;
						}
						this.max_pass.add(i, short_road);
					}
					
					previous = (int) station2.value;
				}
				
				station2 = station2.parent;
			}	
		}
		
		
		// the function that checks if 2 vertexes are connected
		public boolean check_connection(GraphVertex v1, GraphVertex v2) {
			for(GraphEdge connection = this.firstEdge(v1); connection != null; connection = connection.nextEdge) {
				if(this.endPoint(connection).equals(v2))
					return true;
			}			
			return false;
		}
	}

	
	public static void main(String[] args) {
		
		try(FileReader reader = new FileReader(args[0]);
				BufferedReader br = new BufferedReader(reader)){
				
			try(FileWriter writer = new FileWriter(args[1]);
				BufferedWriter bw = new BufferedWriter(writer)){
						
				int num_ver = 0;
				String vrstica = br.readLine();
				String s[] = vrstica.split(",");
				int num_edges = Integer.parseInt(s[0]);
				int m = Integer.parseInt(s[1]);
				
				//oznacava konekcije izmedju mesta
				int[][] roades = new int[num_edges][2];
				
				//oznacava puteve koje ljudi prelaze
				int[][] num_pass = new int [m][3];
						
				for(int i = 0; i < num_edges; i++) {
					vrstica = br.readLine();
							
					String str[] = vrstica.split(",");
					roades[i][0] = Integer.parseInt(str[0]);
					roades[i][1] = Integer.parseInt(str[1]); 
					
					if(roades[i][1] > num_ver)
						num_ver = roades[i][1];
				}	
				
				DirectedGraph graph = new DirectedGraph();
				GraphVertex[] vertices = new GraphVertex [num_ver];
				
				for (int i = 0; i < vertices.length; i++){
					vertices[i] = new GraphVertex(Integer.valueOf(i + 1));
					graph.insertVertex(vertices[i]);
				}
				
				//kreiranje grafa
				for(int i = 0; i < roades.length; i++) {
					int start = roades[i][0]; int end = roades[i][1];
					graph.insertEdge(vertices[start - 1], vertices[end - 1], i + 1);
					graph.insertEdge(vertices[end - 1], vertices[start - 1], i + 1);
					
					List<Integer> road = new ArrayList<>();
					road.add(roades[i][0]);
					road.add(roades[i][1]);
					
					road.sort(null);
					graph.road2pass.put(road, 0);
				}
							
				for(int i = 0; i < m; i++) {
					vrstica = br.readLine();
					
					String str[] = vrstica.split(",");
					num_pass[i][0] = Integer.parseInt(str[0]);
					num_pass[i][1] = Integer.parseInt(str[1]);
					num_pass[i][2] = Integer.parseInt(str[2]);
					
					graph.chain(num_pass[i][0], num_pass[i][1]);
					graph.update_passengers(num_pass[i][1], num_pass[i][2]);
				}
				
				for(int i = 0; i < graph.max_pass.size(); i++) {
					bw.write(String.valueOf(graph.max_pass.get(i).get(0)) + "," + String.valueOf(graph.max_pass.get(i).get(1)) + "\n");
				}	
									
				bw.close(); writer.close();		
				} catch(IOException e) {
					System.err.format("IOException: %s%n", e);
				}		
				
			br.close(); reader.close();
			} catch(IOException e) {
				System.err.format("IOException: %s%n", e);
			}		
	}
	
	
	
}
