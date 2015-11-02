/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.extractor;

import data.init.InitDrugReposConfig;
import data.io.DataReader;
import data.io.DataWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import data.io.DrugReposConfig;

/**
 *
 * @author penpen926
 */
public class DataGenerator {
    
    
    public void generateNegative(DrugReposConfig conf, int num){
        DataReader reader = new DataReader();
        ArrayList<String> drugList= reader.readIds(conf.drug_id);
        ArrayList<String> diseaseList = reader.readIds2(conf.disease_id);
        HashMap<String,HashSet<String>> relationMap = reader.readMap(conf.drug_disease_assoc);
        HashMap<String, String> negativeRelationMap = new HashMap<>();
        while(num-->0){
            boolean isPicked = false;
            while(!isPicked){
                String randomDrug = drugList.get((int)(Math.random()* drugList.size()));
                String randomDisease = diseaseList.get((int)(Math.random()*diseaseList.size()));
                /* Get the associated disease set of the randomly picked drug. */
                HashSet<String> associatedSet = relationMap.get(randomDrug);
                if(associatedSet == null || !associatedSet.contains(randomDisease)){
                    negativeRelationMap.put(randomDrug, randomDisease);
                    isPicked = true;
                }
                else{}
            }
        }
        DataWriter writer = new DataWriter();
        writer.writeHashMap4(negativeRelationMap, conf.gsn);
    }
    
    public void generateNegative(DrugReposConfig conf, float th){
        DataReader reader = new DataReader();
        ArrayList<String> drugList= reader.readIds(conf.drug_id);
        ArrayList<String> diseaseList = reader.readIds2(conf.disease_id);
        HashMap<String,HashSet<String>> relationMap = reader.readMap(conf.drug_disease_assoc);
        HashMap<String, HashSet<String>> negativeRelationMap = new HashMap<>();
        float simMatrix[][] = reader.readMatrix(conf.disease_matrix, diseaseList.size(), diseaseList.size());
        
        for(String drug: drugList){
            HashSet<String> diseaseSet = relationMap.get(drug);
            if(diseaseSet == null || diseaseSet.isEmpty())
                continue;
            if(!negativeRelationMap.containsKey(drug))
                negativeRelationMap.put(drug,new HashSet<>());
            
            for(String disease: diseaseSet){
                
                // Get the index of the current disease.
                int diseaseIdx = diseaseList.indexOf(disease.trim());
                if(diseaseIdx == -1)
                    continue;
                for(int i=0;i<diseaseList.size();i++){
                    if(simMatrix[i][diseaseIdx]<=th && !relationMap.get(drug).contains(diseaseList.get(i)))
                        // !relationMap.get(drug).contains(disease) is very important, it removes the positive records.
                        negativeRelationMap.get(drug).add(diseaseList.get(i));
                }
            }
        }
        DataWriter writer = new DataWriter();
        writer.writeHashMap2(negativeRelationMap, conf.gsn);
    }
    
    
    public void generateNegative(DrugReposConfig conf, float th, int num){
        DataReader reader = new DataReader();
        ArrayList<String> drugList= reader.readIds(conf.drug_id);
        ArrayList<String> diseaseList = reader.readIds2(conf.disease_id);
        HashMap<String,HashSet<String>> relationMap = reader.readMap(conf.drug_disease_assoc);
        
        HashMap<String, HashSet<String>> negativeRelationMap = new HashMap<>();
        float simMatrix[][] = reader.readMatrix(conf.disease_matrix, diseaseList.size(), diseaseList.size());
        
        for(String drug: drugList){
            HashSet<String> diseaseSet = relationMap.get(drug);
            if(diseaseSet == null || diseaseSet.isEmpty())
                continue;
            if(!negativeRelationMap.containsKey(drug))
                negativeRelationMap.put(drug,new HashSet<>());
            
            for(String disease: diseaseSet){
                
                // Get the index of the current disease.
                int diseaseIdx = diseaseList.indexOf(disease.trim());
                if(diseaseIdx == -1)
                    continue;
                for(int i=0;i<diseaseList.size();i++){
                    if(simMatrix[i][diseaseIdx]<=th && !relationMap.get(drug).contains(diseaseList.get(i)))
                        // !relationMap.get(drug).contains(disease) is very important, it removes the positive records.
                        negativeRelationMap.get(drug).add(diseaseList.get(i));
                }
            }
        }
        HashMap<String, HashSet<String>> ans = new HashMap<>();
        ArrayList<String> keySet = new ArrayList<>(negativeRelationMap.keySet());

        System.out.println("Start random selecting.");
        int count =0;
        while(count< num){
            String randomKey =keySet.get((int)(Math.random()*keySet.size()));
            ArrayList<String> diseaseSet = new ArrayList<>(negativeRelationMap.get(randomKey));
            String randomValue = diseaseSet.get((int)(Math.random()*diseaseSet.size()));
            if(!ans.containsKey(randomKey)){
                ans.put(randomKey,new HashSet<>());
                ans.get(randomKey).add(randomValue);
                count++;
            }else if(!ans.get(randomKey).contains(randomValue)){
                ans.get(randomKey).add(randomValue);
                count++;
            }
        }
            
        DataWriter writer = new DataWriter();
        writer.writeHashMap2(ans, conf.gsn);
    }
    
