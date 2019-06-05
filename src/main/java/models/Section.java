package models;

import java.time.LocalDateTime;

public class Section {

    private String sectionName ;
    private boolean completed;
    private LocalDateTime createdAt;
    private int id;
    private int departmentId;

    public Section(String sectionName , int departmentId) {
        this.sectionName  = sectionName ;
        this.completed = false;
        this.createdAt = LocalDateTime.now();
        this.departmentId = departmentId;
    }

    public void setSectionName (String sectionName ) { this.sectionName  = sectionName ; }
    public String getSectionName () {
        return sectionName ;
    }

    public void setCompleted(boolean completed) { this.completed = completed; }
    public boolean getCompleted(){
        return this.completed;
    }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setId(int id) { this.id = id; }
    public int getId() { return id; }

    public int getDepartmentId() { return departmentId; }
    public void setDepartmentId(int departmentId) { this.departmentId = departmentId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        if (completed != section.completed) return false;
        if (id != section.id) return false;
        if (departmentId != section.departmentId) return false;
        return sectionName  != null ? sectionName .equals(section.sectionName ) : section.sectionName  == null;
    }

    @Override
    public int hashCode() {
        int result = sectionName  != null ? sectionName .hashCode() : 0;
        result = 31 * result + (completed ? 1 : 0);
        result = 31 * result + id;
        result = 31 * result + departmentId;
        return result;
    }
}