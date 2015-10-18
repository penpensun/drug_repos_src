/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.processor;
import data.io.DataReader;
import java.io.*;
import java.util.*;
import data.io.*;
/**
 *
 * @author penpen926
 */
public class DataProcessor {
    /**
     * This method computes the similairties between pathway pairs using jaccaard coefficient. 
     * @param pathwayInfoFile
     * @param pathwayIdFile
     * @param matrixOutFile 
     */
    public void computeSimPathways(String pathwayInfoFile, String pathwayIdFile, String matrixOutFile){
        /* First get the pathwayIds. */
        ArrayList<String> pathwayIds = new DataReader().readPathwayIds(pathwayIdFile);
        
        float matrix[][] = new float[pathwayIds.size()][pathwayIds.size()];
        for(int i=0;i<matrix.length;i++)
            for(int j=0;j<matrix.length;j++)
                matrix[i][j] = 0;
        /* Second read the enzymes into a hashmap. */
        HashMap<String,ArrayList<String>> pathwayEnzymeMap = new HashMap<>();
        FileReader fr = null;
        BufferedReader br = null;
        FileWriter fw = null;
        BufferedWriter bw = null;
        float probe = 0;
        try{
            fr = new FileReader(pathwayInfoFile);
            br = new BufferedReader(fr);
        String line = null;
        /* Output the matrix.*/
        fw =new FileWriter(matrixOutFile);
        bw = new BufferedWriter(fw);
        
       
        while((line = br.readLine())!= null){
            if(line.isEmpty())
                continue;
            line = line.trim();
            String[] splits = line.split("\t");
            ArrayList<String> enzymes = new ArrayList<>(
                    Arrays.asList(Arrays.copyOfRange(splits, 1, splits.length)));
            String id = String.copyValueOf(splits[0].toCharArray());
            pathwayEnzymeMap.put(id, enzymes);
        }
        fr.close();
        br.close();
        }catch(IOException e){
            System.err.println("(DataProcessor.computeSimPathways) Reading file error.");
            e.printStackTrace();
            return;
        }
        
        try{
        for(int i=0;i<pathwayIds.size();i++)
            for(int j=i+1;j<pathwayIds.size();j++)
                matrix[i][j]= matrix[j][i]= jaccardIndex(pathwayEnzymeMap.get(pathwayIds.get(i)),
                        pathwayEnzymeMap.get(pathwayIds.get(j)));
        
        for(int i=0;i<matrix.length;i++){
            for(int j=0;j<matrix[0].length-1;j++){
                probe = matrix[i][j];
                bw.write(matrix[i][j]+"\t");
            }
            bw.write(matrix[i][matrix[0].length-1]+"\n");
        }
        bw.flush();
        bw.close();
        fw.close();
        }catch(IOException e){
            System.err.println("(DataProcessor.computeSimPathways) Writing file error.");
            e.printStackTrace();
            System.out.println("Probe:  "+probe);
            return;
        }
    }
    
    
    
    /**
     * This method computes the jaccardIndex. 
     * @param list1
     * @param list2
     * @return 
     */
    public float jaccardIndex(ArrayList<String> list1, ArrayList<String> list2){
        if(list1 == null|| list2 == null)
            return 0;
        /* Copy the first list.*/
        ArrayList<String> copiedList = new ArrayList<>(Arrays.asList(new String[list1.size()]));
        Collections.copy(copiedList, list1);
        copiedList.retainAll(list2);
        float nominator = copiedList.size();
        return nominator/Math.min(list1.size(), list2.size());
    }
    
    
    
