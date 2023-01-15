/*
 *
 *  Copyright (c) 2018-2020 Givantha Kalansuriya, This source is a part of
 *   Staxrt - sample application source code.
 *   http://staxrt.com
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package com.example.demo.model;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;


@Entity
@Table(name = "book")
public class Book {
    @Id
    private String db_Id;
    private String isbn;
    private String title;
    private String author;
    private String genre;
    
    
    public void setIsbn(String isbn) {
    	this.isbn=isbn;
    }
    
    public void setDbId(String db_Id) {
    	this.db_Id = db_Id;
    }
    
    public void setTitle(String title) {
    	this.title=title;
    }
    
    public void setAuthor(String author) {
    	this.author=author;
    }
    
    
    public void setGenre(String genre) {
    	this.genre=genre;
    }
    
    //get
    public String getDbId() {
    	return db_Id;
    }
    
    public String getIsbn() {
    	return isbn;
    }
    
    public String getTitle() {
    	return title;
    }
    public String getAuthor() {
    	return author;
    }
    public String getGenre() {
    	return genre;
    }
    
    @Override
    public String toString() {
        return "Book{" +
                "isbn=" + isbn +
                ", Title='" + title + '\'' +
                ", Author='" + author + '\'' +
                ", Genre='" + genre +
                '}';
    }

    
}