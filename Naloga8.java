import java.util.*;
import java.lang.*;
import java.io.*;

public class Naloga8 {
	
	static class Node {
		int id;
		int value;
		int x;
		int y;
		Node left;
		Node right;
		
		public Node(int id, int value) {
			this.id = id;
			this.value = value;
			this.x = 0;
			this.y = 0;
			this.left = null;
			this.right = null;
		}
	}

	static class Tree {
		Node root;
		int i;
		
		public Tree() {
			makenull();
		}
		
		public void makenull() {
			this.root = null;
			this.i = 0;
		}
		
		public int num_elems () {
			return this.num_elems(this.root);
		}
		
		public int num_elems (Node node) {
			if(node == null)
				return 0;
			
			int num = 1;
			num += num_elems(node.left) + num_elems(node.right);
			
			return num;
		}
		
		public void createFromArray(int[][] data_tree, Map<Integer, Integer> id2index, int id_root) {
			this.root = this.createFA(data_tree, id2index, id_root, 0);
			this.add_inorder();
		}
		
		
		public Node createFA(int[][] data_tree, Map<Integer, Integer> id2index, int cur_id, int depth) {		
			int index = id2index.get(cur_id);
			
			Node node = new Node(data_tree[index][0], data_tree[index][1]);
			node.y = depth;
			
			if(data_tree[index][2] != -1)
				node.left = this.createFA(data_tree, id2index, data_tree[index][2], depth + 1);
			
			if(data_tree[index][3] != -1)
				node.right = this.createFA(data_tree, id2index, data_tree[index][3], depth + 1);
			
			return node;
		}
		
		public void add_inorder() {	
			this.add_inorder(this.root);
		}
				
		private void add_inorder(Node node) {
			if(node == null)
				return; 
				
			this.add_inorder(node.left);
			node.x = this.i;
			this.i++;
			this.add_inorder(node.right);
		}
		
		public String description(Node node) {
			StringBuilder sb = new StringBuilder();
			sb.append(node.value + "," + node.x + "," + node.y); 
			return sb.toString();
		}
		
		public String printLvlLike() {	
			StringBuilder sb = new StringBuilder();
		
			List<Node> nodes_list = new ArrayList<Node>();
			nodes_list.add(this.root);
			int from = 0;
			int to = 1;
			this.fillList(nodes_list, from, to);
			
			for(int i = 0; i < nodes_list.size(); i++) {
				sb.append(this.description(nodes_list.get(i)) + "\n");
			}
			
			return sb.toString();		
		}
					
		//nivojski zapis, ki uporablja vrsto
		private void fillList(List<Node> nodes_list, int from, int to) {		
			if(from == to)
				return;
			
			int added = 0;
			
			for(int ii = from; ii < to; ii++) {
				Node node = nodes_list.get(ii);
				if(node.left != null) {
					nodes_list.add(node.left);
					added++;
				}
				
				if(node.right != null) {
					nodes_list.add(node.right);
					added++;
				}
			}
			
			from = to;
			to += added;
			
			this.fillList(nodes_list, from, to);
		}
	}	
	
	public static void main (String[] args) {
		
		try(FileReader reader = new FileReader(args[0]);
				BufferedReader br = new BufferedReader(reader)){
				
					try(FileWriter writer = new FileWriter(args[1]);
							BufferedWriter bw = new BufferedWriter(writer)){
						
						int num_nodes = Integer.parseInt(br.readLine());
						String vrstica;						
						int[][] data_tree = new int [num_nodes][4];
						Map<Integer, Integer> id2index = new TreeMap<>();
						
						int i = 0;
						
						// sacuvamo id node-a u nizu i na osnovu tog niza kasnije pravimo drvo
						// Node[] array_nodes = new Node[num_nodes];
						
						while  ((vrstica = br.readLine()) != null) { 
							String[] str = vrstica.split(",");
							for(int ii = 0; ii < 4; ii++) {
								data_tree[i][ii] = Integer.parseInt(str[ii]);
							}
							
							id2index.put(data_tree[i][0], i);
							i++;
						}
						
						i = 0;
					
						//moramo da nadjemo koren drveta --> to je id koji se nigde ne ponavlja
						int id_root = -1;
						for(i = 0; i < num_nodes && id_root == -1; i++) {
							int wanted_id = data_tree[i][0];
							for(int j = 0; j < num_nodes; j++) {
								if(data_tree[j][2] == wanted_id || data_tree[j][3] == wanted_id) {
									break;
								} 
								
								//dosli smo do zadnjeg node-a i ni njegov sin nije wanted_id
								if(j == num_nodes - 1) {
									id_root = wanted_id;
								}
							}
						}					
						
						Tree tree = new Tree();
		
						tree.createFromArray(data_tree, id2index, id_root);

						bw.write(tree.printLvlLike());
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
