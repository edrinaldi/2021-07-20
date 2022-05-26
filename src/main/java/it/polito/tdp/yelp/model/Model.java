package it.polito.tdp.yelp.model;

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
		
		System.out.format("# vertici: %d\n", this.grafo.vertexSet().size());
		System.out.format("# archi: %d\n", this.grafo.edgeSet().size());
		return String.format("Creato grafo con %d vertici e %d archi", 
				this.grafo.vertexSet().size(), this.grafo.edgeSet().size());
	}
	
	public List<User> getUsers() {
		return this.utenti;
	}
	
}
