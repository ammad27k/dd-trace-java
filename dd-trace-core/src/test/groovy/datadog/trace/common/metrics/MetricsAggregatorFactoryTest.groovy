package datadog.trace.common.metrics

import datadog.communication.ddagent.DDAgentFeaturesDiscovery
import datadog.trace.api.Config
import datadog.trace.test.util.DDSpecification
import spock.lang.Requires

import static datadog.trace.api.Platform.isJavaVersionAtLeast

@Requires({
  isJavaVersionAtLeast(8)
})
class MetricsAggregatorFactoryTest extends DDSpecification {

  def "when metrics disabled no-op aggregator created"() {
    setup:
    Config config = Mock(Config)
    config.isTracerMetricsEnabled() >> false
    expect:
    def aggregator = MetricsAggregatorFactory.createMetricsAggregator(config, Mock(DDAgentFeaturesDiscovery))
    assert aggregator instanceof NoOpMetricsAggregator
  }

  def "when metrics enabled conflating aggregator created"() {
    setup:
    Config config = Spy(Config.get())
    config.isTracerMetricsEnabled() >> true
    expect:
    def aggregator = MetricsAggregatorFactory.createMetricsAggregator(config, Mock(DDAgentFeaturesDiscovery))
    assert aggregator instanceof ConflatingMetricsAggregator
  }
}
