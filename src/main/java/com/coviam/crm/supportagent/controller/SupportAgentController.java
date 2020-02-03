package com.coviam.crm.supportagent.controller;

import com.coviam.crm.supportagent.dto.*;
import com.coviam.crm.supportagent.document.SaTicket;
import com.coviam.crm.supportagent.document.SupportAgent;
import com.coviam.crm.supportagent.document.Ticket;
import com.coviam.crm.supportagent.service.SupportAgentService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
public class SupportAgentController {

    @Autowired
    SupportAgentService supportAgentService;

    //request header
    @PostMapping("/createTicket")
    public ResponseEntity<String> createTicket(@RequestHeader(value = "userId") String userId,@RequestBody PostDTO postDTO){
//        postDTO.setUserId(userId);
        postDTO.setDislikedId(userId);
        return new ResponseEntity<>(supportAgentService.createTicket(postDTO),HttpStatus.OK);
    }

    @PostMapping("/addSupportAgent")
    public ResponseEntity<String> addSupportAgent(@RequestBody SupportAgentDTO supportAgentDTO){
        SupportAgent supportAgent = new SupportAgent();
        BeanUtils.copyProperties(supportAgentDTO,supportAgent);
        supportAgentService.addSupportAgent(supportAgent);

        return new ResponseEntity<>("Successfully added",HttpStatus.OK);
    }

    @DeleteMapping("/deleteSupportAgent/{supportAgentId}")
    public ResponseEntity<String> deleteSupportAgent(@PathVariable("supportAgentId") String supportAgentId){
        supportAgentService.deleteSupportAgent(supportAgentId);
        return new ResponseEntity<>("Successfully deleted",HttpStatus.OK);
    }

    @PostMapping("/assignTicket")
    public ResponseEntity<String> assignTicket(@RequestBody SaTicketDTO saTicketDTO){
        SaTicket saTicket = new SaTicket();
        BeanUtils.copyProperties(saTicketDTO,saTicket);
        supportAgentService.assignTicket(saTicket);
        return new ResponseEntity<>("Successfully assigned",HttpStatus.OK);
    }

    //request header
    @GetMapping("/getCountOfTickets/")
    public ResponseEntity<NoTicketsDTO> resolvedTickets(@RequestHeader(value = "userId") String userId){
        System.out.println(userId);
        return new ResponseEntity<>(supportAgentService.resolvedTickets(userId),HttpStatus.OK);
    }


    @GetMapping("/getTicketList")
    public ResponseEntity<List<Ticket>> getTicketList(){
        return new ResponseEntity<>(supportAgentService.getTicketList(),HttpStatus.OK);
    }

    @GetMapping("/getSAList")
    public ResponseEntity<List<SupportAgent>> getSAList(){
        return new ResponseEntity<>(supportAgentService.getSAList(),HttpStatus.OK);
    }

    @GetMapping("/getTicketById/{ticketId}")
    public ResponseEntity<Ticket> getTicketById(@PathVariable("ticketId") String ticketId){
        return new ResponseEntity<>(supportAgentService.getTicketById(ticketId),HttpStatus.OK);
    }


    @PostMapping("/uploadComments")
    public ResponseEntity<Ticket> uploadComments(@RequestBody CommentDTO commentDTO){
        return new ResponseEntity<>(supportAgentService.uploadComments(commentDTO),HttpStatus.OK);
    }

    //request header
    @GetMapping("/getPendingTicketsBySAId/")
    public ResponseEntity<List<Ticket>> getPendingTicketsBySAId(@RequestHeader(value = "userId") String userId){
        System.out.println(userId+" hiiiiiiii");
        System.out.println(supportAgentService.getPendingTicketsBySAId(userId));
        return new ResponseEntity<>(supportAgentService.getPendingTicketsBySAId(userId),HttpStatus.OK);
    }

    @GetMapping("/getResolvedTicketsBySAId/")
    public ResponseEntity<List<Ticket>> getResolvedTicketsBySAId(@RequestHeader(value = "userId") String userId){
        System.out.println(userId+" hiiiiiiii");
        System.out.println(supportAgentService.getResolvedTicketsBySAId(userId));
        return new ResponseEntity<>(supportAgentService.getResolvedTicketsBySAId(userId),HttpStatus.OK);
    }


    @GetMapping("/closeTicket/{ticketId}")
    public ResponseEntity<String> closeTicket(@PathVariable("ticketId") String ticketId){
        return new ResponseEntity<>(supportAgentService.closeTicket(ticketId),HttpStatus.OK);
    }

}
