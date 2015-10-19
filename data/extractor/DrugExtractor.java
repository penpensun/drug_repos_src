/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package data.extractor;

import data.io.DataReader;
import data.io.DataWriter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import java.util.*;
import data.io.DataReader;
import data.io.DataWriter;
import java.io.*;
import data.io.DrugReposConfig;

/**
 * This class contains the extractors for drugs.
 * @author Peng Sun
 */
public class DrugExtractor {
    
    /**
     * This method parses the drug sim file from yuan zhao.
     * @param drug
     * @param output 
     * @param matrixOutput 
     * Un Test
     */
    public void parseYuanFile(String drug, String output, String matrixOutput){
        FileReader fr= null;
        BufferedReader br = null;
        HashSet<String> drugSet = new HashSet<>();
        DataReader reader = new DataReader();
        int numLines = reader.getLineNum(drug);
        int line=0;
        try{
            fr = new FileReader(drug);
            br = new BufferedReader(fr);
            while(true){
                String id1 = br.readLine();
                line++;
                String id2 = br.readLine();
                line++;
                br.readLine(); // Jump the coefficient line.
                line++;
                if(line%300000 == 0)
                    System.out.println((float)line/numLines*100+"% finished");
                if(id1 == null && id2 == null)
                    break;
                if(id1 == null && id2!= null)
                    System.err.println("id1 and id2 are not null at the same point:  "+id2);
                if(id2 == null && id1!= null)
                    System.err.println("id1 and id2 are not null at the same point:  "+id1);
                
                id1 = id1.trim();
                id2 = id2.trim();
                drugSet.add(id1);
                drugSet.add(id2);
            }
            br.close();
            fr.close();
        }catch(IOException e){
            System.err.println("(DrugExtractor.parseDrugIdsYuan) File reading error.");
            return;
        }
        System.out.println("Drug ids extraction finished.");
        //Create the matrix.
        ArrayList<String> drugList = new ArrayList<>(drugSet);
        float matrix[][] = new float[drugList.size()][drugList.size()];
        for(int i=0;i<matrix.length;i++)
            for(int j=0;j<matrix[0].length;j++)
                matrix[i][j] = Float.NaN;
        line = 0;
        try{
            fr = new FileReader(drug);
            br = new BufferedReader(fr);
            while(true){
                String id1 = br.readLine();
                line++;
                String id2 = br.readLine();
                line++;
                String coeffLine = br.readLine();
                line++;
                if(line%300000 == 0)
                    System.out.println((float)line/numLines*100+"% finished");
                if(id1 == null && id2 == null)
                    break;
                
                float coeff = -1;
                try{
                    coeff = Float.parseFloat(coeffLine.split(":")[1].trim());
                }catch(NumberFormatException e){
                    System.err.println("(DrugExtractor.parseDrugIdsYuan) The number format error:  "+coeffLine);
                }
                id1= id1.trim();
                id2 = id2.trim();
                coeffLine = coeffLine.trim();
                int idx1 = drugList.indexOf(id1);
                int idx2 = drugList.indexOf(id2);
                matrix[idx1][idx2] = coeff;
                matrix[idx2][idx1] = coeff;
            }
            br.close();
            fr.close();
        }catch(IOException e){
            System.err.println("(DrugExtractor.parseDrugIdsYuan) File reading error.");
            return;
        }
        System.out.println("Matrix extraction finished.");
        // Output the druglist and the matrix.
        DataWriter writer = new DataWriter();
        writer.writeIds(drugList, output, "\n");
        writer.writeMatrix(matrix, matrixOutput);
    }
   
