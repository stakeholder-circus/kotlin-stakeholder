package stakeholder

import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.zip.CRC32
import kotlin.system.exitProcess

data class Result(
    val exitCode: Int,
    val stdout: String,
    val stderr: String,
)

data class SessionConfig(
    val devType: String = "backend",
    val complexity: String = "medium",
    val jargon: String = "normal",
    val outputFormat: String = "text",
    val seed: String = "default-seed",
    val focusFamily: String = "",
    val alerts: Boolean = false,
    val team: String = "platform",
    val minimal: Boolean = false,
    val trace: Boolean = false,
    val framework: String = "gradle",
    val project: String = "stakeholder-circus",
    val duration: Int = 60,
    val noColor: Boolean = false,
)

data class FamilyDef(
    val id: String,
    val label: String,
    val group: String,
    val summary: String,
    val rendererKey: String,
    val smoke: Boolean,
    val parityClass: String,
)

private data class DedicatedMeta(
    val contextKey: String,
    val contextDetail: String,
    val rustPath: String,
    val javaPath: String,
    val corePath: String,
)

private data class ParseState(
    val config: SessionConfig = SessionConfig(),
    val showHelp: Boolean = false,
    val listValues: Boolean = false,
    val experimentalProvider: String? = null,
)

private sealed interface ParseResult {
    data class Success(val state: ParseState) : ParseResult

    data class Failure(val message: String) : ParseResult
}

object StakeholderApp {
    private val formatter = DateTimeFormatter.ISO_INSTANT.withZone(ZoneOffset.UTC)
    private val experimentalProviderPrefix = "--experimental-provider="
    private val devTypes =
        listOf(
            "backend",
            "blockchain",
            "data-science",
            "dev-ops",
            "frontend",
            "fullstack",
            "game-development",
            "machine-learning",
            "security",
            "systems-programming",
        )
    private val jargonLevels = listOf("low", "normal", "high", "extreme")
    private val complexities = listOf("low", "medium", "high", "extreme")
    private val outputFormats = listOf("text", "json")

