/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.init;
import data.io.DrugReposConfig;
import nforce.algorithms.Param;
import nforce.io.Config;
/**
 *
 * @author penpen926
 */
public class InitDrugReposConfig {

    public void initDrugReposConfig(DrugReposConfig conf) {
        //Drug bank xml
        conf.drug_xml = "../../drugbank/drugbank.xml";
        // Id Files
        conf.drug_id = "../../id/drug_id.txt";
        conf.cas_id = "../../id/cas_number.txt";
        conf.gene_id = "../../id/gene_id.txt";
        conf.disease_id = "../../id/disease_id.txt";
        // Smiles file
        conf.drug_smiles = "../../smiles/drug_smiles.txt";
        //Associations
        conf.drug_disease_assoc = "../../assoc/drug_disease_assoc.txt";
        conf.drug_gene_assoc = "../../assoc/drug_gene_assoc.txt";
        conf.gene_disease_assoc = "../../assoc/gene_disease_assoc.txt";
        // Matrix
        conf.drug_matrix = "../../matrix/new_drug_matrix.txt";
        conf.gene_matrix = "../../matrix/gene_matrix.txt";
        conf.disease_matrix = "../../matrix/disease_matrix.txt";
        conf.drug_gene_matrix = "../../matrix/drug_gene_matrix.txt";
        conf.drug_disease_matrix = "../../matrix/drug_disease_matrix.txt";
        conf.gene_disease_matrix = "../../matrix/gene_disease_matrix.txt";
        //Precluster settings;
        //Precluster configs for drug
        conf.drugPreClustConfig = new Config();
        conf.drugPreClustConfig.input = "../../preclustering/drug/input.xml";
        conf.drugPreClustConfig.clusterOutput = "../../preclustering/drug/drug_preclusters.txt";
        conf.drugPreClustConfig.editOutput = null;
        conf.drugPreClustConfig.graphOutput = null;
        conf.drugPreClustConfig.graphType = 4;
        conf.drugPreClustConfig.isXmlFormat = true;
        conf.drugPreClustConfig.p = new Param(conf.paramFile);
        conf.drugPreClustConfig.p.setThresh(0.85F);
        conf.drugPreClustConfig.p.setThreshArray(null);
        conf.drug_precluster_id = "../../preclustering/drug/drug_precluster_id.txt";
        conf.drug_vertex_precluster_mapping = "../../preclustering/drug/drug_vertex_precluster_mapping.txt";
        conf.drug_precluster_index_mapping = "../../preclustering/drug/drug_precluster_index_mapping.txt";
        //Precluster configs for gene
        conf.genePreClustConfig = new Config();
        conf.genePreClustConfig.input = "../../preclustering/gene/input.xml";
        conf.genePreClustConfig.clusterOutput = "../../preclustering/gene/gene_preclusters.txt";
        conf.genePreClustConfig.editOutput = null;
        conf.genePreClustConfig.graphOutput = null;
        conf.genePreClustConfig.graphType = 4;
        conf.genePreClustConfig.isXmlFormat = true;
        conf.genePreClustConfig.p = new Param(conf.paramFile);
        conf.genePreClustConfig.p.setThresh(37.0F);
        conf.genePreClustConfig.p.setThreshArray(null);
        conf.genePreClustConfig.p = new Param(conf.paramFile);
        conf.genePreClustConfig.p.setThresh(37.0F);
        conf.genePreClustConfig.p.setThreshArray(null);
        conf.gene_precluster_id = "../../preclustering/gene/gene_precluster_id.txt";
        conf.gene_vertex_precluster_mapping = "../../preclustering/gene/gene_vertex_precluster_mapping.txt";
        conf.gene_precluster_index_mapping = "../../preclustering/gene/gene_precluster_index_mapping.txt";
        //Precluser configs for disease
        conf.diseasePreClustConfig = new Config();
        conf.diseasePreClustConfig.input = "../../preclustering/disease/input.xml";
        conf.diseasePreClustConfig.clusterOutput = "../../preclustering/disease/disease_preclusters.txt";
        conf.diseasePreClustConfig.editOutput = null;
        conf.diseasePreClustConfig.graphOutput = null;
        conf.diseasePreClustConfig.graphType = 4;
        conf.diseasePreClustConfig.isXmlFormat = true;
        conf.diseasePreClustConfig.p = new Param(conf.paramFile);
        conf.diseasePreClustConfig.p.setThreshArray(null);
        conf.diseasePreClustConfig.p.setThresh(0.8F);
        conf.diseasePreClustConfig.p.setThreshArray(null);
        conf.disease_precluster_id = "../../preclustering/disease/disease_precluster_id.txt";
        conf.disease_vertex_precluster_mapping = "../../preclustering/disease/disease_vertex_precluster_mapping.txt";
        conf.disease_precluster_index_mapping = "../../preclustering/disease/disease_precluster_index_mapping.txt";
        //Settings for the drug, gene and disease precluster matrix
        conf.drug_disease_precluster_matrix = "../../repos/drug_disease_precluster_matrix.txt";
        conf.drug_gene_precluster_matrix = "../../repos/drug_gene_precluster_matrix.txt";
        conf.gene_disease_precluster_matrix = "../../repos/gene_disease_precluster_matrix.txt";
        //Setting for gsp
        conf.gsp = "../../cv/cv_gsp.txt";
        conf.gsn = "../../gsn/negative_0.3.txt";
        //Settings for repositioning
        conf.reposConfig = new Config();
        conf.reposConfig.input = "../../repos/inputxml.txt";
        conf.reposConfig.clusterOutput = "../../repos/repos_cluster_out.txt";
        conf.reposConfig.editOutput = null;
        conf.reposConfig.graphOutput = null;
        conf.reposConfig.graphType = 5;
        conf.reposConfig.isXmlFormat = true;
        conf.reposConfig.p = new Param(conf.paramFile);
        conf.repos_output = "../../repos/parsed_repos_out.txt";
        //Settings for cross validation
        conf.precluster_parsed_entity = "../../cv/entity.txt";
        conf.cvConfig = new Config();
        conf.cvConfig.input = "../../cv/inputxml.txt";
        conf.cvConfig.editOutput = null;
        conf.cvConfig.p = new Param(conf.paramFile);
        conf.cvConfig.p.setThresh(0.45F);
        conf.cvConfig.p.setThreshArray(null);
        conf.cvConfig.clusterOutput = "../../cv/output/cluster_out.txt";
        conf.cvConfig.graphOutput = null;
        conf.cvConfig.graphType = 5;
        conf.cvConfig.isXmlFormat = true;
        conf.posEw = 1;
        conf.negEw = 0;
        conf.cv_prop = 0.1F;
        conf.cv_result_output = "../../cv/cv_parsed_res.txt";
        conf.roc_output = "../../cv/roc.txt";
        conf.drug_disease_cv_matrix = "../../cv/drug_disease_cv_matrix.txt";
        conf.drug_disease_precluster_cv_matrix = "../../cv/drug_disease_precluster_cv_matrix.txt";
        //Sematic output
        conf.semanticGoldFile = "../../sematic/drug_repos_silver.txt";
        conf.semanticSilverFile = "../../sematic/drug_repos_gold.txt";
        conf.semanticOutput = "../../repos/sematic_output.txt";
        conf.semanticSumOutput = "../../repos/semantic_sum.txt";
        // Disease to cui map
        conf.disease_cui_map = "../../assoc/disease_cui_assoc.txt";
        // The switch of the similar repositioning
        conf.simRepos = true;
        conf.simReposTh = 0.85F;
        conf.remove_dissimilar_disease = true;
        conf.disease_to_remove_file = "../../disease/disease_to_remove_id.txt";
    }

