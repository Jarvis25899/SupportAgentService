package com.coviam.crm.supportagent.service.impl;

import com.coviam.crm.supportagent.document.SaTicket;
import com.coviam.crm.supportagent.document.SupportAgent;
import com.coviam.crm.supportagent.document.Ticket;
import com.coviam.crm.supportagent.dto.CommentDTO;
import com.coviam.crm.supportagent.dto.MailDTO;
import com.coviam.crm.supportagent.dto.NoTicketsDTO;
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
import java.util.concurrent.atomic.AtomicInteger;
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
    KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public SupportAgent addSupportAgent(SupportAgent supportAgent) {
        return supportAgentRepository.save(supportAgent);
    }

    @Override
    public void deleteSupportAgent(String sId) {
        supportAgentRepository.deleteById(sId);
    }

//    @Value("${jwt.secret}")
//    private String secret;
//
//    @Override
//    public String parseToken(String idToken) {
//        Claims body = Jwts.parser()
//                .setSigningKey(secret)
//                .parseClaimsJws(idToken)
//                .getBody();
//        String id = (String) body.get("userId");
//        System.out.println(id);
//        return id;
//    }

    @Override
    public List<Ticket> getTicketList() {

        List<Ticket> ticketList = new ArrayList<>();
        ticketRepository.findAll().stream().forEach(ticket -> {
            if ((ticket.getStatus()).equals("open")) {
                ticketList.add(ticket);
            }
        });

        return ticketList;
    }

    @Override
    public SaTicket assignTicket(SaTicket saTicket) {
        Ticket ticket = ticketRepository.findById(saTicket.getTicketId()).get();

        SupportAgent supportAgent = supportAgentRepository.findById(saTicket.getSupportAgentId()).get();

        supportAgent.setTicketsPending(supportAgent.getTicketsPending() + 1);

        supportAgentRepository.save(supportAgent);

        ticket.setStatus("in progress");

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        ticket.setUpdatedTime(dtf.format(now));
        ticket.setMailCount(0);

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
        List<String> commentsList = ticket.getComments();
        commentsList.add(commentDTO.getComments());

        List<String> imageList = ticket.getImages();
        imageList.add(commentDTO.getImages());

        List<String> videoList = ticket.getVideo();
        videoList.add(commentDTO.getVideo());

        List<String> docsList = ticket.getDocs();
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
            if ((saTicket.getTicketId()).equals(ticketId)) {
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
    public List<Ticket> getPendingTicketsBySAId(String supportAgentId) {
        List<SaTicket> saTickets = saTicketRespository.findAll();
        List<Ticket> tickets = new ArrayList<>();

        saTickets.stream().forEach(saTicket -> {
            if ((saTicket.getSupportAgentId()).equals(supportAgentId)) {
                Ticket ticket = new Ticket();
                ticket = ticketRepository.findById(saTicket.getTicketId()).get();
                if ((ticket.getStatus()).equals("in progress")) {
                    tickets.add(ticket);
                }
            }
        });

        return tickets;
    }

    @Override
    public List<Ticket> getResolvedTicketsBySAId(String supportAgentId) {
        List<SaTicket> saTickets = saTicketRespository.findAll();
        List<Ticket> tickets = new ArrayList<>();

        saTickets.stream().forEach(saTicket -> {
            if ((saTicket.getSupportAgentId()).equals(supportAgentId)) {
                Ticket ticket = ticketRepository.findById(saTicket.getTicketId()).get();
                if ((ticket.getStatus()).equals("closed")) {
                    tickets.add(ticket);
                }
            }
        });

        return tickets;
    }

    @Override
    public String createTicket(PostDTO postDTO) {
        if (ticketRepository.existsById(postDTO.getPostId())) {
            Ticket ticket = ticketRepository.findById(postDTO.getPostId()).get();
            ticket.setCountOfDislike(postDTO.getCounterOfDislikes());

            List<String> dislikeIdsList = ticket.getDislikeIds();
            dislikeIdsList.add(postDTO.getDislikedId());
            ticket.setDislikeIds(dislikeIdsList);

            ticketRepository.save(ticket);


        } else {

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

    @Override
    public NoTicketsDTO resolvedTickets(String supportAgentId) {
        SupportAgent supportAgent = supportAgentRepository.findById(supportAgentId).get();
        NoTicketsDTO noTicketsDTO = new NoTicketsDTO();
        noTicketsDTO.setTicketsResolved(supportAgent.getTicketsResolved());
        noTicketsDTO.setTickertsPending(supportAgent.getTicketsPending());
        return noTicketsDTO;
    }

    @Scheduled(fixedDelay = 600000)
    private void sendMails() {
        System.out.println("hi");
        List<Ticket> ticketList = ticketRepository.findAll();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");

        ticketList.stream().forEach(ticket -> {
            if ((ticket.getStatus()).equals("open")) {
                LocalDateTime now = LocalDateTime.now();

                if (dtf.format(now.minusMinutes(15)).compareTo(ticket.getUpdatedTime()) > 0) {

                    MailDTO mailDTO = MailTemplateAdmin.mail(ticket);

                    ObjectMapper objectMapper = new ObjectMapper();
                    try {
                        kafkaTemplate.send("mail", objectMapper.writeValueAsString(mailDTO));
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }

                    System.out.println(ticket.getMailCount());
                    if (ticket.getMailCount() <= 4) {
                        System.out.println(ticket.getMailCount() + 1);
                        LocalDateTime now1 = LocalDateTime.now();
                        ticket.setUpdatedTime(dtf.format(now1));
                        ticket.setMailCount(ticket.getMailCount() + 1);
                    }

                    else {
                        ticket.setMailCount(0);
                        ticket.setStatus("discarded");

                    }

                    ticketRepository.save(ticket);

                }

            }
            else if ((ticket.getStatus()).equals("in progress")) {
                LocalDateTime now = LocalDateTime.now();

                if (dtf.format(now.minusMinutes(15)).compareTo(ticket.getUpdatedTime()) > 0) {

                    AtomicReference<String> saId = new AtomicReference<>("");
                    saTicketRespository.findAll().stream().forEach(saTicket -> {
                        if ((saTicket.getTicketId()).equals(ticket.getTicketId())) {
                            saId.set(saTicket.getSupportAgentId());
                            return;
                        }
                    });

                    MailDTO mailDTO = MailTemplateAgent.mail(ticket, supportAgentRepository.findById(saId.get()).get());

                    ObjectMapper objectMapper = new ObjectMapper();
                    try {
                        kafkaTemplate.send("mail", objectMapper.writeValueAsString(mailDTO));
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }

                    System.out.println(ticket.getMailCount());
                    if (ticket.getMailCount() <= 4) {
                        System.out.println(ticket.getMailCount() + 1);
                        LocalDateTime now1 = LocalDateTime.now();
                        ticket.setUpdatedTime(dtf.format(now1));
                        ticket.setMailCount(ticket.getMailCount() + 1);
                    }

                    else {
                        LocalDateTime now1 = LocalDateTime.now();
                        ticket.setUpdatedTime(dtf.format(now1));
                        ticket.setMailCount(0);
                        SupportAgent supportAgent = supportAgentRepository.findById(saId.get()).get();
                        supportAgent.setTicketsPending(supportAgent.getTicketsPending() - 1);
                        supportAgentRepository.save(supportAgent);
                        List<SaTicket> saTicketList = saTicketRespository.findAll();

                        AtomicReference<String> id = new AtomicReference<>("");
                        AtomicInteger flag = new AtomicInteger();
                        saTicketList.stream().forEach(saTicket -> {
                            if ((saTicket.getSupportAgentId()).equals(saId.get()) && (saTicket.getTicketId()).equals(ticket.getTicketId())){
                                id.set(saTicket.getSupportAgentTicketId());
                                flag.set(1);
                                return;
                            }
                        });
                        if (flag.get() == 1)
                            saTicketRespository.deleteById(id.get());
                        ticket.setStatus("open");
                    }

                    ticketRepository.save(ticket);
                }
            }
        });
    }
}

