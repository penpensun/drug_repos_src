/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package data.extractor;

import data.io.DataReader;
import data.io.DataWriter;
import java.util.*;
import java.io.*;
/**
 * This class contains association (e.g. drug-disease association extractor)
 * @author Peng
 */
public class AssociationExtractor {
    
    

    public void drugDiseaseAssociationExtractor(String drugGeneNameRelationFile, String geneNameDiseaseRelationFile, String outputFile){
        DataReader reader = new DataReader();
        HashMap<String, HashSet<String>> drugGeneNameMap = reader.readMap(drugGeneNameRelationFile);
        HashMap<String, HashSet<String>> geneNameDiseaseMap = reader.readMap(geneNameDiseaseRelationFile);
        HashMap<String, HashSet<String>> drugDiseaseMap = new HashMap();
        Set<Map.Entry<String, HashSet<String>>> mapEntrySet = drugGeneNameMap.entrySet();
        Iterator< Map.Entry<String, HashSet<String>>> mapIter = mapEntrySet.iterator();
        while(mapIter.hasNext()){
            Map.Entry<String, HashSet<String>> entry = mapIter.next();
            String drug = entry.getKey();
            HashSet<String> geneNameSet = entry.getValue();
            
            /* For each gene in gene name set. */
            Iterator<String> geneNameIter = geneNameSet.iterator();
            while(geneNameIter.hasNext()){
                String geneName = geneNameIter.next();
                HashSet<String> diseaseSet = geneNameDiseaseMap.get(geneName);
                if(diseaseSet == null)
                    continue;
                else{
                    /* Push all drug-disease assocation into hashmap drugDiseaseMap. */
                    drugDiseaseMap.put(drug, diseaseSet);
                }
            }    
        }
        // Output the result.
        new DataWriter().writeHashMap2(drugDiseaseMap, outputFile);
    }
    
    
   
    /**
     * This method refines the drug-disease association.
     * @param drugDiseaseAssoc
     * @param diseaseIdFile
     * @param output 
     */
    public void refineDrugDiseaseAssoc(String drugDiseaseAssoc, String diseaseIdFile, String output){
        DataReader reader = new DataReader();
        HashMap<String, HashSet<String>> drugDiseaseMap = reader.readMap(drugDiseaseAssoc);
        HashMap<String, HashSet<String>> ans = new HashMap<>();
        ArrayList<String> diseaseIds = reader.readIds2(diseaseIdFile);
        
        ArrayList<String> drugSet = new ArrayList<>(drugDiseaseMap.keySet());
        for(String drug: drugSet){
            ans.put(drug, new HashSet<>());
            HashSet<String> diseaseSet = drugDiseaseMap.get(drug);
            for(String d:diseaseSet)
                if(diseaseIds.contains(d))
                    ans.get(drug).add(d);
        }
        
        new DataWriter().writeHashMap2(ans, output);
    }
    
    
    private void extractDiseaseCui(String diseaseMeshMap, String meshCuiMap, String output){
        HashMap<String, HashSet<String>> dMap = new DataReader().readMap(diseaseMeshMap);
        HashMap<String, HashSet<String>> cMap = null;
        HashMap<String, HashSet<String>> cuiMap = new HashMap<>();
        try{
            cMap = readMeshCuiMap(meshCuiMap);
        }catch(IOException e){
            e.printStackTrace();
        }
        for(String d: dMap.keySet()){
            if(!cuiMap.containsKey(d))
                cuiMap.put(d, new HashSet<>());
            HashSet<String> meshSet = dMap.get(d);
            if(meshSet == null)
                continue;
            for(String mesh: meshSet){
                HashSet<String> cuis = cMap.get(mesh);
                if(cuis == null || cuis.isEmpty())
                    continue;
                cuiMap.get(d).addAll(cuis);
            }
        }
        new DataWriter().writeHashMap2(cuiMap, output);
    }
    
    
    /**
     * This method extracts the genenames connected with diseases.
     * @param geneFile
     * @param ctdGeneDiseaseFile
     * @param geneNameOutput
     * @param diseaseNameOutput
     * @param assocOutput 
     */
    private void extractGeneDiseaseAssoc(String geneFile, 
            String diseaseFile,
            String ctdGeneDiseaseFile, 
            String assocOutput){
        DataReader reader = new DataReader();
        ArrayList<String> diseaseList = reader.readIds2(diseaseFile);
        ArrayList<String> geneList = reader.readIds(geneFile);
        HashMap<String, HashSet<String>> map = new HashMap<>();
        try{
        FileReader fr = new FileReader(ctdGeneDiseaseFile);
        BufferedReader br = new BufferedReader(fr);
        String line = null;
        int i =0;
        int size = new DataReader().getLineNum(ctdGeneDiseaseFile);
        while((line= br.readLine())!= null){
            i++;
            if(i%50000 == 0)
                System.out.println(i/(float)size*100+"% is finished.");
            line= line.trim();
            if(line.isEmpty() || line.startsWith("#"))
                continue;
            String[] splits = line.split("\t");
            String geneName = String.copyValueOf(splits[0].toCharArray()).trim();
            String diseaseName = String.copyValueOf(splits[2].toCharArray()).trim();
            if(diseaseName.isEmpty() || geneName.isEmpty()) /* If disease name is empty, then continue. */
                continue;
            if(!geneList.contains(geneName))
                continue;
            if(!diseaseList.contains(diseaseName))
                continue;
            
            /* Insert into the hashmap. */
            if(!map.containsKey(geneName))
                map.put(geneName, new HashSet<>());
            map.get(geneName).add(diseaseName);
            
        }
        br.close();
        fr.close();
        }catch(IOException e){
            e.printStackTrace();
        }
        /* Output the gene name set. */
        new DataWriter().writeHashMap2(map, assocOutput);
    }
    
