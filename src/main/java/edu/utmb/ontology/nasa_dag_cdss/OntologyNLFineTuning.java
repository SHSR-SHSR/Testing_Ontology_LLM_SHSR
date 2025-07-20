package edu.utmb.ontology.nasa_dag_cdss;

import edu.utmb.ontology.hootation.core.Hootation;
import java.io.File;
import java.util.ArrayList;


public class OntologyNLFineTuning {
    
    private String ontology_file = null;
    
    public OntologyNLFineTuning(){
        
    }
    
    public void addOntology(String string_file){
        this.ontology_file = string_file;
        System.out.println(ontology_file);
    }
    
    public ArrayList<String> convertAxiomsToNaturalLanguage(){
        Hootation hootation = new Hootation();
        ArrayList<String> natural_language_statements = hootation.get_naturalLangaugeStatements(this.ontology_file);
        
        return natural_language_statements;
    }
    
    public File RBO(){
        File file = new File(getClass().getClassLoader().getResource("rbo.owl").getFile());
        
        return file;
    }
    
    public File SLSO(){
        File file = new File(getClass().getClassLoader().getResource("slso.owl").getFile());
        
        return file;
    }
    
    public static void main(String[] args) {
        
        OntologyNLFineTuning tune = new OntologyNLFineTuning();
        tune.addOntology("/Users/mac/NetBeansProjects/nasa_dag_cdss/rbo.owl");
        
        ArrayList<String> nl_list = tune.convertAxiomsToNaturalLanguage();
        
        for(String s : nl_list){
            System.out.println(s);
        }
        
    }
}
