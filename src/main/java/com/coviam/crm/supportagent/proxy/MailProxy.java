package com.coviam.crm.supportagent.proxy;

import com.coviam.crm.supportagent.dto.MailDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("/ip/")
public interface MailProxy {

    @PostMapping("/sendMail")
    void sendMail(@RequestBody MailDTO mailDTO);
}