    public void summarizeRes(DrugReposConfig conf, HashMap<String, HashSet<String>> resMap,
            boolean append){
        /* Get the drug list. */
        DataReader reader = new DataReader();
        ArrayList<String> diseaseList = reader.readIds2(conf.disease_id);
        HashMap<String, HashSet<String>> negMap = reader.readMap(conf.gsn);
        HashMap<String, HashSet<String>> posMap = reader.readMap(conf.gsp);
        int tp = 0,fp = 0,tn = 0, fn = 0;
        Set<Map.Entry<String,HashSet<String>>> posMapEntrySet = posMap.entrySet();
        Iterator<Map.Entry<String, HashSet<String>>> posMapEntrySetIter = posMapEntrySet.iterator();
        while(posMapEntrySetIter.hasNext()){
            Map.Entry<String, HashSet<String>> posMapEntry = posMapEntrySetIter.next();
            String drug = posMapEntry.getKey();
            HashSet<String> posDiseaseSet = posMapEntry.getValue();
            Iterator<String> posDiseaseIter = posDiseaseSet.iterator();
            HashSet<String> resDiseaseSet = resMap.get(drug);
            while(posDiseaseIter.hasNext()){
                String disease = posDiseaseIter.next();
                if(resDiseaseSet!= null && resDiseaseSet.contains(disease))
                    tp++;
                else
                    fn++;
            }
        }
        
        // Drug list involved with cross-validation removals.
        ArrayList<String> removedDrugList = new ArrayList<>(posMap.keySet());
        for(String drug: removedDrugList)
            for(String disease: diseaseList){
                
                // Check if this drug-disease pair is in the negative map, is in the result.
                boolean isInRes,isInNeg;
                isInRes = resMap.get(drug) != null && resMap.get(drug).contains(disease);
                isInNeg = negMap.get(drug)!= null && negMap.get(drug).contains(disease);
                if(isInRes && isInNeg){
                    fp++;
                } 
                else if(!isInRes && isInNeg)
                    tn++;
            }
                
        /* Output the results.*/
        try{
        FileWriter fw= new FileWriter(conf.roc_output,append);
        BufferedWriter bw = new BufferedWriter(fw);
        //bw.write(thresh+"\t"+tp+"\t"+fp+"\t"+tn+"\t"+fn+"\t"+(float)tp/(fn+tp)+"\t"+(1-(float)tn/(fp+tn))+"\n");
        bw.write((float)fp/(fp+tn)+"\t"+(float)tp/(fn+tp)+"\n");
        bw.flush();
        bw.close();
        fw.close();
        }catch(IOException e){
            System.err.println("(DataProcessor.compareRes) File writer error.");
            return;
        }
    }
    
    /**
     * This method summarizes the clustering results of n-clustering on preclusters.
     * Pre-cond: The clustering results.
     * Post-cond: To write the tp,tn, fp,fn 
     * @param posMap
     * @param negAssocFile
     * @param drugPreClustFile
     * @param diseasePreClustFile
     * @param resAssocFile
     * @param outputFile 
     * @param outputName 
     * @param append 
     */
    public void summarizeResPreCluster(HashMap<String, HashSet<String>> posMap,
            String negAssocFile,
            String drugPreClustFile, String diseasePreClustFile,
            String resAssocFile,
            String outputFile, String outputName, boolean append){
        int tp =0,fp=0,tn=0,fn=0;
        //Read the precluster mapping.
        DataReader reader = new DataReader();
        HashMap<String, String> drugPreClustMap = reader.readMap2(drugPreClustFile);
        HashMap<String, String> diseasePreClustMap = reader.readMap2(diseasePreClustFile);
        //Read the result map.
        HashMap<String, HashSet<String>> resMap = reader.readMap(resAssocFile);
        //Traverse the posMap.
        Iterator<Map.Entry<String, HashSet<String>>> posIter = posMap.entrySet().iterator();
        // Count tp and fn
        while(posIter.hasNext()){
            Map.Entry<String, HashSet<String>> posMapEntry = posIter.next();
            String posDrug =posMapEntry.getKey();
            HashSet<String> posDiseaseSet = posMapEntry.getValue();
            for(String posDisease: posDiseaseSet){
                // Get the corresponding precluster
                String drugPreClust = drugPreClustMap.get(posDrug);
                String diseasePreClust = diseasePreClustMap.get(posDisease);
                // Check if this association is in the map.
                HashSet<String> resMapDiseaseSet = resMap.get(drugPreClust);
                if(resMapDiseaseSet!= null && resMapDiseaseSet.contains(diseasePreClust))
                    tp++;
                else
                    fn++;
            }
        }
        // Count tn and fp
        HashMap<String, HashSet<String>> negMap = reader.readMap(negAssocFile);
        Iterator<Map.Entry<String, HashSet<String>>> negMapIter = negMap.entrySet().iterator();
        while(negMapIter.hasNext()){
            Map.Entry<String, HashSet<String>> negMapEntry = negMapIter.next();
            String negDrug = negMapEntry.getKey();
            HashSet<String> negDiseaseSet = negMapEntry.getValue();
            for(String negDisease: negDiseaseSet){
                String drugPreClust = drugPreClustMap.get(negDrug);
                String diseasePreClust = diseasePreClustMap.get(negDisease);
                HashSet<String> resMapDiseasSet = resMap.get(drugPreClust);
                if(resMapDiseasSet!= null && resMapDiseasSet.contains(diseasePreClust))
                    fp++;
                else
                    tn++;
            }
        }
         /* Output the results.*/
        try{
        FileWriter fw= new FileWriter(outputFile,append);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(outputName+"\t"+tp+"\t"+fp+"\t"+tn+"\t"+fn+"\t"+(float)tp/(fn+tp)+"\t"+(float)tn/(fp+tn)+"\n");
        bw.flush();
        bw.close();
        fw.close();
        }catch(IOException e){
            System.err.println("(DataProcessor.compareRes) File writer error.");
            return;
        }
    }
    
    
    
