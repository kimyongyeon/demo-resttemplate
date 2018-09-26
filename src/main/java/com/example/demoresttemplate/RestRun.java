package com.example.demoresttemplate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.reflect.Array;
import java.util.Arrays;

@Configuration
public class RestRun implements ApplicationRunner{

    @Autowired
    RestTemplateBuilder restTemplateBuilder;

    @Autowired
    WebClient.Builder webClientBuild;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        // 성능시간 체크
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        // =================================================
        // non-blocking code
        // =================================================
        // 다수 건으로 받는다.
        WebClient webClient = webClientBuild.baseUrl("https://api.github.com").build();
        Mono<GithubRepository[]> repositoryMono =  webClient.get().uri("/users/kimyongyeon/repos")
                .retrieve()
                .bodyToMono(GithubRepository[].class);

        repositoryMono.doOnSuccess(ra -> {
            Arrays.stream(ra).forEach(r -> {
                System.out.println("repos: " + r.getUrl());
            });
        }).subscribe();

        // 건 by 건
        Flux<GithubRepository> repositoryMono2 =  webClient.get().uri("/users/kimyongyeon/repos")
                .retrieve()
                .bodyToFlux(GithubRepository.class);

        repositoryMono2.doOnNext(r -> System.out.println(r.getUrl())).subscribe();
        repositoryMono2.subscribe(r -> System.out.println("repos2: " + r.getUrl()));

        // non blocking call이기 때문에 stopwatch가 먼저 찍힌다.

        // =================================================
        // blocking code
        // =================================================
//        RestTemplate restTemplate = restTemplateBuilder.build();
//        GithubRepository[] repos = restTemplate.getForObject("https://api.github.com/users/kimyongyeon/repos", GithubRepository[].class);
//        Arrays.stream(repos).forEach(r -> {
//            System.out.println("repo: " + r.getUrl());
//        });
//        GithubCommit[] githubCommit = restTemplate.getForObject("https://api.github.com/repos/kimyongyeon/sat_study/commits", GithubCommit[].class);
//        Arrays.stream(githubCommit).forEach(r -> {
//            System.out.println(r.getSha());
//        });
        stopWatch.stop();
        System.out.println(stopWatch.prettyPrint());

    }
}
