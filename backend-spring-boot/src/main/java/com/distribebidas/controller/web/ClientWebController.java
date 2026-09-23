package com.distribebidas.controller.web;

import com.distribebidas.model.entity.Client;
import com.distribebidas.service.ClientService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/clientes")
public class ClientWebController {

    private final ClientService clientService;

    public ClientWebController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping
    public String clientes(Model model) {
        model.addAttribute("clients", clientService.getAllClients());
        model.addAttribute("newClient", new Client());
        return "clients";
    }

    @PostMapping("/salvar")
    public String salvarCliente(@ModelAttribute Client client, RedirectAttributes ra) {
        clientService.saveClient(client);
        ra.addFlashAttribute("successMessage", "Cliente salvo com sucesso!");
        return "redirect:/clientes";
    }

    @PostMapping("/{id}/pagamento")
    public String receberPagamentoCliente(@PathVariable Long id,
                                          @RequestParam Double amount,
                                          RedirectAttributes ra) {
        clientService.payClientDebt(id, amount);
        ra.addFlashAttribute("successMessage", "Pagamento recebido e abatido com sucesso!");
        return "redirect:/clientes";
    }

    @PostMapping("/{id}/devolver-cascos")
    public String devolverCascosCliente(@PathVariable Long id,
                                         @RequestParam int count,
                                         RedirectAttributes ra) {
        clientService.returnClientBottles(id, count);
        ra.addFlashAttribute("successMessage", "Devolução de cascos registrada!");
        return "redirect:/clientes";
    }
}
