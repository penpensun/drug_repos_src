/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.io.*;
import java.util.HashMap;
import java.util.HashSet;

/**
 * In this class there are several method to read data from the given file.
 * @author penpen926
 */
public class DataReader {

    /**
     * This method extracts the drug ids from yuan's drug list.
     * @param drugFileYuan
     * @return
     */
    public ArrayList<String> readIds(String drugFileYuan) {
        FileReader drugNameFr = null;
        BufferedReader drugNameBr = null;
        try {
            drugNameFr = new FileReader(drugFileYuan);
            drugNameBr = new BufferedReader(drugNameFr);
        } catch (IOException e) {
            System.out.println("(dataprocessing.TargetsInformationExtractor.extractNonRedunTargetsYuan) Drug name file reading error: "+drugFileYuan);
        }
        ArrayList<String> drugIdList = new ArrayList<>();
        String line = null;
        try {
            while ((line = drugNameBr.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] splits = line.split("\n");
                for (String nodeName : splits) {
                    drugIdList.add(String.copyValueOf(nodeName.toCharArray()));
                }
            }
        } catch (IOException e) {
            System.out.println("(dataprocessing.TargetInformationExtractor.extractTarget) Read line error.");
        }
       // if (drugIdList.size() != 6829) {
         //   throw new IllegalStateException("(dataprocessing.TargetInformationExtractor.extractTarget) The length of the nodes arraylist is expected to be 6829, which really is: " + drugIdList.size());
        //}
        return drugIdList;
    }
    
    /**
     * This method reads the nbt result file and returns a drug--disease association hashmap.
     * @param nbtFile
     * @return 
     */
    public HashMap<String, HashSet<String>> readNbtMap(String nbtFile){
        FileReader fr = null;
        BufferedReader br = null;
        HashMap<String, HashSet<String>> map = new HashMap<>();
        try{
            fr = new FileReader(nbtFile);
            br= new BufferedReader(fr);
            
        }catch(IOException e){
            System.err.println("File reading error:  "+nbtFile);
            e.printStackTrace();
            return null;
        }
        try{
            String line= null;
            while((line = br.readLine())!= null){
                String[] splits = line.split("\t");
                String drug = String.copyValueOf(splits[0].toCharArray());
                String disease = String.copyValueOf(splits[5].toCharArray());
                if(!map.containsKey(drug))
                    map.put(drug,new HashSet<>());
                map.get(drug).add(disease);
            }
        }catch(IOException e){
            System.err.println("File reading error:  "+nbtFile);
            return null;
        }
        return map;
    }
    /**
     * This methdo reads the disease ids from the given file.
     * Sep="\n"
     * @param diseaseFile
     * @return 
     */
    public ArrayList<String> readIds2(String diseaseFile){
        FileReader diseaseFileFr = null;
        BufferedReader diseaseFileBr = null;
        try {
            diseaseFileFr = new FileReader(diseaseFile);
            diseaseFileBr = new BufferedReader(diseaseFileFr);
        } catch (IOException e) {
            System.out.println("(dataprocessing.TargetsInformationExtractor.extractNonRedunTargetsYuan) Drug name file reading error.");
        }
        ArrayList<String> diseaseList = new ArrayList<>();
        String line = null;
        try {
            while ((line = diseaseFileBr.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] splits = line.split("\n");
                for (String nodeName : splits) {
                    diseaseList.add(String.copyValueOf(nodeName.toCharArray()));
                }
            }
        } catch (IOException e) {
            System.out.println("(dataprocessing.TargetInformationExtractor.extractTarget) Read line error.");
        }
       // if (drugIdList.size() != 6829) {
         //   throw new IllegalStateException("(dataprocessing.TargetInformationExtractor.extractTarget) The length of the nodes arraylist is expected to be 6829, which really is: " + drugIdList.size());
        //}
        return diseaseList;
    }

