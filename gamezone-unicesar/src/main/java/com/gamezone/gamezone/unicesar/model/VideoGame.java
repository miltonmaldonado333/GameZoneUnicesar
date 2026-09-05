
package com.gamezone.gamezone.unicesar.model;
/**
 * a class representing a VideoGame Product
 * @author JESUS
 */

public class VideoGame extends Product {
   private String platform;
   private String genre;
   private String ageRating;

    public VideoGame() {
    }

    public VideoGame(String platform, String genre, String ageRating) {
        this.platform = platform;
        this.genre = genre;
        this.ageRating = ageRating;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getAgeRating() {
        return ageRating;
    }

    public void setAgeRating(String ageRating) {
        this.ageRating = ageRating;
    }
   
    @Override
    public String getFullDescription(){
        return String.format("Console: [ID %s, Title: %s, price: $%.2f, Stock: %d, Brand: %s,Model: %s, Generation: %s] ",getId(), getTitle(), getPrice(), getStock(), platform, genre, ageRating);
    }
   
    
    
}
