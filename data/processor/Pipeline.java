/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.processor;
import java.util.*;
import data.preprocess.*;
import data.io.*;
import data.init.InitDrugReposConfig;
/**
 *
 * @author penpen926
 */
public class Pipeline {
    public void preClusterPipeline(DrugReposConfig conf){
        PreClustering precluster = new PreClustering();
        precluster.preCluster(conf, "drug");
        precluster.preCluster(conf, "gene");
        precluster.preCluster(conf, "disease");
        PreClusterParser parser = new PreClusterParser();
        parser.parsePreCluster(conf);
    }
    
    public void cvPipeline(DrugReposConfig conf){
        //initDrugReposConfig(conf);
        CrossValidation cv = new CrossValidation();
        cv.genCrossValid(conf);
    }
    
    public void rocPipeline(DrugReposConfig conf){
        
        cvPipeline(conf);
        // For test
        //String testOutput = "../../disease/test.txt";
        //try{checkHighestSim(crossValidMap, drugDiseaseAssoc, testOutput);} catch(IOException e){e.printStackTrace();}
        //System.out.println("H sim test finished");
        
        System.out.println("Drug - disease matrix for cross-validation has been written to:  "+conf.drug_disease_cv_matrix);
        PreClusterParser parser = new PreClusterParser();
        
        parser.createDrugDiseasePreclusterCvMatrix(conf);
        System.out.println("Drug - disease prcluster matrix has been written to:  "+conf.drug_disease_precluster_cv_matrix);
        
        nforce.graphs.NpartiteGraph resGraph = (nforce.graphs.NpartiteGraph)nforce.io.Main.runGraph(conf.cvConfig);
         
        // Parse the result into association file.
        
        ResParser resParser = new ResParser();
        
        //HashMap<String, HashSet<String>> parsedRes = resParser.parseRes3(clusterOutput, drugVertexPreClusterMap, diseaseVertexPreClusterMap);
        HashMap<String, HashSet<String>> parsedRes = resParser.parseRes2(resGraph, conf);
        
        ArrayList<String> diseaseList = new DataReader().readIds2(conf.disease_id);
        float[][] diseaseMatrix = new DataReader().readMatrix(conf.disease_matrix, diseaseList.size(), diseaseList.size());
        HashMap<String, HashSet<String>> cvMap = new DataReader().readMap(conf.gsp);
        System.out.println("Start sim disease repos.");
        //new SimDiseaseRepos().reposSimDisease(parsedRes, inputMap, diseaseMatrix, diseaseList, 0.9f);
        //HashMap<String, HashSet<String>> parsedRes = resParser.parseRes2(resGraph, 
          //      drugVertexPreClusterMap, 
            //    diseaseVertexPreClusterMap, 
              //  drugDiseaseAssoc);
        //For test output the pared result\
        if(conf.simRepos){
            System.out.println("Start sim disease repos.");
            new SimDiseaseRepos().reposSimDisease(parsedRes, diseaseMatrix, diseaseList, conf.simReposTh);
        }
        new DataWriter().writeHashMap2(parsedRes, conf.cv_result_output);
        
        
        
        System.out.println("Parsed result has been written.");
        
        // Parse the result and output the roc data.
        DataProcessor processor = new DataProcessor();
        processor.summarizeRes(conf,parsedRes, conf.cvConfig.p.getThresh(),true);
    }
    
    
    public void reposPipeline(DrugReposConfig conf){
        nforce.graphs.NpartiteGraph resGraph = (nforce.graphs.NpartiteGraph)nforce.io.Main.runGraph(conf.reposConfig);
         
        // Parse the result into association file.
        
        ResParser resParser = new ResParser();
        
        //HashMap<String, HashSet<String>> parsedRes = resParser.parseRes3(clusterOutput, drugVertexPreClusterMap, diseaseVertexPreClusterMap);
        HashMap<String, HashSet<String>> parsedRes = resParser.parseRepos(resGraph, conf);
        ArrayList<String> diseaseList = new DataReader().readIds2(conf.disease_id);
        float[][] diseaseMatrix = new DataReader().readMatrix(conf.disease_matrix, diseaseList.size(), diseaseList.size());
        HashMap<String, HashSet<String>> drugDiseaseAssoc = new DataReader().readMap(conf.drug_disease_assoc);
        if(conf.simRepos){
            System.out.println("Start sim disease repos.");
            new SimDiseaseRepos().reposSimDisease(parsedRes, diseaseMatrix, diseaseList, conf.simReposTh);
        }
        //Remove drug-disease assoc from the parsed res, for we only focus on the novel associations.
        resParser.filter(parsedRes, drugDiseaseAssoc);
        new DataWriter().writeHashMap2(parsedRes, conf.repos_output);
        new SematicValidation().runSematicValid2(conf,true);
    }
    
