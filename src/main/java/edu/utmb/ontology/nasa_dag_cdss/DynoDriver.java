/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.utmb.ontology.nasa_dag_cdss;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.input.Prompt;
import java.util.Scanner;

/**
 *
 * @author mac
 */
public class DynoDriver {
    
    private ExperimentalEngine engine = null;
    
    private ChatModel jlamaModel = null;
    
    static private String NASA_Human_Health_KG = "/Users/mac/Desktop/ndkg.rdf";
    
    public DynoDriver(){
        
        engine = new ExperimentalEngine();
        
    }
    
    public void initalize(){
        engine.importFineTuningContent(NASA_Human_Health_KG);
        engine.embeddFineTuneContent();
        
        jlamaModel = engine.activateDefaultJlamaModel();
    }
    
    public void staticQuestion(){
        
    }
    
    public String generateAnswer(String inquiry){
        Prompt promptInquiry = engine.addUserInquiry(inquiry);
        
        AiMessage ai_message = jlamaModel.chat(promptInquiry.toUserMessage()).aiMessage();
        
        return ai_message.text();
    }
    
    public static void main(String[] args) {
        System.out.println("\n************************************");
        System.out.println("Starting Dyno Driver for CDSS Engine");
        System.out.println("************************************\n");
        
        DynoDriver dd = new DynoDriver();
        dd.initalize();
        
        Scanner console = new Scanner(System.in);
        
        while(true){
            
            System.out.println("Type a question or type 'exit' to quit");
            
            String input = console.next();
            
            if(input.equalsIgnoreCase("exit")){
                break;
            }
            else{
                
                System.out.println(dd.generateAnswer(input));
            }
            
        }    
        
        
        System.out.println("\n************************************");
        System.out.println("END");
        System.out.println("************************************\n");
        
    }
    
}
