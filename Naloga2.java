import java.lang.*;
import java.io.*;
public class Naloga2 {
	
	public static int brojTacki(String str) {
		int res = 0;
		for(int i = 0; i < str.length(); i++) {
			if(str.charAt(i) == '.') {
				res++;
			}
		}
		return res;
	}
	
	/*
	funkcija koja prvo zameni redosled parnih i neparnih reci
	onda u svakoj reci obrne redosled karaktera
	*/
	public static StringBuilder srediRecenicu(StringBuilder recenica) {
		String[] reci = recenica.toString().split(" ");
		int lng = reci.length;
		for(int i = 0; i < lng; i = i + 2) {
			if(i != lng - 1) {
				String temp = reci[i];
				reci[i] = reci[i + 1];
				reci[i + 1] = temp;
			}
		}
		
		StringBuilder str = new StringBuilder("");
		for(int i = 0; i < reci.length; i++) {
			if(i != reci.length - 1)
				str.append(new StringBuilder(reci[i]).reverse() + " ");
			else str.append(new StringBuilder(reci[i]).reverse()); 
			//kada je zadnja rec u pitanju, ne dodajemo razmak!
		}
		return str;
	}
	
	public static boolean imaVelika(String str) {
		for(int i = 0; i < str.length(); i++) {
			if(Character.isUpperCase(str.charAt(i))) 
				return true;
		}
		return false;
	}
	
	/* 
	funkcija koja podeli paragraf na recenice i vrati StringBuilder niz sa svakom recenicom posebno
	 */
	public static StringBuilder razdeliParagraf(String[] sveReci, int n) {
		StringBuilder[] recenice = new StringBuilder[n];
		for(int ii = 0; ii < n; ii++) {
			recenice[ii] = new StringBuilder("");
		}
		StringBuilder paragraf = new StringBuilder("");
		int i = 0; int lng = sveReci.length;
		int j = 0; //indeks u nizu recenice
		
		while(i < lng) {
			
			if(sveReci[i].charAt(0) == '.') {
				recenice[j].append(sveReci[i]);
				
				if(i == lng - 1) { //poslednja rec paragrafa
					recenice[j] = srediRecenicu(recenice[j]);
					i++;
				} else if(i == lng - 2) { //pretposlednja rec paragrafa
					if(sveReci[i + 1].charAt(0) == '.') { 
						recenice[j] = srediRecenicu(recenice[j]);
						i++; 
					} else {
						recenice[j].append(" ");
						recenice[j].append(sveReci[i + 1]);
						recenice[j] = srediRecenicu(recenice[j]);
						i += 2; 
					}
				} else { // i < lng - 2
					boolean imaVelikaI1 = imaVelika(sveReci[i + 1]);
					boolean imaTackaI1 = sveReci[i + 1].charAt(0) == '.';
					boolean imaVelikaI2 = imaVelika(sveReci[i + 2]);
					boolean imaTackaI2 = sveReci[i + 2].charAt(0) == '.';
					
					
					if(imaVelikaI1 && imaTackaI1) { // sledeca recenica ima samo 1 rec
						recenice[j] = srediRecenicu(recenice[j]);
						i++;
					} else if(imaVelikaI1 && !imaTackaI1) { //sledeca rec pocinje sa velikim slovom, ali nema tacku, dakle za ovu recenicu je	
						recenice[j].append(" "+ sveReci[i + 1]);
						recenice[j] = srediRecenicu(recenice[j]);
						i += 2; 
					} else if(!imaVelikaI1 && imaTackaI1){ //pocetak nove recenice je i + 1
						recenice[j] = srediRecenicu(recenice[j]);
						i++;
					} else {
						if(imaVelikaI2) {
							if(imaTackaI2) {
								recenice[j].append(" " + sveReci[i + 1]);
								recenice[j] = srediRecenicu(recenice[j]);
								i += 2; 
							} else {
								recenice[j] = srediRecenicu(recenice[j]);
								i++;
							} 
						} else {
							recenice[j].append(" " + sveReci[i + 1]);
							recenice[j] = srediRecenicu(recenice[j]);
							i += 2; 
						}
					}
				}
				j++;
			} else {
				recenice[j].append(sveReci[i] + " ");
				i++;
			}
		}
		
		//vrati recenice u normalni redosled
		for(int ii = 0; ii < n / 2; ii++) {
			StringBuilder temp = new StringBuilder(recenice[ii]);
			recenice[ii] = recenice[n - 1 - ii];
			recenice[n - 1 - ii] = temp;
		}
		
		//sklopi paragraf
		for(int ii = 0; ii < n; ii++) {
			if(ii != n - 1)
				paragraf.append(recenice[ii] + " ");
			else paragraf.append(recenice[ii]);
		}
		
		return paragraf;
	}
	
	/*
	funkcija koja podeli tekst na paragrafe
	*/
	public static String[] srediTekst(String str) {
		String[] paragrafi = str.split(","); // \n
		int lng = paragrafi.length;
		if(lng % 2 == 0)
			lng --;
		for(int i = 0; i < lng/2; i = i + 2) {
				String temp = paragrafi[i];
				paragrafi[i] = paragrafi[lng - 1 - i];
				paragrafi[lng - 1- i] = temp;
		}
		return paragrafi;
	}
	
	
	public static void main(String[] args) {
		
		try(FileReader reader = new FileReader(args[0]);
				BufferedReader br = new BufferedReader(reader)){
			
			try(FileWriter writer = new FileWriter(args[1]);
					BufferedWriter bw = new BufferedWriter(writer)){			
				String vrstica; int ii = 0; boolean pocetak = true;
				StringBuilder sb = new StringBuilder("");
				while  ((vrstica = br.readLine()) != null) { 
					if(pocetak) {
						pocetak = false;
					} else sb.append(",");
					sb.append(vrstica);
				}

				String[] paragrafi = srediTekst(sb.toString());
				for(int i = 0; i < paragrafi.length; i++) {
					String str = paragrafi[i];
					int n = brojTacki(str);
					String[] sveReci = str.split(" ");
					StringBuilder rez = new StringBuilder("");
					rez = razdeliParagraf(sveReci, n);
					bw.write(rez.toString());
					if(i != paragrafi.length - 1)
						bw.write("\n");
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
