package com.distribebidas.config;

import com.distribebidas.model.dto.OrderItemRequestDto;
import com.distribebidas.model.entity.Client;
import com.distribebidas.model.entity.Product;
import com.distribebidas.repository.ClientRepository;
import com.distribebidas.repository.ProductRepository;
import com.distribebidas.service.OrderService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final ClientRepository clientRepository;
    private final OrderService orderService;

    public DataInitializer(ProductRepository productRepository,
                           ClientRepository clientRepository,
                           OrderService orderService) {
        this.productRepository = productRepository;
        this.clientRepository = clientRepository;
        this.orderService = orderService;
    }

    @Override
    public void run(String... args) {
        if (productRepository.count() == 0) {
            Product p1 = productRepository.save(new Product("Cerveja Heineken 330ml Long Neck", "Cervejas", "Fardo c/ 24", 95.0, 142.0, 48, 10, false, "7891000101", "Heineken Brasil"));
            Product p2 = productRepository.save(new Product("Cerveja Brahma Chopp 600ml Retornável", "Cervejas", "Caixa c/ 24", 82.0, 120.0, 3, 12, true, "7891000102", "Ambev"));
            Product p3 = productRepository.save(new Product("Cerveja Amstel 350ml Lata", "Cervejas", "Fardo c/ 12", 34.5, 46.9, 85, 15, false, "7891000103", "Heineken Brasil"));
            Product p4 = productRepository.save(new Product("Cerveja Corona Extra 330ml", "Cervejas", "Fardo c/ 24", 110.0, 165.0, 20, 8, false, "7891000104", "Ambev"));
            Product p5 = productRepository.save(new Product("Chopp Pilsen Artesanal Barril 50L", "Chopp & Barril", "Barril 50L", 380.0, 620.0, 4, 3, true, "7891000105", "Cervejaria Germânia"));
            Product p6 = productRepository.save(new Product("Refrigerante Coca-Cola 2L", "Refrigerantes", "Fardo c/ 6", 36.0, 52.9, 60, 10, false, "7891000201", "FEMSA"));
            Product p7 = productRepository.save(new Product("Refrigerante Guaraná Antarctica 2L", "Refrigerantes", "Fardo c/ 6", 30.0, 44.0, 40, 10, false, "7891000202", "Ambev"));
            Product p8 = productRepository.save(new Product("Whisky Johnnie Walker Black Label 1L", "Destilados", "Garrafa 1L", 115.0, 179.9, 14, 4, false, "7891000301", "Diageo"));
            Product p9 = productRepository.save(new Product("Gin Tanqueray London Dry 750ml", "Destilados", "Garrafa 750ml", 78.0, 129.9, 18, 5, false, "7891000302", "Diageo"));
            Product p10 = productRepository.save(new Product("Vodka Absolut 1L", "Destilados", "Garrafa 1L", 65.0, 99.9, 22, 6, false, "7891000303", "Pernod Ricard"));
            Product p11 = productRepository.save(new Product("Energético Red Bull 250ml", "Energéticos", "Fardo c/ 24", 132.0, 199.0, 2, 8, false, "7891000401", "Red Bull"));
            Product p12 = productRepository.save(new Product("Água Mineral Crystal sem Gás 500ml", "Águas & Sucos", "Fardo c/ 12", 12.0, 24.0, 90, 20, false, "7891000501", "FEMSA"));
            Product p13 = productRepository.save(new Product("Vinho Chileno Casillero del Diablo", "Vinhos & Espumantes", "Garrafa 750ml", 38.0, 59.9, 28, 6, false, "7891000601", "Concha y Toro"));

            Client c1 = clientRepository.save(new Client("Bar e Mercearia do Zé", "José Pereira Bar ME", "12.345.678/0001-90", "(11) 98765-4321", "Av. Brasil, 450 - Centro", 450.0, 6));
            Client c2 = clientRepository.save(new Client("Restaurante e Espetinho Brasa", "Brasa Grill Alimentos Ltda", "23.456.789/0001-01", "(11) 97654-3210", "Rua das Flores, 120 - Jardins", 0.0, 10));
            Client c3 = clientRepository.save(new Client("Adega e Empório Central", "Central Bebidas Eireli", "34.567.890/0001-12", "(11) 96543-2109", "Praça da Matriz, 88 - Bairro Alto", 820.0, 0));
            Client c4 = clientRepository.save(new Client("Pizzaria Forno a Lenha", "Forno Nobre Gastronomia", "45.678.901/0001-23", "(11) 95432-1098", "Rua São Paulo, 930 - Vila Nova", 0.0, 4));

            // Cria vendas iniciais de exemplo através do OrderService
            try {
                orderService.createSaleOrder(
                        c1.getId(),
                        List.of(
                                new OrderItemRequestDto(p1.getId(), 2),
                                new OrderItemRequestDto(p6.getId(), 3)
                        ),
                        "FATURADO_BOLETO",
                        10.0,
                        true,
                        c1.getAddress(),
                        "Entregar até às 17h"
                );

                orderService.createSaleOrder(
                        null,
                        List.of(
                                new OrderItemRequestDto(p3.getId(), 4),
                                new OrderItemRequestDto(p8.getId(), 1)
                        ),
                        "PIX",
                        0.0,
                        false,
                        "",
                        "Venda balcão"
                );
            } catch (Exception ignored) {}
        }
    }
}