    /**
     * This method extracts the ligand ids from the ligand id file filtered by yuan's drug list.
     * @param ligandFileYuan
     * @return
     */
    public ArrayList<String> readLigandIds(String ligandFileYuan) {
        if (ligandFileYuan == null || !new File(ligandFileYuan).exists()) {
            throw new IllegalArgumentException("(extractLigandIdsYuan) The ligand file is null or does not exists!");
        }
        ArrayList<String> ligandIds = new ArrayList<>();
        try {
            FileReader fr = new FileReader(ligandFileYuan);
            BufferedReader br = new BufferedReader(fr);
            String line = null;
            while ((line = br.readLine()) != null) {
                String lineTrim = line.trim();
                if (lineTrim.isEmpty()) {
                } else {
                    ligandIds.add(lineTrim);
                }
            }
        } catch (IOException e) {
            System.out.println("(extractLigadnIdsYuan) Read ligand file error. ");
            return null;
        }
        return ligandIds;
    }
    
    
    public ArrayList<String> readPathwayIds(String pathwayIds){
        /* Check the pathwayIds. */
        
        try{
            ArrayList<String> ans = new ArrayList<>();
            FileReader fr = new FileReader(pathwayIds);
            BufferedReader br = new BufferedReader(fr);
            String line= null;
            while((line = br.readLine())!= null){
                ans.add(line.trim());
            }
            br.close();
            fr.close();
            return ans;
        }catch(IOException e){
            System.err.println("(DataReader.readPathwayIds) File read error.");
            return null;
        }
    }
    
    /**
     * This matrix reads 
     * @param filePath
     * @param rows
     * @param cols
     * @return 
     */
    public float[][] readMatrix(String filePath,int rows, int cols){
        FileReader fr = null;
        BufferedReader br= null;
        try{
            fr = new FileReader(filePath);
            br = new BufferedReader(fr); 
        }catch(IOException e){
            e.printStackTrace();
        }
        float matrix[][] = new float[rows][cols];
        
        try{
        String line= null;
        int rowIdx = 0;
        while((line = br.readLine())!= null){
            if(line.trim().isEmpty()){
                continue;
            }
            if(rowIdx >= rows)
                throw new IllegalArgumentException("(DataReader.readMatrix) Rows out of bound:  "+rows);
            line = line.trim();
            String[] splits = line.split("\\s+");
            if(cols != splits.length)
                throw new IllegalArgumentException("(DataReader.readMatrix) Exp cols:  "+cols+"  real cols:  "+splits.length);
            for(int i=0;i<splits.length;i++){
                matrix[rowIdx][i] = Float.parseFloat(splits[i]);
            }
            rowIdx++;   
        }
        }catch(IOException e){
            e.printStackTrace();
        }
        return matrix;
    }
    
    /**
     * This method is used to read normal hashmap.
     * @param filePath
     * @return 
     */
    public HashMap<String,HashSet<String>> readMap(String filePath){
        HashMap<String, HashSet<String>> map = new HashMap<>();
        
        try{
        FileReader fr = new FileReader(filePath);
        BufferedReader br = new BufferedReader(fr);
        String line = null;
        while((line =br.readLine())!= null){
            line = line.trim();
            if(line.isEmpty())
                continue;
            String[] splits = line.split("\t");
            String drug = String.copyValueOf(splits[0].toCharArray());
            
            if(!map.containsKey(drug))
                map.put(drug, new HashSet<>());
            else System.out.println("Duplicated drug: "+drug);
            // Get the entry of the map.
            HashSet<String> setEntry = map.get(drug);
            for(int i=1;i<splits.length;i++)
                setEntry.add(String.copyValueOf(splits[i].toCharArray()));
        }
        }catch(IOException e){
            System.err.println("(DataReader.readMap) File reading error:  "+filePath);
            return null;
        }
        return map;
    }
    
    
    /**
     * This method is used to read the relation files with each line only two items.
     * @param filePath
     * @return 
     */
    public HashMap<String, String> readMap2(String filePath){
        try{
        FileReader fr = new FileReader(filePath);
        BufferedReader br= new BufferedReader(fr);
        HashMap<String, String> ans = new HashMap<>();
        String line= null;
        while((line = br.readLine())!= null){
            line = line.trim();
            if(line.isEmpty())
                continue;
            String[] splits = line.split("\t");
            if(splits.length >2)
                throw new IllegalArgumentException("(DataReader.readMap2) There is a line with more than 2 splits:  "+line);
            String item1 = String.copyValueOf(splits[0].toCharArray());
            String item2 = String.copyValueOf(splits[1].toCharArray());
            if(ans.containsKey(item1)){
                System.err.println("(DataReader.readMap2) Duplicated map entry:  "+item1);
                return null;
            }
            ans.put(item1, item2);
        }
        
        return ans;
        }catch(IOException e){
            System.err.println("(DataReadaer.readMap2) File reading error.");
            return null;
        }
        
    }
    
