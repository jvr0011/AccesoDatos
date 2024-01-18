/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package accesojjaa;

/**
 *
 * @author Medac
 */
public enum ETipoContrato {
    
    MENOR("Menor"),
    ADJUDICACION_DIRECTA("Adjudicación directa"),
    BASADO_MARCO("Basado en acuerdo marco"),
    ABIERTO_SUPERSIMPLIFICADO("ABIERTO SUPERSIMPLIFICADO (SIN MESAS)");
    
    
    private final String texto;
    
    ETipoContrato(String texto)
    {
        this.texto=texto;
    }
            
    
    
    
}