    public void initCompare2(DrugReposConfig conf){
        //Drug bank xml
        conf.drug_xml = "../../drugbank/drugbank.xml";
        // Id Files
        conf.drug_id = "../../compare2/id/compare2_drug_id.txt";
        conf.cas_id = "../../compare2/id/compare2_cas_id.txt";
        conf.gene_id = "../../id/gene_id.txt";
        conf.disease_id = "../../compare2/id/compare2_disease_id.txt";
        // Smiles file
        conf.drug_smiles = "../../compare2/smiles/compare2_drug_smiles.txt";
        //Associations
        conf.drug_disease_assoc = "../../compare2/assoc/compare2_drug_disease_assoc.txt";
        conf.drug_gene_assoc = "../../compare2/assoc/compare2_drug_gene_assoc.txt";
        conf.gene_disease_assoc = "../../compare2/assoc/compare2_gene_disease_assoc.txt";
        // Matrix
        conf.drug_matrix = "../../compare2/matrix/compare2_drug_matrix.txt";
        conf.gene_matrix = "../../matrix/gene_matrix.txt";
        conf.disease_matrix = "../../compare2/matrix/compare2_disease_matrix.txt";
        conf.drug_gene_matrix = "../../compare2/matrix/compare2_drug_gene_matrix.txt";
        conf.drug_disease_matrix = "../../compare2/matrix/compare2_drug_disease_matrix.txt";
        conf.gene_disease_matrix = "../../compare2/matrix/compare2_gene_disease_matrix.txt";
        //Precluster settings;
        //Precluster configs for drug
        conf.drugPreClustConfig = new Config();
        conf.drugPreClustConfig.input = "../../compare2/preclustering/drug/input.xml";
        conf.drugPreClustConfig.clusterOutput = "../../compare2/preclustering/drug/drug_preclusters.txt";
        conf.drugPreClustConfig.editOutput = null;
        conf.drugPreClustConfig.graphOutput = null;
        conf.drugPreClustConfig.graphType = 4;
        conf.drugPreClustConfig.isXmlFormat = true;
        conf.drugPreClustConfig.p = new Param(conf.paramFile);
        conf.drugPreClustConfig.p.setThresh(0.85F);
        conf.drugPreClustConfig.p.setThreshArray(null);
        conf.drug_precluster_id = "../../compare2/preclustering/drug/drug_precluster_id.txt";
        conf.drug_vertex_precluster_mapping = "../../compare2/preclustering/drug/drug_vertex_precluster_mapping.txt";
        conf.drug_precluster_index_mapping = "../../compare2/preclustering/drug/drug_precluster_index_mapping.txt";
        //Precluster configs for gene
        conf.genePreClustConfig = new Config();
        conf.genePreClustConfig.input = "../../compare2/preclustering/gene/input.xml";
        conf.genePreClustConfig.clusterOutput = "../../compare2/preclustering/gene/gene_preclusters.txt";
        conf.genePreClustConfig.editOutput = null;
        conf.genePreClustConfig.graphOutput = null;
        conf.genePreClustConfig.graphType = 4;
        conf.genePreClustConfig.isXmlFormat = true;
        conf.genePreClustConfig.p = new Param(conf.paramFile);
        conf.genePreClustConfig.p.setThresh(37.0F);
        conf.genePreClustConfig.p.setThreshArray(null);
        conf.genePreClustConfig.p = new Param(conf.paramFile);
        conf.genePreClustConfig.p.setThresh(37.0F);
        conf.genePreClustConfig.p.setThreshArray(null);
        conf.gene_precluster_id = "../../compare2/preclustering/gene/gene_precluster_id.txt";
        conf.gene_vertex_precluster_mapping = "../../compare2/preclustering/gene/gene_vertex_precluster_mapping.txt";
        conf.gene_precluster_index_mapping = "../../compare2/preclustering/gene/gene_precluster_index_mapping.txt";
        //Precluser configs for disease
        conf.diseasePreClustConfig = new Config();
        conf.diseasePreClustConfig.input = "../../compare2/preclustering/disease/input.xml";
        conf.diseasePreClustConfig.clusterOutput = "../../compare2/preclustering/disease/disease_preclusters.txt";
        conf.diseasePreClustConfig.editOutput = null;
        conf.diseasePreClustConfig.graphOutput = null;
        conf.diseasePreClustConfig.graphType = 4;
        conf.diseasePreClustConfig.isXmlFormat = true;
        conf.diseasePreClustConfig.p = new Param(conf.paramFile);
        conf.diseasePreClustConfig.p.setThreshArray(null);
        conf.diseasePreClustConfig.p.setThresh(0.8F);
        conf.diseasePreClustConfig.p.setThreshArray(null);
        conf.disease_precluster_id = "../../compare2/preclustering/disease/disease_precluster_id.txt";
        conf.disease_vertex_precluster_mapping = "../../compare2/preclustering/disease/disease_vertex_precluster_mapping.txt";
        conf.disease_precluster_index_mapping = "../../compare2/preclustering/disease/disease_precluster_index_mapping.txt";
        //Settings for the drug, gene and disease precluster matrix
        conf.drug_disease_precluster_matrix = "../../compare2/repos/drug_disease_precluster_matrix.txt";
        conf.drug_gene_precluster_matrix = "../../compare2/repos/drug_gene_precluster_matrix.txt";
        conf.gene_disease_precluster_matrix = "../../compare2/repos/gene_disease_precluster_matrix.txt";
        //Setting for gsp
        conf.gsp = "../../compare2/compare2_mapped_gsp.txt";
        conf.gsn = "../../compare2/gsn/compare2_gsn.txt";
        //Settings for repositioning
        conf.reposConfig = new Config();
        conf.reposConfig.input = "../../compare2/repos/inputxml.txt";
        conf.reposConfig.clusterOutput = "../../compare2/repos/repos_cluster_out.txt";
        conf.reposConfig.editOutput = null;
        conf.reposConfig.graphOutput = null;
        conf.reposConfig.graphType = 5;
        conf.reposConfig.isXmlFormat = true;
        conf.reposConfig.p = new Param(conf.paramFile);
        conf.repos_output = "../../compare2/repos/parsed_repos_out.txt";
        //Settings for cross validation
        conf.precluster_parsed_entity = "../../compare2/cv/entity.txt";
        conf.cvConfig = new Config();
        conf.cvConfig.input = "../../compare2/cv/inputxml.txt";
        conf.cvConfig.editOutput = null;
        conf.cvConfig.p = new Param(conf.paramFile);
        conf.cvConfig.p.setThresh(0.45F);
        conf.cvConfig.p.setThreshArray(null);
        conf.cvConfig.clusterOutput = "../../compare2/cv/output/cluster_out.txt";
        conf.cvConfig.graphOutput = null;
        conf.cvConfig.graphType = 5;
        conf.cvConfig.isXmlFormat = true;
        conf.posEw = 1;
        conf.negEw = 0;
        conf.cv_prop = 0.1F;
        conf.cv_result_output = "../../compare2/cv/cv_parsed_res.txt";
        conf.roc_output = "../../compare2/cv/roc.txt";
        conf.drug_disease_cv_matrix = "../../compare2/cv/drug_disease_cv_matrix.txt";
        conf.drug_disease_precluster_cv_matrix = "../../compare2/cv/drug_disease_precluster_cv_matrix.txt";
        //Sematic output
        conf.semanticGoldFile = "../../sematic/drug_repos_silver.txt";
        conf.semanticSilverFile = "../../sematic/drug_repos_gold.txt";
        conf.semanticOutput = "../../repos/sematic_output.txt";
        conf.semanticSumOutput = "../../repos/semantic_sum.txt";
        // Disease to cui map
        conf.disease_cui_map = "../../assoc/disease_cui_assoc.txt";
        // The switch of the similar repositioning
        conf.simRepos = true;
        conf.simReposTh = 0.90F;
        conf.remove_dissimilar_disease = true;
        conf.disease_to_remove_file = "../../disease/disease_to_remove_id.txt";
        conf.compare2_cv_gsp = "../../compare2/compare2_cv_gsp.txt";
    }
    
