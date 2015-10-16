/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.io;
import nforce.io.Config;

/**
 *
 * @author penpen926
 */
public class DrugReposConfig {
    // Preclustering configs;
    public Config drugPreClustConfig;
    public Config genePreClustConfig;
    public Config diseasePreClustConfig;
    
    public String drug_xml;
    public String drug_id;
    public String drug_smiles;
    public String drug_matrix;
    public String gene_id;
    public String gene_matrix;
    public String disease_id;
    public String disease_matrix;
    // inter matrix and associations.
    public String drug_disease_assoc;
    public String drug_gene_assoc;
    public String gene_disease_assoc;
    
    public String drug_gene_matrix;
    public String drug_disease_matrix;
    public String gene_disease_matrix;
    
    // Drug preclusters information
    public String drug_precluster_id;
    public String drug_vertex_precluster_mapping;
    public String drug_precluster_index_mapping;
    
    // Gene preclusters information
    public String gene_precluster_id;
    public String gene_vertex_precluster_mapping;
    public String gene_precluster_index_mapping;
    
    // Disease preclusters information
    public String disease_precluster_id;
    public String disease_vertex_precluster_mapping;
    public String disease_precluster_index_mapping;
    
    // pre cluster matrix
    public String drug_gene_precluster_matrix;
    public String drug_disease_precluster_matrix;
    public String gene_disease_precluster_matrix;
    
    //Pre-cluster parsed entity output
    public String precluster_parsed_entity;
    
    // For cross-validation
    public String drug_disease_cv_matrix;
    public String drug_disease_precluster_cv_matrix;
    public float posEw;
    public float negEw;
    public float cv_prop;
    
    // Gold standard data
    public String gsp;
    public String gsn;
    
    // Output
    // Roc output
    public String roc_output;
    public Config cvConfig;
    public String cv_result_output;
    
    // Repos output
    public Config reposConfig;
    public String repos_output;
    
    // Param file
    public String paramFile = "./parameters.ini";
    
    public String semanticGoldFile;
    public String semanticSilverFile;
    public String semanticOutput;
    public String semanticSumOutput;
    
    // The switch of similar repos
    public boolean simRepos = false;
    public float simReposTh;
    
    // The switch of removing dissimilar diseases
    public boolean remove_dissimilar_disease;
    
    public String disease_to_remove_file;
    
    // The map from disease to cui
    public String disease_cui_map;
    
    // The settings for the compare2
    public String compare2_append_names;
    public String compare2_append_smiles;
    public String compare2_new_smiles;
}
