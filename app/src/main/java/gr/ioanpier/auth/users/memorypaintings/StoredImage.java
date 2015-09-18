package gr.ioanpier.auth.users.memorypaintings;

/**
 * Created by Ioannis on 18/9/2015.
 */
public class StoredImage {

    public final String name;
    public final String absolute_path;

    public StoredImage(String name, String absolute_path){
        this.name = name;
        this.absolute_path = absolute_path;
    }

    @Override
    public int hashCode(){
        return name.hashCode();
    }

    @Override
    public boolean equals(Object img) {
        return img instanceof StoredImage && name.equals(((StoredImage) img).name);

    }
}