    public void initCompare3(DrugReposConfig conf){
        //Drug bank xml
        conf.drug_xml = "../../drugbank/drugbank.xml";
        // Id Files
        conf.drug_id = "../../compare3/id/compare3_drug_id.txt";
        conf.cas_id = "../../compare3/id/compare3_cas_id.txt";
        conf.gene_id = "../../compare3/id/compare3_gene_id.txt";
        conf.disease_id = "../../compare3/id/compare3_disease_id.txt";
        // Smiles file
        conf.drug_smiles = "../../compare3/smiles/compare3_drug_smiles.txt";
        //Associations
        conf.drug_disease_assoc = "../../compare3/assoc/compare3_drug_disease_assoc.txt";
        conf.drug_gene_assoc = "../../compare3/assoc/compare3_drug_gene_assoc.txt";
        conf.gene_disease_assoc = "../../compare3/assoc/compare3_gene_disease_assoc.txt";
        // Matrix
        conf.drug_matrix = "../../compare3/matrix/compare3_drug_matrix.txt";
        conf.gene_matrix = "../../compare3/matrix/compare3_gene_matrix.txt";
        conf.disease_matrix = "../../compare3/matrix/compare3_disease_matrix.txt";
        conf.drug_gene_matrix = "../../compare3/matrix/compare3_drug_gene_matrix.txt";
        conf.drug_disease_matrix = "../../compare3/matrix/compare3_drug_disease_matrix.txt";
        conf.gene_disease_matrix = "../../compare3/matrix/compare3_gene_disease_matrix.txt";
        //Precluster settings;
        //Precluster configs for drug
        conf.drugPreClustConfig = new Config();
        conf.drugPreClustConfig.input = "../../compare3/preclustering/drug/input.xml";
        conf.drugPreClustConfig.clusterOutput = "../../compare3/preclustering/drug/drug_preclusters.txt";
        conf.drugPreClustConfig.editOutput = null;
        conf.drugPreClustConfig.graphOutput = null;
        conf.drugPreClustConfig.graphType = 4;
        conf.drugPreClustConfig.isXmlFormat = true;
        conf.drugPreClustConfig.p = new Param(conf.paramFile);
        conf.drugPreClustConfig.p.setThresh(0.85F);
        conf.drugPreClustConfig.p.setThreshArray(null);
        conf.drug_precluster_id = "../../compare3/preclustering/drug/drug_precluster_id.txt";
        conf.drug_vertex_precluster_mapping = "../../compare3/preclustering/drug/drug_vertex_precluster_mapping.txt";
        conf.drug_precluster_index_mapping = "../../compare3/preclustering/drug/drug_precluster_index_mapping.txt";
        //Precluster configs for gene
        conf.genePreClustConfig = new Config();
        conf.genePreClustConfig.input = "../../compare3/preclustering/gene/input.xml";
        conf.genePreClustConfig.clusterOutput = "../../compare3/preclustering/gene/gene_preclusters.txt";
        conf.genePreClustConfig.editOutput = null;
        conf.genePreClustConfig.graphOutput = null;
        conf.genePreClustConfig.graphType = 4;
        conf.genePreClustConfig.isXmlFormat = true;
        conf.genePreClustConfig.p = new Param(conf.paramFile);
        conf.genePreClustConfig.p.setThresh(37.0F);
        conf.genePreClustConfig.p.setThreshArray(null);
        conf.genePreClustConfig.p = new Param(conf.paramFile);
        conf.genePreClustConfig.p.setThresh(37.0F);
        conf.genePreClustConfig.p.setThreshArray(null);
        conf.gene_precluster_id = "../../compare3/preclustering/gene/gene_precluster_id.txt";
        conf.gene_vertex_precluster_mapping = "../../compare3/preclustering/gene/gene_vertex_precluster_mapping.txt";
        conf.gene_precluster_index_mapping = "../../compare3/preclustering/gene/gene_precluster_index_mapping.txt";
        //Precluser configs for disease
        conf.diseasePreClustConfig = new Config();
        conf.diseasePreClustConfig.input = "../../compare3/preclustering/disease/input.xml";
        conf.diseasePreClustConfig.clusterOutput = "../../compare3/preclustering/disease/disease_preclusters.txt";
        conf.diseasePreClustConfig.editOutput = null;
        conf.diseasePreClustConfig.graphOutput = null;
        conf.diseasePreClustConfig.graphType = 4;
        conf.diseasePreClustConfig.isXmlFormat = true;
        conf.diseasePreClustConfig.p = new Param(conf.paramFile);
        conf.diseasePreClustConfig.p.setThreshArray(null);
        conf.diseasePreClustConfig.p.setThresh(0.8F);
        conf.diseasePreClustConfig.p.setThreshArray(null);
        conf.disease_precluster_id = "../../compare3/preclustering/disease/disease_precluster_id.txt";
        conf.disease_vertex_precluster_mapping = "../../compare3/preclustering/disease/disease_vertex_precluster_mapping.txt";
        conf.disease_precluster_index_mapping = "../../compare3/preclustering/disease/disease_precluster_index_mapping.txt";
        //Settings for the drug, gene and disease precluster matrix
        conf.drug_disease_precluster_matrix = "../../compare3/repos/drug_disease_precluster_matrix.txt";
        conf.drug_gene_precluster_matrix = "../../compare3/repos/drug_gene_precluster_matrix.txt";
        conf.gene_disease_precluster_matrix = "../../compare3/repos/gene_disease_precluster_matrix.txt";
        //Setting for gsp
        conf.gsp = "../../compare3/cv/compare3_gsp.txt";
        conf.gsn = "../../compare3/gsn/compare3_gsn.txt";
        //Settings for repositioning
        conf.reposConfig = new Config();
        conf.reposConfig.input = "../../compare3/repos/inputxml.txt";
        conf.reposConfig.clusterOutput = "../../compare3/repos/repos_cluster_out.txt";
        conf.reposConfig.editOutput = null;
        conf.reposConfig.graphOutput = null;
        conf.reposConfig.graphType = 5;
        conf.reposConfig.isXmlFormat = true;
        conf.reposConfig.p = new Param(conf.paramFile);
        conf.repos_output = "../../compare3/repos/parsed_repos_out.txt";
        //Settings for cross validation
        conf.precluster_parsed_entity = "../../compare3/cv/entity.txt";
        conf.cvConfig = new Config();
        conf.cvConfig.input = "../../compare3/cv/inputxml.txt";
        conf.cvConfig.editOutput = null;
        conf.cvConfig.p = new Param(conf.paramFile);
        conf.cvConfig.p.setThresh(0.45F);
        conf.cvConfig.p.setThreshArray(null);
        conf.cvConfig.clusterOutput = "../../compare3/cv/output/cluster_out.txt";
        conf.cvConfig.graphOutput = null;
        conf.cvConfig.graphType = 5;
        conf.cvConfig.isXmlFormat = true;
        conf.posEw = 1;
        conf.negEw = 0;
        conf.cv_prop = 0.1F;
        conf.cv_result_output = "../../compare3/cv/cv_parsed_res.txt";
        conf.roc_output = "../../compare3/cv/roc.txt";
        conf.drug_disease_cv_matrix = "../../compare3/cv/drug_disease_cv_matrix.txt";
        conf.drug_disease_precluster_cv_matrix = "../../compare3/cv/drug_disease_precluster_cv_matrix.txt";
        //Sematic output
        conf.semanticGoldFile = "../../sematic/drug_repos_silver.txt";
        conf.semanticSilverFile = "../../sematic/drug_repos_gold.txt";
        conf.semanticOutput = "../../repos/sematic_output.txt";
        conf.semanticSumOutput = "../../repos/semantic_sum.txt";
        // Disease to cui map
        conf.disease_cui_map = "../../assoc/disease_cui_assoc.txt";
        // The switch of the similar repositioning
        conf.simRepos = true;
        conf.simReposTh = 0.80F;
        conf.remove_dissimilar_disease = false;
        conf.disease_to_remove_file = "../../compare3/id/compare3_disease_to_remove.txt";
        
    }
    