    private val dedicatedMeta =
        mapOf(
            "code_analyzer" to
                DedicatedMeta(
                    "analysisFocus",
                    "typed interfaces, agent-authored patches, and MCP assumptions",
                    "src/generators/code_analyzer.rs",
                    "java-stakeholder/src/main/java/stakeholder/generators/CodeAnalyzerRenderer.java",
                    "stakeholder-core/docs/traceability-matrix.md#code_analyzer",
                ),
            "data_processing" to
                DedicatedMeta(
                    "dataWindow",
                    "embeddings, semantic chunks, and batch transforms with deterministic ordering",
                    "src/generators/data_processing.rs",
                    "java-stakeholder/src/main/java/stakeholder/generators/DataProcessingRenderer.java",
                    "stakeholder-core/docs/traceability-matrix.md#data_processing",
                ),
            "jargon" to
                DedicatedMeta(
                    "languagePolicy",
                    "credible 2026 terminology instead of fake-deep phrasing",
                    "src/generators/jargon.rs",
                    "java-stakeholder/src/main/java/stakeholder/generators/JargonRenderer.java",
                    "stakeholder-core/docs/traceability-matrix.md#jargon",
                ),
            "metrics" to
                DedicatedMeta(
                    "signalBlend",
                    "queue depth, token spend, and GPU occupancy in a single operations lane",
                    "src/generators/metrics.rs",
                    "java-stakeholder/src/main/java/stakeholder/generators/MetricsRenderer.java",
                    "stakeholder-core/docs/traceability-matrix.md#metrics",
                ),
            "network_activity" to
                DedicatedMeta(
                    "transportMix",
                    "RPC, event-stream, and adapter traffic under deterministic retry rules",
                    "src/generators/network_activity.rs",
                    "java-stakeholder/src/main/java/stakeholder/generators/NetworkActivityRenderer.java",
                    "stakeholder-core/docs/traceability-matrix.md#network_activity",
                ),
            "system_monitoring" to
                DedicatedMeta(
                    "telemetryScope",
                    "collector pressure, runner health, and policy-denial signals across the stack",
                    "src/generators/system_monitoring.rs",
                    "java-stakeholder/src/main/java/stakeholder/generators/SystemMonitoringRenderer.java",
                    "stakeholder-core/docs/traceability-matrix.md#system_monitoring",
                ),
            "agent_workflows" to
                DedicatedMeta(
                    "coordinationMode",
                    "delegated agent work, approval gates, and cross-repo handoff envelopes",
                    "src/generators/agent_workflows.rs",
                    "java-stakeholder/src/main/java/stakeholder/generators/AgentWorkflowsRenderer.java",
                    "stakeholder-core/docs/traceability-matrix.md#agent_workflows",
                ),
            "platform_engineering" to
                DedicatedMeta(
                    "platformSurface",
                    "golden paths, identity boundaries, and queue ownership in the shared platform lane",
                    "src/generators/platform_engineering.rs",
                    "java-stakeholder/src/main/java/stakeholder/generators/PlatformEngineeringRenderer.java",
                    "stakeholder-core/docs/traceability-matrix.md#platform_engineering",
                ),
            "observability_ai_runtime" to
                DedicatedMeta(
                    "runtimeSignals",
                    "trace spans, token burn, GPU pressure, and policy denials in one runtime lane",
                    "src/generators/observability_ai_runtime.rs",
                    "java-stakeholder/src/main/java/stakeholder/generators/ObservabilityAiRuntimeRenderer.java",
                    "stakeholder-core/docs/traceability-matrix.md#observability_ai_runtime",
                ),
            "delivery_preview_ops" to
                DedicatedMeta(
                    "deliveryGuardrail",
                    "preview deploys, canaries, release flags, and rollback checkpoints under seed control",
                    "src/generators/delivery_preview_ops.rs",
                    "java-stakeholder/src/main/java/stakeholder/generators/DeliveryPreviewOpsRenderer.java",
                    "stakeholder-core/docs/traceability-matrix.md#delivery_preview_ops",
                ),
            "supply_chain_security" to
                DedicatedMeta(
                    "supplyChainPosture",
                    "provenance, attestations, dependency drift, and secret exposure in one security lane",
                    "src/generators/supply_chain_security.rs",
                    "java-stakeholder/src/main/java/stakeholder/generators/SupplyChainSecurityRenderer.java",
                    "stakeholder-core/docs/traceability-matrix.md#supply_chain_security",
                ),
        )

