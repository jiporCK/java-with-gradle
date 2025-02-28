package co.istad.javagradle.product;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Product {

    private String id;

    private String name;

    private String description;

    private double price;

}