    public void generateNegativeSpecial(DrugReposConfig conf, float th, int num){
        DataReader reader = new DataReader();
        ArrayList<String> drugList= reader.readIds(conf.drug_id);
        ArrayList<String> diseaseList = reader.readIds2(conf.disease_id);
        HashMap<String,HashSet<String>> relationMap = reader.readMap(conf.drug_disease_assoc);
        HashMap<String, HashSet<String>> negativeRelationMap = new HashMap<>();
        float simMatrix[][] = reader.readMatrix(conf.disease_matrix, diseaseList.size(), diseaseList.size());
        
        for(String drug: drugList){
            HashSet<String> diseaseSet = relationMap.get(drug);
            if(diseaseSet == null || diseaseSet.isEmpty())
                continue;
            if(!negativeRelationMap.containsKey(drug))
                negativeRelationMap.put(drug,new HashSet<>());
            
            for(String disease: diseaseSet){
                
                // Get the index of the current disease.
                int diseaseIdx = diseaseList.indexOf(disease.trim());
                if(diseaseIdx == -1)
                    continue;
                for(int i=0;i<diseaseList.size();i++){
                    if(simMatrix[i][diseaseIdx]<=th && !relationMap.get(drug).contains(diseaseList.get(i)))
                        // !relationMap.get(drug).contains(disease) is very important, it removes the positive records.
                        negativeRelationMap.get(drug).add(diseaseList.get(i));
                }
            }
        }
        HashMap<String, HashSet<String>> ans = new HashMap<>();
        ArrayList<String> keySet = new ArrayList<>(negativeRelationMap.keySet());

        System.out.println("Start random selecting.");
        int count =0;
        while(count< num/3){
            String randomKey =keySet.get((int)(Math.random()*keySet.size()));
            ArrayList<String> diseaseSet = new ArrayList<>(negativeRelationMap.get(randomKey));
            String randomValue = diseaseSet.get((int)(Math.random()*diseaseSet.size()));
            if(!ans.containsKey(randomKey)){
                ans.put(randomKey,new HashSet<>());
                ans.get(randomKey).add(randomValue);
                count++;
            }else if(!ans.get(randomKey).contains(randomValue)){
                ans.get(randomKey).add(randomValue);
                count++;
            }
        }
        
        
        ArrayList<String> removedDiseases = reader.readIds2(conf.disease_to_remove_file);
        
        while(count<num){
            String randomKey =keySet.get((int)(Math.random()*keySet.size()));
            String randomValue = removedDiseases.get((int)(Math.random()*removedDiseases.size()));
            if(!ans.containsKey(randomKey)){
                ans.put(randomKey,new HashSet<>());
                ans.get(randomKey).add(randomValue);
                count++;
            }else if(!ans.get(randomKey).contains(randomValue)){
                ans.get(randomKey).add(randomValue);
                count++;
            }
        }
            
        DataWriter writer = new DataWriter();
        writer.writeHashMap2(ans, conf.gsn);
    }
    
    
    public void generateNegativeCompare2(DrugReposConfig conf, int num){
        HashMap<String, HashSet<String>> compare2Gsp = new DataReader().readMap(conf.gsp);
        ArrayList<String> drugList = new ArrayList<>(compare2Gsp.keySet());
        HashSet<String> diseaseList = new HashSet<>();
        for(HashSet<String> value: compare2Gsp.values()){
            diseaseList.addAll(value);
        }
        HashMap<String, HashSet<String>> negativeRelationMap = new HashMap<>();
        for(String drug: drugList){
            HashSet<String> diseaseSet = compare2Gsp.get(drug);
            if(diseaseSet == null || diseaseSet.isEmpty())
                continue;
            if(!negativeRelationMap.containsKey(drug))
                negativeRelationMap.put(drug,new HashSet<>());
            
            for(String disease: diseaseList)
                if(!diseaseSet.contains(disease))
                    negativeRelationMap.get(drug).add(disease);
            
        }
        HashMap<String, HashSet<String>> ans = new HashMap<>();
        ArrayList<String> keySet = new ArrayList<>(negativeRelationMap.keySet());

        System.out.println("Start random selecting.");
        int count =0;
        while(count< num){
            String randomKey =keySet.get((int)(Math.random()*keySet.size()));
            ArrayList<String> diseaseSet = new ArrayList<>(negativeRelationMap.get(randomKey));
            String randomValue = diseaseSet.get((int)(Math.random()*diseaseSet.size()));
            if(!ans.containsKey(randomKey)){
                ans.put(randomKey,new HashSet<>());
                ans.get(randomKey).add(randomValue);
                count++;
            }else if(!ans.get(randomKey).contains(randomValue)){
                ans.get(randomKey).add(randomValue);
                count++;
            }
        }
            
        DataWriter writer = new DataWriter();
        writer.writeHashMap2(ans, conf.gsn);
        
        
    }
    
