package io.refactor.ordersservice;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UIController {
    @GetMapping("/error")
    public String error() {
        return "error";
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/get")
    public String getOrder() {
        return "get-order";
    }

    @GetMapping("/filter")
    public String getFilters() {
        return "filters";
    }
}
