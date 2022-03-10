import java.io.*;
import java.util.*;
import java.lang.*;

public class Naloga6 {
	
	static int i = 0;
	
	static class LENode {
		String key;
		LENode left;
		LENode right;
		
		public LENode(String k) {
			key = k;
			left = null;
			right = null;
		}
	}
	
	static class LETree {
		LENode root;
		
		public LETree() {
			this.root = null;
		}
		
		public int height() {
			return this.height(this.root);
		}
		
		private int height(LENode node) {
			if(node == null)
				return 0;
			else return Math.max(this.height(node.left), this.height(node.right)) + 1;	
		}
		
		public void leToTree(String[] str) {
			this.root = expression(str);
		}
		
		public String printPreorder() {	
			StringBuilder sb = new StringBuilder();
			this.printPreorder(this.root, sb);
			sb.deleteCharAt(sb.length() - 1);
			return sb.toString();
		}
		
		private void printPreorder(LENode node, StringBuilder sb) {
			if(node == null)
				return; 
			
			if(isRBracket(node.key)) {
				int idx = node.key.indexOf(')');
				node.key = node.key.substring(0, idx);
			}
			
			sb.append(node.key + ",");
			
			this.printPreorder(node.left, sb);
			this.printPreorder(node.right, sb);
		}
		
		public LENode expression(String[] str) {
			if(i >= str.length) 
				return null;
			
			LENode root = prior1(str);
			if(i < str.length && isOR(str[i])) {
				return or(str, root);
			} else return root;
		
		}
		
		public LENode prior1(String[] str) {
			if(i >= str.length)
				return null;
		
			LENode root = value(str);
			
			if(i < str.length && isAND(str[i])) {
				return and(str, root); 
			} else return root;
		}
		
		public LENode and(String[] str, LENode left) {
			if(i >= str.length)
				return null;
			LENode root = new LENode("AND");
			root.left = left;
			i++;
			
			if (isRBracket(str[i])) {
				root.right = value(str);
				return root;
			} else {
				root.right = value(str);
				if(i < str.length && isAND(str[i])) {
					return and(str, root);
				} else return root;
			}			
			
		}
		
		public LENode or(String[] str, LENode left) {
			if(i >= str.length)
				return null;
			LENode root = new LENode("OR");
			root.left = left;
			i++;
			
			if (isRBracket(str[i])) {
				root.right = value(str);
				return root;
			} else {
				root.right = prior1(str);
				if(i < str.length && isOR(str[i])) {
					return or(str, root);
				} else return root;
			}
		}
		
		/*
		 * kada naidjemo na NOT, ubacujemo ga u drvo dok ne naidjemo
		 * do sledeceg stringa koji nije NOT.
		 * Na taj string se onda odnosi NOT i ubacimo ga kao list.
		 */
		public LENode not(String[] str) {
			if(i >= str.length)
				return null;
			
			LENode root = new LENode("NOT");
			i++;
			while(str[i].equals("NOT")) {	
				LENode temp = new LENode("NOT");
				temp.left = root;
				root = temp;
				i++;
			}
			
			LENode iter = root;
			while(iter.left != null) {
				iter = iter.left;
			}
			
			//negiramo expression, koji stoji po NOT-ovima.
			iter.left = value(str);
			
			return root;
		}
		
		
		public LENode value(String[] str) {
			LENode node = null;
			if(i >= str.length)
				return null;
			
			if(operand(str[i])) {
				node = new LENode(str[i]);
				i++;
			} else if (isNOT(str[i])) {
				node = not(str);
			} else if (isLBracket(str[i])) {
				str[i] = str[i].substring(1);
				node = expression(str);
				if(!isRBracket(str[i])) {
					i++;
				} else value(str);
			} else if (isRBracket(str[i])) {
				str[i] = str[i].substring(0, str[i].length() - 1);
				//if(!isRBracket(str[i]))
					node = new LENode(str[i]);
			}
			
			return node;
		}
		
	}
	
	private static boolean isAND(String s) {
		return s.equals("AND");
	}
	
	private static boolean isOR(String s) {
		return s.equals("OR");
	}
	
	private static boolean isNOT(String s) {
		return s.equals("NOT");
	}
	
	private static boolean isLBracket(String s) {
		return s.charAt(0) == '(';
	}
	
	private static boolean isRBracket(String s) {
		return s.charAt(s.length() - 1) == ')';
	}
	
	private static boolean operand(String s) {
		return !(isAND(s) || isOR(s) || isNOT(s) || isLBracket(s) || isRBracket(s));
	}

	
	public static void main (String[] args) {
		
		try(FileReader reader = new FileReader(args[0]);
			BufferedReader br = new BufferedReader(reader)){
			

				try(FileWriter writer = new FileWriter(args[1]);
						BufferedWriter bw = new BufferedWriter(writer)){
					String vrstica = br.readLine();
					vrstica.replace(' ',',');
					System.out.println(vrstica);
					String[] str = vrstica.split(" ");
					LETree tree = new LETree();
					tree.leToTree(str);
					bw.write(tree.printPreorder());
					bw.write('\n');
					bw.write(String.valueOf(tree.height()));
				
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