    private HashMap<String, HashSet<String>> readMeshCuiMap(String meshCuiMap) throws IOException{
        FileReader fr = new FileReader(meshCuiMap);
        BufferedReader br = new BufferedReader(fr);
        String line= null;
        HashMap<String, HashSet<String>> ans = new HashMap<>();
        while((line = br.readLine())!= null){
            String[] splits = line.split("\\|");
            String mesh = splits[0];
            String cui = splits[1];
            if(!ans.containsKey(mesh))
                ans.put(mesh, new HashSet<>());
            ans.get(mesh).add(cui);
        }
        
        return ans;
    }
            
            
            
            
    public void runDrugDiseaseAssocationExtractor(){
        String drugGeneNameRelationFile = "../../drug/drug_genename_relation_5.txt";
        String geneNameDiseaseRelationFile = "../../drug/genename_disease_relation_5.txt";
        String outputFile = "../../drug/drug_disease_extracted_relation_5.txt";
        drugDiseaseAssociationExtractor(drugGeneNameRelationFile, geneNameDiseaseRelationFile, outputFile);
    }
    
    public void runRefineDrugDiseaseAssoc(){
        String drugDiseaseAssoc = "../../assoc/drug_disease_assoc.txt";
        String diseaseList = "../../disease/disease_id.txt";
        String output = "../../assoc/drug_disease_refined.txt";
        refineDrugDiseaseAssoc(drugDiseaseAssoc, diseaseList, output);
    }
    
    
    private void runExtractDiseaseCui(){
        String diseaseMeshMap = "../../mesh/disease_mesh_assoc.txt";
        String meshCuiMap = "../../mesh/mesh_umls_2015AA_mapped.txt";
        String output = "../../mesh/disease_cui_assoc.txt";
        extractDiseaseCui(diseaseMeshMap, meshCuiMap, output);
    }
    
    
    public void runExtractGeneDiseaseAssoc(){
        String geneNameFile = "../../ligand/geneName_6.txt";
        String diseaseFile = "../../disease/id/disease_5.txt";
        String ctdFile = "../../ctd/CTD_genes_diseases.tsv";
        String output = "../../ligand/geneName_disase_assoc_6.txt";
        extractGeneDiseaseAssoc(geneNameFile,diseaseFile, ctdFile, output);
    }
    
    
    public static void main(String[] args){
        //new AssociationExtractor().runDrugDiseaseAssocationExtractor();
        //new AssociationExtractor().runDrugDiseaseAssocationExtractor();
        //new AssociationExtractor().runRefineDrugDiseaseAssoc();
        new AssociationExtractor().runExtractDiseaseCui();
    }
}
