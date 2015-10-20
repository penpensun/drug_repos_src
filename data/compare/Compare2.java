/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.compare;
import java.io.*;
import data.io.*;
import data.processor.IndigoSim;
import java.util.*;
import data.init.InitDrugReposConfig;
import data.extractor.DrugExtractor;
import data.extractor.GeneExtractor;
import org.jdom2.Element;

/**
 *
 * @author penpen926
 */
public class Compare2 {
    /**
     * This method computes the overlap.
     * @param compare2Disease
     * @param cui 
     * @param output 
     */
    public void extractDiseaseNonoverlap(String compare2Disease, 
            String cui,
            String output){
        FileReader fr = null;
        BufferedReader br = null;
        FileWriter fw = null;
        BufferedWriter bw = null;
        ArrayList<String> cuiList = new DataReader().readIds(cui);
        HashSet<String> containedDiseaseName = new HashSet<>();
        HashSet<String> uncontainedDiseaseName = new HashSet<>();
        try{
            fr = new FileReader(compare2Disease);
            br = new BufferedReader(fr);
            fw = new FileWriter(output);
            bw = new BufferedWriter(fw);
            String line = null;
            while((line = br.readLine())!= null){
                String[] splits = line.split("\t");
                String cuiId =splits[2];
                if(cuiList.contains(cuiId)){
                    containedDiseaseName.add(splits[1]);
                    if(splits[1].contains("Colorectal Cancer"))
                        System.out.println("contained  "+line);
                }
            }
            
            fr.close();
            br.close();
            fr = new FileReader(compare2Disease);
            br = new BufferedReader(fr);
            while((line = br.readLine())!= null){
                String[] splits = line.split("\t");
                String cuiId = splits[2];
                if(!cuiList.contains(cuiId) && 
                        !containedDiseaseName.contains(splits[1])){
                    uncontainedDiseaseName.add(splits[1]);
                    if(splits[1].contains("Colorectal Cancer"))
                        System.out.println("uncontained  "+line);
                }
            }
            new DataWriter().writeHashSet(uncontainedDiseaseName, output, "\n");
            fr.close();
            br.close();
        }catch(IOException e){
            System.err.println("Error");
            e.printStackTrace();
        }
    }
    
    
    public void extractDrugNonoverlap(String compare2Gsp, String drugDrugNames,
            String output){
        HashMap<String, String> drugNameDrugMap = new DataReader().readMap2(drugDrugNames);
        FileReader fr = null;
        BufferedReader br = null;
        try{
            fr = new FileReader(compare2Gsp);
            br = new BufferedReader(fr);
            String line =null;
            HashSet<String> uncontained = new HashSet<>();
            while((line = br.readLine())!=  null){
                String[] splits = line.split("\t");
                String drug = splits[0];
                if(true)
                    uncontained.add(drug);
            }
            new DataWriter().writeHashSet(uncontained, output, "\n");
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    
    /**
     * This method extracts the mapping from compare2 disease to our dataset.
     * @param compare2Disease
     * @param diseaseCuiMap
     * @param output 
     */
    public void extractDiseaseMapping(String compare2Disease, String diseaseCuiMap,String output){
        HashMap<String, HashSet<String>> diseaseMap = new HashMap<>();
        DataReader reader =new DataReader();
        HashMap<String, HashSet<String>> cuiDiseaseMap = reader.readMapInReverseOrder4(diseaseCuiMap);
        HashMap<String, HashSet<String>> compare2DiseaseCuiMap = readCompare2DiseaseCuiMap(compare2Disease);
        ArrayList<String> compare2KeySet = new ArrayList<>(compare2DiseaseCuiMap.keySet());
        for(String compare2d: compare2KeySet){
            HashSet<String> compare2Cui = compare2DiseaseCuiMap.get(compare2d);
            if(!diseaseMap.containsKey(compare2d))
                diseaseMap.put(compare2d, new HashSet<>());
            for(String cui: compare2Cui){
                HashSet<String> revDisease = cuiDiseaseMap.get(cui);
                if(revDisease == null)
                    continue;
                diseaseMap.get(compare2d).addAll(revDisease);
            }
        }
        
        new DataWriter().writeHashMap2(diseaseMap, output);
    }
   
    /**
     * This method extracts the gsp mapping.
     * @param diseaseMap
     * @param drugMap
     * @param output 
     */
    public void extractGspMapping(){
        DataReader reader = new DataReader();
        HashMap<String, String>  drugMap = reader.readMap2("../../compare2/assoc/compare2_drug_name_drug_assoc.txt");
        HashMap<String, HashSet<String>> diseaseMap = reader.readMap("../../compare2/assoc/compare2_disease_mapping.txt");
        HashMap<String, HashSet<String>> ans = new HashMap<>();
        FileReader fr = null;
        BufferedReader br= null;
        try{
            fr = new FileReader("../../compare2/compare2_gsp.txt");
            br = new BufferedReader(fr);
            
            String line= null;
            while((line =br.readLine())!= null){
                String[] splits = line.split("\t");
                String drug = splits[0].trim();
                String disease = splits[1].trim();
                String mappedDrug = drugMap.get(drug);
                HashSet<String> mappedDisease = diseaseMap.get(disease);
                if(!ans.containsKey(mappedDrug))
                    ans.put(mappedDrug,new HashSet<>());
                ans.get(mappedDrug).addAll(mappedDisease);
            }
            br.close();
            fr.close();
        }catch(IOException e){
            e.printStackTrace();
        }
        
        new DataWriter().writeHashMap2(ans, "../../compare2/compare2_mapped_gsp.txt");
    }
    /**
     * This method reads the disease-cui map from the gsp of compare2.
     * @param compare2Disease
     * @return 
     */
    private HashMap<String, HashSet<String>> readCompare2DiseaseCuiMap(String compare2Disease){
        FileReader fr = null;
        BufferedReader br = null;
        HashMap<String, HashSet<String>> ans = new HashMap<>();
        try{
            fr = new FileReader(compare2Disease);
            br = new BufferedReader(fr);
            String line =null;
            while((line = br.readLine())!= null){
                String[] splits = line.split("\t");
                String diseaseName = String.copyValueOf(splits[1].toCharArray());
                String cui = String.copyValueOf(splits[2].toCharArray());
                if(!ans.containsKey(diseaseName))
                    ans.put(diseaseName, new HashSet<>());
                ans.get(diseaseName).add(cui);
            }
            return ans;
        }catch(IOException e){
            e.printStackTrace();
        }
        return null;
    }
    
    private void computeDrugMatrix(){
        DrugReposConfig conf = new DrugReposConfig();
        new InitDrugReposConfig().initCompare2(conf);
        new data.extractor.MatrixExtractor().extractDrugMatrix(conf);
    }
    
    private void computeDiseaseMatrix(){
        DrugReposConfig conf = new DrugReposConfig();
        new data.init.InitDrugReposConfig().initCompare2(conf);
        String diseaseMatrix = "../../matrix/disease_matrix.txt";
        String oldDiseaseId = "../../id/disease_id.txt";
        String compare2DiseaseId = "../../compare2/id/compare2_disease_id.txt";
        String compare2DiseaseSim = "../../compare2/matrix/compare2_disease_sim.txt";
        DataReader reader = new DataReader();
        ArrayList<String> oldDiseaseList = reader.readIds2(oldDiseaseId);
        ArrayList<String> compare2DiseaseList = reader.readIds2(compare2DiseaseId);
        float[][] oldDiseaseMatrix = reader.readMatrix(diseaseMatrix,oldDiseaseList.size(), oldDiseaseList.size());
        float[][] matrix = new float[compare2DiseaseList.size()][compare2DiseaseList.size()];
        for(int i=0;i<matrix.length;i++)
            for(int j=0;j<matrix.length;j++)
                matrix[i][j] = Float.NaN;
        FileReader fr = null;
        BufferedReader br = null;
        try{
            fr = new FileReader(compare2DiseaseSim);
            br = new BufferedReader(fr);
            String line= null;
            while((line = br.readLine())!= null){
                String[] splits = line.split("\t");
                String disease1 = splits[1];
                String disease2 = splits[3];
                float sim = Float.parseFloat(splits[4]);
                int idx1 = compare2DiseaseList.indexOf(disease1);
                int idx2 = compare2DiseaseList.indexOf(disease2);
                if(idx1 == -1)
                    System.err.println(disease1);
                if(idx2 == -1)
                    System.err.println(disease2);
                matrix[idx1][idx2] = sim;
                matrix[idx2][idx1] = sim;
            }
        }catch(IOException e){
            System.err.println("Compare2 read error.");
        }
        // Extract the old disease matrix;
        for(int i=0;i<oldDiseaseList.size();i++)
            for(int j=0;j<oldDiseaseList.size();j++){
                String disease1 = oldDiseaseList.get(i);
                String disease2 = oldDiseaseList.get(j);
                int idx1 = compare2DiseaseList.indexOf(disease1);
                int idx2 = compare2DiseaseList.indexOf(disease2);
                matrix[idx1][idx2] = oldDiseaseMatrix[i][j];
                matrix[idx2][idx1] = oldDiseaseMatrix[j][i];
            }
        
        for(int i=0;i<matrix.length;i++)
            matrix[i][i] = Float.NaN;
        
        new DataWriter().writeMatrix(matrix, conf.disease_matrix);
    }
    
    private void computeDrugDiseaseMatrix(){
        DrugReposConfig conf = new DrugReposConfig();
        new data.init.InitDrugReposConfig().initCompare2(conf);
        DataReader reader = new DataReader();
        HashMap<String, HashSet<String>> drugDiseaseAssoc = reader.readMap("../../assoc/drug_disease_assoc.txt");
        HashMap<String, String> compare2DrugMap = reader.readMap2("../../compare2/id/compare2_drug_name_drug_assoc.txt");
        HashMap<String, HashSet<String>> compare2DiseaseMap = reader.readMap("../../compare2/id/compare2_disease_map.txt");
        HashMap<String, HashSet<String>> gsp = new HashMap<>();
        FileReader fr = null;
        
        BufferedReader br= null;
        try{
            fr = new FileReader("../../compare2/compare2_gsp.txt");
            br = new BufferedReader(fr);
            String line= null;
            while((line = br.readLine())!= null){
                String[] splits = line.split("\t");
                String drug = compare2DrugMap.get(splits[0].trim());
                if(drug == null)
                    System.out.println(line);
                HashSet<String> diseaseSet = compare2DiseaseMap.get(splits[1].trim());
                
                if(!gsp.containsKey(drug))
                    gsp.put(drug,new HashSet<>());
                gsp.get(drug).addAll(diseaseSet);
                
                if(!drugDiseaseAssoc.containsKey(drug))
                    drugDiseaseAssoc.put(drug, new HashSet<>());
                drugDiseaseAssoc.get(drug).addAll(diseaseSet);
            }
        }catch(IOException e){
            System.err.println("compare2 gsp errr.");
        }
        new DataWriter().writeHashMap2(drugDiseaseAssoc, conf.drug_disease_assoc);
        
        ArrayList<String> drugs = reader.readIds(conf.drug_id);
        ArrayList<String> diseases = reader.readIds(conf.disease_id);
        float[][] drugDiseaseMatrix = new float[drugs.size()][diseases.size()];
        for(int i=0;i<drugs.size();i++)
            for(int j=0;j<diseases.size();j++){
                drugDiseaseMatrix[i][j] = 0;
            }
        ArrayList<String> keySet = new ArrayList<>(drugDiseaseAssoc.keySet());
        for(String k: keySet){
            HashSet<String> values = drugDiseaseAssoc.get(k);
            if(values == null)
                continue;
            int idx1 = drugs.indexOf(k);
            
            for(String v:values){
                int idx2 = diseases.indexOf(v);
                if(idx2 ==-1)
                    continue;
                if(idx1 ==-1)
                    System.out.println(k);
                drugDiseaseMatrix[idx1][idx2] = 1;
            }
        }
                
        new DataWriter().writeMatrix(drugDiseaseMatrix,conf.drug_disease_matrix);
        new DataWriter().writeHashMap2(drugDiseaseAssoc, conf.gsp);
        new DataWriter().writeHashMap2(gsp, conf.gsp+"_s");
    }
    
    
    /**
     * This method extracts the gene-disease matrix for compare2.
     */
    private void computeGeneDiseaseMatrix(){
        String geneFile = "../../id/gene_id.txt";
        DataReader reader = new DataReader();
        ArrayList<String> genes = reader.readIds(geneFile);
        FileReader fr = null;
        BufferedReader br = null;
        
        HashMap<String, HashSet<String>> omimMap = new HashMap<>();
        // Read the OMIM-disease map
        try{
            fr = new FileReader("../../compare2/compare2_disease.txt");
            br = new BufferedReader(fr);
            br.readLine();
            String line = null;
            while((line = br.readLine())!= null){
                String[] splits = line.split("\t");
                String omim = splits[0];
                String disease = splits[1];
                if(!omimMap.containsKey(omim))
                    omimMap.put(omim, new HashSet());
                omimMap.get(omim).add(disease);
            }
            fr.close();
            br.close();
            
        }catch(IOException e){
            System.err.println("ctd error");
        }
        
        HashMap<String, HashSet<String>> assoc = new DataReader().readMap("../../assoc/gene_disease_assoc.txt");
        HashMap<String, HashSet<String>> diseaseMap = new DataReader().readMap("../../compare2/id/compare2_disease_map.txt");
        
        System.out.println(new data.processor.DataCounter().mapCounter(assoc));
        try{
            fr = new FileReader("../../ctd/CTD_genes_diseases.tsv");
            br = new BufferedReader(fr);
            String line =null;
            while((line = br.readLine())!= null){
                if(line.startsWith("#"))
                    continue;
                String[] splits = line.split("\t");
                String omim = splits[7];
                String gene = splits[0];
                if(!genes.contains(gene))
                    continue;
                if(omimMap.keySet().contains(omim)){
                    if(!assoc.containsKey(gene))
                        assoc.put(gene, new HashSet<>());
                    HashSet<String> diseaseSet = omimMap.get(omim);
                    for(String d: diseaseSet)
                        assoc.get(gene).addAll(diseaseMap.get(d));
                }
                    
            }
        }catch(IOException e){
            System.err.println("ctd error");
        }
        System.out.println(new data.processor.DataCounter().mapCounter(assoc));
        
        new DataWriter().writeHashMap2(assoc, "../../compare2/assoc/compare2_gene_disease_assoc.txt");
        
        ArrayList<String> diseaseList = reader.readIds2("../../compare2/id/compare2_disease_id.txt");
        System.out.println(new data.processor.DataCounter().mapCounter(assoc));
        
        float[][] matrix = new float[genes.size()][diseaseList.size()];
        for(int i=0;i<matrix.length;i++)
            for(int j=0;j<matrix[0].length;j++)
                matrix[i][j] = 0;
        
        for(String g: genes){
            HashSet<String> dSet = assoc.get(g);
            if(dSet == null || dSet.isEmpty())
                continue;
            int idx1 = genes.indexOf(g);
            for(String d: dSet){
  
                int idx2 = diseaseList.indexOf(d);
                if(idx2 == -1)
                    System.out.println(d);
                matrix[idx1][idx2] = 1;
            }
        }
        new DataWriter().writeMatrix(matrix, "../../compare2/matrix/compare2_gene_disease_matrix.txt");
    }
    
    /**
     * This method extracts the drug-gene matrix for compare2.
     */
    private void computeDrugGeneMatrix(){
        DrugExtractor drugEx = new DrugExtractor();
        DrugReposConfig conf = new DrugReposConfig();
        
        new data.init.InitDrugReposConfig().initCompare2(conf);
        drugEx.extractDrugCasNumberRelation(conf.drug_id, conf.drug_xml, conf.cas_id);
        ArrayList<String> geneList = new DataReader().readIds(conf.gene_id);
        HashMap<String, String> casMap = 
                new DataReader().readMapInReverseOrder2(conf.cas_id);
        extractGene(conf.drug_id, conf.drug_xml, 
                conf.gene_id, conf.drug_gene_assoc);
        HashMap<String, HashSet<String>> drugGeneAssoc = new DataReader().readMap(conf.drug_gene_assoc);
        ArrayList<String> drugList = new DataReader().readIds(conf.drug_id);
        //Create the matrix
        float[][] drugGeneMatrix = new float[drugList.size()][geneList.size()];
        for(int i=0;i<drugList.size();i++){
            String drug = drugList.get(i);
            HashSet<String> genes = drugGeneAssoc.get(drug);
            if(genes == null || genes.isEmpty())
                continue;
            for(String g: genes){
                if(geneList.indexOf(g) == -1)
                    continue;
                drugGeneMatrix[i][geneList.indexOf(g)] = 1;
            }
        }
        new DataWriter().writeMatrix(drugGeneMatrix, conf.drug_gene_matrix);
    }
    
    
    public void extractGene(String drugIdFile, String drugbankXml ,String geneListFile, 
            String associationOutput){
        /* Read the drug list. */
        DrugExtractor drugEx = new DrugExtractor();
        DataReader reader = new DataReader();
        GeneExtractor geneEx = new GeneExtractor();
        ArrayList<String> drugList = reader.readIds(drugIdFile);
        List<Element> drugElementList = drugEx.extractDrugList(drugbankXml);
        ArrayList<String> geneList = reader.readIds(geneListFile);
        
        HashMap<String, ArrayList<String>> drugGenenameMap = new HashMap<>();
        int i=0;
        for(Element drugElement: drugElementList){
            i++;
            if(i%5000==0)
                System.out.println(i/(float)drugElementList.size()*100+"% is finished.");
            /* Extract all gene names. */
            String drugId = drugEx.extractPrimaryId(drugElement);
            if(!drugList.contains(drugId))
                continue;
            
            ArrayList<Element> polypeptideElementList = geneEx.extractPolypepElementInDrug(drugElement);
            if(polypeptideElementList == null || polypeptideElementList.isEmpty())
                continue;
            ArrayList<String> genesInDrug= new ArrayList<>();
            for(Element polypeptide: polypeptideElementList){
                String fasta = geneEx.extractFastaSeq(polypeptide);
                if(fasta == null || fasta.isEmpty())
                    continue;
                else{
                    String gene= geneEx.extractGeneName(polypeptide);
                    if(gene == null || gene.isEmpty())
                        continue;
                    
                    // Remove HIV-1 protease
                    if(gene.equals("HIV-1 protease"))
                        continue;
                    
                    // Change AAC(6')-IY, AAC(6')-II and BCR/ABL Fusion
                    if(gene.equalsIgnoreCase("BCR/ABL FUSION"))
                        gene = "BCR_ABL_FUSION";
                    if(gene.equalsIgnoreCase("aac(6')-Iy"))
                        gene = "AAC6IY";
                    if(gene.equalsIgnoreCase("aac(6')-Ii"))
                        gene = "AAC6II";
                    
                    // Turn the gene name into upper case.
                    gene = gene.toUpperCase();
                    if(geneList.contains(gene))
                        continue;
                    genesInDrug.add(gene);
                }
            }
            drugGenenameMap.put(drugId, genesInDrug);
        }
        /* Output the geneNames set and the hashmap. */
        new DataWriter().writeHashMap(drugGenenameMap, associationOutput);
    }
    
    public void runDiseaseNonoverlap(){
        String cuiId = "../../id/cui.txt";
        String compareDisease = "../../compare2/compare2_disease.txt";
        String nonOverlap = "../../compare2/disease_nonoverlap.txt";
        extractDiseaseNonoverlap(compareDisease, cuiId, nonOverlap);
    }
    
    
    
    public void runDrugNonoverlap(){
        String compare2Gsp = "../../compare2/compare2_gsp.txt";;
        String drugDrugNameAssoc = "../../assoc/drug_drug_names_assoc.txt";
        String output = "../../compare2/drug_nonoverlap.txt";
        extractDrugNonoverlap(compare2Gsp, drugDrugNameAssoc, output);
    }
    
    public void runDiseaseMapping(){
        String comapre2Disease = "../../compare2/compare2_disease.txt";
        String diseaseCuiMap = "../../assoc/disease_cui_assoc.txt";
        String output = "../../compare2/compare2_disease_mapping.txt";
        extractDiseaseMapping(comapre2Disease, diseaseCuiMap, output);
    }
    
    
    public void merge(){
       ArrayList<String> drugs = new DataReader().readIds("../../id/drug_id.txt");
       ArrayList<String> drugs2 = new DataReader().readIds("../../compare2/id/compare2_drug_id.txt");
       HashSet<String> ans = new HashSet<>();
       ans.addAll(drugs);
       ans.addAll(drugs2);
       new DataWriter().writeHashSet(ans, "../../compare2/id/compare2_drug_id.txt", "\n");
    }
    public static void main(String args[]){
        //new Compare2().runDiseaseNonoverlap();
        //new Compare2().runCheckGspOverlap();
        //new Compare2().runDrugNonoverlap();
        //new Compare2().runDiseaseMapping();
        //new Compare2().computeDrugMatrix();
       //new Compare2().computeDiseaseMatrix();
       //new Compare2().computeDrugDiseaseMatrix();
       //new Compare2().computeGeneDiseaseMatrix();
       //new Compare2().computeDrugGeneMatrix();
        new Compare2().extractGspMapping();
      
    }
}
