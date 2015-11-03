/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.extractor;
import data.io.DataReader;
import data.io.DataWriter;
import java.util.*;
/**
 *
 * @author penpen926
 */
public class IncompDataExtractor {
    public void extractIncompMatrix(String matrixFile,
            int row, int col, float percent, String output){
        DataReader reader = new DataReader();
        float[][] matrix = reader.readMatrix(matrixFile, row, col);
        ArrayList<Pair> pairs = new ArrayList<>();
        for(int i=0;i<row;i++)
            for(int j=0;j<col;j++)
                pairs.add(new Pair(i,j));
        ArrayList<Pair> removePair = new ArrayList<>();
        //Random extract the pair
        for(int i=0;i<row*col*percent;i++){
            int randIndex = (int)(pairs.size()*Math.random());
            removePair.add(pairs.remove(randIndex));
        }
        //Remove the element from the matrix
        for(int i=0;i<removePair.size();i++){
            matrix[removePair.get(i).x][removePair.get(i).x] = 0;
        }
        for(int i=0;i<matrix.length;i++)
            for(int j=0;j<matrix[0].length;j++)
                if(i==j)
                    matrix[i][j] = Float.NaN;
        //Output the matrix
        new DataWriter().writeMatrix(matrix, output);
    }
    
    public void extractIncompMatrix2(String matrixFile,
            int row, int col, float percent, String output){
        DataReader reader = new DataReader();
        float[][] matrix = reader.readMatrix(matrixFile, row, col);
        for(int i=0;i<matrix.length;i++)
            for(int j=0;j<matrix[0].length;j++){
                if(Math.random()<percent)
                    matrix[i][j] = 0;
            }
        new DataWriter().writeMatrix(matrix, output);
                
    }
    public void filterHighSimDrug(String drugFile, 
            String drugMatrixFile,
            String drugGeneMatrixFile, 
            String drugDiseaseMatrixFile,
            String drugDiseaseAssocFile,
            String drugMatrixOut,
            String drugGeneMatrixOut,
            String drugDiseaseMatrixOut,
            String drugDiseaseAssocOut,
            String gsnFile,
            String gsnOut){
        DataReader reader = new DataReader();
        ArrayList<String> drugs = reader.readIds(drugFile);
        float[][] drugMatrix = reader.readMatrix(drugMatrixFile, drugs.size(), drugs.size());
        ArrayList<String> reservedDrugs = new ArrayList<>();
        ArrayList<String> filteredDrugs = new ArrayList<>();
        for(int i=0;i<drugs.size();i++){
            boolean flag = true;
            for(int j=0;j<reservedDrugs.size();j++){
                if(drugMatrix[i][drugs.indexOf(reservedDrugs.get(j))] >0.8)
                    flag = false;
            }
            if(flag)
                reservedDrugs.add(drugs.get(i));
            else
                filteredDrugs.add(drugs.get(i));
        }
        System.out.println("filered drugs size: "+filteredDrugs.size());
        // Read the matrices.
        float[][] drugGeneMatrix = reader.readMatrix(drugGeneMatrixFile, drugs.size(), 1622);
        float[][] drugDiseaseMatrix = reader.readMatrix(drugDiseaseMatrixFile, drugs.size(),3407);
        HashMap<String, HashSet<String>> drugDiseaseAssoc = reader.readMap(drugDiseaseAssocFile);
        HashMap<String, HashSet<String>> gsn = reader.readMap(gsnFile);
        
        // Modify the drug matrix.
        for(int i=0;i<filteredDrugs.size();i++){
            for(int j=0;j<drugGeneMatrix[0].length;j++)
                drugGeneMatrix[drugs.indexOf(filteredDrugs.get(i))][j] = Float.NaN;
            for(int j=0;j<drugDiseaseMatrix[0].length;j++)
                drugDiseaseMatrix[drugs.indexOf(filteredDrugs.get(i))][j] = Float.NaN;
            if(drugDiseaseAssoc.containsKey(filteredDrugs.get(i)))
                drugDiseaseAssoc.remove(filteredDrugs.get(i));
            if(gsn.containsKey(filteredDrugs.get(i)))
                gsn.remove(filteredDrugs.get(i));
        }
        
        // Output the matrix, associations
        DataWriter writer = new DataWriter();
        writer.writeMatrix(drugMatrix, drugMatrixOut);
        writer.writeMatrix(drugGeneMatrix, drugGeneMatrixOut);
        writer.writeMatrix(drugDiseaseMatrix, drugDiseaseMatrixOut);
        writer.writeHashMap2(drugDiseaseAssoc, drugDiseaseAssocOut);
        writer.writeHashMap2(gsn, gsnOut);
    }
    
