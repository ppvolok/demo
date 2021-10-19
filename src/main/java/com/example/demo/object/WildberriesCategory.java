package com.example.demo.object;

public class WildberriesCategory {
    private long id; // не может быть null
    private Long parentId; // а этот может
    private String name;

    public WildberriesCategory(long id, Long parentId, String name) {
        this.id = id;
        this.parentId = parentId;
        this.name = name;
    }

    @Override
    public String toString() {
        return "WildberriesCategory{" +
                "id=" + id +
                ", parentId=" + parentId +
                ", name='" + name + '\'' +
                '}';
    }
}
