import java.lang.*;
import java.io.*;
import java.util.*;

public class Naloga1 {
	
	//proveravamo da li je polje unutar okvira
	public static boolean validnoPolje (int i, int j, int n) {
		return (i >= 0 && j >= 0 && i < n && j < n);
	}
	
	//proveravamo da li je to kretanje pravilno
	public static boolean validnoKretanje (int pokret) {
		return (20 >= pokret && pokret >= - 30); 
	}
	
	//zelimo da u rezultatu imamo samo uspone
	public static int sumirajKretanja(int k1, int k2) {
		if(k1 >= 0 && k2 >= 0)
			return k1 + k2;
		if(k1 >= 0 && k2 <= 0)
			return k1;
		if(k1 <= 0 && k2 >= 0)
			return k2;
		return 0;
	}
	
	/*
	proverimo sva moguca polja, ali ako dobijemo da je negde okej, vracamo tu vrednost
	i tada prekidamo dalji rad funkcije --> tako smanjujemo casovnu zahtevnost
	svaki put kada prodjemo neko polje zabelezimo u prosli[i][j] = true
	medjutim, kad zavrsimo rekurziju za to polje vratimo na false
	*/
	
	
	public static int plantaze(int i, int j, int[][] a, int n, boolean[][] prosli, int[][] memo, int[] elems, int kk) {
		
	//	System.out.println(i + " " + j);
		if(memo[i][j] != -21234){
			if(memo[i][j] > 0)
				return memo[i][j];
			else if(memo[i][j] == -311)
				return -311;
			else return 0;
		}
	
		if(i == n - 1 && j == n - 1) { //dosli smo do jugoistocnog polja
			return 0;		
		} 
		
		elems[kk] = a[i][j];
		
		int res = 0;
		for(int k = 0; k < 4; k++) {
			int trenKretanje = 0;
			int daljeKretanje = 0;
			int ii = i;
			int jj = j;
			switch (k) {
				case 0:
					ii = i + 1;
					jj = j;
					break;
				case 1:
					ii = i;
					jj = j + 1;
					break;
				case 2: 
					ii = i - 1;
					jj = j;
					break;
				case 3: 
					ii = i;
					jj = j - 1;
					break;
				
			}
			
			if (validnoPolje(ii, jj, n)) {
				if(!prosli[ii][jj]){
					trenKretanje = a[ii][jj] - a[i][j];
					prosli[ii][jj] = true;
				
					if(validnoKretanje(trenKretanje)) {
						daljeKretanje = plantaze(ii, jj, a, n, prosli, memo, elems, kk + 1);
						if(daljeKretanje != -311) {
							memo[i][j] = sumirajKretanja(trenKretanje, daljeKretanje);
					//		prosli[ii][jj] = false;
							return memo[i][j];
						}
					}
					prosli[ii][jj] = false;
				}
			}
		}
		
		//nismo uspeli da pronadjemo pravi put
		memo[i][j] = -311;
		return memo[i][j];
	}
	
	public static void main (String[] args) {
		int rez = -311;
		try(FileReader reader = new FileReader(args[0]);
				BufferedReader br = new BufferedReader(reader)){
			int n = Integer.parseInt(br.readLine());
			int[][]  a = new int[n][n];
			String vrstica; int ii = 0;
			int[][] memo = new int[n][n];
			boolean[][] prosli = new boolean[n][n];
			
			
			while  ((vrstica = br.readLine()) != null) { 
				String[] s = vrstica.split(",");
				for(int j = 0; j < n; j++) {
					a[ii][j] = Integer.parseInt(s[j]);
					memo[ii][j] = -21234;
					prosli[ii][j] = false;
				}
				ii++;
			}
			
			int[] elems = new int[n*n + n];
			
			prosli[0][0] = true; //pocetak kretanja, uvek ga prolazimo
			rez = plantaze(0, 0, a, n, prosli, memo, elems, 0);
		
			br.close(); reader.close();
			
		} catch(IOException e) {
			System.err.format("IOException: %s%n", e);
		}
		
		
		try(FileWriter writer = new FileWriter(args[1]);
				BufferedWriter bw = new BufferedWriter(writer)){
			bw.write(Integer.toString(rez));
			bw.close(); writer.close();		
		} catch(IOException e) {
			System.err.format("IOException: %s%n", e);
		}		
	}
	
}
