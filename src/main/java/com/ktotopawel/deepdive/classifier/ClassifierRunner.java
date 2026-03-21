import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Profile("classifier")
class ClassifierRunner implements CommandLineRunner {

  @Override
  public void run(String... args) throws Exception {
      // TODO Auto-generated method stub
      
  }

}
