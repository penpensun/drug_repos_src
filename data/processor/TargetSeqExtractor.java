/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package data.processor;
import org.jdom2.input.*;
import org.jdom2.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
/**
 *
 * This class extracts the sequences of all 
 * @author mac-97-41
 */
public class TargetSeqExtractor {
    public void extractTargetSeq(){
        String drugbankXml= "../../drugbank.xml";
        String outputFolder = "../../target_seq/";
        SAXBuilder drugbankXmlBuilder = new SAXBuilder();
        Document drugbankDoc = null;
        try{
            drugbankDoc = drugbankXmlBuilder.build(new File(drugbankXml));
        }catch(IOException e){
            System.out.println("(dataprocessing.TargetSeqExtractor.extractTargetSeq) File reading error. ");
            return;
        }
        catch(JDOMException e){
            System.out.println("(dataprocessing.TargetSeqExtractor.extractTargetSeq) JDom parsing error");
        }
        /* Get the root element. */
        /* Check if the xml document is properly initialized. */
        if(drugbankDoc == null)
            throw new IllegalStateException("(dataprocessing.TargetSeqExtractor.extractTargetSeq) Xml document is not properly initialized. ");
        /* Get the element. */
        Element root = null;
        try{
            root = drugbankDoc.getRootElement();
        }catch(IllegalStateException e){
            System.out.println("(dataprocessing.TargetSeqExtractor.extractTargetSeq) Root element extraction error.");
        }
        Namespace ns = root.getNamespace(); /* Return the namespace used in this doc.*/
        /* Get the drugs elements. */
        List<Element> drugElementList = root.getChildren("drug",ns);
        if(drugElementList == null)
            throw new IllegalStateException("(dataprocessing.TargetSeqExtractor.extractTargetSeq) NO drug element found in the root element.");
        
        
    }
}
