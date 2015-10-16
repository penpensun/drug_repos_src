/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.checker;
import java.io.*;
import java.util.*;
import org.jdom2.*;
import org.jdom2.input.*;
import data.extractor.DrugExtractor;
import data.io.DataReader;
import biforce.graphs.*;
import data.preprocess.*;
/**
 * In this class I write several "checking" methods to check some properties of the data set. 
 * @author penpen926
 */
public class DataChecker {
    
    /**
     * This method returns if a drug element has a cas number.
     * pre-cond: Given a drugElement
     * post-cond: Return if the drug element has a fasta.
     * @param drugbankXml
     */
    private void checkCasNumber(String drugbankXml){
        DrugExtractor drugEx = new DrugExtractor();
        List<Element> drugElementList = drugEx.extractDrugList(drugbankXml);
        int drugWithOneCas = 0;
        for(Element drugElement: drugElementList){
            if(drugElement == null)
                throw new IllegalArgumentException("(DataChecker.checkCasNumber) drugElement is null.");
            List<Element> casNumberElementList = drugElement.getChildren("cas-number", drugElement.getNamespace());
            String drugPrimaryId = drugEx.extractPrimaryId(drugElement);
            //Check if there is no cas number for this drug element. 
            if(casNumberElementList.isEmpty())
                System.out.println("No cas-number element for drug:  "+drugPrimaryId);
            else if(casNumberElementList.size()>1)
                System.out.println("More than one cas-number elemet for drug:  "+drugPrimaryId);
            else
                drugWithOneCas++;
        
        }
        System.out.println("Totally "+drugWithOneCas+" drugs with one cas number");
    }
    
    
    
    
    /**
     * This method checkes if there exists any inconsistency in the fasta sequences given the ligand element
     * and the fasta file storing the sequence of the ligand. That is, to check
     * are there two same ligands with different fasta sequences, present in different drug elements. 
     * @param ligandElement The ligand element.
     * @param fastaFile The file storing the fasta sequence.
     * @return False if there is no inconsistency. True if there is. 
     */
    private boolean ligandFastaInconsistency(Element ligandElement, String fastaFile){
        /* Check the arguments first. */
        if(ligandElement == null )
            throw new IllegalArgumentException("(DataChecker.ligandFastaInconsistency) The ligand element cannot be null.");
        if (fastaFile == null)
            throw new IllegalArgumentException("(DataChecker.ligandFastaInconsistency) The fasta file cannot be null. ");
        /* Extract the id of the ligandElement. */
        Element idElement = ligandElement.getChild("id",ligandElement.getNamespace());
        /* Check the idElement. */
        if(idElement == null)
            throw new IllegalStateException("(DataChecker.ligandFastaInconsistency) Here we have a ligand without id. Error. ");
        String id = idElement.getContent(0).getValue();
        if(id== null|| id.isEmpty())
            throw new IllegalStateException("(DataChecker.ligandFastaInconsistency) Fasta extract failed. ");
        /* Check if there is any fasta element in the ligandElement. */
        Element polypeptideElement = ligandElement.getChild("polypeptide",ligandElement.getNamespace());
        if(polypeptideElement == null){
            System.out.println("(DataChecker.ligandFastaInconsistency) The polypeptide child element inside the ligand element is null.");
            return false;
        }
        
        Element aminoAcidSeqElement = polypeptideElement.getChild("amino-acid-sequence",ligandElement.getNamespace());
        if(aminoAcidSeqElement == null){
            System.out.println("(DataChecker.ligandFastaInconsistency) The amino-acid-sequence element inside the amino-acid-sequence element is null");
            return false;
        }
        /* Get the fasta sequence. */
        String fastaSeq = aminoAcidSeqElement.getContent(0).getValue().trim();
        if(fastaSeq == null|| fastaSeq.isEmpty())
            throw new IllegalStateException("(DataChecker.ligandFastaInconsistency) The fasta sequence extraction failed.");
        /* Parse the extracted fasta sequence into header and body. */
        String fastaBodyFromElement = fastaSeq.substring(fastaSeq.indexOf("\n")+1);
        
        /* Read the fasta from the given file.*/
        FileReader fr = null;
        BufferedReader br = null;
        String fastaBodyFromFile = null;
        String fastaHeaderFromFile = null;
        
        try{
        fr= new FileReader(fastaFile);
        br = new BufferedReader(fr);
        String line = null;
        /* The first line is header. */
        fastaHeaderFromFile = br.readLine();
        StringBuilder fastaBodyBuilder = new StringBuilder();
        while((line = br.readLine())!= null){
            fastaBodyBuilder.append(line+"\n");
        }
        fastaBodyFromFile = fastaBodyBuilder.toString().trim();
        }catch(IOException e){
            System.err.println("(DataChecker.ligandFastaInconsistency) The fasta file cannot be opend.");
            return true;
        }
        /* Check the two fasta. */
        if(!fastaBodyFromFile.equals(fastaBodyFromElement))
            return true;
        else if(!fastaHeaderFromFile.contains(id))
            return true;
        else 
            return false;
    }
    
    
    
