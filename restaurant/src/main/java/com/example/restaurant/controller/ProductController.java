package com.example.restaurant.controller;

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
@RequestMapping(value = "/menuItem", produces = MediaType.APPLICATION_JSON_VALUE)
public class ProductController {

    private final List<Product> productDB = new ArrayList<>();

    @PostConstruct
    private void initDB() {
        productDB.add(new Product("1", "番茄鮭魚義大利麵", 380));
        productDB.add(new Product("2", "焗烤蘑菇飯", 420));
        productDB.add(new Product("3", "西西里披薩", 250));
        productDB.add(new Product("4", "高檔牛小排", 450));
        productDB.add(new Product("5", "冰淇淋佐鵝肝醬", 330));
    }

    //取得菜單by id
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable("id") String id) {
        Optional<Product> productOp = productDB.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();

        if (productOp.isPresent()) {
            Product product = productOp.get();
            return ResponseEntity.ok().body(product);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    //取得全部菜單
    @GetMapping
    public ResponseEntity<List<Product>> getProducts(@ModelAttribute ProductQueryParameter param) {
        String keyword = param.getKeyword();
        String orderBy = param.getOrderBy();
        String sortRule = param.getSortRule();
        Comparator<Product> comparator = genSortComparator(orderBy, sortRule);
        
        List<Product> products = productDB.stream()
                //.filter(p -> p.getName().toUpperCase().contains(keyword.toUpperCase()))
                .sorted(comparator)
                .collect(Collectors.toList());

        return ResponseEntity.ok().body(products);
    }

//    @PostMapping
//    public ResponseEntity<Product> createProduct(@RequestBody Product request) {
//        boolean isIdDuplicated = productDB.stream()
//                .anyMatch(p -> p.getId().equals(request.getId()));
//        if (isIdDuplicated) {
//            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
//        }
//
//        Product product = new Product();
//        product.setId(request.getId());
//        product.setName(request.getName());
//        product.setPrice(request.getPrice());
//        productDB.add(product);
//
//        URI location = ServletUriComponentsBuilder
//                .fromCurrentRequest()
//                .path("/{id}")
//                .buildAndExpand(product.getId())
//                .toUri();
//
//        return ResponseEntity.created(location).body(product);
//    }

//    @PutMapping("/{id}")
//    public ResponseEntity<Product> replaceProduct(
//            @PathVariable("id") String id, @RequestBody Product request) {
//        Optional<Product> productOp = productDB.stream()
//                .filter(p -> p.getId().equals(id))
//                .findFirst();
//
//        if (productOp.isPresent()) {
//            Product product = productOp.get();
//            product.setName(request.getName());
//            product.setPrice(request.getPrice());
//
//            return ResponseEntity.ok().body(product);
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

    private Comparator<Product> genSortComparator(String orderBy, String sortRule) {
        Comparator<Product> comparator = (p1, p2) -> 0;
        if (Objects.isNull(orderBy) || Objects.isNull(sortRule)) {
            return comparator;
        }

        if (orderBy.equalsIgnoreCase("price")) {
            comparator = Comparator.comparing(Product::getPrice);
        } else if (orderBy.equalsIgnoreCase("name")) {
            comparator = Comparator.comparing(Product::getName);
        }

        return sortRule.equalsIgnoreCase("desc")
                ? comparator.reversed()
                : comparator;
    }
}
