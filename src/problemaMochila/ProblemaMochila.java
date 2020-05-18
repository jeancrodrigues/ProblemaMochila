package problemaMochila;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class ProblemaMochila {
	
	private int mutacoes = 0;
	
	private static int[] seeds = {2022103589,217281671,-1776858665,-219196422,567960174,-1912844119,1053746065,-2006722950,862013082,1480641398};

	private static boolean PENALIZACAO = true;
	
	private static int GERACOES = 500;
	private static int TAMANHO_POPULACAO = 1000;
	
	private static int QUANTIDADE_ITENS = 42;	
	private static int CAPACIDADE_MOCHILA = 120;
	
	private static int[] PESOS = {3, 8, 12, 2, 8, 4, 4, 5, 1, 1, 8, 6, 4, 3, 3, 5, 7, 3, 5, 7, 4, 3, 7, 2, 3, 5, 4, 3, 7, 19, 20, 21, 11, 24, 13, 17, 18, 6, 15, 25, 12, 19};
	private static int[] VALORES = {1, 3, 1, 8, 9, 3, 2, 8, 5, 1, 1, 6, 3, 2, 5, 2, 3, 8, 9, 3, 2, 4, 5, 4, 3, 1, 3, 2, 14, 32, 20, 19, 15, 37, 18, 13, 19, 10, 15, 40, 17, 39};
	
	public static void main(String[] args) {		
		ProblemaMochila p = new ProblemaMochila();
		p.rodar();
	}
	
	public void rodar() {
		double probabilidadeMutacao = 0.05;
		double probabilidadeCrossover = 0.8;
		int tamanhoPopulacao = TAMANHO_POPULACAO;
		int i =0 ;
		//for (int i = 0 ; i < seeds.length ; i++) {
			mutacoes=0;
			Random random = new Random(seeds[i]);
			
			List<Mochila> populacao = geraPopulacao(random,tamanhoPopulacao);
			int fitnessTotal = 0;
			
			for (int j = 1; j < GERACOES; j++) {
				fitnessTotal = 0;
			
				List<Mochila> filhos = cruzamento(populacao, random, probabilidadeCrossover);
				mutacao(filhos, random, probabilidadeMutacao);
				
				populacao.addAll(filhos);
				populacao = sobrevivencia(populacao, tamanhoPopulacao);
				
				for (Mochila mochila : populacao) {			
					//System.out.println(mochila + "" + mochila.fitness());
					fitnessTotal += mochila.fitness();
				}
				System.out.println(j + " " + fitnessTotal/populacao.size() + " " + populacao.get(0) + "\n");				
				
			}
		//}
		
	}
	
	private List<Mochila> sobrevivencia(List<Mochila> populacao, int tamanhoPopulacao) {
			if( PENALIZACAO ) {
				Collections.sort(populacao, (a,b) -> b.fitnessPenalizacao() - a.fitnessPenalizacao());				
			} else {
				Collections.sort(populacao, (a,b) -> b.fitnessAdaptacao() - a.fitnessAdaptacao());
			}
						
		return populacao.subList(0, tamanhoPopulacao - 1);
	}

	private void mutacao(List<Mochila> filhos, Random random, double probabilidadeMutacao) {
		for (Mochila mochila : filhos) {
			for (int i = 0; i < mochila.itens.length; i++) {
				if(random.nextDouble() <= probabilidadeMutacao) {
					mochila.itens[i] = ! mochila.itens[i];
					mutacoes++;
				}
			}
			mochila.calcula();
		}
	}

	public List<Mochila> cruzamento(List<Mochila> populacao, Random random, double probabilidadeCrossover) {
		List<Mochila> roleta = new ArrayList<>();
		List<Mochila> pares = new ArrayList<>();		
		List<Mochila> filhos = new ArrayList<>();
		
		roleta.addAll(populacao);
				
		for (int i = 0; i < populacao.size(); i++) {
			int sorteado = sorteioRoleta(roleta, random);			
			pares.add(roleta.remove(sorteado));
		}
		
		for (int i = 0; i < pares.size() - 1; i+=2) {
			double probabilidade = random.nextDouble();
			if(probabilidade <= probabilidadeCrossover) {
				filhos.addAll( crossover(random, pares.get(i), pares.get(i+1)) );				
			}
		}
		return filhos;
	}
	
	public int sorteioRoleta(List<Mochila> roleta, Random random) {
		double[] p = new double[roleta.size()];
		int total = 0;
		for (Mochila mochila : roleta) {
			total += mochila.fitness();
		}
		
		for (int i = 0; i < roleta.size(); i++) {
			double fitness = roleta.get(i).fitness() ;
			p[i] = fitness / total;
		}
		
		double r = random.nextDouble();
		
		double soma = p[0];
		
		int i = 0;
		while(soma < r && i < roleta.size() - 1) {
			soma += p[i++];
		}
		return i;
	}
	
	public List<Mochila> geraPopulacao(Random random, int tamanhoPopulacao){
		List<Mochila> mochilas = new ArrayList<>();
		for (int i = 0; i<tamanhoPopulacao ; i++) {
			mochilas.add(geraIndividuo(random));
		}
		return mochilas;		
	}
	
	public Mochila geraIndividuo(Random random) {
		Mochila mochila = new Mochila();		
		for (int i = 0; i < QUANTIDADE_ITENS; i++) {			
			mochila.itens[i] = random.nextBoolean();			
		}		
		mochila.calcula();		
		return mochila;
	}
	
	public List<Mochila> crossover(Random random, Mochila mochila1, Mochila mochila2){
		int pontoCrossover = random.nextInt(QUANTIDADE_ITENS);
		
		Mochila filho1 = new Mochila();
		Mochila filho2 = new Mochila();
		
		for(int i = 0; i < pontoCrossover; i++) {
			filho1.itens[i] = mochila1.itens[i];
			filho2.itens[i] = mochila2.itens[i];
		}
				
		for(int i = pontoCrossover; i < QUANTIDADE_ITENS; i++) {
			filho1.itens[i] = mochila2.itens[i];
			filho2.itens[i] = mochila1.itens[i];
		}
		
		filho1.calcula();
		filho2.calcula();
		
		List<Mochila> filhos = new ArrayList<>();
		filhos.add(filho1);
		filhos.add(filho2);
		return filhos;
	}
	
	class Mochila {
		public boolean[] itens = new boolean[QUANTIDADE_ITENS];
		public int valorTotal =0;
		public int pesoTotal=0;
		public int quantidade = 0;
		
		public void calcula() {
			quantidade = 0;
			valorTotal = 0;
			pesoTotal = 0;
			for (int i = 0; i < QUANTIDADE_ITENS; i++) {				
				if(itens[i]) {
					quantidade++;
					valorTotal += VALORES[i];
					pesoTotal += PESOS[i];
				}				
			}			
		}
		
		public int fitness() {
			return valorTotal - Math.abs(CAPACIDADE_MOCHILA-pesoTotal);
		}
		
		public int fitnessAdaptacao() {
			if(pesoTotal > CAPACIDADE_MOCHILA) {				
				while(pesoTotal > CAPACIDADE_MOCHILA) {
					int maior = 0;
					int index = 0;
					for (int i = 0; i < QUANTIDADE_ITENS; i++) {
						if(itens[i] && PESOS[i] > maior) {
							maior = PESOS[i];
							index = i;							
						}						
					}					
					itens[index] = false;
					calcula();					
				}				
			}	
			return fitness();			
		}
		
		public int fitnessPenalizacao() {
			int penalizacao = 0;
			if(pesoTotal > CAPACIDADE_MOCHILA) {
				Mochila mochilaNova = clone();
				mochilaNova.fitnessAdaptacao();
				penalizacao = valorTotal - mochilaNova.valorTotal;
			} 			
			return fitness() - penalizacao;						
		}
				
		public String toString() {
			String str = quantidade + "," + pesoTotal + "," + valorTotal + ",";			
			for (boolean b : itens) {
				str+= ( b ? "1" : "0" ) + ",";
			}
			return str;
		}
		
		public Mochila clone() {
			Mochila mochila = new Mochila();
			for (int i = 0; i < QUANTIDADE_ITENS; i++) {				
				mochila.itens[i] = itens[i];	
			}
			mochila.calcula();
			return mochila;
		}
	}

}
