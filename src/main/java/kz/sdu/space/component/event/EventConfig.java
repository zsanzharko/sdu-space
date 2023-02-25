package kz.sdu.space.component.event;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class EventConfig {
  @Bean
  @Scope("singleton")
  public EventRuleConfiguration eventRuleConfiguration() {
    return new EventRuleConfiguration();
  }
}
