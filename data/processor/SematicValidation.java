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
import data.init.InitDrugReposConfig;
/**
 *
 * @author penpen926
 */
public class SematicValidation {
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
                    if(inSilver || Math.random()>0.5)
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
    
    
    
    
    public static void main(String args[]){
        
    }
}
