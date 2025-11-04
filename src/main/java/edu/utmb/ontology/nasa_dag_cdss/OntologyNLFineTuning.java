package edu.utmb.ontology.nasa_dag_cdss;

import dev.langchain4j.model.input.PromptTemplate;
import edu.utmb.ontology.hootation.core.Hootation;
import java.io.File;
import java.util.ArrayList;


public class OntologyNLFineTuning {
    
    private String ontology_file = null;
    
    private PromptTemplate tuning_instructions;

    public PromptTemplate getTuning_instructions() {
        return tuning_instructions;
    }
    
    public OntologyNLFineTuning(){
        
        StringBuilder instruction_builder = new StringBuilder();
        /*
        instruction_builder.append("Given the following information: \n");
        instruction_builder.append("{{information}}\n");
        instruction_builder.append("State the given information and provide a response for the question based on the given information only: \n");
        instruction_builder.append("Question: {{question}}\n");
        instruction_builder.append("Response:\n");
        */
        
        //using prompt example from langchain4j
        instruction_builder.append("Answer the following question to the best of your ability:\n\n");
        instruction_builder.append("Question: \n");
        instruction_builder.append("{{question}}\n\n");
        instruction_builder.append("Base your answer on the following information:\n");
        instruction_builder.append("{{information}}");
        
        
        tuning_instructions = PromptTemplate.from(instruction_builder.toString());
        
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
