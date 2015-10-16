/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.preprocess;
import data.io.DataReader;
import data.io.DataWriter;
import data.processor.*;
import java.util.*;
/**
 * This class contains the method to preprocess the sim matrix. 
 * The matrix is gene_name - gene_name matrix which is computed by python script.
 * @author penpen926
 */

/**
 * 
 * @author penpen926
 */
public class MatrixPreProcess {
    public void preProMatrix(String inputMatrix, String outputMatrix, String geneNameFile){
        DataReader reader  = new DataReader();
        ArrayList<String> geneNameList  = reader.readIds2(geneNameFile);
        float[][] matrix  =reader.readMatrix(inputMatrix, geneNameList.size(), geneNameList.size());
        for(int i=0;i<matrix.length;i++)
            for(int j=0;j<matrix[0].length;j++){
                if(i == j)
                    matrix[i][j] = Float.NaN;
                if(matrix[i][j] != matrix[j][i]){
                    float value = Math.min(matrix[i][j], matrix[j][i]);
                    matrix[i][j] = value;
                    matrix[j][i] = value;
                }
            }
        
        new DataWriter().writeMatrix(matrix, outputMatrix);
    }
    
    public static void main(String args[]){
        String inputMatrix = "../../ligand/python_geneName_matrix_6.txt";
        String outputMatrix = "../../ligand/geneName_matrix_6.txt";
        String diseaseFile = "../../ligand/geneName_6.txt";
        new MatrixPreProcess().preProMatrix(inputMatrix, outputMatrix, diseaseFile);
    }
}