    /**
     * This method reads map written in the following order:
     * value1 \t key1
     * value2 \t key2
     * Pre-cond: The input file
     * Post-cond: The Hashmap is returned.
     * @param filePath
     * @return 
     */
    public HashMap<String, HashSet<String>> readMapInReverseOrder(String filePath){
        try{
            FileReader fr = new FileReader(filePath);
            BufferedReader br = new BufferedReader(fr);
            HashMap<String, HashSet<String>> ans = new HashMap<>();
            String line= null;
            while((line = br.readLine())!= null){
                line = line.trim();
                if(line.isEmpty())
                    continue;
                String[] splits = line.split("\t");
                if(splits.length >2)
                    throw new IllegalArgumentException("(DataReader.readMap2) There is a line with more than 2 splits:  "+line);
                String item1 = String.copyValueOf(splits[0].toCharArray());
                String item2 = String.copyValueOf(splits[1].toCharArray());
                if(!ans.containsKey(item2))
                    ans.put(item2, new HashSet<>());
                ans.get(item2).add(item1);
                
            }
            return ans;
        }catch(IOException e){
            System.err.println("(DataReader.readMapInReverseOrder) File reading error:  "+filePath);
        }
        return null;
    }
    
    public HashMap<String, String> readMapInReverseOrder2(String filePath){
        try{
            FileReader fr = new FileReader(filePath);
            BufferedReader br = new BufferedReader(fr);
            HashMap<String, String> ans = new HashMap<>();
            String line= null;
            while((line = br.readLine())!= null){
                line = line.trim();
                if(line.isEmpty())
                    continue;
                String[] splits = line.split("\t");
                if(splits.length >2)
                    throw new IllegalArgumentException("(DataReader.readMap2) There is a line with more than 2 splits:  "+line);
                String item1 = String.copyValueOf(splits[0].toCharArray());
                String item2 = String.copyValueOf(splits[1].toCharArray());
                ans.put(item2, item1);
                
            }
            return ans;
        }catch(IOException e){
            System.err.println("(DataReader.readMapInReverseOrder) File reading error:  "+filePath);
        }
        return null;
    }
    
    
    public HashMap<String, String> readMapInReverseOrder3(String filePath){
        try{
            FileReader fr = new FileReader(filePath);
            BufferedReader br = new BufferedReader(fr);
            HashMap<String, String> ans = new HashMap<>();
            String line= null;
            while((line = br.readLine())!= null){
                line = line.trim();
                if(line.isEmpty())
                    continue;
                String[] splits = line.split("\t");
                
                String item1 = String.copyValueOf(splits[0].toCharArray());
                for(int i=1;i<splits.length;i++){
                    String item2 = String.copyValueOf(splits[i].toCharArray());
                    ans.put(item2,item1);
                }
                
            }
            return ans;
        }catch(IOException e){
            System.err.println("(DataReader.readMapInReverseOrder) File reading error:  "+filePath);
        }
        return null;
    }
    
