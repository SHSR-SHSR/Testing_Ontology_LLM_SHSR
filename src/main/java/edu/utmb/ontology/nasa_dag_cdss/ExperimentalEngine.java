/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.utmb.ontology.nasa_dag_cdss;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentParser;
import dev.langchain4j.data.document.DocumentSplitter;
import static dev.langchain4j.data.document.loader.FileSystemDocumentLoader.loadDocument;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.model.jlama.JlamaChatModel;
import dev.langchain4j.model.jlama.JlamaEmbeddingModel;
import dev.langchain4j.model.jlama.JlamaStreamingChatModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import edu.utmb.ontology.nasa_dag_cdss.ontology.OWL2OntologyController;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import static java.util.stream.Collectors.joining;
import org.apache.commons.io.FileUtils;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
/**
 *
 * @author mac
 */
public class ExperimentalEngine {
    
    private OntologyNLFineTuning ontology_tuner;
    
    private ArrayList<TextSegment> segments;
    
    private EmbeddingModel embedding_model;
    
    private EmbeddingStore<TextSegment> embedding_store;
    
    private Embedding inquiry_embedding;
    
    private int searchMaxResults = 3;
    private double minScoreSearch = 0.85;
    private float chatModelTemperature = 0.2f;
    
    private String storeModel = "intfloat/e5-small-v2";
    
    public ExperimentalEngine(){
        ontology_tuner = new OntologyNLFineTuning();
    }
    
    public void importFineTuningContent(String file_path){
        
        
        ontology_tuner.addOntology(file_path);
        
        
    }
    
    public void embedFineTuneTextContent(){
        
        //create text content
        ArrayList<String> axiom_list = ontology_tuner.convertAxiomsToNaturalLanguage();
        File file = new File("temp.txt");
        
        
        System.out.println("creating document");
        StringBuilder content = new StringBuilder();
        
        for(String line_content : axiom_list){
            
            content.append(line_content);
            content.append(". \n");
            
        }
        
        try {
            FileUtils.writeStringToFile(file, content.toString(), "UTF-8");
        } catch (IOException ex) {
            System.getLogger(ExperimentalEngine.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
        
        
        System.out.println("Embedding...");
        DocumentParser documentParser = new TextDocumentParser();
        
        System.out.println("\tLoading... " + file.getAbsolutePath());
        Document document = loadDocument(file.getAbsolutePath(), documentParser);
         
         //DocumentBySentenceSplitter splitter = new DocumentBySentenceSplitter(30, 0);
         DocumentSplitter splitter = DocumentSplitters.recursive(300, 0);
         List<TextSegment> segments = splitter.split(document);
         
         embedding_model = JlamaEmbeddingModel.builder().modelName(storeModel).build();
         List<Embedding> embeddings = embedding_model.embedAll(segments).content();

         
        embedding_store = new InMemoryEmbeddingStore<>();
        
        embedding_store.addAll(embeddings, segments);
    }
    
    public void embedOntologyRelatedFineTuneContent(){
        
        //add ontology related terms
        OWL2OntologyController owl_controller = OWL2OntologyController.getInstance();
        
        //aerospace planned process
        owl_controller.addSeedClassTerm("http://purl.org/utmb/ndkg-base.owl#NDKG_0000568");
        
        Set<OWLEntity> seedList = owl_controller.getSeedList();
        
        Set<OWLAxiom> relatedAxioms = ontology_tuner.getRelatedAxioms(seedList);
        
        ArrayList<String> natural_language_axioms = ontology_tuner.convertAxiomsToNaturalLanguage(relatedAxioms);
        
        System.out.println("creating document");
        
        File file = new File("temp_SLE.txt");
        StringBuilder content = new StringBuilder();
        for(String line_content : natural_language_axioms){
            content.append(line_content);
            content.append(". ");
        }
        
        try {
            FileUtils.writeStringToFile(file, content.toString(), "UTF-8");
        } catch (IOException ex) {
            System.getLogger(ExperimentalEngine.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
        
        
    }
    
    public void embeddFineTuneContent(){
        
        ArrayList<String> axiom_list = ontology_tuner.convertAxiomsToNaturalLanguage();
        
        embedding_model = JlamaEmbeddingModel.builder().modelName(storeModel).build();
        
        
        
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
    
    public Prompt addUserInquryAndContext(String inquiry){
        
        String nl_ontology_info = "";
        
        try {
            nl_ontology_info = FileUtils.readFileToString(new File("temp_SLE.txt"), "UTF-8");
        } catch (IOException ex) {
            System.getLogger(ExperimentalEngine.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
        
        Map<String, Object> promptInputs = new HashMap<>();
            promptInputs.put("question", inquiry);
            promptInputs.put("information", nl_ontology_info);
        
         PromptTemplate tuning_instructions = ontology_tuner.getTuning_instructions();
         Prompt prompt = tuning_instructions.apply(promptInputs);

         
        System.out.println("The prompt used: ..\n");
        
        System.out.println(prompt.toString());
        
        return prompt;
        
    }
    
    public Prompt addUserInquiry(String inquiry){
        
        inquiry_embedding = embedding_model.embed(inquiry).content();
        
        EmbeddingSearchRequest embeddingRequest = EmbeddingSearchRequest.builder()
                    .queryEmbedding(inquiry_embedding)
                    .maxResults(searchMaxResults)
                    .minScore(minScoreSearch)
                    .build();
            List<EmbeddingMatch<TextSegment>> relevantEmbeddings = embedding_store.search(embeddingRequest).matches();
            
        String information = relevantEmbeddings.stream()
                    .map(match -> match.embedded().text())
                    .collect(joining("\n\n"));
        
        
        PromptTemplate tuning_instructions = ontology_tuner.getTuning_instructions();
        
        
        Map<String, Object> promptInputs = new HashMap<>();
            promptInputs.put("question", inquiry);
            promptInputs.put("information", information);
            
        Prompt prompt = tuning_instructions.apply(promptInputs);
        
        
        System.out.println("The prompt used: ..\n");
        
        System.out.println(prompt.toString());
        
        return prompt;
        
        
        
    }
    
    public JlamaStreamingChatModel activateDefaultJlamaModel(){
        return JlamaStreamingChatModel.builder()
                    .modelName("tjake/Llama-3.2-1B-Instruct-JQ4")
                    .temperature(chatModelTemperature) 
                    .build();
    }
    
    public ChatModel activateJlamaModel(float chat_temperature){
        return JlamaChatModel.builder()
                    .modelName("tjake/Llama-3.2-1B-Instruct-JQ4")
                    .temperature(chat_temperature) 
                    .build();
    }
    
    public void activateJlamaModel(Prompt prompt){
        
        ChatModel jlamaModel = JlamaChatModel.builder()
                    .modelName("tjake/Llama-3.2-1B-Instruct-JQ4")
                    .temperature(chatModelTemperature) 
                    .build();
        
        AiMessage message = jlamaModel.chat(prompt.toUserMessage()).aiMessage();
        
        
        System.out.println("-----------------\n\n\n");
        System.out.println(message.text());
        
    }
    
    public static void main(String[] args) {

        
        String inquiry_example = "Can you tell me something about cancer from the given information?";
        
        ExperimentalEngine engine = new ExperimentalEngine();
        engine.importFineTuningContent("/Users/mac/Desktop/ndkg.rdf");
        engine.embeddFineTuneContent();
        Prompt prompt = engine.addUserInquiry(inquiry_example);
        engine.activateJlamaModel(prompt);
    }

}
