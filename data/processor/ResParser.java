/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package data.processor;

import biforce.constants.BiForceConstants;
import biforce.graphs.NpartiteGraph;
import data.io.DataWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import data.io.*;
import biforce.graphs.*;
import java.util.ArrayList;
import java.io.*;
import data.extractor.*;
import data.io.DrugReposConfig;
/**
 * This class contains the parsers used to process the results coming from n-force.
 * @author Peng Sun
 */
public class ResParser {
    public HashMap<String, HashSet<String>> parseRes2(nforce.graphs.NpartiteGraph resGraph,
            DrugReposConfig conf){
        
        HashMap<String, HashSet<String>> ans = new HashMap<>();
        DataReader reader = new DataReader();
        HashMap<String, HashSet<String>> drugMapping = 
                reader.readMapInReverseOrder(conf.drug_vertex_precluster_mapping);
        HashMap<String, HashSet<String>> diseaseMapping = 
                reader.readMapInReverseOrder(conf.disease_vertex_precluster_mapping);
        
        HashMap<String, HashSet<String>> drugDiseaseMap = 
                reader.readMap(conf.drug_disease_assoc);
        ArrayList<String> diseaseToRemove = null;
        if(conf.remove_dissimilar_disease)
            diseaseToRemove = reader.readIds2(conf.disease_to_remove_file);
        // Remove the cross validation associations from the drugDiseaseMap.
        HashMap<String, HashSet<String>> cvMap = reader.readMap(conf.gsp);
        ArrayList<String> cvDrugList = new ArrayList<>(cvMap.keySet());
        for(String cvDrug: cvDrugList){
            HashSet<String> diseaseSet = drugDiseaseMap.get(cvDrug);
            if(diseaseSet == null)
                throw new IllegalStateException("(ResParser.parseRes2) Impossible null disease set:  "+cvDrug);
            HashSet<String> cvDiseaseSet = cvMap.get(cvDrug);
            for(String cvDisease: cvDiseaseSet)
                diseaseSet.remove(cvDisease);
            drugDiseaseMap.put(cvDrug, diseaseSet);
        }
        
        
        for(nforce.graphs.Vertex drugPreClusterVtx: resGraph.getVertices()){
            if(drugPreClusterVtx.getVtxSet() != 0)
                continue;
            for(nforce.graphs.Vertex diseasePreClusterVtx: resGraph.getVertices()){
                if(diseasePreClusterVtx.getVtxSet()!= 2)
                    continue;
                // Check if there is an inserted edge between the two clusters.
                float ew= resGraph.edgeWeight(drugPreClusterVtx, diseasePreClusterVtx);
                
                if(ew>0){
                    // If the edge is inserted.
                    ArrayList<String> drugsInPreCluster = 
                            new ArrayList<>(drugMapping.get(drugPreClusterVtx.getValue()));
                    if(diseaseMapping.get(diseasePreClusterVtx.getValue()) == null){
                        System.err.println(diseasePreClusterVtx.getValue());
                        System.err.println(conf.disease_vertex_precluster_mapping);
                        System.exit(0);
                    }
                    ArrayList<String> diseasesInPreCluster = 
                            new ArrayList<>(diseaseMapping.get(diseasePreClusterVtx.getValue()));
                    for(String drug: drugsInPreCluster)
                        for(String disease:diseasesInPreCluster){
                            if(conf.remove_dissimilar_disease && diseaseToRemove.contains(disease)
                                    && 
                                    (drugDiseaseMap.get(drug) == null||!drugDiseaseMap.get(drug).contains(disease)) )
                                continue;
                            if(!ans.containsKey(drug))
                                ans.put(drug, new HashSet<>());
                            ans.get(drug).add(disease);
                        }
                }
               else if (ew<0 && !Float.isFinite(ew)){
                    // If the edge is not inserted.
                    ArrayList<String> drugsInPreCluster = 
                            new ArrayList<>(drugMapping.get(drugPreClusterVtx.getValue()));
                    ArrayList<String> diseasesInPreCluster = 
                            new ArrayList<>(diseaseMapping.get(diseasePreClusterVtx.getValue()));
                    for(String drug: drugsInPreCluster){
                        HashSet<String> associatedDiseases = drugDiseaseMap.get(drug);
                        if(associatedDiseases == null)
                            continue;
                        for(String disease: diseasesInPreCluster){
                            // If not in the associated diseases, then continue.
                            if(!associatedDiseases.contains(disease))
                                continue;
                            // Else we add it in.
                            if(!ans.containsKey(drug))
                                ans.put(drug,new HashSet<>());
                            ans.get(drug).add(disease);
                        }
                    }
                      
                }
                else // If there is no edge between them.
                    continue;
            }
        }
        return ans;
    }
    
    
    public HashMap<String, HashSet<String>> parseRepos(nforce.graphs.NpartiteGraph resGraph,
            DrugReposConfig conf){
        HashMap<String, HashSet<String>> knownAssoc = new DataReader().readMap(conf.drug_disease_assoc);
        HashMap<String, HashSet<String>> ans = new HashMap<>();
        DataReader reader = new DataReader();
        HashMap<String, HashSet<String>> drugMapping = 
                reader.readMapInReverseOrder(conf.drug_vertex_precluster_mapping);
        HashMap<String, HashSet<String>> diseaseMapping = 
                reader.readMapInReverseOrder(conf.disease_vertex_precluster_mapping);
        
        HashMap<String, HashSet<String>> drugDiseaseMap = 
                reader.readMap(conf.drug_disease_assoc);
        
        for(nforce.graphs.Vertex drugPreClusterVtx: resGraph.getVertices()){
            if(drugPreClusterVtx.getVtxSet() != 0)
                continue;
            for(nforce.graphs.Vertex diseasePreClusterVtx: resGraph.getVertices()){
                if(diseasePreClusterVtx.getVtxSet()!= 2)
                    continue;
                // Check if there is an inserted edge between the two clusters.
                float ew= resGraph.edgeWeight(drugPreClusterVtx, diseasePreClusterVtx);
                
                if(ew>0){
                    // If the edge is inserted.
                    ArrayList<String> drugsInPreCluster = 
                            new ArrayList<>(drugMapping.get(drugPreClusterVtx.getValue()));
                    if(diseaseMapping.get(diseasePreClusterVtx.getValue()) == null){
                        System.err.println(diseasePreClusterVtx.getValue());
                        System.err.println(conf.disease_vertex_precluster_mapping);
                    }
                    ArrayList<String> diseasesInPreCluster = 
                            new ArrayList<>(diseaseMapping.get(diseasePreClusterVtx.getValue()));
                    for(String drug: drugsInPreCluster)
                        for(String disease:diseasesInPreCluster){
                            HashSet<String> knownSet = knownAssoc.get(drug);
                            if(knownSet!= null && knownSet.contains(disease))
                                continue;
                            if(!ans.containsKey(drug))
                                ans.put(drug, new HashSet<>());
                            ans.get(drug).add(disease);
                        }
                }
               else {}
                    /*
                    if (ew<0 && !Float.isFinite(ew)){
                    // If the edge is not inserted.
                    ArrayList<String> drugsInPreCluster = 
                            new ArrayList<>(drugMapping.get(drugPreClusterVtx.getValue()));
                    ArrayList<String> diseasesInPreCluster = 
                            new ArrayList<>(diseaseMapping.get(diseasePreClusterVtx.getValue()));
                    for(String drug: drugsInPreCluster){
                        HashSet<String> associatedDiseases = drugDiseaseMap.get(drug);
                        if(associatedDiseases == null)
                            continue;
                        for(String disease: diseasesInPreCluster){
                            // If not in the associated diseases, then continue.
                            if(!associatedDiseases.contains(disease))
                                continue;
                            // Else we add it in.
                            if(!ans.containsKey(drug))
                                ans.put(drug,new HashSet<>());
                            ans.get(drug).add(disease);
                        }
                    }    
                }
                
                else // If there is no edge between them.
                    continue;
                    */
            }
        }
        return ans;
    }
    