    public HashMap<String, HashSet<String>> readMapInReverseOrder4(String filePath){
        try{
            FileReader fr = new FileReader(filePath);
            BufferedReader br = new BufferedReader(fr);
            HashMap<String, HashSet<String>> ans = new HashMap<>();
            String line= null;
            while((line = br.readLine())!= null){
                line = line.trim();
                if(line.isEmpty())
                    continue;
                String[] splits = line.split("\t");
                
                String item1 = String.copyValueOf(splits[0].toCharArray());
                for(int i=1;i<splits.length;i++){
                    String item2 = String.copyValueOf(splits[i].toCharArray());
                    if(!ans.containsKey(item2))
                        ans.put(item2, new HashSet<>());
                    ans.get(item2).add(item1);
                }
                
            }
            return ans;
        }catch(IOException e){
            System.err.println("(DataReader.readMapInReverseOrder) File reading error:  "+filePath);
        }
        return null;
    }
    /**
     * This method returns the number of lines in the given file. 
     * @param fileName
     * @return 
     */
    public int getLineNum(String fileName){
        try{
            int i=0;
            FileReader fr = new FileReader(fileName);
            BufferedReader br = new BufferedReader(fr);
            String line = null;
            while((line =br.readLine())!= null){
                i ++;
            }
            fr.close();
            br.close();
            return i;
        }catch(IOException e){
            System.err.println("(DataReader.getLineNum) File reading error:  "+fileName);
            return -1;
        }
    }
    
    /**
     * This method extracts the ligand id given a drug id in the drug-ligand relation file.
     * @param drugLigandRelationFile
     * @return 
     */
    public ArrayList<String> readLigands(String drugLigandRelationFile, String drugId){
        /* Init the reader. */
        FileReader fr = null;
        BufferedReader br = null;
        try{
            fr = new FileReader(drugLigandRelationFile);
            br = new BufferedReader(fr);
        }catch(IOException e){
            System.err.println("(DataReader.readLingand) The file reader init error.");
            return null;
        }
        ArrayList<String> ligandList = new ArrayList<>();
        try{
        String line = null;
        while((line =br.readLine())!= null){
            line = line.trim();
            if(line.isEmpty())
                continue;
            String[] splits = line.split("\t");
            if(splits.length<=1)
                continue;
            if(!splits[0].equals(drugId)) /* Check if the drugid is what we need. */
                continue;
            for(int i=1;i<splits.length;i++){
                String ligandId = splits[i].trim();
                if(!ligandId.isEmpty())
                    ligandList.add(String.copyValueOf(ligandId.toCharArray()));
            }
        }
        }catch(IOException e){
            System.err.println("(DataReader.readLigand) The file reading error.");
            return null;
        }
        if(ligandList.isEmpty())
            return null;
        else return ligandList;
    }
    
    /**
     * This method reads the assoc map with seperator as "|".
     * @param inputFile
     * @return 
     */
    public HashMap<String,HashSet<String>> readMap4(String inputFile){
        FileReader fr = null;
        BufferedReader br = null;
        try{
            fr= new FileReader(inputFile);
            br = new BufferedReader(fr);
        }catch(IOException e){
            System.err.println("(ReadMap4) File reader init error.");
            e.printStackTrace();
        }
        
        String line= null;
        HashMap<String, HashSet<String>> map = new HashMap<>();
        try{
            while((line = br.readLine())!= null){
                line = line.trim();
                if(line.isEmpty())
                    continue;
                String[] splits = line.split("\\|");
                if(splits.length<2)
                    System.err.println("Wrong:  "+line);
                String meshId = String.copyValueOf(splits[0].toCharArray());
                String cuiId = String.copyValueOf(splits[1].toCharArray());
                if(!map.containsKey(meshId))
                    map.put(meshId, new HashSet<>());
                map.get(meshId).add(cuiId);
            }
        }catch(IOException e){
            System.err.println("Reading error.");
            e.printStackTrace();
        }
        
        return map;
    }
    
}
