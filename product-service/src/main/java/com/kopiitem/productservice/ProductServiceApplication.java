package com.kopiitem.productservice;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.*;
import java.util.List;
import java.util.stream.Stream;

@SpringBootApplication
@EnableEurekaClient
public class ProductServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductServiceApplication.class, args);
    }

}

@Component
class CMDLiner implements CommandLineRunner {
    @Autowired
    ProductRepository productRepository;

    @Override
    public void run(String... args) throws Exception {
        Stream.of(
                new Product("Monitor"), new Product("Apple"), new Product("Samsung"),
                new Product("Beer"), new Product("Fruit"), new Product("Raspberry")
        ).forEach(productRepository::save);
    }
}


@RestController
@RequestMapping("/api")
class ProductResources {

    private final ProductRepository productRepository;

    ProductResources(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @GetMapping("/findByName/{name}")
    public List<Product> findByName(@PathVariable("name") String name) {
        return productRepository.findByName(name);
    }

    @GetMapping("/findByLikeName/{name}")
    public List<Product> findByLikeName(@PathVariable("name") String name) {
        return productRepository.findByLikeName(name);
    }

}


interface ProductCustomRepository {
    List<Product> findByLikeName(String name);
}


class ProductCustomRepositoryImpl implements ProductCustomRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Product> findByLikeName(String name) {

        String sql = "SELECT * FROM PRODUCT WHERE NAME LIKE :name";

        Query query = entityManager.createNativeQuery(sql, Product.class);
        query.setParameter("name", "%" + name + "%");
        return query.getResultList();

    }
}

interface ProductRepository extends JpaRepository<Product, Long>, ProductCustomRepository {
    List<Product> findByName(String name);

}


@Entity
@Getter
@Setter
@NoArgsConstructor
class Product {

    @Id
    @GeneratedValue
    private Long id;
    private String name;

    public Product(String name) {
        this.name = name;
    }

}

