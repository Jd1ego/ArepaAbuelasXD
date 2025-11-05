package com.example.ArepaAbuelas.controller;

import com.arepabuelas.dto.CommentDTO;
import com.arepabuelas.dto.ProductDTO;
import com.arepabuelas.service.CommentService;
import com.arepabuelas.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CommentService commentService;

    @GetMapping
    public List<ProductDTO> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProduct(@PathVariable Long id) {
        return productService.getProductById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<CommentDTO> addComment(@PathVariable Long id, @RequestBody CommentDTO dto) {
        dto.setProductId(id);
        CommentDTO added = commentService.addComment(dto);
        if (added != null) {
            return ResponseEntity.ok(added);
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/{id}/comments")
    public List<CommentDTO> getComments(@PathVariable Long id) {
        return commentService.getCommentsByProductId(id);
    }
}