/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package data.extractor;
import data.io.DataReader;
import java.util.*;
import java.io.*;
import data.io.DataWriter;
import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;

/**
 * This class contains the extracting methods for diseases.
 * @author Peng Sun
 */
public class DiseaseExtractor {
    /**
     * This method extracts the drug associated diseases and the drug--disease associations.
     * @param casFile
     * @param drugFile
     * @param ctdDrugDiseaseFile
     * @param diseaseOutput 
     * @param assocOutput 
     */
    public void extractDiseaseByDrug(String casFile, String drugFile,
            String ctdDrugDiseaseFile,
            String diseaseOutput,
            String assocOutput){
        /* Check the parameters. */
        if(ctdDrugDiseaseFile == null || !new File(ctdDrugDiseaseFile).exists())
            throw new IllegalArgumentException("(DiseaseExtractor.extractDisease) The input ctd file is wrong.");
        if(casFile == null || ! new File(casFile).exists())
            throw new IllegalArgumentException("(DiseaseExtractor.extractDisease) The input cas file is wrong.");
        FileWriter diseaseFw = null;
        BufferedWriter diseaseBw = null;
        
        FileWriter assocFw = null;
        BufferedWriter assocBw = null;
        try{
            diseaseFw = new FileWriter(diseaseOutput);
            diseaseBw = new BufferedWriter(diseaseFw);
            diseaseBw.close();
            diseaseFw.close();
        }catch(IOException e){
            System.err.println("(DiseaseExtractor.extractDisease) Disease output file writer error.");
            return;
        }
        
        try{
            assocFw = new FileWriter(assocOutput);
            assocBw = new BufferedWriter(assocFw);
            assocBw.close();
            assocFw.close();
        }catch(IOException e){
            System.err.println("(DiseaseExtractor.extractDisease) Associated output file writer error.");
            return;
        }
        HashMap<String, HashSet<String>> drugDiseaseMap = new HashMap<>();
        HashSet<String> diseaseSet = new HashSet<>();
        
        
        DataReader reader = new DataReader();
        ArrayList<String> casList = reader.readIds(casFile);
        ArrayList<String> drugList = reader.readIds(drugFile);
        int manyLines = reader.getLineNum(ctdDrugDiseaseFile);
        int i=0;
        FileReader fr= null;
        BufferedReader br = null;
        try{
            fr = new FileReader(ctdDrugDiseaseFile);
            br = new BufferedReader(fr);
        }catch(IOException e){
            System.err.println("(DiseaseExtractor.extractDisease) File reader error.");
            return;
        }
        try{
        String line = null;
        while((line=br.readLine())!= null){
             /* Jump the header. */
            if(line.startsWith("#"))
                continue;
            line = line.trim();
            if(line.isEmpty())
                continue;
            /* Split the line. */
            String[] splits = line.split("\t");
            /* Get the cas number. */
            String casNumber = String.copyValueOf(splits[2].toCharArray());
            if(casNumber.trim().isEmpty())
            {}
            else /* We check the casNumber. */
                if(casList.contains(casNumber)){
                    String diseaseName = String.copyValueOf(splits[3].toCharArray());
                    diseaseSet.add(diseaseName);
                    String drug = drugList.get(casList.indexOf(casNumber));
                    if(!drugDiseaseMap.containsKey(drug))
                        drugDiseaseMap.put(drug, new HashSet<>());
                    drugDiseaseMap.get(drug).add(diseaseName);
                }
            
        }
        }catch(IOException e){
            System.err.println("(DiseaseExtractor.extractDisease) File reading error.");
            return;
        }
        
        //Output the disease list.
        DataWriter writer = new DataWriter();
        writer.writeHashSet(diseaseSet, diseaseOutput, "\n");
        writer.writeHashMap2(drugDiseaseMap, assocOutput);
        
    }

