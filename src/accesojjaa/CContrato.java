/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package accesojjaa;

import java.util.HashMap;

/**
 *
 * @author Medac
 */

public class CContrato{
    
    final String CADENA_INSERT = "INSERT INTO TABLA (CAMPOS) VALUES (VALORES);";
    
    HashMap <ECampos,String> datos;
    
    CContrato(HashMap <ECampos,String> datos)
    {
        this.datos=new HashMap <ECampos,String> (datos);
    }

    public HashMap<ECampos, String> getDatos() {
        return datos;
    }
    
    public String toInsertString(String tabla)
    {
        String cadena=CADENA_INSERT;
        
        String campos = "";
        String valores = "";
        
        // recorro todos los campos por orden
        for (int i=0; i < this.datos.size(); i++ )
        {
            
            for (ECampos e : ECampos.values())
            {
                // averiguo el campo que le toca y si está contenido
                if( e.getOrden() == i && this.datos.containsKey(e)) 
                {
                    // funcion que me permite omitir la coma en el ultimo campo
                    char coma = (i==this.datos.size()-1) ? ' ' : ',';
                    campos += e.toString() + coma;
                    valores += "'" + this.datos.get(e) + "'" + coma; 
                }
            }
        }
        // pongo el nombre de la tabla
        cadena = cadena.replace("TABLA", tabla);
        // escribo el nombre de los campos
        cadena = cadena.replace("CAMPOS", campos);
        // escribo el valore de los campos
        cadena = cadena.replace("VALORES", valores);
        
        return cadena; 
    }
    
}

