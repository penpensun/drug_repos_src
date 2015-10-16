/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.lucene;
import org.apache.lucene.*;
import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.standard.*;
import org.apache.lucene.store.*;
import org.apache.lucene.index.*;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import java.io.IOException;
import java.io.*;
import java.util.*;
import data.processor.ResParser;
import data.extractor.SynExtractor;
import data.processor.ResParser;
import data.io.*;
/**
 * This class contains methods that perform searching of the drug repositioning records
 * in the lucene-created index.
 * @author penpen926
 */
public class Searcher {
    
    /**
     * THis method creates a query from the given drug syns and the disease syns.
     * @param drugs
     * @param diseases
     * @param analyzer
     * @return 
     */
    public Query createQuery(ArrayList<String> drugs, ArrayList<String> diseases,
            Analyzer analyzer){
        StringBuilder drugQuery = new StringBuilder();
        StringBuilder diseaseQuery = new StringBuilder();
        // Create the drug query.
        drugQuery.append("( ");
        for(int i=0;i<drugs.size()-1;i++)
            drugQuery.append("info: ").append("\"").append(drugs.get(i)).append("\"").append(" OR ");
        drugQuery.append("info: ").append("\"").append(drugs.get(drugs.size()-1)).append("\"").append(")");
        
         // Create the disease query.
        diseaseQuery.append("( ");
        for(int i=0;i<diseases.size()-1 ;i++)
            diseaseQuery.append("info: ").append("\"").append(diseases.get(i)).append("\"").append(" OR ");
        diseaseQuery.append("info: ").append("\"").append(diseases.get(diseases.size()-1)).append("\"").append(")");
        
        StringBuilder queryStr = drugQuery.append(" AND ").append(diseaseQuery);
        Query q = null;
        try{
            
            q= new QueryParser("info",analyzer ).parse(queryStr.toString());
        }catch(Exception e){
            System.err.println("Query create error.");
            System.err.println(queryStr.toString());
            try{q = new QueryParser("info",analyzer).parse("");} catch(ParseException ex){}
            e.printStackTrace();
        }
        return q;
    }
    
    /**
     * This method searches the record in the given index dir.
     * The record contains two parts: The arraylist of the synonyms of the drug and the
     * arrayist of the synonyms of the disease.
     * @param drugs
     * @param diseases
     * @param indexDir
     * @param indexReader
     * @param iSearcher
     * @param searchQuery
     * @param analyzer
     * @return 
     */
    public int searchRecord(ArrayList<String> drugs, ArrayList<String> diseases,
            String indexDir, 
            DirectoryReader indexReader,
            IndexSearcher iSearcher,
            Query searchQuery,
            Analyzer analyzer){
       
        
        searchQuery = createQuery(drugs, diseases,analyzer);
        TopDocs queryRes = null;
        try{
            queryRes = iSearcher.search(searchQuery, 50);
        }catch(IOException e){
            System.err.println("query error.");
            e.printStackTrace();
        }
        if(queryRes == null || queryRes.scoreDocs == null || 
                queryRes.scoreDocs.length == 0 ||queryRes.totalHits == 0)
            return 0;
        int ans = queryRes.totalHits;
        queryRes = null;
        return ans;
    }
    
    
    /**
     * This is the major method in the class Searcher.
     * This method receives a parsedResFile which contains the parsed result from drug repositioning computing,
     * and the path of the index dir and the path of the output.
     * This method parses the results file into arrayList of records, and extracts the synonyms for each drug and disease
     * in the record and tries to query them in the index.
     * The querying results are then written into the output file, including 
     * drug\tdisease\tfoundInNumDocs\n
     * @param parsedResFile
     * @param knownResFile
     * @param drugNameMap
     * @param indexDir
     * @param output 
     * @param drugSynFile 
     * @param diseaseSynFile 
     */
    public void validateParsedRes(String parsedResFile, String knownResFile, String drugNameMap, String indexDir, String output,
            String drugSynFile,
            String diseaseSynFile){
        ResParser parser = new ResParser();
        SynExtractor synEx = new SynExtractor();
        Analyzer analyzer = new StandardAnalyzer();
        //ArrayList<String> extractedRes = parser.extractResultPair(parsedResFile);
        ArrayList<String> extractedRes = parser.extractNovelResultPair(parsedResFile, knownResFile);
        HashMap<String, String> drugNames = new DataReader().readMap2(drugNameMap);
        FileWriter fw = null;
        BufferedWriter bw = null;
        
         // Open the index directory.
        File index = new File(indexDir);
        Directory indexDirectory = null;
        try{
            indexDirectory = FSDirectory.open(index.toPath());
        }catch(IOException e){
            System.err.println("FSDirectory open error.");
            e.printStackTrace();
        }
        
        //Init the index reader.
        DirectoryReader indexReader = null;
        IndexSearcher iSearcher = null;
        try{
            indexReader = DirectoryReader.open(indexDirectory);
            iSearcher = new IndexSearcher(indexReader);
            
        }catch(IOException e){
            System.err.println("Index reader error.");
            e.printStackTrace();
        }
        
        try{
            fw = new FileWriter(output);
            bw = new BufferedWriter(fw);
        }catch(IOException e){
            System.err.println("File writer error.");
            e.printStackTrace();
        }
        System.out.println("Total: "+extractedRes.size()+" pairs.");
        int i=0;
        for(String resPair: extractedRes){
            i++;
            if(i!=0 && i%10 == 0)
                System.out.println((float)i/extractedRes.size()*100+"% finished");
            String[] splits = resPair.split("\t");
            String drug = drugNames.get(String.copyValueOf(splits[0].toCharArray()));
            String disease = String.copyValueOf(splits[1].toCharArray());
            if(drug == null)
                continue;
            ArrayList<String> drugSyns = synEx.getDrugSyns(drug, drugSynFile);
            ArrayList<String> diseaseSyns = synEx.getDiseaseSyns(disease, diseaseSynFile);
            if(drugSyns.isEmpty())
                continue;
            if(diseaseSyns.isEmpty())
                continue;
            
            Query searchQuery = null;
            int docsFound = searchRecord(drugSyns, diseaseSyns, indexDir,
                    indexReader,
                    iSearcher,
                    searchQuery,
                    analyzer
                    );
            if(docsFound >0){
                try{
                    bw.write(drug+"\t"+disease+"\t"+docsFound+"\n");
                    bw.flush();
                }catch(IOException e){
                    System.err.println("Writing error.");
                    e.printStackTrace();
                }
            }
        }
        try{
            bw.close();
            fw.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    
    
    public static void main(String[] args){
        String parsedRes = "../../nforce_output/parsed_res_0.1_0.83.txt";
        String knownRes = "../../assoc/drug_disease_assoc.txt";
        String indexDir = "../../lucene/index";
        String output = "../../lucene/valid_res_0.1.txt";
        String drugSynFile = "../../drug/drug_syn.txt";
        String diseaseSynFile = "../../disease/disease_syn_map.txt";
        String drugNameFile = "../../drug/drug_drug_names_assoc.txt";
        
        new Searcher().validateParsedRes(parsedRes, knownRes,drugNameFile, indexDir, output, drugSynFile, diseaseSynFile);
    }
    
   
}
