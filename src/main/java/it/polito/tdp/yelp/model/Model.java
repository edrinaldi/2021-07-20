package it.polito.tdp.yelp.model;

import java.util.ArrayList;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.yelp.db.YelpDao;

public class Model {
	
	Graph<User, DefaultWeightedEdge> grafo;
	YelpDao dao;
	List<User> utenti;
	
	public Model() {
		this.dao = new YelpDao();
	}
	
	public String creaGrafo(int minRevisioni, int anno) {
		double start = System.currentTimeMillis();	// tempo
		this.grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		this.utenti = this.dao.getUsersWithReviews(minRevisioni);
		Graphs.addAllVertices(this.grafo, this.utenti);
		
		for(User u1 : this.utenti) {
			for(User u2 : this.utenti) {
//				DefaultWeightedEdge edge = this.grafo.getEdge(u2, u1);
				if(!u1.equals(u2) && 
						u1.getUserId().compareTo(u2.getUserId()) < 0) {
					int similarita = this.dao.calcolaSimilarita(u1, u2, anno);
					if(similarita > 0) {
						Graphs.addEdge(this.grafo, u1, u2, similarita);
					}
				}
			}
		}
		double end = System.currentTimeMillis();	// tempo
		System.out.format("Tempo impiegato: %.2fs\n", (end-start)/1000);	// tempo

		System.out.format("# vertici: %d\n", this.grafo.vertexSet().size());
		System.out.format("# archi: %d\n", this.grafo.edgeSet().size());
		return String.format("Creato grafo con %d vertici e %d archi", 
				this.grafo.vertexSet().size(), this.grafo.edgeSet().size());
	}
	
	public List<User> getUsers() {
		return this.utenti;
	}
	
	public List<User> getUtentiSimili(User utente) {
		List<User> simili = new ArrayList<>();
		int pMax = 0;
		List<User> vicini = Graphs.neighborListOf(this.grafo, utente);
		for(DefaultWeightedEdge e : this.grafo.edgesOf(utente)) {
			// ho un arco
			int peso = (int)this.grafo.getEdgeWeight(e);
			if(peso > pMax) {
				pMax = peso;
			}
		}
		for(DefaultWeightedEdge e : this.grafo.edgesOf(utente)) {
			int peso = (int)this.grafo.getEdgeWeight(e);
			if(peso == pMax) {
				User u2 = Graphs.getOppositeVertex(this.grafo, e, utente);
				simili.add(u2);
			}
		}
		System.out.println("Lista dei simili: " + simili);
		return simili;
	}
	
}
