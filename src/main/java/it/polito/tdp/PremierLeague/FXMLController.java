/**
 * Sample Skeleton for 'Scene.fxml' Controller Class
 */

package it.polito.tdp.PremierLeague;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import it.polito.tdp.PremierLeague.model.Match;
import it.polito.tdp.PremierLeague.model.Model;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class FXMLController {

	Model model;
	private boolean grafoCreato = false;
	
    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="btnCreaGrafo"
    private Button btnCreaGrafo; // Value injected by FXMLLoader

    @FXML // fx:id="btnConnessioneMassima"
    private Button btnConnessioneMassima; // Value injected by FXMLLoader

    @FXML // fx:id="btnCollegamento"
    private Button btnCollegamento; // Value injected by FXMLLoader

    @FXML // fx:id="txtMinuti"
    private TextField txtMinuti; // Value injected by FXMLLoader

    @FXML // fx:id="cmbMese"
    private ComboBox<String> cmbMese; // Value injected by FXMLLoader

    @FXML // fx:id="cmbM1"
    private ComboBox<Match> cmbM1; // Value injected by FXMLLoader

    @FXML // fx:id="cmbM2"
    private ComboBox<Match> cmbM2; // Value injected by FXMLLoader

    @FXML // fx:id="txtResult"
    private TextArea txtResult; // Value injected by FXMLLoader

    @FXML
    void doConnessioneMassima(ActionEvent event) {
    	if(!grafoCreato) {
    		this.txtResult.appendText("è necessario creare il grafo prima di richiedere la connessione massima!\n");
    		return;
    	}
    	this.txtResult.appendText(this.model.getMaxConnessione());
    }

    @FXML
    void doCreaGrafo(ActionEvent event) {
    	this.txtResult.clear();
    	String minStr = this.txtMinuti.getText();
    	int min = 0;
    	try {
    		min = Integer.parseInt(minStr);    		
    	}catch(NumberFormatException e) {
    		this.txtResult.appendText("Errore - i minuti devono essere un numero intero positivo\n");
    		return;
    	}
    	
    	String mese = this.cmbMese.getValue();
    	
    	if(mese==null) {
    		this.txtResult.appendText("Selezionare un mese!\n");
    		return;
    	}
    	this.model.creaGrafo(mese, min);
    	grafoCreato = true;
    	this.txtResult.appendText("Grafo creato con "+this.model.numVertici()+" vertici e "+this.model.numArchi()+" archi.\n");
    	this.cmbM1.getItems().addAll(this.model.getMatches().values());
    	this.cmbM2.getItems().addAll(this.model.getMatches().values());
    	
    }

    @FXML
    void doCollegamento(ActionEvent event) {
    	this.txtResult.clear();
    	if(!grafoCreato) {
    		this.txtResult.appendText("Crea il grafo\n");
    		return;
    	}
    	Match partenza = this.cmbM1.getValue();
    	Match arrivo = this.cmbM2.getValue();
    	if(partenza==null || arrivo==null) {
    		this.txtResult.appendText("Selezionare match di partenza e di arrivo\n");
    		return;
    	}
    	List<Match> percorso = this.model.getCollegamento(partenza, arrivo);
    	if(percorso==null) {
    		this.txtResult.appendText("Non esiste un percorso tra i due match\n");
    	}else {
    		for(Match m : percorso) {
        		this.txtResult.appendText(m+"\n");
        	}
    	}
    	
    	
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert btnCreaGrafo != null : "fx:id=\"btnCreaGrafo\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnConnessioneMassima != null : "fx:id=\"btnConnessioneMassima\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnCollegamento != null : "fx:id=\"btnCollegamento\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtMinuti != null : "fx:id=\"txtMinuti\" was not injected: check your FXML file 'Scene.fxml'.";
        assert cmbMese != null : "fx:id=\"cmbMese\" was not injected: check your FXML file 'Scene.fxml'.";        assert cmbM1 != null : "fx:id=\"cmbM1\" was not injected: check your FXML file 'Scene.fxml'.";
        assert cmbM2 != null : "fx:id=\"cmbM2\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Scene.fxml'.";

    }
    
    public void setModel(Model model) {
    	this.model = model;
    	this.cmbMese.getItems().addAll("Gennaio", "Febbraio", "Marzo", "Aprile", "Maggio", "Giugno", "Luglio", "Agosto", "Settembre", "Ottobre", "Novembre", "Dicembre");
  
    }
    
    
}