    /**
     * This method generates the negative disease set based on disease similarity.
     * If drug1 -- disease1 and disease2 is not similar to disease1, then we assume drug1 -- disease2 is not associated.
     * @param diseaseFile disease list
     * @param relationFile drug disease relation
     * @param matrixFile disease-disease matrix
     * @param outputFile output file
     * @param simThresh Thresh of similarity
     * @param num
     * @param diseaseToRemoveFile
     */
    public void generateNegative2(
            String diseaseFile,
            String relationFile, 
            String matrixFile, 
            String outputFile, 
            float simThresh,
        int num){
        DataReader reader = new DataReader();
        ArrayList<String> diseaseList = reader.readIds2(diseaseFile);
        HashMap<String, HashSet<String>> relationMap = reader.readMap(relationFile);
        HashMap<String, HashSet<String>> negativeRelationMap = new HashMap<>();
        float simMatrix[][] = reader.readMatrix(matrixFile, diseaseList.size(), diseaseList.size());
        Set<Map.Entry<String,HashSet<String>> >relationSet = relationMap.entrySet();
        Iterator<Map.Entry<String,HashSet<String>>> iterator = relationSet.iterator();
        int count=0;
        while(iterator.hasNext()){
            Map.Entry<String,HashSet<String>> entry = iterator.next();
            String drug = entry.getKey();
            HashSet<String> diseaseSet = entry.getValue();
            
            if(!negativeRelationMap.containsKey(drug))
                negativeRelationMap.put(drug,new HashSet<>());
            // Iterate over the disease set and find the unsimilar diseases. 
            Iterator<String> diseaseIter = diseaseSet.iterator();
            while(diseaseIter.hasNext()){
                String disease = diseaseIter.next();
                
                // Get the index of the current disease.
                int diseaseIdx = diseaseList.indexOf(disease.trim());
                if(diseaseIdx == -1)
                    continue;
                for(int i=0;i<diseaseList.size();i++){
                    if(simMatrix[i][diseaseIdx]<=simThresh && !relationMap.get(drug).contains(diseaseList.get(i))){
                        // !relationMap.get(drug).contains(disease) is very important, it removes the positive records.
                        negativeRelationMap.get(drug).add(diseaseList.get(i));
                        count++;
                        if(count == num)
                            break;
                    }
                }
            }
        }
        System.out.println("Count:\t"+count);
        DataWriter writer = new DataWriter();
        writer.writeHashMap2(negativeRelationMap, outputFile);
    }
    
    
    /**
     * This method generates a gsn data set.
     * This method uses the mechanisms similar to generateNegative2 (to collect drug- disease2 as a negative
     * if drug -disease1 is associated and s(disease1, disease2)=0.
     * However, we give the size of the negative set as a parameter.
     * @param diseaseFile
     * @param relationFile
     * @param matrixFile
     * @param outputFile
     * @param simThresh
     * @param num 
     */
    public void generateNegative3(String diseaseFile, 
            String relationFile, 
            String matrixFile, 
            String outputFile, float simThresh,
            int num){
        DataReader reader = new DataReader();
        ArrayList<String> diseaseList = reader.readIds2(diseaseFile);
        HashMap<String, HashSet<String>> relationMap = reader.readMap(relationFile);
        HashMap<String, HashSet<String>> negativeRelationMap = new HashMap<>();
        float simMatrix[][] = reader.readMatrix(matrixFile, diseaseList.size(), diseaseList.size());
        Set<Map.Entry<String,HashSet<String>> >relationSet = relationMap.entrySet();
        Iterator<Map.Entry<String,HashSet<String>>> iterator = relationSet.iterator();
        System.out.println("Start negative set generation.");
        while(iterator.hasNext()){
            Map.Entry<String,HashSet<String>> entry = iterator.next();
            String drug = entry.getKey();
            HashSet<String> diseaseSet = entry.getValue();
            
            if(!negativeRelationMap.containsKey(drug))
                negativeRelationMap.put(drug,new HashSet<>());
            // Iterate over the disease set and find the unsimilar diseases. 
            Iterator<String> diseaseIter = diseaseSet.iterator();
            while(diseaseIter.hasNext()){
                String disease = diseaseIter.next();
                // Get the index of the current disease.
                int diseaseIdx = diseaseList.indexOf(disease);
                if(diseaseIdx == -1)
                    throw new IllegalStateException("(negativeGenerator.generateNegative2) Disease index -1:  "+diseaseIdx);
                for(int i=0;i<diseaseList.size();i++){
                    if(simMatrix[i][diseaseIdx]<=simThresh && !relationMap.get(drug).contains(diseaseList.get(i))) // !relationMap.get(drug).contains(disease) is very important, it removes the positive records.
                        negativeRelationMap.get(drug).add(diseaseList.get(i));
                }
            }
        }
        
        HashMap<String, HashSet<String>> ans = new HashMap<>();
        ArrayList<String> keySet = new ArrayList<>(negativeRelationMap.keySet());

        System.out.println("Start random selecting.");
        for(int i=0;i<num;i++){
            int randomIdx = (int)(Math.random()*keySet.size());
            String key = keySet.remove(randomIdx);
            ans.put(key, negativeRelationMap.get(key));
        }
            
        DataWriter writer = new DataWriter();
        writer.writeHashMap2(ans, outputFile);
    }
    
