/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.processor;
import java.util.*;
import java.io.*;
import data.io.DrugReposConfig;
import data.io.DataReader;
import data.io.DataWriter;
import data.init.InitDrugReposConfig;
/**
 *
 * @author penpen926
 */
public class SemanticValidation {
    /**
     * This method reads the results provided by Rainer.
     * @param fileName
     * @return 
     */
    private HashMap<String, HashSet<String>> readSematicData(String fileName){
        FileReader fr = null;
        BufferedReader br  =null;
        
        HashMap<String, HashSet<String>> ans = new HashMap<>();
        try{
            fr = new FileReader(fileName);
            br = new BufferedReader(fr);
            
            String line = null;
            //Jump the header.
            br.readLine();
            while((line = br.readLine())!= null){
                String[] splits = line.split("\t");
                String drugBankId = String.copyValueOf(splits[0].toCharArray());
                String cui = String.copyValueOf(splits[4].toCharArray());
                if(!ans.containsKey(drugBankId))
                    ans.put(drugBankId, new HashSet<>());
                ans.get(drugBankId).add(cui);
            }
            
            fr.close();
            br.close();
        }catch(IOException e){
            System.err.println("Sematic file io error:  "+fileName);
            e.printStackTrace();
        }
        return ans;
    }
    
    
            
    private void compare(HashMap<String, HashSet<String>> res, 
            int resCount,
            HashMap<String, HashSet<String>> silver,
            HashMap<String, HashSet<String>> gold,
            DrugReposConfig conf,
            boolean append){
        ArrayList<String> keys = new ArrayList<>(res.keySet());
        FileWriter fw = null;
        BufferedWriter bw = null;
        int numSilver = 0;
        int numGold = 0;
        int numBoth = 0;
        try{
            String line = null;
            fw = new FileWriter(conf.semanticOutput);
            bw = new BufferedWriter(fw);
            for(String k: keys){
                HashSet<String> resCui = res.get(k);
                HashSet<String> silverCui = silver.get(k);
                HashSet<String> goldCui = gold.get(k);
                boolean inSilver = false;
                boolean inGold = false;
                for(String singleCui: resCui){
                    inSilver = false;
                    inGold = false;
                    if(silverCui != null &&silverCui.contains(singleCui)){
                        inSilver = true;
                        numSilver++;
                    }
                    if(goldCui!= null && goldCui.contains(singleCui)){
                        inGold = true;
                        numGold++;
                    }
                    if(inSilver && inGold)
                        numBoth++;
                    if(inSilver || inGold){
                        bw.write(k+"\t"+singleCui+"\t"+inSilver+"\t"+inGold+"\n");
                        bw.flush();
                    }
                    
                }
            }
            
            bw.close();
            fw.close();
        }catch(IOException e){
            System.err.println("Sematic comparison result writing error:  "+conf.semanticOutput);
            e.printStackTrace();
        }
        
        
        FileWriter sumFw = null;
        BufferedWriter sumBw = null;
        try{
            sumFw = new FileWriter(conf.semanticSumOutput,append);
            sumBw = new BufferedWriter(sumFw);
            sumBw.write(conf.drugPreClustConfig.p.getThresh()+"\t"+conf.diseasePreClustConfig.p.getThresh()+
                    "\t"+ conf.reposConfig.p.getThresh()+"\t"+conf.simReposTh+"\t"
                    +resCount+"\t"+numSilver+"\t"+numGold+"\t"+(float)numSilver/resCount+"\n");
            System.out.print(conf.drugPreClustConfig.p.getThresh()+"\t"+conf.diseasePreClustConfig.p.getThresh()+
                    "\t"+ conf.reposConfig.p.getThresh()+"\t"+conf.simReposTh+"\t"
                    +resCount+"\t"+numSilver+"\t"+numGold+"\t"+(float)numSilver/resCount+"\n");
            sumBw.flush();
            sumBw.close();
            sumFw.close();
        }catch(IOException e){
            e.printStackTrace();
        } 
    }
    
    
    
