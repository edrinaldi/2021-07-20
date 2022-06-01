package it.polito.tdp.yelp.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;

import it.polito.tdp.yelp.model.Event.EventType;

public class Simulatore {	// IL TRUCCO E' SPEZZARE IL CODICE. NELLA PARTE DI ALTO LIVELLO
							// SI DEVE VEDERE SOLO LA LOGICA, IL MECCANISMO RIENTRA NELLE 
							// FUNZIONI IN BASSO
	// coda degli eventi
	private Queue<Event> queue;
	
	// parametri della simulazione
	private int x1;	// giornalisti (<< x2 nel controller)
	private int x2;	// utenti da intervistare (< # vertici nel controller)
	private double probSuccesso;	
	private double probFerie;
	private double probFail;
	
	// output della simulazione
	private List<Giornalista> giornalisti;
	private int numeroGiorni;

	// modello del mondo
	private Set<User> intervistati;
	private Graph<User, DefaultWeightedEdge> grafo;
	
	public Simulatore(Graph<User, DefaultWeightedEdge> grafo) {
		this.grafo = grafo;
		this.probSuccesso = 0.6;	// assegnato un nuovo utente per il giorno dopo
		this.probFerie = 0.2;	// assegnato un nuovo utente per 2 giorni dopo
		this.probFail = 0.2;	// riassegnato lo stesso utente il giorno dopo
	}
	
	public void init(int x1, int x2) {	// non metto x1, x2 nel costruttore per poter usare
										// la classe con piu' simulazioni diverse
		this.queue = new PriorityQueue<>();
		this.x1 = x1;
		this.x2 = x2;
		this.intervistati = new HashSet<User>();
		this.numeroGiorni = 0;
		this.giornalisti = new ArrayList<Giornalista>();
		for(int id=0;id<this.x1;id++) {
			this.giornalisti.add(new Giornalista(id));
		}
		// carico la coda
		for(Giornalista g : this.giornalisti) {
			User intervistato = this.selezionaIntervistato(this.grafo.vertexSet());
			this.intervistati.add(intervistato);
			g.incrementaNumIntervistati();
			this.queue.add(new Event(1, EventType.DA_INTERVISTARE, intervistato, g));
		}
	}
	
	public void run() {
		while(!this.queue.isEmpty() && this.intervistati.size() < x2) {
			Event e = this.queue.poll();
			this.numeroGiorni = e.getGiorno();	// l'ultimo estratto sara' il valore finale
			this.processEvent(e);
			
		}
	}

	private void processEvent(Event e) {
		// TODO Auto-generated method stub
		switch(e.getType()) {
		case DA_INTERVISTARE:
			double caso = Math.random();
			if(caso < this.probSuccesso) {
				// punto I.
				User vicino = this.selezionaAdiacente(e.getIntervistato());
				if(vicino == null ) {
					vicino = this.selezionaIntervistato(this.grafo.vertexSet());
				}
				this.queue.add(new Event(e.getGiorno()+1, EventType.DA_INTERVISTARE,
						vicino, e.getGiornalista()));
				this.intervistati.add(vicino);
				e.getGiornalista().incrementaNumIntervistati();
			}
			else if(caso < this.probSuccesso+this.probFerie /* 0.8 */) {
				// punto II.
				this.queue.add(new Event(e.getGiorno()+1, EventType.FERIE,
						e.getIntervistato(), e.getGiornalista()));
			}
			else {
				// caso III.
				this.queue.add(new Event(e.getGiorno()+1, EventType.DA_INTERVISTARE,
						e.getIntervistato(), e.getGiornalista()));
			}
		case FERIE:
			// TODO
		}
	}

	public int getX1() {
		return x1;
	}

	public void setX1(int x1) {
		this.x1 = x1;
	}

	public int getX2() {
		return x2;
	}

	public void setX2(int x2) {
		this.x2 = x2;
	}

	public List<Giornalista> getGiornalisti() {
		return giornalisti;
	}

	public int getNumeroGiorni() {
		return numeroGiorni;
	}
	
	/**
	 * Seleziona un intervistato dalla 'lista' evitando di selezionare quelli gia' in 
	 * this.intervistati
	 * @param lista
	 * @return l'User selezionato
	 */
	private User selezionaIntervistato(Collection<User> lista) {
		// trasformo 'lista' in un set per fare la differenza tra insiemi (inutile: si
		// poteva fare anche con una List)
		Set<User> candidati = new HashSet<User>(lista);
		
		// elimino quelli gia' intervistati
		candidati.removeAll(this.intervistati);
		
		// scelgo il candidato
		int scelto = (int)(Math.random()*candidati.size());
		return (new ArrayList<User>(candidati)).get(scelto);
	}
	
	private User selezionaAdiacente(User u) {
		// TODO Auto-generated method stub
		List<User> vicini = Graphs.neighborListOf(this.grafo, u);
		vicini.removeAll(this.intervistati);
		if(vicini.size() == 0) {
			// vertice isolato oppure tutti gli adiacenti già intervistati
			return null;
		}
		double max = 0;
		for(User v : vicini) {
			double peso = this.grafo.getEdgeWeight(this.grafo.addEdge(u, v));
			if(peso > max) {
				max = peso;
			}
		}
		List<User> migliori = new ArrayList<User>();
		for(User v : vicini) {
			double peso = this.grafo.getEdgeWeight(this.grafo.addEdge(u, v));
			if(peso == max) {
				migliori.add(v);
			}
		}
		int scelto = (int)(Math.random()*migliori.size());
		return migliori.get(scelto);
	}
}
