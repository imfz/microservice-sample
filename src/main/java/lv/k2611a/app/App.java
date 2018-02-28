package lv.k2611a.app;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import lv.k2611a.conf.FooConfiguration;

@EnableAutoConfiguration
@EnableDiscoveryClient
@SpringBootApplication
@RibbonClient(name = "anotherapp", configuration = FooConfiguration.class)
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}

@RestController
class MessageRestController {

    @Autowired
    private LoadBalancerClient loadBalancer;


    @RequestMapping("/")
    String getMessage() {
        ServiceInstance instance = loadBalancer.choose("anotherapp");
        if (instance == null) {
            return "no instance available";
        }
        URI uri = instance.getUri();
        return new RestTemplate().getForObject(uri, String.class);
    }

}
