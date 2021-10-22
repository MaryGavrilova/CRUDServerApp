package ru.netology.service;

import org.springframework.stereotype.Service;
import ru.netology.exception.NotFoundException;
import ru.netology.model.Post;
import ru.netology.repository.PostRepository;

import java.util.List;

@Service
public class PostService {
    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public List<Post> all() {
        return postRepository.all();
    }

    public Post getById(long id) {
        return postRepository.getById(id).orElseThrow(NotFoundException::new);
    }

    public Post save(Post post) {
        return postRepository.save(post);
    }

    public void removeById(long id) {
        postRepository.removeById(id);
    }
}