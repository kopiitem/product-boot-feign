package com.kopiitem.productclient;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
@EnableCircuitBreaker
@EnableHystrix
public class ProductClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductClientApplication.class, args);
    }

}

@RestController
@RequestMapping("/product-client")
class ProductClientController {

    @Autowired
    ProductsServiceClient productsServiceClient;

    @RequestMapping("/findByLikeName/{name}")
    @HystrixCommand(fallbackMethod = "fallbackFindByLikeName")
    public List<Product> findByLikeName(@PathVariable("name") String name) {
        return productsServiceClient.findByLikeName(name);

    }

    private List<Product> fallbackFindByLikeName(@PathVariable("name") String name) {
        return new ArrayList<>();
    }


}

@Component
class ProductsServiceClientImpl implements ProductsServiceClient {
    @Override
    public List<Product> findByLikeName(@PathVariable("name") String name) {
        return new ArrayList<>();
    }
}

@FeignClient("product-service")
interface ProductsServiceClient {
    @GetMapping("/api/findByLikeName/{name}")
    public List<Product> findByLikeName(@PathVariable("name") String name);
}

@Getter
@Setter
@AllArgsConstructor
class Product {

    private Long id;
    private String name;

    public Product(String name) {
        this.name = name;
    }

}