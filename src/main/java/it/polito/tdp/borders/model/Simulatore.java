package it.polito.tdp.borders.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;

public class Simulatore {

	//Modello --> qual è lo stato del sistema ad ogni passo
	private Graph<Country, DefaultEdge> graph;
	
	//Tipi di evento --> coda prioritaria
	private PriorityQueue<Evento> queue;
	
	//Parametri della simulazione
	private int N_MIGRANTI = 1000;
	private Country partenza;
	
	//Valori in output
	private int T = -1; 
	private Map<Country, Integer> stanziali;
	
	public void init(Country country, Graph<Country, DefaultEdge> grafo) {
		this.partenza=country;
		this.graph=grafo;
		
		this.T=1;
		this.stanziali=new HashMap<>();
		for (Country c : grafo.vertexSet()) {
			stanziali.put(c, 0);
		}
		
		//creo la coda
		this.queue = new PriorityQueue<>();
		//inserisco il primo evento
		this.queue.add(new Evento(T, partenza, N_MIGRANTI));
	}
	
	public void run() {
		//finchè la coda non si svuota prendo un evento per volta e lo eseguo
		
		Evento e;
		while ((e=queue.poll()) != null) {
			//simulo l'evento e
			this.T = e.getT();
			int nPersone = e.getN();
			Country stato = e.getCountry();
			
			//ottengo i vicini di stato
			List<Country> vicini = Graphs.neighborListOf(this.graph, stato);
			
			int migrantiPerStato = (nPersone/2)/vicini.size();
			if(migrantiPerStato > 1) {
				//le persone si possono muovere
				for(Country confinante : vicini) {
					queue.add(new Evento(e.getT()+1, confinante, migrantiPerStato));
				}
			}
			int stanziali = nPersone - migrantiPerStato*vicini.size();
			this.stanziali.put(stato, this.stanziali.get(stato)+stanziali);
		}
	}
	
	public Map<Country, Integer> getStanziali() {
		return this.stanziali;
	}
	
	public Integer getT() {
		return this.T;
	}
}