    private void drugNamesInconsistency(){
        ArrayList<String> drugbankNames = new ArrayList<>();
        ArrayList<String> yuandrugNames = new DataReader().readIds("../../drug_drug_sim/similarityscore/yuan/drug_names.txt");
        
        String drugbankXml = "../../drug/drugbank.xml";
        DrugExtractor extractor = new DrugExtractor();
        List<Element> drugElementList= extractor.extractDrugList(drugbankXml);
        for(Element drugElement: drugElementList){
            drugbankNames.add(extractor.extractPrimaryId(drugElement));
        }
        
        for(String drugId: yuandrugNames){
            if(!drugbankNames.contains(drugId))
                System.out.println(drugId);
        }  
    }
    
    private void twoLigandIncon(){
        ArrayList<String> ligand1 = new DataReader().readIds("../../ligand/ligand_data/ligand_fasta_yuan.txt");
        ArrayList<String> ligand2 = new DataReader().readIds("../../ligand/ligand_fasta_yuan.txt");
        System.out.println("ligand 1 size: "+ligand1.size());
        System.out.println("ligand 2 size: "+ligand2.size());
        for(String id :ligand1){
            if(!ligand2.contains(id))
                System.out.println(id);
        }
    }
    
    /**
     * This method checks the similarities between the diseases associated with one drug, and outputs the average value.
     * @param drugDiseaseAssociationFile
     * @param diseaseListFile 
     * @param diseaseMatrixFile 
     */
    public void checkDiseaseSimByDrug(String drugDiseaseAssociationFile, String diseaseListFile, String diseaseMatrixFile){
        HashMap<String, HashSet<String>> map = new DataReader().readMap(drugDiseaseAssociationFile);
        ArrayList<String> diseaseList = new DataReader().readIds2(diseaseListFile);
        Set<Map.Entry<String,HashSet<String>>> mapEntrySet = map.entrySet();
        Iterator<Map.Entry<String, HashSet<String>>> iterEntrySet = mapEntrySet.iterator();
        
        float[][] matrix = new DataReader().readMatrix(diseaseMatrixFile, diseaseList.size(), diseaseList.size());
        while(iterEntrySet.hasNext()){
            Map.Entry<String,HashSet<String>> mapEntry = iterEntrySet.next();
            String drug = mapEntry.getKey();
            HashSet<String> diseaseSet = mapEntry.getValue();
            ArrayList<String> diseases = new ArrayList<>(diseaseSet);
            float sum = 0;
            int num = 0;
            for(int i=0;i<diseases.size();i++)
                for(int j=i+1;j<diseases.size();j++){
                    if(Float.isNaN(matrix[diseaseList.indexOf(diseases.get(i))][diseaseList.indexOf(diseases.get(j))]))
                        continue;
                    num++;
                    sum+= matrix[diseaseList.indexOf(diseases.get(i))][diseaseList.indexOf(diseases.get(j))];
                }
            System.out.println(sum/num);
        }
    }
    
