package com.coviam.crm.supportagent.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostDTO {

    private String postId;
    private String postDescription;
    private String postImageUrl;
    private String postVideoUrl;
    private String userId;
    private int counterOfDislikes;
    private String dislikedId;
    private String source;
}
