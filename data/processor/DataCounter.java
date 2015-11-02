/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package data.processor;
import data.io.DataReader;
import data.io.DataWriter;
import java.io.*;
import java.util.*;
import org.jdom2.*;
import java.util.HashSet;
import data.extractor.DrugExtractor;


/**
 * This class contains the methods to extract some important information of the targets in drugbank.xml
 * @author mac-97-41
 */
public class DataCounter {
    
    public int mapCounter(String mapFile){
        DataReader reader = new DataReader();
        HashMap<String, HashSet<String>> map =  reader.readMap(mapFile);
        return mapCounter(map);
    }
    
    public int mapCounter(HashMap<String, HashSet<String>> map){
        int count = 0;
        Set<Map.Entry<String,HashSet<String>>> set = map.entrySet();
        for(Map.Entry<String, HashSet<String>> entry: set){
            count+= entry.getValue().size();
        }
        return count;
        /*
        ArrayList<String> keySet = new ArrayList<>(map.keySet());
        for(String key: keySet){
            HashSet<String> valueSet = map.get(key);
            count += valueSet.size();
        }
        return count;
        */
    }
    /**
     * This method counts the number of drug elements;
     * the number of drug elements with one primary id; 
     * the number of drug elements with no primary id;
     * the number of drug elements with multiple primary id.
     */
    private void countDrugPrimaryId(){ 
        String drugbankXml = "../../drug/drugbank.xml";
        DrugExtractor extractor = new DrugExtractor();
        List<Element> drugElementList = extractor.extractDrugList(drugbankXml);
        if(drugElementList == null)
            throw new IllegalStateException("(dataprocessing.TargetInformationExtractor) No drug element found in the root element.");
        int numDrugOnePrimaryId = 0; /* The number of drugs with one primary id. */
        int numDrugMultiPrimaryId = 0; /* The number of drugs with multiple primary ids. */
        int numDrugNoPrimaryId = 0; /* The number of drugs with no primary id. */
        
        for(Element drugElement: drugElementList){
            int numPrimaryId = 0;
            List<Element> drugbankIdElementList = drugElement.getChildren("drugbank-id",drugElement.getNamespace());
            /* If the drug element does not have drugbank-id element. */
            if(drugbankIdElementList == null) 
                numDrugNoPrimaryId++;
            for(Element drugbankIdElement: drugbankIdElementList){
                if(drugbankIdElement.getAttributeValue("primary")== null)
                {}
                else if(drugbankIdElement.getAttributeValue("primary").equals("true"))
                    numPrimaryId++;
            }
            /* Check the numPrimaryId. */
            if(numPrimaryId == 0)
                numDrugNoPrimaryId ++;
            else if(numPrimaryId ==1)
                numDrugOnePrimaryId++;
            else
                numDrugMultiPrimaryId++;
        }
        System.out.println("The number of the drug elements before filtering:  "+drugElementList.size());
        System.out.println("The number of the drug elements having one primary id:  "+numDrugOnePrimaryId);
        System.out.println("The number of the drug elements having multiple primary id:  "+numDrugMultiPrimaryId);
        System.out.println("The number of the drug elements having no primary id:  "+numDrugNoPrimaryId);
    }
    
    /**
     * This method count how many gene name --> multiple ligand id relations are there in the 
     * gene name -- ligand relation file.
     * @param geneNameFile
     * @param ligandGeneNameRelationFile 
     */
    private void countGeneNameLigandMultiple(
            String geneNameFile, String ligandGeneNameRelationFile){
        // Read the gene list.
        DataReader reader = new DataReader();
        ArrayList<String> geneNameList = reader.readIds2(geneNameFile);
        int count =0;
        for(String geneName:geneNameList){
            ArrayList<String> ligandIdList = extractLigandId(geneName,ligandGeneNameRelationFile);
            if(ligandIdList.size() >1){
                count++;
                System.out.print(geneName+"\t");
                for(String ligandId:ligandIdList)
                    System.out.print(ligandId+"\t");
                System.out.println();
            }
        }
        System.out.println("Count:  "+count);
    }
    