    /**
     * This method checks the similarities between diseases associated/ not associated with one drug.
     * @param drugDiseaseAssociationFile
     * @param diseaseListFile
     * @param drugFile
     * @param diseaseMatrixFile 
     */
    public void checkDiseaseSimByDrug2(String drugDiseaseAssociationFile, String diseaseListFile, String drugFile, String diseaseMatrixFile){
        DataReader reader = new DataReader();
        HashMap<String, HashSet<String>> map = new DataReader().readMap(drugDiseaseAssociationFile);
        ArrayList<String> drugList = reader.readIds(drugFile);
        ArrayList<String> diseaseList = reader.readIds2(diseaseListFile);
        float[][] matrix = reader.readMatrix(diseaseMatrixFile, diseaseList.size(),diseaseList.size());
        for(String drug:drugList){
            HashSet<String> diseaseSet = map.get(drug);
            if(diseaseSet == null)
                continue;
            float sum = 0;
            int num= 0;
            for(String assocDisease: diseaseSet){
                for(String disease: diseaseList){
                    if(diseaseSet.contains(disease))
                    {}
                    else if(Float.isNaN(matrix[diseaseList.indexOf(assocDisease)][diseaseList.indexOf(disease)]))
                    {}
                    else{
                        
                        num++;
                        sum+=matrix[diseaseList.indexOf(assocDisease)][diseaseList.indexOf(disease)];
                    }
                }
            }
            sum /=num;
            System.out.println(sum);
        }
    }
    
    /**
     * This method checks if the disease-disease matrix is properly initialized.
     * Since disease-disease matrix (ver 5.) creating method gives some error, I have to check if the matrix is properly initialized.
     * (1) splits length smaller than 5
     * (2) Disease 2 cannot be found.
     * (3) Number format exception.
     * @param diseaseFile
     * @param diseaseMatrix 
     */
    public void checkDiseaseDiseaseMatrix(String diseaseFile, String diseaseMatrix, String output){
        DataReader reader = new DataReader();
        ArrayList<String> diseaseList = reader.readIds2(diseaseFile);
        FileWriter fw = null;
        BufferedWriter bw = null;
        try{
            fw = new FileWriter(output);
            bw = new BufferedWriter(fw);
        }catch(IOException e){
            System.err.println("File writer inti error.");
            return;
        }
        
        float matrix[][] = reader.readMatrix(diseaseMatrix, diseaseList.size(), diseaseList.size());
        for(int i=0;i<matrix.length;i++)
            for(int j=i;j<matrix[0].length;j++){
                if(i == j && !Float.isNaN(matrix[i][j])){
                    System.err.println("Same disease non NaN:  "+diseaseList.get(i));
                }
                if(i!= j){
                    if(Float.isNaN(matrix[i][j])){
                        System.err.println("Difference diseases NaN:  "+i+"\t"+diseaseList.get(i)+"\t"+j+"\t"+diseaseList.get(j));
                        try{bw.write(diseaseList.get(i)+"\t"+diseaseList.get(j)+"\n");}catch(IOException e){System.err.println("Write error");}
                    }
                    if(matrix[i][j] <0){
                        System.err.println("Difference diseases <0:  "+i+"\t"+diseaseList.get(i)+"\t"+j+"\t"+diseaseList.get(j));
                        try{bw.write(diseaseList.get(i)+"\t"+diseaseList.get(j)+"\n");}catch(IOException e){System.err.println("Write error");}
                    }
                }
                    
            }
        try{
            bw.flush();
            bw.close();
            fw.close();
        }catch(IOException e){
            System.err.println("Writer close error.");
        }
    }
    
    public void runCheckDiseaseDiseaseMatrix(){
        String diseaseFile = "../../disease/id/disease_5.txt";
        String diseaseMatrix = "../../disease/disease_disease_matrix_5.txt";
        String output = "../../disease/disease_5_nan_pairs.txt";
        checkDiseaseDiseaseMatrix(diseaseFile, diseaseMatrix,output);
    }
    
    public void runCheckDiseaseSimByDrug(){
        checkDiseaseSimByDrug("../../drug/ctd_drug_disease_relation.txt", "../../disease/id/disease_5.txt", "../../disease/disease_disease_matrix_5.txt");
    }
    
