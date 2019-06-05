package dao;

import models.Section;
import org.sql2o.*;
import java.util.List;

public class Sql2OSectionDao implements SectionDao { //implementing our interface

    private final Sql2o sql2o;

    public Sql2OSectionDao(Sql2o sql2o){
        this.sql2o = sql2o; //making the sql2o object available everywhere so we can call methods in it
    }

    @Override
    public void add(Section section) {
        String sql = "INSERT INTO sections (sectionName , departmentId) VALUES (:sectionName , :departmentId)"; //raw sql
        try(Connection con = sql2o.open()){ //try to open a connection
            int id = (int) con.createQuery(sql, true) //make a new variable
                    .bind(section)
                    .executeUpdate() //run it all
                    .getKey(); //int id is now the row number (row “key”) of db
            section.setId(id); //update object to set id now from database
        } catch (Sql2oException ex) {
            System.out.println(ex); //oops we have an error!
        }
    }

    @Override
    public List<Section> getAll() {
        try(Connection con = sql2o.open()){
             return con.createQuery("SELECT * FROM sections") //raw sql
                    .executeAndFetch(Section.class); //fetch a list
        }
    }


    @Override
    public Section findById(int id) {
        try(Connection con = sql2o.open()){
            return con.createQuery("SELECT * FROM sections WHERE id = :id")
                    .addParameter("id", id) //key/value pair, key must match above
                    .executeAndFetchFirst(Section.class); //fetch an individual item
        }
    }

    @Override
    public void update(int id, String newSectionName , int newDepartmentId){
        String sql = "UPDATE sections SET (sectionName , departmentId) = (:sectionName , :departmentId) WHERE id=:id"; //raw sql
        try(Connection con = sql2o.open()){
            con.createQuery(sql)
                    .addParameter("sectionName ", newSectionName )
                    .addParameter("departmentId", newDepartmentId)
                    .addParameter("id", id)
                    .executeUpdate();
        } catch (Sql2oException ex) {
            System.out.println(ex);
        }
    }

    @Override
    public void deleteById(int id) {
        String sql = "DELETE from sections WHERE id=:id";
        try (Connection con = sql2o.open()) {
            con.createQuery(sql)
                    .addParameter("id", id)
                    .executeUpdate();
        } catch (Sql2oException ex){
            System.out.println(ex);
        }
    }

    @Override
    public void clearAllSections() {
        String sql = "DELETE from sections";
        try (Connection con = sql2o.open()) {
            con.createQuery(sql)
                    .executeUpdate();
        } catch (Sql2oException ex){
            System.out.println(ex);
        }
    }
}