    public void initIncomp(DrugReposConfig conf){
        //Drug bank xml
        conf.drug_xml = "../../drugbank/drugbank.xml";
        // Id Files
        conf.drug_id = "../../id/drug_id.txt";
        conf.cas_id = "../../id/cas_number.txt";
        conf.gene_id = "../../id/gene_id.txt";
        conf.disease_id = "../../id/disease_id.txt";
        // Smiles file
        conf.drug_smiles = "../../smiles/drug_smiles.txt";
        //Associations
        conf.drug_disease_assoc = "../../assoc/drug_disease_assoc.txt";
        conf.drug_gene_assoc = "../../assoc/drug_gene_assoc.txt";
        conf.gene_disease_assoc = "../../assoc/gene_disease_assoc.txt";
        // Matrix
        conf.drug_matrix = "../../incomplete/drug_incomp_matrix.txt";
        conf.gene_matrix = "../../incomplete/gene_incomp_matrix.txt";
        conf.disease_matrix = "../../incomplete/disease_incomp_matrix.txt";
        conf.drug_gene_matrix = "../../matrix/drug_gene_matrix.txt";
        conf.drug_disease_matrix = "../../matrix/drug_disease_matrix.txt";
        conf.gene_disease_matrix = "../../matrix/gene_disease_matrix.txt";
        //Precluster settings;
        //Precluster configs for drug
        conf.drugPreClustConfig = new Config();
        conf.drugPreClustConfig.input = "../../preclustering/drug/input.xml";
        conf.drugPreClustConfig.clusterOutput = "../../preclustering/drug/drug_preclusters.txt";
        conf.drugPreClustConfig.editOutput = null;
        conf.drugPreClustConfig.graphOutput = null;
        conf.drugPreClustConfig.graphType = 4;
        conf.drugPreClustConfig.isXmlFormat = true;
        conf.drugPreClustConfig.p = new Param(conf.paramFile);
        conf.drugPreClustConfig.p.setThresh(0.85F);
        conf.drugPreClustConfig.p.setThreshArray(null);
        conf.drug_precluster_id = "../../preclustering/drug/drug_precluster_id.txt";
        conf.drug_vertex_precluster_mapping = "../../preclustering/drug/drug_vertex_precluster_mapping.txt";
        conf.drug_precluster_index_mapping = "../../preclustering/drug/drug_precluster_index_mapping.txt";
        //Precluster configs for gene
        conf.genePreClustConfig = new Config();
        conf.genePreClustConfig.input = "../../preclustering/gene/input.xml";
        conf.genePreClustConfig.clusterOutput = "../../preclustering/gene/gene_preclusters.txt";
        conf.genePreClustConfig.editOutput = null;
        conf.genePreClustConfig.graphOutput = null;
        conf.genePreClustConfig.graphType = 4;
        conf.genePreClustConfig.isXmlFormat = true;
        conf.genePreClustConfig.p = new Param(conf.paramFile);
        conf.genePreClustConfig.p.setThresh(37.0F);
        conf.genePreClustConfig.p.setThreshArray(null);
        conf.genePreClustConfig.p = new Param(conf.paramFile);
        conf.genePreClustConfig.p.setThresh(37.0F);
        conf.genePreClustConfig.p.setThreshArray(null);
        conf.gene_precluster_id = "../../preclustering/gene/gene_precluster_id.txt";
        conf.gene_vertex_precluster_mapping = "../../preclustering/gene/gene_vertex_precluster_mapping.txt";
        conf.gene_precluster_index_mapping = "../../preclustering/gene/gene_precluster_index_mapping.txt";
        //Precluser configs for disease
        conf.diseasePreClustConfig = new Config();
        conf.diseasePreClustConfig.input = "../../preclustering/disease/input.xml";
        conf.diseasePreClustConfig.clusterOutput = "../../preclustering/disease/disease_preclusters.txt";
        conf.diseasePreClustConfig.editOutput = null;
        conf.diseasePreClustConfig.graphOutput = null;
        conf.diseasePreClustConfig.graphType = 4;
        conf.diseasePreClustConfig.isXmlFormat = true;
        conf.diseasePreClustConfig.p = new Param(conf.paramFile);
        conf.diseasePreClustConfig.p.setThreshArray(null);
        conf.diseasePreClustConfig.p.setThresh(0.8F);
        conf.diseasePreClustConfig.p.setThreshArray(null);
        conf.disease_precluster_id = "../../preclustering/disease/disease_precluster_id.txt";
        conf.disease_vertex_precluster_mapping = "../../preclustering/disease/disease_vertex_precluster_mapping.txt";
        conf.disease_precluster_index_mapping = "../../preclustering/disease/disease_precluster_index_mapping.txt";
        //Settings for the drug, gene and disease precluster matrix
        conf.drug_disease_precluster_matrix = "../../repos/drug_disease_precluster_matrix.txt";
        conf.drug_gene_precluster_matrix = "../../repos/drug_gene_precluster_matrix.txt";
        conf.gene_disease_precluster_matrix = "../../repos/gene_disease_precluster_matrix.txt";
        //Setting for gsp
        conf.gsp = "../../cv/cv_gsp.txt";
        conf.gsn = "../../gsn/negative_0.3.txt";
        //Settings for repositioning
        conf.reposConfig = new Config();
        conf.reposConfig.input = "../../repos/inputxml.txt";
        conf.reposConfig.clusterOutput = "../../repos/repos_cluster_out.txt";
        conf.reposConfig.editOutput = null;
        conf.reposConfig.graphOutput = null;
        conf.reposConfig.graphType = 5;
        conf.reposConfig.isXmlFormat = true;
        conf.reposConfig.p = new Param(conf.paramFile);
        conf.repos_output = "../../repos/parsed_repos_out.txt";
        //Settings for cross validation
        conf.precluster_parsed_entity = "../../cv/entity.txt";
        conf.cvConfig = new Config();
        conf.cvConfig.input = "../../cv/inputxml.txt";
        conf.cvConfig.editOutput = null;
        conf.cvConfig.p = new Param(conf.paramFile);
        conf.cvConfig.p.setThresh(0.45F);
        conf.cvConfig.p.setThreshArray(null);
        conf.cvConfig.clusterOutput = "../../cv/output/cluster_out.txt";
        conf.cvConfig.graphOutput = null;
        conf.cvConfig.graphType = 5;
        conf.cvConfig.isXmlFormat = true;
        conf.posEw = 1;
        conf.negEw = 0;
        conf.cv_prop = 0.1F;
        conf.cv_result_output = "../../cv/cv_parsed_res.txt";
        conf.roc_output = "../../cv/roc.txt";
        conf.drug_disease_cv_matrix = "../../cv/drug_disease_cv_matrix.txt";
        conf.drug_disease_precluster_cv_matrix = "../../cv/drug_disease_precluster_cv_matrix.txt";
        //Sematic output
        conf.semanticGoldFile = "../../sematic/drug_repos_silver.txt";
        conf.semanticSilverFile = "../../sematic/drug_repos_gold.txt";
        conf.semanticOutput = "../../repos/sematic_output.txt";
        conf.semanticSumOutput = "../../repos/semantic_sum.txt";
        // Disease to cui map
        conf.disease_cui_map = "../../assoc/disease_cui_assoc.txt";
        // The switch of the similar repositioning
        conf.simRepos = true;
        conf.simReposTh = 0.85F;
        conf.remove_dissimilar_disease = true;
        conf.disease_to_remove_file = "../../disease/disease_to_remove_id.txt";
    }
    
