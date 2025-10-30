package leets.leenk.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaAuditing
@EnableJpaRepositories(basePackages = {
        "leets.leenk.domain.feed.domain.repository",
        "leets.leenk.domain.user.domain.repository",
        "leets.leenk.domain.media.domain.repository",
        "leets.leenk.domain.leenk.domain.repository",
        "leets.leenk.domain.birthday.domain.repository",
})
public class JpaConfig {
}
