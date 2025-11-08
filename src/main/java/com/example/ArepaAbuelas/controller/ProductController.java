package com.example.ArepaAbuelas.controller;

import com.example.ArepaAbuelas.dto.CommentDTO;
import com.example.ArepaAbuelas.dto.ProductDTO;
import com.example.ArepaAbuelas.service.CommentService;
import com.example.ArepaAbuelas.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@CrossOrigin(origins = "*")
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