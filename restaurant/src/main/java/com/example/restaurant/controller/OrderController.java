package com.example.restaurant.controller;

import com.example.restaurant.entity.Order;
import com.example.restaurant.entity.Product;
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
@RequestMapping(value = "/order", produces = MediaType.APPLICATION_JSON_VALUE)
public class OrderController {

    private final List<Order> productDB = new ArrayList<>();

    @PostConstruct
    private void initDB() {
    	List<Product> orderItem =new ArrayList<>();;
        productDB.add(new Order("1", orderItem));
        productDB.add(new Order("2", orderItem));
        productDB.add(new Order("3", orderItem));
    }

    //取得訂單by id
    @GetMapping("/{id}")
    public ResponseEntity<Order> getProduct(@PathVariable("id") String id) {
        Optional<Order> productOp = productDB.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();

        if (productOp.isPresent()) {
        	Order order = productOp.get();
            return ResponseEntity.ok().body(order);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    //取得全部訂單
    @GetMapping
    public ResponseEntity<List<Order>> getProducts(@ModelAttribute ProductQueryParameter param) {        
        List<Order> products = productDB.stream()
                .collect(Collectors.toList());

        return ResponseEntity.ok().body(products);
    }

    //新增訂單
    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody Order request) {
        boolean isIdDuplicated = productDB.stream()
                .anyMatch(p -> p.getId().equals(request.getId()));
        if (isIdDuplicated) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
        }

        Order order = new Order();
        order.setId(request.getId());
        order.setproduct(request.getproduct());
        productDB.add(order);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(order.getId())
                .toUri();

        return ResponseEntity.created(location).body(order);
    }

    //修改訂單
    @PutMapping("/{id}")
    public ResponseEntity<Order> replaceOrder(
            @PathVariable("id") String id, @RequestBody Order request) {
        Optional<Order> productOp = productDB.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();

        if (productOp.isPresent()) {
        	Order order = productOp.get();
        	order.setId(request.getId());
        	order.setproduct(request.getproduct());

            return ResponseEntity.ok().body(order);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    //刪除訂單
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable("id") String id) {
        boolean isRemoved = productDB.removeIf(p -> p.getId().equals(id));

        return isRemoved
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
    
//    //刪除訂單中的一筆資料
//    @DeleteMapping("/{id}/{productid}")
//    public ResponseEntity<Void> deleteProduct(@PathVariable("id") String id,@PathVariable("productid") List<Product> productid) {
//        boolean isRemoved = productDB.removeIf(p -> p.getId().equals(id) && p -> p.getproduct().equals(productid));
//
//        return isRemoved
//                ? ResponseEntity.noContent().build()
//                : ResponseEntity.notFound().build();
//    }

}
