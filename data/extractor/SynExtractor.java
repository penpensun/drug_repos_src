/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.extractor;
import java.util.*;
import data.io.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import org.jdom2.Element;
/**
 * This class contains two extractors to extract the synonyms of drugs and diseases.
 * @author penpen926
 */
public class SynExtractor {
    /**
     * This method extract drug syns and writes them into a given file.
     * @param drugbankXml
     * @param drugXml
     * @param drugIdFile
     * @param output 
     */
    public void extractDrugSynFile(String drugbankXml, String drugIdFile, String output){
        DrugExtractor drugEx = new DrugExtractor();
        List<Element> drugElementList = drugEx.extractDrugList(drugbankXml);
        ArrayList<String> drugList = new DataReader().readIds(drugIdFile);
        HashMap<String, HashSet<String>> synonymMap = new HashMap<>();
        for(Element drugElement: drugElementList){
            String drugId = drugEx.extractPrimaryId(drugElement);
            if(!drugList.contains(drugId))
                continue;
            String drugName = drugElement.getChild("name",drugElement.getNamespace()).getContent(0).getValue();
            Element synonyms = drugElement.getChild("synonyms", drugElement.getNamespace());
            if(synonyms == null)
                continue;
            List<Element> synonymList = synonyms.getChildren("synonym", synonyms.getNamespace());
            if(synonymList == null)
                continue;
            if(!synonymMap.containsKey(drugName))
                synonymMap.put(drugName, new HashSet<>());
            
            for(Element s: synonymList){
                String syn = s.getContent(0).getValue();
                synonymMap.get(drugName).add(syn);
            }
        }
        new DataWriter().writeHashMap2(synonymMap, output);
    }
    
    /**
     * This method extracts the drug synonyms from the given drug synonyms file for the 
     * given drug.
     * @param drug
     * @param drugSynFile
     * @return 
     */
    public ArrayList<String> getDrugSyns(String drug, String drugSynFile){
        DataReader reader = new DataReader();
        HashMap<String, HashSet<String>> drugSynMap = reader.readMap(drugSynFile);
        ArrayList<String> ans = null;
        if(drugSynMap.containsKey(drug))
            ans = new ArrayList<>(drugSynMap.get(drug));
        else{
            ans = new ArrayList<>();
            ans.add(drug);
        }
        return ans;
    }
    
    
    
    /**
     * This method extracts the disease synonyms from the given disease synonyms fie for
     * the given disease.
     * @param disease
     * @param diseaseSynFile
     * @return 
     */
    public ArrayList<String> getDiseaseSyns(String disease, String diseaseSynFile){
        DataReader reader = new DataReader();
        HashMap<String, HashSet<String>> diseaseSynMap = reader.readMap(diseaseSynFile);
        ArrayList<String> ans = null;
        if(diseaseSynMap.containsKey(disease))
            ans = new ArrayList<>(diseaseSynMap.get(disease));
        else{
            ans = new ArrayList<>();
            ans.add(disease);
        }
        return ans;
    }
    
    
    public void runExtractDrugSynFile(){
        String drugbankXml = "../../drug/drugbank.xml";
        String drugIdFile = "../../drug/drug_id.txt";
        String output = "../../drug/drug_syn.txt";
        extractDrugSynFile(drugbankXml, drugIdFile, output);
    }
    
    public static void main(String args[]){
        new SynExtractor().runExtractDrugSynFile();
    }

    /**
     * This method extracts the synonyms from ctd file and returns a HashMap.
     * This method is called by extractDiseaseSynonyms().
     * @param diseaseFile
     * @param ctdDiseaseFile
     * @return
     */
    public HashMap<String, HashSet<String>> extractCtdDiseaseSynonyms(String diseaseFile, String ctdDiseaseFile) {
        ArrayList<String> diseaseList = new DataReader().readIds2(diseaseFile);
        HashMap<String, HashSet<String>> map = new HashMap<>();
        FileReader fr = null;
        BufferedReader br = null;
        try {
            fr = new FileReader(ctdDiseaseFile);
            br = new BufferedReader(fr);
        } catch (IOException e) {
            System.err.println("(DiseaseExtractor.extractDiseaseSynonyms) File reading error.");
            e.printStackTrace();
            return null;
        }
        try {
            String line = null;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                String[] splits = line.split("\t");
                if (splits.length < 8) {
                    System.out.println(line);
                    continue;
                }
                String diseaseName = String.copyValueOf(splits[0].toCharArray());
                if (!diseaseList.contains(diseaseName)) {
                    continue;
                }
                String synonyms = String.copyValueOf(splits[7].toCharArray());
                synonyms = synonyms.trim();
                if (synonyms.isEmpty()) {
                    continue;
                }
                if (!map.containsKey(diseaseName)) {
                    map.put(diseaseName, new HashSet<>());
                }
                String[] synonymsSplit = synonyms.split("\\|");
                for (int i = 0; i < synonymsSplit.length; i++) {
                    map.get(diseaseName).add(String.copyValueOf(synonymsSplit[i].toCharArray()));
                }
            }
            fr.close();
            br.close();
        } catch (IOException e) {
            System.err.println("(DiseaseExtractor.extractDiseaseSynonyms) File reading error.");
            e.printStackTrace();
            return null;
        }
        return map;
    }

    /**
     * This method extracts the synonyms "term list" from the Mesh xml file.
     * This method is called by extractDiseaseSynonyms
     * @param meshIdFile
     * @param meshXml
     * @param diseaseIdFile
     * @return
     */
    public HashMap<String, HashSet<String>> extractMeshDiseaseSynonyms(String meshIdFile, String meshXml, String diseaseIdFile) {
        HashMap<String, HashSet<String>> map = new HashMap<>();
        return map;
    }
}
