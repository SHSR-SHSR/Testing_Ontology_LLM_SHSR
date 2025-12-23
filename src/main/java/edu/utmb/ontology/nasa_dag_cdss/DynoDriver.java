/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.utmb.ontology.nasa_dag_cdss;

import dev.langchain4j.data.message.AiMessage;
import static dev.langchain4j.model.LambdaStreamingResponseHandler.onPartialResponse;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import dev.langchain4j.model.input.Prompt;
import edu.utmb.ontology.nasa_dag_cdss.ontology.OWL2OntologyController;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;

/**
 *
 * @author mac
 */
public class DynoDriver {
    
    private ExperimentalEngine engine = null;
    
    private StreamingChatModel jlamaModel = null;
    
    static private String NASA_Human_Health_KG = "/Users/mac/Desktop/ndkg-final.owl";
    
    //static private String SPACE_LIFE_SCIENCE = "/Users/mac/NetBeansProjects/nasa_dag_cdss/slso.owl";
    
    //static private String OMRSE ="/Users/mac/Desktop/bsso/omrse.owl";
    
    public DynoDriver(){
        
        engine = new ExperimentalEngine();
        
    }
    
    public void initalize(){
        engine.importFineTuningContent(NASA_Human_Health_KG);
        //engine.embeddFineTuneContent();
        //engine.embedFineTuneTextContent();
        OWL2OntologyController.getInstance().initOntology(NASA_Human_Health_KG);
        engine.embedOntologyRelatedFineTuneContent();
        jlamaModel = engine.activateDefaultJlamaModel();
    }
    
    public void staticQuestion(){
        
    }
    
    public String generateAnswerWithOntoRAG(String inquiry){
        Prompt prompt_inquiry = engine.addUserInquryAndContext(inquiry);
        
        CompletableFuture<ChatResponse> futureResponse = new CompletableFuture<>();
         
        jlamaModel.chat(inquiry,new StreamingChatResponseHandler() {

            @Override
            public void onPartialResponse(String partialResponse) {
                System.out.print(partialResponse);
            }

            @Override
            public void onCompleteResponse(ChatResponse completeResponse) {
                futureResponse.complete(completeResponse);
            }

            @Override
            public void onError(Throwable error) {
                futureResponse.completeExceptionally(error);
            }
            
            
        });
       
        ChatResponse chat_response = futureResponse.join();
         
        
        
         return chat_response.aiMessage().text();
        
    }
    
    //TODO: implement this function as a thread
    public String generateAnswer(String inquiry){
        Prompt promptInquiry = engine.addUserInquiry(inquiry);
        

        
        
        CompletableFuture<ChatResponse> futureResponse = new CompletableFuture<>();
         
        jlamaModel.chat(inquiry,new StreamingChatResponseHandler() {

            @Override
            public void onPartialResponse(String partialResponse) {
                System.out.print(partialResponse);
            }

            @Override
            public void onCompleteResponse(ChatResponse completeResponse) {
                futureResponse.complete(completeResponse);
            }

            @Override
            public void onError(Throwable error) {
                futureResponse.completeExceptionally(error);
            }
            
            
        });
       
        ChatResponse chat_response = futureResponse.join();
         
        
        
         return chat_response.aiMessage().text();
    }
    
    public static void main(String[] args) {
        
        final String user_input = "What does humidity affect?";
        
        System.out.println("\n************************************");
        System.out.println("Starting Dyno Driver for CDSS Engine");
        System.out.println("************************************\n");
        
        DynoDriver dd = new DynoDriver();
        dd.initalize();
        
        
        System.out.println("\n************************************");
        System.out.println("Done initializing, now inputting your response");
        System.out.println("************************************\n");
        
        
        //dd.generateAnswer(user_input);
        dd.generateAnswerWithOntoRAG(user_input);
        
        
        /*
        Scanner console = new Scanner(System.in);
        
        
        System.out.println("Type a question or type 'exit' to quit");
        while(console.hasNext()){
            
            
            
            String input = console.next();
            
            if(input.equalsIgnoreCase("exit")){
                break;
            }
            else{
                
               dd.generateAnswer(input);
            }
            
            System.out.println("Type a question or type 'exit' to quit");
            
        }    
        */
        
        System.out.println("\n************************************");
        System.out.println("END");
        System.out.println("************************************\n");
        
    }
    
}
