/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.preprocess;
import data.io.DataReader;
import data.io.DataWriter;
import data.io.DrugReposConfig;
import java.util.*;
import java.io.*;
import data.processor.*;
/**
 *
 * @author penpen926
 */
public class PreClusterParser {
    public HashMap<String, String[]> parsePreCluster(String clusterFile, String clusterPrefix){
        try{
            FileReader fr = new FileReader(clusterFile);
            BufferedReader br = new BufferedReader(fr);
            String line = null;
            boolean clusterTag = false;
            String clustName = null;
            HashMap<String, String[]> ans = new HashMap<>();
            while((line = br.readLine())!= null){
                line = line.trim();
                if(line.isEmpty())
                    continue;
                if(line.startsWith("<cluster")){
                    if(!clusterTag){
                        clusterTag = true;
                        clustName = new StringBuilder(clusterPrefix).append("_")
                                .append(line.substring(line.lastIndexOf(" ")+1, line.lastIndexOf(">")))
                                .toString();
                    }
                    else
                        throw new IllegalStateException("(PreClusterParser.parsePreClusters) Unclosed cluster tag.");
                }
                else if(line.startsWith("</cluster>")){
                    if(clusterTag)
                        clusterTag = false;
                    else
                        throw new IllegalStateException("(PreClusterParser.parsePreClusters) Close tag before a cluster beginns.");
                }
                else{ // Process content line
                    // Check the tag opening tag.
                    if(!clusterTag || clustName == null)
                        throw new IllegalStateException("(PreClusterParser.parsePreClusters) Cluster content before the beginning of the cluster tag.");
                    String[] splits = line.split("\t");
                    ans.put(clustName, splits);
                    clustName = null;
                }
                
            }
            fr.close();
            br.close();
            return ans;
        }catch(IOException e){
            System.err.println("(PreClusterParser.parsePreClusters) File Read error.");
            e.printStackTrace();
        }
        return null;
    }
    
    
    public HashMap<String, int[]> parsePreClusterIndex(String clusterFile, String clusterPrefix){
        try{
            FileReader fr = new FileReader(clusterFile);
            BufferedReader br = new BufferedReader(fr);
            String line = null;
            boolean clusterTag = false;
            String clustName = null;
            HashMap<String, int[]> ans = new HashMap<>();
            while((line = br.readLine())!= null){
                line = line.trim();
                if(line.isEmpty())
                    continue;
                if(line.startsWith("<cluster")){
                    if(!clusterTag){
                        clusterTag = true;
                        clustName = new StringBuilder(clusterPrefix).append("_")
                                .append(line.substring(line.lastIndexOf(" ")+1, line.lastIndexOf(">")))
                                .toString();
                    }
                    else
                        throw new IllegalStateException("(PreClusterParser.parsePreClusters) Unclosed cluster tag.");
                }
                else if(line.startsWith("</cluster>")){
                    if(clusterTag)
                        clusterTag = false;
                    else
                        throw new IllegalStateException("(PreClusterParser.parsePreClusters) Close tag before a cluster beginns.");
                }
                else{ // Process content line
                    // Check the tag opening tag.
                    if(!clusterTag || clustName == null)
                        throw new IllegalStateException("(PreClusterParser.parsePreClusters) Cluster content before the beginning of the cluster tag.");
                    String[] splits = line.split("\t");
                    int[] indices = new int[splits.length];
                    for(int i=0;i<indices.length;i++)
                        indices[i] = Integer.parseInt(splits[i]);
                    ans.put(clustName, indices);
                    clustName = null;
                }
                
            }
            fr.close();
            br.close();
            return ans;
        }catch(IOException e){
            System.err.println("(PreClusterParser.parsePreClusters) File Read error.");
            e.printStackTrace();
        }
        return null;
    }
    
    public void createEntityFile(DrugReposConfig conf){
        HashMap<String, String[]> drugClusters = parsePreCluster(conf.drugPreClustConfig.clusterOutput, "drug");
        HashMap<String, String[]> diseaseClusters = parsePreCluster(conf.diseasePreClustConfig.clusterOutput, "disease");
        HashMap<String, String[]> geneClusters = parsePreCluster(conf.genePreClustConfig.clusterOutput, "gene");
        
        ArrayList<String> drugClusterList= new ArrayList<>(drugClusters.keySet());
        ArrayList<String> diseaseClusterList = new ArrayList<>(diseaseClusters.keySet());
        ArrayList<String> geneClusterList = new ArrayList<>(geneClusters.keySet());
        new DataWriter().writeIds(drugClusterList, conf.drug_precluster_id, "\n");
        new DataWriter().writeIds(geneClusterList, conf.gene_precluster_id, "\n");
        new DataWriter().writeIds(diseaseClusterList, conf.disease_precluster_id, "\n");
        FileWriter fw = null;
        BufferedWriter bw = null;
        try{
            fw = new FileWriter(conf.precluster_parsed_entity);
            bw = new BufferedWriter(fw);
            // Output drug entities.
            for(int i=0;i<drugClusterList.size()-1;i++)
                bw.write(drugClusterList.get(i)+"\t");
            bw.write(drugClusterList.get(drugClusterList.size()-1)+"\n");
            // Output gene name entities.
            for(int i=0;i<geneClusterList.size()-1;i++)
                bw.write(geneClusterList.get(i)+"\t");
            bw.write(geneClusterList.get(geneClusterList.size()-1)+"\n");
            // Output disease entities.
            for(int i=0;i<diseaseClusterList.size()-1;i++)
                bw.write(diseaseClusterList.get(i)+"\t");
            bw.write(diseaseClusterList.get(diseaseClusterList.size()-1)+"\n");
            
            bw.flush();
            bw.close();
            fw.close();
        }catch(IOException e){
            System.err.println("(PreClusterParser.createEntityFile)");
            e.printStackTrace();
            return;
        }
    }
    
