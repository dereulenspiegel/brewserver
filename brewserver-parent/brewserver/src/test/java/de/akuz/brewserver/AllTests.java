package de.akuz.brewserver;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ TestBrewServer.class, TestSerialization.class })
public class AllTests {

}
