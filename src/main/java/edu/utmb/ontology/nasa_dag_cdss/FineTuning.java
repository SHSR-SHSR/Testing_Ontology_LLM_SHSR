package edu.utmb.ontology.nasa_dag_cdss;

import edu.utmb.ontology.hootation.core.Hootation;
import java.io.File;
import java.util.ArrayList;


public class FineTuning {
    
    private File ontology_file;
    
    public FineTuning(File ontology){
        ontology_file = ontology;
    }
    
    public ArrayList<String> convertAxiomsToNaturalLanguage(){
        Hootation hootation = new Hootation();
        ArrayList<String> natural_language_statements = hootation.get_naturalLangaugeStatements(ontology_file);
        
        return natural_language_statements;
    }
    
    
    
    public static void main(String[] args) {
        
    }
}