    /**
     * This method generates gsn based on the same mechanism of generateNegative2.
     * Only this method uses the already generated gsn and random select given "num" negative records.
     * @param gsn
     * @param outputFile
     * @param num 
     */
    public void generateNegative3(String gsn,
            String outputFile, int num){
        DataReader reader = new DataReader();
        HashMap<String, HashSet<String>> negativeMap = reader.readMap(gsn);
        HashMap<String, HashSet<String>> ans = new HashMap<>();
        ArrayList<String> keySet = new ArrayList<>(negativeMap.keySet());
        
        System.out.println("Start random selecting.");
        int i =0;
        while(i <num){
            String key = keySet.get((int)(Math.random()*keySet.size()));
            ArrayList<String> valueList = new ArrayList<>(negativeMap.get(key));
            String value = valueList.get((int)(Math.random()*valueList.size()));
            if(!ans.containsKey(key)){
                ans.put(key, new HashSet<>());
                ans.get(key).add(value);
                i++;
            }
            else if(ans.get(key).contains(value))
                continue;
            else{
                ans.get(key).add(value);
                i++;
            }
        }
            
        DataWriter writer = new DataWriter();
        writer.writeHashMap2(ans, outputFile);
    }
   
    
    
    public void runGenerateNegative(){
        String diseaseFile = "../../id/disease_id.txt";
        String relationFile = "../../assoc/drug_disease_assoc.txt";
        String matrixFile = "../../matrix/disease_matrix.txt";
        String outputFile = "../../gsn/negative_comp.txt";
        float simThresh = 0.00f;
        int num = 60000;
        generateNegative3(diseaseFile, relationFile, matrixFile, outputFile, simThresh,num);
    }
    
    
    public void runGsn(){
        DrugReposConfig conf = new DrugReposConfig();
        new InitDrugReposConfig().initDrugReposConfig(conf);
        conf.gsn = "../../gsn/negative_comp.txt";
        generateNegativeSpecial(conf,0.0f,60000);
    }
    
   public void runGsnCompare2(){
       DrugReposConfig conf = new DrugReposConfig();
       new InitDrugReposConfig().initCompare2(conf);
       conf.gsn = "../../gsn/compare2_gsn.txt";
       generateNegativeCompare2(conf, 3868);
   }
   
   public void runGsnCompare3(){
       DrugReposConfig conf = new DrugReposConfig();
       new InitDrugReposConfig().initCompare3(conf);
       conf.gsn = "../../compare3/gsn/compare3_gsn.txt";
       generateNegative(conf,0.1f,3868);
   }
    
    
    public static void main(String args[]){
        DataGenerator gen = new DataGenerator();
        gen.runGsnCompare3();
        //gen.runGenerateNegative();
        //gen.runGenerateNegative();
        //gen.runGenerateNegative3_1();
        //gen.check();
    }
}