/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package data.processor;
import java.util.*;
import data.io.*;
import data.io.DrugReposConfig;
/**
 *
 * @author mac-97-41
 */
public class SimDiseaseRepos {
    /**
     * This method repos the very similar diseases.
     * For example we have drug1 --- disease1 and disease2 is VERY SIMILAR to disease1, e.g. sim>0.95
     * Then we believe that drug1 is also associated to disease2.
     * @param parsedRes
     * @param inputMap
     * @param matrix
     * @param diseaseList 
     * @param threshold 
     */
    public void reposSimDisease(HashMap<String, HashSet<String>> parsedRes, 
            float[][] matrix, 
            ArrayList<String> diseaseList, 
            float threshold){
        ArrayList<String> drugSet = new ArrayList<>(parsedRes.keySet());
        
        //Create a disease-index hashmap.
        HashMap<String, Integer> idxMap = new HashMap<>();
        for(int i=0;i<diseaseList.size();i++)
            idxMap.put(diseaseList.get(i), i);
        
        
        int counter = 0;
        for(String drug:drugSet){
            //HashSet<String> diseaseSet = inputMap.get(drug);
            HashSet<String> diseaseSet = parsedRes.get(drug);
            if(diseaseSet == null)
                continue;
            ArrayList<String> diseasesToAdd  = new ArrayList<>();
            counter++;
            if(counter % 1000  == 0)
                System.out.println((float)(counter)/drugSet.size()*100+"% is finished.");
            for(String d: diseaseList){
                if(diseaseSet.contains(d))
                    continue;
                if(checkMaxSim(d, idxMap, diseaseSet, matrix, threshold))
                    diseasesToAdd.add(d);
            }
            parsedRes.get(drug).addAll(diseasesToAdd);
        }
    }
    
    /**
     * Similar repositioning for similar repositioning
     * 
     * @param parsedRes
     * @param conf 
     */
    public void reposSimDisease(HashMap<String, HashSet<String>> parsedRes,
            DrugReposConfig conf){
        ArrayList<String> diseaseList = new DataReader().readIds2(conf.disease_id);
        float[][] matrix = new DataReader().readMatrix(conf.disease_matrix, diseaseList.size(), diseaseList.size());
        ArrayList<String> drugSet = new ArrayList<>(parsedRes.keySet());
        
        //Create a disease-index hashmap.
        HashMap<String, Integer> idxMap = new HashMap<>();
        for(int i=0;i<diseaseList.size();i++)
            idxMap.put(diseaseList.get(i), i);
        
        
        int counter = 0;
        for(String drug:drugSet){
            //HashSet<String> diseaseSet = inputMap.get(drug);
            HashSet<String> diseaseSet = parsedRes.get(drug);
            if(diseaseSet == null)
                continue;
            ArrayList<String> diseasesToAdd  = new ArrayList<>();
            counter++;
            if(counter % 1000  == 0)
                System.out.println((float)(counter)/drugSet.size()*100+"% is finished.");
            for(String d: diseaseList){
                if(diseaseSet.contains(d))
                    continue;
                if(checkMaxSim(d, idxMap, diseaseSet, matrix, conf.simReposTh))
                    diseasesToAdd.add(d);
            }
            parsedRes.get(drug).addAll(diseasesToAdd);
        }
    }
    
    
    public void reposSimDrug(HashMap<String, HashSet<String>> parsedRes,
            DrugReposConfig conf){
        ArrayList<String> drugList = new DataReader().readIds2(conf.drug_id);
        float[][] matrix = new DataReader().readMatrix(conf.drug_matrix, drugList.size(), drugList.size());
        ArrayList<String> drugSet = new ArrayList<>(parsedRes.keySet());
        
        
        
        int counter = 0;
        for(String drug:drugSet){
            //HashSet<String> diseaseSet = inputMap.get(drug);
            HashSet<String> diseaseSet = parsedRes.get(drug);
            if(diseaseSet == null)
                continue;
            ArrayList<String> drugToAdd  = new ArrayList<>();
            counter++;
            if(counter % 1000  == 0)
                System.out.println((float)(counter)/drugSet.size()*100+"% is finished.");
            for(String d: drugList){
                if(matrix[drugList.indexOf(drug)][drugList.indexOf(d)]>conf.simReposTh)
                    if(!parsedRes.containsKey(d))
                        parsedRes.put(d, diseaseSet);
                    else parsedRes.get(d).addAll(diseaseSet);
            }
        }
    
    }
    
    /**
     * This method checks if the given disease has a max sim beyond the threshold to the given diseaseSet.
     * i.e. There exists at least one disease d0 in diesaseSet, such that s(d0, disease)>threshold
     * @param disease
     * @param idxMap
     * @param diseaseSet
     * @param matrix
     * @param threshold
     * @return 
     */
    private boolean checkMaxSim(String disease, HashMap<String,Integer> idxMap,  HashSet<String> diseaseSet,float matrix[][], float threshold){
        int idx1 = -1;
        try{
            idx1 = idxMap.get(disease);
        }catch(NullPointerException e){
            System.err.println("Illegal index for disease: "+disease);
            e.printStackTrace();
        }
       
        for(String d:diseaseSet){
            int idx2 = -1;
            try{
                idx2 = idxMap.get(d);
            }catch(NullPointerException e){
            System.err.println("Illegal index for disease: "+d);
                e.printStackTrace();
            }
            
            if(idx1 == idx2)
                continue;
            if(matrix[idx1][idx2] > threshold)
                return true;
        }
        return false;
    }
    
    public ArrayList<String> extractSim0Disease(ArrayList<String> diseaseList, float[][] matrix, float th){
        ArrayList<String> ans = new ArrayList<>();
        for(int i=0;i<diseaseList.size();i++){
            boolean flag = true;
            for(int j=0;j<matrix.length;j++)
                if(matrix[i][j] >th)
                    flag = false;
            if(flag)
                ans.add(diseaseList.get(i));
        }
        return ans;
    }
    
    public static void main(String args[]){
        ArrayList<String> dl = new DataReader().readIds2("../../compare3/id/compare3_disease_id.txt");
        float[][] matrix = new DataReader().readMatrix("../../compare3/matrix/compare3_disease_matrix.txt",
                dl.size(), dl.size());
        ArrayList<String> toRem = new SimDiseaseRepos().extractSim0Disease(dl, matrix, 0);
        new DataWriter().writeIds(toRem, "../../compare3/id/compare3_disease_to_remove.txt", "\n");
    }

}
