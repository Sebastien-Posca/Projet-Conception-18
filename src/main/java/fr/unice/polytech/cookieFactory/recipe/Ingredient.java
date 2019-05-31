package fr.unice.polytech.cookieFactory.recipe;

public class Ingredient {

    private String type;
    private String name;

    public Ingredient(String type, String name) {
        this.type = type;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    /**
     * @param o
     * @return true if both ingredients have the same name and type (must be upper case)
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Ingredient that = (Ingredient) o;

        return this.getName().equals(that.getName().toUpperCase()) && this.getType().equals(that.getType().toUpperCase());
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}