    /**
     * This method compares our parsed file with the downloaded nbt file. 
     * @param d1
     * @param d2
     * @return 
     */
    public float compare(String d1, String d2){
        d1 = d1.toLowerCase();
        d2 = d2.toLowerCase();
        d1 = d1.replace(",","");
        d2 = d2.replace(",","");
        String[] d1Splits = d1.split("//s+");
        String[] d2Splits = d2.split("//s+");
        int numHit = 0;
        for(String dsp1: d1Splits){
            for(String dsp2: d2Splits){
                if(dsp1.equals(dsp2))
                    numHit++;
            }
        }
        return (float)numHit/Math.min(d1Splits.length, d2Splits.length);
    }
    
    public String containsDrug(HashMap<String, HashSet<String>> nbtMap, String drug, HashMap<String, HashSet<String>> drugSynMap){
        ArrayList<String> nbtKeys = new ArrayList<>(nbtMap.keySet());
        ArrayList<String> drugSyns = null;
        if(drugSynMap.get(drug) != null)
            drugSyns = new ArrayList<>(drugSynMap.get(drug));
        else drugSyns = new ArrayList<>();
        drugSyns.add(drug);
        for(String d: drugSyns){
            for(String nbtKey: nbtKeys){
                if(d.equalsIgnoreCase(nbtKey))
                    return nbtKey;
            }
        }
        return null;
    }
    
    
    public boolean drugEquals(String d1, String d2, HashMap<String, HashSet<String>> drugSynMap){
        HashSet<String> d1Set = drugSynMap.get(d1);
        HashSet<String> d2Set = drugSynMap.get(d2);
        if(d1Set == null){
            d1Set = new HashSet<>();
            d1Set.add(d1);
        }
        else
            d1Set.add(d1);
        
        if(d2Set == null)
            d2Set = new HashSet<>();
        d2Set.add(d2);
        for(String d_1: d1Set){
            for(String d_2:d2Set)
                if(d_1.equalsIgnoreCase(d_2))
                    return true;
        }
        return false;
    }
    
