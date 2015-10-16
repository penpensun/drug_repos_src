/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.lucene;
import java.io.*;
import java.util.*;
import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import data.io.*;

/**
 *
 * @author penpen926
 */
public class MedlineParser {
    
    /**
     * This method parses a given xmlfile and create an ArrayList of Medline elements.
     * Then the ArrayList is written into the given otuput file as serialized object.
     * @param xmlFile 
     * @param output
     */
    public void parseMedlineFile(String xmlFile, String output){
        FileOutputStream out = null;
        ObjectOutputStream bout = null;
        try{
            out = new FileOutputStream(output);
            bout = new ObjectOutputStream(out);
            ArrayList<Medline> medlineInfo = new ArrayList<>();
            List<Element> medlineList = extractMedlineElementList(xmlFile);
            for(Element med: medlineList){
                Medline m = extractMedlineInfo(med);
                medlineInfo.add(m);
            }
            bout.writeObject(medlineInfo);
        }catch(IOException e){
            System.err.println("File output error.");
            e.printStackTrace();
        }
    }
    
    /**
     * This method reads the written objectstream file into an arraylist of medline.
     * @param inputFile
     * @return 
     */
    public ArrayList<Medline> readMedlineFile(String inputFile){
        ArrayList<Medline> ans = null;
        FileInputStream in = null;
        ObjectInputStream objin = null;
        try{
            in = new FileInputStream(inputFile);
            objin = new ObjectInputStream(in);
            ans = (ArrayList<Medline>)objin.readObject();
        }catch(IOException e){
            System.err.println("Object input error.");
            e.printStackTrace();
        }
        catch(ClassNotFoundException e){
            System.err.println("Cass not found.");
            e.printStackTrace();
        }
        return ans;
    }
    /**
     * This method extract all MedlineCitation
     * @param xmlFile
     * @return 
     */
    public List<Element> extractMedlineElementList(String xmlFile){
        SAXBuilder builder = new SAXBuilder();
        Document medlineDoc = null;
        try {
            medlineDoc = builder.build(new File(xmlFile));
        } catch (IOException e) {
            System.out.println("(MeshExtractor.extractMeshElementList) Reading file error.");
            return null;
        } catch (JDOMException e) {
            System.out.println("(MeshExtractor.extractMeshElementList) JDOM error");
        }
        if(medlineDoc == null)
            throw new IllegalStateException("No element found in the xml.");
        Element root = medlineDoc.getRootElement();
        Namespace ns = root.getNamespace();
        if(root == null)
            throw new IllegalStateException("No root elment.");
        return root.getChildren("MedlineCitation", ns);
    }
    
    /**
     * This method extracts the information of a given medline element, writing them into the given 
     * file.
     * @param medlineElement
     * @param output
     * @param append 
     */
    public Medline extractMedlineInfo(Element medlineElement, String output, boolean append){
        FileWriter fw = null;
        BufferedWriter bw = null;
        Medline ans= new Medline();
        try{
            fw = new FileWriter(output, append);
            bw = new BufferedWriter(fw);
        }catch(IOException e){
            System.err.println("File writer open wrong: "+output);
            e.printStackTrace();
        }
        try{
            bw.write("#MedlineCitationStart\n");
            String pmid = extractPmid(medlineElement);
            String title = extractTitle(medlineElement);
            String abstractText = extractAbstract(medlineElement);
            ArrayList<String> meshHeadings = extractMeshHeadingList(medlineElement);
            ArrayList<String> keywords = extractKeywords(medlineElement);
            ans.pmid = pmid;
            ans.title = title;
            ans.abstractText = abstractText;
            ans.meshHeadings = meshHeadings;
            ans.keywords = keywords;
            if(pmid != null)
                bw.write(pmid+"\n");
            if(title != null)
                bw.write(title+"\n");
            if(abstractText != null)
                bw.write(abstractText+"\n");
            if(meshHeadings != null){
                for(String heading: meshHeadings)
                    bw.write(heading+"\n");
            }
            if(keywords != null){
                for(String keyword: keywords)
                    bw.write(keyword+"\n");
            }
            bw.flush();
            bw.close();
            fw.close();
        }catch(IOException e){
            System.err.println("Writing error.");
            e.printStackTrace();
        }
        return ans;
    }
    
    
    /**
     * This method 
     * @param medlineElement
     * @return 
     */
    public Medline extractMedlineInfo(Element medlineElement){
        Medline ans = new Medline();
        String pmid = extractPmid(medlineElement);
        String title = extractTitle(medlineElement);
        String abstractText = extractAbstract(medlineElement);
        ArrayList<String> meshHeadings = extractMeshHeadingList(medlineElement);
        ArrayList<String> keywords = extractKeywords(medlineElement);
        ans.pmid = pmid;
        ans.title = title;
        ans.abstractText = abstractText;
        ans.meshHeadings = meshHeadings;
        ans.keywords = keywords;
        return ans;
    }
    /**
     * This method extracts the title of a given medline element.
     * DTD: Article (Exactly one)
     * DTD: ArticleTitle (Exactly one)
     * @param medlineElement
     * @return 
     */
    public String extractTitle(Element medlineElement){
        Element article = medlineElement.getChild("Article", medlineElement.getNamespace());
        if(article == null)
            return null;
        Element articleTitleElement= article.getChild("ArticleTitle", article.getNamespace());
        if(articleTitleElement == null)
            return null;
        String title = articleTitleElement.getContent(0).getValue();
        return title;
    }
    
