package com.coursework.Server.Controllers;

import com.coursework.Server.Factories.AbstractFactory;
import com.coursework.Server.Factories.AcceptanceService;
import com.coursework.Server.Factories.PlacementService;
import com.coursework.Server.Factories.ReleaseService;
import com.coursework.Server.Model.*;
import com.coursework.Server.Repositories.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("/services")
@SessionAttributes(names = {"order", "pickListProducts"})
public class ServicesController {
    private AbstractFactory service;
    @Autowired
    private BatchRepository batchRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private AcceptanceRepository acceptanceRepository;
    @Autowired
    private OperationRepository operationRepository;
    @Autowired
    private PlacementRepository placementRepository;
    @Autowired
    private ReleaseRepository releaseRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ReportRepository reportRepository;
    @Autowired
    private UserRepository userRepository;

    @ModelAttribute(name="batch")
    public Batch batch(){
        return new Batch();
    }
    @ModelAttribute(name="product")
    public Product product(){
        return new Product();
    }
    @ModelAttribute(name="acceptance")
    public Acceptance acceptance(){
        return new Acceptance();
    }
    @ModelAttribute(name="placement")
    public Placement placement(){
        return new Placement();
    }
    @ModelAttribute(name="order")
    public Order order(){
        return new Order();
    }
    @ModelAttribute(name="release")
    public Release release(){
        return new Release();
    }
    @ModelAttribute(name = "pickListProducts")
    public List<Product> products() { return new ArrayList<>(); }

    private void upgradeActivity(User user){
        User profile = userRepository.findById(user.getUserID()).orElseThrow();
        profile.setActivity(profile.getActivity()+1);
        userRepository.save(profile);
    }

