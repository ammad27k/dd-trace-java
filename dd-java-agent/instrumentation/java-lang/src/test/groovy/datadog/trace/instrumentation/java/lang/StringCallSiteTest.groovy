package datadog.trace.instrumentation.java.lang

import datadog.trace.agent.test.AgentTestRunner
import datadog.trace.api.iast.InstrumentationBridge
import datadog.trace.api.iast.propagation.StringModule
import foo.bar.TestStringSuite

class StringCallSiteTest extends AgentTestRunner {

  @Override
  protected void configurePreAgent() {
    injectSysConfig("dd.iast.enabled", "true")
  }

  def 'test string concat call site'() {
    setup:
    StringModule stringModule = Mock(StringModule)
    InstrumentationBridge.registerIastModule(stringModule)

    when:
    final result = TestStringSuite.concat('Hello ', 'World!')

    then:
    result == 'Hello World!'
    1 * stringModule.onStringConcat('Hello ', 'World!', 'Hello World!')
    0 * _
  }

  def 'test string toUpperCase call site'() {
    setup:
    final stringModule = Mock(StringModule)
    InstrumentationBridge.registerIastModule(stringModule)

    when:
    final result = TestStringSuite.stringToUpperCase('hello', null)
    final result2 = TestStringSuite.stringToUpperCase('world', new Locale("en"))

    then:
    result == 'HELLO'
    result2 == 'WORLD'
    1 * stringModule.onStringToUpperCase('hello', 'HELLO')
    1 * stringModule.onStringToUpperCase('world', 'WORLD')
    0 * _
  }

  def 'test string toLowerCase call site'() {
    setup:
    final stringModule = Mock(StringModule)
    InstrumentationBridge.registerIastModule(stringModule)

    when:
    final result = TestStringSuite.stringToLowerCase('HELLO', null)
    final result2 = TestStringSuite.stringToLowerCase('WORLD', new Locale("en"))

    then:
    result == 'hello'
    result2 == 'world'
    1 * stringModule.onStringToLowerCase('HELLO', 'hello')
    1 * stringModule.onStringToLowerCase('WORLD', 'world')
    0 * _
  }

  def 'test string substring call site'() {
    setup:
    final iastModule = Mock(StringModule)
    InstrumentationBridge.registerIastModule(iastModule)
    final self = '012345'
    final expected = '12345'

    when:
    final result = TestStringSuite.substring(self, 1)

    then:
    result == expected
    1 * iastModule.onStringSubSequence(self, 1, self.length(), expected)
    0 * _
  }

  def 'test string substring with endIndex call site'() {
    setup:
    final iastModule = Mock(StringModule)
    InstrumentationBridge.registerIastModule(iastModule)
    final self = '012345'
    final expected = '1234'

    when:
    final result = TestStringSuite.substring(self, 1, 5)

    then:
    result == expected
    1 * iastModule.onStringSubSequence(self, 1, 5, expected)
    0 * _
  }

  def 'test string subSequence call site'() {
    setup:
    final iastModule = Mock(StringModule)
    InstrumentationBridge.registerIastModule(iastModule)
    final self = '012345'
    final expected = '1234'

    when:
    final result = TestStringSuite.subSequence(self, 1, 5)

    then:
    result == expected
    1 * iastModule.onStringSubSequence(self, 1, 5, expected)
    0 * _
  }

  def 'test string join call site'() {
    setup:
    final iastModule = Mock(StringModule)
    final expected = '012-345'
    InstrumentationBridge.registerIastModule(iastModule)

    when:
    final result = TestStringSuite.join('-', '012', '345')

    then:
    result == expected
    1 * iastModule.onStringJoin(expected, '-', '012', '345')
    0 * _
  }

  def 'test string join with Iterable call site'() {
    setup:
    final iastModule = Mock(StringModule)
    final expected = '012-345'
    InstrumentationBridge.registerIastModule(iastModule)
    final iterable = Arrays.asList('012', '345')

    when:
    final result = TestStringSuite.join('-', iterable)

    then:
    result == expected
    1 * iastModule.onStringJoin(expected, '-', '012', '345')
    0 * _
  }

  def 'test string join with Iterable fail on iterable copy'() {

    given:
    final iterable = Mock(Iterable)

    when:
    TestStringSuite.join('-', iterable)

    then:
    1 * iterable.forEach(_) >> { throw new Error('Boom!!!') }
    thrown Error
  }
}
