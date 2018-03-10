package models;

import play.data.validation.Constraints;

/**
 * Keyword form
 * @author Adrien Poupa
 */
public class Keyword {

    @Constraints.Required
    protected String keyword;

    /**
     * Keyword entered by the user
     * @return String keyword
     */
    public String getKeyword() {
        return keyword;
    }

    /**
     * Set the keyword
     * @param keyword String keyword
     */
    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}
