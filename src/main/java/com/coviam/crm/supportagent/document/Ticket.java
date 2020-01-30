package com.coviam.crm.supportagent.document;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.GeneratedValue;
import java.util.List;

@Getter
@Setter
@Document(collection = "Ticket")
public class Ticket {

    @Id
    private String ticketId;//same as postId
//    private String postId;
    private String postDesc;
    private String postImageUrl;
    private String postVideoUrl;
    private String postUserId;
    private int countOfDislike;
    private String status;
    private String createdTime;
    private String updatedTime;

    private long mailCount;
    private String source;
    private List<String> comments;
    private List<String> images;
    private List<String> video;
    private List<String> docs;
    private List<String> dislikeIds;

}