    private void createMatrixFile(DrugReposConfig conf){
        
        DataReader reader = new DataReader();
        int numDrugs = reader.readIds(conf.drug_id).size();
        //Check the number of drugs
        if(numDrugs !=1543)
            throw new IllegalStateException("(PreClusterParser.createMatrixFile) The drug number is wrong:  "+numDrugs);
        int numGeneNames = reader.readIds(conf.gene_id).size();
        if(numGeneNames != 1622)
            throw new IllegalStateException("(PreClusterParser.createMatrixFile) The gene number is wrong: "+numGeneNames);
        
        int numDiseases = reader.readIds2(conf.disease_id).size();
        
        float[][] drugGeneMatrix = reader.readMatrix(conf.drug_gene_matrix, numDrugs, numGeneNames);
        float[][] geneNameDiseaseMatrix = reader.readMatrix(conf.gene_disease_matrix, numGeneNames, numDiseases);
        float[][] drugDiseaseMatrix  = reader.readMatrix(conf.drug_disease_matrix, numDrugs, numDiseases);
        
        HashMap<String, int[]> drugIndexClusters = parsePreClusterIndex(conf.drug_precluster_index_mapping, "drug");
        HashMap<String, int[]> geneIndexClusters = parsePreClusterIndex(conf.gene_precluster_index_mapping, "gene");
        HashMap<String, int[]> diseaseIndexClusters = parsePreClusterIndex(conf.disease_precluster_index_mapping, "disease");
        
        float preClusterDrugGeneMatrix[][] = new float[drugIndexClusters.size()][geneIndexClusters.size()];
        float preClusterGeneNameDiseaseMatrix[][]  = new float[geneIndexClusters.size()][diseaseIndexClusters.size()];
        float preClusterDrugDiseaseMatrix[][] = new float[drugIndexClusters.size()][diseaseIndexClusters.size()];
        
        ArrayList<String> drugClusterNames =reader.readIds(conf.drug_precluster_id);
        ArrayList<String> geneClusterNames = reader.readIds(conf.gene_precluster_id);
        ArrayList<String> diseaseClusterNames = reader.readIds(conf.disease_precluster_id);
        
        // Compute drug--gene name
        System.out.println("Drug-gene matrix.");
        System.out.println("DrugClusterNames size:  "+drugClusterNames.size());
        System.out.println("GeneNameClusterNames size:  "+geneClusterNames.size());
        for(int i=0;i<drugClusterNames.size();i++)
            for(int j=0;j<geneClusterNames.size();j++){
               
                if(i!=0 && j !=0 &&i*j %200000 == 0)
                    System.out.println(( (i-1)*geneClusterNames.size()+j)/(float)drugClusterNames.size()/geneClusterNames.size()*100+"% finished.");
                String drugClusterName  = drugClusterNames.get(i);
                String geneClusterName = geneClusterNames.get(j);
                if(drugClusterName.equals("drug_636") &&
                        geneClusterName.equals("gene_name_929"))
                    System.out.println();
                int[] dic = drugIndexClusters.get(drugClusterName);
                int[] gnic = geneIndexClusters.get(geneClusterName);
                float ew = computePreClusterSim(drugGeneMatrix, dic, gnic);
                preClusterDrugGeneMatrix[i][j] =ew;
            }
        // Compute drug -- disease matrix
        System.out.println("Drug-disease matrix.");
        for(int i=0;i<drugClusterNames.size();i++)
            for(int j=0;j<diseaseClusterNames.size();j++){
                if(i!=0 && j !=0 &&i*j %200000 == 0)
                    System.out.println(( (i-1)*diseaseClusterNames.size()+j)/(float)drugClusterNames.size()/diseaseClusterNames.size()*100+"% finished.");
                String drugClusterName  = drugClusterNames.get(i);
                String diseaseClusterName = diseaseClusterNames.get(j);
                int[] drugIdxCluster = drugIndexClusters.get(drugClusterName);
                int[] diseaseIdxCluster = diseaseIndexClusters.get(diseaseClusterName);
                float ew = computePreClusterSim(drugDiseaseMatrix, drugIdxCluster, diseaseIdxCluster);
                preClusterDrugDiseaseMatrix[i][j] =ew;
            }
        
        // Compute gene name -- disease matrix
        System.out.println("GeneName-disease matrix.");
        for(int i=0;i<geneClusterNames.size();i++)
            for(int j=0;j<diseaseClusterNames.size();j++){
                if(i!=0 && j !=0 &&i*j %200000 == 0)
                    System.out.println(( (i-1)*diseaseClusterNames.size()+j)/(float)geneClusterNames.size()/diseaseClusterNames.size()*100+"% finished.");
                String geneNameClusterName = geneClusterNames.get(i);
                String diseaseClusterName = diseaseClusterNames.get(j);
                int[] geneNameIdxCluster = geneIndexClusters.get(geneNameClusterName);
                int[] diseaseIdxCluster = diseaseIndexClusters.get(diseaseClusterName);
                float ew = computePreClusterSim(geneNameDiseaseMatrix, geneNameIdxCluster, diseaseIdxCluster);
                preClusterGeneNameDiseaseMatrix[i][j]= ew;
            }
        
        // Write the matrices.
        DataWriter writer = new DataWriter();
        writer.writeMatrix(preClusterDrugGeneMatrix, conf.drug_gene_precluster_matrix);
        writer.writeMatrix(preClusterDrugDiseaseMatrix,conf.drug_disease_precluster_matrix);
        writer.writeMatrix(preClusterGeneNameDiseaseMatrix, conf.gene_disease_precluster_matrix);
    }
    