    /**
     * This method extracts the disease-cui assoc.
     * @param diseaseInput
     * @param diseaseMeshAssoc
     * @param meshCuiAssoc
     * @param output 
     */
    public void extractDiseaseCuiMap(String diseaseInput, String diseaseMeshAssoc, 
            String meshCuiAssoc, String output){
        DataReader reader = new DataReader();
        ArrayList<String> diseaseIds = reader.readIds2(diseaseInput);
        HashMap<String, HashSet<String>> diseaseMeshMap = reader.readMap(diseaseMeshAssoc);
        HashMap<String, HashSet<String>> meshCuiMap = reader.readMap(meshCuiAssoc);
        HashMap<String, HashSet<String>> ans = new HashMap<>();
        for(String id: diseaseIds){
            HashSet<String> meshIdSet = diseaseMeshMap.get(id);
            HashSet<String> cuiIdSet = new HashSet<>();
            if(meshIdSet == null || meshIdSet.isEmpty()){
                System.err.println("No mesh id found:\t"+id);
                continue;
            }
            for(String meshId:meshIdSet){
                HashSet<String> c = meshCuiMap.get(meshId);
                if(c != null)
                    cuiIdSet.addAll(c);
            }
            if(cuiIdSet.isEmpty())
                System.err.println("empty cui:\t"+id);
            else ans.put(id, cuiIdSet);
        }
        
        //Output
        DataWriter writer = new DataWriter();
        writer.writeHashMap2(ans, output);
    }
    
    /**
     * This method extracts the cui-cui matrix.
     * @param cuiId
     * @param cuiSim
     * @param output 
     */
    public void extractCuiSim(String cuiId, String cuiSim, String output){
        
    }
    