    @GetMapping
    public String getServices(){
        return "services";
    }
    @GetMapping("/batches")
    public String getBatches(Model model){
        List<Batch> batches = new ArrayList<Batch>();
        batchRepository.findAll().forEach(batches::add);
        model.addAttribute("batches", batches);
        return "batches";
    }
    @GetMapping("/batches/add")
    public String addBatch(){
        return "batch-add";
    }
    @PostMapping("/batches/add")
    public String addBatchPost(@Valid Batch batch, Errors errors, @AuthenticationPrincipal User user, Model model){
        if(errors.hasErrors())
            return "batch-add";
        batchRepository.save(batch);
        upgradeActivity(user);
        List<Batch> batches = new ArrayList<Batch>();
        batchRepository.findAll().forEach(batches::add);
        model.addAttribute("batches", batches);
        return "batches";
    }
    @GetMapping("/batches/{id}/edit")
    public String getEditBatch(@PathVariable(value = "id") Long id, Model model){
        if(!batchRepository.existsById(id))
            return "redirect:/services/batches";
        Batch batch = batchRepository.findById(id).orElseThrow();
        model.addAttribute("res", batch);
        return "batch-edit";
    }
    @PostMapping("/batches/{id}/edit")
    public String postEditBatch(@PathVariable(value="id") Long id, @Valid Batch batch, Errors errors, @AuthenticationPrincipal User user, Model model){
        if(errors.hasErrors()) {
            Batch editBatch = batchRepository.findById(id).orElseThrow();
            model.addAttribute("res", editBatch);
            model.addAttribute("error", "Данные введены неверно");
            return "batch-edit";
        }
        Batch editBatch = batchRepository.findById(id).orElseThrow();
        editBatch.setType(batch.getType());
        editBatch.setAmount(batch.getAmount());
        editBatch.setWeight(batch.getWeight());
        batchRepository.save(editBatch);
        upgradeActivity(user);
        return "redirect:/services/batches";
    }
    @PostMapping("/batches/{id}/remove")
    public String removeBatch(@PathVariable(value = "id") Long id, @AuthenticationPrincipal User user, Model model) {
        try {
            if (!batchRepository.existsById(id))
                return "redirect:/services/batches";
            batchRepository.deleteById(id);
            upgradeActivity(user);
            return "redirect:/services/batches";
        }
        catch (Exception ex) {
            List<Batch> batches = new ArrayList<Batch>();
            batchRepository.findAll().forEach(batches::add);
            model.addAttribute("batches", batches);
            model.addAttribute("error", "Перед удалением партии необходимо очистить данные о связанных товарах");
            return "batches";
        }
    }
    @GetMapping("/batches/sort")
    public String sortBatches(@RequestParam(required = false, name = "type") String type, @RequestParam(required = false, name = "sort") String sort, Model model) {
        if(sort==null)
            sort = "";
        List<Batch> batches = new ArrayList<>();
        batchRepository.findAll().forEach(batches::add);
        if(type!="")
            batches = new ArrayList<>(batches.stream().filter(obj ->(obj.getType().equals(type))).toList());
        if(sort.equals("count-asc"))
            batches.sort(new Comparator<Batch>() {
                @Override
                public int compare(Batch o1, Batch o2) {
                    return o1.getAmount() - o2.getAmount();
                }
            });
        if(sort.equals("count-desc"))
            batches.sort(new Comparator<Batch>() {
                @Override
                public int compare(Batch o1, Batch o2) {
                    return o2.getAmount() - o1.getAmount();
                }
            });
        if(sort.equals("weight-asc"))
            batches.sort(new Comparator<Batch>() {
                @Override
                public int compare(Batch o1, Batch o2) {
                    if(o1.getWeight() > o2.getWeight()) return 1;
                    else if(o1.getWeight() < o2.getWeight()) return -1;
                    else return 0;
                }
            });
        if(sort.equals("weight-desc"))
            batches.sort(new Comparator<Batch>() {
                @Override
                public int compare(Batch o1, Batch o2) {
                    if(o1.getWeight() < o2.getWeight()) return 1;
                    else if(o1.getWeight() > o2.getWeight()) return -1;
                    else return 0;
                }
            });
        model.addAttribute("batches", batches);
        return "batches";
    }
    @PostMapping("/batches/sort/clear")
    public String clearSortingBatches(){
        return "redirect:/services/batches";
    }
    @GetMapping("/products")
    public String getProducts(Model model){
        List<Product> products = new ArrayList<Product>();
        productRepository.findAll().forEach(products::add);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        model.addAttribute("formatter", formatter);
        model.addAttribute("products", products);
        return "products";
    }
    @GetMapping("/products/add")
    public String addProduct(Model model){
        List<Batch> batches = new ArrayList<Batch>();
        batchRepository.findAll().forEach(batches::add);
        model.addAttribute("batches", batches);
        return "product-add";
    }
    @PostMapping("/products/add")
    public String addProductPost(@Valid Product product, Errors errors, @AuthenticationPrincipal User user, Model model){
        if(errors.hasErrors()) {
            List<Batch> batches = new ArrayList<Batch>();
            batchRepository.findAll().forEach(batches::add);
            model.addAttribute("batches", batches);
            return "product-add";
        }
        productRepository.save(product);
        upgradeActivity(user);
        return "redirect:/services/products";
    }
    @GetMapping("/products/{id}/edit")
    public String getEditProduct(@PathVariable(value = "id") Long id, Model model){
        if(!productRepository.existsById(id))
            return "redirect:/services/products";
        Product product = productRepository.findById(id).orElseThrow();
        List<Batch> batches = new ArrayList<Batch>();
        batchRepository.findAll().forEach(batches::add);
        model.addAttribute("batches", batches);
        model.addAttribute("res", product);
        return "product-edit";
    }
    @PostMapping("/products/{id}/edit")
    public String postEditProduct(@Valid Product product, Errors errors, @PathVariable(value="id") Long id, @AuthenticationPrincipal User user, Model model){
        if(errors.hasErrors()) {
            Product editProduct = productRepository.findById(id).orElseThrow();
            List<Batch> batches = new ArrayList<Batch>();
            batchRepository.findAll().forEach(batches::add);
            model.addAttribute("res", editProduct);
            model.addAttribute("batches", batches);
            model.addAttribute("error", "Данные введены неверно");
            return "product-edit";
        }
        Product editProduct = productRepository.findById(id).orElseThrow();
        Batch batch = batchRepository.findById(product.getBatch().getBatchId()).orElseThrow();
        editProduct.setBatch(batch);
        editProduct.setName(product.getName());
        editProduct.setCell(product.getCell());
        editProduct.setPrice(product.getPrice());
        editProduct.setShelfLife(product.getShelfLife());
        productRepository.save(editProduct);
        upgradeActivity(user);
        return "redirect:/services/products";
    }
    @PostMapping("/products/{id}/remove")
    public String removeProduct(@PathVariable(value = "id") Long id, @AuthenticationPrincipal User user, Model model) {
        if(!productRepository.existsById(id))
            return "redirect:/services/products";
        productRepository.deleteById(id);
        upgradeActivity(user);
        List<Product> products = new ArrayList<Product>();
        productRepository.findAll().forEach(products::add);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        model.addAttribute("formatter", formatter);
        model.addAttribute("products", products);
        return "products";
    }
    @GetMapping("/products/sort")
    public String sortProducts(@RequestParam(required = false, name = "name") String name, @RequestParam(required = false, name = "price") String price, @RequestParam(required = false, name = "from") Float from, @RequestParam(required = false, name = "to") Float to, Model model) {
        if(price==null)
            price = "";
        List<Product> products = new ArrayList<>();
        productRepository.findAll().forEach(products::add);
        if(name!="")
            products = new ArrayList<>(products.stream().filter(obj ->(obj.getName().equals(name))).toList());
        if(price.equals("asc"))
            products.sort(new Comparator<Product>() {
                @Override
                public int compare(Product o1, Product o2) {
                    if(o1.getPrice() > o2.getPrice()) return 1;
                    else if(o1.getPrice() < o2.getPrice()) return -1;
                    else return 0;
                }
            });
        if(price.equals("desc"))
            products.sort(new Comparator<Product>() {
                @Override
                public int compare(Product o1, Product o2) {
                    if(o1.getPrice() < o2.getPrice()) return 1;
                    else if(o1.getPrice() > o2.getPrice()) return -1;
                    else return 0;
                }
            });
        if(from != null && to != null)
            products = new ArrayList<>(products.stream().filter(obj -> (obj.getPrice() >= from && obj.getPrice() <= to)).toList());
        model.addAttribute("products", products);
        return "products";
    }
    @PostMapping("/products/sort/clear")
    public String clearSorting() {
        return "redirect:/services/products";
    }
    @GetMapping("/operations")
    public String getOperations(Model model){
        List<Acceptance> acceptanceList = new ArrayList<>();
        List<Placement> placementList = new ArrayList<>();
        List<Release> releaseList = new ArrayList<>();
        acceptanceRepository.findAll().forEach(acceptanceList::add);
        placementRepository.findAll().forEach(placementList::add);
        releaseRepository.findAll().forEach(releaseList::add);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        model.addAttribute("formatter", formatter);
        model.addAttribute("acceptanceList", acceptanceList);
        model.addAttribute("placementList", placementList);
        model.addAttribute("releaseList", releaseList);
        return "operations";
    }
    @GetMapping("/operations/acceptance/add")
    public String addAcceptance(Model model){
        List<Batch> batches = new ArrayList<Batch>();
        batchRepository.findAll().forEach(batches::add);
        model.addAttribute("batches", batches);
        return "acceptance-add";
    }
    @PostMapping("/operations/acceptance/add")
    public String addAcceptancePost(@RequestParam(name = "date") LocalDate date, @Valid Acceptance acceptance, Errors errors, @AuthenticationPrincipal User user, Model model){
        if(errors.hasErrors()){
            List<Batch> batches = new ArrayList<Batch>();
            batchRepository.findAll().forEach(batches::add);
            model.addAttribute("batches", batches);
            return "acceptance-add";
        }
        Operation operation = new Operation();
        operation.setUser(user);
        operation.setDate(date);
        operation.setType("приём");
        acceptance.setOperation(operationRepository.save(operation));
        acceptanceRepository.save(acceptance);
        upgradeActivity(user);
        return "redirect:/services/operations";
    }
    @GetMapping("/operations/acceptance/{id}/edit")
    public String getEditAcceptance(@PathVariable(value = "id") Long id, Model model){
        if(!acceptanceRepository.existsById(id))
            return "redirect:/services/operations";
        Acceptance acceptance = acceptanceRepository.findById(id).orElseThrow();
        List<Batch> batches = new ArrayList<Batch>();
        batchRepository.findAll().forEach(batches::add);
        model.addAttribute("batches", batches);
        model.addAttribute("res", acceptance);
        return "acceptance-edit";
    }
    @PostMapping("/operations/acceptance/{id}/edit")
    public String postEditAcceptance(@PathVariable(value="id") Long id, @RequestParam(name = "date") LocalDate date, @Valid Acceptance acceptance, Errors errors, Model model){
        if(errors.hasErrors()){
            Acceptance editAcceptance = acceptanceRepository.findById(id).orElseThrow();
            List<Batch> batches = new ArrayList<Batch>();
            batchRepository.findAll().forEach(batches::add);
            model.addAttribute("batches", batches);
            model.addAttribute("res", editAcceptance);
            model.addAttribute("error", "Данные введены неверно");
            return "acceptance-edit";
        }
        Acceptance editAcceptance = acceptanceRepository.findById(id).orElseThrow();
        editAcceptance.getOperation().setDate(date);
        Batch batch = batchRepository.findById(acceptance.getBatch().getBatchId()).orElseThrow();
        editAcceptance.setBatch(batch);
        editAcceptance.setSender(acceptance.getSender());
        editAcceptance.setPrice(acceptance.getPrice());
        acceptanceRepository.save(editAcceptance);
        upgradeActivity(editAcceptance.getOperation().getUser());
        return "redirect:/services/operations";
    }
    @PostMapping("/operations/acceptance/{id}/remove")
    public String removeAcceptance(@PathVariable(value = "id") Long id) {
        Acceptance acceptance = acceptanceRepository.findById(id).orElseThrow();
        if(acceptance == null)
            return "redirect:/services/operations";
        upgradeActivity(acceptance.getOperation().getUser());
        acceptanceRepository.deleteById(id);
        operationRepository.delete(acceptance.getOperation());
        return "redirect:/services/operations";
    }
    @GetMapping("/operations/placement/add")
    public String addPlacement(Model model){
        List<Batch> batches = new ArrayList<Batch>();
        batchRepository.findAll().forEach(batches::add);
        model.addAttribute("batches", batches);
        return "placement-add";
    }
    @PostMapping("/operations/placement/add")
    public String addPlacementPost(@RequestParam(name = "date") LocalDate date, Placement placement, @AuthenticationPrincipal User user, Model model){
        Operation operation = new Operation();
        operation.setUser(user);
        operation.setDate(date);
        operation.setType("размещение");
        placement.setOperation(operationRepository.save(operation));
        placementRepository.save(placement);
        upgradeActivity(user);
        return "redirect:/services/operations";
    }
    @GetMapping("/operations/placement/{id}/edit")
    public String getEditPlacement(@PathVariable(value = "id") Long id, Model model){
        if(!placementRepository.existsById(id))
            return "redirect:/services/operations";
        Placement placement = placementRepository.findById(id).orElseThrow();
        List<Batch> batches = new ArrayList<Batch>();
        batchRepository.findAll().forEach(batches::add);
        model.addAttribute("batches", batches);
        model.addAttribute("res", placement);
        return "placement-edit";
    }
    @PostMapping("/operations/placement/{id}/edit")
    public String postEditPlacement(@PathVariable(value="id") Long id, @RequestParam(name = "date") LocalDate date, Placement placement, Model model){
        Placement editPlacement = placementRepository.findById(id).orElseThrow();
        editPlacement.getOperation().setDate(date);
        Batch batch = batchRepository.findById(placement.getBatch().getBatchId()).orElseThrow();
        editPlacement.setBatch(batch);
        placementRepository.save(editPlacement);
        upgradeActivity(editPlacement.getOperation().getUser());
        return "redirect:/services/operations";
    }
    @PostMapping("/operations/placement/{id}/remove")
    public String removePlacement(@PathVariable(value = "id") Long id) {
        Placement placement = placementRepository.findById(id).orElseThrow();
        if(placement == null)
            return "redirect:/services/operations";
        upgradeActivity(placement.getOperation().getUser());
        placementRepository.deleteById(id);
        operationRepository.delete(placement.getOperation());
        return "redirect:/services/operations";
    }
    @GetMapping("/operations/release/add")
    public String addRelease(Model model){
        List<Order> orders = new ArrayList<Order>();
        orderRepository.findAll().forEach(orders::add);
        model.addAttribute("orders", orders);
        return "release-add";
    }
    @PostMapping("/operations/release/add")
    public String addReleasePost(@RequestParam(name = "date") LocalDate date, Release release, @AuthenticationPrincipal User user, Model model){
        Operation operation = new Operation();
        operation.setUser(user);
        operation.setDate(date);
        operation.setType("отпуск");
        release.setOperation(operationRepository.save(operation));
        releaseRepository.save(release);
        upgradeActivity(user);
        return "redirect:/services/operations";
    }
    @GetMapping("/operations/release/{id}/edit")
    public String getEditRelease(@PathVariable(value = "id") Long id, Model model){
        if(!releaseRepository.existsById(id))
            return "redirect:/services/operations";
        Release release = releaseRepository.findById(id).orElseThrow();
        List<Order> orders = new ArrayList<>();
        orderRepository.findAll().forEach(orders::add);
        model.addAttribute("orders", orders);
        model.addAttribute("res", release);
        return "release-edit";
    }
    @PostMapping("/operations/release/{id}/edit")
    public String postEditRelease(@PathVariable(value="id") Long id, @RequestParam(name = "date") LocalDate date, Release release, Model model){
        Release editRelease = releaseRepository.findById(id).orElseThrow();
        editRelease.getOperation().setDate(date);
        Order order = orderRepository.findById(release.getOrder().getOrderId()).orElseThrow();
        editRelease.setOrder(order);
        releaseRepository.save(editRelease);
        upgradeActivity(editRelease.getOperation().getUser());
        return "redirect:/services/operations";
    }
    @PostMapping("/operations/release/{id}/remove")
    public String removeRelease(@PathVariable(value = "id") Long id) {
        Release release = releaseRepository.findById(id).orElseThrow();
        if(release == null)
            return "redirect:/services/operations";
        upgradeActivity(release.getOperation().getUser());
        releaseRepository.deleteById(id);
        operationRepository.delete(release.getOperation());
        return "redirect:/services/operations";
    }
    @GetMapping("/operations/sort")
    public String sortOperations(@RequestParam(required = false, name = "type") String type, @RequestParam(required = false, name = "date") String date, @RequestParam(required = false, name = "from") LocalDate from, @RequestParam(required = false, name = "to") LocalDate to, Model model) {
        if(date==null)
            date = "";
        List<Acceptance> acceptanceList = new ArrayList<>();
        List<Placement> placementList = new ArrayList<>();
        List<Release> releaseList = new ArrayList<>();
        if(type.equals("all")) {
            acceptanceRepository.findAll().forEach(acceptanceList::add);
            placementRepository.findAll().forEach(placementList::add);
            releaseRepository.findAll().forEach(releaseList::add);
        }
        else if(type.equals("acceptance")){
            acceptanceRepository.findAll().forEach(acceptanceList::add);
        }
        else if(type.equals("placement")){
            placementRepository.findAll().forEach(placementList::add);
        }
        else if(type.equals("release")){
            releaseRepository.findAll().forEach(releaseList::add);
        }
        if(date.equals("asc")) {
            service = new AcceptanceService();
            acceptanceList = service.createSorter().sortByDateAsc(acceptanceList);
            service = new PlacementService();
            placementList = service.createSorter().sortByDateAsc(placementList);
            service = new ReleaseService();
            releaseList = service.createSorter().sortByDateAsc(releaseList);
        }
        if(date.equals("desc")) {
            service = new AcceptanceService();
            acceptanceList = service.createSorter().sortByDateDesc(acceptanceList);
            service = new PlacementService();
            placementList = service.createSorter().sortByDateDesc(placementList);
            service = new ReleaseService();
            releaseList = service.createSorter().sortByDateDesc(releaseList);
        }
        if(from != null && to!=null) {
            acceptanceList = new ArrayList<>(acceptanceList.stream().filter(obj -> ((obj.getOperation().getDate()).isAfter(from) || obj.getOperation().getDate().equals(from)) && ((obj.getOperation().getDate().isBefore(to)) || (obj.getOperation().getDate().equals(to)))).toList());
            placementList = new ArrayList<>(placementList.stream().filter(obj ->((obj.getOperation().getDate()).isAfter(from) || obj.getOperation().getDate().equals(from)) && ((obj.getOperation().getDate().isBefore(to))||(obj.getOperation().getDate().equals(to)))).toList());
            releaseList = new ArrayList<>(releaseList.stream().filter(obj ->((obj.getOperation().getDate()).isAfter(from) || obj.getOperation().getDate().equals(from)) && ((obj.getOperation().getDate().isBefore(to))||(obj.getOperation().getDate().equals(to)))).toList());
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        model.addAttribute("formatter", formatter);
        model.addAttribute("acceptanceList", acceptanceList);
        model.addAttribute("placementList", placementList);
        model.addAttribute("releaseList", releaseList);
        return "operations";
    }
    @PostMapping("/operations/sort/clear")
    public String clearSortingOperations(){
        return "redirect:/services/operations";
    }
    @GetMapping("/orders")
    public String getOrder(Model model){
        List<Order> orders = new ArrayList<Order>();
        orderRepository.findAll().forEach(orders::add);
        model.addAttribute("orders", orders);
        return "orders";
    }
    @GetMapping("/orders/add")
    public String addOrder(@ModelAttribute Order order){
        order.setProducts(new ArrayList<>());
        return "order-add";
    }
    @PostMapping("/orders/add")
    public String addOrder(@RequestParam(name = "date") LocalDate date, @ModelAttribute Order order, @AuthenticationPrincipal User user, SessionStatus status, Model model){
        if(order.getProducts().isEmpty()) {
            model.addAttribute("error", "Необходимо выбрать минимум один товар");
            return "order-add";
        }
        order.setDate(date);
        order.setUser(user);
        orderRepository.save(order);
        upgradeActivity(user);
        status.setComplete();
        return "redirect:/services/orders";
    }
    @GetMapping("/orders/add/add-product")
    public String addOrderAddProduct(Model model){
        List<Product> products = new ArrayList<Product>();
        productRepository.findAll().forEach(products::add);
        model.addAttribute("products", products);
        return "order-product-add";
    }
    @PostMapping("/orders/add/add-product/{id}")
    public String addOrderAddProductPost(@ModelAttribute Order order, @PathVariable(name = "id") Long id, Model model){
        Product product = productRepository.findById(id).orElseThrow();
        for (var el : order.getProducts())
            if (el.getProductId().equals(id))
                return "order-add";
        List<Order> orders = new ArrayList<>();
        orderRepository.findAll().forEach(orders::add);
        for(var el : orders)
            for(var pr : el.getProducts())
                if(pr.getProductId().equals(product.getProductId())) {
                    List<Product> products = new ArrayList<Product>();
                    productRepository.findAll().forEach(products::add);
                    model.addAttribute("products", products);
                    model.addAttribute("error", "Ордер на отгрузку данного товара уже существует");
                    return "order-product-add";
                }
        order.getProducts().add(product);
        return "order-add";
    }
    @GetMapping("/orders/edit/add-product")
    public String editOrderAddProduct(Model model){
        List<Product> products = new ArrayList<Product>();
        productRepository.findAll().forEach(products::add);
        model.addAttribute("products", products);
        return "edit-order-product-add";
    }
    @PostMapping("/orders/edit/add-product/{id}")
    public String editOrderAddProductPost(Order order, @PathVariable(name = "id") Long id, Model model){
        Product product = productRepository.findById(id).orElseThrow();
        for (var el : order.getProducts())
            if (el.getProductId().equals(id))
                return "order-edit";
        List<Order> orders = new ArrayList<>();
        orderRepository.findAll().forEach(orders::add);
        for(var el : orders)
            for(var pr : el.getProducts())
                if(pr.getProductId().equals(product.getProductId()) && !el.getOrderId().equals(order.getOrderId())) {
                    List<Product> products = new ArrayList<Product>();
                    productRepository.findAll().forEach(products::add);
                    model.addAttribute("products", products);
                    model.addAttribute("error", "Ордер на отгрузку данного товара уже существует");
                    return "edit-order-product-add";
                }
        order.getProducts().add(product);
        return "order-edit";
    }
    @GetMapping("/orders/add/clear")
    public String addOrderClear(SessionStatus status){
        status.setComplete();
        return "redirect:/services/orders/add";
    }
    @GetMapping("/orders/edit/clear")
    public String editOrderClear(@ModelAttribute Order order){
        order.getProducts().clear();;
        return "order-edit";
    }
    @GetMapping("/orders/{id}/edit")
    public String editOrderGet(@PathVariable(name = "id") Long id, @ModelAttribute Order order){
        if (!orderRepository.existsById(id))
            return "redirect:/services/orders";
        Order editOrder = orderRepository.findById(id).orElseThrow();
        order.setOrderId(editOrder.getOrderId());
        order.setDate(editOrder.getDate());
        order.setProducts(editOrder.getProducts());
        return "order-edit";
    }
    @PostMapping("/orders/{id}/edit")
    public String editOrderPost(@RequestParam(name = "date") LocalDate date, @ModelAttribute Order order, SessionStatus status, Model model){
        if(order.getProducts().isEmpty()) {
            model.addAttribute("error", "Необходимо выбрать минимум один товар");
            return "order-edit";
        }
        Order editOrder = orderRepository.findById(order.getOrderId()).orElseThrow();
        editOrder.setDate(date);
        editOrder.setProducts(order.getProducts());
        orderRepository.save(editOrder);
        upgradeActivity(editOrder.getUser());
        status.setComplete();
        return "redirect:/services/orders";
    }
    @PostMapping("/orders/{id}/remove")
    public String removeOrder(@PathVariable(name = "id") Long id){
        Order order = orderRepository.findById(id).orElseThrow();
        upgradeActivity(order.getUser());
        orderRepository.delete(order);
        return "redirect:/services/orders";
    }
    @GetMapping("/pick-list")
    public String createPickList() {
        return "pick-list";
    }
    @PostMapping("/pick-list")
    public String proccessPickList() {
        return "proccessed-pick-list";
    }
    @GetMapping("/pick-list/add-product")
    public String addProductToPickList(Model model){
        List<Product> products = new ArrayList<Product>();
        productRepository.findAll().forEach(products::add);
        model.addAttribute("products", products);
        return "pick-list-product-add";
    }
    @PostMapping("/pick-list/add-product/{id}")
    public String addProductToTickListPost(@ModelAttribute(name = "pickListProducts") List<Product> products, @PathVariable(name = "id") Long id, Model model){
        Product product = productRepository.findById(id).orElseThrow();
        for (var el : products)
            if (el.getProductId().equals(id))
                return "pick-list";
        products.add(product);
        return "pick-list";
    }
    @GetMapping("/pick-list/clear")
    public String clearPickList(SessionStatus status) {
        status.setComplete();
        return "redirect:/services/pick-list";
    }
    @PostMapping("/pick-list/save")
    public String savePickList(@ModelAttribute(name = "pickListProducts") List<Product> products, Model model){
        try(FileWriter writer = new FileWriter("pick-list.txt", false)){
            for(var el : products) {
                writer.write("===========================================\n");
                writer.write("ID ТОВАРА: " + el.getProductId() + "\n");
                writer.write("Наименование товара: " + el.getName() + "\n");
                writer.write("Номер ячейки: " + el.getCell() + "\n");
                writer.write("===========================================\n");
            }
            model.addAttribute("saved", "Пик-лист был успешно сохранён в файл!");
        }
        catch(IOException ex){

            System.out.println(ex.getMessage());
        }
        return "pick-list";
    }
    @GetMapping("/reports")
    public String getReposts(Model model) {
        List<Report> reports = new ArrayList<>();
        reportRepository.findAll().forEach(reports::add);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        DecimalFormat dFormatter = new DecimalFormat("##.#####");
        model.addAttribute("reports", reports);
        model.addAttribute("formatter", formatter);
        return "reports";
    }
}
