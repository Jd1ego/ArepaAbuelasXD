package com.example.ArepaAbuelas.service;

import com.arepabuelas.dto.CommentDTO;
import com.arepabuelas.entity.Comment;
import com.arepabuelas.entity.Product;
import com.arepabuelas.entity.User;
import com.arepabuelas.repository.CommentRepository;
import com.arepabuelas.repository.ProductRepository;
import com.arepabuelas.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    public CommentDTO addComment(CommentDTO dto) {
        Comment comment = new Comment();
        comment.setText(dto.getText());

        Optional<User> user = userRepository.findById(dto.getUserId());
        Optional<Product> product = productRepository.findById(dto.getProductId());

        if (user.isPresent() && product.isPresent()) {
            comment.setUser(user.get());
            comment.setProduct(product.get());
            comment = commentRepository.save(comment);
            dto.setId(comment.getId());
            return dto;
        }
        return null;
    }

    public List<CommentDTO> getCommentsByProductId(Long productId) {
        return commentRepository.findByProductId(productId).stream().map(c -> {
            CommentDTO dto = new CommentDTO();
            dto.setId(c.getId());
            dto.setText(c.getText());
            dto.setUserId(c.getUser().getId());
            dto.setProductId(c.getProduct().getId());
            return dto;
        }).collect(Collectors.toList());
    }
}