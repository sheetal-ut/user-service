package com.ps.user.runner;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features", // path to your feature files
        glue = "com.ps.user.steps",              // path to your step definitions
        plugin = {
                "pretty",
                "html:target/cucumber-reports.html",
                "json:target/cucumber.json"
        },
        monochrome = true
)
public class CucumberTestRunner {
}
