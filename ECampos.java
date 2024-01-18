/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package accesojjaa;

/**
 *
 * @author Valdivia
 */
public enum ECampos {
    
    NIF(0,"NIF"),
    ADJUDICATARIO(1,"ADJUDICATARIO"),
    OBJETO_GENÉRICO(2,"OBJETO_GENÉRICO"),
    OBJETO(3,"OBJETO"),
    FECHA_ADJUDICACIÓN(4,"FECHA_DE_ADJUDICACION"),
    IMPORTE(5,"IMPORTE"),
    PROVEEDORES_CONSULTADOS(6,"PROVEEDORES_CONSULTADOS"),
    TIPO_CONTRATO(7,"TIPO_DE_CONTRATO"),
    S_N(8,"N_C");
    
    //Nombre que tiene en la base de datos
    private final String nombre;
    //Orden de los campos en el xml
    private final int orden;
    
    ECampos(int orden,String nombre)
    {
        this.orden=orden;
        this.nombre=nombre;
    }
    
    public int getOrden()
    {
        return this.orden;
    }
    
    @Override
    public String toString()
    {
        return nombre;
    } 
}