    private ArrayList<String> extractLigandId(String geneName, String relationFile){
        ArrayList<String> ligandList = new ArrayList<>();
        try{
        FileReader fr = new FileReader(relationFile);
        BufferedReader br = new BufferedReader(fr);
        String line =null;
        while((line =br.readLine())!= null){
            line = line.trim();
            if(line.isEmpty())
                continue;
            String[] splits = line.split("\t");
            for(String name: splits){
                name = name.trim();
                if(name.equals(geneName))
                    ligandList.add(String.copyValueOf(splits[0].toCharArray()));
            }
        }
        }catch(IOException e){
            System.err.println("(DataCounter.extractLigandId) File reading error.");
        }
        return ligandList;
    }
    
    /**
     * This method counts the number of drugs with cas number in drugbank.xml
     */
    private void countCasNumber(){
        String drugbankXml = "../../drug/drugbank.xml";
        DrugExtractor extractor = new DrugExtractor();
        ArrayList<Element> drugElementList = 
                new ArrayList<>(extractor.extractDrugList(drugbankXml));
        int countDrugMultiCas = 0 ;
        int countDrugCas = 0;
        int countDrugNoCas = 0;
        for(Element drugElement: drugElementList){
            List<Element> casElementList =drugElement.getChildren("cas-number", drugElement.getNamespace()) ;
            int numCas = 0; /* This variable stores the number of the cas-number. */
            for(Element casElement: casElementList){
                if(!casElement.getContent().isEmpty())
                    numCas++;
            }
            if(numCas== 0)
                countDrugNoCas++;
            else if(numCas>1 ){
                countDrugMultiCas++;
                countDrugCas++;
            }
            else
                countDrugCas++;
                
        }
        System.out.println("(DataCounter.countCasNumber) The number of drugs without cas number:  "+countDrugNoCas);
        System.out.println("(DataCounter.countCasNumber) The number of drugs with cas number:  "+countDrugCas);
        System.out.println("(DataCounter.countCasNumber) The number of drugs with multi cas number:  "+countDrugMultiCas);
        
    }
    
    /**
     * This method counts the number of drug element with one groups child element, 
     * with none groups child element and with more than one groups child element. 
     */
    private void countGroups(){
        String drugbankXml = "../../drug/drugbank.xml";
        DrugExtractor extractor = new DrugExtractor();
        List<Element> drugElementList = extractor.extractDrugList(drugbankXml);
        int countGroups = 0;
        int countMultiGroups = 0;
        int countNoGroups = 0;
        /* Extract the drug element list.*/
        for(Element drugElement:drugElementList){
            List<Element> groupsList = drugElement.getChildren("groups",drugElement.getNamespace());
            if(groupsList == null || groupsList.isEmpty())
                countNoGroups++;
            else if(groupsList.size() == 1)
                countGroups ++;
            else 
                countMultiGroups ++;
        }
        System.out.println("(DataCounter.countGroups) The number of drugs with multi groups:  "+countMultiGroups);
        System.out.println("(DataCounter.countGroups) The number of drugs with one groups: "+countGroups);
        System.out.println("(DataCounter.countGroups) The number of drugs with no groups: "+countNoGroups);
    }
    /**
     * This method counts the number of drugs in each group. 
     */
    private void countGroup(){
        String drugbankXml = "../../drug/drugbank.xml";
        DrugExtractor extractor = new DrugExtractor();
        List<Element> drugElementList = extractor.extractDrugList(drugbankXml);
        int countApproved = 0;
        int countNutraceutical = 0;
        int countIllicit = 0;
        int countInvestigational = 0;
        int countWithdrawn = 0;
        int countExperimental = 0;
        for(Element drugElement:drugElementList){
            /* Get the groups element. */
            Element groupsElement = drugElement.getChild("groups",drugElement.getNamespace());
            /* Get the list of group element. */
            List<Element> groupElementList = groupsElement.getChildren("group",drugElement.getNamespace());
            for(Element groupElement: groupElementList){
                /* Get the content of the group element. */
                String groupContent = groupElement.getContent(0).getValue().trim();
                if(groupContent.equalsIgnoreCase("approved"))
                    countApproved++;
                if(groupContent.equalsIgnoreCase("nutraceutical"))
                    countNutraceutical++;
                if(groupContent.equalsIgnoreCase("illicit"))
                    countIllicit++;
                if(groupContent.equalsIgnoreCase("investigational"))
                    countInvestigational++;
                if(groupContent.equalsIgnoreCase("withdrawn"))
                    countWithdrawn++;
                if(groupContent.equalsIgnoreCase("experimental"))
                    countExperimental++;
            }
        }
        
        System.out.println("(DataCounter.countGroup) The number of approved drugs:  "+countApproved);
        System.out.println("(DataCounter.countGroup) The number of nutraceutical drugs:  "+countNutraceutical);
        System.out.println("(DataCounter.countGroup) The number of illicit drugs: "+countIllicit);
        System.out.println("(DataCounter.countGroup) The number of investigational drugs:  "+countInvestigational);
        System.out.println("(DataCounter.countGroup) The number of withdrawn drugs:  "+countWithdrawn);
        System.out.println("(DataCounter.countGroup) The number of experimental drugs:  "+countExperimental);
    }
    
   
    
