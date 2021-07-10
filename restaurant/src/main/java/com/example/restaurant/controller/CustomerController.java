package com.example.restaurant.controller;

import com.example.restaurant.entity.Customer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import com.example.restaurant.parameter.ProductQueryParameter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/customer", produces = MediaType.APPLICATION_JSON_VALUE)
public class CustomerController {

    private final List<Customer> productDB = new ArrayList<>();

    @PostConstruct
    private void initDB() {
        productDB.add(new Customer("1", "neddy"));
        productDB.add(new Customer("2", "jay"));
        productDB.add(new Customer("3", "lisa"));
    }

    //取得顧客by id
    @GetMapping("/{id}")
    public ResponseEntity<Customer> getProduct(@PathVariable("id") String id) {
        Optional<Customer> productOp = productDB.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();

        if (productOp.isPresent()) {
        	Customer product = productOp.get();
            return ResponseEntity.ok().body(product);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    //取得全部顧客
    @GetMapping
    public ResponseEntity<List<Customer>> getProducts(@ModelAttribute ProductQueryParameter param) {
        String keyword = param.getKeyword();
        String orderBy = param.getOrderBy();
        String sortRule = param.getSortRule();
        Comparator<Customer> comparator = genSortComparator(orderBy, sortRule);
        
        List<Customer> products = productDB.stream()
                //.filter(p -> p.getName().toUpperCase().contains(keyword.toUpperCase()))
                .sorted(comparator)
                .collect(Collectors.toList());

        return ResponseEntity.ok().body(products);
    }

//    @PostMapping
//    public ResponseEntity<Customer> createProduct(@RequestBody Customer request) {
//        boolean isIdDuplicated = productDB.stream()
//                .anyMatch(p -> p.getId().equals(request.getId()));
//        if (isIdDuplicated) {
//            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
//        }
//
//        Customer customer = new Customer();
//        customer.setId(request.getId());
//        customer.setName(request.getName());
//        productDB.add(customer);
//
//        URI location = ServletUriComponentsBuilder
//                .fromCurrentRequest()
//                .path("/{id}")
//                .buildAndExpand(customer.getId())
//                .toUri();
//
//        return ResponseEntity.created(location).body(customer);
//    }
//
//    @PutMapping("/{id}")
//    public ResponseEntity<Customer> replaceProduct(
//            @PathVariable("id") String id, @RequestBody Customer request) {
//        Optional<Customer> productOp = productDB.stream()
//                .filter(p -> p.getId().equals(id))
//                .findFirst();
//
//        if (productOp.isPresent()) {
//        	Customer customer = productOp.get();
//        	customer.setName(request.getName());
//
//            return ResponseEntity.ok().body(customer);
//        } else {
//            return ResponseEntity.notFound().build();
//        }
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteProduct(@PathVariable("id") String id) {
//        boolean isRemoved = productDB.removeIf(p -> p.getId().equals(id));
//
//        return isRemoved
//                ? ResponseEntity.noContent().build()
//                : ResponseEntity.notFound().build();
//    }

    private Comparator<Customer> genSortComparator(String orderBy, String sortRule) {
        Comparator<Customer> comparator = (p1, p2) -> 0;
        if (Objects.isNull(orderBy) || Objects.isNull(sortRule)) {
            return comparator;
        }

//        if (orderBy.equalsIgnoreCase("price")) {
//            comparator = Comparator.comparing(Customer::getPrice);
//        } 
        else if (orderBy.equalsIgnoreCase("name")) {
            comparator = Comparator.comparing(Customer::getName);
        }

        return sortRule.equalsIgnoreCase("desc")
                ? comparator.reversed()
                : comparator;
    }
}