    val familyRegistry: List<FamilyDef> =
        listOf(
            FamilyDef(
                "code_analyzer",
                "Code analyzer",
                "classic-six",
                "Typed-code and patch review scenarios.",
                "classic-six.code_analyzer",
                true,
                "full-parity",
            ),
            FamilyDef(
                "data_processing",
                "Data processing",
                "classic-six",
                "Deterministic data processing and chunking scenarios.",
                "classic-six.data_processing",
                true,
                "full-parity",
            ),
            FamilyDef(
                "jargon",
                "Jargon",
                "classic-six",
                "Terminology and language-policy scenarios.",
                "classic-six.jargon",
                true,
                "full-parity",
            ),
            FamilyDef(
                "metrics",
                "Metrics",
                "classic-six",
                "Operational metrics and signal blend scenarios.",
                "classic-six.metrics",
                true,
                "full-parity",
            ),
            FamilyDef(
                "network_activity",
                "Network activity",
                "classic-six",
                "Transport, adapter, and retry scenarios.",
                "classic-six.network_activity",
                true,
                "full-parity",
            ),
            FamilyDef(
                "system_monitoring",
                "System monitoring",
                "classic-six",
                "Telemetry, collector, and runner health scenarios.",
                "classic-six.system_monitoring",
                true,
                "full-parity",
            ),
            FamilyDef(
                "agent_workflows",
                "Agent workflows",
                "modern-core",
                "Delegation, approval, and handoff scenarios.",
                "modern-core.agent_workflows",
                true,
                "full-parity",
            ),
            FamilyDef(
                "platform_engineering",
                "Platform engineering",
                "modern-core",
                "Golden-path and shared platform scenarios.",
                "modern-core.platform_engineering",
                true,
                "full-parity",
            ),
            FamilyDef(
                "observability_ai_runtime",
                "Observability AI runtime",
                "modern-core",
                "Trace, token, and runtime pressure scenarios.",
                "modern-core.observability_ai_runtime",
                true,
                "full-parity",
            ),
            FamilyDef(
                "delivery_preview_ops",
                "Delivery preview ops",
                "modern-core",
                "Preview deploy, canary, and rollback scenarios.",
                "modern-core.delivery_preview_ops",
                true,
                "full-parity",
            ),
            FamilyDef(
                "supply_chain_security",
                "Supply chain security",
                "modern-core",
                "Provenance and dependency posture scenarios.",
                "modern-core.supply_chain_security",
                true,
                "full-parity",
            ),
            FamilyDef(
                "ai_governance",
                "AI governance",
                "ai-governance",
                "Grouped fallback for AI-governance lanes.",
                "grouped-fallback.ai_governance",
                false,
                "grouped-fallback",
            ),
            FamilyDef(
                "policy_automation",
                "Policy automation",
                "ai-governance",
                "Grouped fallback for policy automation lanes.",
                "grouped-fallback.ai_governance",
                false,
                "grouped-fallback",
            ),
            FamilyDef(
                "model_risk",
                "Model risk",
                "ai-governance",
                "Grouped fallback for model-risk lanes.",
                "grouped-fallback.ai_governance",
                false,
                "grouped-fallback",
            ),
            FamilyDef(
                "evaluation_ops",
                "Evaluation ops",
                "ai-governance",
                "Grouped fallback for evaluation lanes.",
                "grouped-fallback.ai_governance",
                false,
                "grouped-fallback",
            ),
            FamilyDef(
                "prompt_safety",
                "Prompt safety",
                "ai-governance",
                "Grouped fallback for prompt safety lanes.",
                "grouped-fallback.ai_governance",
                false,
                "grouped-fallback",
            ),
            FamilyDef(
                "security_blockchain",
                "Security blockchain",
                "security-blockchain",
                "Grouped fallback for security and chain lanes.",
                "grouped-fallback.security_blockchain",
                false,
                "grouped-fallback",
            ),
            FamilyDef(
                "smart_contract_risk",
                "Smart contract risk",
                "security-blockchain",
                "Grouped fallback for smart-contract risk lanes.",
                "grouped-fallback.security_blockchain",
                false,
                "grouped-fallback",
            ),
            FamilyDef(
                "wallet_observability",
                "Wallet observability",
                "security-blockchain",
                "Grouped fallback for wallet telemetry lanes.",
                "grouped-fallback.security_blockchain",
                false,
                "grouped-fallback",
            ),
            FamilyDef(
                "chain_forensics",
                "Chain forensics",
                "security-blockchain",
                "Grouped fallback for chain forensics lanes.",
                "grouped-fallback.security_blockchain",
                false,
                "grouped-fallback",
            ),
            FamilyDef(
                "identity_assurance",
                "Identity assurance",
                "security-blockchain",
                "Grouped fallback for identity assurance lanes.",
                "grouped-fallback.security_blockchain",
                false,
                "grouped-fallback",
            ),
            FamilyDef(
                "health_protocol",
                "Health protocol",
                "health-protocol",
                "Grouped fallback for health and protocol lanes.",
                "grouped-fallback.health_protocol",
                false,
                "grouped-fallback",
            ),
            FamilyDef(
                "clinical_workflows",
                "Clinical workflows",
                "health-protocol",
                "Grouped fallback for clinical workflow lanes.",
                "grouped-fallback.health_protocol",
                false,
                "grouped-fallback",
            ),
            FamilyDef(
                "medical_device_telemetry",
                "Medical device telemetry",
                "health-protocol",
                "Grouped fallback for device telemetry lanes.",
                "grouped-fallback.health_protocol",
                false,
                "grouped-fallback",
            ),
            FamilyDef(
                "protocol_translation",
                "Protocol translation",
                "health-protocol",
                "Grouped fallback for protocol translation lanes.",
                "grouped-fallback.health_protocol",
                false,
                "grouped-fallback",
            ),
            FamilyDef(
                "care_pathways",
                "Care pathways",
                "health-protocol",
                "Grouped fallback for care pathway lanes.",
                "grouped-fallback.health_protocol",
                false,
                "grouped-fallback",
            ),
            FamilyDef(
                "overlay_quantum",
                "Overlay quantum",
                "overlay-quantum",
                "Grouped fallback for overlay and quantum lanes.",
                "grouped-fallback.overlay_quantum",
                false,
                "grouped-fallback",
            ),
            FamilyDef(
                "quantum_networking",
                "Quantum networking",
                "overlay-quantum",
                "Grouped fallback for quantum networking lanes.",
                "grouped-fallback.overlay_quantum",
                false,
                "grouped-fallback",
            ),
            FamilyDef(
                "privacy_ops",
                "Privacy ops",
                "overlay-quantum",
                "Grouped fallback for privacy operations lanes.",
                "grouped-fallback.overlay_quantum",
                false,
                "grouped-fallback",
            ),
            FamilyDef(
                "workload_identity",
                "Workload identity",
                "overlay-quantum",
                "Grouped fallback for workload identity lanes.",
                "grouped-fallback.overlay_quantum",
                false,
                "grouped-fallback",
            ),
            FamilyDef(
                "edge_inference",
                "Edge inference",
                "overlay-quantum",
                "Grouped fallback for edge inference lanes.",
                "grouped-fallback.overlay_quantum",
                false,
                "grouped-fallback",
            ),
        )

