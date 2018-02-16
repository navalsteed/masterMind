/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mastermindfxml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Elev
 */
public class Code {

    /**
     * @return the codeList
     */
    public List<String> getCodeList() {
        return codeList;
    }

    /**
     * @param codeList the codeList to set
     */
    public void setCodeList(List<String> codeList) {
        this.codeList = codeList;

    }

    private List<String> codeList;

    public List generateCode() {
        String[] colm = {"redButton", "orangeButton", "yellowButton", "greenButton", "blueButton", "purpleButton"};
        List<String> codeList = new ArrayList();
        for (String s : colm) {
            codeList.add(s);
        }
        Collections.shuffle(codeList);
        codeList.remove(0);
        codeList.remove(0);
        System.out.println("code list generated:" + codeList);
        return codeList;
    }

    public String substringCodelist() {
        StringBuilder codeStringbuilder = new StringBuilder("");
        //String subString = null;
        this.codeList.forEach((s) -> {
            codeStringbuilder.append(Character.toUpperCase(s.charAt(0)));
        });
        return codeStringbuilder.toString();
    }
}
