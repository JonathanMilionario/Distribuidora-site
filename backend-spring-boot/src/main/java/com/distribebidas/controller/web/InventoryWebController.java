package com.distribebidas.controller.web;

import com.distribebidas.model.dto.StockAdjustmentDto;
import com.distribebidas.model.entity.Product;
import com.distribebidas.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/estoque")
public class InventoryWebController {

    private final ProductService productService;

    public InventoryWebController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public String estoque(@RequestParam(required = false) String search,
                          @RequestParam(required = false) String sort,
                          Model model) {
        List<Product> products = productService.getAllProducts();

        if (search != null && !search.isBlank()) {
            products = products.stream()
                    .filter(p -> p.getName().toLowerCase().contains(search.toLowerCase()) ||
                            p.getCategory().toLowerCase().contains(search.toLowerCase()))
                    .toList();
        }

        if ("lucro-unit".equals(sort)) {
            products = products.stream().sorted((a, b) -> Double.compare(b.getUnitProfit(), a.getUnitProfit())).toList();
        } else if ("lucro-total".equals(sort)) {
            products = products.stream().sorted((a, b) -> Double.compare(b.getTotalStockProfit(), a.getTotalStockProfit())).toList();
        } else if ("margem".equals(sort)) {
            products = products.stream().sorted((a, b) -> Double.compare(b.getMarkupPercentage(), a.getMarkupPercentage())).toList();
        } else if ("baixo-estoque".equals(sort)) {
            products = products.stream().filter(Product::isLowStock).toList();
        }

        double totalStockCost = products.stream().mapToDouble(Product::getTotalStockCost).sum();
        double totalStockSale = products.stream().mapToDouble(Product::getTotalStockSale).sum();
        double totalProjectedProfit = Math.max(0.0, totalStockSale - totalStockCost);

        model.addAttribute("products", products);
        model.addAttribute("totalStockCost", totalStockCost);
        model.addAttribute("totalStockSale", totalStockSale);
        model.addAttribute("totalProjectedProfit", totalProjectedProfit);
        model.addAttribute("newProduct", new Product());
        model.addAttribute("currentSort", sort);
        model.addAttribute("currentSearch", search);

        return "inventory";
    }

    @PostMapping("/salvar")
    public String salvarProduto(@ModelAttribute Product product, RedirectAttributes ra) {
        productService.saveProduct(product);
        ra.addFlashAttribute("successMessage", "Produto '" + product.getName() + "' salvo com sucesso!");
        return "redirect:/estoque";
    }

    @PostMapping("/ajuste")
    public String ajustarEstoque(@RequestParam Long productId,
                                 @RequestParam int delta,
                                 @RequestParam String type,
                                 @RequestParam String reason,
                                 RedirectAttributes ra) {
        productService.adjustStock(productId, delta, type, reason);
        ra.addFlashAttribute("successMessage", "Movimentação de estoque registrada com sucesso!");
        return "redirect:/estoque";
    }

    @PostMapping("/excluir")
    public String excluirProduto(@RequestParam Long productId, RedirectAttributes ra) {
        productService.deleteProduct(productId);
        ra.addFlashAttribute("successMessage", "Produto removido do estoque.");
        return "redirect:/estoque";
    }
}
