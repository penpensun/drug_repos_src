/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.processor;
import java.io.IOException;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
/**
 *
 * @author penpen926
 */
public class TanimotoCoeffFormatConverter {
    /**
     * This method converts yuan zhao's file into 
     * @param inputFile
     * @param nodesNameFile
     * @param outputFile 
     */
    public void convertYuanForm(String inputFile, String nodesNameFile, String outputFile){
        try{
        FileReader fr = new FileReader(inputFile);
        BufferedReader br = new BufferedReader(fr);
        
        FileWriter nodeFw = new FileWriter(nodesNameFile);
        BufferedWriter nodeBw = new BufferedWriter(nodeFw);
        
        FileWriter matrixFw = new FileWriter(outputFile);
        BufferedWriter matrixBw = new BufferedWriter(matrixFw);
        
        double[][] matrix = new double[10000][10000]; /* Create a matrix big enough.*/
        /* Init the matrix. */
        for(int i=0;i<matrix.length;i++)
            for(int j=0;j<matrix[0].length;j++)
                matrix[i][j] = Double.NaN;
        
        ArrayList<String> nodes = new ArrayList<>();
        String id1 = null;
        String id2 = null;
        double taniCoeff = -1;
        String line = null;
        while((line = br.readLine())!= null){
            line =line.trim();
            if(line.isEmpty())
                continue;
            /* If this line is not with an id.*/
            if(line.startsWith("Tanimoto")){
                try{
                String tani = line.split(":")[1];
                taniCoeff = Double.parseDouble(tani);
                }catch(ArrayIndexOutOfBoundsException e){
                    System.out.println(line);
                    e.printStackTrace();
                }catch(NumberFormatException e){
                    e.printStackTrace();
                }
                /* Check if both id1 and id2 are initialized.*/
                if(id1 == null || id2 == null)
                    throw new IllegalStateException("(dataprocessing.TanimotoCoeffFormatConverter.convertYuanForm) When tanimoto line, id1 and id2 are not both initialized:  "+line);
                /* Extract the indexes for id1 and id2. */
                int idx1 = nodes.indexOf(id1);
                int idx2 = nodes.indexOf(id2);
                /* Check the two indexes.*/
                if(idx1 == -1 || idx2 == -1)
                    throw new IllegalStateException("(dataprocessing.TanimotoCoeffFormatConverter.convertYuanForm) Indexes for node1 or node2 is -1. Index 1: "+idx1+"  Index 2: "+idx2);
                /* Assign the matrix.*/
                try{
                matrix[idx1][idx2] = taniCoeff;
                matrix[idx2][idx1] = taniCoeff;
                }catch(ArrayIndexOutOfBoundsException e){
                    System.out.println("(dataprocessing.TanimotoCoeffFormatConverter.convertYuanForm) Matrix is not big enough. idx1:  "+idx1+"  idx2:  "+idx2);
                    return;
                }
                id1 = null;
                id2 = null;
                taniCoeff = -1;
            }
            else if(id1 == null){
                line = line.trim();
                id1 = line;
                if(!nodes.contains((id1)))
                    nodes.add(id1);
            }
            else if (id2 == null){
                line = line.trim();
                id2 = line;
                if(!nodes.contains((id2)))
                    nodes.add(id2);
            }
            else throw new IllegalStateException("(dataprocessing.TanimotoCoeffFormatConverter.convertYuanForm) id1 and id2 are both non-null while not a tanimoto line:  "+line);
            
        }
        
        /* Output the nodes names. */
        for(int i=0;i<nodes.size()-1;i++){
            nodeBw.write(nodes.get(i)+"\t");
        }
        nodeBw.write(nodes.get(nodes.size()-1)+"\n");
        nodeBw.flush();
        nodeBw.close();
        nodeFw.close();
        
        /* Output the matrix. */
        for(int i=0;i<matrix.length;i++){
            for(int j=0;j<matrix[0].length-1;j++){
                /* Check the edge of the matrix. */
               if(Double.isNaN(matrix[i][j])){
                   /* If the matrix[i][j] is NaN, we have 3 cases.*/
                   if(i ==j){ /* if i==j, then we output them.*/
                       matrixBw.write(matrix[i][j]+"\t");
                   }
                   else if(j==0){ /* if j==0, then we have reached the edge of the matrix.*/
                       break; /* Break the loop and stop the outputting.*/
                   }
                   else if(i<j){ /* If j>i, then we have reached the end of the line, we proceed to the next line.*/
                       continue;
                   }
                   else throw new IllegalStateException("(dataprocessing.TanimotoCoeffFormatConverter.convertYuanForm) Illegal NaN: "+i+" "+j);
               }
               /* Else, then we output the matrix into the matrix output.*/
               else
                   matrixBw.write(matrix[i][j]+"\t");
            }
            matrixBw.write("\n");
           
        }
        matrixBw.flush();
        matrixBw.close();
        matrixFw.close();
        
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    
    
}
