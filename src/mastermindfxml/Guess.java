/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mastermindfxml;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Elev
 */
public class Guess {

    private List<String> myGuess;

    Guess() {
        myGuess = new ArrayList();
    }

    /**
     * @return the myGuess
     */
    public List<String> getGuess() {
        return myGuess;
    }

    /**
     * @param myGuess the myGuess to set
     */
    public void setGuess(String myGuess) {
        this.myGuess.add(myGuess);
    }

}
