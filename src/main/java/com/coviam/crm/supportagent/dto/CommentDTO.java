package com.coviam.crm.supportagent.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CommentDTO {

    private String ticketId;
    private String comments;
    private String images;
    private String video;
    private String docs;
}
