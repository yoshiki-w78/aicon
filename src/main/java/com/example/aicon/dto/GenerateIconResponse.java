package com.example.aicon.dto;

import java.util.ArrayList;
import java.util.List;

public class GenerateIconResponse {
    private String prompt;
    private List<IconData> icons = new ArrayList<>();
    private String error;

    public String getPrompt() { return prompt; }
    public void setPrompt(String prompt) { this.prompt = prompt; }

    public List<IconData> getIcons() { return icons; }
    public void setIcons(List<IconData> icons) { this.icons = icons; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }

    public static class IconData {
        private String id;
        private String imageUrl;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    }
}