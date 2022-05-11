package com.pbl.foundlost.controllers;

import com.pbl.foundlost.model.Post;
import com.pbl.foundlost.model.User;
import com.pbl.foundlost.payload.dto.MatchedPostDto;
import com.pbl.foundlost.payload.request.PostRequest;
import com.pbl.foundlost.payload.response.MessageResponse;
import com.pbl.foundlost.payload.response.PostResponse;
import com.pbl.foundlost.repository.PostRepository;
import com.pbl.foundlost.repository.UserRepository;
import com.pbl.foundlost.services.AmazonClient;
import com.pbl.foundlost.services.matcher.CreatePostResponse;
import com.pbl.foundlost.services.matcher.MatcherService;
import lombok.AllArgsConstructor;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;


@CrossOrigin(origins = "*", maxAge = 3600)
@AllArgsConstructor
@RestController
@RequestMapping("/api/posts")
public class PostController {
    private final UserRepository userRepository;

    private final PostRepository postRepo;

    private final MatcherService matcherService;

    private AmazonClient amazonClient;

    /**
     * Controller contains:
     * Create a post: /createPost, permissions: authenticated user, admin
     * Get a post:    /getPost,    permissions: all users, authenticated user, admin
     * Get all posts: /getAllPosts,permissions: all users, authenticated user, admin
     * Get my posts: /getMyPosts,  permissions: authenticated user, admin
     * Edit a post:   /editPost,   permissions: authenticated user, admin
     * Delete post:   /deletePost, permissions: authenticated user, admin
     **/

