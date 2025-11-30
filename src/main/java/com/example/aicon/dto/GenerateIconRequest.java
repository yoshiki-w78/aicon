package com.example.aicon.dto;

public class GenerateIconRequest {
    private String prompt;
    private Integer count;
    private String size;
    private String style; // 今は未使用でもOK

    public String getPrompt() { return prompt; }
    public void setPrompt(String prompt) { this.prompt = prompt; }

    public Integer getCount() { return count; }
    public void setCount(Integer count) { this.count = count; }

    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }

    public String getStyle() { return style; }
    public void setStyle(String style) { this.style = style; }
}