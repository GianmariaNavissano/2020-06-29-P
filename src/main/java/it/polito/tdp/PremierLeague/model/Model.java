package it.polito.tdp.PremierLeague.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.event.ConnectedComponentTraversalEvent;
import org.jgrapht.event.EdgeTraversalEvent;
import org.jgrapht.event.TraversalListener;
import org.jgrapht.event.VertexTraversalEvent;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;

import it.polito.tdp.PremierLeague.db.PremierLeagueDAO;

public class Model {
	
	private Graph<Match, DefaultWeightedEdge> grafo;
	private PremierLeagueDAO dao;
	private Map<Integer, Match> idMap;
	private Map<Match, MatchWithWeight> predecessori;
	private List<Match> percorso;
	private int pesoMax;
	
	public Model() {
		this.dao = new PremierLeagueDAO();
	}
	
	
	public void creaGrafo(String mese, int minutes) {
		this.grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		this.idMap = this.dao.getMatchesByMonth(this.getMese(mese));
		
		//aggiungo i vertici
		Graphs.addAllVertices(this.grafo, this.idMap.values());
		
		//aggiungo gli archi
		for(Adiacenza a : this.dao.getAdiacenze(minutes, idMap)) {
			Graphs.addEdgeWithVertices(this.grafo, a.getM1(), a.getM2(), a.getPeso());
		}
		
		
	}
	
	public int numVertici() {
		return this.grafo.vertexSet().size();
	}
	public int numArchi() {
		return this.grafo.edgeSet().size();
	}
	
	public String getMaxConnessione() {
		int pesoMax = 0;
		List<Adiacenza> res = new LinkedList<>();
		for(DefaultWeightedEdge e : this.grafo.edgeSet()) {
			if(this.grafo.getEdgeWeight(e)>pesoMax) {
				pesoMax = (int)this.grafo.getEdgeWeight(e);
				res.clear();
				res.add(new Adiacenza(this.grafo.getEdgeSource(e), this.grafo.getEdgeTarget(e), pesoMax));
			} else if(this.grafo.getEdgeWeight(e)==pesoMax) {
				//se sono uguali aggiungo alla lista SENZA PRIMA SVUOTARLA
				res.add(new Adiacenza(this.grafo.getEdgeSource(e), this.grafo.getEdgeTarget(e), pesoMax));
			}
		}
		
		String result = "";
		for(Adiacenza a : res) {
			result += a.getM1()+" "+a.getM2()+" "+a.getPeso()+"\n";
		}
		return result;
	}
	
	public Map<Integer, Match> getMatches(){
		return idMap;
	}
	
	public List<Match> getCollegamento(Match partenza, Match arrivo){
		
		//ottengo tutti i match raggiungibili da quello di partenza
		this.setPredecessori(partenza);

		//con un metodo ricorsivo vado a selezionare il cammino di
		//peso massimo che rispetti i vincoli del problema
		this.pesoMax = 0;
		List<Match> parziale = new LinkedList<>();
		parziale.add(partenza);
		this.cerca(parziale, 0, arrivo);
		
		return percorso;
	}
	
	public void setPredecessori(Match partenza) {
		BreadthFirstIterator<Match, DefaultWeightedEdge> bfi = new BreadthFirstIterator<>(this.grafo, partenza);
		
		//creo la mappa che conterrà per ogni match il suo predecessore
		this.predecessori = new HashMap<>();
		//partenza non ha predecessori
		predecessori.put(partenza, null);
		
		bfi.addTraversalListener(new TraversalListener<Match, DefaultWeightedEdge>(){

			@Override
			public void connectedComponentFinished(ConnectedComponentTraversalEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void connectedComponentStarted(ConnectedComponentTraversalEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void edgeTraversed(EdgeTraversalEvent<DefaultWeightedEdge> e) {
				

				DefaultWeightedEdge arco = e.getEdge();
				Match m1 = grafo.getEdgeSource(arco);
				Match m2 = grafo.getEdgeTarget(arco);
				int weight = (int)grafo.getEdgeWeight(arco);
				
				if(predecessori.containsKey(m1) && !predecessori.containsKey(m2)) {
					predecessori.put(m2, new MatchWithWeight(m1, weight));
				} else{ if(predecessori.containsKey(m2) && !predecessori.containsKey(m1)) {
					predecessori.put(m1, new MatchWithWeight(m2, weight));
				}}
				
			}

			@Override
			public void vertexTraversed(VertexTraversalEvent<Match> e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void vertexFinished(VertexTraversalEvent<Match> e) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		while(bfi.hasNext())
			bfi.next();
		
	}
	
	public void cerca(List<Match> parziale, int peso, Match arrivo) {
		//condizione terminale:
		//se l'ultimo elemento di parziale è quello di arrivo
		if(parziale.get(parziale.size()-1).equals(arrivo)) {
			//e se è una soluzione a peso massimo
			if(peso>this.pesoMax) {
				pesoMax = peso;
				this.percorso = new LinkedList<>(parziale);
			}
			return;
		}
		
		for(Match m : this.predecessori.keySet()) {
			//aggiungo a parziale questo match, se l'ultimo aggiunto è un suo predecessore
			//e se questo match non fa già parte della soluzione parziale (no cicli)
			if(!parziale.contains(m) && parziale.get(parziale.size()-1).equals(predecessori.get(m).getM())) {
				
				int idH1 = m.getTeamHomeID();
				int idA1 = m.getTeamAwayID();
				int idH2 = predecessori.get(m).getM().getTeamHomeID();
				int idA2 = predecessori.get(m).getM().getTeamAwayID();
				if((idH1==idH2 && idA1==idA2) || (idH1==idA2 && idA1==idH2)) {
					//non rispetta le condizioni del problema
				}else {
					parziale.add(m);
					this.cerca(parziale, peso + this.predecessori.get(m).getWeight(), arrivo);
					
					//backtracking
					parziale.remove(m);
				}
				
			}
		}
		
		
	}
	
	
	
	public int getMese(String mese) {
		if(mese.equals("Gennaio"))
			return 1;
		if(mese.equals("Febbraio"))
			return 2;
		if(mese.equals("Marzo"))
			return 3;
		if(mese.equals("Aprile"))
			return 4;
		if(mese.equals("Maggio"))
			return 5;
		if(mese.equals("Giugno"))
			return 6;
		if(mese.equals("Luglio"))
			return 7;
		if(mese.equals("Agosto"))
			return 8;
		if(mese.equals("Settembre"))
			return 9;
		if(mese.equals("Ottobre"))
			return 10;
		if(mese.equals("Novembre"))
			return 11;
		if(mese.equals("Dicembre"))
			return 12;
		return 0;
	}
	
}
