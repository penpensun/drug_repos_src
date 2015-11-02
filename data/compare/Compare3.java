/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.compare;

import java.io.*;
import data.io.*;
import java.util.*;
import data.init.InitDrugReposConfig;
import data.extractor.DrugExtractor;
import data.extractor.GeneExtractor;
import org.jdom2.Element;
/**
 *
 * @author penpen926
 */
public class Compare3 {
    public void print(){
        try{
            FileReader fr = new FileReader("../../compare3/id/compare3_disease_id_compute.txt");
            BufferedReader br = new BufferedReader(fr);
            FileWriter fw = new FileWriter("../../compare3/id/compare3_disease_id_sim.txt");
            BufferedWriter bw = new BufferedWriter(fw);
            String line = null;
            while((line = br.readLine())!= null){
                bw.write(line+"\n");
                
            }
            
            bw.flush();
            bw.close();
            fw.close();
            br.close();
            fr.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    public void extractRawDisease(){
        String rawGsp = "../../compare3/raw_disease.txt";
        FileReader fr = null;
        BufferedReader br = null;
        ArrayList<String> ans = new ArrayList<>();
        try{
            fr = new FileReader(rawGsp);
            br = new BufferedReader(fr);
            String line = null;
            while((line = br.readLine())!= null){
                String dis = line.split("\t")[1];
                if(!ans.contains(dis))
                    ans.add(dis);
            }
        }catch(IOException e){
            e.printStackTrace();
        }
        new DataWriter().writeIds(ans, "../../compare3/compare3_raw_disease_id.txt", "\n");
    }
    
    public void extractGene(){
        GeneExtractor geneEx = new GeneExtractor();
        geneEx.extractGene("../../compare3/id/compare3_drug_id.txt", 
                "../../drugbank/drugbank.xml", 
                "../../compare3/id/compare3_gene_id.txt", 
                "../../compare3/assoc/drug_gene_assoc.txt");
    }
    
    public void extractGeneMatrix(){
        FileReader fr = null;
        BufferedReader br = null;
        ArrayList<String> geneList = new DataReader()
                .readIds("../../compare3/id/compare3_gene_id.txt");
        HashMap<String, HashSet<String>> map = new HashMap<>();
        float[][] matrix = new float[geneList.size()][geneList.size()];
        for(int i=0;i<matrix.length;i++)
            for(int j=0;j<matrix.length;j++)
                if(i == j)
                    matrix[i][j] = Float.NaN;
                else matrix[i][j] = 0;
        try{
            fr = new FileReader("../../compare3/matrix/compare3_gene_sim.txt");
            br = new BufferedReader(fr);
            String line= null;
            while((line = br.readLine())!= null){
                String[] splits = line.split("\t");
                String gene1 = splits[0];
                String gene2 = splits[1];
                float sim = Float.parseFloat(splits[2]);
                int idx1 = geneList.indexOf(gene1);
                int idx2 = geneList.indexOf(gene2);
                matrix[idx1][idx2] = sim;
                matrix[idx2][idx1] = sim;
            }
            
        }catch(IOException e){
            e.printStackTrace();
        }
        new DataWriter().writeMatrix(matrix, "../../compare3/matrix/compare3_gene_matrix.txt");
    }
    public void extractGeneDiseaseMatrix(){
        DataReader reader = new DataReader();
        ArrayList<String> genes = reader.readIds("../../compare3/id/compare3_gene_id.txt");
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
        
        HashMap<String, HashSet<String>> assoc = new HashMap<>();
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
                if(omimMap.keySet().contains(omim)){
                    HashSet<String> diseaseSet = omimMap.get(omim);
                    HashSet<String> dSet2 = new HashSet<>();
                    for(String d: diseaseSet)
                        dSet2.add(d);
                    if(!assoc.containsKey(gene))
                        assoc.put(gene, new HashSet<>());
                    assoc.get(gene).addAll(dSet2);
                }
                    
            }
        }catch(IOException e){
            System.err.println("ctd error");
        }
        System.out.println(new data.processor.DataCounter().mapCounter(assoc));
        
        new DataWriter().writeHashMap2(assoc, "../../compare3/assoc/compare3_gene_disease_assoc.txt");
        
        ArrayList<String> diseaseList = reader.readIds2("../../compare3/id/compare3_disease_id.txt");
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
        new DataWriter().writeMatrix(matrix, "../../compare3/matrix/compare3_gene_disease_matrix.txt");
    } 
    
