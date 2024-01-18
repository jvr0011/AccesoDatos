/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package accesojjaa;

import java.lang.annotation.Annotation;
import java.sql.Statement;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author Valdivia
 */
public class Cconexion  {
    
         
    private String cadenaConexion;

    final String USUARIO="sa";
    final String CONTRASENYA="Adminis";
    
    final String CONSULTA_SELECT=" SELECT * FROM TABLA ;";
    
    public Cconexion(String cadenaConexion)
    {
        this.cadenaConexion=cadenaConexion;
    }
    
    public void insertaDato(String dato)
    {
        System.out.println(dato);
        
        Connection conexionBd = null;
        
        try 
        {
            
            conexionBd = DriverManager.getConnection(cadenaConexion,USUARIO, CONTRASENYA);
            Statement consulta=conexionBd.createStatement();
            consulta.execute(dato);
            
        } catch (SQLException error) {
            
            System.out.println("Error al conectar con el servidor MariaDB: " + error.getMessage());
        }
        finally{
            
            try {
                conexionBd.close();
            } catch (SQLException ex) {
                Logger.getLogger(Cconexion.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
    
    
    public ArrayList<Object> getObjeto(Object obj, String tabla) throws SecurityException, NoSuchMethodException, IllegalAccessException, InvocationTargetException
    {
        Class claseObjeto = obj.getClass();
        
        ArrayList<Object> objetos = new ArrayList<>();
        
        Annotation[] anotaciones = obj.getClass().getAnnotations();
        
        for(Annotation a : anotaciones)
        {
            System.out.println(a);
        }
        
        ResultSet resultado = ejecutaSelect(tabla);
                
        Field[] fields = obj.getClass().getDeclaredFields();
        
        System.out.println("Campos leidos: " + fields.length);
        
        for(Field f : fields)
        {
            System.out.println(f);
        }
        
        if(resultado != null)
        {
                        
            try {
                
                while (resultado.next())
                {
                    
                    Object objeto;
                    
                    objeto = claseObjeto.newInstance();
                    
                    for (int i = 0; i < fields.length; i++) 
                    {

                        String typeName = fields[i].getType().getName();
                        
                        System.out.println("TypeName = "+ typeName);
                        
                        String fieldName = fields[i].getName();
                        
                        System.out.println("Busco el metodo: " + "set" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1));//, fields[i].getType());
                        
                        System.out.println("Busco el tipo: " + fields[i].getType());
                        System.out.println("Busco el tipo: " + int.class);
                        
                        Method method=null;
                        
                        try
                        {
                            method = objeto.getClass().getDeclaredMethod("set" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1), fields[i].getType()); 

                            System.out.println("Metodo elegido = " + method);
                            
                        }catch (NoSuchMethodException e){
                            System.out.println(e);
                        }

                        if ("java.lang.String".equals(typeName)) 
                        {
                            System.out.println("Creo que es String");
                            method.invoke(objeto, resultado.getString(fieldName));

                        } else if ("short".equals(typeName)) 
                        {
                            System.out.println("Creo que es un short");
                            method.invoke(objeto, resultado.getShort(fieldName));

                        } else if ("long".equals(typeName)) 
                        {
                            System.out.println("Creo que es un long");
                            method.invoke(this, resultado.getLong(fieldName));

                        } else if ("int".equals(typeName)) 
                        {
                            System.out.println("Creo que es un int");
                            method.invoke(objeto, resultado.getInt(fieldName));

                        } else if ("char".equals(typeName)) 
                        {
                            System.out.println("Creoe que es un char");
                            method.invoke(objeto, resultado.getString(fieldName).charAt(0));

                        } else if ("double".equals(typeName)) 
                        {
                            System.out.println("Creo que es un double");
                            method.invoke(objeto, resultado.getDouble(fieldName));

                        } else if ("float".equals(typeName)) 
                        {
                            System.out.println("Creo que es un float");
                            method.invoke(objeto, resultado.getFloat(fieldName));
                        }
                        
                    }
                    
                    System.out.println(objeto);
                    objetos.add(objeto);
                }
                
            } catch (SQLException | InstantiationException ex) {
                
                Logger.getLogger(Cconexion.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println(ex);
            }
        }
        
        
        return objetos;
    }
    
    
    private ResultSet ejecutaSelect(String tabla)
    {
        
        Connection conexionBd = null;
        
        ResultSet resultado = null;
        
        String cadenaConsulta = CONSULTA_SELECT;
        
        cadenaConsulta = cadenaConsulta.replace("TABLA", tabla);
        
        System.out.println(cadenaConsulta);
        
        try 
        {
            
            conexionBd = DriverManager.getConnection(cadenaConexion,USUARIO, CONTRASENYA);
            
            Statement consulta=conexionBd.createStatement();
            
            resultado = consulta.executeQuery(cadenaConsulta);
            
        } catch (SQLException error) {
            
            System.out.println("Error al conectar con el servidor MariaDB: " + error.getMessage());
        }
        finally{
            
            try {
                conexionBd.close();
            } catch (SQLException ex) {
                Logger.getLogger(Cconexion.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return resultado;
    }         
            
}
