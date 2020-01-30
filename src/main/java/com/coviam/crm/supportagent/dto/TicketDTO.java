package com.coviam.crm.supportagent.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TicketDTO {

    private String ticketId;
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
