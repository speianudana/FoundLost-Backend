package com.pbl.foundlost.controllers;

import com.pbl.foundlost.model.GeographicalLocation;
import com.pbl.foundlost.model.Post;
import com.pbl.foundlost.model.User;
import com.pbl.foundlost.payload.dto.MatchedPostDto;
import com.pbl.foundlost.payload.dto.NearPostDto;
import com.pbl.foundlost.payload.request.PostRequestData;
import com.pbl.foundlost.payload.response.MessageResponse;
import com.pbl.foundlost.payload.response.PostResponse;
import com.pbl.foundlost.repository.PostRepository;
import com.pbl.foundlost.repository.UserRepository;
import com.pbl.foundlost.services.AmazonClient;
import com.pbl.foundlost.services.PostsService;
import com.pbl.foundlost.services.matcher.CreatePostResponse;
import com.pbl.foundlost.services.matcher.MatcherService;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.*;

import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;


@CrossOrigin(origins = "*", maxAge = 3600)
@AllArgsConstructor
@RestController
@RequestMapping("/api/posts")
public class PostController {
    private final UserRepository userRepository;

    private final PostRepository postRepo;

    private final MatcherService matcherService;

    private final PostsService postsService;

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
    @PostMapping(value = "/createPost", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    @PreAuthorize("hasRole('REG_USER') or hasRole('ADMIN')")
    public ResponseEntity createPost(@Valid @ModelAttribute PostRequestData postRequestData) {
        User user = userRepository.getOne(getAuthenticatedUserId());
        Post post = new Post();
        post.setUuid(isNull(postRequestData.getUuid()) ? UUID.randomUUID() : postRequestData.getUuid());
        try {
            MultipartFile image = postRequestData.getImage();
            System.out.println(image);
            if (nonNull(image)) {
                String imgUrl = this.amazonClient.uploadFile(image, amazonClient.getAnimalPhotoBucket());
                System.out.println(imgUrl);
                post.setImage(imgUrl);
            }

            post.setStatus(postRequestData.getStatus());
            post.setType(postRequestData.getType());
            post.setAddress(postRequestData.getAddress());
            post.setCreatedDate(new Date());
            post.setContacts(postRequestData.getContacts());
            post.setUser(user);
            post.setReward(postRequestData.getReward());

            if (nonNull(postRequestData.getLatitude()) && nonNull(postRequestData.getLongitude())) {
                GeographicalLocation location = new GeographicalLocation();
                location.setLatitude(postRequestData.getLatitude());
                location.setLongitude(postRequestData.getLongitude());
                post.setGeographicalLocation(location);
            }

            post.setDetails(postRequestData.getDetails());
            post.setAge(postRequestData.getAge());
            post.setBreed(postRequestData.getBreed());
            post.setEyeColor(postRequestData.getEyeColor());
            post.setFurColor(postRequestData.getFurColor());
            post.setGender(postRequestData.getGender());
            post.setSpecialSigns(postRequestData.getSpecialSigns());
            post.setName(postRequestData.getName());
            post.setNationality(postRequestData.getNationality());
            post.setSpecies(postRequestData.getSpecies());
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
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    //done
    @GetMapping("/getPost")
    public ResponseEntity<PostResponse> getPost(@RequestParam UUID uuid) {

        try {
            Post post = postRepo.findByUuid(uuid)
                    .orElseThrow(() -> new ResourceNotFoundException(format("Post with uuid %s not found", uuid)));
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
        List<Post> posts = postRepo.findAll().stream().filter(Post::getIsResolved).collect(toList());
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
    @PutMapping(value = "/editPost", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('REG_USER')or hasRole('ADMIN')")
    public ResponseEntity editPost(@Valid @ModelAttribute PostRequestData postRequestData) {
        User user = userRepository.getOne(getAuthenticatedUserId());
        System.out.println(isAdmin(user));

        try {
            Optional<Post> optionalPost = postRepo.findByUuid(postRequestData.getUuid());
            if (optionalPost.isPresent()) {
                Post post = optionalPost.get();

                System.out.println("User id" + user.getId());
                System.out.println("Author" + postRequestData.getAuthorId());
                if (user.getId().equals(postRequestData.getAuthorId()) || isAdmin(user)) {
                    MultipartFile image = postRequestData.getImage();
                    if (image != null) {
                        String filePath = this.amazonClient.uploadFile(image, amazonClient.getAnimalPhotoBucket());
                        post.setImage(filePath);
                    }
                    post.setStatus(postRequestData.getStatus());
                    post.setType(postRequestData.getType());
                    post.setAddress(postRequestData.getAddress());
                    post.setContacts(postRequestData.getContacts());
                    post.setReward(postRequestData.getReward());

                    if (nonNull(postRequestData.getLatitude()) && nonNull(postRequestData.getLongitude())) {
                        GeographicalLocation location = new GeographicalLocation();
                        location.setLatitude(postRequestData.getLatitude());
                        location.setLongitude(postRequestData.getLongitude());
                        post.setGeographicalLocation(location);
                    }

                    post.setDetails(postRequestData.getDetails());
                    post.setAge(postRequestData.getAge());
                    post.setBreed(postRequestData.getBreed());
                    post.setEyeColor(postRequestData.getEyeColor());
                    post.setFurColor(postRequestData.getFurColor());
                    post.setGender(postRequestData.getGender());
                    post.setSpecialSigns(postRequestData.getSpecialSigns());
                    post.setName(postRequestData.getName());
                    post.setNationality(postRequestData.getNationality());
                    post.setSpecies(postRequestData.getSpecies());
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
    @ApiOperation(value = "asd", consumes = "multipart/form-data, application/json")
//    @PreAuthorize("hasRole('REG_USER')or hasRole('ADMIN')")
    public ResponseEntity deletePost(@RequestParam(name = "uuid") UUID postUuid) {
        User user = userRepository.getOne(getAuthenticatedUserId());

        try {
            Optional<Post> optionalPost = postRepo.findByUuid(postUuid);
            if (optionalPost.isPresent()) {
                Post post = optionalPost.get();

                if (user.getId().equals(post.getUser().getId()) || isAdmin(user)) {
                    boolean deleted = this.amazonClient.deleteFileFromS3Bucket(post.getImage(), amazonClient.getAnimalPhotoBucket());
                    System.out.println(deleted);
                    postRepo.delete(post);
                    matcherService.deletePost(post);
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
        List<NearPostDto> nearPosts = postsService.getNearPosts(post);
        return new PostResponse(post, matchedPosts, nearPosts);
    }
}
