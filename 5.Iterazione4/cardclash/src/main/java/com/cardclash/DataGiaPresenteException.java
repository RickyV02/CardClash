
package com.cardclash;

public class DataGiaPresenteException extends Exception {
    public DataGiaPresenteException() {
        super("Esiste già un torneo con la data selezionata.");
    }
}
