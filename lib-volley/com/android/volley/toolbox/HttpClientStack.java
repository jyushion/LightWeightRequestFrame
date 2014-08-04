/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.volley.toolbox;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Request.FormFile;
import com.android.volley.Request.FormHeader;
import com.android.volley.Request.Method;

/**
 * An HttpStack that performs request over an {@link HttpClient}.
 */
public class HttpClientStack implements HttpStack {
	protected final HttpClient mClient;

	private final static String HEADER_CONTENT_TYPE = "Content-Type";

	public HttpClientStack(HttpClient client) {
		mClient = client;
	}

	private static void addHeaders(HttpUriRequest httpRequest, Map<String, String> headers) {
		for (String key : headers.keySet()) {
			httpRequest.setHeader(key, headers.get(key));
		}
	}

	@SuppressWarnings("unused")
	private static List<NameValuePair> getPostParameterPairs(Map<String, String> postParams) {
		List<NameValuePair> result = new ArrayList<NameValuePair>(postParams.size());
		for (String key : postParams.keySet()) {
			result.add(new BasicNameValuePair(key, postParams.get(key)));
		}
		return result;
	}

	@Override
	public HttpResponse performRequest(Request<?> request, Map<String, String> additionalHeaders) throws IOException,
			AuthFailureError {
		HttpUriRequest httpRequest = createHttpRequest(request, additionalHeaders);
		addHeaders(httpRequest, additionalHeaders);
		addHeaders(httpRequest, request.getHeaders());
		onPrepareRequest(httpRequest);
		HttpParams httpParams = httpRequest.getParams();
		int timeoutMs = request.getTimeoutMs();
		// TODO: Reevaluate this connection timeout based on more wide-scale
		// data collection and possibly different for wifi vs. 3G.
		HttpConnectionParams.setConnectionTimeout(httpParams, 5000);
		HttpConnectionParams.setSoTimeout(httpParams, timeoutMs);
		return mClient.execute(httpRequest);
	}

	/**
	 * Creates the appropriate subclass of HttpUriRequest for passed in request.
	 */
	@SuppressWarnings("deprecation")
	/* protected */static HttpUriRequest createHttpRequest(Request<?> request, Map<String, String> additionalHeaders)
			throws AuthFailureError {
		switch (request.getMethod()) {
		case Method.DEPRECATED_GET_OR_POST: {
			// This is the deprecated way that needs to be handled for backwards
			// compatibility.
			// If the request's post body is null, then the assumption is that
			// the request is
			// GET. Otherwise, it is assumed that the request is a POST.
			byte[] postBody = request.getPostBody();
			if (postBody != null) {
				HttpPost postRequest = new HttpPost(request.getUrl());
				postRequest.addHeader(HEADER_CONTENT_TYPE, request.getPostBodyContentType());
				HttpEntity entity;
				entity = new ByteArrayEntity(postBody);
				postRequest.setEntity(entity);
				return postRequest;
			} else if (request.getPostFormFiles() != null&&request.getPostFormFiles().length>0) {
				HttpPost postRequest = new HttpPost(request.getUrl());
				setFormFilesIfExist(postRequest, request);
			} else {
				return new HttpGet(request.getUrl());
			}
		}
		case Method.GET:
			return new HttpGet(request.getUrl());
		case Method.DELETE:
			return new HttpDelete(request.getUrl());
		case Method.POST: {
			byte[] postBody = request.getPostBody();
			HttpPost postRequest = new HttpPost(request.getUrl());
			if (postBody != null) {
				postRequest.addHeader(HEADER_CONTENT_TYPE, request.getBodyContentType());
				HttpEntity entity = new ByteArrayEntity(postBody);
				postRequest.setEntity(entity);
			} else {
				setFormFilesIfExist(postRequest, request);
			}
			return postRequest;
		}
		case Method.PUT: {
			HttpPut putRequest = new HttpPut(request.getUrl());
			putRequest.addHeader(HEADER_CONTENT_TYPE, request.getBodyContentType());
			setEntityIfNonEmptyBody(putRequest, request);
			return putRequest;
		}
		default:
			throw new IllegalStateException("Unknown request method.");
		}
	}

	private static void setFormFilesIfExist(HttpPost postRequest, Request<?> request) {
		FormFile[] formFiles = request.getPostFormFiles();
		if (formFiles != null && formFiles.length > 0) {
			MultipartEntity entity = new MultipartEntity();
			postRequest.addHeader(HEADER_CONTENT_TYPE, request.getPostFormFileContentType());
			FormHeader[] formHeaders = request.getPostFormFileHeaders();
			if (formHeaders != null && formHeaders.length > 0) {
				for (FormHeader header : formHeaders) {
					try {
						entity.addPart(header.getKey(), new StringBody(header.getValue()));
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}

			}
			for (FormFile file : formFiles) {
				if (file.getData() != null) {
					entity.addPart(file.getFileName(), new ByteArrayBody(file.getData(), file.getMimeType(), file.getFileName()));
				} else {
					entity.addPart(file.getFileName(), new FileBody(new File(file.getFilePath(), file.getMimeType())));
				}
			}
			postRequest.setEntity(entity);
		}
	}

	private static void setEntityIfNonEmptyBody(HttpEntityEnclosingRequestBase httpRequest, Request<?> request)
			throws AuthFailureError {
		byte[] body = request.getBody();
		if (body != null) {
			HttpEntity entity = new ByteArrayEntity(body);
			httpRequest.setEntity(entity);
		}
	}

	/**
	 * Called before the request is executed using the underlying HttpClient.
	 * 
	 * <p>
	 * Overwrite in subclasses to augment the request.
	 * </p>
	 */
	protected void onPrepareRequest(HttpUriRequest request) throws IOException {
		// Nothing.
	}
}