    /**
     * This method extracts the umls id out from the mesh-umls id mapping file.
     * @param input
     * @param output 
     */
    public void extractUmlsCui(String input, String output){
        FileReader fr = null;
        BufferedReader br = null;
        try{
            fr = new FileReader(input);
            br = new BufferedReader(fr);
        }catch(IOException e){
            System.err.println("File reader error:  "+input);
        }
        HashSet<String> cuiSet = new HashSet<>();
        try{
            String line= null;
            while((line = br.readLine())!= null){
                line = line.trim();
                String[] splits = line.split("\\|");
                String meshId = splits[0];
                String cui = splits[1];
                cuiSet.add(cui);
            }
        }catch(IOException e){
            System.err.println("File reading error.");
        }
        
        DataWriter writer = new DataWriter();
        writer.writeHashSet(cuiSet, output, "\n");
    }
    
    
    
    
    /**
     * This method extracts the gene associated diseases from the drug-associated diseases and the
     * gene - disease association.
     * @param casFile
     * @param drugFile
     * @param ctdDrugDiseaseFile
     * @param geneFile
     * @param ctdGeneDiseaseFile
     * @param diseaseOutput
     * @param drugDiseaseAssocOutput
     * @param geneDiseaseAssocOutput 
     */
    public void extractDisease(String casFile, 
            String drugFile,
            String ctdDrugDiseaseFile,
            String geneFile,
            String ctdGeneDiseaseFile,
            String diseaseOutput,
            String drugDiseaseAssocOutput,
            String geneDiseaseAssocOutput){
        /* Check the parameters. */
        if(ctdDrugDiseaseFile == null || !new File(ctdDrugDiseaseFile).exists())
            throw new IllegalArgumentException("(DiseaseExtractor.extractDisease) The input ctd file is wrong.");
        if(casFile == null || ! new File(casFile).exists())
            throw new IllegalArgumentException("(DiseaseExtractor.extractDisease) The input cas file is wrong.");
        
        DataReader reader = new DataReader(); // Init the data reader.
        //Read the gene list.
        ArrayList<String> geneList = reader.readIds(geneFile);
        //Init the disease set by drugs.
        HashSet<String> diseaseSetByDrug = new HashSet<>();
        //Init the drug-disease assoc by drugs.
        HashMap<String, HashSet<String>> drugDiseaseMap = new HashMap<>();
        //Read the drug list and the cas list.
        ArrayList<String> casList = reader.readIds(casFile);
        ArrayList<String> drugList = reader.readIds(drugFile);
        
        int manyLines = reader.getLineNum(ctdDrugDiseaseFile);
        int i=0;
        FileReader fr= null;
        BufferedReader br = null;
        // Here we extract the drug-disease associations.
        System.out.println("Drug-disease associations.");
        try{
            fr = new FileReader(ctdDrugDiseaseFile);
            br = new BufferedReader(fr);
        }catch(IOException e){
            System.err.println("(DiseaseExtractor.extractDisease) File reader error.");
            return;
        }
        try{
        String line = null;
        while((line=br.readLine())!= null){
            i++;
            if(i%500000 == 0)
                System.out.println((float)i/manyLines*100+"% finished.");
             /* Jump the header. */
            if(line.startsWith("#"))
                continue;
            line = line.trim();
            if(line.isEmpty())
                continue;
            /* Split the line. */
            String[] splits = line.split("\t");
            /* Get the cas number. */
            String casNumber = String.copyValueOf(splits[2].toCharArray());
            if(casNumber.trim().isEmpty())
            {}
            else /* We check the casNumber. */
                if(casList.contains(casNumber)){
                    String diseaseName = String.copyValueOf(splits[3].toCharArray());
                    diseaseSetByDrug.add(diseaseName);
                    String drug = drugList.get(casList.indexOf(casNumber));
                    if(!drugDiseaseMap.containsKey(drug))
                        drugDiseaseMap.put(drug, new HashSet<>());
                    drugDiseaseMap.get(drug).add(diseaseName);
                }
        }
        br.close();
        fr.close();
        }catch(IOException e){
            System.err.println("(DiseaseExtractor.extractDisease) File reading error.");
            return;
        }
        
        //Here we extract the gene-disease associations. 
        //Together with the drug-disease associations, we can finally have a disease set.
        //The diseast set is the intersection of drug-associated diseases and gene-associated diseases.
        HashMap<String, HashSet<String>> geneDiseaseMap = new HashMap<>();
        HashSet<String> diseasSet = new HashSet<>();
        System.out.println();
        System.out.println();
        System.out.println("Gene disease associations");
        try{
            fr = new FileReader(ctdGeneDiseaseFile);
            br = new BufferedReader(fr);
            String line = null;
            int numLines= reader.getLineNum(ctdGeneDiseaseFile);
            int j=0;
            while((line = br.readLine())!= null){
                j++;
                if(j%100000 == 0)
                    System.out.println(j/(float)numLines*100+"% finished.");
                if(line.isEmpty())
                    continue;
                if(line.startsWith("#"))
                    continue;
                String[] splits = line.split("\t");
                String geneId = String.copyValueOf(splits[0].toCharArray());
                String disease  = String.copyValueOf(splits[2].toCharArray());
                if(!geneList.contains(geneId))
                    continue;
                if(!diseaseSetByDrug.contains(disease))
                    continue;
                diseasSet.add(disease);
                if(!geneDiseaseMap.containsKey(geneId))
                    geneDiseaseMap.put(geneId, new HashSet<>());
                geneDiseaseMap.get(geneId).add(disease);
                
            }
        }catch(IOException e){
            System.err.println("(DiseaseExtractor.extractDisease) File reading error.");
        }
        // Write the diseases and the map.
        DataWriter writer = new DataWriter();
        writer.writeHashMap2(drugDiseaseMap, drugDiseaseAssocOutput);
        writer.writeHashMap2(geneDiseaseMap, geneDiseaseAssocOutput);
        writer.writeHashSet(diseasSet, diseaseOutput, "\n");
    }
    
    
    /**
     * This method removes the diseases whose similarities with others are 0.
     */
    public void removeSim0Diseases(){
        String drugListFile = "../../drug/drug_id.txt";
        String diseaseListFile = "../../disease/disease_id.txt";
        String geneListFile = "../../gene/gene_id.txt";
        String drugDiseaseMatrixFile= "../../drug/drug_disease_matrix.txt";
        String geneDiseaseMatrixFile = "../../gene/gene_disease_matrix.txt";
        String diseaseMatrixFile = "../../disease/disease_matrix.txt";
        String diseaseToRemoveFile = "../../disease/disease_sim0.txt";
        DataReader reader = new DataReader();
        
        ArrayList<String> drugList = reader.readIds(drugListFile);
        ArrayList<String> diseaseList = reader.readIds2(diseaseListFile);
        ArrayList<String> geneList = reader.readIds(geneListFile);
        ArrayList<String> diseaseToRemove = reader.readIds2(diseaseToRemoveFile);
        
        float drugDiseaseMatrix[][] = reader.readMatrix(drugDiseaseMatrixFile, drugList.size(), diseaseList.size());
        float geneDiseaseMatrix[][] = reader.readMatrix(geneDiseaseMatrixFile, geneList.size(), diseaseList.size());
        float diseaseMatrix[][] = reader.readMatrix(diseaseMatrixFile, diseaseList.size(),diseaseList.size());
        
        ArrayList<String> newDiseaseList = new ArrayList<>();
        for(String disease: diseaseList){
            if(diseaseToRemove.contains(disease))
                continue;
            newDiseaseList.add(disease);
        }
        
        float newDiseaseMatrix[][] = new float[newDiseaseList.size()][newDiseaseList.size()];
        for(int i=0;i<newDiseaseList.size();i++)
            for(int j=0;j<newDiseaseList.size();j++)
                newDiseaseMatrix[i][j] = Float.NaN;
        
        for(int i=0;i<newDiseaseList.size();i++)
            for(int j=0;j<newDiseaseList.size();j++){
                if(i ==j)
                    continue;
                int oldIdxi = diseaseList.indexOf(newDiseaseList.get(i));
                int oldIdxj = diseaseList.indexOf(newDiseaseList.get(j));
                newDiseaseMatrix[i][j] = diseaseMatrix[oldIdxi][oldIdxj];
            }
        
        float newDrugDiseaseMatrix[][] =new float[drugList.size()][newDiseaseList.size()];
        for(int i=0;i<drugList.size();i++)
            for(int j=0;j<newDiseaseList.size();j++){
                int oldIdx= diseaseList.indexOf(newDiseaseList.get(j));
                newDrugDiseaseMatrix[i][j] = drugDiseaseMatrix[i][oldIdx];
            }
        
        
        float newGeneDiseaseMatrix[][] = new float[geneList.size()][newDiseaseList.size()];
        for(int i=0;i<geneList.size();i++)
            for(int j=0;j<newDiseaseList.size();j++){
                int oldIdx = diseaseList.indexOf(newDiseaseList.get(i));
                newGeneDiseaseMatrix[i][j] = geneDiseaseMatrix[i][oldIdx];
            }
         
        DataWriter writer = new DataWriter();
        writer.writeIds(newDiseaseList, "../../disease/disease_id_2.txt", "\n");
        writer.writeMatrix(newDiseaseMatrix, "../../disease/disease_matrix_2.txt");
        writer.writeMatrix(newDrugDiseaseMatrix, "../../drug/drug_disease_matrix_2.txt");
        writer.writeMatrix(newGeneDiseaseMatrix,"../../gene/gene_disease_matrix_2.txt");
    }
   
