package com.coviam.crm.supportagent.service.impl;

import com.coviam.crm.supportagent.document.SaTicket;
import com.coviam.crm.supportagent.document.SupportAgent;
import com.coviam.crm.supportagent.document.Ticket;
import com.coviam.crm.supportagent.dto.CommentDTO;
import com.coviam.crm.supportagent.dto.MailDTO;
import com.coviam.crm.supportagent.dto.PostDTO;
import com.coviam.crm.supportagent.repository.SaTicketRespository;
import com.coviam.crm.supportagent.repository.SupportAgentRepository;
import com.coviam.crm.supportagent.repository.TicketRepository;
import com.coviam.crm.supportagent.service.SupportAgentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Service
@Component
public class SupportAgentServiceImpl implements SupportAgentService {

    @Autowired
    SupportAgentRepository supportAgentRepository;

    @Autowired
    TicketRepository ticketRepository;

    @Autowired
    SaTicketRespository saTicketRespository;

    @Autowired
    KafkaTemplate<String,String> kafkaTemplate;

    @Override
    public SupportAgent addSupportAgent(SupportAgent supportAgent) {
        return supportAgentRepository.save(supportAgent);
    }

    @Override
    public void deleteSupportAgent(String sId) {
        supportAgentRepository.deleteById(sId);
    }

    @Override
    public List<Ticket> getTicketList() {

        List<Ticket> ticketList = new ArrayList<>();
        ticketRepository.findAll().stream().forEach(ticket -> {
            if ((ticket.getStatus()).equals("open")){
                ticketList.add(ticket);
            }
        });

        return ticketList;
    }

    @Override
    public SaTicket assignTicket(SaTicket saTicket) {
        Ticket ticket = new Ticket();
        ticket = ticketRepository.findById(saTicket.getTicketId()).get();

        SupportAgent supportAgent = supportAgentRepository.findById(saTicket.getSupportAgentId()).get();

        supportAgent.setTicketsPending(supportAgent.getTicketsPending() + 1);

        supportAgentRepository.save(supportAgent);

        ticket.setStatus("in progress");

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        ticket.setUpdatedTime(dtf.format(now));

        ticketRepository.save(ticket);

        return saTicketRespository.save(saTicket);
    }

    @Override
    public List<SupportAgent> getSAList() {
        return (List<SupportAgent>) supportAgentRepository.findAll();
    }

    @Override
    public Ticket getTicketById(String ticketId) {
        return ticketRepository.findById(ticketId).get();
    }

    @Override
    public Ticket uploadComments(CommentDTO commentDTO) {
        Ticket ticket = ticketRepository.findById(commentDTO.getTicketId()).get();
        List<String> commentsList =  ticket.getComments();
        commentsList.add(commentDTO.getComments());

        List<String> imageList =  ticket.getImages();
        imageList.add(commentDTO.getImages());

        List<String> videoList =  ticket.getVideo();
        videoList.add(commentDTO.getVideo());

        List<String> docsList =  ticket.getDocs();
        docsList.add(commentDTO.getDocs());

        ticket.setComments(commentsList);
        ticket.setImages(imageList);
        ticket.setVideo(videoList);
        ticket.setDocs(docsList);

        return ticketRepository.save(ticket);
    }

    @Override
    public String closeTicket(String ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId).get();
        ticket.setStatus("closed");
        ticketRepository.save(ticket);

        List<SaTicket> saTickets = saTicketRespository.findAll();

        AtomicReference<SaTicket> saTicket1 = new AtomicReference<>(new SaTicket());

        saTickets.stream().forEach(saTicket -> {
            if((saTicket.getTicketId()).equals(ticketId)){
                saTicket1.set(saTicket);
            }
        });


        String saId = saTicket1.get().getSupportAgentId();
        SupportAgent supportAgent = supportAgentRepository.findById(saId).get();

        supportAgent.setTicketsResolved(supportAgent.getTicketsResolved() + 1);
        supportAgent.setTicketsPending(supportAgent.getTicketsPending() - 1);

