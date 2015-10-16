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

    public void initDrugReposConfig2(DrugReposConfig conf) {
        //Drug bank xml
        conf.drug_xml = "../../drugbank/drugbank.xml";
        // Id Files
        conf.drug_id = "../../id/drug_id.txt";
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
    
}