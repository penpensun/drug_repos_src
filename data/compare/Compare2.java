/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.compare;
import java.io.*;
import data.io.*;
import java.util.*;
import data.processor.DataCounter;
/**
 *
 * @author penpen926
 */
public class Compare2 {
    /**
     * This method computes the overlap.
     * @param compare2Disease
     * @param cui 
     * @param output 
     */
    public void checkDiseaseNonoverlap(String compare2Disease, 
            String cui,
            String output){
        FileReader fr = null;
        BufferedReader br = null;
        FileWriter fw = null;
        BufferedWriter bw = null;
        ArrayList<String> cuiList = new DataReader().readIds(cui);
        HashSet<String> containedDiseaseName = new HashSet<>();
        HashSet<String> uncontainedDiseaseName = new HashSet<>();
        try{
            fr = new FileReader(compare2Disease);
            br = new BufferedReader(fr);
            fw = new FileWriter(output);
            bw = new BufferedWriter(fw);
            String line = null;
            while((line = br.readLine())!= null){
                String[] splits = line.split("\t");
                String cuiId =splits[2];
                if(cuiList.contains(cuiId))
                    containedDiseaseName.add(splits[1]);
            }
            
            fr.close();
            br.close();
            fr = new FileReader(compare2Disease);
            br = new BufferedReader(fr);
            while((line = br.readLine())!= null){
                String[] splits = line.split("\t");
                String cuiId = splits[2];
                if(!cuiList.contains(cuiId) && !containedDiseaseName.contains(splits[1])){
                    uncontainedDiseaseName.add(splits[1]);
                }
            }
            new DataWriter().writeHashSet(uncontainedDiseaseName, output, "\n");
            fr.close();
            br.close();
        }catch(IOException e){
            System.err.println("Error");
        }
    }
    
    
    public void drugNonoverlap(String compare2Gsp, String drugDrugNames,
            String output){
        HashMap<String, String> drugNameDrugMap = new DataReader().readMap2(drugDrugNames);
        FileReader fr = null;
        BufferedReader br = null;
        try{
            fr = new FileReader(compare2Gsp);
            br = new BufferedReader(fr);
            String line =null;
            HashSet<String> uncontained = new HashSet<>();
            while((line = br.readLine())!=  null){
                String[] splits = line.split("\t");
                String drug = splits[0];
                if(!drugNameDrugMap.containsValue(drug))
                    uncontained.add(drug);
            }
            
            new DataWriter().writeHashSet(uncontained, output, "\n");
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    
    
    private HashMap<String, HashSet<String>> readCompare2DiseaseCuiMap(String compare2Disease){
        FileReader fr = null;
        BufferedReader br = null;
        HashMap<String, HashSet<String>> ans = new HashMap<>();
        try{
            fr = new FileReader(compare2Disease);
            br = new BufferedReader(fr);
            String line =null;
            while((line = br.readLine())!= null){
                String[] splits = line.split("\t");
                String diseaseName = String.copyValueOf(splits[1].toCharArray());
                String cui = String.copyValueOf(splits[2].toCharArray());
                if(!ans.containsKey(diseaseName))
                    ans.put(diseaseName, new HashSet<>());
                ans.get(diseaseName).add(cui);
            }
            return ans;
        }catch(IOException e){
            e.printStackTrace();
        }
        return null;
    }
    
    
    private HashSet<String> mapDiseaseName(String diseaseName,
            HashMap<String, HashSet<String>> comp2DiseaseCuiMap,
            HashMap<String, HashSet<String>> cuiDiseaseMap){
        HashSet<String> ans = new HashSet<>();
        HashSet<String> compCui = comp2DiseaseCuiMap.get(diseaseName);
        if(compCui == null){
            System.out.println(diseaseName);
            return null;
        }
        for(String cui: compCui){
            HashSet<String> disease = cuiDiseaseMap.get(cui);
            if(disease == null)
                continue;
            ans.addAll(disease);
        }
        return ans;
    }
    
    
    
    
    public void checkGoldStandardOverlap(String drugDrugNameAssoc, String drugDiseaseAssoc, 
            String compareGsp, String compare2Disease,String diseaseCuiAssoc, String output){
        DataReader reader = new DataReader();
        HashMap<String, String> drugDrugNameMap = reader.readMapInReverseOrder2(drugDrugNameAssoc);
        HashMap<String, HashSet<String>> cuiDiseaseMap = reader.readMapInReverseOrder4(diseaseCuiAssoc);
        HashMap<String, HashSet<String>> compare2DiseaseCuiMap = readCompare2DiseaseCuiMap(compare2Disease);
        FileReader fr = null;
        BufferedReader br = null;
        
        try{
            HashMap<String, HashSet<String>> ans = new HashMap<>();
            fr = new FileReader(compareGsp);
            br = new BufferedReader(fr);
            
            String line=  null;
            while((line =br.readLine())!= null){
                String splits[] = line.split("\t");
                String drugName = splits[0];
                String diseaseName = splits[1];
                // map the disease in comapre 2 to our data set
                HashSet<String> mappedDiseases = mapDiseaseName(diseaseName, compare2DiseaseCuiMap,
                        cuiDiseaseMap);
                String drugId = drugDrugNameMap.get(drugName);
                if(mappedDiseases == null || mappedDiseases.isEmpty() || drugId == null){
                    System.out.println(drugName+"\t"+diseaseName);
                    continue;
                }
                if(!ans.containsKey(drugId))
                    ans.put(drugId, new HashSet<>());
                ans.get(drugId).addAll(mappedDiseases);
            }
            new DataWriter().writeHashMap2(ans, output);
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    
    
    public void runDiseaseNonoverlap(){
        String cuiId = "../../id/cui.txt";
        String compareDisease = "../../compare2/disease.txt";
        String nonOverlap = "../../compare2/disease_nonoverlap.txt";
        checkDiseaseNonoverlap(compareDisease, cuiId, nonOverlap);
    }
    
    public void runCheckGspOverlap(){
        String drugDrugNameAssoc = "../../assoc/drug_drug_names_assoc.txt";
        String drugDiseaseAssoc = "../../assoc/drug_disease_assoc.txt";
        String compare2Gsp = "../../compare2/compare2_gsp.txt";
        String compare2Disease = "../../compare2/disease.txt";
        String output = "../../compare2/gsp_overlap.txt";
        String diseaseCuiAssoc = "../../assoc/disease_cui_assoc.txt";
        checkGoldStandardOverlap(drugDrugNameAssoc, drugDiseaseAssoc, compare2Gsp, compare2Disease, diseaseCuiAssoc, output);
        System.out.println(new DataCounter().mapCounter(output));
    }
    
    public void runDrugNonoverlap(){
        String compare2Gsp = "../../compare2/compare2_gsp.txt";;
        String drugDrugNameAssoc = "../../assoc/drug_drug_names_assoc.txt";
        String output = "../../compare2/drug_nonoverlap.txt";
        drugNonoverlap(compare2Gsp, drugDrugNameAssoc, output);
    }
    
    
    public static void main(String args[]){
        //new Compare2().runDiseaseNonoverlap();
        //new Compare2().runCheckGspOverlap();
        new Compare2().runDrugNonoverlap();
    }
}