        supportAgentRepository.save(supportAgent);

//        saTicketRespository.delete(saTicket1.get());

        return "Ticket Closed Successfully";
    }

    @Override
    public List<Ticket> getTicketsBySAId(String supportAgentId) {
        List<SaTicket> saTickets = saTicketRespository.findAll();
        List<Ticket> tickets = new ArrayList<>();

        saTickets.stream().forEach(saTicket -> {
            if ((saTicket.getSupportAgentId()).equals(supportAgentId)){
                Ticket ticket = new Ticket();
                ticket = ticketRepository.findById(saTicket.getTicketId()).get();
                tickets.add(ticket);
            }
        });

        return tickets;
    }

    @Override
    public String createTicket(PostDTO postDTO) {
        if(ticketRepository.existsById(postDTO.getPostId())){
            Ticket ticket = ticketRepository.findById(postDTO.getPostId()).get();
            ticket.setCountOfDislike(postDTO.getCounterOfDislikes());

            List<String> dislikeIdsList= ticket.getDislikeIds();
            dislikeIdsList.add(postDTO.getDislikedId());
            ticket.setDislikeIds(dislikeIdsList);

            ticketRepository.save(ticket);



        }
        else {


            Ticket ticket = new Ticket();
            ticket.setTicketId(postDTO.getPostId());
            ticket.setPostDesc(postDTO.getPostDescription());
            ticket.setPostImageUrl(postDTO.getPostImageUrl());
            ticket.setPostVideoUrl(postDTO.getPostVideoUrl());
            ticket.setPostUserId(postDTO.getUserId());
            ticket.setCountOfDislike(postDTO.getCounterOfDislikes());
            ticket.setStatus("open");
            ticket.setMailCount(0);
            ticket.setSource(postDTO.getSource());
            List<String> dislikeIds = ticket.getDislikeIds();
            dislikeIds.add(postDTO.getDislikedId());
            ticket.setDislikeIds(dislikeIds);

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            ticket.setCreatedTime(dtf.format(now));
            ticket.setUpdatedTime(dtf.format(now));

            ticketRepository.save(ticket);

        }

        return "Ticket created successfully";

    }

    @Scheduled(fixedDelay = 600000)
    private void sendMails(){
        List<Ticket> ticketList = ticketRepository.findAll();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");

        ticketList.stream().forEach(ticket -> {
            if ((ticket.getStatus()).equals("open")){
                LocalDateTime now = LocalDateTime.now();

                if (dtf.format(now.minusMinutes(15)).compareTo(ticket.getUpdatedTime()) > 0){

                    MailDTO mailDTO = MailTemplate.mail(ticket);

                    //send mailDTO

                    if (ticket.getMailCount() < 4)
                        ticket.setMailCount(ticket.getMailCount() + 1);

                    else
                        ticket.setStatus("discarded");


                }
            }else if((ticket.getStatus()).equals("in progress")){
                LocalDateTime now = LocalDateTime.now();

                if (dtf.format(now.minusMinutes(15)).compareTo(ticket.getUpdatedTime()) > 0){
                    //send mail
                    if(ticket.getMailCount()==0){
                        //send mail1
                        ticket.setMailCount(ticket.getMailCount() + 1);
                    }
                    else if(ticket.getMailCount()==1){
                        //send mail2
                        ticket.setMailCount(ticket.getMailCount() + 1);
                    }
                    else if(ticket.getMailCount()==2){
                        //send mail3
                        ticket.setMailCount(ticket.getMailCount() + 1);
                    }
                    else if(ticket.getMailCount()==3){
                        //send mail4
                        ticket.setMailCount(ticket.getMailCount() + 1);
                    }
                    else if(ticket.getMailCount()==4) {
                        //send mail5
                        ticket.setMailCount(0);
                        ticket.setStatus("open");
                    }

                }
            }
        });


        ObjectMapper objectMapper = new ObjectMapper();
        MailDTO mailDTO = new MailDTO();

        try {
            kafkaTemplate.send("Mail",objectMapper.writeValueAsString(mailDTO));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

}