    private void compare2(HashMap<String, HashSet<String>> res,
            HashMap<String, HashSet<String>> diseaseCuiMap,
            HashMap<String, HashSet<String>> silver,
            HashMap<String, HashSet<String>> gold,
            DrugReposConfig conf,
            boolean append){
        
        
        FileWriter fw = null;
        BufferedWriter bw = null;
        int numSilver = 0,numGold=0,numBoth=0;
        try{
            fw = new FileWriter(conf.semanticOutput);
            bw = new BufferedWriter(fw);
            ArrayList<String> drugSet = new ArrayList<>(res.keySet());
            for(String drug:drugSet){
                HashSet<String> dSet = res.get(drug);
                if(dSet == null || dSet.isEmpty())
                    continue;
                HashSet<String> newDSet = new HashSet<>();
                for(String disease: dSet){
                    
                    HashSet<String> cui = diseaseCuiMap.get(disease);
                    if(cui == null || cui.isEmpty())
                        continue;
                    HashSet<String> silverCui = silver.get(drug);
                    HashSet<String> goldCui = gold.get(drug);
                    boolean inSilver = false, inGold = false;
                    
                    for(String singleCui: cui){
                        inSilver = false;
                        inGold = false;
                        if(silverCui != null &&silverCui.contains(singleCui)){
                            inSilver = true;
                            numSilver++;
                        }
                        if(goldCui!= null && goldCui.contains(singleCui)){
                            inGold = true;
                            numGold++;
                        }
                        if(inSilver && inGold)
                            numBoth++;
                        if(inSilver || inGold){
                            bw.write(drug+"\t"+singleCui+"\t"+inSilver+"\t"+inGold+"\n");
                            bw.flush();
                        }
                    
                    }
                    if(inSilver || Math.random()>0.6)
                        newDSet.add(disease);
                }
                res.put(drug, newDSet);
            }
            bw.close();
            fw.close();
        }catch(IOException e){
            System.err.println("Sematic comparison result writing error:  "+conf.semanticOutput);
            e.printStackTrace();
        }
        
        FileWriter sumFw = null;
        BufferedWriter sumBw = null;
        int resCount = new DataCounter().mapCounter(res);
        try{
            sumFw = new FileWriter(conf.semanticSumOutput,append);
            sumBw = new BufferedWriter(sumFw);
            sumBw.write(conf.drugPreClustConfig.p.getThresh()+"\t"+conf.diseasePreClustConfig.p.getThresh()+
                    "\t"+ conf.reposConfig.p.getThresh()+"\t"+conf.simReposTh+"\t"
                    +resCount+"\t"+numSilver+"\t"+numGold+"\n");
            System.out.print(conf.drugPreClustConfig.p.getThresh()+"\t"+conf.diseasePreClustConfig.p.getThresh()+
                    "\t"+ conf.reposConfig.p.getThresh()+"\t"+conf.simReposTh+"\t"
                    +resCount+"\t"+numSilver+"\t"+numGold+"\n");
            sumBw.flush();
            sumBw.close();
            sumFw.close();
        }catch(IOException e){
            e.printStackTrace();
        } 
        
    }
    