    public void runExtractDisease(){
        String casFile = "../../drug/cas_number.txt";
        String drugFile = "../../drug/drug_id.txt";
        String drugDiseaseCtdFile = "../../ctd/CTD_chemicals_diseases.tsv";
        String drugDiseaseAssoc = "../../drug/drug_disease_assoc.txt";
        String geneDiseaseCtdFile = "../../ctd/CTD_genes_diseases.tsv";
        String geneFile = "../../gene/gene_id.txt";
        String geneDiseaseAssocOutput = "../../disease/gene_disease_assoc.txt";
        String diseaseOutput = "../../disease/disease_id.txt";
        extractDisease(casFile,drugFile,drugDiseaseCtdFile,geneFile,geneDiseaseCtdFile,diseaseOutput,
                drugDiseaseAssoc,geneDiseaseAssocOutput);
    }
    
    
    public void runExtractUmlsCui(){
        String input = "../../disease/mesh_umls_2015AA_mapped.txt";
        String output = "../../disease/cui.txt";
        extractUmlsCui(input,output);
    }
    
    
    public static void main(String args[]){
        //new DiseaseExtractor().runDrugDiseaseMatrixExtractor(1, 0);
        //new DiseaseExtractor().runExtractDrugDiseaseAssociations();
        //new DiseaseExtractor().runExtractDisease();
        //new DiseaseExtractor().runExtractDiseaseByGene();
        //new DiseaseExtractor().runExtractDiseaseMatrix();
        
       // new DiseaseExtractor().runExtractNegativeDrugDiseaseMatrix();
        //new DiseaseExtractor().checkDiseaseMatrix();
        //new DiseaseExtractor().check0SimDiseases();
        //new DiseaseExtractor().removeSim0Diseases();
        //new DiseaseExtractor().checkSim0Connection();
        //new DiseaseExtractor().runExtractUmlsCui();
    }
    
}