    /**
     * This method counts the number of "pathways" in the drug elements
     * @param drugListFilter
     * @param drugIds
     */
    private void countPathways(boolean drugListFilter, ArrayList<String> drugIds){
        /* Check the argument. */
        if(drugListFilter && drugIds == null){
            System.err.println("(DataCounter.countPathWays) The given drugIds is null");
            return;
        }
        DrugExtractor extractor = new DrugExtractor(); /* Create the InfoExtractor. */
        DataWriter writer = new DataWriter(); /* Create the DataWriter. */ 
        String drugbankXml = "../../drug/drugbank.xml";
        
        int onePathwayCount=0;
        int multiPathwayCount = 0;
        int nonPathwayCount = 0;
        int totalCount =0;
        int filteredCount = 0;
        /* Extract all drug elements. */
        List<Element> drugElementList = extractor.extractDrugList(drugbankXml);
        for(Element drugElement: drugElementList){
            totalCount++;
            String drugId = extractor.extractPrimaryId(drugElement);
            /* If the drug id is not in the ArrayList of drug ids that we are interested in. */
            if(drugListFilter && !drugIds.contains(drugId))
                continue;
            filteredCount++;
            /* Then get the pathways tag from the drugId. */
            List<Element> pathwaysElementList = drugElement.getChildren("pathways",drugElement.getNamespace());
            if(pathwaysElementList == null || pathwaysElementList.isEmpty())
                nonPathwayCount++;
            else if(pathwaysElementList.size() == 1)
                onePathwayCount++;
            else multiPathwayCount++;
                
        }
        System.out.println("filter drug id size:  "+drugIds.size());
        System.out.println("The total count:  "+ totalCount);
        System.out.println("The filtered count:  "+filteredCount);
        System.out.println("With none pathways tag: "+nonPathwayCount);
        System.out.println("With one pathway tag:  "+onePathwayCount);
        System.out.println("With multi pathway tag:  "+multiPathwayCount);
    }
    
    
    
    
    private void runCountPathways(){
        //String drugIdsYuan = "../../drug_drug_sim/similarityscore/yuan/drug_names.txt";
        String drugIds2 = "../../drug/drug_ids_2.txt";
        ArrayList<String> drugIds = new DataReader().readIds(drugIds2);
        countPathways(true,drugIds);
    }
    