    /**
     * 
     * @param conf 
     */
    public void createDrugDiseasePreclusterCvMatrix(DrugReposConfig conf){
        DataReader reader = new DataReader();
        int numDrugs = reader.readIds(conf.drug_id).size();
        int numDiseases = reader.readIds2(conf.disease_id).size();
        float[][] drugDiseaseMatrix  = reader.readMatrix(conf.drug_disease_cv_matrix, numDrugs, numDiseases);
        HashMap<String, int[]> drugPreClusterIndexMap = 
                parsePreClusterIndex(conf.drug_precluster_index_mapping, "drug");
        HashMap<String, int[]> diseasePreClusterIndexMap = 
                parsePreClusterIndex(conf.disease_precluster_index_mapping, "disease");

        float preClusterDrugDiseaseMatrix[][] = new float[drugPreClusterIndexMap.size()][diseasePreClusterIndexMap.size()];
        
        ArrayList<String> drugClusterNames =reader.readIds(conf.drug_precluster_id);;
        ArrayList<String> diseaseClusterNames = reader.readIds(conf.disease_precluster_id);
        // Compute drug -- disease matrix
        System.out.println("Drug-disease matrix.");
        for(int i=0;i<drugClusterNames.size();i++)
            for(int j=0;j<diseaseClusterNames.size();j++){
                if(i!=0 && j !=0 &&i*j %200000 == 0)
                    System.out.println(( (i-1)*diseaseClusterNames.size()+j)/(float)drugClusterNames.size()/diseaseClusterNames.size()*100+"% finished.");
                String drugClusterName  = drugClusterNames.get(i);
                String diseaseClusterName = diseaseClusterNames.get(j);
                
                int[] drugIdxCluster = drugPreClusterIndexMap.get(drugClusterName);
                int[] diseaseIdxCluster = diseasePreClusterIndexMap.get(diseaseClusterName);
                float ew = computePreClusterSim(drugDiseaseMatrix, drugIdxCluster, diseaseIdxCluster);
                preClusterDrugDiseaseMatrix[i][j] =ew;
            }
         // Write the matrices.
        DataWriter writer = new DataWriter();
        writer.writeMatrix(preClusterDrugDiseaseMatrix,conf.drug_disease_precluster_cv_matrix); 
    }
    /**
     * This method computes the similarity between two pre-clusters.
     * @param matrix
     * @param preCluster1
     * @param preCluster2
     * @return 
     */
    protected float computePreClusterSim(float[][] matrix,
            int[] preCluster1, int[] preCluster2){
        int total = preCluster1.length* preCluster2.length;
        int links = 0;
        for(int idx1: preCluster1)
            for(int idx2: preCluster2){
                if(idx1 == -1)
                    System.err.println("entity1 -1");
                if(idx2 == -1)
                    System.err.println("entity2 -1");
                if(matrix[idx1]
                        [idx2]>0)
                    links++;
            }
        return (float)links/total;
        //return (float)links;
    }
    
    
    public void parsePreCluster(DrugReposConfig conf){
        createEntityFile(conf);
        createMatrixFile(conf);
    }
 
}
