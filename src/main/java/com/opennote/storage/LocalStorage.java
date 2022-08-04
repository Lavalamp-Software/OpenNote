/*******************************************************

* Copyright (C) 2021-2022 OpenNote, shabman (avia.shabbyman@gmail.com)

* You may not redistribute this file in exchange for payment

*******************************************************/
package com.opennote.storage;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bson.Document;

/**
 *
 * @author shabman
 */
public class LocalStorage {

    private final String dbName;
    private final String collectName;

    // TODO: Download MongoDB and start a server
    private final MongoClient mongoClient = new MongoClient();

    public LocalStorage(String dbName, String collectName) {
        this.dbName = dbName;
        this.collectName = collectName;
    }
    
    public boolean saveData(String id, String[] dict) {
        boolean result;
        try {
            List<String> mainInfo = new ArrayList<>(Arrays.asList(dict));

            Document data = new Document();
            data.put("_id", id);
            mainInfo.forEach(val -> data.put(convert(val)[0], (String) convert(val)[convert(val).length - 1]));
            
            MongoDatabase database = mongoClient.getDatabase(this.dbName);
            MongoCollection<Document> collection = database.getCollection(this.collectName);
            collection.insertOne(data);
            result = true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            result = false;
        }
        return result;
    }
    
    public Object findData(String id, String key) {
        Document result = null;
        try {
            MongoDatabase database = mongoClient.getDatabase(this.dbName);
            MongoCollection<Document> collection = database.getCollection(this.collectName);

            Document query = new Document("_id", id);
            result = collection.find(query).iterator().next();
            MongoCursor<Document> cursor = collection.find(query).iterator();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        if (result == null) {
            return "NULL";
        } else {
            return result.getString(key);
        }
    }
    
    public boolean updateData(String id, String[] sets) {
        boolean result;
        Document res;
        try {
            MongoDatabase database = mongoClient.getDatabase(this.dbName);
            MongoCollection<Document> collection = database.getCollection(this.collectName);

            Document query = new Document("_id", id);
            res = collection.find(query).iterator().next();
            MongoCursor<Document> cursor = collection.find(query).iterator();
            Document one = new Document()
                    .append("_id", id);
            Document newData = new Document()
                .append(convert(sets[0])[0], convert(sets[0])[1]);
            Document updateData = new Document()
                    .append("$set", newData);
            collection.updateOne(one, updateData);
            result = true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            result = false;
        }
        return result;
    }
    
    public boolean deleteData(String id) {
        boolean result;
        Document res;
        try {
            MongoDatabase database = mongoClient.getDatabase(this.dbName);
            MongoCollection<Document> collection = database.getCollection(this.collectName);

            Document query = new Document("_id", id);
            res = collection.find(query).iterator().next();
            MongoCursor<Document> cursor = collection.find(query).iterator();
            Document del = new Document()
                    .append("_id", id);
            collection.deleteOne(del);
            result = true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            result = false;
        }
        return result;
    }
    
    private String[] convert(String word) {
        return word.split("\s");
    }
}
