/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.extractor;

import data.io.DataReader;
import data.io.DataWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author penpen926
 */
public class MatrixExtractor {

    /**
     * This method extracts the gene_name -- disease matrix from the gene_name --- disease
     * association file.
     * @param geneNameFile
     * @param diseaseFile
     * @param geneDiseaseAssoc
     * @param outputMatrix
     * @param posEw
     * @param negEw
     */
    public void extractGeneDiseaseMatrix(String geneNameFile, String diseaseFile, String geneDiseaseAssoc, String outputMatrix, float posEw, float negEw) {
        DataReader reader = new DataReader();
        ArrayList<String> geneNameList = reader.readIds(geneNameFile);
        ArrayList<String> diseaseList = reader.readIds2(diseaseFile);
        float[][] matrix = new float[geneNameList.size()][diseaseList.size()];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                matrix[i][j] = negEw;
            }
        }
        HashMap<String, HashSet<String>> map = reader.readMap(geneDiseaseAssoc);
        Set<Map.Entry<String, HashSet<String>>> mapEntrySet = map.entrySet();
        Iterator<Map.Entry<String, HashSet<String>>> mapEntryIter = mapEntrySet.iterator();
        while (mapEntryIter.hasNext()) {
            Map.Entry<String, HashSet<String>> mapEntry = mapEntryIter.next();
            String geneName = mapEntry.getKey();
            if (!geneNameList.contains(geneName)) {
                continue;
            }
            HashSet<String> diseaseSet = mapEntry.getValue();
            if (diseaseSet == null || diseaseSet.isEmpty()) {
                continue;
            }
            Iterator<String> diseaseIter = diseaseSet.iterator();
            while (diseaseIter.hasNext()) {
                int geneNameIdx = geneNameList.indexOf(geneName);
                String diseaseName = diseaseIter.next();
                int diseaseIdx = diseaseList.indexOf(diseaseName);
                if (geneNameIdx == -1) {
                    System.err.println("(GeneNameExtractor.extractGeneNameDiseaseMatrix) Gene name idx -1:  " + geneName);
                }
                if (diseaseIdx == -1) {
                    System.err.println("(GeneNameExtractor.extractGeneNameDiseaseMatrix) Disease name idx -1:  " + diseaseName);
                }
                matrix[geneNameIdx][diseaseIdx] = posEw;
            }
        }
        //Output the matrix
        new DataWriter().writeMatrix(matrix, outputMatrix);
    }

    /**
     * This method extracts the drug-disease matrix from the ctd chemical-disease file.
     * @param drugListFile
     * @param diseaseListFile
     * @param drugDiseaseAssociation
     * @param posEw
     * @param negEw
     * @param outputFile
     */
    public void extractDrugDiseaseMatrix(String drugListFile, String diseaseListFile, String drugDiseaseAssociation, String outputFile, float posEw, float negEw) {
        DataReader reader = new DataReader();
        ArrayList<String> drugList = reader.readIds(drugListFile);
        ArrayList<String> diseaseList = reader.readIds2(diseaseListFile);
        HashMap<String, HashSet<String>> drugDiseaseMap = reader.readMap(drugDiseaseAssociation);
        float[][] matrix = new float[drugList.size()][diseaseList.size()];
        //Init the matrix
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                matrix[i][j] = negEw;
            }
        }
        Set<Map.Entry<String, HashSet<String>>> entrySet = drugDiseaseMap.entrySet();
        Iterator<Map.Entry<String, HashSet<String>>> entrySetIter = entrySet.iterator();
        while (entrySetIter.hasNext()) {
            Map.Entry<String, HashSet<String>> entry = entrySetIter.next();
            String drug = entry.getKey();
            int drugIdx = drugList.indexOf(drug);
            HashSet<String> diseaseSet = entry.getValue();
            Iterator<String> diseaseIter = diseaseSet.iterator();
            while (diseaseIter.hasNext()) {
                String disease = diseaseIter.next();
                int diseaseIdx = diseaseList.indexOf(disease);
                if (diseaseIdx == -1) {
                    continue;
                }
                matrix[drugIdx][diseaseIdx] = posEw;
            }
        }
        new DataWriter().writeMatrix(matrix, outputFile);
    }

    /**
     * This method extracts the disease-disease similarity from the version 5 disease similarities.
     * Pre-cond: Ver 5. Disease similarities.
     * Post-cond: New version disease similarities.
     * @param disease5File
     * @param diseaseFile
     * @param disease5Matrix
     * @param matrixOut
     */
    public void extractDiseaseMatrix(String disease5File, String diseaseFile, String disease5Matrix, String matrixOut) {
        DataReader reader = new DataReader();
        ArrayList<String> diseaseList = reader.readIds2(diseaseFile);
        ArrayList<String> diseaseList5 = reader.readIds2(disease5File);
        float[][] matrix5 = reader.readMatrix(disease5Matrix, diseaseList5.size(), diseaseList5.size());
        float[][] matrix = new float[diseaseList.size()][diseaseList.size()];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                String disease1 = diseaseList.get(i);
                String disease2 = diseaseList.get(j);
                int idx1 = diseaseList5.indexOf(disease1);
                int idx2 = diseaseList5.indexOf(disease2);
                if (idx1 == -1) {
                    System.err.println("disease cannot be found:  " + disease1);
                    continue;
                }
                if (idx2 == -1) {
                    System.err.println("disease cannot be found:  " + disease2);
                    continue;
                }
                matrix[i][j] = matrix5[idx1][idx2];
                matrix[j][i] = matrix5[idx1][idx2];
            }
        }
        // Write the matrix
        new DataWriter().writeMatrix(matrix, matrixOut);
    }
    
    
    /**
     * This method extracts the drug-disease associations.
     */
    public void runExtractDrugDiseaseMatrix(float posEw, float negEw){
        String drugListFile = "../../drug/drug_id.txt";
        String diseaseListFile = "../../disease/disease_id.txt";
        String drugDiseaseAssociation = "../../assoc/drug_disease_assoc.txt";
        String outputFile = "../../matrix/drug_disease_matrix.txt";
        extractDrugDiseaseMatrix(drugListFile, diseaseListFile, drugDiseaseAssociation, outputFile,posEw,negEw);
        
    }
    
    public void runExtractDiseaseMatrix(){
        
        String disease5File = "../../disease/disease_5.txt";
        String diseaseFile = "../../disease/disease_id.txt";
        String matrix5 = "../../disease/disease_disease_matrix_5.txt";
        String matrixOut = "../../disease/disease_matrix.txt";
        extractDiseaseMatrix(disease5File,diseaseFile, matrix5, matrixOut);
    }
    
    
    public void runExtractNegativeDrugDiseaseMatrix(){
        String drugListFile = "../../drug/drug_id.txt";
        String diseaseListFile = "../../disease/disease_id.txt";
        String drugDiseaseAssociation = "../../disease/disease_negative_set_0.2.txt";
        String outputFile = "../../drug/drug_disease_negative_matrix_2.txt";
        extractDrugDiseaseMatrix(drugListFile, diseaseListFile, drugDiseaseAssociation, outputFile,1,0);
    }
    
    public static void main(String args[]){
        new MatrixExtractor().runExtractDrugDiseaseMatrix(1, 0);
    }
    
}
