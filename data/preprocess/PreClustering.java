/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package data.preprocess;
import biforce.io.*;
import data.io.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import nforce.graphs.Cluster;
import nforce.graphs.Vertex;
import nforce.io.*;
import nforce.graphs.Graph;
/**
 * This class pre-clusters the data for the later clustering on clusters.
 * @author Peng
 */
public class PreClustering {
    public void preCluster(String inputFile, String clusterOut,
            String indexMappingOut,
            String vertexMappingOut,
            String clusterPrefix,
            float threshold){
        String paramFile = "./parameters.ini";
        boolean isMultipleThresh = false;
       
        Main3.runGraphDrugReposPreClust(threshold, null,
                inputFile, clusterOut, paramFile,
                1, indexMappingOut, vertexMappingOut, clusterPrefix,isMultipleThresh);
    }
    
    public void preCluster(DrugReposConfig conf, String type){
        if(type.equalsIgnoreCase("drug")){
            Graph g = nforce.io.Main.runGraph(conf.drugPreClustConfig);
            g.writeClusterTo(conf.drugPreClustConfig.clusterOutput, true);
            writeVertexPreClusterMapping(g, conf.drug_vertex_precluster_mapping, "drug");
            writeClusterIndexMapping(g,conf.drug_precluster_index_mapping);
        }
        else if(type.equalsIgnoreCase("gene")){
            Graph g = nforce.io.Main.runGraph(conf.genePreClustConfig);
            g.writeClusterTo(conf.genePreClustConfig.clusterOutput, true);
            writeVertexPreClusterMapping(g, conf.gene_vertex_precluster_mapping, "gene");
            writeClusterIndexMapping(g,conf.gene_precluster_index_mapping);
        }
        
        else if(type.equalsIgnoreCase("disease")){
            Graph g = nforce.io.Main.runGraph(conf.diseasePreClustConfig);
            g.writeClusterTo(conf.diseasePreClustConfig.clusterOutput, true);
            writeVertexPreClusterMapping(g, conf.disease_vertex_precluster_mapping, "disease");
            writeClusterIndexMapping(g, conf.disease_precluster_index_mapping);
        }
    }
    
    
    /**
     * This method runs preclustering on drugs.
     * @param threshold
     */
    private void clusterDrug(float threshold){
        String dir = "0.8";
        String inputFile = "../../nforce_input/drug/inputxml.txt";
        String clustOut = "../../nforce_output/drug/"+dir+"/drug_clusters.txt";
        String indexMappingOut = "../../nforce_output/drug/"+dir+"/drug_cluster_index_mapping.txt";
        String vertexMapingOut = "../../nforce_output/drug/"+dir+"/drug_vertex_cluster_mapping.txt";
        String clusterPrefix = "drug";
        preCluster(inputFile,clustOut, 
                indexMappingOut,
                vertexMapingOut,
                clusterPrefix,
                threshold);
    }
    
    /**
     * This method runs preclustering on genes.
     * @param threshold
     */
    private void clusterGene(float threshold){
        String dir = "37";
        String inputFile = "../../nforce_input/gene/inputxml.txt";
        String clustOut = "../../nforce_output/gene/"+dir+"/gene_clusters.txt";
        String indexMappingOut = "../../nforce_output/gene/"+dir+"/gene_cluster_index_mapping.txt";
        String vertexMappingOut = "../../nforce_output/gene/"+dir+"/gene_vertex_cluster_mapping.txt";
        String clusterPrefix = "gene";
        preCluster(inputFile,clustOut,
                indexMappingOut,
                vertexMappingOut,
                clusterPrefix,
                threshold);
    }

    /**
     * This method runs preclustering on diseses.
     * @param threshold
     */
    private void clusterDisease(float threshold){
        String dir = "0.9";
        String inputFile = "../../nforce_input/disease/inputxml.txt";
        String clustOut = "../../nforce_output/disease/"+dir+"/disease_clusters.txt";
        String indexMappingOut = "../../nforce_output/disease/"+dir+"/disease_cluster_index_mapping.txt";
        String vertexMappingOut = "../../nforce_output/disease/"+dir+"/disease_vertex_cluster_mapping.txt";
        String clusterPrefix = "disease";
        preCluster(inputFile,clustOut,
                indexMappingOut,
                vertexMappingOut,
                clusterPrefix,
                threshold);
    }
    
    
    
    private void checkDiseaseSim(){
        String diseaseClusterFile = "../../nforce_output/disease/disease_vertex_cluster_mapping.txt";
        String diseaseIdFile = "../../disease/disease_id.txt";
        String diseaseMatrixFile = "../../disease/disease_matrix.txt";
        DataReader reader = new DataReader();
        ArrayList<String> diseaseList = reader.readIds2(diseaseIdFile);
        float[][] matrix = reader.readMatrix(diseaseMatrixFile, diseaseList.size(), diseaseList.size());
        HashMap<String, HashSet<String>> clusterMap = reader.readMapInReverseOrder(diseaseClusterFile);
        
        ArrayList<String> keySet = new ArrayList<>(clusterMap.keySet());
        
        for(String cluster:keySet){
            int numPair = keySet.size()*(keySet.size()-1)/2;
            ArrayList<String> diseases = new ArrayList<>(clusterMap.get(cluster));
            int numScore0 = 0;
            for(int i=0;i<diseases.size()-1;i++)
                for(int j=i+1;j<diseases.size();j++){
                    int idx1 = diseaseList.indexOf(diseases.get(i));
                    int idx2 = diseaseList.indexOf(diseases.get(j));
                    if(idx1 ==-1)
                        throw new IllegalStateException("disease i index -1:  "+diseases.get(i));
                    if(idx2 ==-1)
                        throw new IllegalStateException("disease j index -1:  "+diseases.get(j));
                    float ew = matrix[i][j];
                    if(ew==0.0f)
                        numScore0++;
                }
            System.out.println("Disease cluster name:\t"+cluster+"\tNumber of score 0 pairs:\t"+numScore0);
        }
        
    }
    
