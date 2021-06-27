package it.polito.tdp.PremierLeague.model;

public class MatchWithWeight {
	
	private Match m;
	private int weight;
	
	MatchWithWeight(Match m, int weight) {
		super();
		this.m = m;
		this.weight = weight;
	}
	public Match getM() {
		return m;
	}
	public void setM(Match m) {
		this.m = m;
	}
	public int getWeight() {
		return weight;
	}
	public void setWeight(int weight) {
		this.weight = weight;
	}
	
	

}