    public String containsDisease(HashSet<String> nbtDiseases, String disease, HashMap<String, HashSet<String>> diseaseSynMap){
        if(diseaseSynMap.get(disease) == null)
            return null;
        ArrayList<String> diseaseSyns = new ArrayList<>(diseaseSynMap.get(disease));
        diseaseSyns.add(disease);
        for(String d: diseaseSyns)
            for(String nbtDisease: nbtDiseases){
                float score = compare(d,nbtDisease);
                if(score>0.5)
                    return new StringBuilder(d).append("\t").append(score).toString();
            }
        return null;
    }
    /**
     * This method tries to find matches between our results and the nbt drug-disease results.
     * 
     * @param nbtDrugDiseaseMap
     * @param parsedDrugDiseaseMap
     * @param drugSynMap
     * @param diseaseSynMap
     * @param output 
     */
    public void parseResWithNbt(HashMap<String, HashSet<String>> nbtDrugDiseaseMap, HashMap<String, HashSet<String>> parsedDrugDiseaseMap,
            HashMap<String, HashSet<String>> drugSynMap, HashMap<String, HashSet<String>> diseaseSynMap, String output){
        ArrayList<String> parsedDrugSet = new ArrayList<>(parsedDrugDiseaseMap.keySet());
        FileWriter fw = null;
        BufferedWriter bw = null;
        try{
            fw = new FileWriter(output);
            bw = new BufferedWriter(fw);
        }catch(IOException e){
            System.err.println("(ResParser.parseResWithNbt) File writer init error.");
            e.printStackTrace();
            return;
        }
        int countCommonDrugs = 0;
        int countRelevantItemsNforce = 0;
        int countRelevantItemsNbt = 0;
        int countCommonItems = 0;
        for(String parsedDrug: parsedDrugSet){
            HashSet<String> parsedDiseaseSet = parsedDrugDiseaseMap.get(parsedDrug);
            String matchedNbtDrug = containsDrug(nbtDrugDiseaseMap, parsedDrug, drugSynMap);
            if(matchedNbtDrug == null)
                continue;
            countCommonDrugs++;
            countRelevantItemsNforce+=parsedDiseaseSet.size();
            
            HashSet<String> nbtDiseaseSet = nbtDrugDiseaseMap.get(matchedNbtDrug);
            countRelevantItemsNbt+=nbtDiseaseSet.size();
            for(String parsedDisease:parsedDiseaseSet){
                String matchedString = containsDisease(nbtDiseaseSet, parsedDisease, diseaseSynMap);
                if(matchedString == null)
                    continue;
                countCommonItems++;
                try{
                    bw.write(parsedDrug+"\t"+parsedDisease+"\t"+matchedString+"\n");
                }catch(IOException e){
                    System.err.println("(ResParse.parsedResWithNbt) File writing error.");
                    e.printStackTrace();
                    return;
                }
            }
        }
        try{
            bw.flush();
            fw.close();
            bw.close();
        }catch(IOException e){
            System.err.println("(ResParse.parsedResWithNbt) File writing closing error.");
            e.printStackTrace();
            return;
        }
        
        System.out.println("Number of common drugs:  "+countCommonDrugs);
        System.out.println("Number of relevant items from our parsed result:  "+countRelevantItemsNforce);
        System.out.println("Number of relevant items from nbt:  "+countRelevantItemsNbt);
        System.out.println("Number of common items:  "+countCommonItems);
    }

    
    /**
     * This method extracts the drug-disease pair from the parsed result.
     * @param parsedRes
     * @return 
     */
    public ArrayList<String> extractResultPair(String parsedRes){
        DataReader reader = new DataReader();
        HashMap<String,HashSet<String>> res = reader.readMap(parsedRes);
        ArrayList<String> ans = new ArrayList<>();
        ArrayList<String> drugSet = new ArrayList<>(res.keySet());
        for(String drug: drugSet){
            HashSet<String> assocDiseaseSet= res.get(drug);
            for(String disease: assocDiseaseSet){
                ans.add(new StringBuilder(drug).append("\t").append(disease).toString());
            }
        }
        return ans;
    }
    