    /**
     * This method parses the sim0 details, too see which diseases lead to more sim0 associations.
     * @param resFile
     * @param output1 
     * @param output2 
     */
    public void parsedSim0Diseases(String resFile, String output1, String output2){
        FileReader fr = null;
        BufferedReader br= null;
        try{
            fr = new FileReader(resFile);
            br = new BufferedReader(fr);
        }catch(IOException e){
            System.err.println("File reading error.");
            e.printStackTrace();
            return;
        }
        
        HashMap<String, Integer> map1 = new HashMap<>();
        HashMap<String, Integer> map2 = new HashMap<>();
        
        try{
            String line= null;
            while((line = br.readLine())!= null){
                String splits[] = line.split("\t");
                String d1 = String.copyValueOf(splits[1].toCharArray());
                String d2 = String.copyValueOf(splits[2].toCharArray());
                if(!map1.containsKey(d1))
                    map1.put(d1, 0);
                if(!map2.containsKey(d2))
                    map2.put(d2, 0);
                map1.put(d1, map1.get(d1)+1);
                map2.put(d2, map2.get(d2)+1);
            }
        }catch(IOException e){
            System.err.println("Line reading error.");
            e.printStackTrace();
            return;
        }
        
        DataWriter writer = new DataWriter();
        writer.writeHashMap5(map1, output1);
        writer.writeHashMap5(map2, output2);
    }
    
    /**
     * This method checks the similarities between the diseases in tp and in fn. 
     * If there are many very similar diseases, I can add a rule, that every pair of diseases with similarity over
     * a certain value can be seen as repositioned.
     */
    public void checkFalseNegative(){
        HashMap<String, HashSet<String>> cv = new DataReader().readMap("../../nforce_input/repos/cv/cross_assoc_0.1_0.1.txt");
        HashMap<String, HashSet<String>> res = new DataReader().readMap("../../nforce_output/repos/parsed_res_0.1_0.1.txt");
        ArrayList<String> cvDrugs = new ArrayList<>(cv.keySet());
        float matrix[][] = new DataReader().readMatrix("../../disease/disease_matrix.txt", 3407,3407);
        ArrayList<String> diseaseList = new DataReader().readIds2("../../disease/disease_id.txt");
        
        FileWriter fw = null;
        BufferedWriter bw = null;
        try{
            fw  = new FileWriter("../../nforce_output/parse_cv_res.txt");
            bw = new BufferedWriter(fw);
        }catch(IOException e){
            e.printStackTrace();
        }
        for(String cvDr: cvDrugs){
            HashSet<String> cvSet = cv.get(cvDr);
            HashSet<String> resSet = res.get(cvDr);
            if(resSet == null){
                System.out.println(cvDr+"\tno result");
                continue;
            }
            for(String d1:cvSet){
                float aveSim = 0;
                float maxSim = 0;
                float minSim = -1;
                for(String d2:resSet){
                    int idx1 = diseaseList.indexOf(d1);
                    int idx2 = diseaseList.indexOf(d2);
                    float sim = matrix[idx1][idx2];
                    if(Float.isNaN(sim))
                        continue;
                    aveSim+=sim;
                    maxSim = maxSim <sim ? sim:maxSim;
                    minSim = minSim >sim ? sim:minSim;
                }
                try{
                bw.write(cvDr+"\t"+d1+"\t"+aveSim/(cvSet.size()*resSet.size())+"\t"+maxSim+"\t"+minSim+"\n");
                }catch(IOException e){e.printStackTrace();}
            }
            
        }
        try{bw.flush();fw.close();bw.close();}catch(IOException e){e.printStackTrace();}
    }
    
    public static void main(String args[]){
        //new DataProcessor().computeSimPathways("../../pathway/pathway_info.txt", 
        //        "../../pathway/pathway_id_yuan.txt", 
        //        "../../pathway/pathway_matrix_yuan.txt");
        //new DataProcessor().runSummarizeRes();
        //new DataProcessor().parsedSim0Diseases("../../nforce_output/repos/fp_details.txt", "../../nforce_output/repos/fp_counter1.txt", "../../nforce_output/repos/fp_counter2.txt");
        new DataProcessor().checkFalseNegative();
    }
}
