package ch.bfh.mobilecomputing.fs2014.ratingapp.entities;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import android.content.Context;
import android.util.Log;

/**
 * As dissem.ch uses a CACert certificate which isn't trusted by default on
 * Android devices, we must create our own key store.
 * 
 * @author chris
 */
public class CACertHttpsHelper {
	private KeyStore keyStore;
	private SSLContext sslCtx;

	public CACertHttpsHelper(Context ctx) {
		try {

			// Load CAs from an InputStream
			// (could be from a resource or ByteArrayInputStream or ...)
			CertificateFactory cf = CertificateFactory.getInstance("X.509");

			// From https://www.cacert.org
			InputStream caInput = ctx.getAssets().open(
					"CA Cert Signing Authority.cer");

			Certificate ca;
			try {
				ca = cf.generateCertificate(caInput);
				Log.d(CACertHttpsHelper.class.getSimpleName(), "ca="
						+ ((X509Certificate) ca).getSubjectDN());
			} finally {
				caInput.close();
			}

			// Create a KeyStore containing our trusted CAs
			String keyStoreType = KeyStore.getDefaultType();
			keyStore = KeyStore.getInstance(keyStoreType);
			keyStore.load(null, null);
			keyStore.setCertificateEntry("ca", ca);

			// Create a TrustManager that trusts the CAs in our KeyStore
			String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
			TrustManagerFactory tmf = TrustManagerFactory
					.getInstance(tmfAlgorithm);
			tmf.init(keyStore);

			// Create an SSLContext that uses our TrustManager
			sslCtx = SSLContext.getInstance("TLS");
			sslCtx.init(null, tmf.getTrustManagers(), null);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public InputStream inputStream(URL url) {
		try {
			URLConnection c = url.openConnection();
			if (c instanceof HttpsURLConnection
					&& url.getHost().contains("dissem.ch")) {
				// Tell the URLConnection to use a SocketFactory from our
				// SSLContext
				HttpsURLConnection urlConnection = (HttpsURLConnection) c;
				urlConnection.setSSLSocketFactory(sslCtx.getSocketFactory());
			}
			return c.getInputStream();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public String post(URL url, String data) throws IOException {
		HttpURLConnection c = (HttpURLConnection) url.openConnection();
		if (c instanceof HttpsURLConnection
				&& url.getHost().contains("dissem.ch")) {
			((HttpsURLConnection) c).setSSLSocketFactory(sslCtx
					.getSocketFactory());
		}
		c.setRequestMethod("POST");
		c.setRequestProperty("Accept", "application/json");
		c.setRequestProperty("Content-Type", "application/json");
		OutputStreamWriter writer = new OutputStreamWriter(c.getOutputStream());
		writer.write(data.toString());
		writer.close();

		if (c.getResponseCode() != HttpURLConnection.HTTP_OK) {
			throw new IOException(c.getResponseMessage());
		}

		return c.getResponseMessage();
	}
}
