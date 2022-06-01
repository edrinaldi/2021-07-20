package it.polito.tdp.yelp.model;

public class Giornalista {
	private int id;	// giornalista identificato da num compreso tra 0 e x1-1
	private int numIntervistati;
	public Giornalista(int id) {
		super();
		this.id = id;
		this.numIntervistati = 0;
	}
	public int getId() {
		return id;
	}
	public int getNumIntervistati() {
		return numIntervistati;
	}
	public void incrementaNumIntervistati() {
		this.numIntervistati++;
	}
	
}
