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
import java.io.IOException;
import java.io.*;
import java.util.*;
import org.jdom2.Content;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
/**
 *
 * @author penpen926
 */
public class IndexCreator {
    public void testIndexCreator() throws IOException, ParseException{
        Analyzer analyzer = new StandardAnalyzer();
        File indexDir = new File("../../lucene/test_index.txt");
        // Store the index in memory:
        Directory directory = FSDirectory.open(indexDir.toPath());
        // To store an index on disk, use this instead:
        //Directory directory = FSDirectory.open("/tmp/testindex");
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter iwriter = new IndexWriter(directory, config);
        Document doc = new Document();
        String text = "This is the text to be indexed.";
        doc.add(new Field("fieldname", text, TextField.TYPE_STORED));
        iwriter.addDocument(doc);
        iwriter.close();

        // Now search the index:
        DirectoryReader ireader = DirectoryReader.open(directory);
        IndexSearcher isearcher = new IndexSearcher(ireader);
        // Parse a simple query that searches for "text":
        QueryParser parser = new QueryParser("fieldname", analyzer);
        Query query = parser.parse("text");
        ScoreDoc[] hits = isearcher.search(query, null, 1000).scoreDocs;
        System.out.println("hits length:  "+hits.length);
        // Iterate through the results:
        for (int i = 0; i < hits.length; i++) {
          Document hitDoc = isearcher.doc(hits[i].doc);
          System.out.println(hitDoc.get("fieldname"));
        }
        ireader.close();
        directory.close();
    }
    
    /**
     * This method uses the writer to create the index of the given Medline object.
     * @param writer
     * @param med 
     */
    public void putIndex(IndexWriter writer, Medline med){
        
        StringBuilder keywords = new StringBuilder();
        for(String keyStr: med.keywords)
            keywords.append(keyStr).append("\t");
        
        StringBuilder meshHeadingsText = new StringBuilder();
        for(String meshStr: med.meshHeadings)
            meshHeadingsText.append(meshStr).append("\t");
        
        StringBuilder articleInfo = new StringBuilder();
        articleInfo.append(med.title).append("\t")
                .append(med.abstractText).append("\t")
                .append(meshHeadingsText)
                .append(keywords);
        
        Field articleInfoField = new Field("info",articleInfo.toString(), TextField.TYPE_STORED);
        
        Field pmidField = new Field("pmid", med.pmid, TextField.TYPE_STORED);
        // Put into the index.
        Document doc = new Document();
        doc.add(pmidField);
        doc.add(articleInfoField);
        
        try{
            writer.addDocument(doc);
        }catch(IOException e){
            System.err.println("IndexCreator writing error.");
            e.printStackTrace();
        }
    }
    
    /**
     * This method creates indexes for all files in the medlineDir.
     * @param medlineDir
     * @param indexDir 
     */
    public void createAllMedlineIndex(String medlineDir, String indexDir){
        Analyzer analyzer = new StandardAnalyzer();
        Directory indexPath = null;
        MedlineParser medParser = new MedlineParser();
        try{
            indexPath = FSDirectory.open(new File(indexDir).toPath());
        }catch(IOException e){
            System.err.println("FSDirectory open error.");
            e.printStackTrace();
        }
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter writer = null;
        try{
            writer = new IndexWriter(indexPath, config);
        }catch(IOException e){
            System.err.println("Index writer error.");
            e.printStackTrace();
        }
        // Get all files in the given medline dir.
        String files[] = new File(medlineDir).list();
        if(files == null)
            throw new IllegalArgumentException("The given medline dir cannot be found.");
           
        int i=0;
        for(String file: files){
            i++;
            if(i%10 == 0)
                System.out.println((float)(i)/files.length*100+"% finished" );
            if(file.startsWith("."))
                continue;
            if(!file.endsWith(".xml"))
                continue;
            String filePath = medlineDir+"/"+file;
            List<Element> medlineElementList= medParser.extractMedlineElementList(filePath);
            if(medlineElementList == null){
                System.err.println("Null medline list:  "+filePath);
                continue;
            }
            for(Element medElement: medlineElementList){
                Medline med = medParser.extractMedlineInfo(medElement);
                putIndex(writer, med);
            }
            System.out.println(file+"  finished.");
        }
        
        //Close the index writer
        try{
            writer.close();
        }catch(IOException e){
            System.err.println("index writer closing error.");
            e.printStackTrace();
        }
    }
    
    
    public void runCreateIndex(){
        String xmlDir = "../../medline/files/";
        String outputDir = "../../lucene/index/";
        createAllMedlineIndex(xmlDir, outputDir);
    }
    
    public static void main(String args[]) throws IOException,ParseException{
        IndexCreator creator = new IndexCreator();
        //creator.testIndexCreator();
        creator.runCreateIndex();
    }
}