    /**
     * This method extracts the drug ids.
     * Filter: (1) Within Yuan's id
     * (2) With a CAS number
     * (3) "Approved" or "nutraceutical"
     * pre-cond: drug bank xml
     * post-cond: The drug ids satisfying the 3 condition mentioned above are written to the given output file.
     * @param drugbankXml
     * @param drugIdYuan
     * @param drugIdOut
     * @param casNumberOut 
     */
    public void extractDrugIds(String drugbankXml, String drugIdYuan,
            String drugIdOut, String casNumberOut){
        List<Element> drugElementList = extractDrugList(drugbankXml);
        DataReader reader = new DataReader();
        ArrayList<String> drugIdsYuan = reader.readIds(drugIdYuan);
        HashSet<String> drugSet = new HashSet<>();
        HashMap<String, String> casNumberMap = new HashMap<>();
        for(Element drugElement: drugElementList){
            // Check if the group name is "Approved" or "nutraceutical", if neither, then continue;
            ArrayList<String> groupNameList = extractGroupName(drugElement);
            
            boolean flag = false;
            for(String groupName: groupNameList)
                if(groupName.equalsIgnoreCase("approved") ||
                    groupName.equalsIgnoreCase("nutraceutical"))
                    flag = true;
            if(!flag)
                continue;
            // Check if the drug has a CAS number;
            String casNumber = getCasNumber(drugElement);
            if(casNumber == null)
                continue;
            // Check if the drug is in Yuan's set.
            String drugId = extractPrimaryId(drugElement);
            if(!drugIdsYuan.contains(drugId))
                continue;
            drugSet.add(drugId);
            if(casNumberMap.containsKey(drugId)){
                System.out.println("Duplicated drug id:  "+drugId);
                continue;
            }    
            casNumberMap.put(drugId, casNumber);
        }
        
        //Create the arrayList for drug ids and cas ids.
        ArrayList<String> drugIdList = new ArrayList<>(drugSet);
        ArrayList<String> casNumberList = new ArrayList<>();
        for(String drug:drugIdList)
            casNumberList.add(casNumberMap.get(drug));
        // Output the drug id and the cas number.
        DataWriter writer = new DataWriter();
        writer.writeIds(drugIdList, drugIdOut, "\n");
        writer.writeIds(casNumberList, casNumberOut, "\n");
    }
    
    
    /**
     * This method extracts the drug names from drugbank xml
     * @param drugbankXml
     * @param drugIdFile
     * @param associationOut 
     */
    public void extractDrugNames(String drugbankXml, String drugIdFile,
            String associationOut){
        ArrayList<String> drugIds = new DataReader().readIds(drugIdFile);
        HashMap<String, String> map = new HashMap<>();
        List<Element> drugElementList = extractDrugList(drugbankXml);
        for(Element drugElement: drugElementList){
            String drugId = extractPrimaryId(drugElement);
            if(!drugIds.contains(drugId))
                continue;
            String drugName = extractDrugName(drugElement);
            map.put(drugId, drugName);
        }
        
        new DataWriter().writeHashMap4(map, associationOut);
    }
    
    
    /**
     * This method extracts the synonyms of the drugs in the given drug id list.
     * @param drugbankXml
     * @param drugIdFile
     * @return 
     */
    public HashMap<String, HashSet<String>> extractDrugSynonyms(String drugbankXml, String drugIdFile){
        List<Element> drugElementList = extractDrugList(drugbankXml);
        ArrayList<String> drugList = new DataReader().readIds(drugIdFile);
        HashMap<String, HashSet<String>> synonymMap = new HashMap<>();
        for(Element drugElement: drugElementList){
            String drugId = extractPrimaryId(drugElement);
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
        return synonymMap;
    }
    
    
    /**
     * This method extracts the drug-drug matrix based on the drug matrix in Yuan's data set.
     * Pre-cond: The drug-drug sim matrix coming from yuan's data.
     * Post-cond: The drug-drug sim matrix written to the given file.
     * @param drugIdFile
     * @param drugIdYuan
     * @param drugMatrixYuan
     * @param matrixOutput 
     */
    public void extractDrugMatrix(String drugIdFile, String drugIdYuan, String drugMatrixYuan,
            String matrixOutput){
        DataReader reader =new DataReader();
        ArrayList<String> drugList = reader.readIds(drugIdFile);
        ArrayList<String> drugListYuan = reader.readIds(drugIdYuan);
        // Read the yuan's matrix
        float[][] yuanMatrix = new float[drugListYuan.size()][drugListYuan.size()];
        yuanMatrix = reader.readMatrix(drugMatrixYuan, drugListYuan.size(), drugListYuan.size());
        
        float[][] matrix = new float[drugList.size()][drugList.size()];
        for(int i=0;i<matrix.length;i++){
            int idx1 = drugListYuan.indexOf(drugList.get(i));
            for(int j=0;j<matrix[0].length;j++){
                int finished = j*matrix[0].length+j;
                if(finished %10000 ==0)
                    System.out.println(finished/matrix[0].length/matrix.length*100+"% finished");
                int idx2 = drugListYuan.indexOf(drugList.get(j));
                matrix[i][j] = yuanMatrix[idx1][idx2];
            }
        }
        // Write the matrix.
        new DataWriter().writeMatrix(matrix, matrixOutput);
    }
    
    /**
     * This method extracts the cas number with the given drug element.
     * @param drugElement
     * @return 
     */
    private String getCasNumber(Element drugElement){
        /* Check the drugElement. */
        if(drugElement == null)
            throw new IllegalArgumentException("(DrugExtractor.getCasNumber) drugElement is null.");
        Element casNumberElement = drugElement.getChild("cas-number",drugElement.getNamespace());
        List<Content> contentList = casNumberElement.getContent();
        if(contentList.isEmpty())
            return null;
        else
            return contentList.get(0).getValue();
    }

    /**
     * This method extracts the approved and nutraceutical drugs.
     * @param drugXml
     * @return 
     */
    public ArrayList<String> extractApprNutraDrugIds(String drugXml){
        ArrayList<String> ans = new ArrayList<>();
        DrugExtractor drugEx = new DrugExtractor();
        List<Element> drugElementList = drugEx.extractDrugList(drugXml);
        /* Extract the drug element list. */
        for(Element drugElement: drugElementList){
            /* Get the group name.  */
            ArrayList<String> groupNames = extractGroupName(drugElement);
            boolean flag = false;
            for(String gname:groupNames){
                if(gname.equalsIgnoreCase("approved"))
                    flag = true;
                else if(gname.equals("nutraceutical"))
                    flag = true;
            }
            if(flag){
                /* If the flag is true, then we add the drug id into ans. */
                String drugId = drugEx.extractPrimaryId(drugElement);
                ans.add(drugId);
            }
        }
        return ans;
    }
    
    
    /**
     * This method extracts the smiles of the given ids and writes them
     * into the output file like the following:
     * 
     * drugid1 \t smiles1
     * drugid2 \t smiles2
     * @param conf
     * @param idFile 
     * @param output 
     */
    public void extractSmilesById(DrugReposConfig conf, String idFile, String output){
        DataReader reader = new DataReader();
        ArrayList<String> idList = reader.readIds(idFile);
        List<Element> drugElementList = extractDrugList(conf.drug_xml);
        HashMap<String, String> smilesMap = new HashMap<>();
        for(Element drug: drugElementList){
            String id = extractPrimaryId(drug);
            if(!idList.contains(id))
                continue;
            String smiles = getSmiles(drug);
            smilesMap.put(id, smiles);
        }
        new DataWriter().writeHashMap4(smilesMap, output);
    }
    
    /**
     * This method extracts the smiles of the given ids and writes them
     * into the output file like the following:
     * 
     * drugid1 \t smiles1
     * drugid2 \t smiles2
     * @param conf
     * @param nameFile
     * @param output
     */
    public void extractSmilesByName(DrugReposConfig conf, String nameFile, String output){
        DataReader reader = new DataReader();
        ArrayList<String> nameList = reader.readIds2(nameFile);
        for(int i=0;i<nameList.size();i++)
            nameList.set(i, nameList.get(i).toLowerCase());
        
        List<Element> drugElementList = extractDrugList(conf.drug_xml);
        HashMap<String, String> smilesMap = new HashMap<>();
        for(Element drug: drugElementList){
            String name = extractDrugName(drug);
            String name1 = name.toLowerCase();
            if(!nameList.contains(name1))
                continue;
            String id = extractPrimaryId(drug);
            System.out.println(id+"\t"+name);
            String smiles = getSmiles(drug);
            smilesMap.put(id, smiles);
        }
        new DataWriter().writeHashMap4(smilesMap, output);
    }
    
    
    /**
     * This method extracts the smiles String from a given drug element.
     * @param drug
     * @return 
     */
    protected String getSmiles(Element drug){
        List<Element> calPropElementList = 
                drug.getChildren("calculated-properties", drug.getNamespace());
        if(calPropElementList == null || calPropElementList.isEmpty())
            return null;
        if(calPropElementList.size()>1){
            System.err.println("More than one calculated-properties element.");
            return null;
        }
        String smiles = null;
        Element calProp = calPropElementList.get(0);
        List<Element> propertyList = calProp.getChildren("property",calProp.getNamespace());
        if(propertyList == null || propertyList.isEmpty())
            return null;
        for(Element property: propertyList){
            Element kindElement = property.getChild("kind",property.getNamespace());
            if(kindElement == null)
                continue;
            String kind = kindElement.getContent(0).getValue().trim();
            if(kind.equals("SMILES")){
                if(smiles != null){
                    System.err.println("More than one smiles-property element.");
                    return null;
                }
                Element valueElement = property.getChild("value", property.getNamespace());
                if(valueElement == null)
                    return null;
                smiles = valueElement.getContent(0).getValue();
                    
            }
        }
        return smiles;
    }
    
    /**
     * This method extracts the contents of the group elements.
     * Note that one drug can have multiple group name.
     * @param drugElement
     * @return 
     */
    public ArrayList<String> extractGroupName(Element drugElement){
        ArrayList<String> ans = new ArrayList<>();
        /* Extract the groups element. */
        Element groupsElement = drugElement.getChild("groups",drugElement.getNamespace());
        /* Get the list of group element. */
        List<Element> groupElementList = groupsElement.getChildren("group",drugElement.getNamespace());
        if(groupElementList.isEmpty()){
            System.err.println("(extractGroupName) The given drugElement is without group name.");
        }
        for(Element groupElement: groupElementList){
            ans.add(groupElement.getContent(0).getValue().trim());
        }
        return ans;
    }
    
   
    
    
    /**
     * This method extracts the drug ids with cas number
     * @param inputFile
     * @return The arrayList of the drug ids. 
     */
    public ArrayList<String> extractCasNumber(String inputFile){
        List<Element> drugElementList = extractDrugList(inputFile);
        ArrayList<String> ans = new ArrayList<>();
        for(Element drugElement:drugElementList){
            String casNumber = DrugExtractor.this.getCasNumber(drugElement);
            if(casNumber != null)
                ans.add(extractPrimaryId(drugElement));
        }
        return ans;
    }
    
    /**
     * This method extracts the drug-cas number relation.
     * @param drugFile
     * @param drugbankXml
     * @param outputFile 
     */
    public void extractDrugCasNumberRelation(String drugFile, String drugbankXml, String outputFile){
        DataReader reader = new DataReader();
        ArrayList<String> drugList= reader.readIds(drugFile);
        List<Element> drugElementList = extractDrugList(drugbankXml);
        HashMap<String, String> drugCasNumberRelation = new HashMap<>();
        
        for(Element drugElement: drugElementList){
            String drugId = extractPrimaryId(drugElement);
            if(!drugList.contains(drugId))
                continue;
            String casNumber = DrugExtractor.this.getCasNumber(drugElement);
            if(casNumber == null)
                continue;
            else{
                if(drugCasNumberRelation.containsKey(drugId))
                    System.out.println("Drugid -- multiple cas number:  "+drugId);
                else drugCasNumberRelation.put(drugId, casNumber);
            }
        }
        
        new DataWriter().writeHashMap4(drugCasNumberRelation, outputFile);
    }
    
    
    
    /**
     * This method extracts an arrayList of drug element from the drug bank xml.
     * @param drugbankXml
     * @return A List of "drug" element.
     */
    public List<Element> extractDrugList(String drugbankXml) {
        SAXBuilder builder = new SAXBuilder();
        Document drugbankDoc = null;
        try {
            drugbankDoc = builder.build(new File(drugbankXml));
        } catch (IOException e) {
            System.out.println("(dataprocessing.TargetsInformationExtractor.extractDrugElementList) Reading file error.");
            return null;
        } catch (JDOMException e) {
            System.out.println("(dataprocessing.TargetsInformationExtractor.extractDrugElementList) JDOM error");
        }
        if (drugbankDoc == null) {
            throw new IllegalStateException("(dataprocessing.TargetsInformationExtractor.extractDrugElementList) XML Doc not initialized. ");
        }
        Element root = drugbankDoc.getRootElement();
        Namespace ns = root.getNamespace();
        if (root == null) {
            throw new IllegalStateException("(dataprocessing.TargetsInformationExtractor.extractDrugElementList) No root element is found. ");
        }
        return root.getChildren("drug", ns);
    }
    
    /**
     * This method extracts the primary id from the given drug element.
     * @param drug The "drug" element.
     * @param ns The namespace used by drugbank
     * @return Null if there is no "drugbank-id" inside the element. Empty string if there is "drugbank-id" but without
     * primary="true" attribute. Complete primary id string if primary id is found. "multi"+first found primary id, if
     * there are multi-primary id.
     */
    public String extractPrimaryId(Element drug) {
        List<Element> drugbankIdElementList = drug.getChildren("drugbank-id", drug.getNamespace());
        if (drugbankIdElementList == null) {
            return null;
        } else {
            String primaryId = null;
            for (Element drugbankIdElement : drugbankIdElementList) {
                if (drugbankIdElement.getAttributeValue("primary").equals("true")) {
                    if (primaryId != null) {
                        return "multi:" + primaryId;
                    }
                    primaryId = drugbankIdElement.getContent(0).getValue().trim();
                    return primaryId;
                }
            }
            if (primaryId == null) {
                return "";
            }
        }
        return "";
    }
    
    /**
     * This method extracts the drug name of the given drug element.
     * @param drugElement
     * @return 
     */
    public String extractDrugName(Element drugElement){
        List<Element> drugNameElementList = drugElement.getChildren("name", drugElement.getNamespace());
        if(drugNameElementList == null)
            return null;
        else{
            if(drugNameElementList.size()>1)
                throw new IllegalStateException("Multiple drug names:  "+extractPrimaryId(drugElement));
            
            String drugName = drugNameElementList.get(0).getContent(0).getValue().trim();
            if(drugName == null)
                return null;
            else return drugName;
            
        }
        
    }
    
  
    /**
     * This method extracts the drug gene matrix.
     * @param drugIdFile
     * @param geneFile
     * @param relationFile
     * @param outputMatrix
     * @param ew 
     * @param antiEw 
     */
    public void extractDrugGeneMatrix(String drugIdFile, String geneFile, String relationFile,
            String outputMatrix,int ew, int antiEw){
        // Read the gene name file. 
        DataReader reader = new DataReader();
        ArrayList<String> geneList = reader.readIds(geneFile);
        ArrayList<String> drugList = reader.readIds(drugIdFile);
        // Create the matrix.
        float matrix[][] = new float[drugList.size()][geneList.size()];
        for(int i=0;i<matrix.length;i++)
            for(int j=0;j<matrix[0].length;j++)
                matrix[i][j] = antiEw;
        
        try{
        FileReader fr = new FileReader(relationFile);
        BufferedReader br = new BufferedReader(fr);
        String line =null;
        while((line = br.readLine())!= null){
            line = line.trim();
            if(line == null || line.isEmpty())
                continue;
            String[] splits = line.split("\t");
            String drugId = String.copyValueOf(splits[0].toCharArray()).trim();
            int drugIdx = drugList.indexOf(drugId);
            if(drugIdx == -1){
                System.err.println("(DrugExtractor.extractDrugGeneMatrix) The index of durg cannot be -1:  "+drugId);
                return;
            }
            for(int i=1;i<splits.length;i++){
                String gene = String.copyValueOf(splits[i].toCharArray()).trim().toUpperCase();
                int geneIdx = geneList.indexOf(gene);
                if(geneIdx == -1){
                    System.err.println("(DrugExtractor.extractDrugGeneMatrix) The index of gene name cannot be -1: "+gene);
                    return;
                }
                matrix[drugIdx][geneIdx] = ew;
            }
        }
        br.close();
        fr.close();
        }catch(IOException e){
            System.err.println("(DrugExtractor.extractGeneMatrix) Relation file opened error.");
            return;
        }
        /* Write the matrix into the file.*/
        DataWriter writer = new DataWriter();
        writer.writeMatrix(matrix, outputMatrix);
    }
    
     public void extractDrugGeneMatrix2(String drugListFile, String geneFile, String drugGeneAssoc,
            String outputFile, float posEw, float negEw){
        DataReader reader = new DataReader();
        ArrayList<String> drugList = reader.readIds(drugListFile);
        ArrayList<String> diseaseList = reader.readIds2(geneFile);
        HashMap<String, HashSet<String>> drugDiseaseMap  = reader.readMap(drugGeneAssoc);
        float matrix[][] = new float[drugList.size()][diseaseList.size()];
        
        //Init the matrix
        for(int i=0;i<matrix.length;i++)
            for(int j=0;j<matrix[0].length;j++)
                matrix[i][j] = negEw;
        
        Set<Map.Entry<String, HashSet<String>> > entrySet = drugDiseaseMap.entrySet();
        Iterator<Map.Entry<String, HashSet<String>>> entrySetIter = entrySet.iterator();
        while(entrySetIter.hasNext()){
            Map.Entry<String, HashSet<String>> entry = entrySetIter.next();
            String drug = entry.getKey();
            int drugIdx = drugList.indexOf(drug);
            HashSet<String> diseaseSet = entry.getValue();
            Iterator<String> diseaseIter = diseaseSet.iterator();
            while(diseaseIter.hasNext()){
                String disease = diseaseIter.next();
                int diseaseIdx = diseaseList.indexOf(disease);
                if(diseaseIdx ==-1)
                    continue;
                matrix[drugIdx][diseaseIdx] = posEw;
            }
        }
        new DataWriter().writeMatrix(matrix, outputFile);
    }
     
     
    public void extractCasNumber(){
        String drug = "DB06743";
        DataReader reader = new DataReader();
        ArrayList<String> drugList = reader.readIds("../../drug/drug_id.txt");
        ArrayList<String> casList = reader.readIds("../../drug/cas_number.txt");
        System.out.println(casList.get(drugList.indexOf(drug)));
    }
    
   
    public void runExtractDrugGeneMatrix(int interEw, int antiEw){
        String drugId = "../../drug/drug_id.txt";
        String geneNameIdFile = "../../gene/gene_id.txt";
        String relationFile = "../../assoc/drug_gene_assoc.txt";
        String outputMatrix = "../../drug/drug_gene_matrix.txt";
        extractDrugGeneMatrix2(drugId,geneNameIdFile,relationFile,outputMatrix,interEw, antiEw);
    }
    
     /**
     * This method extracts the drug-cas number relation.
     */
    public void runExtractDrugCasNumberRelation(){
        String drugIdFile ="../../drug/drug_id_3.txt";
        String drugbankXml = "../../drug/drugbank.xml";
        String output = "../../drug/drug_cas_relation_3.txt";
        extractDrugCasNumberRelation(drugIdFile,drugbankXml,output);
    }
    
    /**
     * This method extracts the yuan's drug data.
     */
    public void runExtractDrugYuan(){
        String drugSimYuan = "../../drug/yuan_data/db_similarity.txt";
        String drugIdsYuan = "../../drug/yuan_data/drug_id_yuan.txt";
        String matrixYuan = "../../drug/yuan_data/drug_matrix_yuan.txt";
        parseYuanFile(drugSimYuan, drugIdsYuan, matrixYuan);
    }
    
    /**
     * This method extracts the drug id satisfying the following 3 conditions:
     * (1) with Cas number
     * (2) "approved" or "nutraceutical"
     * (3) 
     */
    public void runExtractDrugId(){
        String drugIdsYuan = "../../drug/yuan_data/drug_id_yuan.txt";
        String drugbankXml = "../../drug/drugbank.xml";
        String drugIdOut = "../../drug/drug_id.txt";
        String casNumberOut = "../../drug/cas_number.txt";
        extractDrugIds(drugbankXml,drugIdsYuan,drugIdOut,casNumberOut);
    }
    
    /**
     * This method extracts the drug-drug sim from the yuan's drug-drug sim data.
     */
    public void runExtractDrugMatrix(){
        String drugIdFile = "../../drug/drug_id.txt";
        String drugIdYuan = "../../drug/yuan_data/drug_id_yuan.txt";
        String drugMatrixYuan = "../../drug/yuan_data/drug_matrix_yuan.txt";
        String matrixOutput = "../../drug/drug_matrix.txt";
        extractDrugMatrix(drugIdFile, drugIdYuan, drugMatrixYuan, matrixOutput);
    }
    
    
    
    public void runExtractDrugNames(){
        String drugFile ="../../drug/drug_id.txt";
        String drugXml = "../../drug/drugbank.xml";
        String output = "../../drug/drug_drug_names_assoc.txt";
        extractDrugNames(drugXml,drugFile, output);
    }
    
    public void runExtractSmilesById(){
        DrugReposConfig conf = new DrugReposConfig();
        new data.init.InitDrugReposConfig().initDrugReposConfig(conf);
        extractSmilesById(conf, conf.drug_id, conf.drug_smiles);
    }
    
    public void runExtractSmilesByName(){
        DrugReposConfig conf = new DrugReposConfig();
        new data.init.InitDrugReposConfig().initDrugReposConfig(conf);
        String compare2_append_names = "../../compare2/drug_nonoverlap.txt";
        String compare2_append_smiles = "../../compare2/drug_nonoverlap_smiles.txt";
        extractSmilesByName(conf,compare2_append_names,compare2_append_smiles);
    }
    
    public void testComputeMatrix(){
        DrugReposConfig conf = new DrugReposConfig();
         DataReader reader = new DataReader();
        new data.init.InitDrugReposConfig().initDrugReposConfig(conf);
        ArrayList<String> drugList = reader.readIds2(conf.drug_id);
        conf.drug_matrix = "../../matrix/new_drug_matrix.txt";
        //extractDrugMatrix(conf);
       
        float[][] oldMatrix = reader.readMatrix("../../matrix/drug_matrix.txt", 
                drugList.size(), drugList.size());
        float[][] newMatrix = reader.readMatrix(conf.drug_matrix, 
                drugList.size(), drugList.size());
        for(int i=0;i<drugList.size();i++){
            for(int j=i+1;j<drugList.size();j++){
                if(i == j && !Float.isNaN(newMatrix[i][j]))
                    System.err.println(i+"\t"+j);
                else if(newMatrix[i][j] != oldMatrix[i][j])
                    System.err.println(i+"\t"+j+" old: "+oldMatrix[i][j]+
                            "  new:  "+newMatrix[i][j]);
            }
        }
        
    }

    
    
    
    public static void main(String[] args){
       
       //new DrugExtractor().runExtractDrugYuan();
       //new DrugExtractor().runExtractDrugId();
       //new DrugExtractor().runExtractDrugMatrix();
       //new DrugExtractor().testMatrix();
       //new DrugExtractor().runExtractDrugGeneMatrix(1, 0);
       //new DrugExtractor().extractCasNumber();
       //new DrugExtractor().runExtractDrugNames();
       //new DrugExtractor().runExtractDrugGeneMatrix(1,0);
       //new DrugExtractor().runExtractSmilesById();
       //new DrugExtractor().testComputeMatrix();
       //new DrugExtractor().modMatrix();
       new DrugExtractor().runExtractSmilesByName();
    }
    
    
    @Deprecated
    private void testMatrix(){
        String matrix = "../../drug/drug_matrix.txt";
        ArrayList<String> drugList = new DataReader().readIds("../../drug/drug_id.txt");
        float[][] drugMatrix = new DataReader().readMatrix(matrix, drugList.size(), drugList.size());
        System.out.println(drugMatrix[858][1542]);
        System.out.println(drugMatrix[881][1542]);
        System.out.println(drugMatrix[982][1542]);
        
        String drugDiseaseMatrixFile = "../../drug/drug_disease_matrix.txt";
        ArrayList<String> diseaseList = new DataReader().readIds2("../../disease/disease_id.txt");
        float[][] drugDiseaseMatrix = new DataReader().readMatrix(drugDiseaseMatrixFile, drugList.size(), diseaseList.size());
        System.out.println(drugDiseaseMatrix[drugList.indexOf("DB00602")][diseaseList.indexOf("Abortion, Spontaneous")]);
        System.out.println(drugDiseaseMatrix[drugList.indexOf("DB00602")][diseaseList.indexOf("Hepatitis, Autoimmune")]);
        System.out.println(drugDiseaseMatrix[drugList.indexOf("DB00602")][diseaseList.indexOf("Arrhythmias, Cardiac")]);
        System.out.println(drugDiseaseMatrix[drugList.indexOf("DB00629")][diseaseList.indexOf("Adenocarcinoma")]);
        System.out.println(drugDiseaseMatrix[drugList.indexOf("DB00629")][diseaseList.indexOf("Kidney Diseases")]);
        System.out.println(drugDiseaseMatrix[drugList.indexOf("DB00629")][diseaseList.indexOf("Cocaine-Related Disorders")]);
        System.out.println(drugDiseaseMatrix[drugList.indexOf("DB00788")][diseaseList.indexOf("Synovitis")]);
        System.out.println(drugDiseaseMatrix[drugList.indexOf("DB00788")][diseaseList.indexOf("Abortion, Spontaneous")]);
        System.out.println(drugDiseaseMatrix[drugList.indexOf("DB01059")][diseaseList.indexOf("Adenocarcinoma")]);
        System.out.println(drugDiseaseMatrix[drugList.indexOf("DB01059")][diseaseList.indexOf("Dermatitis, Atopic")]);
        System.out.println(drugDiseaseMatrix[drugList.indexOf("DB01059")][diseaseList.indexOf("Granuloma")]);
    }
    
}
