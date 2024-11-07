package io.github.jokoframework.myproject.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

@RestController
@RequestMapping("/api/agenda")
@Tag(name = "Agenda Controller", description = "API for managing daily TODO agenda with mock data")
public class AgendaController {

    private static final Logger log = LoggerFactory.getLogger(AgendaController.class);

    @GetMapping("/week")
    @Operation(summary = "Get mock tasks for the week", description = "Returns mock data for daily TODO tasks for each day of the week.")
    public List<DayAgenda> getWeeklyAgenda(HttpServletRequest request, HttpServletResponse response) {
        logRequestDetails(request);
        List<DayAgenda> weekAgenda = getMockTasksForWeek();
        logResponseDetails(response, weekAgenda);
        return weekAgenda;
    }

    private void logRequestDetails(HttpServletRequest request) {
        log.info("Request Method: {}", request.getMethod());
        log.info("Request URI: {}", request.getRequestURI());
        log.info("Request Headers: {}", getHeadersInfo(request));
        log.info("Request Parameters: {}", request.getParameterMap());
    }

    private void logResponseDetails(HttpServletResponse response, Object responseBody) {
        log.info("Response Status: {}", response.getStatus());
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();
            String prettyResponseBody = objectWriter.writeValueAsString(responseBody);
            log.info("Response Body: {}", prettyResponseBody);
        } catch (IOException e) {
            log.error("Failed to pretty print response body", e);
        }
    }

    private String getHeadersInfo(HttpServletRequest request) {
        StringBuilder headers = new StringBuilder();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            headers.append(headerName).append("=").append(request.getHeader(headerName)).append(", ");
        }
        return headers.toString();
    }

    private List<DayAgenda> getMockTasksForWeek() {
        List<DayAgenda> weekAgenda = new ArrayList<>();
        weekAgenda.add(new DayAgenda("Lunes", Arrays.asList(
                new Task("Grabar video", false)
        )));
        weekAgenda.add(new DayAgenda("Martes", Arrays.asList(
                new Task("Divertirse", false)
        )));
        weekAgenda.add(new DayAgenda("Miércoles", Arrays.asList(
                new Task("Tareas", false),
                new Task("Lavar los platos", true),
                new Task("Barrer la casa", false)
        )));
        weekAgenda.add(new DayAgenda("Jueves", Arrays.asList(
                new Task("Estudiando", false)
        )));
        weekAgenda.add(new DayAgenda("Viernes", Arrays.asList(
                new Task("Canta", true)
        )));
        weekAgenda.add(new DayAgenda("Sábado", new ArrayList<>())) ;
        weekAgenda.add(new DayAgenda("Domingo", new ArrayList<>()));
        return weekAgenda;
    }
}


class DayAgenda {
    private String day;
    private List<Task> tasks;

    public DayAgenda(String day, List<Task> tasks) {
        this.day = day;
        this.tasks = tasks;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }
}


class Task {
    private String name;
    private boolean isCompleted;

    public Task(String name, boolean isCompleted) {
        this.name = name;
        this.isCompleted = isCompleted;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
}
