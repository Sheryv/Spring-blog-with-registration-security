package com.sheryv.example.springblog.config;

import com.sheryv.example.springblog.model.Post;
import com.sheryv.example.springblog.repository.CommentRepository;
import com.sheryv.example.springblog.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.sheryv.example.springblog.model.Comment;
import com.sheryv.example.springblog.model.Role;
import com.sheryv.example.springblog.model.User;
import com.sheryv.example.springblog.repository.RoleRepository;
import com.sheryv.example.springblog.repository.UserRepository;

import java.util.*;

@Component
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {

    private boolean alreadySetup = false;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // API

    @Override
    @Transactional
    public void onApplicationEvent(final ContextRefreshedEvent event) {
        if (alreadySetup) {
            return;
        }
        final Role adminRole = createRoleIfNotFound(Role.ADMIN);
        Role roleUser = createRoleIfNotFound(Role.USER);
        User admin = createUserIfNotFound("admin@test.com", "admin", "AdminTest", "Test", "pass", Collections.singleton(adminRole));
        createUserIfNotFound("user@test.com", "userr", "UserTest", "UTest", "pass", Collections.singleton(roleUser));
        createPosts(admin);
        alreadySetup = true;
    }


    @Transactional
    Role createRoleIfNotFound(final String name) {
        Role role = roleRepository.findByName(name);
        if (role == null) {
            role = new Role(name);
            role = roleRepository.save(role);
        }
        return role;
    }

    @Transactional
    User createUserIfNotFound(final String email, final String username, final String firstName, final String lastName, final String password, final Collection<Role> roles) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            user = new User();
            user.setFirstName(firstName);
            user.setUsername(username);
            user.setLastName(lastName);
            user.setPassword(passwordEncoder.encode(password));
            user.setEmail(email);
            user.setEnabled(true);
            user.setRoles(roles);
            user = userRepository.save(user);
        }
        return user;
    }


    @Transactional
    List<Post> createPosts(User author) {
        List<Post> posts = new ArrayList<>();
        if (postRepository.count() == 0) {
            Post post = createPost("Oluptatem accusantium doloremque", "Summary",
                    "Nemo <b>2</b> enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur" +
                    " magni dolores eos qui ratione voluptatem sequi nesciunt. Neque porro quisquam est, qui dolorem",
                    author, 0);
            posts.add(post);

            for (int i = 0; i < 15; i++) {
                posts.add(createPost("Wygenerowany post " + i, "Lorem ipsum dolor sit amet, consectetur adipiscing " +
                        "elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.", "Sed ut perspiciatis " +
                        "unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque" +
                        " ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. " +
                        "Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur" +
                        " magni dolores eos qui ratione voluptatem sequi nesciunt. Neque porro quisquam est, qui dolorem " +
                        "ipsum quia dolor sit amet, consectetur, adipisci velit", author, -((9 + i) * 60 * 1000)));
            }
            List<Comment> comments = new ArrayList<>();
            comments.add(new Comment(post, "Comment", author));
            comments.add(new Comment(post, "dsc", author));
            post.setComments(comments);
            postRepository.saveAll(posts);
            commentRepository.saveAll(comments);
        }
        return posts;
    }


    private Post createPost(String title, String summary, String content, User author, long timeoffset) {
        Post p = new Post();
        p.setAuthor(author);
        p.setContent(content);
        p.setSummary(summary);
        p.setTitle(title);
        p.setPublishedAt(new Date(System.currentTimeMillis() + timeoffset));
        return p;
    }
}