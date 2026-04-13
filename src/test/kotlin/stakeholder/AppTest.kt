package stakeholder

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

class AppTest {
    @Test
    fun `list-values exposes the full registry and dedicated renderer keys`() {
        val result = StakeholderApp.runArgs(listOf("--list-values"))
        assertEquals(0, result.exitCode)
        assertTrue(result.stdout.contains("\"generatorFamilies\""))
        assertTrue(result.stdout.contains("\"rendererKey\""))
        assertTrue(result.stdout.contains("\"code_analyzer\""))
        assertTrue(result.stdout.contains("\"delivery_preview_ops\""))
        assertTrue("\"id\":\"".toRegex().findAll(result.stdout).count() >= 30)
    }

    @TestFactory
    fun `dedicated family metadata`() =
        dedicatedFamilies.map { (familyId, contextKey, rendererKey) ->
            DynamicTest.dynamicTest(familyId) {
                val result =
                    StakeholderApp.runArgs(
                        listOf(
                            "--focus-family",
                            familyId,
                            "--output-format",
                            "json",
                            "--seed",
                            "smoke",
                        ),
                    )
                assertEquals(0, result.exitCode)
                assertTrue(result.stdout.contains("\"family\":\"$familyId\""))
                assertTrue(result.stdout.contains("\"$contextKey\":"))
                assertTrue(result.stdout.contains("\"rendererKey\":\"$rendererKey\""))
            }
        }

    @Test
    fun `deterministic json stays stable for the same seed`() {
        val args =
            listOf(
                "--focus-family",
                "platform_engineering",
                "--output-format",
                "json",
                "--seed",
                "same-seed",
            )
        val first = StakeholderApp.runArgs(args)
        val second = StakeholderApp.runArgs(args)
        assertEquals(0, first.exitCode)
        assertEquals(first.stdout, second.stdout)
    }

    @Test
    fun `experimental provider flags fail fast`() {
        val result = StakeholderApp.runArgs(listOf("--experimental-provider", "openai"))
        assertEquals(1, result.exitCode)
        assertTrue(result.stderr.contains("experimental-provider is not implemented yet in kotlin-stakeholder"))
    }

    companion object {
        private val dedicatedFamilies =
            listOf(
                Triple("code_analyzer", "analysisFocus", "classic-six.code_analyzer"),
                Triple("data_processing", "dataWindow", "classic-six.data_processing"),
                Triple("jargon", "languagePolicy", "classic-six.jargon"),
                Triple("metrics", "signalBlend", "classic-six.metrics"),
                Triple("network_activity", "transportMix", "classic-six.network_activity"),
                Triple("system_monitoring", "telemetryScope", "classic-six.system_monitoring"),
                Triple("agent_workflows", "coordinationMode", "modern-core.agent_workflows"),
                Triple("platform_engineering", "platformSurface", "modern-core.platform_engineering"),
                Triple("observability_ai_runtime", "runtimeSignals", "modern-core.observability_ai_runtime"),
                Triple("delivery_preview_ops", "deliveryGuardrail", "modern-core.delivery_preview_ops"),
                Triple("supply_chain_security", "supplyChainPosture", "modern-core.supply_chain_security"),
            )
    }
}