    fun runCli(args: Array<String>) {
        val result = runArgs(args.toList())
        if (result.stdout.isNotEmpty()) {
            print(result.stdout)
        }
        if (result.stderr.isNotEmpty()) {
            System.err.print(result.stderr)
        }
        if (result.exitCode != 0) {
            exitProcess(result.exitCode)
        }
    }

    fun runArgs(args: List<String>): Result =
        when (val parsed = parseArgs(args)) {
            is ParseResult.Failure -> failure(parsed.message)
            is ParseResult.Success -> {
                val state = parsed.state
                when {
                    state.showHelp -> success(helpText)
                    state.experimentalProvider != null ->
                        failure("experimental-provider is not implemented yet in kotlin-stakeholder (${state.experimentalProvider})")
                    state.listValues -> success(renderJson(listValuesPayload()) + "\n")
                    state.config.focusFamily.isBlank() -> failure("Missing required --focus-family.")
                    else -> {
                        val family =
                            familyRegistry.firstOrNull { it.id == state.config.focusFamily }
                                ?: return failure("Unknown focus family '${state.config.focusFamily}'.")
                        val payload = sessionPayload(family, state.config)
                        if (state.config.outputFormat == "json") {
                            success(renderJson(payload) + "\n")
                        } else {
                            success(renderText(payload))
                        }
                    }
                }
            }
        }