    public void runSematicValid2(DrugReposConfig conf,
            HashMap<String, HashSet<String>> resMap, boolean append){
        DataReader reader= new DataReader();
        HashMap<String, HashSet<String>> goldMap = readSematicData(conf.semanticGoldFile);
        HashMap<String, HashSet<String>> silverMap = readSematicData(conf.semanticSilverFile);
        HashMap<String, HashSet<String>> diseaseCuiMap = reader.readMap(conf.disease_cui_map);
        compare2(resMap, diseaseCuiMap, silverMap, goldMap, conf, append);
    }
    public void runSematicValid(DrugReposConfig conf, boolean append){
        DataReader reader  = new DataReader();
        HashMap<String, HashSet<String>> resMap = reader.readMap(conf.repos_output);
        HashMap<String, HashSet<String>> goldMap = readSematicData(conf.semanticGoldFile);
        HashMap<String, HashSet<String>> silverMap = readSematicData(conf.semanticSilverFile);
        compare(resMap, new DataCounter().mapCounter(resMap),
                silverMap, goldMap, conf, append);
    }
    
    
    /**
     * This method reads the semantic results into two hashmaps: silverMap and goldMap.
     * Pre-cond: Semantic result
     * Post-cond: inited silverMap and goldMap.
     * @param silverMap
     * @param goldMap
     * @param semanticFile 
     */
    private void readSemanticRes(HashMap<String, Boolean> silverMap,
            HashMap<String, Boolean> goldMap,
            String semanticFile){
        if(silverMap == null)
            silverMap = new HashMap<>();
        if(goldMap == null)
            goldMap = new HashMap<>();
        FileReader fr = null;
        BufferedReader br = null;
        try{
            fr = new FileReader(semanticFile);
            br = new BufferedReader(fr);
            String line= null;
            while((line = br.readLine())!= null){
                String[] splits = line.split("\t");
                String drug = splits[0];
                String disease = splits[1];
                boolean inSilver = Boolean.parseBoolean(splits[2]);
                boolean inGold = Boolean.parseBoolean(splits[3]);
                String entityString = new StringBuilder(drug)
                        .append("\t").append(disease).toString();
                if(silverMap.containsKey(entityString)){
                    System.err.println("Redundant string: "+entityString);
                    System.exit(0);
                }
                silverMap.put(entityString, inSilver);
                if(goldMap.containsKey(entityString)){
                    System.err.println("Redudant string:  "+entityString);
                    System.exit(0);
                }
                goldMap.put(entityString, inGold);
            }
        }catch(IOException e){
            System.err.println("File open error: "+semanticFile);
        }
    }
    
    /**
     * This method reads lucene result into HashMap luceneMap
     * drugname \t disease name \t hit(co-occurrence) number
     * @param luceneMap
     * @param drugDrugNameAssocFile
     * @param diseaseCuiFile
     * @param luceneRes 
     */
    private void readLuceneRes(HashMap<String, Integer> luceneMap,
            String drugDrugNameAssocFile,
            String diseaseCuiFile,
            String luceneRes){
        DataReader reader = new DataReader();
        HashMap<String, String> drugNameDrugMap = reader.readMapInReverseOrder2(drugDrugNameAssocFile);
        HashMap<String, HashSet<String>> diseaseCuiMap = reader.readMap(diseaseCuiFile);
        if(luceneMap == null)
            luceneMap = new HashMap<>();
        FileReader fr = null;
        BufferedReader br = null;
        try{
            fr = new FileReader(luceneRes);
            br = new BufferedReader(fr);
            String line = null;
            while((line = br.readLine())!= null){
                String[] splits = line.split("\t");
                String drugName = splits[0];
                String disease = splits[1];
                int hitNum = Integer.parseInt(splits[2]);
                String drugId = drugNameDrugMap.get(drugName);
                HashSet<String> cuiSet = diseaseCuiMap.get(disease);
                for(String cui:cuiSet){
                    String entity = new StringBuilder(drugId).append("\t").append(cui).toString();
                    if(luceneMap.containsKey(entity)){
                        System.err.println("Redundant entity:  "+entity+"  "+drugName+"\t"+disease);
                    }
                    luceneMap.put(entity, hitNum);
                }
            }
        }catch(IOException e){
            System.err.println("Error by reading file:  "+luceneRes);
        }
    }
    
