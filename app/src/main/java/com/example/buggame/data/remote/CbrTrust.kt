package com.example.buggame.data.remote

import okhttp3.OkHttpClient
import java.security.KeyStore
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

fun OkHttpClient.Builder.trustCbrCertificate(): OkHttpClient.Builder {
    val default = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
        .apply { init(null as KeyStore?) }
        .trustManagers
        .filterIsInstance<X509TrustManager>()
        .first()

    val manager = object : X509TrustManager {
        override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) =
            default.checkClientTrusted(chain, authType)

        override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
            try {
                default.checkServerTrusted(chain, authType)
            } catch (e: CertificateException) {
                val subject = chain.firstOrNull()?.subjectX500Principal?.name.orEmpty()
                if (!subject.contains("cbr.ru", ignoreCase = true)) throw e
            }
        }

        override fun getAcceptedIssuers(): Array<X509Certificate> = default.acceptedIssuers
    }

    val context = SSLContext.getInstance("TLS").apply { init(null, arrayOf(manager), null) }
    return sslSocketFactory(context.socketFactory, manager)
}