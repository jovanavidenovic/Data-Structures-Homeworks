import java.util.*;
import java.lang.*;
import java.io.*;


public class Naloga7 {
	
	public static class GraphVertex{
		
		Object value;
		GraphEdge firstEdge;
		GraphVertex nextVertex;
		int num_connections;
		int distance;
		boolean visited;
		GraphVertex parent;
		int transfers;
		Map <Integer, Integer> plines2distance;	
		
		public GraphVertex(Object val) {
			value = val;
			firstEdge = null;
			nextVertex = null;
			this.num_connections = 0;
			this.distance = 0;
			this.transfers = 0;
			this.plines2distance = new HashMap<>();
			this.parent = null;
			this.visited = false;
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
		
		
		public void makenull(){
			fVertex = null;
			this.min_distance = 0;
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
				System.out.print(v + " dist " + v.distance + " transf " + v.transfers + ": ");
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
		 *  za minimalni broj stanica - sve konekcije su nam jednake!
		 *  ne treba nam da eksplicitno trazimo minimum jer svakako idemo "po sirini"
		 *  u jednom trenutku gledamo samo one koji su na istom rastojanju od pocetne stanice
		 */
		public int chain (int start, int end){
			
			GraphVertex station1 = this.locateVertex(start);
			GraphVertex station2 = this.locateVertex(end);
			
			for(GraphVertex iter = this.firstVertex(); iter != null; iter = this.nextVertex(iter)) {
				iter.visited = false;
				iter.distance = 0;
			}	
			
			station1.visited = true;
			station1.parent = null;
			station1.distance = 0;
			
			Queue <GraphVertex> q = new ArrayDeque<>();
			//current node and successor
			GraphVertex v, w;
			GraphEdge e; // current connection
			
			q.add(station1);
			while(!q.isEmpty()) {
				v = q.poll();
				e = this.firstEdge(v);
					
				while(e != null) {
					w = this.endPoint(e);
					
					if(!w.visited) {
						w.visited = true;
						w.parent = v;
						w.distance = v.distance + 1;
						q.add(w);
					}
					
					e = this.nextEdge(v, e);
				}	
			}
		
			if(station2.visited) {
				this.min_distance = station2.distance;
				return station2.distance;
			} else return -1;
		}
		
		public GraphVertex getPriority (Set<GraphVertex> gf) {
			GraphVertex ver_min_transfers = null;
			int min_transfers = this.num_vertexes();
			for(GraphVertex ver : gf) {
				if(ver.transfers < min_transfers || ver_min_transfers == null) {
					ver_min_transfers = ver;
					min_transfers = ver.transfers;
				} else if(ver.transfers == min_transfers && ver.distance < ver_min_transfers.distance) {
					ver_min_transfers = ver;
					min_transfers = ver.transfers;
				}
			}
			return ver_min_transfers;
		}
		
		public void dijkstra(int start) {
			
			GraphVertex station1 = this.locateVertex(start);
			
			for(GraphVertex iter = this.firstVertex(); iter != null; iter = this.nextVertex(iter)) {
				iter.visited = false;
				iter.parent = null;
				iter.distance = 0;
				iter.transfers = 0;
			}
			
			Set<GraphVertex> set_ver = new HashSet<GraphVertex>();
			
			GraphVertex v, w; //curent and next vertex
			int previous_line = -1;
			
			station1.visited = true;
			station1.parent = null;
			station1.distance = 0;
			
			set_ver.add(station1);
			
			while(!set_ver.isEmpty()) {
				v = getPriority(set_ver);
			//	System.out.println(v.value);
				set_ver.remove(v);
				
				for(GraphEdge connection = this.firstEdge(v); connection != null; connection = this.nextEdge(v, connection)) {
					w = this.endPoint(connection);
					int cur_line = (int) connection.evalue; 
					
					if(!w.visited) {
						w.visited = true;
						w.parent = v;
						
						if(v.plines2distance.isEmpty() || v.plines2distance.keySet().contains(cur_line)) {
							w.transfers = v.transfers;
							if(v.plines2distance.isEmpty())
								w.distance = v.distance + 1;
							else w.distance = v.plines2distance.get(cur_line).intValue() + 1;	
						} else {
							w.transfers = v.transfers + 1;
							w.distance = v.distance + 1;
						}
						
						w.plines2distance.clear();
						w.plines2distance.put(cur_line, w.distance);
						
						set_ver.add(w);
					
					} else {
						int new_w_transfers;
						
						if(v.plines2distance.isEmpty() || v.plines2distance.keySet().contains(cur_line))  {
							new_w_transfers = v.transfers;
							
							if (new_w_transfers < w.transfers) {
								w.transfers = new_w_transfers;
								w.parent = v;
								w.distance = v.plines2distance.get(cur_line) + 1;
								w.plines2distance.clear();
								w.plines2distance.put(cur_line, w.distance);
								
							} else if (new_w_transfers == w.transfers  && (v.plines2distance.get(cur_line) + 1) < w.distance){
								w.transfers = new_w_transfers;
								w.parent = v;
								w.distance =v.plines2distance.get(cur_line) + 1;
								w.plines2distance.put(cur_line, w.distance);
							} else if (new_w_transfers == w.transfers) {
								w.plines2distance.put(cur_line, v.plines2distance.get(cur_line) + 1);
							}
							
						} else {
							new_w_transfers = v.transfers + 1;
							if (new_w_transfers < w.transfers) {
								w.transfers = new_w_transfers;
								w.parent = v;
								w.distance = v.distance + 1;
								w.plines2distance.clear();
								w.plines2distance.put(cur_line, w.distance);
								
							} else if (new_w_transfers == w.transfers  && (v.distance + 1) < w.distance){
								w.transfers = new_w_transfers;
								w.parent = v;
								w.distance = v.distance + 1;
								w.plines2distance.put(cur_line, w.distance);
							} else if (new_w_transfers == w.transfers) {
								w.plines2distance.put(cur_line, v.distance + 1);
							}
						}
					}
				}		
			}
				
		}
		
		// funkcija koja proverava da li su 2 stanice DIREKTNO povezane i vraca broj linije ako su povezani, -1 ako nisu
		public int check_connection(GraphVertex v1, GraphVertex v2) {
			for(GraphEdge connection = this.firstEdge(v1); connection != null; connection = this.nextEdge(v1, connection)) {
				if(this.endPoint(connection).equals(v2))
					return (int) connection.evalue;
			}		
			return -1;
		}
		
		
		public int getTransfer(int end) {
			GraphVertex station2 = this.locateVertex(end);
			return station2.transfers;
		}
		
		public int getOptimal(int end) {
			GraphVertex station2 = this.locateVertex(end);
			System.out.println(station2.distance + " " + this.min_distance);
			if(station2.distance == this.min_distance)
				return 1;
			else return 0;
		}
		
		
		public void ispisiPut(int end) {
			GraphVertex station2 = this.locateVertex(end);
			while(station2 != null) {
				System.out.print(station2 + "--> ");
				station2 = station2.parent;
			}
			
		}
	}

	
	public static void main(String[] args) {
		
		try(FileReader reader = new FileReader(args[0]);
				BufferedReader br = new BufferedReader(reader)){
				
					try(FileWriter writer = new FileWriter(args[1]);
							BufferedWriter bw = new BufferedWriter(writer)){
						
						int num_lines = Integer.parseInt(br.readLine());
						int num_ver = 0;
						String vrstica;
						int[][] lines = new int[num_lines][];
						
						DirectedGraph graph = new DirectedGraph();
						
						for(int i = 0; i < num_lines; i++) {
							vrstica = br.readLine();
							
							String str[] = vrstica.split(",");
							lines[i] = new int[str.length];
							
							for(int ii = 0; ii < lines[i].length; ii++) {
								lines[i][ii] = Integer.parseInt(str[ii]); 
								if(lines[i][ii] > num_ver)
									num_ver = lines[i][ii];
							}
						}
						
						String sp_ep = br.readLine();
						String[] sp_ep_split = sp_ep.split(",");
						
						int start = Integer.parseInt(sp_ep_split[0]);
						int end = Integer.parseInt(sp_ep_split[1]);
						
						//da li su direktno povezani
						boolean directly_connected = false;
						
						GraphVertex[] vertices = new GraphVertex [num_ver];
						
						for (int i = 0; i < vertices.length; i++){
							vertices[i] = new GraphVertex(Integer.valueOf(i + 1));
							graph.insertVertex(vertices[i]);
						}
						
						for(int i = 0; i < num_lines; i++) {
							for(int j = 0; j < lines[i].length - 1; j++) {
								int s = lines[i][j];
								int e = lines[i][j + 1];
								
								if((s == start && e == end) || (s == end && e == start))
									directly_connected = true;
								
								graph.insertEdge(vertices[s - 1], vertices[e - 1], i + 1);
								graph.insertEdge(vertices[e - 1], vertices[s - 1], i + 1);
							}
						}
					
				//		graph.print();
							
						int num_transfers; int num_stations; int optimal;
						
						if(start == end) {
							num_transfers = 0;
							num_stations = 0;
							optimal = 1;
						} else {
							num_transfers = 0;
							num_stations = graph.chain(start, end);
							optimal = 0;
							
							if(num_stations == -1) {
								num_transfers = -1;
								optimal = 0;
							} else {
								graph.dijkstra(start);
								num_transfers = graph.getTransfer(end);
								optimal = graph.getOptimal(end);
							}
						}
						
						bw.write(num_transfers + "\n");
						bw.write(num_stations + "\n");
						bw.write(optimal + "\n");
					/*	
						graph.print();
						System.out.println();
						
						graph.ispisiPut(end);
						System.out.println();
					*/	
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