    public void runCheckDiseaseSimByDrug2(){
        checkDiseaseSimByDrug2("../../drug/ctd_drug_disease_relation.txt", "../../disease/id/disease_5.txt","../../drug/drug_id_3.txt", "../../disease/disease_disease_matrix_5.txt");
    }
    
    
    public void checkSubGraphs(){
        String graphInput = "../../nforce_input/repos/inputxml.txt";
        NpartiteGraph g  = new NpartiteGraph(graphInput, false, true);
        g.detractThresh(0.9f);
        ArrayList<NpartiteSubgraph> conSubs = g.connectedComponents();
        System.out.println("Many subgraphs:  "+ conSubs.size());
        for(int i=0;i<conSubs.size();i++)
            System.out.println(conSubs.get(i).vertexCount());
    }
    
    
    public void checkSizes(){
        DataReader reader = new DataReader();
        ArrayList<String> diseaseList = new ArrayList<>();
        diseaseList = reader.readIds2("./disease_inserted.txt");
        String diseaseClustersFile = "../../nforce_output/disease/disease_cluster_index_mapping.txt";
        HashMap<String, int[]> diseaseClusters = new PreClusterParser().parsePreClusterIndex(diseaseClustersFile, "disease");
        for(String d: diseaseList){
            int[] array = diseaseClusters.get(d);
            System.out.println(array.length);
        }
    }
    
    
    public void checkParsedResult(){
        DataReader reader = new DataReader();
        HashMap<String, HashSet<String>> parsedResult = reader.readMap("../../drug/drug_disease_assoc.txt");
        HashMap<String, HashSet<String>> negResult = reader.readMap("../../disease/disease_negative_set_all_2.txt");
        ArrayList<String> drugList = new ArrayList<>(parsedResult.keySet());
        int fp=0;
        for(String drug:drugList){
            ArrayList<String> diseaseRes = new ArrayList<>(parsedResult.get(drug));
            for(String disease: diseaseRes){
                HashSet<String> negSet = negResult.get(drug);
                if(negSet!= null && negSet.contains(disease)){
                    fp++;
                    System.out.println(drug+"\t"+disease);
                }
            }
        }
    }
    
    public void mapCounter(String mapFile){
        DataReader reader = new DataReader();
        HashMap<String, HashSet<String>> map =  reader.readMap(mapFile);
        int count = 0;
        ArrayList<String> keySet = new ArrayList<>(map.keySet());
        for(String key: keySet){
            HashSet<String> valueSet = map.get(key);
            count += valueSet.size();
        }
        System.out.println("The map size:  "+count);
    }
    
    /**
     * This method checks the number of lines with the given word
     * @param ctdRelation 
     */
    public void checkLineNum(String word, String ctdFile, String output){
        ArrayList<String> drugs = new DataReader().readIds("../../id/cas_number.txt");
        int count =0;
        FileReader fr = null;
        BufferedReader br = null;
        FileWriter fw = null;
        BufferedWriter bw = null;
        try{
            fr = new FileReader(ctdFile);
            br = new BufferedReader(fr);
            fw = new FileWriter(output);
            bw = new BufferedWriter(fw);
            String line = null;
            while((line = br.readLine())!= null){
                if(line.startsWith("#") || line.isEmpty())
                    continue;
                String drug = line.split("\t")[2];
                if(line.toLowerCase().contains(word.toLowerCase()) &&
                        drugs.contains(drug)){
                        count++;
                        bw.write(line+"\n");
                }
                
            }
            bw.flush();
            bw.close();
            fw.close();
            br.close();
            fr.close();
        }catch(IOException e){
            System.err.println("Error");
        }
        System.out.println(count);
    }
    
    public static void main(String args[]){
        //new DataChecker().drugNamesInconsistency();
        //new DataChecker().twoLigandIncon();
        //new DataChecker().checkMatrixSize(1543, 1543, "../../drug/drug_sim_matrix_2.txt");
        //new DataChecker().checkOrganAndForm("../../ctd/CTD_chem_gene_ixns.tsv");
        //new DataChecker().runCheckDiseaseDiseaseMatrix();
        //new DataChecker().runCheckDiseaseSimByDrug2();
        //new DataChecker().checkSubGraphs();
        //new DataChecker().checkSizes();
        //new DataChecker().checkParsedResult();
        //new DataChecker().mapCounter("../../cv/cv_gsp.txt");
        //new DataChecker().mapCounter("../../gsn/disease_negative_set_0.3.txt");
        new DataChecker().checkLineNum("colorectal cancer", "../../ctd/CTD_chemicals_diseases.tsv",
                "../../colorectal_cancer_count.txt");
       
        
    }
}
