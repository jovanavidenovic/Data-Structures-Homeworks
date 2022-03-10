import java.util.*;
import java.lang.*;
import java.io.*;


public class Naloga3{

	// LIST //////////////////////////////////////////////////////////////
	public static class List {
		
		// NODE ///////////////////////////////////////////////////////////////////////////////////////// 
		//za vsaki clen seznama - staticna polja velikosti n
		private static class Node {
			Integer[] elems;
			int cur_elems;
			int num_places;
			Node next;
		
			Node (int n){
				this.num_places = n;
				this.elems = new Integer[this.num_places];
				this.cur_elems = 0;
				this.next = null;
			}
		
			Node(int n, Node next){
				this.num_places = n;
				this.elems = new Integer[this.num_places];
				this.cur_elems = 0;
				this.next = next;
			}
		
			boolean full() {
				return this.cur_elems == this.num_places;
			}
		
			int getCurElem() {
				return this.cur_elems;
			}
			
			boolean insert(int v, int p) {
				if(this.full())
					return false;
			
				for(int i = this.cur_elems - 1; i >= p; i --) {
					this.elems[i + 1] = this.elems[i];
				}
			
				this.elems[p] = v;
				this.cur_elems ++;
				return true;
			}
		
			//organizovanje po raspadu
			void separateNode(Node node2) {
				int j = 0;
				for(int i = num_places/2; i < num_places; i++) {
					node2.elems[j] = this.elems[i];
					this.elems[i] = null;
					this.cur_elems --; 
					node2.cur_elems ++;
					j++;
				}
			}
		
			void remove (int p) {
				for(int i = p; i < this.cur_elems - 1; i++) {
					this.elems[i] = this.elems[i + 1];
				}
				this.elems[this.cur_elems - 1] = null;
				this.cur_elems--;
			}
		
			void getEnough(Node node2) {
				int i = this.cur_elems;
				int max = this.num_places;
				while(node2.cur_elems > 0 && i < max / 2) {
					this.insert(node2.elems[0], i);
					node2.remove(0);
					i++;
				}
			}
		
			void getAll(Node node2) {
				
				while(node2.cur_elems > 0) {
					this.insert(node2.elems[0], this.cur_elems);
					node2.remove(0);
				}
			}
		
			String toStr() {
				StringBuilder sb = new StringBuilder("");
				for(int i = 0; i < this.num_places; i++) {
					if(elems[i] == null) {
						sb.append("NULL");
					} else sb.append(elems[i].toString());
					if(i < this.num_places - 1)
						sb.append(",");
				}	
				return sb.toString();
			}
		}
		// KRAJ NODE //////////////////////////////////////////////////////////////////////////////////
		
		//pocetni node
		private Node node;
		private int num_nodes;
		private int capacity_node;
		private int num_elems;
	
		public List() {
			this.init(5);
		}
	
		public void init (int n) {
			this.node = new Node(n);
			this.num_nodes = 1;
			this.capacity_node = n;
			this.num_elems = 0;
		}
	
	
		public boolean insert (int v, int p) {
			if(p < 0 || p > this.num_elems)
				return false;
		
			boolean uspesno = false;
		
			//1. slucaj
			int i = 0; int inNode_num = 0; int node_num = 0;
			Node cur_node = this.node;

			while(cur_node != null) {
				node_num ++;
				int cn_num_elems = cur_node.getCurElem();
				if(i + cn_num_elems >= p) {
					inNode_num = p - i;
					break;
				} else i += cn_num_elems;
				cur_node = cur_node.next;
			}

			uspesno = cur_node.insert(v, inNode_num);
		
			//2. slucaj --> zeljeni node je full
			if(!uspesno) {
				if(cur_node.next != null && inNode_num == this.capacity_node) {
					uspesno = cur_node.next.insert(v, 0);
					if(!uspesno) {
						Node node2 = new Node(capacity_node, cur_node.next.next); //node, koji dodajemo
						cur_node.next.separateNode(node2);
						cur_node.next.next = (Node) node2;
						this.num_nodes ++;
						return this.insert(v, p);
					}
				} else if(!uspesno) { //jos uvek nismo uspesno dodali --> idemo na 3. slucaj
					Node node2 = new Node(capacity_node, cur_node.next); //node, koji dodajemo
				
					cur_node.separateNode(node2);
					cur_node.next = (Node) node2;
					this.num_nodes ++;
					return this.insert(v, p);
				}
			}
	
			this.num_elems ++;
			return uspesno;
		}	
	
		public boolean remove (int p) {
			if(p < 0 || p >= this.num_elems)
				return false;
		
			int i = 0; int inNode_num = 0; int node_num = 0;
			Node cur_node = this.node;

			while(cur_node != null) {
				node_num ++;
				int cn_num_elems = cur_node.getCurElem();
				if(i + cn_num_elems > p) { //ovde je >, u insertu je >=
					inNode_num = p - i;
					break;
				} else i += cn_num_elems;
				cur_node = cur_node.next;
			}
		
			cur_node.remove(inNode_num);
		
			if(cur_node.getCurElem() < this.capacity_node / 2 && cur_node.next != null) {
				cur_node.getEnough(cur_node.next);
				if(cur_node.next.getCurElem() < this.capacity_node / 2) {
					cur_node.getAll(cur_node.next);
					cur_node.next = cur_node.next.next;
					this.num_nodes --;
				}
			}	
		
			this.num_elems --;
			return true;
		}
		
		
		public String toString() {
			StringBuilder sb = new StringBuilder("");
			sb.append(num_nodes + "\n");
			Node cur_node = this.node;
				for(int i = 0; i < num_nodes; i++) {
					sb.append(cur_node.toStr());
					cur_node = cur_node.next;
					if(i != num_nodes - 1)
						sb.append("\n");
				}
			return sb.toString();
		}
	}
	
// KRAJ LIST ///////////////////////////////////////////////////////////////////////////	
	
	public static void main (String[] args) {
		long startTime = System.nanoTime();
		try(FileReader reader = new FileReader(args[0]);
				BufferedReader br = new BufferedReader(reader)){
			List res = new List();
			
			int num_steps = Integer.parseInt(br.readLine());
			String vrstica;
			
			while  ((vrstica = br.readLine()) != null) { 
				String[] s = vrstica.split(",");
				if(s[0].equals("s")) {
					int n = Integer.parseInt(s[1]);
					res.init(n);
				}
				if(s[0].equals("i")) {
					int v = Integer.parseInt(s[1]);
					int p = Integer.parseInt(s[2]);
					res.insert(v,  p);
				}
				if(s[0].equals("r")) {
					int poz = Integer.parseInt(s[1]);
					res.remove(poz);
				}
			}
			
			try(FileWriter writer = new FileWriter(args[1]);
					BufferedWriter bw = new BufferedWriter(writer)){			
				bw.write(res.toString());
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
