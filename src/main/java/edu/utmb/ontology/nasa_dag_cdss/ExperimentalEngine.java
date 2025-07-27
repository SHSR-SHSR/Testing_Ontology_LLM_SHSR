/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.utmb.ontology.nasa_dag_cdss;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import static dev.langchain4j.data.document.Metadata.metadata;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.model.jlama.JlamaChatModel;
import dev.langchain4j.model.jlama.JlamaEmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.joining;
/**
 *
 * @author mac
 */
public class ExperimentalEngine {
    
    private OntologyNLFineTuning tuner;
    
    private ArrayList<TextSegment> segments;
    
    private EmbeddingModel embedding_model;
    
    private EmbeddingStore<TextSegment> embedding_store;
    
    private Embedding inquiry_embedding;
    
    public ExperimentalEngine(){
        tuner = new OntologyNLFineTuning();
    }
    
    public void importFineTuningContent(String file_path){
        
        
        tuner.addOntology(file_path);
        
        
    }
    
    
    public void embeddFineTuneContent(){
        
        ArrayList<String> axiom_list = tuner.convertAxiomsToNaturalLanguage();
        
        embedding_model = JlamaEmbeddingModel.builder().modelName("intfloat/e5-small-v2").build();
        
        
        
        segments = new ArrayList<TextSegment>();
        
        StringBuilder sb = new StringBuilder();
        for(String axiom : axiom_list){
            
            //System.out.println(axiom);
            if(axiom != null){
                TextSegment tx = TextSegment.from(axiom);
            segments.add(tx);
            }

            
        }
        
        
        System.out.println("***** EMBEDDING *******");
        List<Embedding> embeddings = new ArrayList<Embedding>(); 
        
        for(TextSegment tx : segments){
            
            System.out.println(tx.text());
            
            Embedding content = embedding_model.embed(tx).content();
            
            embeddings.add(content);
            
        }
        
        embedding_store = new InMemoryEmbeddingStore<>();
        embedding_store.addAll(embeddings, segments);
        
    }
    
    public Prompt addUserInquiry(String inquiry){
        
        inquiry_embedding = embedding_model.embed(inquiry).content();
        
        EmbeddingSearchRequest embeddingRequest = EmbeddingSearchRequest.builder()
                    .queryEmbedding(inquiry_embedding)
                    .maxResults(3)
                    .minScore(0.7)
                    .build();
            List<EmbeddingMatch<TextSegment>> relevantEmbeddings = embedding_store.search(embeddingRequest).matches();
            
        String information = relevantEmbeddings.stream()
                    .map(match -> match.embedded().text())
                    .collect(joining("\n\n"));
        
        
        PromptTemplate tuning_instructions = tuner.getTuning_instructions();
        
        
        Map<String, Object> promptInputs = new HashMap<>();
            promptInputs.put("question", inquiry);
            promptInputs.put("information", information);
            
        Prompt prompt = tuning_instructions.apply(promptInputs);
        
        return prompt;
        
        
        
    }
    
    public void activateJlamaModel(Prompt prompt){
        
        ChatModel jlamaModel = JlamaChatModel.builder()
                    .modelName("tjake/Llama-3.2-1B-Instruct-JQ4")
                    .temperature(0.2f) 
                    .build();
        
        AiMessage message = jlamaModel.chat(prompt.toUserMessage()).aiMessage();
        
        
        System.out.println("-----------------\n\n\n");
        System.out.println(message.text());
        
    }
    
    public static void main(String[] args) {
        /* ///EXAMPLE
        ChatModel model = JlamaChatModel.builder()
                    .modelName("tjake/Llama-3.2-1B-Instruct-JQ4")
                    .temperature(0.3f)
                    .build();

            ChatResponse chatResponse = model.chat(
                    SystemMessage.from("You are helpful chatbot who is a java expert."),
                    UserMessage.from("Write a java program to print hello world.")
            );

            System.out.println("\n" + chatResponse.aiMessage().text() + "\n");
            */
        
        
        String inquiry_example = "Can you tell me something about cancer from the given information?";
        
        ExperimentalEngine engine = new ExperimentalEngine();
        engine.importFineTuningContent("/Users/mac/Desktop/ndkg.rdf");
        engine.embeddFineTuneContent();
        Prompt prompt = engine.addUserInquiry(inquiry_example);
        engine.activateJlamaModel(prompt);
    }

}
