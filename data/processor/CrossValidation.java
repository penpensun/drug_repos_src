/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.processor;
import data.io.DataReader;
import data.io.DataWriter;
import biforce.graphs.*;
import java.util.ArrayList;
import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import biforce.graphs.NpartiteGraph;
import data.io.DrugReposConfig;
/**
 * This class generates the cross validation graph, and runs a pipeline for cross validation.
 * @author penpen926
 */
public class CrossValidation {
   
    
    public void genCrossValid(DrugReposConfig conf){
        DataReader reader = new DataReader();
        ArrayList<String> drugList = reader.readIds(conf.drug_id);
        ArrayList<String> diseaseList  = reader.readIds2(conf.disease_id);
        float matrix[][] = reader.readMatrix(conf.drug_disease_matrix, drugList.size(), diseaseList.size());
        ArrayList<Pair> edgePairs = new ArrayList<>();
        int numEdges = 0;
        for(int i=0;i<matrix.length;i++)
            for(int j=0;j<matrix[0].length;j++){
                if(matrix[i][j] == conf.posEw){
                    // If there is an edge between vertex i and vertex j.
                    edgePairs.add(new Pair(i,j));
                    numEdges++;
                }
            }
        int numToRemove = (int)(conf.cv_prop*numEdges);
        System.out.println("Number of edges:  "+numEdges);
        System.out.println("Number of edges to remove:  "+numToRemove);
        ArrayList<Pair> removedPairs = new ArrayList<>();
        for(int i=0;i<numToRemove;i++){
            if(i %50000 == 0)
                System.out.println(i/(float)numToRemove*100+"% finished.");
            int idxToRemove = (int)(Math.random()*edgePairs.size());
            Pair p = edgePairs.remove(idxToRemove);
            matrix[p.idx1][p.idx2] = conf.negEw;
            removedPairs.add(p);
        }
        //See if output is needed.
        HashMap<String, HashSet<String>> ans = null;
        if(conf.gsp != null){
            ans = outputCrossValidPos(removedPairs,drugList, diseaseList,conf.gsp);
        }
        //Write the matrix
        new DataWriter().writeMatrix(matrix, conf.drug_disease_cv_matrix);
    }
    /**
     * This method writes the "removed" pairs from cross-validation into the given output File.
     * The format:
     * drug1 \t disease1 \t disease2 ....
     * These associations are regarded as gold positive data.
     * @param removedPairList
     * @param drugList
     * @param diseaseList
     * @param outputFile 
     * @return  
     */
    private HashMap<String, HashSet<String>> outputCrossValidPos(ArrayList<Pair> removedPairList, 
            ArrayList<String> drugList,
            ArrayList<String> diseaseList,
            String outputFile){
        HashMap<String, HashSet<String>> map = new HashMap<>();
        for(Pair p : removedPairList){
            String drug = drugList.get(p.idx1);
            String disease = diseaseList.get(p.idx2);
            if(!map.containsKey(drug))
                map.put(drug, new HashSet<>());
            map.get(drug).add(disease);
        }
        // Write the hashmap
        new DataWriter().writeHashMap2(map, outputFile);
        return map;
    }
}

class Pair{
    int idx1;
    int idx2;
    public Pair(int idx1, int idx2){
        this.idx1 = idx1;
        this.idx2 = idx2;
    }
    
    
}