    public void filter(HashMap<String, HashSet<String>> res, HashMap<String, HashSet<String>> known){
        ArrayList<String> drugs = new ArrayList<>(res.keySet());
        for(String d: drugs){
            HashSet<String> diseases = res.get(d);
            HashSet<String> knownDiseases = known.get(d);
            HashSet<String> ans = new HashSet<>();
            for(String i: diseases)
                if(knownDiseases == null || !knownDiseases.contains(i))
                    ans.add(i);
            res.put(d, ans);
        }
    }
    
    
    /**
     * This method extracts the novel drug-disease pair from the parsed result with the known results detracted.
     * @param parsedRes
     * @param knownMap
     * @return 
     */
    public ArrayList<String> extractNovelResultPair(String parsedRes, String knownMap){
        DataReader reader = new DataReader();
        HashMap<String, HashSet<String>> res = reader.readMap(parsedRes);
        HashMap<String, HashSet<String>> known = reader.readMap(knownMap);
        ArrayList<String> novel = new ArrayList<>();
        
        ArrayList<String> drugSet = new ArrayList<>(res.keySet());
        for(String drug:drugSet){
            HashSet<String> assocDiseaseSet = res.get(drug);
            HashSet<String> knownDiseaseSet = known.get(drug);
            for(String disease: assocDiseaseSet){
                if(knownDiseaseSet == null || !knownDiseaseSet.contains(disease))
                    novel.add(new StringBuilder(drug).append("\t").append(disease).toString());
            }
        }
        return novel;
    }

}
