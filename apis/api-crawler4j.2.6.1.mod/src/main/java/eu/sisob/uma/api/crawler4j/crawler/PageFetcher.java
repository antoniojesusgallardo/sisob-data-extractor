/*
    Copyright (c) 2014 "(IA)2 Research Group. Universidad de Málaga"
                        http://iaia.lcc.uma.es | http://www.uma.es

    This file is part of SISOB Data Extractor.

    SISOB Data Extractor is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    SISOB Data Extractor is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with SISOB Data Extractor. If not, see <http://www.gnu.org/licenses/>.
*/
/*
    This code was initially developed by Yasser Ganjisaffar (yganjisa@uci.dot.edu)
    under an Apache Software Foundation License. 
*/

package eu.sisob.uma.api.crawler4j.crawler;

import java.io.IOException;
import java.util.Date;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParamBean;

import eu.sisob.uma.api.crawler4j.frontier.DocIDServer;
import eu.sisob.uma.api.crawler4j.url.URLCanonicalizer;
import eu.sisob.uma.api.crawler4j.url.WebURL;
import java.util.logging.Level;
import java.util.logging.Logger;


public final class PageFetcher {
    

	private static ThreadSafeClientConnManager connectionManager;
        
    private static final Logger LOG = Logger.getLogger(PageFetcher.class.getName());

	private static DefaultHttpClient httpclient;

	private static Object mutex = PageFetcher.class.toString() + "_MUTEX";

	private static int processedCount = 0;
	private static long startOfPeriod = 0;
	private static long lastFetchTime = 0;

	private static long politenessDelay = Configurations.getIntProperty("fetcher.default_politeness_delay", 200);

	public static final int MAX_DOWNLOAD_SIZE = Configurations.getIntProperty("fetcher.max_download_size", 1048576);

	private static final boolean show404Pages = Configurations.getBooleanProperty("logging.show_404_pages", true);

	private static IdleConnectionMonitorThread connectionMonitorThread = null;

	public static long getPolitenessDelay() {
		return politenessDelay;
	}

	public static void setPolitenessDelay(long politenessDelay) {
		PageFetcher.politenessDelay = politenessDelay;
	}
	public synchronized static void startConnectionMonitorThread()
        {
		if (connectionMonitorThread == null) 
                {
                        HttpParams params = new BasicHttpParams();
                        HttpProtocolParamBean paramsBean = new HttpProtocolParamBean(params);
                        paramsBean.setVersion(HttpVersion.HTTP_1_1);
                        paramsBean.setContentCharset("UTF-8");
                        paramsBean.setUseExpectContinue(false);

                        params.setParameter("http.useragent", Configurations.getStringProperty("fetcher.user_agent",
                                        "crawler4j (http://code.google.com/p/crawler4j/)"));

                        params.setIntParameter("http.socket.timeout", Configurations.getIntProperty("fetcher.socket_timeout", 20000));

                        params.setIntParameter("http.connection.timeout",
                                        Configurations.getIntProperty("fetcher.connection_timeout", 30000));

                        params.setBooleanParameter("http.protocol.handle-redirects", false);

                        ConnPerRouteBean connPerRouteBean = new ConnPerRouteBean();
                        connPerRouteBean.setDefaultMaxPerRoute(Configurations.getIntProperty("fetcher.max_connections_per_host", 100));
                        ConnManagerParams.setMaxConnectionsPerRoute(params, connPerRouteBean);
                        ConnManagerParams.setMaxTotalConnections(params,
                                        Configurations.getIntProperty("fetcher.max_total_connections", 100));

                        SchemeRegistry schemeRegistry = new SchemeRegistry();
                        schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));

                        if (Configurations.getBooleanProperty("fetcher.crawl_https", false)) {
                                schemeRegistry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
                        }

                        connectionManager = new ThreadSafeClientConnManager(params, schemeRegistry);                