    public void runRoc(){
        DrugReposConfig conf = new DrugReposConfig();
        Pipeline pl = new Pipeline();
        new InitDrugReposConfig().initDrugReposConfig2(conf);
        
        float[] drug_thresh_array = { 0.8f, 0.85f,0.9f,0.95f};
        float[] disease_thresh_array = {0.5f,0.6f,0.7f,0.8f,0.85f,0.9f,0.95f};
        for(int i = 0;i < drug_thresh_array.length;i++)
            for(int j = 0;j<disease_thresh_array.length;j++){
                float drug_thresh = drug_thresh_array[i];
                float disease_thresh = disease_thresh_array[j];
                conf.roc_output = "../../cv/roc_"+drug_thresh+"_"+disease_thresh+".txt";
                conf.drugPreClustConfig.p.setThresh(drug_thresh);
                conf.diseasePreClustConfig.p.setThresh(disease_thresh);
                pl.preClusterPipeline(conf);
                pl.cvPipeline(conf);
                float thresh = 0.01f;
                for(thresh = 0.01f; thresh< 0.71f;thresh+=0.02){
                    conf.cvConfig.p.setThresh(thresh);
                    pl.rocPipeline(conf);
                }
            }
    }
    
    
    public void runRocCompare2(){
        DrugReposConfig conf = new DrugReposConfig();
        Pipeline pl = new Pipeline();
        new InitDrugReposConfig().initDrugReposConfig2(conf);
        conf.gsn = "../../gsn/negative_comp.txt";
        float[] drug_thresh_array = {0.85f};
        //{0.85f,0.95f};
        float[] disease_thresh_array = {0.85f};
        //{0.5f,0.6f,0.7f,0.8f,0.85f,0.9f,0.95f};
        for(int i = 0;i < drug_thresh_array.length;i++)
            for(int j = 0;j<disease_thresh_array.length;j++){
                float drug_thresh = drug_thresh_array[i];
                float disease_thresh = disease_thresh_array[j];
                conf.roc_output = "../../cv/roc_"+drug_thresh+"_"+disease_thresh+".txt";
                conf.drugPreClustConfig.p.setThresh(drug_thresh);
                conf.diseasePreClustConfig.p.setThresh(disease_thresh);
                //pl.preClusterPipeline(conf);
                pl.cvPipeline(conf);
                float thresh = 0.01f;
                for(thresh = 0.02f; thresh< 0.71f;thresh+=0.02){
                    conf.cvConfig.p.setThresh(thresh);
                    pl.rocPipeline(conf);
                }
            }
    }
    public void runRepos(){
        DrugReposConfig conf = new DrugReposConfig();
        new InitDrugReposConfig().initDrugReposConfig2(conf);
        float drug_thresh = 0.8f;
        float disease_thresh = 0.9f;
        conf.drugPreClustConfig.p.setThresh(drug_thresh);
        conf.diseasePreClustConfig.p.setThresh(disease_thresh);
        preClusterPipeline(conf);
        conf.semanticOutput = "../../repos/semantic_res.txt";
        conf.simReposTh= 0.9f;
        float thresh = 0.1f;
        for(thresh = 0.45f; thresh< 0.61f;thresh+=0.05){
            conf.reposConfig.p.setThresh(thresh);
            reposPipeline(conf);

        }  
    }
    public static void main(String args[]){
        //new Pipeline().runRepos();
        new Pipeline().runRoc();
        //new Pipeline().runRocCompare2();
        //pl.reposPipeline(conf);
        //pl.cvPipeline(conf);
        //for(float crossProp = 0.1f;crossProp<=1;crossProp+=0.1f){
         //   for(float th = 0.05f;th<1.01;th+=0.03f){
         //       for(int k=0 ;k<1;k++)
         //           new RocPipeLine().runRocCurve(th, 0.1f);
         //   }
                
        //}
        //float crossProp = 0.1f;
        //new PipeLine().run8(0.51f,0.5f);
        //new PipeLine().checkOutGraph();
        /*
        float low = Float.parseFloat(args[0]);
        float upper = Float.parseFloat(args[1]);
        float step = Float.parseFloat(args[2]);
        float crossProp = Float.parseFloat(args[3]);
        //new PipeLine().run6();
        for(float i=low;i<upper;i+=step)
           new PipeLine().run8(i,crossProp);
                */
        //new PipeLine().checkConnComp();
        
    }
}