    private fun parseArgs(args: List<String>): ParseResult {
        var state = ParseState()
        var index = 0
        while (index < args.size) {
            when (val arg = args[index]) {
                "--help", "-h" -> state = state.copy(showHelp = true)
                "--list-values" -> state = state.copy(listValues = true)
                "--alerts" -> state = state.copy(config = state.config.copy(alerts = true))
                "--minimal" -> state = state.copy(config = state.config.copy(minimal = true))
                "--trace" -> state = state.copy(config = state.config.copy(trace = true))
                "--no-color" -> state = state.copy(config = state.config.copy(noColor = true))
                "--dev-type" -> {
                    val value = requireValue(arg, args, index) ?: return ParseResult.Failure("Missing value for $arg.")
                    if (value !in devTypes) return ParseResult.Failure(invalidValue(arg, value))
                    state = state.copy(config = state.config.copy(devType = value))
                    index += 1
                }
                "--complexity" -> {
                    val value = requireValue(arg, args, index) ?: return ParseResult.Failure("Missing value for $arg.")
                    if (value !in complexities) return ParseResult.Failure(invalidValue(arg, value))
                    state = state.copy(config = state.config.copy(complexity = value))
                    index += 1
                }
                "--jargon" -> {
                    val value = requireValue(arg, args, index) ?: return ParseResult.Failure("Missing value for $arg.")
                    if (value !in jargonLevels) return ParseResult.Failure(invalidValue(arg, value))
                    state = state.copy(config = state.config.copy(jargon = value))
                    index += 1
                }
                "--output-format" -> {
                    val value = requireValue(arg, args, index) ?: return ParseResult.Failure("Missing value for $arg.")
                    if (value !in outputFormats) return ParseResult.Failure(invalidValue(arg, value))
                    state = state.copy(config = state.config.copy(outputFormat = value))
                    index += 1
                }
                "--seed" -> {
                    val value = requireValue(arg, args, index) ?: return ParseResult.Failure("Missing value for $arg.")
                    state = state.copy(config = state.config.copy(seed = value))
                    index += 1
                }
                "--focus-family" -> {
                    val value = requireValue(arg, args, index) ?: return ParseResult.Failure("Missing value for $arg.")
                    state = state.copy(config = state.config.copy(focusFamily = value))
                    index += 1
                }
                "--team" -> {
                    val value = requireValue(arg, args, index) ?: return ParseResult.Failure("Missing value for $arg.")
                    state = state.copy(config = state.config.copy(team = value))
                    index += 1
                }
                "--framework" -> {
                    val value = requireValue(arg, args, index) ?: return ParseResult.Failure("Missing value for $arg.")
                    state = state.copy(config = state.config.copy(framework = value))
                    index += 1
                }
                "--project" -> {
                    val value = requireValue(arg, args, index) ?: return ParseResult.Failure("Missing value for $arg.")
                    state = state.copy(config = state.config.copy(project = value))
                    index += 1
                }
                "--duration" -> {
                    val value = requireValue(arg, args, index) ?: return ParseResult.Failure("Missing value for $arg.")
                    val duration = value.toIntOrNull() ?: return ParseResult.Failure(invalidValue(arg, value))
                    state = state.copy(config = state.config.copy(duration = duration))
                    index += 1
                }
                "--experimental-provider" -> {
                    val value = requireValue(arg, args, index) ?: return ParseResult.Failure("Missing value for $arg.")
                    state = state.copy(experimentalProvider = value)
                    index += 1
                }
                else -> {
                    if (arg.startsWith(experimentalProviderPrefix)) {
                        state = state.copy(experimentalProvider = arg.removePrefix(experimentalProviderPrefix))
                    } else {
                        return ParseResult.Failure("Unknown argument '$arg'.")
                    }
                }
            }
            index += 1
        }
        return ParseResult.Success(state)
    }

    private fun requireValue(
        flag: String,
        args: List<String>,
        index: Int,
    ): String? = args.getOrNull(index + 1)

    private fun invalidValue(
        flag: String,
        value: String,
    ): String = "Invalid value '$value' for $flag."

    private fun sessionPayload(
        family: FamilyDef,
        config: SessionConfig,
    ): Map<String, Any?> {
        val hash = deterministicHash(config.seed, family.id)
        val meta = dedicatedMeta[family.id] ?: groupedFallbackMeta(family)
        val timestamp =
            formatter.format(
                Instant.parse("2026-01-01T00:00:00Z")
                    .plusSeconds(hash % (3600L * 24L * 180L)),
            )
        val sequence = ((hash % 9000L) + 1000L).toInt()
        val context =
            linkedMapOf<String, Any?>(
                "family" to family.id,
                "rendererKey" to family.rendererKey,
                "parityClass" to family.parityClass,
                "devType" to config.devType,
                "complexity" to config.complexity,
                "jargon" to config.jargon,
                "team" to config.team,
                "framework" to config.framework,
                "project" to config.project,
                "duration" to config.duration,
                "alerts" to config.alerts,
                "minimal" to config.minimal,
                "trace" to config.trace,
                meta.contextKey to meta.contextDetail,
                "rustPath" to meta.rustPath,
                "javaPath" to meta.javaPath,
                "corePath" to meta.corePath,
            )
        return linkedMapOf(
            "eventType" to "session.generated",
            "sequence" to sequence,
            "family" to family.id,
            "message" to buildMessage(family, config, meta.contextDetail),
            "timestamp" to timestamp,
            "context" to context,
        )
    }