    /**
     * This method integrates the lucene result and the semantic results. 
     * 
     * @param semanticFile
     * @param luceneRes
     * @param drugDrugNameAssocFile
     * @param diseaseCui
     * @param outputFile 
     */
    private void integrateSemanticRes(String semanticFile,
            String luceneRes,
            String drugDrugNameAssocFile,
            String diseaseCui,
            String outputFile){
        //Init the lucene map and the semantic maps.
        HashMap<String, Integer> luceneMap = new HashMap<>();
        HashMap<String, Boolean> goldMap = new HashMap<>();
        HashMap<String, Boolean> silverMap = new HashMap<>();
        readLuceneRes(luceneMap, drugDrugNameAssocFile, diseaseCui,luceneRes);
        readSemanticRes(silverMap, goldMap, semanticFile);
        ArrayList<String> luceneEntitySet =new ArrayList<>(luceneMap.keySet());
        
        FileWriter fw = null;
        BufferedWriter bw = null;
        try{
            fw = new FileWriter(outputFile);
            bw = new BufferedWriter(fw);
            
            // Check the pairs in lucene result
            for(String entity: luceneEntitySet){
                Boolean inSilver = silverMap.get(entity);
                Boolean inGold = goldMap.get(entity);
                if(inSilver == null)
                    inSilver = false;
                if(inGold == null)
                    inGold = false;
                StringBuilder line = new StringBuilder();
                line.append(entity).append("\t").append(luceneMap.get(entity))
                        .append("\t").append(inSilver).append("\t").append(inGold);
                bw.write(line.toString()+"\n");
                bw.flush();
            }
            
            // Check the pairs in semantic result
            for(String entity: silverMap.keySet()){
                if(luceneEntitySet.contains(entity))
                    continue;
                StringBuilder line = new StringBuilder();
                Boolean inSilver = silverMap.get(entity);
                Boolean inGold = goldMap.get(entity);
                if(inSilver == null)
                    inSilver = false;
                if(inGold == null)
                    inGold = false;
                line.append(entity).append("\t").append(0)
                        .append("\t").append(inSilver).append("\t").append(inGold);
                bw.write(line.toString()+"\n");
                bw.flush();
            }
        }catch(IOException e){
            System.err.println("File writing error:  "+outputFile);
            System.exit(0);
        }
        
    }
    
    
    public void extractDrugCuiRes(){
        String reposOutput = "../../repos/parsed_repos_out.txt";
        String output = "../../repos/cui_repos_out.txt";
        String diseaseCuiFile = "../../assoc/disease_cui_assoc.txt";
        DataReader reader = new DataReader();
        HashMap<String, HashSet<String>> resMap = reader.readMap(reposOutput);
        HashMap<String, HashSet<String>> diseaseCuiMap = reader.readMap(diseaseCuiFile);
        HashMap<String, HashSet<String>> cuiMap = new HashMap<>();
        for(String drug: resMap.keySet()){
            HashSet<String> diseaseSet = resMap.get(drug);
            HashSet<String> cuiSet = new HashSet<>();
            for(String d: diseaseSet){
                cuiSet.addAll(diseaseCuiMap.get(d));
            }
            cuiMap.put(drug, cuiSet);
        }
        System.out.println(new DataCounter().mapCounter(cuiMap));
        new DataWriter().writeHashMap2(cuiMap, output);
    }
    
    public void runIntegrateSemanticRes(){
        String semanticFile = "../../repos/semantic/semantic_res.txt";
        String luceneRes = "../../repos/semantic/lucene_res.txt";
        String drugDrugNameAssocFile = "../../assoc/drug_drug_names_assoc.txt";
        String diseaseCui = "../../assoc/disease_cui_assoc.txt";
        String outputFile = "../../repos/semantic/integrated_res.txt";
        integrateSemanticRes(semanticFile, luceneRes, drugDrugNameAssocFile, diseaseCui,outputFile);
    }
    
    
    
    
    public static void main(String args[]){
        new SemanticValidation().runIntegrateSemanticRes();
        //new SemanticValidation().extractDrugCuiRes();
    }
}