    public void removeIntraMatrix(){
        String drugMatrix = "../../matrix/new_drug_matrix.txt";
        String geneMatrix = "../../matrix/gene_matrix.txt";
        String diseaseMatrix = "../../matrix/disease_matrix.txt";
        String drugOut = "../../incomplete/drug_incomp_matrix.txt";
        String geneOut = "../../incomplete/gene_incomp_matrix.txt";
        String diseaseOut = "../../incomplete/disease_incomp_matrix.txt";
        
        extractIncompMatrix(drugMatrix, 1543, 1543, 0.3f, drugOut);
        extractIncompMatrix(geneMatrix, 1622, 1622, 0.3f, geneOut);
        extractIncompMatrix2(diseaseMatrix, 3407,3407,0.2f,diseaseOut);
    }
    
    public void removeInterMatrix(){
        String drugGeneMatrix  = "../../matrix/drug_gene_matrix.txt";
        String drugDiseaseMatrix = "../../matrix/drug_disease_matrix.txt";
        String geneDiseaseMatrix = "../../matrix/gene_disease_matrix.txt";
        String drugGeneOut = "../../incomplete/drug_gene_incomp_matrix.txt";
        String drugDiseaseOut = "../../incomplete/drug_disease_incomp_matrix.txt";
        String geneDiseaseOut = "../../incomplete/gene_disease_incomp_matrix.txt";
        
        //new IncompDataExtractor().extractIncompMatrix(drugGeneMatrix, 1543, 1543, 0.1f, drugGeneOut);
        extractIncompMatrix(drugDiseaseMatrix, 1622, 1622, 0.1f, drugDiseaseOut);
        //new IncompDataExtractor().extractIncompMatrix(geneDiseaseMatrix, 3407,3407,0.1f,geneDiseaseOut);
    }
    
    public void filterDrug(){
        String drugFile = "../../id/drug_id.txt";
        String drugMatrix = "../../matrix/new_drug_matrix.txt";
        String drugGeneMatrix  = "../../matrix/drug_gene_matrix.txt";
        String drugDiseaseMatrix = "../../matrix/drug_disease_matrix.txt";
        String drugDiseaseAssoc = "../../assoc/drug_disease_assoc.txt";
        String gsn = "../../gsn/negative_0.3.txt";
        String drugOut = "../../incomplete/drug_incomp_matrix.txt";
        String drugGeneOut = "../../incomplete/drug_gene_incomp_matrix.txt";
        String drugDiseaseOut = "../../incomplete/drug_disease_incomp_matrix.txt";
        String drugDiseaseAssocOut = "../../incomplete/drug_disease_incomp_assoc.txt";
        String gsnOut = "../../incomplete/gsn.txt";
        filterHighSimDrug(drugFile, drugMatrix, drugGeneMatrix, drugDiseaseMatrix, drugDiseaseAssoc,
                drugOut, drugGeneOut,drugDiseaseOut,drugDiseaseAssocOut,gsn, gsnOut);
       
    }
    
    public static void main(String args[]){
        new IncompDataExtractor().filterDrug();
    }
}

class Pair{
    int x;
    int y;
    Pair(int x, int y){
        this.x = x;
        this.y = y;
    }
}