    /**
     * This method extracts the abstract of a given medline element.
     * DTD: Article (exactly one) Article -- Abstract (zero or one). 
     * OtherAbstract (zero or many)
     * @param medlineElement
     * @return 
     */
    public String extractAbstract(Element medlineElement){
        // Get Article-- Abstract
        Element articleElement= medlineElement.getChild("Article", medlineElement.getNamespace());
        Element abstractElement = articleElement.getChild("Abstract", articleElement.getNamespace());
        StringBuilder ans = new StringBuilder();
        if(abstractElement != null){
            // Get AbstractText
            // DTD: AbstractText + (one or more AbstractText element)
            List<Element> abstractTextList = 
                    abstractElement.getChildren("AbstractText",abstractElement.getNamespace());
            for(Element abstractText: abstractTextList){
                String text = null;
                try{
                    text = abstractText.getContent(0).getValue();
                }catch(IndexOutOfBoundsException e){
                    text = "";
                }
                ans.append(" ").append(text);
            }
        }
        // Get OtherAbstract abstract.
        List<Element> otherAbstractList = medlineElement.getChildren("OtherAbstract", medlineElement.getNamespace());
        if(otherAbstractList == null)
            return ans.toString();
        for(Element otherAbstract: otherAbstractList){
            // Get AbstractText
            // DTD: Abstract + (one or more AbstractText element)
            List<Element> abstractTextList = 
                    otherAbstract.getChildren("AbstractText", otherAbstract.getNamespace());
            for(Element abstractText: abstractTextList){
                String text = abstractText.getContent(0).getValue();
                ans.append(" ").append(text);
            }
        }
        return ans.toString();
    }
    
    /**
     * This method extracts the mesh heading list of a given medline element.
     * DTD: MeshHeadingList (zero or many)
     * @param medlineElement
     * @return 
     */
    public ArrayList<String> extractMeshHeadingList(Element medlineElement){
        List<Element> meshHeadingListList= medlineElement.getChildren("MeshHeadingList", medlineElement.getNamespace());
        if(meshHeadingListList == null)
            return null;
        ArrayList<String> ans = new ArrayList<>();
        for(Element meshHeadingList: meshHeadingListList){
            List<Element> meshHeadings = 
                    meshHeadingList.getChildren("MeshHeading", meshHeadingList.getNamespace());
            if(meshHeadings == null)
                continue;
            for(Element heading: meshHeadings){
                Element descriptorName = heading.getChild("DescriptorName", heading.getNamespace());
                if(descriptorName == null)
                    continue;
                String name = descriptorName.getContent(0).getValue();
                ans.add(name);
            }
        }
        return ans;        
    }
    
    /**
     * This method extracts the keywords of a given medline element.
     * DTD: KeywordList
     * @param medlineElement
     * @return 
     */
    public ArrayList<String> extractKeywords(Element medlineElement){
        ArrayList<String> ans = new ArrayList<>();
        List<Element> keywordListList = 
                medlineElement.getChildren("KeywordList", medlineElement.getNamespace());
        if(keywordListList == null)
            return null;
        for(Element keywordList: keywordListList ){
            // DTD: Keyword +
            List<Element> keywords = keywordList.getChildren("Keyword", medlineElement.getNamespace());
            for(Element kword: keywords){
                String kwordText = kword.getContent(0).getValue();
                ans.add(kwordText);
            }
        }
        return ans;
    }
    
    /**
     * This method extracts the pubmed id of the given medline element.
     * DTD: PMID (exactly one)
     * @param medlineElement
     * @return 
     */
    public String extractPmid(Element medlineElement){
        List<Element> pmidList = medlineElement.getChildren("PMID", medlineElement.getNamespace());
        if(pmidList == null)
            return null;
        if(pmidList.size()>1)
            System.err.println("More than one pmid element:  "+pmidList.toString());
        Element pmidElement = pmidList.get(0);
        String pmid = pmidElement.getContent(0).getValue();
        return pmid;
    }
}
