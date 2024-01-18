/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package accesojjaa;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 *
 * @author Valdivia
 */
public class AccesoJJAA {

        
    /*
    CREAR TABLA 
    
    CREATE TABLE IF NOT EXISTS CONTRATOS (
	id SERIAL PRIMARY KEY,
	NIF VARCHAR(15),
	ADJUDICATARIO VARCHAR(50), 
        OBJETO_GENÉRICO VARCHAR(100),
	OBJETO VARCHAR(100),
	FECHA_DE_ADJUDICACION VARCHAR(30),
	IMPORTE VARCHAR(20), 
	PROVEEDORES_CONSULTADOS VARCHAR(10),
        TIPO_DE_CONTRATO VARCHAR(50));
    
    */
    public static void main(String[] args) {
        
        final String CADENA_CONEXION="jdbc:postgresql://192.168.1.230:5432/";
        
        final int TAMAÑO_FECHA=30;
        
        ConexionBd conexion = new ConexionBd(CADENA_CONEXION);
                
        File fichero= new File("documento.xml");
        
        ArrayList <CContrato> datosRecogidos = null;
        
        // Si el fichero no existe cierro programa
        if (!fichero.exists())
        {
            System.out.println("No encuentro fichero");
            return ;
        }
        
        
        try {
            
            
            FileInputStream flujo = new FileInputStream(fichero);
           
            datosRecogidos = fromXML(flujo);
             
            } catch (FileNotFoundException ex) {
                
            Logger.getLogger(AccesoJJAA.class.getName()).log(Level.SEVERE, null, ex);
            
        }
        
        
        for (CContrato contrato: datosRecogidos)
        {
            //Extraigo los objetos Clave-Valor
            HashMap <ECampos,String> atributos = contrato.getDatos();
                     
            
            System.out.println(atributos.size());
            
            // Hago un apaño para cambiar el campo "S_N" por el "TIPO_CONTRATO"
            // Despues de intercambiar el nodo, borro el nodo que nodo "sobrante"
            if (atributos.containsKey(ECampos.S_N))
            {
                // Extraigo el campo "S_N" (Sin nombre)
                String temp = atributos.get(ECampos.S_N);
                                
                //Intecambio valores
                atributos.put(ECampos.TIPO_CONTRATO,temp);
                
                // elimino nodo "S_N"
                atributos.remove(ECampos.S_N);
                
            }
            
            if (atributos.get(ECampos.FECHA_ADJUDICACIÓN).length() >  TAMAÑO_FECHA)
                atributos.remove(ECampos.FECHA_ADJUDICACIÓN);
            
            
            
            for (ECampos e : ECampos.values())
            {
                if (atributos.containsKey(e))
                System.out.println(e.toString() + " : " + atributos.get(e));
                
            }
            
            //Genero cadena con el insert 
            String cadenaInsert = contrato.toInsertString("Contratos");
            
            //Grabo datos en la base de datos
            conexion.insertaContrato(cadenaInsert);
            
        }
        
        //Genero xml
        toXML(datosRecogidos);
        
        System.out.println();
        
    }
    
    
    /**
     * Fucnion para convertir los datos del XML en pares clave-valor
     * @param datos flujo del fichero xml
     * @return se devuelve par clave valor con los datos del XML en un objeto CContrato
     */
    public static  ArrayList <CContrato> fromXML(InputStream datos)
    {
        // Filtro para buscar los nodos con XPath
        final String filtro="//Table/Row";
        
        // Estructura donde se guardaran los datos
        ArrayList <CContrato> datosRecogidos = new ArrayList<>();
        
        
        InputSource xmlDoc = new InputSource(datos);
        
        //Creo una lista para guardar los nombre de los campos
        ArrayList <String> nombreCampos= new ArrayList<>();
        
        NodeList listaNodos= null;
        
        try
        {
            
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            
            dbf.setIgnoringElementContentWhitespace(true);
            
            DocumentBuilder db = dbf.newDocumentBuilder();
            
            try
            {
                
                Document doc = db.parse(xmlDoc);
                // limpio nodos vacios
                doc.normalize();
                
                // creo una nueva instancia de XPath para filtra los nodos que necesito.
                XPath xpath = XPathFactory.newInstance().newXPath();                
                
                try
                {
                    // creo lista de nodos a partir del filtro XPath                
                    listaNodos = (NodeList) xpath.compile(filtro).evaluate(doc,XPathConstants.NODESET);
                    
                }catch(XPathExpressionException e)
                {
                    System.out.println(e);
                }
                        
      
                //System.out.println(listaNodos.getLength());
                
                // por cada nodo extraigo los datos               
                for (int i=0; i < listaNodos.getLength(); i++)
                {

                    //System.out.println("NODO:" + i);
                    
                    // Etraigo los nodos hijos
                    NodeList listaNodoshijos = listaNodos.item(i).getChildNodes();
                    
                    // Son las cabeceras donde vienen los nombres
                    if (i==0)
                    {
                        
                        for (int j=0; j < listaNodoshijos.getLength(); j++)
                        {

                            String dato=listaNodoshijos.item(j).getTextContent();

                            dato= dato.trim();

                            if (!dato.isEmpty()&& !dato.isBlank())
                            {
                                System.out.println("cabecera Dato: " + dato);

                                nombreCampos.add(dato);
                            }

                        }
                        
                    }else
                    {
                        //Creo la tabla de pares
                        HashMap <ECampos,String> atributos = new HashMap<>();
                        
                        // se crea un nuevo contador porque no se borran los nodos vacios
                        int contador=0;

                        ECampos campo=null;
                        
                        //Listo los datos de los contratos 
                        for (int j=0; j < listaNodoshijos.getLength(); j++)
                        {
                            
                            // Extraigo la informacion de los campos
                            String dato=listaNodoshijos.item(j).getTextContent();

                            dato= dato.trim();
                            
                            // compruebo que no haya campos vacios
                            if (!dato.isEmpty()&& !dato.isBlank())
                            {

                                //System.out.println("NODO HIJO:" + j);

                                // extraigo el campo que es a partir del orden establecido
                                for(ECampos e: ECampos.values())
                                {
                                    if( e.getOrden() == contador) 
                                        campo = e;
                                }
                                 
                                atributos.put(campo, dato);

                                contador++;

                            }
                        }
                        
                        CContrato contrato = new CContrato(atributos);

                        datosRecogidos.add(contrato);
                    }

                    System.out.println("fin NODO:" + i);

                }
                       
                                
            }catch(org.xml.sax.SAXException | IOException e)
            {
                System.out.println(e);
            }    
            
            
            
        }catch(ParserConfigurationException | DOMException e)
        {
             System.out.println(e);
        }
        
        return datosRecogidos;
     
    }
    
    
    /**
     * Funcion para crear un xml a partir de un conjunto clave-valor
     * @param datosRecogidos arrylist de conjuntos clave-valor
     * @return 
     */
    static public File toXML(ArrayList <CContrato> datosRecogidos)
    {
        
        File fichero= new File("test.xml");
        StreamResult cadenaResultado = new StreamResult(fichero);
        final String RAIZ="CONTRATOS";
        final String ELEMENTO="CONTRATO";
        ECampos nodoOmitido = ECampos.TIPO_CONTRATO;
        
        try
        {
            
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc=db.newDocument();
            // Creo el nodo raiz            
            Element raiz=doc.createElement(RAIZ);
            doc.appendChild(raiz);
               
            //Listo los contratos            
            for (CContrato contrato: datosRecogidos)
            {
                //Extraigo los objetos Clave-Valor
                HashMap <ECampos,String> atributos = contrato.getDatos();
                
                //Creo elemento contrato
                Element nodo_contrato = doc.createElement(ELEMENTO);

                for (ECampos e : ECampos.values())
                {
                    // Control para omitir el nodo 
                    if(e != nodoOmitido)
                    {
                        //Creo el nodo con el nombre de la clave
                        Element atributo=doc.createElement(e.toString());
                        //Extraigo el valor del conjunto
                        String valor= atributos.get(e);

                        // Añado valor al nodo creado
                        atributo.appendChild(doc.createTextNode(valor));

                        //Añado nodo al nod_contrato
                        nodo_contrato.appendChild(atributo);
                        
                    }
                    
                }
                // Añado nodo_contrato  al nodo raiz
                raiz.appendChild(nodo_contrato);
            }
            
            TransformerFactory transformerFactory=TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);       

            transformer.transform(source, cadenaResultado);          
            
                    
        }catch(ParserConfigurationException | TransformerException | DOMException e)
        {
            System.out.println(e);
        }
              
        return fichero;
     
    }
    
}