    public void extractDrugDiseaseMatrix(){
        DataReader reader = new DataReader();
        ArrayList<String> drugs = reader.readIds("../../compare3/id/compare3_drug_id.txt");
        ArrayList<String> diseases = reader.readIds("../../compare3/id/compare3_disease_id.txt");
        float[][] drugDiseaseMatrix = new float[drugs.size()][diseases.size()];
        for(int i=0;i<drugs.size();i++)
            for(int j=0;j<diseases.size();j++){
                drugDiseaseMatrix[i][j] = 0;
            }
        
        HashMap<String, HashSet<String>> drugDiseaseAssoc = 
                reader.readMap("../../compare3/compare3_gsp.txt");
        System.out.println(new data.processor.DataCounter().mapCounter(drugDiseaseAssoc));
        ArrayList<String> keySet = new ArrayList<>(drugDiseaseAssoc.keySet());
        for(String k: keySet){
            HashSet<String> values = drugDiseaseAssoc.get(k);
            if(values == null)
                continue;
            int idx1 = drugs.indexOf(k);
            
            for(String v:values){
                int idx2 = diseases.indexOf(v);
                if(idx1 ==-1)
                    System.out.println(k);
                drugDiseaseMatrix[idx1][idx2] = 1;
            }
        }
        
        new DataWriter().
                writeMatrix(drugDiseaseMatrix, "../../compare3/matrix/compare3_drug_disease_matrix.txt");
    }
    
    
    public void extractDiseaseMatrix(){
        FileReader fr = null;
        BufferedReader br = null;
        ArrayList<String> diseaseList = new DataReader().readIds("../../compare3/id/compare3_disease_id_sim.txt");
        HashMap<String, HashSet<String>> map = new HashMap<>();
        float[][] matrix = new float[diseaseList.size()][diseaseList.size()];
        for(int i=0;i<matrix.length;i++)
            for(int j=0;j<matrix.length;j++)
                if(i==j)
                    matrix[i][j] = Float.NaN;
                else matrix[i][j] = 0;
        try{
            fr = new FileReader("../../compare3/matrix/compare3_disease_sim.txt");
            br = new BufferedReader(fr);
            String line= null;
            while((line = br.readLine())!= null){
                String[] splits = line.split("\t");
                String disease1 = splits[1];
                String disease2 = splits[3];
                float sim = Float.parseFloat(splits[4]);
                int idx1 = diseaseList.indexOf(disease1);
                int idx2 = diseaseList.indexOf(disease2);
                matrix[idx1][idx2] = sim;
                matrix[idx2][idx1] = sim;
            }
            
        }catch(IOException e){
            e.printStackTrace();
        }
        new DataWriter().writeMatrix(matrix, "../../compare3/matrix/compare3_disease_matrix.txt");
    }
    
    public void extractMappedGsp(){
        DataReader reader = new DataReader();
        HashMap<String, String>  drugMap = reader.readMap2("../../compare3/assoc/compare3_drug_name_drug_assoc.txt");
        HashMap<String, HashSet<String>> ans = new HashMap<>();
        FileReader fr = null;
        BufferedReader br= null;
        try{
            fr = new FileReader("../../compare3/raw_gsp.txt");
            br = new BufferedReader(fr);
            
            String line= null;
            while((line =br.readLine())!= null){
                String[] splits = line.split("\t");
                String drug = splits[0].trim();
                String disease = splits[1].trim();
                String mappedDrug = drugMap.get(drug);
                if(!ans.containsKey(mappedDrug))
                    ans.put(mappedDrug,new HashSet<>());
                ans.get(mappedDrug).add(disease);
            }
            br.close();
            fr.close();
        }catch(IOException e){
            e.printStackTrace();
        }
        new DataWriter().writeHashMap2(ans, "../../compare3/compare3_gsp.txt");
        new DataWriter().writeHashMap2(ans, "../../compare3/assoc/compare3_drug_disease_assoc.txt");
    }
    
    public void extractDrugGeneMatrix(){
        DataReader reader = new DataReader();
        HashMap<String, HashSet<String>> map = 
                reader.readMap("../../compare3/assoc/compare3_drug_gene_assoc.txt");
        ArrayList<String> drugList = reader.readIds("../../compare3/id/compare3_drug_id.txt");
        ArrayList<String> geneList = reader.readIds("../../compare3/id/compare3_gene_id.txt");
        
        float[][] matrix = new float[drugList.size()][geneList.size()];
        for(int i =0;i<matrix.length;i++)
            for(int j=0;j<matrix[0].length;j++)
                matrix[i][j] = 0;
        
        for(int i=0;i<drugList.size();i++){
            HashSet<String> genes = map.get(drugList.get(i));
            if(genes== null || genes.isEmpty())
                continue;
            for(String g:genes){
                int idx = geneList.indexOf(g);
                matrix[i][idx] = 1;
            }
        }
        new DataWriter()
                .writeMatrix(matrix,"../../compare3/matrix/compare3_drug_gene_matrix.txt");
    }
    public void extractFastaSeq(){
        String geneFile = "../../compare3/id/compare3_gene_id.txt";
        String outputFolder = "../../compare3/seq/";
        String bankXml = "../../drugbank/drugbank.xml";
        GeneExtractor geneEx = new GeneExtractor();
        geneEx.extractGeneSeq(geneFile, bankXml,outputFolder);
    }
    
    
    
    
    public void extractDrugMatrix(){
        DrugReposConfig conf = new DrugReposConfig();
        new data.init.InitDrugReposConfig().initCompare3(conf);
        DrugExtractor drugEx = new DrugExtractor();
        drugEx.extractSmilesById(conf, "../../compare3/id/compare3_drug_id.txt", 
                conf.drug_smiles);
        
        new InitDrugReposConfig().initCompare3(conf);
        new data.extractor.MatrixExtractor().extractDrugMatrix(conf);
    }
    
    public static void main(String args[]){
        //new Compare3().changeId();
        //new Compare3().extractGene();
        //new Compare3().extractGeneMatrix();
        //new Compare3().extractFastaSeq();
        //new Compare3().extractDrugMatrix();
        //new Compare3().extractGeneDiseaseMatrix();
        //new Compare3().extractDrugDiseaseMatrix();
        //new Compare3().extractMappedGsp();
        //new Compare3().extractDrugGeneMatrix();
        //new Compare3().extractDiseaseMatrix();
        new Compare3().extractRawDisease();
        //new Compare3().extractGeneMatrix();
        //new Compare3().print();
    }
}
