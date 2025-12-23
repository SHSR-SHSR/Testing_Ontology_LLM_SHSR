/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Singleton.java to edit this template
 */
package edu.utmb.ontology.nasa_dag_cdss.ontology;

import edu.utmb.ontology.nasa_dag_cdss.IRI_Ontology;
import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

/**
 *
 * @author mac
 */
public class OWL2OntologyController {
    
    private OWLOntology ontology = null;
    private OWLOntologyManager manager = null;
    private OWLDataFactory factory;
    private Set<OWLEntity> seed_list = null;
    
    private OWL2OntologyController() {
    }
    
    public static OWL2OntologyController getInstance() {
        return OWL2OntologyControllerHolder.INSTANCE;
    }
    
    private static class OWL2OntologyControllerHolder {

        private static final OWL2OntologyController INSTANCE = new OWL2OntologyController();
    }
    
    public void initOntology(String path_ontology_file){
        try {
            manager = OWLManager.createConcurrentOWLOntologyManager();
            
            ontology  = manager.loadOntologyFromOntologyDocument(new File(path_ontology_file));
            
            factory = manager.getOWLDataFactory();
            
        } catch (OWLOntologyCreationException ex) {
            System.getLogger(OWL2OntologyController.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
        
    }
    
    public void clearSeedList(){
        
        seed_list.clear();
    }
    
    public void addSeedClassTerm(String IRI_string){
        
        OWLClass owlClass = factory.getOWLClass(IRI_string);
        
        generateSeedList();
        
        seed_list.add(owlClass);
        
    }
    
    public Set<OWLEntity> getSeedList(){
        return this.seed_list;
    }
    
    private void generateSeedList(){
        
        if(seed_list == null){
            seed_list = new HashSet<OWLEntity>();
        }
        
        
    }
    
    public OWLDataFactory getOWLDataFactory(){
        
        return factory;
        
    }
    
    public OWLOntologyManager getOWLOntologyManager(){
        return manager;
    }
    
    public OWLOntology getOWLOntology(){
        return ontology;
    }
    
    public void initOntology(IRI iri_ontology){
        
        try {
            manager = OWLManager.createConcurrentOWLOntologyManager();
            
            ontology  = manager.loadOntology(iri_ontology);
        } catch (OWLOntologyCreationException ex) {
            Logger.getLogger(OWL2OntologyController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
    
    public static void main(String[] args) {
        
        OWL2OntologyController instance = OWL2OntologyController.getInstance();
        
        instance.initOntology(IRI_Ontology.RBO);
        
    }
    
}
