/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.io;
import java.io.*;
import java.util.*;
/**
 *
 * @author penpen926
 */
public class DataWriter {
    /**
     * This method outputs the given fasta to the given filePath.
     * @param fasta
     * @param filePath 
     */
    public void writeFasta(String fasta, String filePath){
        try{
            FileWriter fw = new FileWriter(filePath);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(fasta+"\n");
            bw.flush();
            bw.close();
            fw.close();
        }catch(IOException e){
            System.err.println("(DataWriter.writerFasta) File writing error. ");
            return;
        }
    }
    
    /**
     * This method writes the ids in the given List into the given outputFile. 
     * The ids can be the drugs' ids or ligands' ids, whatever id that can be 
     * represented by string.
     * @param ids The ids to output.
     * @param outputFile  The output file.
     * @param sep The seperating symbol between different ids. If null is given, the default is "\n"
     */
    public void writeIds(List<String> ids, String outputFile, String sep){
        /* Check the arguments. */
        if(ids == null || ids.isEmpty())
            throw new NullPointerException("(DataWriter.writeIds) The ids must not be null.");
        if(outputFile== null)
            throw new NullPointerException("(DataWriter.writeIds) The outputFile cannot be null.");
        if(sep == null)
            sep = "\n";
        /* Create the FileWriter and the BufferedWriter. */
        FileWriter fw =null;
        BufferedWriter bw = null;
        try{
            fw = new FileWriter(outputFile);
            bw = new BufferedWriter(fw);
        }catch(IOException e){
            System.err.println("(DataWriter.writeIds) The file writer initialization error.");
            return;
        }
        try{
            for(int i=0;i<ids.size()-1;i++){
                bw.write(ids.get(i)+sep);
            }
            bw.write(ids.get(ids.size()-1)+sep);
            bw.flush();
            bw.close();
            fw.close();
        }catch(IOException e){
            System.err.println("(DataWriter.writeIds) Error by writing the output file. ");
            return;
        }
    }
    
    
    public void writeMatrix(float[][] matrix, String outFile){
        try{
            FileWriter fw= new FileWriter(outFile);
            BufferedWriter bw = new BufferedWriter(fw);
            for(int i=0;i<matrix.length;i++){
                for(int j=0;j<matrix[0].length-1;j++)
                    bw.write(matrix[i][j]+"\t");
                bw.write(matrix[i][matrix[i].length-1]+"\n");
            }
            bw.close();
            fw.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
        
    }
    
    /**
     * This method writes the hashmap of string to arrayList to a file.
     * @param map
     * @param outFile 
     */
    public void writeHashMap(HashMap<String,ArrayList<String>> map, String outFile){
        /* Check the arguments. */
        if(map == null)
            throw new IllegalArgumentException("(DataWriter.writeHashMap) Map cannot be null. ");
        FileWriter fw = null;
        BufferedWriter bw = null;
        try{
            fw = new FileWriter(outFile);
            bw = new BufferedWriter(fw);
        }catch(IOException e){
            System.err.println("(DataWriter.writeHashMap) The out put file error:  "+outFile);
            return;
        }
        
        Set<Map.Entry<String,ArrayList<String>> > mapEntrySet = mapEntrySet = map.entrySet();
        if(mapEntrySet == null)
            throw new IllegalStateException("(DataWriter.writeHashMap) map entry null. ");
        Iterator<Map.Entry<String,ArrayList<String>> > iterator = mapEntrySet.iterator();
        while(iterator.hasNext()){
            Map.Entry<String,ArrayList<String>> entry = iterator.next();
            String key = entry.getKey();
            ArrayList<String> value = entry.getValue();
            try{
            bw.write(key+"\t");
            /* If the value is empty. */
            if(value.isEmpty()){
                bw.write("\n");
                continue;
            }
            for(int i=0;i<value.size()-1;i++)
                bw.write(value.get(i)+"\t");
            
            bw.write(value.get(value.size()-1)+"\n");
            }catch(IOException e){
                System.err.println("(DataWriter.writeHashMap) Writing error.");
                return ;
            }    
        }
        try{
        bw.flush();
        bw.close();
        fw.close();
        }catch(IOException e){
            System.err.println("(DataWriter.writeHashMap) Flushing and close error.");
                return ;
        }
        
    }
    
    /** 
     * Write one-one hashmap association
     * @param map
     * @param outputFile 
     */
    public void writeHashMap4(HashMap<String, String> map, String outputFile){
        /* Check the arguments. */
        if(map == null)
            throw new IllegalArgumentException("(DataWriter.writeHashMap) Map cannot be null. ");
        FileWriter fw = null;
        BufferedWriter bw = null;
        try{
            fw = new FileWriter(outputFile);
            bw = new BufferedWriter(fw);
        }catch(IOException e){
            System.err.println("(DataWriter.writeHashMap) The out put file error:  "+outputFile);
            return;
        }
        Set<Map.Entry<String,String>> mapEntrySet = map.entrySet();
        if(mapEntrySet == null)
            throw new IllegalStateException("(DataWriter.writeHashMap) map entry null. ");
        Iterator<Map.Entry<String,String> > iterator = mapEntrySet.iterator();
        while(iterator.hasNext()){
            Map.Entry<String,String> entry = iterator.next();
            String key = entry.getKey();
            String value = entry.getValue();
            try{
            bw.write(key+"\t"+value+"\n");
            }catch(IOException e){
                System.err.println("(DataWriter.writeHashMap) Writing error.");
                return ;
            }    
        }
        try{
        bw.flush();
        bw.close();
        fw.close();
        }catch(IOException e){
            System.err.println("(DataWriter.writeHashMap) Flushing and close error.");
                return ;
        }
    }
    
    
    /**
     * This method writes hashmap of String and integer.
     * @param map
     * @param outputFile 
     */
    public void writeHashMap5(HashMap<String, Integer> map, String outputFile){
        /* Check the arguments. */
        if(map == null)
            throw new IllegalArgumentException("(DataWriter.writeHashMap) Map cannot be null. ");
        FileWriter fw = null;
        BufferedWriter bw = null;
        try{
            fw = new FileWriter(outputFile);
            bw = new BufferedWriter(fw);
        }catch(IOException e){
            System.err.println("(DataWriter.writeHashMap) The out put file error:  "+outputFile);
            return;
        }
        Set<Map.Entry<String,Integer>> mapEntrySet = map.entrySet();
        if(mapEntrySet == null)
            throw new IllegalStateException("(DataWriter.writeHashMap) map entry null. ");
        Iterator<Map.Entry<String,Integer> > iterator = mapEntrySet.iterator();
        while(iterator.hasNext()){
            Map.Entry<String,Integer> entry = iterator.next();
            String key = entry.getKey();
            Integer value = entry.getValue();
            try{
            bw.write(key+"\t"+value+"\n");
            }catch(IOException e){
                System.err.println("(DataWriter.writeHashMap) Writing error.");
                return ;
            }    
        }
        try{
        bw.flush();
        bw.close();
        fw.close();
        }catch(IOException e){
            System.err.println("(DataWriter.writeHashMap) Flushing and close error.");
                return ;
        }
    }
    /**
     * This method writes the hashmap of string-hashset to a file.
     * @param map
     * @param outFile 
     */
    public void writeHashMap2(HashMap<String,HashSet<String>> map, String outFile){
        /* Check the arguments. */
        if(map == null)
            throw new IllegalArgumentException("(DataWriter.writeHashMap) Map cannot be null. ");
        FileWriter fw = null;
        BufferedWriter bw = null;
        try{
            fw = new FileWriter(outFile);
            bw = new BufferedWriter(fw);
        }catch(IOException e){
            System.err.println("(DataWriter.writeHashMap) The out put file error:  "+outFile);
            return;
        }
        
        Set<Map.Entry<String,HashSet<String>> > mapEntrySet = map.entrySet();
        if(mapEntrySet == null)
            throw new IllegalStateException("(DataWriter.writeHashMap) map entry null. ");
        Iterator<Map.Entry<String,HashSet<String>> > iterator = mapEntrySet.iterator();
        while(iterator.hasNext()){
            Map.Entry<String,HashSet<String>> entry = iterator.next();
            String key = entry.getKey();
            ArrayList<String> value = new ArrayList<>(entry.getValue());
            try{
            bw.write(key+"\t");
            /* If the value is empty. */
            if(value.isEmpty()){
                bw.write("\n");
                continue;
            }
            for(int i=0;i<value.size()-1;i++)
                bw.write(value.get(i)+"\t");
            
            bw.write(value.get(value.size()-1)+"\n");
            }catch(IOException e){
                System.err.println("(DataWriter.writeHashMap) Writing error.");
                return ;
            }    
        }
        try{
        bw.flush();
        bw.close();
        fw.close();
        }catch(IOException e){
            System.err.println("(DataWriter.writeHashMap) Flushing and close error.");
                return ;
        }
        
    }
    
    
    /**
     * This method writes a hashmap of string -- integer to a file, mainly used to output the count. 
     * @param map
     * @param outputFile 
     */
    public void writeHashMap3(HashMap<String, HashSet<String>> map, String outputFile){
        /* Check the arguments. */
        if(map == null)
            throw new IllegalArgumentException("(DataWriter.writeHashMap) Map cannot be null. ");
        FileWriter fw = null;
        BufferedWriter bw = null;
        try{
            fw = new FileWriter(outputFile);
            bw = new BufferedWriter(fw);
        }catch(IOException e){
            System.err.println("(DataWriter.writeHashMap) The out put file error:  "+outputFile);
            return;
        }
        
        Set<Map.Entry<String,HashSet<String>> > mapEntrySet = map.entrySet();
        if(mapEntrySet == null)
            throw new IllegalStateException("(DataWriter.writeHashMap) map entry null. ");
        Iterator<Map.Entry<String,HashSet<String>> > iterator = mapEntrySet.iterator();
        while(iterator.hasNext()){
            Map.Entry<String,HashSet<String>> entry = iterator.next();
            String key = entry.getKey();
            ArrayList<String> value = new ArrayList<>(entry.getValue());
            try{
            bw.write(key+"\t");
            /* If the value is empty. */
            if(value.isEmpty()){
                bw.write("\n");
                continue;
            }
            bw.write(value.size()+"\n");
            }catch(IOException e){
                System.err.println("(DataWriter.writeHashMap) Writing error.");
                return ;
            }    
        }
        try{
        bw.flush();
        bw.close();
        fw.close();
        }catch(IOException e){
            System.err.println("(DataWriter.writeHashMap) Flushing and close error.");
                return ;
        }
    }
    
    /**
     * This method writes a hash set into an output file, separated by sep.
     * @param outFile
     * @param sep 
     */
    public void writeHashSet(HashSet<String> set, String outFile, String sep){
        FileWriter fw =null;
        BufferedWriter bw  = null;
        try{
        fw = new FileWriter(outFile);
        bw = new BufferedWriter(fw);
        
        
        Iterator iter = set.iterator();
        while(iter.hasNext()){
            bw.write(iter.next()+sep);
        }
        
        bw.flush();
        bw.close();
        fw.close();
        }catch(IOException e){
            System.err.println("(DataWriter.writeHashSet) Write error.");
            return;
        }
    }
    
    
    public void modifyMatrix(String matrixFile, String outputFile){
        DataReader reader = new DataReader();
        float[][] matrix = reader.readMatrix(matrixFile, 2610, 2610);
        for(int i=0;i<matrix.length;i++)
            for(int j=0;j<matrix[0].length;j++)
                matrix[i][j] = 0.5f*matrix[i][j];
        new DataWriter().writeMatrix(matrix, outputFile);
    }
    
    public static void main(String args[]){
        new DataWriter().modifyMatrix("../../disease/disease_disease_matrix_5.txt", "../../disease/disease_disease_matrix_5_mod.txt");
    }

    /**
     * This method writes the gene name and the corresponding fasta into the given output file.
     * @param outFile
     * @param gene
     * @param fastaSeq
     */
    public void writeGeneFasta(String outFile, String gene, String fastaSeq) {
        FileWriter fw = null;
        BufferedWriter bw = null;
        try {
            fw = new FileWriter(outFile);
            bw = new BufferedWriter(fw);
        } catch (IOException e) {
            System.err.println("(GeneNameExtractor.writeGeneNameFasta) File write init error.");
            return;
        }
        try {
            bw.write(">" + gene + "\n");
            if (fastaSeq.startsWith(">")) {
                fastaSeq = fastaSeq.substring(fastaSeq.indexOf("\n") + 1);
            }
            bw.write(fastaSeq + "\n");
            bw.flush();
            bw.close();
            fw.close();
        } catch (IOException e) {
            System.err.println("(GeneNameExtractor.writeGeneNameFasta) Fasta writing error.");
            return;
        }
    }
    
}
