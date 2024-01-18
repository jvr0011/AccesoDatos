/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package accesojjaa;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;
import java.util.ArrayList;
import java.util.HashMap;



/**
 *
 * @author Medac
 */
public class ConexionBd {


    // TODO code application logic here

    //Realizamos la conexión con el servidor MySQL/MariaDB
    //Con los datos de conexión: dirección, puerto, usuario y contraseña

    private String cadenaConexion;
    
    final String BD="JJAA";
    final String USUARIO="postgres";
    final String CONTRASENYA="Adminis";
    final String TABLA="contratos_administracion";
    final String SELECT="SELECT * FROM TABLA ;";
    
    private Connection conexion = null;
    
    
    public ConexionBd(String cadenaConexion)
    {
        this.cadenaConexion=cadenaConexion;
    }
    
       

    private void conecta(){

        try {
            
            // conexion con la base de datos
            conexion = DriverManager.getConnection(cadenaConexion + BD,USUARIO, CONTRASENYA);
            
        } catch (SQLException error) {

            System.out.println("Error al conectar con el servidor: " + error.getMessage());
        }

    }


    public ArrayList <CContrato> extraeContratos()
    {
        
        ArrayList <CContrato> datosRecogidos = new ArrayList();
        
        conecta();
        
        ResultSet resultado=null;

        try
        {
            
            String cadena_sql=SELECT;
            
            cadena_sql = cadena_sql.replace("TABLA", TABLA);
            
            //Creo consulta
            Statement consulta=conexion.createStatement();
            
            //Ejecuto consulta
            resultado=consulta.executeQuery(cadena_sql);
            
            //Extraigo los metadatos
            ResultSetMetaData metadatos=resultado.getMetaData();

            for (int i=1; i<=metadatos.getColumnCount();i++)
            {

                System.out.println(metadatos.getColumnType(i));

            }


            if (metadatos.getColumnCount()>0)
            {
                System.out.println("Hay  " + metadatos.getColumnCount() + " columnas");
                
                CContrato objContrato = null;
                
                // Extraigo los resultados
                while( resultado.next())
                {
                    
                                        
                    
                    HashMap <ECampos,String> contrato = new HashMap<>();

                    if (metadatos.getColumnCount()>0)
                    {

                        //busco lo metadatos por orden
                        for (int i=1; i<=metadatos.getColumnCount();i++)
                        {
                            //System.out.print(metadatos.getColumnName(i)+ ": ");
                            ECampos campo=null;
                            
                            
                            for(ECampos e: ECampos.values())
                            {
                                //Busco el nombre de lacolumna para localizar los pares
                                if(metadatos.getColumnName(i).equals(e.toString())) 
                                    campo = e;
                            }

                            String nombreColumna = metadatos.getColumnName(i);
                            //Extraigo el valor de la columna de la tabla
                            String dato = resultado.getString(i);

                            //Creo el par clave valor
                            contrato.put(campo, dato);
                            //consumo.dataToObject(nombreColumna , dato);

                        }
                        
                        objContrato = new CContrato(contrato);


                    }

                    datosRecogidos.add(objContrato);

                }


            }


        }catch (SQLException error)
        {
            System.out.println("Error en el select: " + error.getMessage());
        }
        finally
        {
            if(resultado!=null)
            {
                try {
                    
                    // cierro la consulta
                    resultado.close();
                    
                } catch (SQLException ex) {
                    Logger.getLogger(ConexionBd.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
            if(conexion!=null)
            {

                try {
                    
                    // cierro conexion
                    conexion.close();
                    
                    //borro la memoria de la conexion
                    conexion=null;
                    
                } catch (SQLException ex) {
                    
                    Logger.getLogger(ConexionBd.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }
        
        return datosRecogidos;

    }
    
    
           
    void insertaContrato(String cadenaInsert)
    {
        // conecto base de datos
        conecta();
        
        System.out.println(cadenaInsert);
        
        ResultSet resultado=null;
                
        try
        {
            // creo una clase consulta 
            Statement consulta=conexion.createStatement();
            
            // Inserto los contratos
            resultado=consulta.executeQuery(cadenaInsert);
        
        
        }catch (SQLException error)
        {
            System.out.println("Error en el select: " + error.getMessage());
        }
        finally
        {
            // Si funciona la conexion cierro la conexion
            if(resultado!=null)
            {
                try {
                    
                    resultado.close();
                    
                                       
                } catch (SQLException ex) {
                    Logger.getLogger(ConexionBd.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
            
            if(conexion!=null)
            {

                try {
                    
                    // cierro conexion
                    conexion.close();
                    
                    //borro la memoria de la conexion
                    conexion=null;
                    
                } catch (SQLException ex) {
                    
                    Logger.getLogger(ConexionBd.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
            
        }
        
    }
    
    
    
    
    static Object dataToObject(int tipo, String dato)
    {
        dato = dato.trim();
        System.out.println("Dato recibido: "+ dato);
        Object resultado=null;
        switch(tipo){
            case java.sql.Types.TINYINT:
                
                System.out.println("TINYINT");
                
                resultado=toInt(dato);
                break;
                
            case java.sql.Types.DOUBLE:
                
                System.out.println("DOUBLE");
                
                resultado=toDouble(dato);
                break;
                
            case java.sql.Types.VARCHAR:
                
                System.out.println("VARCHAR");
                
                resultado=dato;
                break;
                
            case java.sql.Types.INTEGER:
                
                System.out.println("INTEGER");
                
                resultado=toInt(dato);
                break;
                
            case java.sql.Types.FLOAT:
                resultado=toDouble(dato);
                System.out.println("FLOAT");
                break;
                
            case java.sql.Types.TIMESTAMP:
                DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                // recibo en este formato '2021-05-11 01:00:00'
                System.out.println("TIMESTAMP");
                
                resultado=(LocalDateTime) LocalDateTime.parse(dato, formato);
                break;
                
            default:
                resultado=dato;
                System.out.println("DESCONOCIDO");
                                
                
        }
        
        return resultado;
        
    }
    
    static Integer toInt(String dato)
    {
        return Integer.getInteger(dato);
    }
    
    static Double toDouble(String dato)
    {
        return Double.valueOf(dato);
    }
    
    static LocalDateTime toDateTime(String dato)
    {
        return LocalDateTime.parse(dato,ISO_LOCAL_DATE);
    }
    
    
}
