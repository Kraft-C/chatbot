package org.ldv.chatbot.config


import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient
import java.net.InetSocketAddress
import java.net.Proxy
import java.net.URI
import kotlin.text.isNotBlank

import org.springframework.http.client.SimpleClientHttpRequestFactory


@Configuration
class RestClientConfig(
    @Value("\${ia.proxy:}") private val proxyConfig: String,
) {

    @Bean
    fun restClientBuilder(): RestClient.Builder {
        val factory = SimpleClientHttpRequestFactory().apply {
            proxySysteme()?.let { proxy ->
                setProxy(proxy)
            }
        }

        return RestClient.builder()
            .requestFactory(factory)
    }

    private fun proxySysteme(): Proxy? {
        val valeur = proxyConfig.takeIf(String::isNotBlank) ?: listOf("HTTPS_PROXY", "https_proxy", "HTTP_PROXY", "http_proxy")
            .firstNotNullOfOrNull { System.getenv(it)?.takeIf(String::isNotBlank) } ?: return null
        return try {
            val uri = URI(if ("://" in valeur) valeur else "http://$valeur")
            val port = if (uri.port > 0) uri.port else 8080

            Proxy(Proxy.Type.HTTP, InetSocketAddress(uri.host, port))
        } catch (e: Exception) {
            println("Variable de proxy illisible, connexion directe : " + e.message)
            null
        }
    }
}