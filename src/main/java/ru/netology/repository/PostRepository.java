package ru.netology.repository;

import ru.netology.model.Post;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

public class PostRepository {
    protected List<Post> allPosts = new CopyOnWriteArrayList<>();
    protected long numberOfAllPosts;

    public List<Post> all() {
        if (allPosts.size() == 0) {
            return Collections.emptyList();
        }
        return allPosts;
    }

    public Optional<Post> getById(long id) {
        for (Post currentPost : allPosts) {
            if (id == currentPost.getId()) {
                return Optional.of(currentPost);
            }
        }
        return Optional.empty();
    }

    public Post save(Post post) {
        if (post.getId() == 0) {
            post.setId(++numberOfAllPosts);
            allPosts.add(post);
            return post;
        } else {
            for (Post currentPost : allPosts) {
                if (post.getId() == currentPost.getId()) {
                    currentPost.setContent(post.getContent());
                    return currentPost;
                }
            }
            return new Post(0, "Post can not be saved");
        }
    }

    public void removeById(long id) {
        allPosts.removeIf(currentPost -> currentPost.getId() == id);
    }
}