    //done
    @PostMapping("/createPost")
//    @PreAuthorize("hasRole('REG_USER') or hasRole('ADMIN')")
    public ResponseEntity createPost(@Valid @ModelAttribute PostRequest postRequest) {
        User user = userRepository.getOne(getAuthenticatedUserId());
        Post post = new Post();
        post.setUuid(isNull(postRequest.getUuid()) ? UUID.randomUUID() : postRequest.getUuid());
        try {
            String filePath = this.amazonClient.uploadFile(postRequest.getImage(), amazonClient.getAnimalPhotoBucket());
            post.setImage(filePath);
            post.setStatus(postRequest.getStatus());
            post.setType(postRequest.getType());
            post.setAddress(postRequest.getAddress());
            post.setCreatedDate(new Date());
            post.setContacts(postRequest.getContacts());
            post.setUser(user);
            post.setReward(postRequest.getReward());
            post.setDetails(postRequest.getDetails());
            post.setAge(postRequest.getAge());
            post.setBreed(postRequest.getBreed());
            post.setEyeColor(postRequest.getEyeColor());
            post.setFurColor(postRequest.getFurColor());
            post.setGender(postRequest.getGender());
            post.setSpecialSigns(postRequest.getSpecialSigns());
            post.setName(postRequest.getName());
            post.setNationality(postRequest.getNationality());
            post.setSpecies(postRequest.getSpecies());
            Post persistedPost = postRepo.save(post);
            List<Post> list = postRepo.findAll();
            Post lastPost = list.get(list.size() - 1);
//            RecommenderSystemController.authenticateRecommenderSystem();
//            RecommenderSystemController.sendCreatedPostToRecommenderSystem(lastPost, postRequest.getPetImage());

            CreatePostResponse createAIPostResponse = matcherService.createPost(post);
            if (!matcherService.createPost(persistedPost).getStatus().equals("ok")) {
                return ResponseEntity.badRequest().body(createAIPostResponse);
            }

            return ResponseEntity.ok(new MessageResponse("Post created successfully!"));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    //done
    @GetMapping("/getPost")
    public ResponseEntity<PostResponse> getPost(@RequestParam String id) {

        try {
            Post post = postRepo.findById(Long.parseLong(id))
                    .orElseThrow(() -> new ResourceNotFoundException(format("Post with id %s not found", id)));
            return ResponseEntity.ok(getPostResponse(post));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    //done
    @GetMapping("/getAllPosts")
    public List<PostResponse> getAllPosts() {
        List<Post> posts = postRepo.findAll();
        Collections.reverse(posts);
//        RecommenderSystemController.authenticateRecommenderSystem();
//        RecommenderSystemController.sendTextToExtract();
        return posts.stream()
                .map(this::getPostResponse)
                .collect(toList());
    }

    //done
    @GetMapping("/getSimilarPosts")
    public List<Post> getSimilarPosts(@RequestParam String id) {
//        RecommenderSystemController.authenticateRecommenderSystem();
//        Response response = RecommenderSystemController.getSimilarPostsIds(id);
        Set<Integer> similarPostIds;
//        similarPostIds = response.getResults().keySet();
//        List<Post> similarPosts = new ArrayList<>();
//        for (Integer similarPostId:similarPostIds) {
//            Optional<Post> similarPost = postRepo.findById(Long.parseLong(similarPostId.toString()));
//            if (similarPost.isPresent()){
//                Post post = similarPost.get();
//            similarPosts.add(post);}
//        }
        List<Post> posts = postRepo.findAll();
        posts = posts.subList(posts.size() - 5, posts.size() - 1);
//        return similarPosts;
        return posts;
    }

    //done
    @GetMapping("/getLostPosts")
    public List<PostResponse> getLostPetsPosts() {
        List<Post> posts = postRepo.findAll().stream().filter(post -> post.getType().equals("lost")).collect(toList());
        Collections.reverse(posts);
        return posts.stream()
                .map(this::getPostResponse)
                .collect(toList());
    }

    //done
    @GetMapping("/getFoundPosts")
    public List<PostResponse> getFoundPetsPosts() {
        List<Post> posts = postRepo.findAll().stream().filter(post -> post.getType().equals("found")).collect(toList());
        Collections.reverse(posts);
        return posts.stream()
                .map(this::getPostResponse)
                .collect(toList());
    }

    //done
    @GetMapping("/getSuccessStories")
    public List<Post> getSuccessStories() {
        List<Post> posts = postRepo.findAll().stream().filter(post -> post.getStatus().equals("Ajuns acasă")).collect(toList());
        Collections.reverse(posts);
        return posts;
    }

    //done
    @GetMapping("/getMyPosts")
//    @PreAuthorize("hasRole('REG_USER')or hasRole('ADMIN')")
    public List<PostResponse> getMyPosts() {
        User user = userRepository.getOne(getAuthenticatedUserId());
        return postRepo.findAll().stream().filter(post -> post.getUser().equals(user))
                .map(this::getPostResponse)
                .collect(toList());
    }

    //done
    @PutMapping("/editPost")
    @PreAuthorize("hasRole('REG_USER')or hasRole('ADMIN')")
    public ResponseEntity editPost(@Valid @ModelAttribute PostRequest postRequest) {
        User user = userRepository.getOne(getAuthenticatedUserId());
        System.out.println(isAdmin(user));

        try {
            Optional<Post> optionalPost = postRepo.findById(postRequest.getId());
            if (optionalPost.isPresent()) {
                Post post = optionalPost.get();
                System.out.println("User id" + user.getId());
                System.out.println("Author" + postRequest.getAuthorId());
                if (user.getId().equals(postRequest.getAuthorId()) || isAdmin(user)) {
                    System.out.println(postRequest.getImage());
                    if (postRequest.getImage() != null) {
                        String filePath = this.amazonClient.uploadFile(postRequest.getImage(), amazonClient.getAnimalPhotoBucket());
                        post.setImage(filePath);
                    }
                    post.setStatus(postRequest.getStatus());
                    post.setType(postRequest.getType());
                    post.setAddress(postRequest.getAddress());
                    post.setContacts(postRequest.getContacts());
                    post.setReward(postRequest.getReward());
                    post.setDetails(postRequest.getDetails());
                    post.setAge(postRequest.getAge());
                    post.setBreed(postRequest.getBreed());
                    post.setEyeColor(postRequest.getEyeColor());
                    post.setFurColor(postRequest.getFurColor());
                    post.setGender(postRequest.getGender());
                    post.setSpecialSigns(postRequest.getSpecialSigns());
                    post.setName(postRequest.getName());
                    post.setNationality(postRequest.getNationality());
                    post.setSpecies(postRequest.getSpecies());
                    postRepo.save(post);
                    return ResponseEntity.ok().build();
                } else {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
                }
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private boolean isAdmin(User user) {
        System.out.println(user.getRoleId());
        return user.getRoleId().equals(2);
    }

    //done
    @DeleteMapping("/deletePost")
//    @PreAuthorize("hasRole('REG_USER')or hasRole('ADMIN')")
    public ResponseEntity deletePost(@RequestParam(name = "id") String id) {
        User user = userRepository.getOne(getAuthenticatedUserId());

        try {
            Optional<Post> optionalPost = postRepo.findById(Long.parseLong(id));
            if (optionalPost.isPresent()) {
                Post post = optionalPost.get();

                if (user.getId().equals(post.getUser().getId()) || isAdmin(user)) {
                    boolean deleted = this.amazonClient.deleteFileFromS3Bucket(post.getImage(), amazonClient.getAnimalPhotoBucket());
                    System.out.println(deleted);
                    postRepo.delete(post);
                    return ResponseEntity.ok().build();
                } else {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
                }
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private Long getAuthenticatedUserId() {
        Long userId = 0L;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        try {
            Optional<User> optionalUser = userRepository.findByUsername(auth.getName());
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                userId = user.getId();
            }
            return userId;
        } catch (Exception e) {
            System.out.println("Not present");
        }
        return userId;
    }

    private PostResponse getPostResponse(Post post) {
        List<MatchedPostDto> matchedPosts = matcherService.getMatchedPosts(post);
        return new PostResponse(post, matchedPosts);
    }
}