    /**
     * This method counts the number of associations.
     * @param input
     * @param output 
     */
    private void associationCounter(String input, String output){
        FileWriter fw = null;
        BufferedWriter bw = null;
        try{
        fw = new FileWriter(output);
        bw = new BufferedWriter(fw);
        }catch(IOException e){
            System.err.println("(DataCounter.associationCounter) File writer error.");
            return;
        }
        HashMap<String, HashSet<String>> map = new DataReader().readMap(input);
        Set<Map.Entry<String,HashSet<String>>> mapEntrySet = map.entrySet();
        Iterator<Map.Entry<String, HashSet<String>>> iter = mapEntrySet.iterator();
        while(iter.hasNext()){
            Map.Entry<String, HashSet<String>> entry = iter.next();
            String key = entry.getKey();
            int numOfValue = entry.getValue().size();
            try{
            bw.write(key+"\t"+numOfValue+"\n");
            }catch(IOException e){
                System.err.println("(DataCounter.associationCounter) File writing error.");
                return;
            }
        }
        try{
        bw.flush();
        bw.close();
        fw.close();
        }catch(IOException e){
            System.err.println("(DataCounter.associationCounter) File writing error.");
            return;
        }
    }
    
    /**
     * This method counts the
     * @param keggDiseaseRelationFile 
     */
    private void diseaseCounter(String keggDiseaseRelationFile, String outFile){
        FileReader fr = null;
        BufferedReader br = null;
        FileWriter fw = null;
        BufferedWriter bw = null;
        HashSet<String> diseases = new HashSet<>();
        try{
        fr = new FileReader(keggDiseaseRelationFile);
        br = new BufferedReader(fr);
        fw = new FileWriter(outFile);
        bw = new BufferedWriter(fw);
        
        String line= null;
        while((line = br.readLine())!= null){
            if(line.trim().isEmpty())
                continue;
            String[] splits = line.split("\\s+");
            for(int i=1;i<splits.length;i++){
                diseases.add(String.copyValueOf(splits[i].toCharArray()));
            }
        }
        
        bw.close();
        fw.close();
        br.close();
        fr.close();
        }catch(IOException e){
            System.err.println("(DataCounter.diseaseCounter) File i/o error.");
            return;
        }
        System.out.println("The number of diseases: "+diseases.size());
        new DataWriter().writeHashSet(diseases, outFile, "\n");
    }
    
    
    private void runDataCounter(){
        String keggDiseaseRelationFile = "../../disease/kegg_disease.txt";
        String outFile = "../../disease/disease_id_yuan.txt";
        diseaseCounter(keggDiseaseRelationFile,outFile);
    }
    
    private void runCasCounter(){
        countCasNumber(); 
    }
    
    private void runGroups(){
        countGroups();
    }
    
    private void runGroup(){
        countGroup();
    }
    
    /**
     * This method runs association counter on drug-disease, drug-genename, and genename-disease associations.
     */
    private void runAssociationCounter(){
        String drugGenenameInput = "../../drug/drug_genename_relation_5.txt";
        String drugDiseaseInput = "../../drug/ctd_drug_disease_relation.txt";
        String genenameDiseaseInput = "../../ligand/geneName_disease_relation_5.txt";
        String drugGenenameOutput = "../../drug/drug_geneanme_association_count_5.txt";
        String drugDiseaseOutput = "../../drug/drug_disease_association_count_5.txt";
        String genenameDiseaseOutput = "../../ligand/geneName_disease_association_count.txt";
        associationCounter(drugGenenameInput, drugGenenameOutput);
        associationCounter(drugDiseaseInput, drugDiseaseOutput);
        associationCounter(genenameDiseaseInput, genenameDiseaseOutput);
    }
    
    
    public static void main(String args[]){
        //new DataCounter().runCounterLigand();
        //new DataCounter().runCounterLigandAll();
        //new DataCounter().runCounterLigandYuan();
        //new DataCounter().runCounterLigandAllYuan();
        //new DataCounter().runCountPathways();
        //new DataCounter().runCountPathway();
        //new DataCounter().runDataCounter();
        //new DataCounter().countCasNumber();
        //new DataCounter().countGroups();
        //new DataCounter().countGroup();
        
        //new DataCounter().runCountPathways();
        //new DataCounter().runCountPathway();
        //new DataCounter().countGeneNameLigandMultiple(
          //      "../../ligand/filtered_geneName_5.txt",
            //    "../../ligand/ligand_geneName_relation_5.txt");
        
        //new DataCounter().runAssociationCounter();
        System.out.println(new DataCounter().mapCounter("../../gsn/negative_0.3.txt"));
    }
    
}
