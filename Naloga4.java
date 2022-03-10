import java.util.*;
import java.lang.*;
import java.io.*;

public class Naloga4 {
	
	//FIFO VRSTA MAX DUZINE///////////////////////////////
	public static class Queue {
		
		//NODE ////////////////////////
		public static class Node {
			int id;
			Node next;
					
			Node (int id, Node next){	
				this.id = id;
				this.next = next;
			}
					
			int getID() {
				return this.id;
			}
		}
		/////////////////////////////		
				
		private Node bnode;
		private Node enode;
		private int cur_size;
		private int max_size;
				
		public Queue(int max_size) {
			this.bnode = null;
			this.enode = null;
			this.cur_size = 0;
			this.max_size = max_size;
		}
				
		public int getSize() {
			return this.cur_size;
		}
				
		//mozemo da dodamo samo na kraj, vraca true ako je uspesno
		public boolean add(int value) {
			if(this.cur_size == this.max_size)
				return false;
					
			if(this.bnode == null) {
				this.bnode = new Node(value, null);
				this.enode = this.bnode;
			} else {
				Node node2 = new Node(value, null);
				this.enode.next = (Node) node2;
				this.enode = node2;
			}
					
			this.cur_size ++;
			return true;
		}
				
		//mozemo da brisemo samo sa pocetka
		public void remove () {
			if(this.cur_size > 1) {
				this.bnode = this.bnode.next;
				this.cur_size --;
			} else if(this.cur_size == 1) {	
				this.bnode = null;
				this.enode = null;
				this.cur_size --;
			}
		}
				
		public int getFirst() {
			return this.bnode.id;
		}
				
		public String toString() {
			StringBuilder sb = new StringBuilder("");
			if(this.bnode == null)
				return "\n";
			Node pnode = this.bnode;
			while(pnode != this.enode) {
				sb.append(pnode.getID() + ", ");
				pnode = pnode.next;
			} sb.append(this.enode.getID() + "\n");
			return sb.toString();
		}	
	}
			
	//QUEUE kraj ///////////////////////////////////////////
				
				
	public static void main(String[] args) {
		
		try(FileReader reader = new FileReader(args[0]);
				BufferedReader br = new BufferedReader(reader)){
			
			//UCITAVANJE////////////////////////////////////////////////
			int num_steps = Integer.parseInt(br.readLine()); //broj koraka simulacije
			int num_chairs = Integer.parseInt(br.readLine()); //broj stolica u cekaonici
			int cut_time = Integer.parseInt(br.readLine()); //vreme sisanja na pocetku
			int add_time = Integer.parseInt(br.readLine()); //vreme, koje se pridoda posle svake stranke
			
			String line = br.readLine();
			String[] str = line.split(",");
			int[] period_arr = new int [str.length];
			int sum_arr = 0;
			for(int i = 0; i < str.length; i++) {
				period_arr[i] = Integer.parseInt(str[i]);
				sum_arr += period_arr[i];
			}
			
			line = br.readLine();
			str = line.split(",");
			int[] wait_time = new int [str.length];
			for(int i = 0; i < str.length; i++) {
				wait_time[i] = Integer.parseInt(str[i]);
			}
			//kraj UCITAVANJA/////////////////////////////////////////////////////////////////////////////
			
			//maksimalan moguci broj stranaka
			int max_cust = ((num_steps / sum_arr) + 1) * period_arr.length;
			//vremena dolaska svake stranke
			int[] arrivals = new int[max_cust]; int a = period_arr.length;
			//vreme strpljivosti svake stranke
			int[] patience = new int[max_cust]; int p = wait_time.length;
			
			int cur_arrival_time = 0;
			//u nizove arrivals i patience pamtimo u kom trenutku ta osoba dolazi i koliko je spremna da ceka
			for(int i = 1; i < max_cust; i++) {
				cur_arrival_time += period_arr[(i - 1) % a];
				arrivals[i] = cur_arrival_time;
				patience[i] = wait_time[(i - 1) % p];
			}
			
			//u niz vrsta q pamtimo red za svaki casovni trenutak
			Queue[] q = new Queue [num_steps + 1];
			for(int i = 0; i <= num_steps; i++) {
				q[i] = new Queue(num_chairs);
			}
			
	  		int time = arrivals[1]; //pocetni trenutak
			StringBuilder res = new StringBuilder("");
			
			int remaining_cutting_time = 0;
			
			
			// pocetak ITERACIJA PO VREMENU ///////////////////////////////////////////////////////////////////
			while (time + cut_time <= num_steps) {
				int id_arriving = Arrays.binarySearch(arrivals, time);
				
				if(remaining_cutting_time == 0) {
					if(q[time - 1].getSize() != 0) {
						int id_fc = q[time - 1].getFirst();
						res.append(Integer.valueOf(id_fc).toString() + ",");
						remaining_cutting_time = cut_time;
						cut_time += add_time;
						
						//sklonimo onog koji se sisa i dodamo novog
						for(int ii = arrivals[id_fc]; (ii < arrivals[id_fc] +  patience[id_fc]) &&  (ii <= num_steps); ii++) {
							q[ii].remove();
						}
						
						for(int ii = 0; id_arriving > 0 && ii < patience[id_arriving] && (time + ii <= num_steps); ii++) {
							q[time + ii].add(id_arriving);
						}
						
					} else if(id_arriving > 0) {
						res.append(Integer.valueOf(id_arriving).toString() + ",");
						remaining_cutting_time = cut_time;
						cut_time += add_time;
					
					}
				} else if(id_arriving > 0 && q[time].add(id_arriving)) {
						for(int ii = 1; ii < patience[id_arriving] && (time + ii <= num_steps); ii++) {
							q[time + ii].add(id_arriving);
						}
				}
				
				if(remaining_cutting_time > 0)
					remaining_cutting_time --;
			
				time++;	
			}
			// kraj ITERACIJA PO VREMENU ///////////////////////////////////////////////////////////////////
			res.deleteCharAt(res.toString().length() - 1);
			
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