    public void initFilterIncomp(DrugReposConfig conf){
        //Drug bank xml
        conf.drug_xml = "../../drugbank/drugbank.xml";
        // Id Files
        conf.drug_id = "../../id/drug_id.txt";
        conf.cas_id = "../../id/cas_number.txt";
        conf.gene_id = "../../id/gene_id.txt";
        conf.disease_id = "../../id/disease_id.txt";
        // Smiles file
        conf.drug_smiles = "../../smiles/drug_smiles.txt";
        //Associations
        conf.drug_disease_assoc = "../../incomplete/drug_disease_incomp_assoc.txt";
        conf.drug_gene_assoc = "../../assoc/drug_gene_assoc.txt";
        conf.gene_disease_assoc = "../../assoc/gene_disease_assoc.txt";
        // Matrix
        conf.drug_matrix = "../../incomplete/drug_incomp_matrix.txt";
        conf.gene_matrix = "../../matrix/gene_matrix.txt";
        conf.disease_matrix = "../../disease/disease_matrix.txt";
        conf.drug_gene_matrix = "../../incomplete/drug_gene_incomp_matrix.txt";
        conf.drug_disease_matrix = "../../incomplete/drug_disease_incomp_matrix.txt";
        conf.gene_disease_matrix = "../../matrix/gene_disease_matrix.txt";
        //Precluster settings;
        //Precluster configs for drug
        conf.drugPreClustConfig = new Config();
        conf.drugPreClustConfig.input = "../../preclustering/drug/input.xml";
        conf.drugPreClustConfig.clusterOutput = "../../preclustering/drug/drug_preclusters.txt";
        conf.drugPreClustConfig.editOutput = null;
        conf.drugPreClustConfig.graphOutput = null;
        conf.drugPreClustConfig.graphType = 4;
        conf.drugPreClustConfig.isXmlFormat = true;
        conf.drugPreClustConfig.p = new Param(conf.paramFile);
        conf.drugPreClustConfig.p.setThresh(0.85F);
        conf.drugPreClustConfig.p.setThreshArray(null);
        conf.drug_precluster_id = "../../preclustering/drug/drug_precluster_id.txt";
        conf.drug_vertex_precluster_mapping = "../../preclustering/drug/drug_vertex_precluster_mapping.txt";
        conf.drug_precluster_index_mapping = "../../preclustering/drug/drug_precluster_index_mapping.txt";
        //Precluster configs for gene
        conf.genePreClustConfig = new Config();
        conf.genePreClustConfig.input = "../../preclustering/gene/input.xml";
        conf.genePreClustConfig.clusterOutput = "../../preclustering/gene/gene_preclusters.txt";
        conf.genePreClustConfig.editOutput = null;
        conf.genePreClustConfig.graphOutput = null;
        conf.genePreClustConfig.graphType = 4;
        conf.genePreClustConfig.isXmlFormat = true;
        conf.genePreClustConfig.p = new Param(conf.paramFile);
        conf.genePreClustConfig.p.setThresh(37.0F);
        conf.genePreClustConfig.p.setThreshArray(null);
        conf.genePreClustConfig.p = new Param(conf.paramFile);
        conf.genePreClustConfig.p.setThresh(37.0F);
        conf.genePreClustConfig.p.setThreshArray(null);
        conf.gene_precluster_id = "../../preclustering/gene/gene_precluster_id.txt";
        conf.gene_vertex_precluster_mapping = "../../preclustering/gene/gene_vertex_precluster_mapping.txt";
        conf.gene_precluster_index_mapping = "../../preclustering/gene/gene_precluster_index_mapping.txt";
        //Precluser configs for disease
        conf.diseasePreClustConfig = new Config();
        conf.diseasePreClustConfig.input = "../../preclustering/disease/input.xml";
        conf.diseasePreClustConfig.clusterOutput = "../../preclustering/disease/disease_preclusters.txt";
        conf.diseasePreClustConfig.editOutput = null;
        conf.diseasePreClustConfig.graphOutput = null;
        conf.diseasePreClustConfig.graphType = 4;
        conf.diseasePreClustConfig.isXmlFormat = true;
        conf.diseasePreClustConfig.p = new Param(conf.paramFile);
        conf.diseasePreClustConfig.p.setThreshArray(null);
        conf.diseasePreClustConfig.p.setThresh(0.8F);
        conf.diseasePreClustConfig.p.setThreshArray(null);
        conf.disease_precluster_id = "../../preclustering/disease/disease_precluster_id.txt";
        conf.disease_vertex_precluster_mapping = "../../preclustering/disease/disease_vertex_precluster_mapping.txt";
        conf.disease_precluster_index_mapping = "../../preclustering/disease/disease_precluster_index_mapping.txt";
        //Settings for the drug, gene and disease precluster matrix
        conf.drug_disease_precluster_matrix = "../../repos/drug_disease_precluster_matrix.txt";
        conf.drug_gene_precluster_matrix = "../../repos/drug_gene_precluster_matrix.txt";
        conf.gene_disease_precluster_matrix = "../../repos/gene_disease_precluster_matrix.txt";
        //Setting for gsp
        conf.gsp = "../../cv/cv_gsp.txt";
        conf.gsn = "../../gsn/negative_0.3.txt";
        //Settings for repositioning
        conf.reposConfig = new Config();
        conf.reposConfig.input = "../../repos/inputxml.txt";
        conf.reposConfig.clusterOutput = "../../repos/repos_cluster_out.txt";
        conf.reposConfig.editOutput = null;
        conf.reposConfig.graphOutput = null;
        conf.reposConfig.graphType = 5;
        conf.reposConfig.isXmlFormat = true;
        conf.reposConfig.p = new Param(conf.paramFile);
        conf.repos_output = "../../repos/parsed_repos_out.txt";
        //Settings for cross validation
        conf.precluster_parsed_entity = "../../cv/entity.txt";
        conf.cvConfig = new Config();
        conf.cvConfig.input = "../../cv/inputxml.txt";
        conf.cvConfig.editOutput = null;
        conf.cvConfig.p = new Param(conf.paramFile);
        conf.cvConfig.p.setThresh(0.45F);
        conf.cvConfig.p.setThreshArray(null);
        conf.cvConfig.clusterOutput = "../../cv/output/cluster_out.txt";
        conf.cvConfig.graphOutput = null;
        conf.cvConfig.graphType = 5;
        conf.cvConfig.isXmlFormat = true;
        conf.posEw = 1;
        conf.negEw = 0;
        conf.cv_prop = 0.1F;
        conf.cv_result_output = "../../cv/cv_parsed_res.txt";
        conf.roc_output = "../../cv/roc.txt";
        conf.drug_disease_cv_matrix = "../../cv/drug_disease_cv_matrix.txt";
        conf.drug_disease_precluster_cv_matrix = "../../cv/drug_disease_precluster_cv_matrix.txt";
        //Sematic output
        conf.semanticGoldFile = "../../sematic/drug_repos_silver.txt";
        conf.semanticSilverFile = "../../sematic/drug_repos_gold.txt";
        conf.semanticOutput = "../../repos/sematic_output.txt";
        conf.semanticSumOutput = "../../repos/semantic_sum.txt";
        // Disease to cui map
        conf.disease_cui_map = "../../assoc/disease_cui_assoc.txt";
        // The switch of the similar repositioning
        conf.simRepos = true;
        conf.simReposTh = 0.85F;
        conf.remove_dissimilar_disease = true;
        conf.disease_to_remove_file = "../../disease/disease_to_remove_id.txt";
    }
    /*
    public void initDrugReposConfig(DrugReposConfig conf) {
        conf.drug_id = "../../id/drug_id.txt";
        conf.gene_id = "../../id/gene_id.txt";
        conf.disease_id = "../../id/cui.txt";
        //Associations
        conf.drug_disease_assoc = "../../assoc/drug_cui_assoc.txt";
        conf.drug_gene_assoc = "../../assoc/drug_gene_assoc.txt";
        conf.gene_disease_assoc = "../../assoc/gene_cui_assoc.txt";
        // Matrix
        conf.drug_matrix = "../../matrix/new_drug_matrix.txt";
        conf.gene_matrix = "../../matrix/gene_matrix.txt";
        conf.disease_matrix = "../../matrix/cui_matrix.txt";
        conf.drug_gene_matrix = "../../matrix/drug_gene_matrix.txt";
        conf.drug_disease_matrix = "../../matrix/drug_cui_matrix.txt";
        conf.gene_disease_matrix = "../../matrix/gene_cui_matrix.txt";
        //Precluster settings;
        //Precluster configs for drug
        conf.drugPreClustConfig = new Config();
        conf.drugPreClustConfig.input = "../../preclustering/drug/input.xml";
        conf.drugPreClustConfig.clusterOutput = "../../preclustering/drug/drug_preclusters.txt";
        conf.drugPreClustConfig.editOutput = null;
        conf.drugPreClustConfig.graphOutput = null;
        conf.drugPreClustConfig.graphType = 4;
        conf.drugPreClustConfig.isXmlFormat = true;
        conf.drugPreClustConfig.p = new Param(conf.paramFile);
        conf.drugPreClustConfig.p.setThresh(0.9F);
        conf.drugPreClustConfig.p.setThreshArray(null);
        conf.drug_precluster_id = "../../preclustering/drug/drug_precluster_id.txt";
        conf.drug_vertex_precluster_mapping = "../../preclustering/drug/drug_vertex_precluster_mapping.txt";
        conf.drug_precluster_index_mapping = "../../preclustering/drug/drug_precluster_index_mapping.txt";
        //Precluster configs for gene
        conf.genePreClustConfig = new Config();
        conf.genePreClustConfig.input = "../../preclustering/gene/input.xml";
        conf.genePreClustConfig.clusterOutput = "../../preclustering/gene/gene_preclusters.txt";
        conf.genePreClustConfig.editOutput = null;
        conf.genePreClustConfig.graphOutput = null;
        conf.genePreClustConfig.graphType = 4;
        conf.genePreClustConfig.isXmlFormat = true;
        conf.genePreClustConfig.p = new Param(conf.paramFile);
        conf.genePreClustConfig.p.setThresh(37.0F);
        conf.genePreClustConfig.p.setThreshArray(null);
        conf.gene_precluster_id = "../../preclustering/gene/gene_precluster_id.txt";
        conf.gene_vertex_precluster_mapping = "../../preclustering/gene/gene_vertex_precluster_mapping.txt";
        conf.gene_precluster_index_mapping = "../../preclustering/gene/gene_precluster_index_mapping.txt";
        //Precluser configs for disease
        conf.diseasePreClustConfig = new Config();
        conf.diseasePreClustConfig.input = "../../preclustering/disease/input.xml";
        conf.diseasePreClustConfig.clusterOutput = "../../preclustering/disease/disease_preclusters.txt";
        conf.diseasePreClustConfig.editOutput = null;
        conf.diseasePreClustConfig.graphOutput = null;
        conf.diseasePreClustConfig.graphType = 4;
        conf.diseasePreClustConfig.isXmlFormat = true;
        conf.diseasePreClustConfig.p = new Param(conf.paramFile);
        conf.diseasePreClustConfig.p.setThreshArray(null);
        conf.diseasePreClustConfig.p.setThresh(0.9F);
        conf.disease_precluster_id = "../../preclustering/disease/disease_precluster_id.txt";
        conf.disease_vertex_precluster_mapping = "../../preclustering/disease/disease_vertex_precluster_mapping.txt";
        conf.disease_precluster_index_mapping = "../../preclustering/disease/disease_precluster_index_mapping.txt";
        //Settings for the drug, gene and disease precluster matrix
        conf.drug_disease_precluster_matrix = "../../repos/drug_disease_precluster_matrix.txt";
        conf.drug_gene_precluster_matrix = "../../repos/drug_gene_precluster_matrix.txt";
        conf.gene_disease_precluster_matrix = "../../repos/gene_disease_precluster_matrix.txt";
        //Setting for gsp
        conf.gsp = "../../cv/cv_gsp.txt";
        conf.gsn = "../../gsn/negative_0.3.txt";
        //Settings for repositioning
        conf.reposConfig = new Config();
        conf.reposConfig.input = "../../repos/inputxml.txt";
        conf.reposConfig.clusterOutput = "../../repos/repos_cluster_out.txt";
        conf.reposConfig.editOutput = null;
        conf.reposConfig.graphOutput = null;
        conf.reposConfig.graphType = 5;
        conf.reposConfig.isXmlFormat = true;
        conf.reposConfig.p = new Param(conf.paramFile);
        conf.repos_output = "../../repos/parsed_repos_out.txt";
        //Settings for cross validation
        conf.precluster_parsed_entity = "../../cv/entity.txt";
        conf.cvConfig = new Config();
        conf.cvConfig.input = "../../cv/inputxml.txt";
        conf.cvConfig.editOutput = null;
        conf.cvConfig.p = new Param(conf.paramFile);
        conf.cvConfig.p.setThresh(0.45F);
        conf.cvConfig.p.setThreshArray(null);
        conf.cvConfig.clusterOutput = "../../cv/output/cluster_out.txt";
        conf.cvConfig.graphOutput = null;
        conf.cvConfig.graphType = 5;
        conf.cvConfig.isXmlFormat = true;
        conf.posEw = 1;
        conf.negEw = 0;
        conf.cv_prop = 0.1F;
        conf.cv_result_output = "../../cv/cv_parsed_res.txt";
        conf.roc_output = "../../cv/roc.txt";
        conf.drug_disease_cv_matrix = "../../cv/drug_disease_cv_matrix.txt";
        conf.drug_disease_precluster_cv_matrix = "../../cv/drug_disease_precluster_cv_matrix.txt";
        //Sematic output
        conf.semanticGoldFile = "../../sematic/drug_repos_silver.txt";
        conf.semanticSilverFile = "../../sematic/drug_repos_gold.txt";
        conf.semanticOutput = "../../repos/sematic_output.txt";
        // The switch of the similar repositioning
        conf.simRepos = true;
        conf.simReposTh = 0.9F;
        conf.remove_dissimilar_disease = true;
        conf.disease_to_remove_file = "../../disease/disease_to_remove_id.txt";
    }
    */
}
