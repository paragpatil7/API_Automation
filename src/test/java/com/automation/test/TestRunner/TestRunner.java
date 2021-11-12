
package com.automation.test.TestRunner;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.automation.test.E2E_Integration_System_Functional_Test;
import com.automation.test.UtilUnitTests;		

@RunWith(Suite.class)
@SuiteClasses({ E2E_Integration_System_Functional_Test.class,UtilUnitTests.class})//, TestClassB.class })
public class TestRunner {
 
} 