    private fun buildMessage(
        family: FamilyDef,
        config: SessionConfig,
        detail: String,
    ): String =
        if (family.parityClass == "full-parity") {
            "${family.label} for ${config.devType} teams emphasizes $detail."
        } else {
            "${family.label} currently runs through grouped fallback while the dedicated tranche is deferred; keep attention on $detail."
        }

    private fun listValuesPayload(): Map<String, Any?> =
        linkedMapOf(
            "generatorFamilies" to
                familyRegistry.map { family ->
                    linkedMapOf(
                        "id" to family.id,
                        "label" to family.label,
                        "group" to family.group,
                        "summary" to family.summary,
                        "rendererKey" to family.rendererKey,
                        "smoke" to family.smoke,
                        "parityClass" to family.parityClass,
                    )
                },
            "devTypes" to devTypes,
            "jargonLevels" to jargonLevels,
            "complexities" to complexities,
            "outputFormats" to outputFormats,
        )

    private fun groupedFallbackMeta(family: FamilyDef): DedicatedMeta =
        DedicatedMeta(
            contextKey = "groupFallback",
            contextDetail = "later packet families remain grouped until their dedicated tranche lands",
            rustPath = "src/activities.rs",
            javaPath = "java-stakeholder/src/main/java/stakeholder/generators/GroupedFallbackRenderer.java",
            corePath = "stakeholder-core/docs/traceability-matrix.md#${family.group}",
        )

    private fun deterministicHash(
        seed: String,
        familyId: String,
    ): Long {
        val crc32 = CRC32()
        crc32.update("$seed::$familyId".toByteArray())
        return crc32.value
    }

    private fun renderText(payload: Map<String, Any?>): String {
        val context = payload["context"] as Map<*, *>
        return buildString {
            appendLine("eventType: ${payload["eventType"]}")
            appendLine("sequence: ${payload["sequence"]}")
            appendLine("family: ${payload["family"]}")
            appendLine("timestamp: ${payload["timestamp"]}")
            appendLine("message: ${payload["message"]}")
            appendLine("rendererKey: ${context["rendererKey"]}")
        }
    }

    private fun renderJson(value: Any?): String =
        when (value) {
            null -> "null"
            is String -> "\"${escapeJson(value)}\""
            is Boolean, is Int, is Long -> value.toString()
            is Map<*, *> ->
                value.entries.joinToString(prefix = "{", postfix = "}") { entry ->
                    "\"${escapeJson(entry.key.toString())}\":${renderJson(entry.value)}"
                }
            is Iterable<*> -> value.joinToString(prefix = "[", postfix = "]") { renderJson(it) }
            else -> "\"${escapeJson(value.toString())}\""
        }

    private fun escapeJson(input: String): String =
        buildString(input.length + 8) {
            input.forEach { ch ->
                when (ch) {
                    '\\' -> append("\\\\")
                    '"' -> append("\\\"")
                    '\n' -> append("\\n")
                    '\r' -> append("\\r")
                    '\t' -> append("\\t")
                    else -> append(ch)
                }
            }
        }

    private val helpText =
        """
        kotlin-stakeholder
        Required:
          --focus-family <family-id>

        Optional:
          --dev-type <${devTypes.joinToString("|")}>
          --complexity <${complexities.joinToString("|")}>
          --jargon <${jargonLevels.joinToString("|")}>
          --output-format <${outputFormats.joinToString("|")}>
          --seed <value>
          --team <value>
          --framework <value>
          --project <value>
          --duration <minutes>
          --alerts --minimal --trace --no-color
          --list-values
          --experimental-provider <value>
        """.trimIndent() + "\n"

    private fun success(stdout: String): Result = Result(exitCode = 0, stdout = stdout, stderr = "")

    private fun failure(message: String): Result = Result(exitCode = 1, stdout = "", stderr = "$message\n")
}
