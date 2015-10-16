/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.extractor;

import data.io.DataReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author penpen926
 */
public class EntityExtractor {
    /**
     * 
     * @param drugIdFile
     * @param geneIdFile
     * @param diseaseIdFile
     * @param outputFile 
     */
    public void extractInputEntity(String drugIdFile, 
            String geneIdFile, 
            String diseaseIdFile,
            String outputFile){
        /* Read the ids. */
        DataReader reader = new DataReader();
        ArrayList<String> drugList = reader.readIds(drugIdFile);
        ArrayList<String> geneList = reader.readIds2(geneIdFile);        
        ArrayList<String> diseaseList = reader.readIds2(diseaseIdFile);
        
        try{
            FileWriter fw = new FileWriter(outputFile);
            BufferedWriter bw = new BufferedWriter(fw);
            /* Output the drug list. */
            for(int i=0;i<drugList.size()-1;i++)
                bw.write(drugList.get(i)+"\t");
            bw.write(drugList.get(drugList.size()-1)+"\n");
            /* Output the gene list. */
            for(int i=0;i<geneList.size()-1;i++)
                bw.write(geneList.get(i)+"\t");
            bw.write(geneList.get(geneList.size()-1)+"\n");
            /* Output the disease list. */
            for(int i=0;i<diseaseList.size()-1;i++)
                bw.write(diseaseList.get(i)+"\t");
            bw.write(diseaseList.get(diseaseList.size()-1)+"\n");
            bw.flush();
            bw.close();
            fw.close();
        }catch(IOException e){
            System.err.println("(InfoExtractor.extractInputEntity) File writing error.");
            return;
        }
    }
    
    /**
     * 
     */
    public void runExtractEntity(){
        String drugFile = "../../drug/drug_id.txt";
        String geneFile = "../../gene/gene_id.txt";
        String diseaseFile = "../../disease/disease_id.txt";
        String outputFile = "../../nforce_input/repos2/input_entity.txt";
        extractInputEntity(drugFile,geneFile,diseaseFile,outputFile);
    }
    
    public static void main(String[] args){
        new EntityExtractor().runExtractEntity();
    }
}