                        //ProjectLogger.LOGGER.setLevel(Level.INFO);
                        httpclient = new DefaultHttpClient(connectionManager, params);
			connectionMonitorThread = new IdleConnectionMonitorThread(connectionManager);
		}
		connectionMonitorThread.start();
	}

	public synchronized static void stopConnectionMonitorThread() {
		if (connectionMonitorThread != null)
                {
                    connectionManager.shutdown();
                    connectionMonitorThread.shutdown();
                    connectionMonitorThread = null;
                    connectionManager = null;
		}
	}

	public static int fetch(Page page, boolean ignoreIfBinary, DocIDServer refdocIDServer) {
		String toFetchURL = page.getWebURL().getURL();
		HttpGet get = null;
		HttpEntity entity = null;
		try {
			get = new HttpGet(toFetchURL);
			synchronized (mutex) {
				long now = (new Date()).getTime();
				if (now - startOfPeriod > 10000) {
					//ProjectLogger.LOGGER.info("Number of pages fetched per second: " + processedCount / ((now - startOfPeriod) / 1000));
					processedCount = 0;
					startOfPeriod = now;
				}
				processedCount++;

				if (now - lastFetchTime < politenessDelay) {
					Thread.sleep(politenessDelay - (now - lastFetchTime));
				}
				lastFetchTime = (new Date()).getTime();
                        }                        
                        get.addHeader("Accept","text/xml,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5");                        
			HttpResponse response = httpclient.execute(get);
			entity = response.getEntity();

			int statusCode = response.getStatusLine().getStatusCode();
			if ((statusCode != HttpStatus.SC_OK)) {
				if (statusCode != HttpStatus.SC_NOT_FOUND) {
					if (statusCode == HttpStatus.SC_MOVED_PERMANENTLY || statusCode == HttpStatus.SC_MOVED_TEMPORARILY) {
						Header header = response.getFirstHeader("Location");
						if (header != null) {
							String movedToUrl = header.getValue();
                                                        if(!movedToUrl.contains("http://")) {
                                                            movedToUrl = get.getURI().getScheme() + "://" + get.getURI().getHost() + movedToUrl;
                                                        }
							page.getWebURL().setURL(movedToUrl);
						} else {
							page.getWebURL().setURL(null);
						}
						return PageFetchStatus.Moved;
					}
					LOG.info("Failed: " + response.getStatusLine().toString() + ", while fetching " + toFetchURL);
				} else if (show404Pages) {
					LOG.info("Not Found: " + toFetchURL + " (Link found in doc#: "
							+ page.getWebURL().getParentDocid() + ")");
				}
				return response.getStatusLine().getStatusCode();
			}

			String uri = get.getURI().toString();
			if (!uri.equals(toFetchURL)) {
				if (!URLCanonicalizer.getCanonicalURL(uri).equals(toFetchURL)) {
					int newdocid = refdocIDServer.getDocID(uri);
					if (newdocid != -1) {
						if (newdocid > 0) {
							return PageFetchStatus.RedirectedPageIsSeen;
						}
						WebURL webURL = new WebURL();
						webURL.setURL(uri);
						webURL.setDocid(refdocIDServer.getNewDocID(uri));
						page.setWebURL(webURL);
					}
				}
			}

			if (entity != null) {
				long size = entity.getContentLength();
				if (size == -1) {
					Header length = response.getLastHeader("Content-Length");
					if (length == null) {
						length = response.getLastHeader("Content-length");
					}
					if (length != null) {
						size = Integer.parseInt(length.getValue());
					} else {
						size = -1;
					}
				}
				if (size > MAX_DOWNLOAD_SIZE) {
					entity.consumeContent();
					return PageFetchStatus.PageTooBig;
				}

				boolean isBinary = false;

				Header type = entity.getContentType();
				if (type != null) {
					String typeStr = type.getValue().toLowerCase();
					if (typeStr.contains("image") || typeStr.contains("audio") || typeStr.contains("video")) {
						isBinary = true;
						if (ignoreIfBinary) {
							return PageFetchStatus.PageIsBinary;
						}
					}
				}

				if (page.load(entity.getContent(), (int) size, isBinary)) {
					return PageFetchStatus.OK;
				} else {
					return PageFetchStatus.PageLoadError;
				}
			} else {
				get.abort();
			}
		} catch (IOException e) {
			LOG.log(Level.SEVERE, "Fatal transport error: " + e.getMessage() + " while fetching " + toFetchURL
					+ " (link found in doc #" + page.getWebURL().getParentDocid() + ")");
			return PageFetchStatus.FatalTransportError;
		} catch (IllegalStateException e) {
			// ignoring exceptions that occur because of not registering https
			// and other schemes
		} catch (Exception e) {
			if (e.getMessage() == null) {
				LOG.log(Level.SEVERE, "Error while fetching " + page.getWebURL().getURL());
			} else {
				LOG.log(Level.SEVERE, e.getMessage() + " while fetching " + page.getWebURL().getURL());
			}
		} finally {
			try {
				if (entity != null) {
					entity.consumeContent();
				} else if (get != null) {
					get.abort();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return PageFetchStatus.UnknownError;
	}

	public static void setProxy(String proxyHost, int proxyPort) {
		HttpHost proxy = new HttpHost(proxyHost, proxyPort);
		httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
	}

	public static void setProxy(String proxyHost, int proxyPort, String username, String password) {
		httpclient.getCredentialsProvider().setCredentials(new AuthScope(proxyHost, proxyPort),
				new UsernamePasswordCredentials(username, password));
		setProxy(proxyHost, proxyPort);
	}

}
