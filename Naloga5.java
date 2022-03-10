import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

public class Naloga5 {
	
	public static int xor_sum(int[] groups) {
		int sum = groups[1];
		for (int i = 2; i < groups.length; i++) {
			sum = sum ^ groups[i]; 
		}
		return sum;
	}
	
	//pocetak MAP///////////////////////////////////////////////////
	public static class Array2int{
			
		public static class Node{
			int[] key;
			int value;
			Node next;
				
			Node (int[] k, int value, Node next){
				this.key = new int [k.length];
				for(int i = 0; i < k.length; i++) {
					this.key[i] = k[i];
				}
				this.value = value;
				this.next = next;
			}	
		}
			
		private Node data[];
			
		public Array2int(int length_table) {
			this.data = (Node[]) new Node[length_table];
		}
		
		private static int hc(int[] key) {
			int sum = 0;
			for(int i = 0; i < key.length; i++) {
				sum += key[i];
			}
			return sum;
		}
		
		private int index(int[] key) {
			int n = this.data.length;
		    return ((hc(key) % n) + n) % n;
		}
		
		private static boolean equal(int[] a1, int[] a2) {
			if(a1.length != a2.length)
				return false;
			for(int i = 0; i < a1.length; i++) {
				if(a1[i] != a2[i]) {
					return false;
				}
			}

			return true;
		}
			
		private Node find (int[] key) {
			int index = this.index(key);
			Node node = this.data[index];
				
			while(node != null && !(equal(node.key, key))){
				node = node.next;
			}
			return node;
		}
			
		private int get(int[] key){
			Node node = this.find(key);
			if(node == null) return -100;
			return node.value;
		}
			
		public void addFirst(int[] key, int value) {
			//ubacujemo sortiran niz
			Node node = this.find(key);
			
			if(node == null) {
				int index = this.index(key);
				this.data[index] = new Node(key, value, this.data[index]);
			}
		}	
		
		public void prn() {
			for(int i = 0; i < this.data.length; i++) {
				Node pnode = this.data[i];
				while(pnode!=null) {
					System.out.println(Arrays.toString(pnode.key) + " " + pnode.value);					
					pnode = pnode.next;
				}
				System.out.println();
			}
			
			
		}
		
	}
	//kraj MAP/////////////////////////////////////////
		
		static int count;
		static Array2int array2memo =  new Array2int(17); 
		
		public static boolean allZeros(int[] a) {
			for(int i = 1; i < a.length; i++) {
				if(a[i] != 0)
					return false;
			}
			return true;
		}
		
		//memoizacija mora da ima oznaku ko je na potezu, -10 za 1. -20 za 2., to je prvi clan niza g
		public static int naloga(int[] g) {
			count++;
			//sortiramo niz, jer samo takve zelimo da ubacujemo u map-u
			int[] groups = Arrays.copyOf(g, g.length);
			Arrays.sort(groups);
			int moves = -1;
			
			//nemamo vise kuglica
			if (allZeros(groups)) {
				if(groups[0] == -20) { //1. odigrao poslednji potez
					return 0;
				} 
				if(groups[0] == -10){
					return -1; //ako pobedjuje drugi vracamo -1
				}
			}
			
			
			int findMEMO = array2memo.get(groups);
			if(findMEMO != -100)
				return findMEMO;
			
			for(int i = groups.length - 1; i >= 1; i--) { 
				if(groups[i] != groups[i - 1]) {
					int oldValue = groups[i];
					
					for(int j = 0; j < oldValue; j++)  {
						
						//igrac je uzeo oldValue - j kuglica
						groups[i] = j;	
						
						// 1. optimalno igra, ako ga taj potez ne dovodi do pobede, ne odigra ga
						if(groups[0] == -10 && xor_sum(groups) != 0) {
							continue;
						} 
						
						int cur_moves = -1;
						
						//zamena igraca
						if(groups[0] == -20)
							groups[0] = -10;
						else groups[0] = -20;
						
						findMEMO = array2memo.get(groups);
						
						if(findMEMO == -100) {
							cur_moves = naloga(groups);
						} else cur_moves = findMEMO;
						

						//moramo svaki put da vratimo vrednost igraca na podrazumevanu
						if(groups[0] == -20)
							groups[0] = -10;
						else groups[0] = -20;
						
						//DRUGI sa optimalnim potezima pobedjuje, ali takve igre nas apsolutno ne zanimaju
						if(groups[0] == -20 && cur_moves == -1) {
							groups[i] = oldValue;
							return -1;
						} 
						
						//DRUGI - optimalno igra - da sto duze igra zeli sto vise poteza
						if(groups[0] == -20 && cur_moves > moves) {
							moves = cur_moves; //zelimo da izvucemo najvise poteza koliko nam treba za pobedu
						}
						
						//PRVI - optimalno igra, da sto pre pobedi zeli sto manje poteza
						if(groups[0] == -10 && cur_moves != -1 && (cur_moves < moves || moves == -1)) {
							moves = cur_moves;
						}
					}
					
					groups[i] = oldValue;
				}
			}
	
			if(moves > -1) {
				array2memo.addFirst(groups, 1 + moves);
				return 1 + moves;
			} else {
				array2memo.addFirst(groups, -1);
				return -1;
			}
		}
		
		public static void main(String[] args) {
			
			try(FileReader reader = new FileReader(args[0]);
					BufferedReader br = new BufferedReader(reader)){
				
				int num_groups = Integer.parseInt(br.readLine());
				int[] groups  = new int[num_groups + 1];
				groups[0] = -10; //zapocinje igru prvi
				for(int i = 1; i <= num_groups; i++) {
					groups[i] = Integer.parseInt(br.readLine());
				}
				
				int res = -1;
				if(xor_sum(groups) != 0)
					res = naloga(groups);
				
				try(FileWriter writer = new FileWriter(args[1]);
						BufferedWriter bw = new BufferedWriter(writer)){			
					bw.write(Integer.valueOf(res).toString());
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
