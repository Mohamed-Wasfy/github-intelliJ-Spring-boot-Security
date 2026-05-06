package io.github.mohamedwasfy.spring.boot.security.controllers;

import io.github.mohamedwasfy.spring.boot.security.dtos.request.ChangePasswordRequest;
import io.github.mohamedwasfy.spring.boot.security.dtos.request.RegisterUserRequest;
import io.github.mohamedwasfy.spring.boot.security.dtos.request.UpdateUserRequest;
import io.github.mohamedwasfy.spring.boot.security.dtos.response.UserDto;
import io.github.mohamedwasfy.spring.boot.security.mappers.UserMapper;
import io.github.mohamedwasfy.spring.boot.security.repositories.UserRepository;
import io.github.mohamedwasfy.spring.boot.security.services.ExternalAuthService;
import io.github.mohamedwasfy.spring.boot.security.services.JwtService;
import io.github.mohamedwasfy.spring.boot.security.services.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@AllArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @GetMapping
    public ResponseEntity<?> getAllUsers (
        @RequestHeader String authorization,
        @RequestParam(required = false, defaultValue = "", name = "sortBy") String sortBy
    ) {
        // With a base URL -- Preferred for talking to one API.

        RestClient client = RestClient.builder()
                .baseUrl("https://tenant1.sasserver.demo.sas.com")
                .defaultHeader("Accept", "application/json")
                .defaultHeader("Authorization", "Bearer " + authorization)
                .defaultHeader("Content-Type", "application/json")
                .requestInterceptor((req, body, execution) -> {
                    System.out.println(">> " + req.getMethod() + " " + req.getURI());
                    return execution.execute(req, body);
                })
                .build();

        System.out.println(client);


        // ------------------------------------------------------------------------------------------------------

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Missing or malformed Authorization header");
        }

        String token = authorization.substring(7);

        if (!jwtService.validateToken(token)) {
            return ResponseEntity.status(401).body("Invalid or expired token");
        }

        if(!Set.of("name", "email").contains(sortBy)) {
            sortBy = "name";
        }

        return ResponseEntity.ok (
            userRepository.findAll(Sort.by(sortBy).ascending())
            .stream()
            .map(userMapper::toDto)
            .toList()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        var user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

//        var userDto = new UserDto(user.getId(), user.getName(), user.getEmail());
        return ResponseEntity.ok(userMapper.toDto(user));
    }

    @PostMapping
    // MethodArgumentNotValidException
    public ResponseEntity<?> registerUser (
            @Valid @RequestBody RegisterUserRequest request,
            UriComponentsBuilder uriBuilder
    ) {
        if(userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body(
                    Map.of("email", "Email is already registered.")
            );
        }

        var user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        var userDto = userMapper.toDto(user);
        var uri = uriBuilder.path("/users/{id}").buildAndExpand(userDto.getId()).toUri();

        return ResponseEntity.created(uri).body(userDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser (
        @PathVariable(name = "id") Long id,
        @RequestBody UpdateUserRequest request
    ) {

        var existingUser = userRepository.findById(id).orElse(null);
        if (existingUser == null) {
            return ResponseEntity.notFound().build();
        }

        userMapper.update(request, existingUser);
        userRepository.save(existingUser);

        return ResponseEntity.ok(userMapper.toDto(existingUser));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser (@PathVariable Long id) {
        var userToDelete = userRepository.findById(id).orElse(null);
        if (userToDelete == null) {
            return ResponseEntity.notFound().build();
        }
        userRepository.delete(userToDelete);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/change-password")
    public ResponseEntity<Void> changePassword (
        @PathVariable Long id,
        @RequestBody ChangePasswordRequest request
    ) {
        var user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        if (!user.getPassword().equals(request.getOldPassword())) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        user.setPassword(request.getNewPassword());
        userRepository.save(user);

        return ResponseEntity.noContent().build();
    }
}

// Part 01 - Fundamentals
// Apache Kafka Series - Welcome!
// 1- Kafka For Beginners: get a strong base for Kafka, basic operations, write your first producers and consumers.
////
// 2- Kafka Connect API: Understanding how to import / export data to / from Kafka.
// 3- Kafka Streams API: Learn how to process and transform data within Kafka.
// 4- KsqlDB: Writes Kafka Streams applications using SQL.
// 5- Confluent Components: REST Proxy and Schema Registry.
// 6- Kafka Security: Setup Kafka Security in a Cluster and Integrate your applications with Kafka Security.
// 7- Kafka Monitoring and Operations: use [Prometheus] and [Grafana] to monitor Kafka, learn operations.
// 8- Kafka Cluster Setup & Administration: Get a deep understanding of how Kafka & Zookeeper works, how to Setup Kafka and various administration tasks.
// 9- Confluent Certifications for Developers Practice Exams.
// 10- Confluent Certifications for Operators Practice Exams.
///
// Kafka Topics: a Particular stream of data within your Kafka Cluster.
// Kafka Cluster:(log - purchases - twitter_tweets - trucks_gps).
// Kafka Topics [inside Kafka Cluster].
// Like a table in a database [without all the constraints].
// You can have as many topics as you want.
// A topic is identified by its name.
// Any kind of message format.
// The sequence of messages is called a data stream.
// You cannot query topics, instead, use Kafka Producers to send data and Kafka Consumers to read the data.

///

// Partitions and offsets:

// Topics are split in partitions [example: 100 partitions].

// Messages within each partition are ordered.

// Each message within a partition gets an incremental id, called offset.

// Partition 0 - Partition 1 - Partition 2

// And then as I keep

// Kafka topics are immutable: once data is written to a Partition, it cannot be changed.

// You can not Delete, Update in Kafka ?!

// Say you have a fleet of trucks; each truck reports its GPS position to Kafka.

// Each truck will send a message to Kafka every 20 Seconds, each message will contain the truck ID and the truck position [latitude and longitude].

////

// Topics, partitions and offsets - important notes.

// Kafka Topic [Partition 0 - Partition 1 - Partition 2].

// Once the data is written to a partition, it cannot be changed [immutability].

// Data in Kafka is only kept for a limited time [default is one week - configurable].

// E.g. offset 3 in partition 0 doesn't represent the same data as offset 3 in partition 1.

// Offsets are not re-used even if previous message have been deleted.

// Order is guaranteed only within a partition [not across partitions].

// Data is assigned randomly to a Partition unless a Key is provided.

// Topics, partitions, offsets.

// Producers

// Producers write data to topics [which are made of partitions].
// Producers know to which partition to write to [and which Kafka broker has it].
// In case of Kafka broker failures, Producers will automatically recover.

// Topic-A / Partition 0 [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12]
// Topic-A / Partition 1 [0, 1, 2, 3, 4, 5, 6, 7, 8]
// Topic-A / Partition 2 [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
// This load is balanced to many brokers thanks to the number of partitions.
// Producers: Message keys
// Producers can choose to send a key with the message(String, number, binary, etc...)
// PRODUCERS: -> Topic-A / Partition 0
// If key  = null, data is sent round robin (Partition 0, then 1, then 2).
// If key != null, then all messages for that key will always go to the same partition [hashing].
// A Key are typically sent if you need message ordering for a Specific field (ex: truck_id).

// truck_id_123
// Data will always be in Partition 0.
