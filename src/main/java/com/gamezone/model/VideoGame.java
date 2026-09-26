package com.gamezone.model;

/**
 * Represents a video game product extending the base Product class.
 * @author JESUS
 */
public class VideoGame extends Product {
    // Video game specific attributes
    private String platform;
    private String genre;
    private String ageRating;
    
    /**
     * Default constructor.
     */
    public VideoGame() {
    }
    
    /**
     * Parameterized constructor initializing base product attributes and video game properties.
     * 
     * @param platform the platform the game runs on
     * @param genre the genre of the game
     * @param ageRating the age restriction rating
     * @param id unique product identifier
     * @param title video game title
     * @param price unit price
     * @param stock available stock quantity
     */
    public VideoGame(String platform, String genre, String ageRating, String id, String title, double price, int stock) {
        super(id, title, price, stock);
        this.platform = platform;
        this.genre = genre;
        this.ageRating = ageRating;
    }

    // Getters and setters
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
 
    /**
     * Returns a formatted full description of the video game.
     * 
     * @return string containing all details of the video game
     */
    @Override
    public String getFullDescription(){
       return String.format("VideoGame: [ID: %s, Title: %s, Price: $%.2f, Stock: %d, Platform: %s, Genre: %s, Age Rating: %s]",
               getId(), getTitle(), getPrice(), getStock(), platform, genre, ageRating);
    } 
    
}