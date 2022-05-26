package it.polito.tdp.yelp.model;

public class TestModel {

	public static void main(String[] args) {
		Model m = new Model() ;
//		double start = System.currentTimeMillis();
		String msg = m.creaGrafo(200, 2007);
//		double end = System.currentTimeMillis();
		System.out.println(msg);
//		System.out.format("Tempo impiegato: %.2fs", (end-start)/1000);
	}

}
