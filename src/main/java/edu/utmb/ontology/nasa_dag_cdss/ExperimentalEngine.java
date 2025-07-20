/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.utmb.ontology.nasa_dag_cdss;

import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.jlama.JlamaChatModel;
/**
 *
 * @author mac
 */
public class ExperimentalEngine {
    
    
    public ExperimentalEngine(){
        
    }
    
    public static void main(String[] args) {

        ChatModel model = JlamaChatModel.builder()
                    .modelName("tjake/Llama-3.2-1B-Instruct-JQ4")
                    .temperature(0.3f)
                    .build();

            ChatResponse chatResponse = model.chat(
                    SystemMessage.from("You are helpful chatbot who is a java expert."),
                    UserMessage.from("Write a java program to print hello world.")
            );

            System.out.println("\n" + chatResponse.aiMessage().text() + "\n");

    }

}
