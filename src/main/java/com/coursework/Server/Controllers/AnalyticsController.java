package com.coursework.Server.Controllers;

import com.coursework.Server.Model.*;
import com.coursework.Server.Repositories.AcceptanceRepository;
import com.coursework.Server.Repositories.ProductRepository;
import com.coursework.Server.Repositories.ReleaseRepository;
import com.coursework.Server.Repositories.ReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/analytics")
public class AnalyticsController {
    @Autowired
    private AcceptanceRepository acceptanceRepository;
    @Autowired
    private ReleaseRepository releaseRepository;
    @Autowired
    private ReportRepository reportRepository;
    @Autowired
    private ProductRepository productRepository;
    @GetMapping
    public String getAnalytics() { return "analytics"; }
    @GetMapping("/report")
    public String gerReport() { return "report"; }
    @PostMapping("/report")
    public String processReport(@RequestParam(name = "from") LocalDate from, @RequestParam(name = "to") LocalDate to, @RequestParam(name = "expenses1") Float expenses1, @RequestParam(name = "expenses2") Float expenses2, @RequestParam(name = "workers") Integer workers, @RequestParam(name = "capacity") Float capacity, Model model) {
        List<Acceptance> acceptanceList = new ArrayList<>();
        List<Release> releaseList = new ArrayList<>();
        acceptanceRepository.findAll().forEach(acceptanceList::add);
        releaseRepository.findAll().forEach(releaseList::add);
        float sumWeight = 0;
        for(var el : acceptanceList)
            if((el.getOperation().getDate().isAfter(from) || el.getOperation().getDate().equals(from)) && ((el.getOperation().getDate().isBefore(to))||(el.getOperation().getDate().equals(to))))
                sumWeight += el.getBatch().getWeight();
        for(var el : releaseList)
            if((el.getOperation().getDate().isAfter(from) || el.getOperation().getDate().equals(from)) && ((el.getOperation().getDate().isBefore(to))||(el.getOperation().getDate().equals(to))))
                for(var product : el.getOrder().getProducts())
                    sumWeight += product.getBatch().getWeight()/product.getBatch().getAmount();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
        model.addAttribute("formatter", formatter);
        model.addAttribute("from", from);
        model.addAttribute("to", to);
        model.addAttribute("turnover", String.format("%.2f", sumWeight).replace(',', '.'));
        model.addAttribute("expenses", String.format("%.2f", expenses1+expenses2).replace(',', '.'));
        model.addAttribute("productivity", String.format("%.2f", sumWeight/workers).replace(',', '.'));
        model.addAttribute("turnoverRatio", String.format("%.5f", sumWeight/1000/capacity).replace(',', '.'));
        return "proccessedReport";
    }
    @PostMapping("/report/save")
    public String saveReport(@AuthenticationPrincipal User user, @RequestParam(name = "beginning") LocalDate beginning, @RequestParam(name = "ending") LocalDate ending, @RequestParam(name = "turnover") Float turnover, @RequestParam(name = "expenses") Float expenses, @RequestParam(name = "productivity") Float productivity, @RequestParam(name = "turnoverRatio") Float turnoverRatio, Model model) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
        try(FileWriter writer = new FileWriter("report.txt", false)){
            writer.write("===============================================\n");
            writer.write("Id пользователя: " + user.getUserID() + "\n");
            writer.write("Дата начала периода: " + beginning.format(formatter) + "\n");
            writer.write("Дата окончания периода: " + ending.format(formatter) + "\n");
            writer.write("Грузооборот: " + turnover + " кг.\n");
            writer.write("Расходы: " + expenses + " руб.\n");
            writer.write("Производительность труда: " + productivity + " кг\\чел\n");
            writer.write("Коэффициент оборачиваемости: " + String.format("%.5f", turnoverRatio).replace(',', '.') + "\n");
            writer.write("===============================================\n");
        }
        catch(IOException ex){

            System.out.println(ex.getMessage());
        }
        Report report = new Report(user, beginning, ending, turnover, expenses, productivity, turnoverRatio);
        reportRepository.save(report);
        model.addAttribute("saved", "Отчёт был успешно сохранён в файл!");
        return "report";
    }
    @GetMapping("/space")
    public String getSpaceForm() {
        return "space";
    }
    @PostMapping("/space")
    public String proccessSpace(@RequestParam(name = "cells") Integer cells, @RequestParam(name = "capacity") Float capacity, Model model) {
        List<Product> productList = new ArrayList<>();
        productRepository.findAll().forEach(productList::add);
        float sumWeight = 0;
        for(var el : productList)
            sumWeight += el.getBatch().getWeight()/el.getBatch().getAmount();
        model.addAttribute("cells", cells);
        model.addAttribute("freeCells", cells - productList.size());
        model.addAttribute("capacity", String.format("%.2f", capacity).replace(',', '.'));
        model.addAttribute("freeCapacity", String.format("%.2f", capacity - sumWeight).replace(',', '.'));
        return "proccessedSpace";
    }
    @GetMapping("/expired")
    public String getExpiredProducts(Model model) {
        List<Product> productList = new ArrayList<>();
        productRepository.findAll().forEach(productList::add);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
        productList = new ArrayList<>(productList.stream().filter(obj ->(obj.getShelfLife().isBefore(LocalDate.now()) || obj.getShelfLife().equals(LocalDate.now()))).toList());
        model.addAttribute("formatter", formatter);
        model.addAttribute("now", LocalDate.now());
        model.addAttribute("products", productList);
        return "expired";
    }
}
