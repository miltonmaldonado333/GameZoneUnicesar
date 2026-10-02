package com.gamezone.model;

/**
 * Model class representing a video game product.
 * Extends the base {@link Product} class with game-specific properties.
 */
public class VideoGame extends Product {

    private String platform;
    private String genre;
    private String ageRating;

    public VideoGame() {
    }

    public VideoGame(String platform, String genre, String ageRating, String id, String title, double price, int stock) {
        super(id, title, price, stock);
        this.platform = platform;
        this.genre = genre;
        this.ageRating = ageRating;
    }

    public String getPlatform() { return platform; }
    public void setPlatform(String platform) { this.platform = platform; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public String getAgeRating() { return ageRating; }
    public void setAgeRating(String ageRating) { this.ageRating = ageRating; }

    /**
     * Returns the category identifier for video game products.
     *
     * @return the category name "VideoGame"
     */
    @Override
    public String getCategory() {
        return "VideoGame";
    }

    @Override
    public String getFullDescription() {
        return String.format("VideoGame: [ID: %s, Title: %s, Price: $%.2f, Stock: %d, Platform: %s, Genre: %s, Rating: %s]",
                getId(), getTitle(), getPrice(), getStock(), platform, genre, ageRating);
    }
}