    private void check0pair(){
        String diseaseIdFile = "../../disease/disease_id.txt";
        String diseaseMatrixFile = "../../disease/disease_matrix.txt";
        DataReader reader = new DataReader();
        ArrayList<String> diseaseList = reader.readIds2(diseaseIdFile);
        String d1 = "Neutrophil Chemotactic Response, Abnormal";
        String d2 = "Hypertension, Malignant";
        int idx1 = diseaseList.indexOf(d1);
        int idx2 = diseaseList.indexOf(d2);
        float matrix[][] = reader.readMatrix(diseaseMatrixFile, diseaseList.size(), diseaseList.size());
        System.out.println("Sim:\t"+matrix[idx1][idx2]);
    }
    
    
    public void perfromPreClustering(float drugTh, float diseaseTh, float geneTh){
        //clusterDrug(drugTh);
        //clusterDisease(diseaseTh);
        clusterGene(geneTh);
    }
    
    /**
     * This method writes the indices of the vertices in each cluster to the given filePath.
     * This method is designed for drug-repositioning project.
     * @param filePath 
     */
    private void writeClusterIndexMapping(Graph graph, String filePath){
        try{
            FileWriter fw = new FileWriter(filePath);
            BufferedWriter bw = new BufferedWriter(fw);
            
            for(int i=0;i<graph.getClusters().size();i++){
                writeSingleClusterIndexMapping(bw, graph.getClusters().get(i));
            }
            bw.flush();
            bw.close();
            fw.close();
            
        }catch(IOException e){
            System.err.println("(MatrixGraph.writeXmlClusterTo) Cluster writing error.");
            e.printStackTrace();
            return;
        }
    }
    
    /**
     * This method writes the vertex -- precluster mapping to the given file path.
     * This method is designe d for drug repositioning.
     * @param filePath 
     */
    private final void writeVertexPreClusterMapping(Graph graph, String filePath, String clusterPrefix){
        try{
            FileWriter fw = new FileWriter(filePath);
            BufferedWriter bw = new BufferedWriter(fw);
            for(int i=0;i<graph.getClusters().size();i++){
                writeVertexSingleClusterMapping(bw,graph.getClusters().get(i),clusterPrefix);
            }
        }catch(IOException e){
            System.err.println("(MatrixGraph.writeVertexPreClusterMapping) vertex -- precluster mapping output error.");
        }
    }
    
    
    
    
    /**
     * This method write 
     * @param bw
     * @param cluster 
     */
    public void writeSingleClusterIndexMapping(BufferedWriter bw, Cluster cluster){
         ArrayList<Vertex> clusterVertices = cluster.getVertices();
        // For test
        if(clusterVertices.isEmpty()){
            System.out.println("Empty cluster");
            return;
        }
        try{
        bw.write("<cluster  "+clusterVertices.get(0).getClustNum()+">\n");
        /* We output the cluster in separated sets. */
        
        for(int j=0;j<clusterVertices.size();j++)
                bw.write(clusterVertices.get(j).getVtxIdx()+"\t");
            
        bw.write("\n");
        bw.flush();
        bw.write("</cluster>\n");
        }catch(IOException e){
            System.err.println("(MatrixGraph.writeSingleClusterIndexMapping) Single cluster output error.");
            e.printStackTrace();
            return;
        }
    }
    
    
    /**
     * This method writes the mapping from vertex to a single cluster for the given cluster,
     * using the given BufferedWriter.
     * @param bw
     * @param cluster
     * @param clusterPrefix 
     */
    private void writeVertexSingleClusterMapping(BufferedWriter bw, Cluster cluster, String clusterPrefix){
        ArrayList<Vertex> clusterVertices = cluster.getVertices();
        if(clusterVertices.isEmpty()){
            System.out.println("Empty cluster");
            return;
        }
        try{
            for(Vertex vtx: clusterVertices){
                bw.write(vtx.getValue()+"\t"+new StringBuilder(clusterPrefix).append("_").append(cluster.getClustIdx())+"\n");
            }
            bw.flush();
        }catch(IOException e){
            System.err.println("(MatrixGraph.writeVertexSingleClusterMapping) Output error.");
            e.printStackTrace();
            return;
        }
            
    }
    
    
    /**
     * This method writes a single cluster using the given BufferedWriter
     * @param bw 
     * @param cluster 
     */
    private void writeSingleCluster(BufferedWriter bw, Cluster cluster){
        ArrayList<Vertex> clusterVertices = cluster.getVertices();
        // For test
        if(clusterVertices.isEmpty()){
            System.out.println("Empty cluster");
            return;
        }
        try{
        bw.write("<cluster  "+clusterVertices.get(0).getClustNum()+">\n");
        /* We output the cluster in separated sets. */
        
        for(int j=0;j<clusterVertices.size();j++)
                bw.write(clusterVertices.get(j).getValue()+"\t");
            
        bw.write("\n");
        bw.flush();
        bw.write("</cluster>\n");
        }catch(IOException e){
            System.err.println("(MatrixHierGeneralGraph.writeSingleCluster) Single cluster output error.");
            e.printStackTrace();
            return;
        }
    }
    public static void main(String args[]){
       new PreClustering().perfromPreClustering(0.80f, 0.80f, 37);
    }
}
